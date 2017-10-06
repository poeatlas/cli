package com.github.poeatlas.cli.dat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.poeatlas.cli.dat.domain.AtlasNode;
import com.github.poeatlas.cli.dat.domain.AtlasQuestItems;
import com.github.poeatlas.cli.dat.domain.ItemVisualIdentity;
import com.github.poeatlas.cli.dat.domain.WorldAreas;
import com.github.poeatlas.cli.dat.json.MapJson;
import com.github.poeatlas.cli.dat.repository.AtlasNodeRepository;
import com.github.poeatlas.cli.dat.repository.AtlasQuestItemsRepository;
import com.github.poeatlas.cli.dat.repository.ItemVisualIdentityRepository;
import com.github.poeatlas.cli.dat.repository.WorldAreasRepository;
import com.github.poeatlas.cli.dat.util.MapUtil;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by NothingSoup on 7/9/17.
 */
@SpringBootApplication
@Slf4j
@SuppressWarnings("PMD.UseUtilityClass")
public class Main implements CommandLineRunner {
  // give it .0001 of precision
  private static final double SEXTANT_MAX_DISTANCE = 55.0001;

  @Autowired
  private AtlasNodeRepository atlasNodeRepo;

  @Autowired
  private WorldAreasRepository worldAreasRepo;

  @Autowired
  private ItemVisualIdentityRepository itemVisualIdentityRepo;

  @Autowired
  private AtlasQuestItemsRepository atlasQuestItemsRepo;

  /**
   * reads dat files.
   *
   * @param args file
   * @throws IOException if file is invalid
   */
  public static void main(final String[] args) throws IOException {

    final StopWatch stopWatch = StopWatch.createStarted();

    if (log.isDebugEnabled()) {
      log.debug("args: {}", Arrays.asList(args));
    }

    try {
      final SpringApplication app = new SpringApplication(Main.class);
      app.run(args);
      log.info("Elapsed Time: {}", stopWatch.toString());
    } catch (Exception ex) {
      log.error(ex.getMessage(), ex);
      log.info("Elapsed Time: {}", stopWatch.toString());
      System.exit(1);
    }
  }

  @Override
  public void run(final String... args)
      throws IOException,
      InstantiationException,
      IllegalAccessException,
      InvocationTargetException {

    final OptionParser parser = new OptionParser();

    // spec for json input filename
    final OptionSpec<File> inputSpec = parser.accepts("input", "dat input directory")
        .withRequiredArg()
        .ofType(File.class)
        .required();

    // spec for json output filename
    final OptionSpec<File> outputSpec = parser.accepts("output", "JSON output name")
        .withRequiredArg()
        .ofType(File.class)
        .required();

    // parse arguments
    final OptionSet opt = parser.parse(args);

    // directory of ouptut file
    // opt = parser.parse(args);
    final File inputDir = opt.valueOf(inputSpec);
    final File outputFile = opt.valueOf(outputSpec);

    // check directory for dat files is valid
    Objects.requireNonNull(inputDir, "Input directory is null");
    // check directory for dat files is valid
    Objects.requireNonNull(outputFile, "output file directory is null");

    final File parentOutputDir = outputFile.getAbsoluteFile().getParentFile();
    // check spec output file exists in existing directory
    if (!parentOutputDir.isDirectory() && !parentOutputDir.mkdirs()) {
      throw new IOException(outputFile.getParentFile()
                            + "directory for output file does not exist");
    }

    if (!inputDir.isDirectory()) {
      throw new IOException(inputDir.getPath() + "is not a directory.");
    }

    parseDats(inputDir, outputFile);
  }

  private void parseDats(final File inputDir, final File outputFile)
      throws IOException,
      InvocationTargetException,
      InstantiationException,
      IllegalAccessException {
    final DatParser<WorldAreas> worldAreasParser = new DatParser<>(inputDir, WorldAreas.class);
    final DatParser<AtlasQuestItems> atlasQuestItemsParser = new DatParser<>(inputDir,
        AtlasQuestItems.class);
    final DatParser<ItemVisualIdentity> itemVisualIdentityParser = new DatParser<>(inputDir,
        ItemVisualIdentity.class);
    final DatParser<AtlasNode> atlasNodeParser = new DatParser<>(inputDir, AtlasNode.class);

    final List<WorldAreas> worldAreasRecList = worldAreasParser.parse();
    final List<AtlasQuestItems> atlasQuestItemsList = atlasQuestItemsParser.parse();
    final List<ItemVisualIdentity> itemVisualIdentityList = itemVisualIdentityParser.parse();
    final List<AtlasNode> atlasNodeList = atlasNodeParser.parse();

    worldAreasRepo.save(worldAreasRecList);
    atlasQuestItemsRepo.save(atlasQuestItemsList);
    itemVisualIdentityRepo.save(itemVisualIdentityList);
    atlasNodeRepo.save(atlasNodeList);

    final List<AtlasQuestItems> shaperMapsList = atlasQuestItemsRepo.fetchShaperMaps();
    final List<AtlasNode> atlasNodes = atlasNodeRepo.findAll();
    // contains all relevant atlas data to be written into JSON file
    final List<MapJson> atlasJson = new ArrayList<>();

    final Map<Long, Integer> shaperOrbMap = new HashMap<>();
    for (final AtlasQuestItems item : shaperMapsList) {
      shaperOrbMap.put(item.getWorldAreas().getId(), item.getMapTier());
    }

    for (final AtlasNode node : atlasNodes) {
      final WorldAreas worldAreas = node.getWorldAreas();

      // create AtlasData object with relevant data
      final MapJson.MapJsonBuilder builder = MapJson.builder()
          .id(node.getId())
          .x(node.getX())
          .y(node.getY())
          .connected(MapUtil.getConnectedMapIds(node.getAtlasNodeKeys()))
          .name(worldAreas.getName())
          .level(worldAreas.getAreaLevel())
          .iconPath(MapUtil.getIconPath(node.getDefaultItemVisualIdentityKey()))
          .shapedIconPath(MapUtil.getIconPath(node.getDefaultShapedItemVisualIdentityKey()));

      // check if curr map contains shaper orb
      if (shaperOrbMap.containsKey(node.getWorldAreas().getId())) {
        builder.shaperOrbTier(shaperOrbMap.get(node.getWorldAreas().getId()));
      }

      // add to list to write to JSON
      atlasJson.add(builder.build());
    }

    allocateSextants(atlasJson);

    final ObjectMapper mapper = new ObjectMapper();
    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    mapper.writeValue(outputFile, atlasJson);
  }

  @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
  private void allocateSextants(final List<MapJson> atlas) {
    for (final MapJson map : atlas) {
      final List<Integer> sextants = new ArrayList<>();
      final float x = map.getX();
      final float y = map.getY();

      for (final MapJson otherMap : atlas) {
        if (otherMap.equals(map)) {
          continue;
        }

        final float otherX = otherMap.getX();
        final float otherY = otherMap.getY();
        final double distance = Math.hypot(x - otherX, y - otherY);

        if (distance < SEXTANT_MAX_DISTANCE) {
          sextants.add(otherMap.getId());
        }
      }

      if (!sextants.isEmpty()) {
        map.setSextant(sextants);
      }
    }
  }
}

package com.github.poeatlas.cli.dat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.poeatlas.cli.dat.domain.AtlasNode;
import com.github.poeatlas.cli.dat.domain.ItemVisualIdentity;
import com.github.poeatlas.cli.dat.domain.WorldAreas;
import com.github.poeatlas.cli.dat.repository.AtlasNodeRepository;
import com.github.poeatlas.cli.dat.repository.ItemVisualIdentityRepository;
import com.github.poeatlas.cli.dat.repository.WorldAreasRepository;
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
import java.util.Arrays;
import java.util.List;

/**
 * Created by NothingSoup on 7/9/17.
 */
@SpringBootApplication
@Slf4j
@SuppressWarnings("PMD.UseUtilityClass")
public class Main implements CommandLineRunner {
  @Autowired
  private AtlasNodeRepository atlasNodeRepo;

  @Autowired
  private WorldAreasRepository worldAreasRepo;

  @Autowired
  private ItemVisualIdentityRepository itemVisualIdentityRepo;

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

    // spec for json output filename
    final OptionSpec<File> outputSpec = parser.accepts("output", "JSON output name")
        .withRequiredArg()
        .ofType(File.class)
        .required();

    // directory of dat files
    final OptionSet opt = parser.parse(args);
    final File outputFile = opt.valueOf(outputSpec);

    // check spec output file exists in existing directory
    if (!outputFile.getParentFile().isDirectory()) {
      throw new IOException(outputFile.getParentFile() 
                            + "directory for output file does not exist");
    }

    final OptionSpec<File> dir = parser.nonOptions().ofType(File.class);
    final File directory = opt.valueOf(dir);
    // check directory for dat files is valid
    if (!directory.isDirectory()) {
      throw new IOException(directory.getPath() + "is not a directory.");
    }

    final DatParser<WorldAreas> worldAreasParser = new DatParser<>(directory, WorldAreas.class);
    final DatParser<ItemVisualIdentity> itemVisualIdentityParser = new DatParser<>(directory,
        ItemVisualIdentity.class);
    final DatParser<AtlasNode> atlasNodeParser = new DatParser<>(directory, AtlasNode.class);

    final List<WorldAreas> worldAreasRecList = worldAreasParser.parse();
    final List<ItemVisualIdentity> itemVisualIdentityList = itemVisualIdentityParser.parse();
    final List<AtlasNode> atlasNodeList = atlasNodeParser.parse();

    worldAreasRepo.save(worldAreasRecList);
    itemVisualIdentityRepo.save(itemVisualIdentityList);
    atlasNodeRepo.save(atlasNodeList);

    final List<AtlasNode> atlasNodes = atlasNodeRepo.findAll();

    final ObjectMapper mapper = new ObjectMapper();
    mapper.writeValue(outputFile,atlasNodes);

  }
}

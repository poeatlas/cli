package com.github.poeatlas.cli.dat;

import com.github.poeatlas.cli.dat.domain.AtlasNode;
import com.github.poeatlas.cli.dat.domain.ItemVisualIdentity;
import com.github.poeatlas.cli.dat.domain.WorldAreas;
import com.github.poeatlas.cli.dat.repository.AtlasNodeRepository;
import com.github.poeatlas.cli.dat.repository.ItemVisualIdentityRepository;
import com.github.poeatlas.cli.dat.repository.WorldAreasRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;
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
  public void run(String... args) throws Exception {
    final File directory = new File(args[0]);
    if (!directory.isDirectory()) {
      throw new IOException(directory.getPath() + "is not a directory");
    }

    // Field stringListField = Main.class.getDeclaredField("stringList");
    // Field intListField = Main.class.getDeclaredField("integerList");
    //
    // log.info("str list: {}", stringListField.getType());
    // log.info("int list: {}", intListField.getType());
    //
    // log.info(String.valueOf(stringListField.getType().equals(intListField.getType())));

    DatParser<WorldAreas> worldAreasParser = new DatParser<>(directory, WorldAreas.class);
    DatParser<ItemVisualIdentity> itemVisualIdentityParser = new DatParser<>(directory, ItemVisualIdentity.class);
    DatParser<AtlasNode> atlasNodeParser = new DatParser<>(directory, AtlasNode.class);

    List<WorldAreas> worldAreasRecList = worldAreasParser.parse();
    List<ItemVisualIdentity> itemVisualIdentityList = itemVisualIdentityParser.parse();
    List<AtlasNode> atlasNodeList = atlasNodeParser.parse();

    // DatMeta datMeta = null;
    // datMeta.builder()
    //     .magicOffset(1111)
    //     .tableLength(10)
    //     .tableRowLength(30)
    //     .tableRows(50)
    //     .build();
    //
    // // log.info(String.valueOf(AtlasNode.class.getDeclaredField("atlasNodeKeys")));
    // ListDecoder listDecoder = new ListDecoder(datMeta, AtlasNode.class.getDeclaredField("atlasNodeKeys"));

    // List<AtlasNode> atLasNodeList = atlasNodeParser.parse();
    //

    worldAreasRepo.save(worldAreasRecList);
    itemVisualIdentityRepo.save(itemVisualIdentityList);
    atlasNodeRepo.save(atlasNodeList);
  }
}

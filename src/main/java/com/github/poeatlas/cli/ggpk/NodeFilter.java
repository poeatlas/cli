package com.github.poeatlas.cli.ggpk;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by NothingSoup on 7/5/17.
 */
public class NodeFilter {
  private final Pattern ATLAS_MAP_PATTERN = Pattern.compile("^/art/2ditems/maps/atlasmaps/[^/]+\\.dds$");
  private final Matcher ATLAS_MAP_MATCHER = ATLAS_MAP_PATTERN.matcher("");

  /**
   * filters for directories relevant to atlas information
   * @param node directory node containing path information to be filtered
   * @return boolean true if directory passes filter
   */
  public boolean directoryFilter(final DirectoryNode node) {
    final String nodePath = node.getPath().toLowerCase();

    return nodePath.equals("/art/2dart/atlas") ||
        nodePath.equals("/textures/interface/2d") ||
        nodePath.equals("/art/2ditems/maps/atlasmaps") ||
        nodePath.equals("/art/2ditems/currency") ||
        nodePath.equals("/data");
  }

  /**
   * filters file names for relevant atlas information
   * @param node a file node to be passed through the filter
   * @return boolean true if file passes filter
   */
  public boolean fileFilter(final FileNode node) {
    final String filePath = node.getPath().toLowerCase();

    return filePath.equals("/art/2dart/atlas/atlas.dds") ||
           filePath.equals("/art/2dart/atlas/atlasblank.dds") ||
           filePath.equals("/textures/interface/2d/2dart_uiimages_ingame_3.dds") ||
           ATLAS_MAP_MATCHER.reset(filePath).matches() ||
           filePath.equals("/art/2ditems/currency/atlasradiuswhite.dds") ||
           filePath.equals("/art/2ditems/currency/atlasradiusyellow.dds") ||
           filePath.equals("/art/2ditems/currency/atlasradiusred.dds") ||
           filePath.equals("/art/2ditems/currency/upgrademaptoyellow.dds") ||
           filePath.equals("/art/2ditems/currency/upgrademaptored.dds") ||
           filePath.equals("/art/2ditems/currency/atlasdowngrade.dds") ||
           filePath.equals("/art/2ditems/currency/sealwhite.dds") ||
           filePath.equals("/art/2ditems/currency/sealyellow.dds") ||
           filePath.equals("/art/2ditems/currency/sealred.dds") ||
           filePath.equals("/data/atlasnode.dat") ||
           filePath.equals("/data/worldareas.dat") ||
           filePath.equals("/data/baseitemtypes.dat") ||
           filePath.equals("/data/itemvisualeffect.dat");
  }
}

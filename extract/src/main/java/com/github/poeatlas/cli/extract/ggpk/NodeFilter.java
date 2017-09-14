package com.github.poeatlas.cli.extract.ggpk;

import com.github.poeatlas.cli.extract.enums.NodeTypes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by NothingSoup on 7/5/17.
 */
public class NodeFilter {
  private static final Pattern ATLAS_MAP_PATTERN =
      Pattern.compile(
        "^/art/2ditems/maps/(?!^map)([a-z0-9]+\\.dds$)|atlasmaps/(notnamed/)?[^/]+\\.dds$");
  private static final Matcher ATLAS_MAP_MATCHER = ATLAS_MAP_PATTERN.matcher("");

  /**
   * filters for directories relevant to atlas information.
   *
   * @param node directory node containing path information to be filtered
   * @return boolean true if directory passes filter
   */
  private boolean directoryFilter(final DirectoryNode node) {
    final String nodePath = node.getPath().toLowerCase();

    return "/art/2dart/atlas".startsWith(nodePath)
           || "/textures/interface/2d".startsWith(nodePath)
           || "/art/2ditems/maps/atlasmaps".startsWith(nodePath)
           || "/art/2ditems/currency".startsWith(nodePath)
           || "/data".startsWith(nodePath)
           || "/art/2ditems/maps/atlasmaps/notnamed".startsWith(nodePath)
           || "/art/2ditems/maps".startsWith(nodePath)
           || "/art/textures/interface/2d".startsWith(nodePath);
  }

  /**
   * filters out unwanted directories and files.
   *
   * @param node node to check
   * @return true if node is wanted, false if node is unwanted
   */
  public boolean doFilter(final DataNode node) {
    return (node.getType() == NodeTypes.PDIR && directoryFilter(node.asDirectoryNode()))
           || (node.getType() == NodeTypes.FILE && fileFilter(node.asFileNode()));
  }

  /**
   * filters file names for relevant atlas information.
   *
   * @param node a file node to be passed through the filter
   * @return boolean true if file passes filter
   */
  private boolean fileFilter(final FileNode node) {
    final String filePath = node.getPath().toLowerCase();

    return "/art/2dart/atlas/atlas.dds".equals(filePath)
           || "/art/2dart/atlas/atlasblank.dds".equals(filePath)
           || "/textures/interface/2d/2dart_uiimages_ingame_3.dds".equals(filePath)
           || "/art/2ditems/currency/atlasradiuswhite.dds".equals(filePath)
           || "/art/2ditems/currency/atlasradiusyellow.dds".equals(filePath)
           || "/art/2ditems/currency/atlasradiusred.dds".equals(filePath)
           || "/art/2ditems/currency/upgrademaptoyellow.dds".equals(filePath)
           || "/art/2ditems/currency/upgrademaptored.dds".equals(filePath)
           || "/art/2ditems/currency/atlasdowngrade.dds".equals(filePath)
           || "/art/2ditems/currency/sealwhite.dds".equals(filePath)
           || "/art/2ditems/currency/sealyellow.dds".equals(filePath)
           || "/art/2ditems/currency/sealred.dds".equals(filePath)
           || "/art/textures/interface/2d/2dart_uiimages_ingame_3.dds".equals(filePath)
           || "/art/textures/interface/2d/2dart_uiimages_ingame_6.dds".equals(filePath)
           || "/data/atlasnode.dat".equals(filePath)
           || "/data/worldareas.dat".equals(filePath)
           || "/data/baseitemtypes.dat".equals(filePath)
           || "/data/itemvisualidentity.dat".equals(filePath)
           || "/data/atlasquestitems.dat".equals(filePath)
           || ATLAS_MAP_MATCHER.reset(filePath).matches();
  }
}

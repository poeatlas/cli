package com.github.poeatlas.cli.dat.util;

import com.github.poeatlas.cli.dat.domain.ConnectedAtlasNode;
import com.github.poeatlas.cli.dat.domain.ItemVisualIdentity;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

public class MapUtil {
  private MapUtil() {
    // not allowed
  }

  /**
   * <p>Converts a list of {@link ConnectedAtlasNode} into a a list of the connected atlas node
   * ids.</p>
   *
   * @param connectedAtlasNodes the list of connected atlas nodes
   * @return the list of connected atlas node ids
   */
  public static List<Integer> getConnectedMapIds(final List<ConnectedAtlasNode>
                                                     connectedAtlasNodes) {
    if (connectedAtlasNodes == null || connectedAtlasNodes.isEmpty()) {
      return null;
    }

    return connectedAtlasNodes.stream()
        .map(ConnectedAtlasNode::getId)
        .map(ConnectedAtlasNode.ConnectedId::getConnectedAtlasNodeId)
        .collect(Collectors.toList());
  }

  /**
   * <p>Converts an {@link ItemVisualIdentity}'s icon path into a PNG file extension.</p>
   *
   * @param itemVisualIdentity an {@link ItemVisualIdentity}
   * @return the string with a PNG extension
   */
  public static String getIconPath(final ItemVisualIdentity itemVisualIdentity) {
    if (itemVisualIdentity == null) {
      return null;
    }

    return StringUtils.join(itemVisualIdentity.getDdsFile()
        .replaceFirst("\\.dds$", ""), ".png");
  }
}

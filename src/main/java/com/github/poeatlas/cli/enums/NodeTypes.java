package com.github.poeatlas.cli.enums;

import com.github.poeatlas.cli.exceptions.InvalidNodeTypeException;

import java.nio.charset.StandardCharsets;

/**
 * Created by blei on 6/22/17.
 */
public enum NodeTypes {
  GGPK,
  PDIR,
  FILE,
  FREE;

  /** Returns a NodeTypes based on the bytes provided. */
  public static NodeTypes valueOf(final byte[] type)
      throws InvalidNodeTypeException {
    final NodeTypes[] nodeTypes = NodeTypes.values();
    final String str = new String(type, StandardCharsets.UTF_8);

    for (final NodeTypes nodeType : nodeTypes) {
      if (str.equals(nodeType.name())) {
        return nodeType;
      }
    }

    throw new InvalidNodeTypeException("Unknown inputFile type: " + str);
  }
}

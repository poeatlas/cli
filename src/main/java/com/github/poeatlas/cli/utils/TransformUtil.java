package com.github.poeatlas.cli.utils;

/**
 * Created by NothingSoup on 6/28/17.
 */
public class TransformUtil {
  private TransformUtil() {
    // do nothing
  }

  /**
   * converts digest byte array value to hex.
   * @param digest byte array containing digest value of node
   * @return String representation of digest
   */
  public static final String digest(final byte[] digest) {
    final StringBuilder builder = new StringBuilder();
    for (final byte b : digest) {
      builder.append(String.format("%02x", b));
    }
    return builder.toString();
  }

}

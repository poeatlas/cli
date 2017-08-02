package com.github.poeatlas.cli.dat.util;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

public class DecoderUtils {
  private static final Set<? extends Number> nullValues;

  static {
    Set<? super Number> hashSet = new HashSet<>();
    hashSet.add(new BigInteger("FFFFFFFFEFEFEFEF", 16).longValue());
    hashSet.add(new BigInteger("00000000FEFEFEFE", 16).longValue());
    hashSet.add(new BigInteger("EFEFEFEFEFEFEFEF", 16).longValue());
    hashSet.add(new BigInteger("FEFEFEFEFEFEFEFE", 16).longValue());
    hashSet.add(new BigInteger("00000000FFFFFFFF", 16).longValue());
    nullValues = (Set<? extends Number>) hashSet;
  }

  public static boolean isNull(Number value) {
    return nullValues.contains(value);
  }
}

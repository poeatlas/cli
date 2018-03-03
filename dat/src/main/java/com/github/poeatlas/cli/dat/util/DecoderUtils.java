package com.github.poeatlas.cli.dat.util;

import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public final class DecoderUtils {
  private static final Set<? extends Number> NULL_VALUES;

  private DecoderUtils() {
    // do not create
  }

  static {
    final Set<? super Number> hashSet = new HashSet<>();
    hashSet.add(new BigInteger("FFFFFFFF", 16).intValue()); // null unsigned int
    hashSet.add(new BigInteger("FEFEFEFE", 16).intValue()); // null int
    hashSet.add(new BigInteger("EFEFEFEF", 16).floatValue()); // null float
    hashSet.add(new BigInteger("FFFFFFFFFFFFFFFF", 16).longValue()); // null unsigned long
    hashSet.add(new BigInteger("FEFEFEFEFEFEFEFE", 16).longValue()); // null long
    hashSet.add(new BigInteger("EFEFEFEFEFEFEFEF", 16).doubleValue()); // null double
    NULL_VALUES = (Set<? extends Number>) hashSet;
  }

  public static boolean isNull(final Number value) {

    return NULL_VALUES.contains(value);
  }
}

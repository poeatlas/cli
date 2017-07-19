package com.github.poeatlas.cli.dat.decoder;

import com.github.poeatlas.cli.dat.DatMeta;
import com.github.poeatlas.cli.dat.Main;
import com.github.poeatlas.cli.dat.exception.DatDecoderException;
import org.reflections.Reflections;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by blei on 7/18/17.
 */
public abstract class Decoder<T> {
  private static Map<Class<?>, Decoder<?>> decoders;

  // this will create a map of all implemented decoders
  static {
    final Reflections reflections = new Reflections(Main.class.getPackage().getName());
    final Set<Class<? extends Decoder>> subTypes = reflections.getSubTypesOf(Decoder.class);
    final Map<Class<?>, Decoder<?>> decoders = new HashMap<>();

    for (Class<? extends Decoder> impl : subTypes) {
      Type type = ((ParameterizedType) impl.getGenericSuperclass()).getActualTypeArguments()[0];
      try {
        decoders.put((Class<?>) type, impl.newInstance());
      } catch (InstantiationException | IllegalAccessException ex) {
        ex.printStackTrace();
        System.exit(1); // this should never happen
      }
    }

    Decoder.decoders = Collections.unmodifiableMap(decoders);
  }

  public static <U> Decoder<U> getDecoder(Class<U> clazz) {
    if (!decoders.containsKey(clazz)) {
      throw new DatDecoderException("No DAT decoder of type " + clazz.getName());
    }

    return (Decoder<U>) decoders.get(clazz);
  }

  public abstract T decode(final ByteBuffer buf, final DatMeta meta);

}

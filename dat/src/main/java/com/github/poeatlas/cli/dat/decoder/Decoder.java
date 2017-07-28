package com.github.poeatlas.cli.dat.decoder;

import com.github.poeatlas.cli.dat.DatMeta;
import com.github.poeatlas.cli.dat.Main;
import lombok.Data;
import org.apache.commons.lang3.ClassUtils;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.persistence.Transient;

/**
 * Created by blei on 7/20/17.
 */
@Data
public abstract class Decoder<T> {
  private static Map<Class<?>, Constructor<? extends Decoder>> decoders = new HashMap<>();

  private final DatMeta meta;
  private final Field field;
  private final boolean skippable;

  // this will create a ListMapper of all implemented decoders
  static {
    final Reflections reflections = new Reflections(Main.class.getPackage().getName());
    final Set<Class<? extends Decoder>> subTypes = reflections.getSubTypesOf(Decoder.class);

    for (Class<? extends Decoder> impl : subTypes) {
      Type fieldType =
          ((ParameterizedType) impl.getGenericSuperclass()).getActualTypeArguments()[0];

      try {
        // THIS IS THE LINE WHICH SPECIFIES THE CONSTRUCTOR OF THE DECODER
        Constructor<? extends Decoder> declaredConstructor =
            impl.getDeclaredConstructor(DatMeta.class, Field.class);

        // it's a parameterized class, e.g. List<T>
        if (fieldType instanceof ParameterizedType) {
          ParameterizedType parameterizedType = (ParameterizedType) fieldType;
          decoders.put((Class<?>) parameterizedType.getRawType(), declaredConstructor);
        } else {
          decoders.put((Class<?>) fieldType, declaredConstructor);
        }
      } catch (NoSuchMethodException e) {
        e.printStackTrace();
        System.exit(1);
      }
    }
  }

  Decoder(final DatMeta meta, final Field field) {
    this.meta = meta;
    this.field = field;
    this.skippable = field.getAnnotation(Transient.class) != null;
  }

  public abstract T decode(int id, final ByteBuffer buf);

  public abstract int getColumnLength();

  public static Decoder<?> getDecoder(final Field field, final DatMeta meta)
      throws IllegalAccessException, InvocationTargetException, InstantiationException {
    Class<?> clazz = ClassUtils.primitiveToWrapper(field.getType());
    Constructor<? extends Decoder> constructor;

    if (!decoders.containsKey(clazz)) { // get the wrapped decoder to make decisions
      constructor = decoders.get(Object.class);
    } else {
      constructor = decoders.get(clazz);
    }

    return constructor.newInstance(meta, field);
  }
}

package com.github.poeatlas.cli.dat.decoder;

import com.github.poeatlas.cli.dat.DatMeta;
import com.github.poeatlas.cli.dat.Main;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Data
@SuppressWarnings("PMD.SingularField")
public abstract class AbstractDecoder<T> {
  private static Map<Class<?>, Constructor<? extends AbstractDecoder>> decoders = new HashMap<>();

  private final DatMeta meta;
  private final Field field;
  private final boolean skippable;

  // this will create a ListMapper of all implemented decoders
  static {
    final Reflections reflections = new Reflections(Main.class.getPackage().getName());
    final Set<Class<? extends AbstractDecoder>> subTypes =
        reflections.getSubTypesOf(AbstractDecoder.class);

    for (final Class<? extends AbstractDecoder> impl : subTypes) {
      final Type fieldType =
          ((ParameterizedType) impl.getGenericSuperclass()).getActualTypeArguments()[0];

      try {
        // THIS IS THE LINE WHICH SPECIFIES THE CONSTRUCTOR OF THE DECODER
        final Constructor<? extends AbstractDecoder> declaredConstructor =
            impl.getDeclaredConstructor(DatMeta.class, Field.class);

        // it's a parameterized class, e.g. List<T>
        if (fieldType instanceof ParameterizedType) {
          final ParameterizedType parameterizedType = (ParameterizedType) fieldType;
          decoders.put((Class<?>) parameterizedType.getRawType(), declaredConstructor);
        } else {
          decoders.put((Class<?>) fieldType, declaredConstructor);
        }
      } catch (NoSuchMethodException ex) {
        log.info("Method not found: {}", ex);
        System.exit(1);
      }
    }
  }

  AbstractDecoder(final DatMeta meta, final Field field) {
    this.meta = meta;
    this.field = field;
    this.skippable = field.getAnnotation(Transient.class) != null;
  }

  public abstract T decode(int id, final ByteBuffer buf)
      throws IllegalAccessException, InstantiationException;

  public abstract int getColumnLength();

  /**
   * gets decoder based on class.
   * @param field field to decode
   * @param meta contains information relevant to decode the dat
   * @return decoder for specific class
   * @throws IllegalAccessException illegal access to class
   * @throws InvocationTargetException unable to invoke class
   * @throws InstantiationException unable to instantiate class
   */
  public static AbstractDecoder<?> getDecoder(final Field field, final DatMeta meta)
      throws IllegalAccessException, InvocationTargetException, InstantiationException {
    final Class<?> clazz = ClassUtils.primitiveToWrapper(field.getType());
    Constructor<? extends AbstractDecoder> constructor;

    if (!decoders.containsKey(clazz)) { // get the wrapped decoder to make decisions
      constructor = decoders.get(Object.class);
    } else {
      constructor = decoders.get(clazz);
    }

    return constructor.newInstance(meta, field);
  }
}

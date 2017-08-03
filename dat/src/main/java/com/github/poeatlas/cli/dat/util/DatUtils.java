package com.github.poeatlas.cli.dat.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EmbeddedId;
import javax.persistence.Id;

/**
 * Created by blei on 7/19/17.
 */
public final class DatUtils {

  private DatUtils() {
    // do not create
  }

  /**
   * get fields in dat file
   * @param clazz class of dat file (e.g. AtlasNode.dat is AtlasNode class)
   * @return list of fileds
   */
  public static List<Field> getFields(final Class<?> clazz) {
    final List<Field> fields = new ArrayList<>();

    for (final Field field : clazz.getDeclaredFields()) {
      final boolean isStatic = Modifier.isStatic(field.getModifiers());
      final boolean isId = field.getAnnotation(Id.class) != null
                           || field.getAnnotation(EmbeddedId.class) != null;

      if (!isStatic && !isId) {
        fields.add(field);
      }
    }

    return fields;
  }

  /** Gets the table id field. */
  public static Field getId(final Class<?> clazz) {
    for (final Field field : clazz.getDeclaredFields()) {
      // if it has the Id class, it's the one
      if (field.getAnnotation(Id.class) != null) {
        return field;
      }
    }
    // throw new RuntimeException("No Id annotation found in class.");
    return null;
  }
}

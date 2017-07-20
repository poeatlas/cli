package com.github.poeatlas.cli.dat.util;

import com.github.poeatlas.cli.dat.annotation.Spec;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Id;

/**
 * Created by blei on 7/19/17.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SpecUtils {
  /** Gets table spec'able fields (ordered). */
  public static List<Pair<Field, Spec>> getSpec(Class<?> clazz) {
    final List<Pair<Field, Spec>> fields = new ArrayList<>();

    for (Field field : clazz.getDeclaredFields()) {
      final Spec spec = field.getAnnotation(Spec.class);
      if (spec != null) {
        fields.add(Pair.of(field, spec));
      }
    }

    return fields;
  }

  /** Gets the table id field. */
  public static Field getId(Class<?> clazz) {
    for (Field field : clazz.getDeclaredFields()) {
      // add non static and non-transient fields into list for parsing
      if (!Modifier.isStatic(field.getModifiers())
          && field.getAnnotation(Id.class) != null) {
        return field;
      }
    }

    return null;
  }
}

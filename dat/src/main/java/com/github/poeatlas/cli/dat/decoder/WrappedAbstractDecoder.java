package com.github.poeatlas.cli.dat.decoder;

import static org.springframework.beans.PropertyAccessorFactory.forBeanPropertyAccess;

import com.github.poeatlas.cli.dat.DatMeta;
import com.github.poeatlas.cli.dat.exception.DatDecoderException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.PropertyAccessor;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.Objects;
import javax.persistence.Id;

@Slf4j
public class WrappedAbstractDecoder extends AbstractDecoder<Object> {
  private AbstractDecoder decoder;

  private String foreignEntityIdFieldName;

  private Class<?> foreignEntityClass;

  WrappedAbstractDecoder(final DatMeta meta, final Field field) {
    super(meta, field);
    init();
  }

  private void init() {
    final Field field = getField();
    // final OneToOne oneToOne = field.getAnnotation(OneToOne.class);

    // Objects.requireNonNull(oneToOne,
    //     "Wrapped field " + field.getName() + "does not contain OneToOne");

    foreignEntityClass = field.getType();
    final Field[] foreignEntityFields = foreignEntityClass.getDeclaredFields();

    Field foreignEntityIdField = null;
    // find Id field within foreign key object class
    for (final Field entityField : foreignEntityFields) {
      if (entityField.getAnnotation(Id.class) != null) {
        foreignEntityIdField = entityField;
        break;
      }
    }

    if (foreignEntityIdField != null) {
      foreignEntityIdFieldName = foreignEntityIdField.getName();
    }

    Objects.requireNonNull(foreignEntityIdField,
        "Missing foreign entity id.");
    Objects.requireNonNull(getMeta(),
        "Missing DatMeta--cannot decode without this information.");
    try {
      decoder = AbstractDecoder.getDecoder(foreignEntityIdField, getMeta());
    } catch (IllegalAccessException | InstantiationException | InvocationTargetException ex) {
      throw new DatDecoderException("Problem decoding dat: " + ex);
    }
  }

  @Override
  public Object decode(final int id, final ByteBuffer buf) {
    // create foreign key object and set fields with reflection
    final Object foreignEntityObj;
    try {
      foreignEntityObj = foreignEntityClass.newInstance();
    } catch (InstantiationException | IllegalAccessException ex) {
      throw new DatDecoderException("Problem decoding dat: " + ex);
    }
    final PropertyAccessor foreignEntityAccessor = forBeanPropertyAccess(foreignEntityObj);


    try {
      final Number decodedValue = (Number) decoder.decode(id, buf);
      if (decodedValue == null) {
        return null;
      }
      foreignEntityAccessor.setPropertyValue(foreignEntityIdFieldName, decodedValue);
    } catch (IllegalAccessException | InstantiationException ex) {
      throw new DatDecoderException("Problem decoding dat: " + ex);
    }

    return foreignEntityObj;
  }

  @Override
  public int getColumnLength() {
    return decoder.getColumnLength();
  }
}

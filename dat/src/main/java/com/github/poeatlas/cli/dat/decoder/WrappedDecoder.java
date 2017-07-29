package com.github.poeatlas.cli.dat.decoder;

import static org.springframework.beans.PropertyAccessorFactory.forBeanPropertyAccess;

import com.github.poeatlas.cli.dat.DatMeta;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.PropertyAccessor;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import javax.persistence.Id;

@Slf4j
public class WrappedDecoder extends Decoder<Object> {
  WrappedDecoder(DatMeta meta, Field field) {
    super(meta, field);
  }

  @Override
  public Object decode(int id, ByteBuffer buf) throws IllegalAccessException, InstantiationException {
    final Field field = getField();
    // final OneToOne oneToOne = field.getAnnotation(OneToOne.class);

    // Objects.requireNonNull(oneToOne,
    //     "Wrapped field " + field.getName() + "does not contain OneToOne");

    Class foreignEntityClass = (Class) field.getType();
    final Field[] foreignEntityFields = foreignEntityClass.getDeclaredFields();

    Field foreignEntityIdField = null;
    // find Id field within foreign key object class
    for (Field entityField : foreignEntityFields) {
      if (entityField.getAnnotation(Id.class) != null) {
        foreignEntityIdField = entityField;
        break;
      }
    }

    Decoder decoder = null;
    try {
      decoder = Decoder.getDecoder(foreignEntityIdField, getMeta());
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }

    // create foreign key object and set fields with reflection
    final Object foreignEntityObj = foreignEntityClass.newInstance();
    final PropertyAccessor foreignEntityAccessor = forBeanPropertyAccess(foreignEntityObj);

    foreignEntityAccessor.setPropertyValue(foreignEntityIdField.getName(), decoder.decode(id,buf));

    return foreignEntityObj;
  }

  @Override
  public int getColumnLength() {
    return 0;
  }
}

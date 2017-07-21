package com.github.poeatlas.cli.dat.decoder;

import com.github.poeatlas.cli.dat.DatMeta;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.nio.ByteBuffer;
import java.util.List;
import javax.persistence.OneToMany;

/**
 * Created by NothingSoup on 7/19/17.
 */
public class ListDecoder extends Decoder<List<?>> {
  private final Class<?> externalClass;

  ListDecoder(DatMeta meta, Field field) {
    super(meta, field);

    this.externalClass = (Class<?>) ((ParameterizedType) field.getType()
        .getGenericSuperclass()).getActualTypeArguments()[0];

    determineId();
  }

  private void determineId() {
    final Field field = getField();
    final OneToMany otm = field.getAnnotation(OneToMany.class);

    if (otm != null) {
      final String thisName = otm.mappedBy();
    }

  }


  @Override
  public List<?> decode(ByteBuffer buf) {
    return null;
  }

  @Override
  public int getColumnLength() {
    return 0;
  }
}

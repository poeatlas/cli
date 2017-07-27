package com.github.poeatlas.cli.dat.decoder;

import com.github.poeatlas.cli.dat.DatMeta;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

/**
 * Created by NothingSoup on 7/19/17.
 */
@Slf4j
public class ListDecoder extends Decoder<List<?>> {
  private static final int COLUMN_LENGTH = 8;

  private final Class<?> externalClass;

  private ReferenceList referenceList;

  public ListDecoder(DatMeta meta, Field field) {
    super(meta, field);

    this.externalClass = (Class<?>) ((ParameterizedType) field.getGenericType())
        .getActualTypeArguments()[0];

    determineId();

  }


  private void determineId() {
    final Field field = getField();
    final OneToMany otm = field.getAnnotation(OneToMany.class);

    if (otm == null) {
      return;
    }
    final String thisName = otm.mappedBy();

    final Field[] externalClassFields = externalClass.getDeclaredFields();

    Field thisConnectedField = null;
    // find embedded ID and the mappedBy field
    Field connectedId = null;
    for (Field f : externalClassFields) {
      if (f.getAnnotation(EmbeddedId.class) != null) {
        connectedId = f;
      }
      // this id -- this is the mappedBy field
      if (f.getName().equals(thisName)) {
        thisConnectedField = f;
      }
    }

    // get joined column annotation for this id field from mappedby and its name
    final JoinColumn thisJoinColumn = thisConnectedField
        .getAnnotation(JoinColumn.class);
    String thisJoinColumnName = "";
    if (thisJoinColumn != null) {
      thisJoinColumnName = thisJoinColumn.name();
    }

    String thisFieldName = "";
    String thatFieldName = "";
    // determine the this and that id field names (connected to and connected by)
    if (connectedId != null) {
      // get the inner class of embeddedIdClass
      final Class embeddedIdClass = connectedId.getDeclaringClass().getClasses()[0];

      final Field[] connectedIdFields = embeddedIdClass.getDeclaredFields();

      for (Field f : connectedIdFields) {
        final Column col = f.getAnnotation(Column.class);
        if (col != null && col.name().equals(thisJoinColumnName)) {
          thisFieldName = f.getName();
        } else if (col != null) {
          thatFieldName = f.getName();
        }
      }
      referenceList = ReferenceList.builder()
          .thisFieldName(thisFieldName)
          .thatFieldName(thatFieldName)
          .externalClassIdName(connectedId.getName())
          .embeddedIdClass(embeddedIdClass)
          .externalClass(externalClass)
          .build();
    }

    log.info("this field name: {}", referenceList.getThisFieldName());
    log.info("that field name: {}", referenceList.getThatFieldName());
  }

  @Override
  @SneakyThrows
  public List<?> decode(int id, ByteBuffer buf) {
    final DatMeta datMeta = getMeta();
    final int range = buf.getInt(); // number of values in list
    final int dataOffset = buf.getInt(); // where the data starts
    final int beginOffset = dataOffset + datMeta.getMagicOffset();

    List<Integer> valueList = new ArrayList<>();
    for (int i = beginOffset; i < beginOffset + range*4; i+=4) {
     // log.info("value: {}, offset: {}", buf.getInt(i), i);
     valueList.add(buf.getInt(i));
    }

    return referenceList.createReferenceObjects(valueList,id);
  }

  @Override
  public int getColumnLength() {
    return COLUMN_LENGTH;
  }
}

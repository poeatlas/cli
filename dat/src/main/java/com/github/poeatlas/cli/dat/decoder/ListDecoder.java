package com.github.poeatlas.cli.dat.decoder;

import com.github.poeatlas.cli.dat.DatMeta;
import com.github.poeatlas.cli.dat.decoder.mapper.ListMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

  private final ListMapper mapper;

  public ListDecoder(DatMeta meta, Field field) {
    super(meta, field);

    // the arg basically gets the T from List<T>.
    mapper = getMapper((Class<?>) ((ParameterizedType) field.getGenericType())
        .getActualTypeArguments()[0]);
  }

  private ListMapper getMapper(final Class<?> entityClass) {
    final Field field = getField();
    final OneToMany oneToMany = field.getAnnotation(OneToMany.class);

    Objects.requireNonNull(oneToMany,
        "List field " + field.getName() + " does not contain OneToMany annotation.");

    final String mappedByFieldName = oneToMany.mappedBy();
    final Field[] entityFields = entityClass.getDeclaredFields();

    Field mappedByField = null;
    Field entityEmbeddedIdField = null;

    // find embedded ID and the mappedBy field
    for (Field entityField : entityFields) {
      if (entityField.getAnnotation(EmbeddedId.class) != null) {
        entityEmbeddedIdField = entityField;
      }

      // this id -- this is the mappedBy field
      if (entityField.getName().equals(mappedByFieldName)) {
        mappedByField = entityField;
      }
    }

    Objects.requireNonNull(mappedByField, "Could not find field to map to: " + mappedByFieldName);

    // get joined column annotation for this id field from mappedBy and its name
    String entityIdSourceColumnName = mappedByField.getAnnotation(JoinColumn.class).name();

    Objects.requireNonNull(entityEmbeddedIdField,
        "Could not find the entity's embedded ID field which contains annotation EmeddedId.");

    // get the embedded id class of entityIdClass
    final Class<?> entityIdClass = entityEmbeddedIdField.getDeclaringClass().getClasses()[0];
    final Field[] entityIdFields = entityIdClass.getDeclaredFields();

    Field entityIdSourceField = null;
    Field entityIdDestField = null;

    for (Field entityIdField : entityIdFields) {
      final Column col = entityIdField.getAnnotation(Column.class);

      if (col != null && col.name().equals(entityIdSourceColumnName)) {
        entityIdSourceField = entityIdField;
      } else if (col != null) {
        entityIdDestField = entityIdField;
      }
    }

    Objects.requireNonNull(entityIdSourceField, "Entity ID class' source field not found.");
    Objects.requireNonNull(entityIdDestField, "Entity ID class' dest field not found.");

    // we might need entityIdSourceField and/or entityIdDestField later

    return ListMapper.builder()
        .entityIdSourceFieldName(entityIdSourceField.getName())
        .entityIdDestFieldName(entityIdDestField.getName())
        .entityIdFieldName(entityEmbeddedIdField.getName())
        .entityIdClass(entityIdClass)
        .entityClass(entityClass)
        .build();
  }

  @Override
  @SneakyThrows
  public List<?> decode(int id, ByteBuffer buf) {
    final DatMeta datMeta = getMeta();
    final int range = buf.getInt(); // number of values in list
    final int dataOffset = buf.getInt(); // where the data starts
    final int beginOffset = dataOffset + datMeta.getMagicOffset();

    List<Number> valueList = new ArrayList<>();
    for (int i = beginOffset; i < beginOffset + range * 4; i += 4) {
      valueList.add(buf.getInt(i));
    }
    log.info("current atlasNode Id: {}",id);
    return mapper.map(id, valueList);
  }

  @Override
  public int getColumnLength() {
    return COLUMN_LENGTH;
  }
}

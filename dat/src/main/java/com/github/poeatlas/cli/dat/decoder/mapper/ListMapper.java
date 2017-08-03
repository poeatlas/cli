package com.github.poeatlas.cli.dat.decoder.mapper;

import static org.springframework.beans.PropertyAccessorFactory.forBeanPropertyAccess;

import com.github.poeatlas.cli.dat.exception.MapperException;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.PropertyAccessor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class ListMapper {
  private Class<?> entityClass;
  private Class<?> entityIdClass;
  private String entityIdSourceFieldName;
  private String entityIdDestFieldName;
  private String entityIdFieldName;

  /**
   * maps values for list of items to respective sourceID item.
   * @param sourceId id of object that values will be mapped to
   * @param list list of ids
   * @return list of objects that are mapped
   */
  public List<?> map(final int sourceId, final List<? extends Number> list) {
    final List<Object> mappedList = new ArrayList<>();

    try {
      // create external object and set fields
      for (final Number destId : list) {
        final Object entityObj = entityClass.newInstance();
        final PropertyAccessor entityAccessor = forBeanPropertyAccess(entityObj);

        final Object entityIdObj = entityIdClass.newInstance();
        final PropertyAccessor entityIdAccessor = forBeanPropertyAccess(entityIdObj);

        // assign the id obj to the entity
        entityAccessor.setPropertyValue(entityIdFieldName, entityIdObj);

        // for the id obj, set its two fields
        entityIdAccessor.setPropertyValue(entityIdSourceFieldName, sourceId);
        entityIdAccessor.setPropertyValue(entityIdDestFieldName, destId);

        mappedList.add(entityObj);
      }
    } catch (IllegalAccessException | InstantiationException ex) {
      throw new MapperException("Could not map class.", ex);
    }

    return mappedList;
  }
}

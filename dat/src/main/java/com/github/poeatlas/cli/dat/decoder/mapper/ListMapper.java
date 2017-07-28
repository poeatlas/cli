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

  public List<?> map(final int sourceId, final List<? extends Number> list) {
    List<Object> mappedList = new ArrayList<>();

    try {
      // create external object and set fields
      for (Number destId : list) {
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

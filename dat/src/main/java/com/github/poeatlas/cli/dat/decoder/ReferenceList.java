package com.github.poeatlas.cli.dat.decoder;

import lombok.Builder;
import lombok.Data;
import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.PropertyAccessorFactory;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class ReferenceList {

  private String thisFieldName;

  private String thatFieldName;

  private String externalClassIdName;

  private Class<?> embeddedIdClass;

  private Class<?> externalClass;

  public List<?> createReferenceObjects(final List<Integer> valueList,
                                        final int thisId) throws IllegalAccessException, InstantiationException {
    List<Object> objectList = new ArrayList<>();
    for (int value: valueList) {
      // create external object and set fields
      final Object externalObject = externalClass.newInstance();

      final PropertyAccessor externalRecordAccessor =
          PropertyAccessorFactory.forBeanPropertyAccess(externalObject);

      final Object embeddedIdObject = embeddedIdClass.newInstance();
      final PropertyAccessor embeddedRecordAccessor =
          PropertyAccessorFactory.forBeanPropertyAccess(embeddedIdObject);

      embeddedRecordAccessor.setPropertyValue(thisFieldName, thisId);
      embeddedRecordAccessor.setPropertyValue(thatFieldName, value);
      externalRecordAccessor.setPropertyValue(externalClassIdName, embeddedIdObject);

      objectList.add(externalObject);
    }
    return objectList;
  }
}

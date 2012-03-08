package me.prettyprint.hom.mapping;

import java.beans.PropertyDescriptor;

import me.prettyprint.hom.PropertyMappingDefinition;
import me.prettyprint.hom.converters.Converter;

public class PropertyMappingDefinitionCollection extends PropertyMappingDefinition {
  private Class<?> collectionType;

  public PropertyMappingDefinitionCollection(PropertyDescriptor propDesc, String colName,
      @SuppressWarnings("rawtypes") Converter converter, Class<?> collectionType)
      throws InstantiationException, IllegalAccessException {
    super(propDesc, colName, converter);
    this.collectionType = collectionType;
  }

  public Class<?> getCollectionType() {
    return collectionType;
  }

  @Override
  public boolean isCollectionType() {
    return null != collectionType;
  }
}

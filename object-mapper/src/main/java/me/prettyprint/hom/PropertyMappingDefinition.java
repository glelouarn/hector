package me.prettyprint.hom;

import java.beans.PropertyDescriptor;

import me.prettyprint.hom.converters.Converter;

public class PropertyMappingDefinition {
  private PropertyDescriptor propDesc;
  private String colName;
  @SuppressWarnings("rawtypes")
  private Converter converter;

  public PropertyMappingDefinition(PropertyDescriptor propDesc, String colName,
      @SuppressWarnings("rawtypes") Converter converter) throws InstantiationException,
      IllegalAccessException {
    this.propDesc = propDesc;
    this.colName = colName;
    this.converter = converter;
  }

  protected PropertyMappingDefinition(PropertyDescriptor propDesc, String colName) {
    this.propDesc = propDesc;
    this.colName = colName;
  }

  public PropertyDescriptor getPropDesc() {
    return propDesc;
  }

  public String getColName() {
    return colName;
  }

  @SuppressWarnings("rawtypes")
  public Converter getConverter() {
    return converter;
  }

  @Override
  public String toString() {
    return "PropertyMappingDefinition [colName=" + colName + ", converter=" + converter
        + ", propDesc=" + propDesc + "]";
  }

  public boolean isCollectionType() {
    return false;
  }

  public boolean isEmbeddedType() {
    return false;
  }
}

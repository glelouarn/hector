package me.prettyprint.hom.cache;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import me.prettyprint.hom.mapping.PropertiesMappingDefs;

public interface ColumnParserValidator {
  <T> void parse(Field field, Annotation anno, PropertyDescriptor pd,
      PropertiesMappingDefs propertiesMappingDefs);
}

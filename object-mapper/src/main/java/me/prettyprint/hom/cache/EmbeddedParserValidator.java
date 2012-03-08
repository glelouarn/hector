package me.prettyprint.hom.cache;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import me.prettyprint.hom.annotations.Embedded;
import me.prettyprint.hom.mapping.PropertiesMappingDefs;

/**
 * Parse an {@link Embedded} property.
 * 
 * @author gildas
 */
public interface EmbeddedParserValidator {
  /**
   * Method to parse {@link Embedded} property.
   * 
   * @param field
   *          Associated field.
   * @param anno
   *          Annotation of type {@link Embedded}.
   * @param pd
   *          Property descriptor.
   * @param cfMapDef
   *          Properties mapping definitions.
   */
  <T> void parse(Field field, Annotation anno, PropertyDescriptor pd,
      PropertiesMappingDefs propertiesMappingDefs);
}

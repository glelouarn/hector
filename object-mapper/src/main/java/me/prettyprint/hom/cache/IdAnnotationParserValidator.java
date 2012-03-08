package me.prettyprint.hom.cache;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import me.prettyprint.hom.KeyDefinition;

/**
 * Parse {@link javax.persistence.Id} or
 * {@link me.prettyprint.hom.annotations.Id} annotations.
 * 
 * @author gildas
 */
public interface IdAnnotationParserValidator {
  /**
   * Parse annotated field.
   * 
   * @param field
   *          The field.
   * @param anno
   *          Field annotation of type Id.
   * @param pd
   *          Property definition.
   * @param keyDefinition
   *          Key definition.
   */
  <T> void parse(Field field, Annotation anno, PropertyDescriptor pd, KeyDefinition keyDefinition);
}

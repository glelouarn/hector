package me.prettyprint.hom.parser;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * Listener for event parsing class properties and fields.
 * 
 * @author gildas
 */
public interface ClassFieldsParserListener {
  /**
   * Class define no property.
   * 
   * @param effectiveClass
   *          Concerned class.
   */
  public <T> void noPropertyForClass(Class<T> effectiveClass);

  /**
   * Class field is not a Bean.
   * 
   * @param effectiveClass
   *          Concerned class.
   * @param f
   *          Concerned field.
   */
  public <T> void noPropertyForField(Class<T> effectiveClass, Field f);

  /**
   * Class field define no annotations.
   * 
   * @param effectiveClass
   *          Concerned class.
   * @param f
   *          Concerned field.
   */
  public <T> void noAnnotationForField(Class<T> effectiveClass, Field f);

  /**
   * One event per annotation for each field of Bean type.
   * 
   * @param effectiveClass
   *          Concerned class.
   * @param f
   *          Concerned field.
   * @param pd
   *          Description of the field.
   * @param anno
   *          Annotation.
   */
  public <T> void fieldAnnotationEvent(Class<T> effectiveClass, Field f, PropertyDescriptor pd,
      Annotation anno);
}

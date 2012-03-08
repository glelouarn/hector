package me.prettyprint.hom.parser;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import javax.persistence.Column;

import me.prettyprint.hom.cache.ColumnParser;
import me.prettyprint.hom.cache.ColumnParserValidator;
import me.prettyprint.hom.cache.HectorObjectMapperException;
import me.prettyprint.hom.mapping.PropertyMappingDefinitionEmbeddable;

/**
 * Listener used during {@link me.prettyprint.hom.annotationsEmbeddable}
 * properties parsing.
 * 
 * @author gildas
 */
public class EmbeddableFieldsParser<C> implements ClassFieldsParserListener {

  private PropertyMappingDefinitionEmbeddable propertyMappingDefinitionEmbeddable;

  private ColumnParserValidator columnPar = new ColumnParser();

  public EmbeddableFieldsParser(
      PropertyMappingDefinitionEmbeddable propertyMappingDefinitionEmbeddable) {
    this.propertyMappingDefinitionEmbeddable = propertyMappingDefinitionEmbeddable;
  }

  @Override
  public <T> void noPropertyForClass(Class<T> effectiveClass) {
    throw new HectorObjectMapperException("Could not find any properties annotated with @"
        + Column.class.getSimpleName());
  }

  @Override
  public <T> void fieldAnnotationEvent(Class<T> effectiveClass, Field f, PropertyDescriptor pd,
      Annotation anno) {
    // Property annotated with @Column
    if (anno instanceof Column || anno instanceof me.prettyprint.hom.annotations.Column) {
      columnPar.parse(f, anno, pd, propertyMappingDefinitionEmbeddable.getPropertiesMappingDefs());
    }
  }

  @Override
  public <T> void noPropertyForField(Class<T> effectiveClass, Field f) {
    throw new HectorObjectMapperException("Property, " + effectiveClass.getSimpleName() + "."
        + f.getName() + ", does not have proper setter/getter");
  }

  @Override
  public <T> void noAnnotationForField(Class<T> effectiveClass, Field f) {
  }
}

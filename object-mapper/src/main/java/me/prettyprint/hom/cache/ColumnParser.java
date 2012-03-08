package me.prettyprint.hom.cache;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;

import javax.persistence.Column;

import me.prettyprint.hom.PropertyMappingDefinition;
import me.prettyprint.hom.converters.Converter;
import me.prettyprint.hom.converters.DefaultConverter;
import me.prettyprint.hom.mapping.PropertiesMappingDefs;
import me.prettyprint.hom.mapping.PropertyMappingDefinitionCollection;

/**
 * Parse, validate, and set defaults if needed for Inheritance functionality.
 * 
 * @author bburruss
 */
public class ColumnParser implements ColumnParserValidator {

  @Override
  public <T> void parse(Field f, Annotation anno, PropertyDescriptor pd,
      PropertiesMappingDefs propertiesMappingDefs) {
    try {
      if (anno instanceof Column) {
        processColumnAnnotation(f, (Column) anno, pd, propertiesMappingDefs);
      } else if (anno instanceof me.prettyprint.hom.annotations.Column) {
        processColumnCustomAnnotation(f, (me.prettyprint.hom.annotations.Column) anno, pd,
            propertiesMappingDefs);
      } else {
        throw new HectorObjectMapperException("This class cannot parse annotation, "
            + anno.getClass().getSimpleName());
      }
    } catch (InstantiationException e) {
      throw new HectorObjectMapperException("Unable to instanciate converter for class "
          + anno.getClass().getSimpleName(), e);
    } catch (IllegalAccessException e) {
      throw new HectorObjectMapperException("Unable to instanciate converter for class "
          + anno.getClass().getSimpleName(), e);
    }
  }

  private <T> void processColumnAnnotation(Field f, Column anno, PropertyDescriptor pd,
      PropertiesMappingDefs propertiesMappingDefs) throws InstantiationException,
      IllegalAccessException {
    PropertyMappingDefinition md = new PropertyMappingDefinition(pd, anno.name(),
        DefaultConverter.class.newInstance());
    propertiesMappingDefs.addPropertyDefinition(md);
  }

  private void processColumnCustomAnnotation(Field f, me.prettyprint.hom.annotations.Column anno,
      PropertyDescriptor pd, PropertiesMappingDefs propertiesMappingDefs)
      throws InstantiationException, IllegalAccessException {
    @SuppressWarnings("rawtypes")
    Converter converter = anno.converter().newInstance();

    PropertyMappingDefinition md;

    // if collection type and default converter then make note of collection
    // type for later use
    Class<?> type = pd.getPropertyType();
    if (Collection.class.isAssignableFrom(type) && (converter instanceof DefaultConverter)) {
      md = new PropertyMappingDefinitionCollection(pd, anno.name(), converter, type);
    }
    // standard type
    else {
      md = new PropertyMappingDefinition(pd, anno.name(), converter);
    }

    propertiesMappingDefs.addPropertyDefinition(md);
  }
}

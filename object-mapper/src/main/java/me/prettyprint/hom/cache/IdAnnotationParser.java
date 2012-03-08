package me.prettyprint.hom.cache;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import javax.persistence.Id;

import me.prettyprint.hom.KeyDefinition;
import me.prettyprint.hom.PropertyMappingDefinition;
import me.prettyprint.hom.converters.Converter;
import me.prettyprint.hom.converters.DefaultConverter;

/**
 * Parse {@link javax.persistence.Id} or
 * {@link me.prettyprint.hom.annotations.Id} annotations.
 * 
 * @author gildas
 */
public class IdAnnotationParser implements IdAnnotationParserValidator {

  @Override
  public <T> void parse(Field field, Annotation anno, PropertyDescriptor pd,
      KeyDefinition keyDefinition) {
    try {
      @SuppressWarnings("rawtypes")
      Converter converter;
      if (anno instanceof Id) {
        converter = DefaultConverter.class.newInstance();
      } else if (anno instanceof me.prettyprint.hom.annotations.Id) {
        converter = ((me.prettyprint.hom.annotations.Id) anno).converter().newInstance();
      } else {
        throw new HectorObjectMapperException("This class cannot parse annotation, "
            + anno.getClass().getSimpleName());
      }

      // TODO lookup JPA 2 spec for class-level ids
      PropertyMappingDefinition md = new PropertyMappingDefinition(pd, null, converter);

      if (null == md.getPropDesc() || null == md.getPropDesc().getReadMethod()
          || null == md.getPropDesc().getWriteMethod()) {
        throw new HectorObjectMapperException("@" + Id.class.getSimpleName()
            + " is defined on property, " + field.getName()
            + ", but its missing proper setter/getter");
      }

      keyDefinition.addIdPropertyMap(md);
    } catch (InstantiationException e) {
      throw new HectorObjectMapperException("Unable to instanciate converter for class "
          + anno.getClass().getSimpleName(), e);
    } catch (IllegalAccessException e) {
      throw new HectorObjectMapperException("Unable to instanciate converter for class "
          + anno.getClass().getSimpleName(), e);
    }
  }
}

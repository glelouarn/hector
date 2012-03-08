package me.prettyprint.hom.cache;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import me.prettyprint.hom.annotations.Embeddable;
import me.prettyprint.hom.annotations.Embedded;
import me.prettyprint.hom.mapping.PropertiesMappingDefs;
import me.prettyprint.hom.mapping.PropertyMappingDefinitionEmbeddable;

/**
 * Implementation used to parse an
 * {@link me.prettyprint.hom.annotations.Embedded} property.
 * 
 * @author gildas
 */
public class EmbeddedParser implements EmbeddedParserValidator {

  @Override
  public <T> void parse(Field f, Annotation anno, PropertyDescriptor pd,
      PropertiesMappingDefs propertiesMappingDefs) {
    if ((anno instanceof Embedded)
        && (pd.getPropertyType().getAnnotation(Embeddable.class) != null)) {
      Embedded embeddedAnno = (Embedded) anno;

      PropertyMappingDefinitionEmbeddable embeddablePropertyMappingDefinition = new PropertyMappingDefinitionEmbeddable(
          pd, pd.getPropertyType(), embeddedAnno.name(), embeddedAnno.nameSeparator());

      propertiesMappingDefs.addPropertyDefinition(embeddablePropertyMappingDefinition);
    } else {
      throw new HectorObjectMapperException("This class cannot parse annotation, "
          + anno.getClass().getSimpleName() + " because object must define "
          + Embeddable.class.getSimpleName() + " annotation");
    }
  }
}

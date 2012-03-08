package me.prettyprint.hom.parser;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import javax.persistence.Column;
import javax.persistence.Id;

import me.prettyprint.hom.CFMappingDef;
import me.prettyprint.hom.annotations.Embedded;
import me.prettyprint.hom.cache.ColumnParser;
import me.prettyprint.hom.cache.ColumnParserValidator;
import me.prettyprint.hom.cache.EmbeddedParser;
import me.prettyprint.hom.cache.EmbeddedParserValidator;
import me.prettyprint.hom.cache.HectorObjectMapperException;
import me.prettyprint.hom.cache.IdAnnotationParser;
import me.prettyprint.hom.cache.IdAnnotationParserValidator;

/**
 * Listener used during entity properties parsing.
 * 
 * @author gildas
 */
public class CFMappingFieldsParser<C> implements ClassFieldsParserListener {
  private CFMappingDef<C> cfMapDef;

  private ColumnParserValidator columnPar = new ColumnParser();
  private EmbeddedParserValidator embeddedPar = new EmbeddedParser();
  private IdAnnotationParserValidator idAnnotationPar = new IdAnnotationParser();

  public CFMappingFieldsParser(CFMappingDef<C> cfMapDef) {
    this.cfMapDef = cfMapDef;
  }

  @Override
  public <T> void noPropertyForClass(Class<T> effectiveClass) {
    if (!cfMapDef.isPersistableDerivedEntity()) {
      throw new HectorObjectMapperException("Could not find any properties annotated with @"
          + Column.class.getSimpleName());
    }
  }

  @Override
  public <T> void fieldAnnotationEvent(Class<T> effectiveClass, Field f, PropertyDescriptor pd,
      Annotation anno) {
    // Property annotated with @Column
    if (anno instanceof Column || anno instanceof me.prettyprint.hom.annotations.Column) {
      columnPar.parse(f, anno, pd, cfMapDef.getPropertiesMappingDefs());
    }
    // Property annotated with @Embedded and mapping an object
    // annotated with @Embeddable
    else if (anno instanceof Embedded) {
      embeddedPar.parse(f, anno, pd, cfMapDef.getPropertiesMappingDefs());
    }
    // Property annotated with @Id
    else if (anno instanceof Id || anno instanceof me.prettyprint.hom.annotations.Id) {
      idAnnotationPar.parse(f, anno, pd, cfMapDef.getKeyDef());
    }
  }

  @Override
  public <T> void noPropertyForField(Class<T> effectiveClass, Field f) {
    throw new HectorObjectMapperException("Property, " + effectiveClass.getSimpleName() + "."
        + f.getName() + ", does not have proper setter/getter");
  }

  @Override
  public <T> void noAnnotationForField(Class<T> effectiveClass, Field f) {
    // TODO BTB:assume @Basic - fields are not required to be
    // annotated to
    // be persisted if they are a "basic type" - see 2.8 and 11.1.6
    // @Transient to ignore a field
  }
}

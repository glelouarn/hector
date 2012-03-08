package me.prettyprint.hom.mapping;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

import me.prettyprint.hom.ClassFieldsHelper;
import me.prettyprint.hom.PropertyMappingDefinition;
import me.prettyprint.hom.cache.HectorObjectMapperException;
import me.prettyprint.hom.parser.EmbeddableFieldsParser;

/**
 * Specific property mapping definition used for class declaring
 * {@link me.prettyprint.hom.annotations.Embeddable} annotation.
 * 
 * @author gildas
 */
public class PropertyMappingDefinitionEmbeddable extends PropertyMappingDefinition {
  /**
   * Class defining {@link me.prettyprint.hom.annotations.Embeddable} annotation
   * mapped to an {@link me.prettyprint.hom.annotations.Embedded} property.
   */
  private Class<?> embeddedType;

  /**
   * Separator used between {@link me.prettyprint.hom.annotations.Embeddable}
   * name and {@link me.prettyprint.hom.annotations.Embedded} name to represent
   * Cassandra column name.
   */
  private char nameSeparator;

  /**
   * {@link me.prettyprint.hom.annotations.Embeddable} class mapped properties
   * defined in a single object.
   */
  private PropertiesMappingDefs propertiesMappingDefs = new PropertiesMappingDefs();

  /**
   * Constructor dedicated to properties of type
   * {@link me.prettyprint.hom.annotations.Embedded}.
   * 
   * @param propDesc
   *          Property descriptor.
   * @param embeddedType
   *          {@link me.prettyprint.hom.annotations.Embeddable} object class
   *          type.
   * @param colName
   *          Column name (used as prefix),
   *          {@link me.prettyprint.hom.annotations.Embeddable} property names
   *          will be used as suffix for column names.
   */
  public <T> PropertyMappingDefinitionEmbeddable(PropertyDescriptor propDesc,
      Class<?> embeddedType, String colName, char nameSeparator) {
    super(propDesc, colName);
    this.embeddedType = embeddedType;
    this.nameSeparator = nameSeparator;

    try {
      ClassFieldsHelper classFieldsHelper = new ClassFieldsHelper();
      classFieldsHelper.parseAllFieldsProperties(embeddedType, new EmbeddableFieldsParser<T>(this));
    } catch (IntrospectionException e) {
      throw new HectorObjectMapperException("Unable to introspect class " + embeddedType, e);
    }
  }

  /**
   * Getter.
   * 
   * @return Class defining {@link me.prettyprint.hom.annotations.Embeddable}
   *         annotation mapped to an
   *         {@link me.prettyprint.hom.annotations.Embedded} property.
   */
  public Class<?> getEmbeddedType() {
    return embeddedType;
  }

  /**
   * Is current property of type
   * {@link me.prettyprint.hom.annotations.Embeddable} and mapped to an
   * {@link me.prettyprint.hom.annotations.Embedded} object.
   * 
   * @return True if property is an
   *         {@link me.prettyprint.hom.annotations.Embeddeed} object.
   */
  @Override
  public boolean isEmbeddedType() {
    return null != embeddedType;
  }

  /**
   * Getter.
   * 
   * @return Used separator.
   */
  public char getNameSeparator() {
    return nameSeparator;
  }

  /**
   * Getter.
   * 
   * @return {@link me.prettyprint.hom.annotations.Embeddable} class mapped
   *         properties.
   */
  public PropertiesMappingDefs getPropertiesMappingDefs() {
    return propertiesMappingDefs;
  }
}

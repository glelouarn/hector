package me.prettyprint.hom.mapping;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import me.prettyprint.hom.PropertyMappingDefinition;

/**
 * Collection of properties mapping definition.
 * 
 * @author gildas
 */
public class PropertiesMappingDefs {
  /**
   * Provides caching by class object for the class' mapping definition.
   */
  private Map<String, PropertyMappingDefinition> propertyCacheByPropName = new HashMap<String, PropertyMappingDefinition>();

  /**
   * Provides caching by class object for the class' mapping definition.
   */
  private Map<String, PropertyMappingDefinition> propertyCacheByColName = new HashMap<String, PropertyMappingDefinition>();

  /**
   * Provides caching all mapping definition.
   */
  private Collection<PropertyMappingDefinition> mappedProps;

  /**
   * Property mapping definition from property name.
   * 
   * @param propName
   *          property name.
   * @return Expected result, null if not found.
   */
  public PropertyMappingDefinition getPropMapByPropName(String propName) {
    return propertyCacheByPropName.get(propName);
  }

  /**
   * Property mapping definition from column name.
   * 
   * @param colName
   *          Column name.
   * @return Expected result, null if not found.
   */
  public PropertyMappingDefinition getPropMapByColumnName(String colName) {
    return propertyCacheByColName.get(colName);
  }

  /**
   * Retrieve all collection mapped properties definitions.
   * 
   * @return Expected result never null but can be empty.
   */
  public Collection<PropertyMappingDefinition> getMappedProps() {
    if (null == mappedProps) {
      Set<PropertyMappingDefinition> propSet = new HashSet<PropertyMappingDefinition>();
      for (PropertyMappingDefinition propMapDef : propertyCacheByColName.values()) {
        propSet.add(propMapDef);
      }
      mappedProps = propSet;
    }

    return mappedProps;
  }

  /**
   * Add a property mapping definition to collection.
   * 
   * @param propDef
   *          Property mapping definition to add.
   */
  public void addPropertyDefinition(PropertyMappingDefinition propDef) {
    propertyCacheByColName.put(propDef.getColName(), propDef);
    propertyCacheByPropName.put(propDef.getPropDesc().getName(), propDef);
  }
}

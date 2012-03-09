package me.prettyprint.hom;

import java.beans.IntrospectionException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.IdClass;
import javax.persistence.Inheritance;
import javax.persistence.Table;

import me.prettyprint.hom.annotations.AnonymousPropertyHandling;
import me.prettyprint.hom.cache.AnonymousParserValidator;
import me.prettyprint.hom.cache.HectorObjectMapperException;
import me.prettyprint.hom.cache.IdClassParserValidator;
import me.prettyprint.hom.cache.InheritanceParserValidator;
import me.prettyprint.hom.cache.TableParserValidator;
import me.prettyprint.hom.mapping.PropertyMappingDefinitionEmbeddable;
import me.prettyprint.hom.parser.CFMappingFieldsParser;

/**
 * Manage parsing and caching of class meta-data.
 * 
 * @author
 */
public class ClassCacheMgr {
  private Map<String, CFMappingDef<?>> cfMapByColFamName = new HashMap<String, CFMappingDef<?>>();
  private Map<Class<?>, CFMappingDef<?>> cfMapByClazz = new HashMap<Class<?>, CFMappingDef<?>>();

  private InheritanceParserValidator inheritanceParVal = new InheritanceParserValidator();
  private TableParserValidator tableParVal = new TableParserValidator();
  private IdClassParserValidator idClassParVal = new IdClassParserValidator();
  private AnonymousParserValidator anonymousParVal = new AnonymousParserValidator();

  /**
   * Examine class hierarchy using {@link CFMappingDef} objects to discover the
   * given class' "base inheritance" class. A base inheritance class is
   * determined by {@link CFMappingDef#isBaseEntity()}
   * 
   * @param <T>
   * 
   * @param cfMapDef
   * @return returns the base in the ColumnFamily mapping hierarchy
   */
  public <T> CFMappingDef<? super T> findBaseClassViaMappings(CFMappingDef<T> cfMapDef) {
    CFMappingDef<? super T> tmpDef = cfMapDef;
    CFMappingDef<? super T> cfSuperDef;
    while (null != (cfSuperDef = tmpDef.getCfSuperMapDef())) {
      if (cfSuperDef.isBaseEntity()) {
        return cfSuperDef;
      }
      tmpDef = cfSuperDef;
    }
    return null;
  }

  /**
   * Retrieve class mapping meta-data by <code>Class</code> object.
   * 
   * @param <T>
   * @param clazz
   * @param throwException
   * @return CFMappingDef if found, exception if throwException = true, and null
   *         otherwise
   */
  public <T> CFMappingDef<T> getCfMapDef(Class<T> clazz, boolean throwException) {
    @SuppressWarnings("unchecked")
    CFMappingDef<T> cfMapDef = (CFMappingDef<T>) cfMapByClazz.get(clazz);
    if (null == cfMapDef && throwException) {
      throw new HectorObjectMapperException(
          "could not find property definitions for class, "
              + clazz.getSimpleName()
              + ", in class cache.  This indicates the EntityManager was not initialized properly.  If not using EntityManager the cache must be explicity initialized");

    }
    return cfMapDef;
  }

  /**
   * Retrieve class mapping meta-data by ColumnFamily name.
   * 
   * @param <T>
   * @param colFamName
   * @param throwException
   * @return CFMappingDef if found, exception if throwException = true, and null
   *         otherwise
   */
  public <T> CFMappingDef<T> getCfMapDef(String colFamName, boolean throwException) {
    @SuppressWarnings("unchecked")
    CFMappingDef<T> cfMapDef = (CFMappingDef<T>) cfMapByColFamName.get(colFamName);
    if (null == cfMapDef && throwException) {
      throw new HectorObjectMapperException(
          "could not find property definitions for column family, "
              + colFamName
              + ", in class cache.  This indicates the EntityManager was not initialized properly.  If not using EntityManager the cache must be explicity initialized");

    }
    return cfMapDef;
  }

  /**
   * For each class that should be managed, this method must be called to parse
   * its annotations and derive its meta-data.
   * 
   * @param <T>
   * 
   * @param clazz
   * @return CFMapping describing the initialized class.
   */
  public <T> CFMappingDef<T> initializeCacheForClass(Class<T> clazz) {
    CFMappingDef<T> cfMapDef = initializeColumnFamilyMapDef(clazz);
    try {
      ClassFieldsHelper classFieldsPropertiesHelper = new ClassFieldsHelper();
      classFieldsPropertiesHelper.parseAllFieldsProperties(cfMapDef.getEffectiveClass(),
          new CFMappingFieldsParser<T>(cfMapDef));
    } catch (IntrospectionException e) {
      throw new HectorObjectMapperException("Unable to introspect class "
          + cfMapDef.getEffectiveClass().getSimpleName(), e);
    }

    // by the time we get here, all super classes and their annotations have
    // been processed and validated, and all annotations for this class have
    // been processed. what's left to do is validate this class, set super
    // classes, and and set any defaults
    checkMappingAndSetDefaults(cfMapDef);

    // if this class is not a derived class, then map the ColumnFamily name
    if (!cfMapDef.isDerivedEntity()) {
      cfMapByColFamName.put(cfMapDef.getEffectiveColFamName(), cfMapDef);
    }

    // always map the parsed class to its ColumnFamily map definition
    cfMapByClazz.put(cfMapDef.getRealClass(), cfMapDef);

    return cfMapDef;
  }

  private <T> CFMappingDef<T> initializeColumnFamilyMapDef(Class<T> realClass) {
    // if already init'd don't do it again - could have happened because of
    // inheritance - causes recursive processing for class hierarchy
    CFMappingDef<T> cfMapDef = getCfMapDef(realClass, false);
    if (null != cfMapDef) {
      return cfMapDef;
    }

    cfMapDef = new CFMappingDef<T>(realClass);

    Class<T> effectiveType = cfMapDef.getEffectiveClass();
    CFMappingDef<? super T> cfSuperMapDef = null;

    // if this class extends a super, then process it first
    if (null != effectiveType.getSuperclass()) {
      try {
        cfSuperMapDef = initializeCacheForClass(effectiveType.getSuperclass());
        cfMapDef.setCfSuperMapDef(cfSuperMapDef);
      } catch (HomMissingEntityAnnotationException e) {
        // ok, becuase may not have a super class that's an entity
      }
    }

    Annotation[] annoArr = effectiveType.getAnnotations();
    if (null == annoArr) {
      // TODO:btb see if this might be an error
      return cfMapDef;
    }

    for (Annotation anno : annoArr) {
      if (anno instanceof Table) {
        tableParVal.parse(this, anno, cfMapDef);
      } else if (anno instanceof IdClass) {
        idClassParVal.parse(this, anno, cfMapDef);
      } else if (anno instanceof Inheritance) {
        inheritanceParVal.parse(this, anno, cfMapDef);
      } else if (anno instanceof DiscriminatorColumn) {
        inheritanceParVal.parse(this, anno, cfMapDef);
      } else if (anno instanceof DiscriminatorValue) {
        inheritanceParVal.parse(this, anno, cfMapDef);
      } else if (anno instanceof AnonymousPropertyHandling) {
        anonymousParVal.parse(this, anno, cfMapDef);
      }
    }

    return cfMapDef;
  }

  private <T> void checkMappingAndSetDefaults(CFMappingDef<T> cfMapDef) {
    inheritanceParVal.validateAndSetDefaults(this, cfMapDef);
    tableParVal.validateAndSetDefaults(this, cfMapDef);
    idClassParVal.validateAndSetDefaults(this, cfMapDef);
    anonymousParVal.validateAndSetDefaults(this, cfMapDef);

    // must do this after tabeParVal validate
    checkForPojoPrimaryKey(cfMapDef);

    // checkForAnonymousHandler(cfMapDef);

    generateColumnSliceIfNeeded(cfMapDef);
  }

  private void checkForPojoPrimaryKey(CFMappingDef<?> cfMapDef) {
    // if we know it's a complex key then it must be present so we only
    // check
    // case for simple one field key

    // SimpleTestBean breaks this check right now because it uses method
    // annotations which isn't supported by the ClassCacheMgr at this time
    // if (!cfMapDef.getKeyDef().isComplexKey()) {
    // if (!cfMapDef.getKeyDef().isSimpleIdPresent()) {
    // throw new HectorObjectMapperException("Entity, " +
    // cfMapDef.getRealClass().getName()
    // +
    // ", is missing a primary key.  Must annotate at least one field with @"
    // + Id.class.getSimpleName() + " or use a complex primary key");
    // }
    // }
  }

  // private <T> void checkForAnonymousHandler(CFMappingDef<T> cfMapDef) {
  // CFMappingDef<? super T> tmpDef = cfMapDef;
  // while (null != tmpDef) {
  // Method meth = findAnnotatedMethod(cfMapDef.getEffectiveClass(),
  // AnonymousPropertyAddHandler.class);
  // if (null != meth) {
  // Class<?>[] typeArr = meth.getParameterTypes();
  // if (2 != typeArr.length || !(typeArr[0] == String.class) || !(typeArr[1]
  // == byte[].class)) {
  // throw new
  // HectorObjectMapperException(AnonymousPropertyAddHandler.class.getSimpleName()
  // +
  // " expects a method with exactly two paramters of types, String and byte[]");
  // }
  //
  // cfMapDef.setAnonymousPropertyAddHandler(meth);
  // return;
  // }
  // tmpDef = tmpDef.getCfSuperMapDef();
  // }
  // }

  private void generateColumnSliceIfNeeded(CFMappingDef<?> cfMapDef) {
    if (cfMapDef.isColumnSliceRequired()) {
      // For result
      ArrayList<String> resultColNames = new ArrayList<String>();

      // Generation of column names
      generateColNamesRecursiv(cfMapDef.getAllProperties(), "", resultColNames);

      // if an inheritance hierarchy exists we need to add in the discriminator
      // column
      if (!cfMapDef.isPersistableEntity()) {
        resultColNames.add(cfMapDef.getDiscColumn());
      }

      // Store result
      cfMapDef.setSliceColumnNameArr((String[]) resultColNames.toArray(new String[0]));
    }
  }

  /**
   * Recursive function used to determine all property names.
   * 
   * @param propsMapDefs
   *          Collection of properties.
   * @param namePrefix
   *          Prefix used to name properties, required for recursive calls.
   * @param resultColNames
   *          Result map of all constructed property names.
   */
  private void generateColNamesRecursiv(Collection<PropertyMappingDefinition> propsMapDefs,
      String namePrefix, ArrayList<String> resultColNames) {
    if (null != propsMapDefs) {
      for (PropertyMappingDefinition currPropMapDef : propsMapDefs) {
        // Column property, simply add name with prefix
        if (!currPropMapDef.isEmbeddedType()) {
          resultColNames.add(namePrefix + currPropMapDef.getColName());
        }
        // Embeddable property, recursive call to manage all embbeddable class
        // properties
        else {
          // Embeddable property definition
          PropertyMappingDefinitionEmbeddable embPropMapDef = (PropertyMappingDefinitionEmbeddable) currPropMapDef;

          // Embeddable children properties
          Collection<PropertyMappingDefinition> childrenProps = embPropMapDef.getPropertiesMappingDefs()
                                                                             .getMappedProps();

          // Prefix to use for children properties
          String childrenPrefix = namePrefix + embPropMapDef.getColName()
              + embPropMapDef.getNameSeparator();

          // Recursive call to manage children names
          generateColNamesRecursiv(childrenProps, childrenPrefix, resultColNames);
        }
      }
    }
  }

  /**
   * Find method annotated with the given annotation.
   * 
   * @param clazz
   * @param anno
   * @return returns Method if found, null otherwise
   */
  public Method findAnnotatedMethod(Class<?> clazz, Class<? extends Annotation> anno) {
    for (Method meth : clazz.getMethods()) {
      if (meth.isAnnotationPresent(anno)) {
        return meth;
      }
    }
    return null;
  }
}

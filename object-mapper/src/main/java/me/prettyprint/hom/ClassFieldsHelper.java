package me.prettyprint.hom;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import me.prettyprint.hom.parser.ClassFieldsParserListener;

/**
 * Helper class used to interact with class fields, properties and annotation.
 * 
 * @author gildas
 */
public class ClassFieldsHelper {

  /**
   * Parse a class looking for all annotation for each field of Bean class.
   * 
   * @param effectiveClass
   *          Concerned class.
   * @param listener
   *          Manage events.
   * @throws IntrospectionException
   *           Can't evaluate bean description.
   */
  public <T> void parseAllFieldsProperties(Class<T> effectiveClass,
      ClassFieldsParserListener listener) throws IntrospectionException {
    // Bean exposed content
    Map<String, PropertyDescriptor> pdMap = getFieldPropertyDescriptorMap(effectiveClass);

    // Notification about a class without any property
    if (pdMap.isEmpty()) {
      listener.noPropertyForClass(effectiveClass);
    }

    Field[] fieldArr = effectiveClass.getDeclaredFields();

    // iterate over all declared fields (for this class only, no inherited
    // fields) processing annotations as we go
    for (Field f : fieldArr) {
      Annotation[] annoArr = f.getAnnotations();

      // Notification about a field without annotation
      if (null == annoArr) {
        listener.noAnnotationForField(effectiveClass, f);
      }

      for (Annotation anno : annoArr) {
        PropertyDescriptor pd = pdMap.get(f.getName());

        // Notification about a field without associated property
        if (null == pd) {
          listener.noPropertyForField(effectiveClass, f);
        }

        // Notification about one annotation for a field
        listener.fieldAnnotationEvent(effectiveClass, f, pd, anno);
      }
    }
  }

  /**
   * Retrieve all properties for a class.
   * 
   * @param Concerned
   *          class.
   * @return Description for each field.
   * @throws IntrospectionException
   *           Can't evaluate bean description.
   */
  public Map<String, PropertyDescriptor> getFieldPropertyDescriptorMap(Class<?> clazz)
      throws IntrospectionException {
    Map<String, PropertyDescriptor> pdMap = new HashMap<String, PropertyDescriptor>();

    // get descriptors for all properties in POJO
    PropertyDescriptor[] pdArr = Introspector.getBeanInfo(clazz, clazz.getSuperclass())
                                             .getPropertyDescriptors();

    // if no property descriptors then return leaving empty annotation map
    if (null == pdArr || 0 == pdArr.length) {
      return pdMap;
    }

    // create tmp map for easy field -> descriptor mapping
    for (PropertyDescriptor pd : pdArr) {
      pdMap.put(pd.getName(), pd);
    }

    return pdMap;
  }
}

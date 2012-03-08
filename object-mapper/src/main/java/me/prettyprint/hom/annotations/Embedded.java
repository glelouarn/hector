package me.prettyprint.hom.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for specifying whose value of type @link {@link link
 * me.prettyprint.hom.annotations.Embeddable} class should be mapped to
 * Cassandra entity class. The {@link link
 * me.prettyprint.hom.annotations.Embeddable} class must be annotated as
 * {@link me.prettyprint.hom.annotations.Embeddable}. Must specify "name" as the
 * column prefix name in Cassandra.
 * 
 * @author
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Embedded {

  /**
   * The Cassandra column name.
   * 
   * @return name of column
   */
  String name();

  /**
   * Separator used between {@link me.prettyprint.hom.annotations.Embeddable}
   * name and {@link me.prettyprint.hom.annotations.Embedded} name to represent
   * Cassandra column name.
   * 
   * @return Used separator.
   */
  char nameSeparator() default '.';
}

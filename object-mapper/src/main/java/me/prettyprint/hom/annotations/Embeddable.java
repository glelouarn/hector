package me.prettyprint.hom.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Defines a class whose instances are stored as an intrinsic part of an owning
 * entity and share the identity of the entity. Each of the persistent
 * properties or fields of the embedded object is mapped to the Cassandra column
 * family.
 * 
 * @author
 */
@Retention(RUNTIME)
@Target({ TYPE })
public @interface Embeddable {

}

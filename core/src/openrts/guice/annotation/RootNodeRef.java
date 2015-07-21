/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package openrts.guice.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.google.inject.BindingAnnotation;

/**
 *
 * @author lbrasseur
 */
@BindingAnnotation
@Target({FIELD})
@Retention(RUNTIME)
public @interface RootNodeRef {
}

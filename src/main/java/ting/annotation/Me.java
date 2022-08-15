package ting.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * If an argument of a route is annotated by this and its type is {@link ting.dto.UserDto},
 * then the value of this argument is resolved to current login user in this session.
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Me {
}

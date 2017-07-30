package com.gmail.tarkhanov.lev.reflection.config.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Lev Tarkhanov on 29-Jul-17.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CustomJsonFieldName {

    String jsonFieldName();

}

package com.github.poeatlas.cli.dat.annotation;

import com.github.poeatlas.cli.dat.decoder.Decoder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by blei on 7/19/17.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Spec {
  Class<? extends Decoder> value();
  boolean skip() default false;
}

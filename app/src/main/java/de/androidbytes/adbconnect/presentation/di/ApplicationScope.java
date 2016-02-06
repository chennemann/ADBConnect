package de.androidbytes.adbconnect.presentation.di;


/**
 * Created by Christoph on 19.09.2015.
 */
import java.lang.annotation.Retention;

import javax.inject.Scope;

import static java.lang.annotation.RetentionPolicy.RUNTIME;


/**
 * A scoping annotation to permit objects whose lifetime should
 * conform to the life of the application to be memorized in the
 * correct component.
 */
@Scope
@Retention(RUNTIME)
public @interface ApplicationScope {}

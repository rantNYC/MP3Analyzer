package com.projects.mp3.model;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)  
@Target(ElementType.FIELD)  
public @interface AudioFileAnnotation {
	String value() default "Unknown";
}

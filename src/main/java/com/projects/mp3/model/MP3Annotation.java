package com.projects.mp3.model;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)  
@Target(ElementType.FIELD)  
public @interface MP3Annotation {
	String value() default "Unknown";
}

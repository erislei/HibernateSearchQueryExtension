package de.hotware.hibernate.query.intelligent.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface Query {
	
	public static final String DEFAULT_PROFILE = "DEFAULT_PROFILE";
	
	Must[] must() default @Must;
	
	Should[] should() default @Should;
	
	MustNot[] mustNot() default @MustNot;
	
	String profile() default DEFAULT_PROFILE;

}

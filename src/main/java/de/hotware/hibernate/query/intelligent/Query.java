package de.hotware.hibernate.query.intelligent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface Query {
	
	Must[] must() default @Must;
	
	Should[] should() default @Should;
	
	MustNot[] mustNot() default @MustNot;

}

package yuzunyannn.elementalsorcery.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Config {

	String kind() default "global";

	String group() default "";

	/** 如果返回为"#"则表示要自动注入到注解的Bean中 */
	String name() default "";

	String note() default "";

	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface NumberRange {
		double max();

		double min();
	}

}

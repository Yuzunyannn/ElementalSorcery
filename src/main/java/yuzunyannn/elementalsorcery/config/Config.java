package yuzunyannn.elementalsorcery.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Config {

	String kind() default "global";

	/** 不写的话，默认类型 */
	String group() default "";

	/** 如果返回为"#"则表示要自动注入到注解的Bean中 */
	String name() default "";

	String note() default "";

	/**
	 * 同步字段，在进服务器时，会将所有同步字段的内容发送到客户端<br/>
	 * 如果仅在服务端使用，则不需要进行同步
	 */
	boolean sync() default false;

	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface NumberRange {
		double max();

		double min();
	}

}

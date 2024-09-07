package yuzunyannn.elementalsorcery.tile.device;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DeviceFeature {

	public static final int AUTHORITY_ROOT = 2;
	public static final int AUTHORITY_WARDEN = 1;
	public static final int AUTHORITY_USER = 0;

	String[] id() default {};

	int authority() default AUTHORITY_USER;
}

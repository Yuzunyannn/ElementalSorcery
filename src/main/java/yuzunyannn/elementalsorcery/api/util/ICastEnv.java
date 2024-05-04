package yuzunyannn.elementalsorcery.api.util;

import net.minecraft.world.World;

public interface ICastEnv {

	static public final ICastEnv EMPTY = new ICastEnv() {
	};

	default World getWorld() {
		return null;
	}

	default <T> T find(String hint, Class<T> cls) {
		return null;
	}

}

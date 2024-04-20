package yuzunyannn.elementalsorcery.api.util;

import net.minecraft.world.World;

public interface ICastEnv {

	static public final ICastEnv EMPTY = new ICastEnv() {
	};

	default World getWorld() {
		return null;
	}

}

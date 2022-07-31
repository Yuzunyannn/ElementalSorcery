package yuzunyannn.elementalsorcery.mods;

import net.minecraftforge.fml.common.Loader;

public class Mods {

	public static final String IC2 = "ic2";
	public static final String AE2 = "appliedenergistics2";
	public static final String RedstoneFlux = "redstoneflux";

	public static boolean isLoaded(String id) {
		return Loader.isModLoaded(id);
	}
}

package yuzunyannn.elementalsorcery.init;

import net.minecraft.util.ResourceLocation;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.grimoire.mantra.Mantra;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraEnderTeleport;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraFireBall;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraFloat;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraSprint;

public class MantraRegister {

	private static void reg(Mantra m, String name) {
		Mantra.REGISTRY.register(m.setRegistryName(new ResourceLocation(ElementalSorcery.MODID, name)));
	}

	public static void registerAll() {
		reg(new MantraEnderTeleport(), "ender_teleport");
		reg(new MantraFloat(), "float");
		reg(new MantraSprint(), "sprint");
		reg(new MantraFireBall(), "fire_ball");
	}
}

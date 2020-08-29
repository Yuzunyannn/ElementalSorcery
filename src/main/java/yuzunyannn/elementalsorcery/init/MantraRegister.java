package yuzunyannn.elementalsorcery.init;

import net.minecraft.util.ResourceLocation;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.grimoire.Mantra;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraEnderTeleport;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraFloat;

public class MantraRegister extends ESImplRegister<Mantra> {

	public static final MantraRegister instance = new MantraRegister();

	@Override
	public Class<Mantra> getRegistrySuperType() {
		return Mantra.class;
	}

	private static void reg(Mantra m, String name) {
		instance.register(m.setRegistryName(new ResourceLocation(ElementalSorcery.MODID, name)));
	}

	public static void registerAll() {
		reg(new Mantra(), "TEST");
		reg(new MantraEnderTeleport(), "ender_teleport");
		reg(new MantraFloat(), "float");
	}
}

package yuzunyannn.elementalsorcery.init;

import net.minecraft.util.ResourceLocation;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.entity.fcube.FCMDestoryBlock;
import yuzunyannn.elementalsorcery.entity.fcube.FairyCubeModule;

public class FairyCubeModuleRegister {

	private static void register(String id, Class<? extends FairyCubeModule> cls) {
		FairyCubeModule.REGISTRY.register(new ResourceLocation(ElementalSorcery.MODID, id), cls);
	}

	static public void registerAll() {
		register("destory_block", FCMDestoryBlock.class);
	}
}

package yuzunyannn.elementalsorcery.init;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.entity.fcube.FCMAttack;
import yuzunyannn.elementalsorcery.entity.fcube.FCMAttackCritical;
import yuzunyannn.elementalsorcery.entity.fcube.FCMAttackRange;
import yuzunyannn.elementalsorcery.entity.fcube.FCMDestoryBlock;
import yuzunyannn.elementalsorcery.entity.fcube.FCMEnderChest;
import yuzunyannn.elementalsorcery.entity.fcube.FCMExpUp;
import yuzunyannn.elementalsorcery.entity.fcube.FCMFarm;
import yuzunyannn.elementalsorcery.entity.fcube.FCMFortune;
import yuzunyannn.elementalsorcery.entity.fcube.FCMHeal;
import yuzunyannn.elementalsorcery.entity.fcube.FCMLightweight;
import yuzunyannn.elementalsorcery.entity.fcube.FCMPlaceBlock;
import yuzunyannn.elementalsorcery.entity.fcube.FCMPlunder;
import yuzunyannn.elementalsorcery.entity.fcube.FCMSilk;
import yuzunyannn.elementalsorcery.entity.fcube.FairyCubeModule;
import yuzunyannn.elementalsorcery.entity.fcube.IFairyCubeModuleClient;
import yuzunyannn.elementalsorcery.util.TextHelper;

public class FairyCubeModuleRegister {

	private static void register(String id, Class<? extends FairyCubeModule> cls) {
		FairyCubeModule.REGISTRY.register(new ResourceLocation(ElementalSorcery.MODID, id), cls);
	}

	static public void registerAll() {
		register("destory_block", FCMDestoryBlock.class);
		register("attr_silk", FCMSilk.class);
		register("fortune", FCMFortune.class);
		register("place_block", FCMPlaceBlock.class);
		register("heal", FCMHeal.class);
		register("lightweight", FCMLightweight.class);
		register("attack", FCMAttack.class);
		register("plunder", FCMPlunder.class);
		register("attack_range", FCMAttackRange.class);
		register("attack_critical", FCMAttackCritical.class);
		register("exp_up", FCMExpUp.class);
		register("ender_chest", FCMEnderChest.class);
		register("farm", FCMFarm.class);
	}

	@SideOnly(Side.CLIENT)
	private static void register(String id, IFairyCubeModuleClient render) {
		IFairyCubeModuleClient.HANDLER.put(new ResourceLocation(ElementalSorcery.MODID, id), render);
	}

	@SideOnly(Side.CLIENT)
	private static void register(String id, int x, int y) {
		String name = TextHelper.castToCamel(id);
		register(id, new IFairyCubeModuleClient.FairyCubeModuleDeafultRender(x * 32, 224 - y * 32, name));
	}

	@SideOnly(Side.CLIENT)
	private static void register(String id, int x, int y, String customName) {
		register(id, new IFairyCubeModuleClient.FairyCubeModuleDeafultRender(x * 32, 224 - y * 32, customName) {
			@Override
			public String getDiplayName() {
				return I18n.format(unlocalizedName);
			}
		});
	}

	@SideOnly(Side.CLIENT)
	static public void registerAllRender() {
		register("destory_block", 0, 0);
		register("attr_silk", 1, 0);
		register("fortune", 2, 0);
		register("place_block", 0, 1);
		register("heal", 1, 1);
		register("lightweight", 2, 1);
		register("attack", 3, 0);
		register("plunder", 4, 0);
		register("attack_range", 5, 0);
		register("attack_critical", 6, 0);
		register("exp_up", 7, 0);
		register("ender_chest", 7, 1, "tile.enderChest.name");
		register("farm", 3, 1);
	}
}

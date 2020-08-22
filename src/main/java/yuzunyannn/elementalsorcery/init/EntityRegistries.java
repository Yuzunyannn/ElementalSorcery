package yuzunyannn.elementalsorcery.init;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.entity.EntityBlockThrowEffect;
import yuzunyannn.elementalsorcery.entity.EntityCrafting;
import yuzunyannn.elementalsorcery.entity.EntityExploreDust;
import yuzunyannn.elementalsorcery.entity.EntityPortal;
import yuzunyannn.elementalsorcery.entity.EntityResonantCrystal;
import yuzunyannn.elementalsorcery.entity.elf.EntityElf;
import yuzunyannn.elementalsorcery.render.entity.EntityRenderFactory;
import yuzunyannn.elementalsorcery.render.entity.RenderBlockThrowEffect;
import yuzunyannn.elementalsorcery.render.entity.RenderEntityCrafting;
import yuzunyannn.elementalsorcery.render.entity.RenderEntityElf;
import yuzunyannn.elementalsorcery.render.entity.RenderEntityExploreDust;
import yuzunyannn.elementalsorcery.render.entity.RenderEntityPortal;

public class EntityRegistries {

	public static void registerAll() {
		// 生物
		register(0, "elf", EntityElf.class, "Elf", 64, 3, true);
		registerEgg("elf", 0x82bf71, 0x529b3d);
		// 效果处理
		register(51, "block_effect", EntityBlockThrowEffect.class, "BlockEffect", 128, 1, true);
		register(52, "entity_crafting", EntityCrafting.class, "EntityCrafting", 128, 1, false);
		register(53, "portal", EntityPortal.class, "EntityPortal", 64, 1, false);
		register(54, "explore_dust", EntityExploreDust.class, "EntityPortal", 64, 1, false);
		// 投掷
		register(41, "resonant_crystal", EntityResonantCrystal.class, "ResonantCrystal", 64, 10, true);
	}

	private static void register(int id, String registryName, Class<? extends Entity> entityClass, String name,
			int trackingRange, int updateFrequency, boolean sendsVelocityUpdates) {
		EntityRegistry.registerModEntity(new ResourceLocation(ElementalSorcery.MODID, registryName), entityClass, name,
				id, ElementalSorcery.instance, trackingRange, updateFrequency, sendsVelocityUpdates);
	}

	private static void registerEgg(String registryName, int eggPrimary, int eggSecondary) {
		EntityRegistry.registerEgg(new ResourceLocation(ElementalSorcery.MODID, registryName), eggPrimary,
				eggSecondary);
	}

	@SideOnly(Side.CLIENT)
	public static void registerAllRender() {
		registerRender(EntityBlockThrowEffect.class, RenderBlockThrowEffect.class);
		registerRender(EntityCrafting.class, RenderEntityCrafting.class);
		registerRender(EntityPortal.class, RenderEntityPortal.class);
		registerRender(EntityResonantCrystal.class, new EntityResonantCrystal.Factory());
		registerRender(EntityElf.class, RenderEntityElf.class);
		registerRender(EntityExploreDust.class, RenderEntityExploreDust.class);
	}

	@SideOnly(Side.CLIENT)
	private static <T extends Entity> void registerRender(Class<T> entityClass, Class<? extends Render<T>> render) {
		RenderingRegistry.registerEntityRenderingHandler(entityClass, new EntityRenderFactory<T>(render));
	}

	@SideOnly(Side.CLIENT)
	private static <T extends Entity> void registerRender(Class<T> entityClass, IRenderFactory<? super T> factory) {
		RenderingRegistry.registerEntityRenderingHandler(entityClass, factory);
	}

}

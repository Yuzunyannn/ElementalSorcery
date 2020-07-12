package yuzunyannn.elementalsorcery.init;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.entity.EntityBlockThrowEffect;
import yuzunyannn.elementalsorcery.entity.EntityCrafting;
import yuzunyannn.elementalsorcery.entity.EntityParticleEffect;
import yuzunyannn.elementalsorcery.entity.elf.EntityElf;
import yuzunyannn.elementalsorcery.render.entity.EntityRenderFactory;
import yuzunyannn.elementalsorcery.render.entity.RenderBlockThrowEffect;
import yuzunyannn.elementalsorcery.render.entity.RenderEntityCrafting;
import yuzunyannn.elementalsorcery.render.entity.RenderEntityElf;

public class EntityRegistries {
	private static int nextID = 0;

	public static void registerAll() {
		// 效果
		register("particle_effect", EntityParticleEffect.class, "ParticleEffect", 128, 4, false);
		register("block_effect", EntityBlockThrowEffect.class, "BlockEffect", 128, 1, true);
		register("entity_crafting", EntityCrafting.class, "EntityCrafting", 128, 1, false);
		// 生物
		register("elf", EntityElf.class, "Elf", 64, 3, true);
		registerEgg("elf", 0x82bf71, 0x529b3d);
	}

	private static void register(String registryName, Class<? extends Entity> entityClass, String name,
			int trackingRange, int updateFrequency, boolean sendsVelocityUpdates) {
		EntityRegistry.registerModEntity(new ResourceLocation(ElementalSorcery.MODID, registryName), entityClass, name,
				nextID++, ElementalSorcery.instance, trackingRange, updateFrequency, sendsVelocityUpdates);
	}

	private static void registerEgg(String registryName, int eggPrimary, int eggSecondary) {
		EntityRegistry.registerEgg(new ResourceLocation(ElementalSorcery.MODID, registryName), eggPrimary,
				eggSecondary);
	}

	@SideOnly(Side.CLIENT)
	public static void registerAllRender() {
		registerRender(EntityBlockThrowEffect.class, RenderBlockThrowEffect.class);
		registerRender(EntityCrafting.class, RenderEntityCrafting.class);
		registerRender(EntityElf.class, RenderEntityElf.class);
	}

	@SideOnly(Side.CLIENT)
	private static <T extends Entity> void registerRender(Class<T> entityClass, Class<? extends Render<T>> render) {
		RenderingRegistry.registerEntityRenderingHandler(entityClass, new EntityRenderFactory<T>(render));
	}

}

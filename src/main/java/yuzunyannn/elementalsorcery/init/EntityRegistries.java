package yuzunyannn.elementalsorcery.init;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Biomes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.entity.EntityBlockThrowEffect;
import yuzunyannn.elementalsorcery.entity.EntityBulletin;
import yuzunyannn.elementalsorcery.entity.EntityCrafting;
import yuzunyannn.elementalsorcery.entity.EntityExploreDust;
import yuzunyannn.elementalsorcery.entity.EntityGrimoire;
import yuzunyannn.elementalsorcery.entity.EntityPortal;
import yuzunyannn.elementalsorcery.entity.EntityResonantCrystal;
import yuzunyannn.elementalsorcery.entity.elf.EntityElf;
import yuzunyannn.elementalsorcery.entity.elf.EntityElfTravelling;
import yuzunyannn.elementalsorcery.render.entity.EntityRenderFactory;
import yuzunyannn.elementalsorcery.render.entity.RenderBlockThrowEffect;
import yuzunyannn.elementalsorcery.render.entity.RenderEntitiyBulletin;
import yuzunyannn.elementalsorcery.render.entity.RenderEntityCrafting;
import yuzunyannn.elementalsorcery.render.entity.RenderEntityElf;
import yuzunyannn.elementalsorcery.render.entity.RenderEntityExploreDust;
import yuzunyannn.elementalsorcery.render.entity.RenderEntityGrimoire;
import yuzunyannn.elementalsorcery.render.entity.RenderEntityPortal;

public class EntityRegistries {

	public static void registerAll() {
		// 生物
		register(0, "elf", EntityElf.class, "Elf", 64, 3, true);
		registerEgg("elf", 0x82bf71, 0x529b3d);
		register(1, "elfTraveler", EntityElfTravelling.class, "Elf", 64, 3, true);
		registerEntitySpawn(EntityElfTravelling.class, 5, 1, 2, EnumCreatureType.CREATURE, Biomes.PLAINS, Biomes.DESERT,
				Biomes.FOREST, Biomes.BIRCH_FOREST, Biomes.EXTREME_HILLS, Biomes.SWAMPLAND, Biomes.HELL);
		registerEntitySpawn(EntityElfTravelling.class, 10, 2, 3, EnumCreatureType.CREATURE, Biomes.PLAINS);
		// 效果处理
		register(51, "block_effect", EntityBlockThrowEffect.class, "BlockEffect", 128, 1, true);
		register(52, "entity_crafting", EntityCrafting.class, "EntityCrafting", 128, 1, false);
		register(53, "portal", EntityPortal.class, "EntityPortal", 64, 1, false);
		register(54, "explore_dust", EntityExploreDust.class, "EntityPortal", 64, 1, false);
		register(55, "entity_grimoire", EntityGrimoire.class, "EntityGrimoire", 64, 1, false);
		// 投掷
		register(41, "resonant_crystal", EntityResonantCrystal.class, "ResonantCrystal", 64, 10, true);
		// 实体方块
		register(20, "bulletin", EntityBulletin.class, "Bulletin", 64, 1, false);
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

	private static void registerEntitySpawn(Class<? extends EntityLiving> entityClass, int spawnWeight, int min,
			int max, EnumCreatureType typeOfCreature, Biome... biomes) {
		EntityRegistry.addSpawn(entityClass, spawnWeight, min, max, typeOfCreature, biomes);
	}

	@SideOnly(Side.CLIENT)
	public static void registerAllRender() {
		registerRender(EntityBlockThrowEffect.class, RenderBlockThrowEffect.class);
		registerRender(EntityCrafting.class, RenderEntityCrafting.class);
		registerRender(EntityPortal.class, RenderEntityPortal.class);
		registerRender(EntityResonantCrystal.class, new EntityResonantCrystal.Factory());
		registerRender(EntityElf.class, RenderEntityElf.class);
		registerRender(EntityExploreDust.class, RenderEntityExploreDust.class);
		registerRender(EntityGrimoire.class, RenderEntityGrimoire.class);
		registerRender(EntityBulletin.class, RenderEntitiyBulletin.class);
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

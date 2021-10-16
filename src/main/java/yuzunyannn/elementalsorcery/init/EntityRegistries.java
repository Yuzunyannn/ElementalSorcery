package yuzunyannn.elementalsorcery.init;

import java.util.Map.Entry;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.config.Config;
import yuzunyannn.elementalsorcery.config.WorldGenAndSpawnConfig;
import yuzunyannn.elementalsorcery.entity.EntityBlockMove;
import yuzunyannn.elementalsorcery.entity.EntityBlockThrowEffect;
import yuzunyannn.elementalsorcery.entity.EntityBulletin;
import yuzunyannn.elementalsorcery.entity.EntityCrafting;
import yuzunyannn.elementalsorcery.entity.EntityExploreDust;
import yuzunyannn.elementalsorcery.entity.EntityFallingElfFruit;
import yuzunyannn.elementalsorcery.entity.EntityGrimoire;
import yuzunyannn.elementalsorcery.entity.EntityMagicMelting;
import yuzunyannn.elementalsorcery.entity.EntityPortal;
import yuzunyannn.elementalsorcery.entity.EntityScapegoat;
import yuzunyannn.elementalsorcery.entity.EntityThrow;
import yuzunyannn.elementalsorcery.entity.elf.EntityElf;
import yuzunyannn.elementalsorcery.entity.elf.EntityElfTravelling;
import yuzunyannn.elementalsorcery.entity.fcube.EntityFairyCube;
import yuzunyannn.elementalsorcery.entity.mob.EntityDejectedSkeleton;
import yuzunyannn.elementalsorcery.entity.mob.EntityDreadCube;
import yuzunyannn.elementalsorcery.entity.mob.EntityRabidRabbit;
import yuzunyannn.elementalsorcery.entity.mob.EntityRelicZombie;
import yuzunyannn.elementalsorcery.render.entity.EntityRenderFactory;
import yuzunyannn.elementalsorcery.render.entity.RenderBlockThrowEffect;
import yuzunyannn.elementalsorcery.render.entity.RenderEntitiyBulletin;
import yuzunyannn.elementalsorcery.render.entity.RenderEntityBlockMove;
import yuzunyannn.elementalsorcery.render.entity.RenderEntityCrafting;
import yuzunyannn.elementalsorcery.render.entity.RenderEntityDejectedSkeleton;
import yuzunyannn.elementalsorcery.render.entity.RenderEntityDreadCube;
import yuzunyannn.elementalsorcery.render.entity.RenderEntityElf;
import yuzunyannn.elementalsorcery.render.entity.RenderEntityExploreDust;
import yuzunyannn.elementalsorcery.render.entity.RenderEntityFairyCube;
import yuzunyannn.elementalsorcery.render.entity.RenderEntityFallingElfFruit;
import yuzunyannn.elementalsorcery.render.entity.RenderEntityGrimoire;
import yuzunyannn.elementalsorcery.render.entity.RenderEntityMagicMelting;
import yuzunyannn.elementalsorcery.render.entity.RenderEntityPortal;
import yuzunyannn.elementalsorcery.render.entity.RenderEntityRabidRabbit;
import yuzunyannn.elementalsorcery.render.entity.RenderEntityRelicZombie;
import yuzunyannn.elementalsorcery.render.entity.RenderEntityScapegoat;
import yuzunyannn.elementalsorcery.render.entity.RenderThrow;

public class EntityRegistries {

	@Config(kind = "spawn_and_gen", group = "elf", name = "#")
	public static WorldGenAndSpawnConfig CONFIG_ELF = new WorldGenAndSpawnConfig(null,
			new String[] { "plains", "desert", "hell", "forest", "birch_forest", "extreme_hills", "swampland" }, null,
			new int[] { 8 });

	public static void registerAll() {
		// 生物
		register(0, "elf", EntityElf.class, "Elf", 64, 3, true);
		registerEgg("elf", 0x82bf71, 0x529b3d);
		register(1, "elfTraveler", EntityElfTravelling.class, "Elf2", 64, 3, true);
		Entry<Biome, Integer>[] entries = CONFIG_ELF.getAllBiomes();
		for (Entry<Biome, Integer> entry : entries) {
			int num = entry.getValue();
			int add = 0;
			if (num > 0) add = (int) MathHelper.sqrt(num) / 2;
			Biome biome = entry.getKey();
			registerEntitySpawn(EntityElfTravelling.class, 3 + num, 1, 1 + add, EnumCreatureType.CREATURE, biome);
		}
		register(2, "relicZombie", EntityRelicZombie.class, "RelicZombie", 64, 3, true);
		registerEgg("relicZombie", 0x00a3a3, 0x529b3d);
		register(3, "rabidRabbit", EntityRabidRabbit.class, "RabidRabbit", 64, 3, true);
		registerEgg("rabidRabbit", 0xffd1d9, 0xde225b);
		register(4, "dreadCube", EntityDreadCube.class, "DreadCube", 64, 10, true);
		registerEgg("dreadCube", 0x161616, 0x6a1212);
		register(5, "dejectedSkeleton", EntityDejectedSkeleton.class, "DejectedSkeleton", 64, 3, true);
		registerEgg("dejectedSkeleton", 0xe8e8e8, 0x9c9c9c);

		// 实体方块
		register(20, "bulletin", EntityBulletin.class, "Bulletin", 64, 1, false);
		register(21, "scapegoat", EntityScapegoat.class, "Scapegoat", 64, 1, false);
		register(22, "fairy_cube", EntityFairyCube.class, "FairyCube", 64, 3, false);

		// 效果处理
		register(40, "throw", EntityThrow.class, "Throw", 64, 10, true);
		register(41, "falling_elf_fruit", EntityFallingElfFruit.class, "Falling", 64, 1, true);

		register(50, "block_effect", EntityBlockThrowEffect.class, "BlockEffect", 128, 1, true);
		register(51, "block_move", EntityBlockMove.class, "EntityBlockMove", 128, 1, false);
		register(52, "entity_crafting", EntityCrafting.class, "EntityCrafting", 128, 1, false);
		register(53, "portal", EntityPortal.class, "EntityPortal", 64, 1, false);
		register(54, "explore_dust", EntityExploreDust.class, "EntityPortal", 64, 1, false);
		register(55, "entity_grimoire", EntityGrimoire.class, "EntityGrimoire", 64, 1, false);
		register(56, "magic_melting", EntityMagicMelting.class, "EntityMagicMelting", 64, 1, false);

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
		registerRender(EntityThrow.class, RenderThrow.class);
		registerRender(EntityElf.class, RenderEntityElf.class);
		registerRender(EntityRelicZombie.class, RenderEntityRelicZombie.class);
		registerRender(EntityExploreDust.class, RenderEntityExploreDust.class);
		registerRender(EntityGrimoire.class, RenderEntityGrimoire.class);
		registerRender(EntityBulletin.class, RenderEntitiyBulletin.class);
		registerRender(EntityMagicMelting.class, RenderEntityMagicMelting.class);
		registerRender(EntityBlockMove.class, RenderEntityBlockMove.class);
		registerRender(EntityScapegoat.class, RenderEntityScapegoat.class);
		registerRender(EntityFairyCube.class, RenderEntityFairyCube.class);
		registerRender(EntityRabidRabbit.class, RenderEntityRabidRabbit.class);
		registerRender(EntityDreadCube.class, RenderEntityDreadCube.class);
		registerRender(EntityDejectedSkeleton.class, RenderEntityDejectedSkeleton.class);
		registerRender(EntityFallingElfFruit.class, RenderEntityFallingElfFruit.class);
	}

	@SideOnly(Side.CLIENT)
	private static <T extends Entity> void registerRender(Class<T> entityClass,
			Class<? extends Render<? super T>> render) {
		RenderingRegistry.registerEntityRenderingHandler(entityClass, new EntityRenderFactory<T>(render));
	}

	@SideOnly(Side.CLIENT)
	private static <T extends Entity> void registerRender(Class<T> entityClass, IRenderFactory<? super T> factory) {
		RenderingRegistry.registerEntityRenderingHandler(entityClass, factory);
	}

}

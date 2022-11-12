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
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.DataSerializerEntry;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.config.Config;
import yuzunyannn.elementalsorcery.config.WorldGenAndSpawnConfig;
import yuzunyannn.elementalsorcery.entity.EntityAutoMantra;
import yuzunyannn.elementalsorcery.entity.EntityBlockMove;
import yuzunyannn.elementalsorcery.entity.EntityBlockThrowEffect;
import yuzunyannn.elementalsorcery.entity.EntityBulletin;
import yuzunyannn.elementalsorcery.entity.EntityCrafting;
import yuzunyannn.elementalsorcery.entity.EntityExploreDust;
import yuzunyannn.elementalsorcery.entity.EntityFallingElfFruit;
import yuzunyannn.elementalsorcery.entity.EntityGrimoire;
import yuzunyannn.elementalsorcery.entity.EntityItemGoods;
import yuzunyannn.elementalsorcery.entity.EntityMagicMelting;
import yuzunyannn.elementalsorcery.entity.EntityPortal;
import yuzunyannn.elementalsorcery.entity.EntityRotaryWindmillBlate;
import yuzunyannn.elementalsorcery.entity.EntityScapegoat;
import yuzunyannn.elementalsorcery.entity.EntityThrow;
import yuzunyannn.elementalsorcery.entity.elf.EntityElf;
import yuzunyannn.elementalsorcery.entity.elf.EntityElfTravelling;
import yuzunyannn.elementalsorcery.entity.fcube.EntityFairyCube;
import yuzunyannn.elementalsorcery.entity.mob.EntityArrogantSheep;
import yuzunyannn.elementalsorcery.entity.mob.EntityDejectedSkeleton;
import yuzunyannn.elementalsorcery.entity.mob.EntityDreadCube;
import yuzunyannn.elementalsorcery.entity.mob.EntityPuppet;
import yuzunyannn.elementalsorcery.entity.mob.EntityRabidRabbit;
import yuzunyannn.elementalsorcery.entity.mob.EntityRelicGuard;
import yuzunyannn.elementalsorcery.entity.mob.EntityRelicZombie;
import yuzunyannn.elementalsorcery.render.entity.EntityRenderFactory;
import yuzunyannn.elementalsorcery.render.entity.RenderBlockThrowEffect;
import yuzunyannn.elementalsorcery.render.entity.RenderEntitiyBulletin;
import yuzunyannn.elementalsorcery.render.entity.RenderEntityBlockMove;
import yuzunyannn.elementalsorcery.render.entity.RenderEntityCrafting;
import yuzunyannn.elementalsorcery.render.entity.RenderEntityExploreDust;
import yuzunyannn.elementalsorcery.render.entity.RenderEntityFairyCube;
import yuzunyannn.elementalsorcery.render.entity.RenderEntityFallingElfFruit;
import yuzunyannn.elementalsorcery.render.entity.RenderEntityItemGoods;
import yuzunyannn.elementalsorcery.render.entity.RenderEntityMagicMelting;
import yuzunyannn.elementalsorcery.render.entity.RenderEntityNothing;
import yuzunyannn.elementalsorcery.render.entity.RenderEntityPortal;
import yuzunyannn.elementalsorcery.render.entity.RenderEntityRotaryWindmillBlate;
import yuzunyannn.elementalsorcery.render.entity.RenderEntityScapegoat;
import yuzunyannn.elementalsorcery.render.entity.RenderEntityThrow;
import yuzunyannn.elementalsorcery.render.entity.living.RenderEntityArrogantSheep;
import yuzunyannn.elementalsorcery.render.entity.living.RenderEntityDejectedSkeleton;
import yuzunyannn.elementalsorcery.render.entity.living.RenderEntityDreadCube;
import yuzunyannn.elementalsorcery.render.entity.living.RenderEntityElf;
import yuzunyannn.elementalsorcery.render.entity.living.RenderEntityPuppet;
import yuzunyannn.elementalsorcery.render.entity.living.RenderEntityRabidRabbit;
import yuzunyannn.elementalsorcery.render.entity.living.RenderEntityRelicGuard;
import yuzunyannn.elementalsorcery.render.entity.living.RenderEntityRelicZombie;
import yuzunyannn.elementalsorcery.util.helper.EntityHelper;

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
			registerEntitySpawn(EntityElfTravelling.class, 2 + num, 1, 1 + add, EnumCreatureType.CREATURE, biome);
		}
		register(2, "relicZombie", EntityRelicZombie.class, "RelicZombie", 64, 3, true);
		registerEgg("relicZombie", 0x00a3a3, 0x529b3d);
		register(3, "rabidRabbit", EntityRabidRabbit.class, "RabidRabbit", 64, 3, true);
		registerEgg("rabidRabbit", 0xffd1d9, 0xde225b);
		register(4, "dreadCube", EntityDreadCube.class, "DreadCube", 64, 10, true);
		registerEgg("dreadCube", 0x161616, 0x6a1212);
		register(5, "dejectedSkeleton", EntityDejectedSkeleton.class, "DejectedSkeleton", 64, 3, true);
		registerEgg("dejectedSkeleton", 0xe8e8e8, 0x9c9c9c);
		register(6, "arrogantSheep", EntityArrogantSheep.class, "ArrogantSheep", 64, 3, true);
		registerEgg("arrogantSheep", 0xfedf0d, 0xea8900);
		register(7, "puppet", EntityPuppet.class, "Puppet", 64, 3, true);
//		registerEgg("puppet", 0x4fb037, 0x146700);
		register(8, "relicGuard", EntityRelicGuard.class, "RelicGuard", 64, 3, true);
		registerEgg("relicGuard", 0xcbccef, 0x865334);

		// 实体方块
		register(20, "bulletin", EntityBulletin.class, "Bulletin", 64, 20, false);
		register(21, "scapegoat", EntityScapegoat.class, "Scapegoat", 64, 20, false);
		register(22, "fairy_cube", EntityFairyCube.class, "FairyCube", 64, 3, false);

		// 效果处理
		register(40, "throw", EntityThrow.class, "Throw", 64, 1, true);
		register(41, "falling_elf_fruit", EntityFallingElfFruit.class, "Falling", 64, 2, true);
		register(42, "rotary_windmill_blate", EntityRotaryWindmillBlate.class, "WindmillBlate", 64, 2, false);
		register(43, "item_goods", EntityItemGoods.class, "ItemGoods", 64, 2, true);

		register(50, "block_effect", EntityBlockThrowEffect.class, "BlockEffect", 128, 1, true);
		register(51, "block_move", EntityBlockMove.class, "EntityBlockMove", 128, 1, false);
		register(52, "entity_crafting", EntityCrafting.class, "EntityCrafting", 128, 1, false);
		register(53, "portal", EntityPortal.class, "EntityPortal", 64, 20, false);
		register(54, "explore_dust", EntityExploreDust.class, "ExploreDust", 64, 20, false);
		register(55, "entity_grimoire", EntityGrimoire.class, "EntityGrimoire", 128, 3, false);
		register(56, "magic_melting", EntityMagicMelting.class, "EntityMagicMelting", 64, 20, false);
		register(57, "auto_mantra", EntityAutoMantra.class, "AutoMantra", 128, 3, false);

		ForgeRegistries.DATA_SERIALIZERS
				.register(new DataSerializerEntry(EntityHelper.DS_INT).setRegistryName(ESAPI.MODID, "int"));
	}

	private static void register(int id, String registryName, Class<? extends Entity> entityClass, String name,
			int trackingRange, int updateFrequency, boolean sendsVelocityUpdates) {
		EntityRegistry.registerModEntity(new ResourceLocation(ESAPI.MODID, registryName), entityClass, name, id,
				ElementalSorcery.instance, trackingRange, updateFrequency, sendsVelocityUpdates);
	}

	private static void registerEgg(String registryName, int eggPrimary, int eggSecondary) {
		EntityRegistry.registerEgg(new ResourceLocation(ESAPI.MODID, registryName), eggPrimary, eggSecondary);
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
		registerRender(EntityThrow.class, RenderEntityThrow.class);
		registerRender(EntityElf.class, RenderEntityElf.class);
		registerRender(EntityRelicZombie.class, RenderEntityRelicZombie.class);
		registerRender(EntityPuppet.class, RenderEntityPuppet.class);
		registerRender(EntityExploreDust.class, RenderEntityExploreDust.class);
		registerRender(EntityGrimoire.class, RenderEntityNothing.class);
		registerRender(EntityAutoMantra.class, RenderEntityNothing.class);
		registerRender(EntityBulletin.class, RenderEntitiyBulletin.class);
		registerRender(EntityMagicMelting.class, RenderEntityMagicMelting.class);
		registerRender(EntityBlockMove.class, RenderEntityBlockMove.class);
		registerRender(EntityScapegoat.class, RenderEntityScapegoat.class);
		registerRender(EntityFairyCube.class, RenderEntityFairyCube.class);
		registerRender(EntityRabidRabbit.class, RenderEntityRabidRabbit.class);
		registerRender(EntityDreadCube.class, RenderEntityDreadCube.class);
		registerRender(EntityDejectedSkeleton.class, RenderEntityDejectedSkeleton.class);
		registerRender(EntityFallingElfFruit.class, RenderEntityFallingElfFruit.class);
		registerRender(EntityRotaryWindmillBlate.class, RenderEntityRotaryWindmillBlate.class);
		registerRender(EntityArrogantSheep.class, RenderEntityArrogantSheep.class);
		registerRender(EntityItemGoods.class, RenderEntityItemGoods.class);
		registerRender(EntityRelicGuard.class, RenderEntityRelicGuard.class);

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

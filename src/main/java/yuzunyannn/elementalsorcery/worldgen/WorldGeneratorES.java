package yuzunyannn.elementalsorcery.worldgen;

import java.util.Random;

import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.terraingen.TerrainGen;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.config.ESConfigGenAndSpawnGetter;

public class WorldGeneratorES {
	// 生成矿
	static public class GenOre {

		private BlockPos pos;

		@SubscribeEvent
		public void onOreGenPost(OreGenEvent.Post event) {
			// 山地会调用两次
			if (event.getPos().equals(this.pos)) return;
			this.pos = event.getPos();
			this.genKynateOre(event);
		}

		static void gen(World world, BlockPos pos, Random rand, WorldGenerator generator,
				ESConfigGenAndSpawnGetter config) {
			Biome biome = world.getBiome(pos);
			if (!config.canSpawn(world, biome)) return;
			if (!TerrainGen.generateOre(world, rand, generator, pos, OreGenEvent.GenerateMinable.EventType.CUSTOM))
				return;
			generator.generate(world, rand, pos);
		}

		private void gen(OreGenEvent.Post event, WorldGenerator generator, ESConfigGenAndSpawnGetter config) {
			GenOre.gen(event.getWorld(), event.getPos(), event.getRand(), generator, config);
		}

		public void genKynateOre(OreGenEvent.Post event) {
			gen(event, new WorldGenKyaniteOre(), ElementalSorcery.config.SPAWN.GEN_KYNATE);
		}
	}

	// 生成装饰
	static public class GenDecorate {

		private BlockPos pos;

		@SubscribeEvent
		public void onDecorateGen(DecorateBiomeEvent.Post event) {
			BlockPos pos = event.getChunkPos().getBlock(0, 0, 0);
			if (pos.equals(this.pos)) return; // 防止重复调用两次
			this.pos = pos;
			this.genElfTree(event);
			this.genStarStone(event);
			this.genSealStone(event);
		}

		// 生成精灵树
		public void genElfTree(DecorateBiomeEvent.Post event) {
			World world = event.getWorld();
			ChunkPos chunkPos = event.getChunkPos();
			Random rand = event.getRand();
			Biome biome = world.getBiome(chunkPos.getBlock(0, 0, 0));
			if (!ElementalSorcery.config.SPAWN.GEN_ELF_TREE.canSpawn(world, biome)) return;
			if (!net.minecraftforge.event.terraingen.TerrainGen.decorate(world, rand, chunkPos,
					DecorateBiomeEvent.Decorate.EventType.CUSTOM))
				return;
			WorldGenerator generator;
			int expect = ElementalSorcery.config.SPAWN.GEN_ELF_TREE.getSpawnPoint(world, biome);
			if (biome == Biomes.PLAINS) {
				if (rand.nextInt(Math.max(1, 5 - expect)) != 0) return;
			} else if (rand.nextInt(Math.max(1, 10 - expect * 2)) != 0) return;
			generator = WorldGenElfTree.getGenTreeFromBiome(false, biome);
			int x = rand.nextInt(16) + 8;
			int z = rand.nextInt(16) + 8;
			BlockPos blockpos = world.getHeight(chunkPos.getBlock(x, 0, z));
			generator.generate(world, rand, blockpos);
		}

		private void gen(DecorateBiomeEvent.Post event, WorldGenerator generator, ESConfigGenAndSpawnGetter config) {
			BlockPos pos = event.getChunkPos().getBlock(0, 0, 0);
			GenOre.gen(event.getWorld(), pos.add(8, 0, 8), event.getRand(), generator, config);
		}

		public void genStarStone(DecorateBiomeEvent.Post event) {
			gen(event, new WorldGenStarStone(), ElementalSorcery.config.SPAWN.GEN_START_STONE);
		}

		public void genSealStone(DecorateBiomeEvent.Post event) {
			gen(event, new WorldGenSealStone(), ElementalSorcery.config.SPAWN.GEN_SEAL_STONE);
		}

	}

	// 刷图用
	@Deprecated
	public static class GenTerrain {
		@SubscribeEvent
		public void onPopulate(PopulateChunkEvent.Populate event) {
		}
	}
}

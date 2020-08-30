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

public class WorldGeneratorES {
	// 生成矿
	static public class genOre {

		private BlockPos pos;

		@SubscribeEvent
		public void onOreGenPost(OreGenEvent.Post event) {
			// 山地会调用两次
			if (!event.getPos().equals(this.pos)) {
				this.pos = event.getPos();
				this.genKynateOre(event);
				this.genStarStone(event);
			}
		}

		public void genKynateOre(OreGenEvent.Post event) {
			WorldGenerator generator = new WorldGenKyaniteOre();
			if (TerrainGen.generateOre(event.getWorld(), event.getRand(), generator, event.getPos(),
					OreGenEvent.GenerateMinable.EventType.CUSTOM))
				generator.generate(event.getWorld(), event.getRand(), event.getPos());
		}

		public void genStarStone(OreGenEvent.Post event) {
			WorldGenerator generator = new WorldGenStarStone();
			if (TerrainGen.generateOre(event.getWorld(), event.getRand(), generator, event.getPos(),
					OreGenEvent.GenerateMinable.EventType.CUSTOM))
				generator.generate(event.getWorld(), event.getRand(), event.getPos());
		}
	}

	// 生成装饰
	static public class genDecorate {
		@SubscribeEvent
		public void onDecorateGen(DecorateBiomeEvent.Post event) {
			World world = event.getWorld();
			ChunkPos chunkPos = event.getChunkPos();
			Random rand = event.getRand();
			this.genElfTree(world, chunkPos, rand, world.getBiome(chunkPos.getBlock(0, 0, 0)));
		}

		public void genElfTree(World world, ChunkPos chunkPos, Random rand, Biome biome) {
			if (net.minecraftforge.event.terraingen.TerrainGen.decorate(world, rand, chunkPos,
					DecorateBiomeEvent.Decorate.EventType.CUSTOM)) {
				WorldGenerator generator;
				if (biome == Biomes.PLAINS) {
					if (rand.nextInt(5) != 0) return;
				} else if (rand.nextInt(10) != 0) return;
				if (rand.nextInt(50) == 0) generator = new WorldGenElfTree(false, 3);
				else generator = WorldGenElfTree.getGenTreeFromBiome(false, biome);
				int x = rand.nextInt(16) + 8;
				int z = rand.nextInt(16) + 8;
				BlockPos blockpos = world.getHeight(chunkPos.getBlock(x, 0, z));
				generator.generate(world, rand, blockpos);
			}
		}
	}

	// 生成填充
	static public class genPopulate {
		@SubscribeEvent
		public void onPopulateGen(PopulateChunkEvent.Post event) {
			
		}

	}
}

package yuzunyannn.elementalsorcery.worldgen;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType;
import net.minecraftforge.event.terraingen.TerrainGen;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import yuzunyannn.elementalsorcery.elf.pro.ElfProfession;
import yuzunyannn.elementalsorcery.entity.elf.EntityElf;

public class WorldGeneratorES {
	// 生成矿
	static public class GenOre {

		private BlockPos pos;

		@SubscribeEvent
		public void onOreGenPost(OreGenEvent.Post event) {
			// 山地会调用两次
			if (!event.getPos().equals(this.pos)) {
				this.pos = event.getPos();
				this.genKynateOre(event);
				this.genStarStone(event);
				this.genSealStone(event);
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

		public void genSealStone(OreGenEvent.Post event) {
			WorldGenerator generator = new WorldGenSealStone();
			if (TerrainGen.generateOre(event.getWorld(), event.getRand(), generator, event.getPos(),
					OreGenEvent.GenerateMinable.EventType.CUSTOM))
				generator.generate(event.getWorld(), event.getRand(), event.getPos());
		}
	}

	// 生成装饰
	static public class GenDecorate {
		@SubscribeEvent
		public void onDecorateGen(DecorateBiomeEvent.Post event) {
			World world = event.getWorld();
			ChunkPos chunkPos = event.getChunkPos();
			Random rand = event.getRand();
			this.genElfTree(world, chunkPos, rand, world.getBiome(chunkPos.getBlock(0, 0, 0)));
		}

		// 生成精灵树
		public void genElfTree(World world, ChunkPos chunkPos, Random rand, Biome biome) {
			if (net.minecraftforge.event.terraingen.TerrainGen.decorate(world, rand, chunkPos,
					DecorateBiomeEvent.Decorate.EventType.CUSTOM)) {
				WorldGenerator generator;
				if (biome == Biomes.PLAINS) {
					if (rand.nextInt(5) != 0) return;
				} else if (rand.nextInt(10) != 0) return;
				/*
				 * if (rand.nextInt(50) == 0) generator = new WorldGenElfTree(false, 3); else
				 */ generator = WorldGenElfTree.getGenTreeFromBiome(false, biome);
				int x = rand.nextInt(16) + 8;
				int z = rand.nextInt(16) + 8;
				BlockPos blockpos = world.getHeight(chunkPos.getBlock(x, 0, z));
				generator.generate(world, rand, blockpos);
			}
		}

	}

	// 刷图用
	@Deprecated
	public static class GenTerrain {
		@SubscribeEvent
		public void onPopulate(PopulateChunkEvent.Populate event) {
			World world = event.getWorld();
			int chunkX = event.getChunkX();
			int chunkZ = event.getChunkX();
			if (event.getType() == EventType.ANIMALS) genElfM(world, new ChunkPos(chunkX, chunkZ), event.getRand());
		}

		public void genElfM(World world, ChunkPos chunkPos, Random rand) {
			if (rand.nextFloat() > 0.1) return;
			int x = chunkPos.getXStart();
			int y = chunkPos.getZStart();
			BlockPos pos = new BlockPos(x, 1, y);
			Biome biome = world.getBiome(pos);
			if (biome == Biomes.OCEAN || biome == Biomes.BEACH) return;
			pos = findCenterGroundPos(world, chunkPos);
			Block block = world.getBlockState(pos).getBlock();
			if (block != Blocks.GRASS && block != Blocks.SAND) return;
			pos = pos.up();
			EntityElf elf = new EntityElf(world, ElfProfession.MERCHANT);
			elf.setPosition(pos.getX(), pos.getY(), pos.getZ());
			world.spawnEntity(elf);
		}

		// 寻找区块重点可生长的位置
		public BlockPos findCenterGroundPos(World world, ChunkPos chunkPos) {
			int x = chunkPos.getXStart();
			int z = chunkPos.getZStart();
			BlockPos pos = new BlockPos(x + (chunkPos.getXEnd() - x) / 2, world.provider.getHeight(),
					z + (chunkPos.getZEnd() - z) / 2);
			do {
				pos = pos.down();
			} while (world.isAirBlock(pos));
			return pos;
		}
	}
}

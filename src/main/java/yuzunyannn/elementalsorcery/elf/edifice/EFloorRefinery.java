package yuzunyannn.elementalsorcery.elf.edifice;

import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.elf.pro.ElfProfession;
import yuzunyannn.elementalsorcery.elf.pro.ElfProfessionIronSmith;
import yuzunyannn.elementalsorcery.entity.elf.EntityElf;
import yuzunyannn.elementalsorcery.entity.elf.EntityElfBase;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.tile.TileElfTreeCore;

public class EFloorRefinery extends ElfEdificeFloor {

	@Override
	public NBTTagCompound createBuildData(IBuilder builder, Random rand) {
		BuilderHelper help = new BuilderHelper(builder).toward(rand);
		return help.startRand(rand).getNBT();
	}

	@Override
	public int getFloorHeight(IBuilder builder) {
		return 6;
	}

	@Override
	public int getInvestWeight() {
		return 35;
	}

	@Override
	public int getMaxCountInTree(TileElfTreeCore core) {
		return 2;
	}

	protected int furnaceHalfSize = 2;

	@Override
	public void build(IBuilder builder) {
		BlockPos pos = builder.getFloorBasicPos();
		int treeSize = builder.getEdificeSize();
		BuilderHelper helper = new BuilderHelper(builder);
		EnumFacing toward = helper.toward();
		EnumFacing towardRY = toward.rotateY();
		int high = this.getFloorHeight(builder);
		// 地毯
		helper.genCarpet(EnumDyeColor.RED);
		IBlockState STONE = helper.blockStone(0);
		IBlockState STONE2 = helper.blockStone(10);
		IBlockState STAR_SAND = ESInit.BLOCKS.STAR_SAND.getDefaultState();
		IBlockState HOPPER = Blocks.HOPPER.getDefaultState();
		IBlockState CHEST = Blocks.CHEST.getDefaultState().withProperty(BlockChest.FACING, toward.getOpposite());
		IBlockState IRON_BARS = Blocks.IRON_BARS.getDefaultState();
		IBlockState LAVA = Blocks.LAVA.getDefaultState();
		// 炉子
		{
			int size = GenElfEdifice.getFakeCircleLen(treeSize, 0, 2);
			BlockPos cPos = pos.offset(toward, size - 4);
			size = furnaceHalfSize;
			for (int x = -size; x <= size; x++) {
				for (int z = -size; z <= size; z++) {
					if (Math.abs(x) == size || Math.abs(z) == size) {
						for (int y = 0; y < 2; y++) {
							if (helper.randNextInt(100) >= 80) builder.setBlockState(cPos.add(x, y, z), STONE2);
							else builder.setBlockState(cPos.add(x, y, z), STONE);
						}
						for (int y = 2; y < 4; y++) builder.setBlockState(cPos.add(x, y, z), IRON_BARS);
						builder.setBlockState(cPos.add(x, 4, z), STONE);
					} else {
						builder.setBlockState(cPos.add(x, 0, z), STAR_SAND);
						builder.setBlockState(cPos.add(x, 1, z), LAVA);
					}
				}
			}
			// 第一层
			{
				BlockPos at = cPos.offset(towardRY, size);
				builder.setBlockState(at, HOPPER.withProperty(BlockHopper.FACING, towardRY));
				for (int i = 1; i <= 2; i++) builder.setBlockState(at.offset(towardRY, i), CHEST);
				at = cPos.offset(towardRY, -size);
				builder.setBlockState(at, HOPPER.withProperty(BlockHopper.FACING, towardRY.getOpposite()));
				for (int i = 1; i <= 2; i++) builder.setBlockState(at.offset(towardRY.getOpposite(), i), CHEST);
			}
			for (int i = -1; i <= 1; i++) {
				BlockPos at = cPos.offset(toward, -size - 1);
				builder.setBlockState(at.offset(towardRY, i), STONE);
			}
			// 第二层
			{
				BlockPos at = cPos.offset(toward, -size).up(2);
				builder.setBlockState(at, Blocks.AIR.getDefaultState());
			}
		}
		// 头上的灯
		{
			int size = GenElfEdifice.getFakeCircleLen(treeSize, 0, 2);
			for (EnumFacing facing : EnumFacing.HORIZONTALS) {
				if (facing == toward) continue;
				for (int i = 0; i < size; i++) {
					if (i % 5 != 0) continue;
					BlockPos at = pos.up(high - 1).offset(facing, i);
					helper.genLamp(at, 1);
				}
			}
		}
		// 一些东西
		IBlockState NETHERRACK = Blocks.NETHERRACK.getDefaultState();
		IBlockState ASTONE = ESInit.BLOCKS.ASTONE.getDefaultState();
		IBlockState MELT_CAULDRON = ESInit.BLOCKS.MELT_CAULDRON.getDefaultState();
		IBlockState IRON_ORE = Blocks.IRON_ORE.getDefaultState();
		IBlockState GOLD_ORE = Blocks.GOLD_ORE.getDefaultState();
		IBlockState KYANITE_ORE = ESInit.BLOCKS.KYANITE_ORE.getDefaultState();
		IBlockState HEARTH = ESInit.BLOCKS.HEARTH.getDefaultState();
		IBlockState SMELT_BOX = ESInit.BLOCKS.SMELT_BOX.getDefaultState();
		IBlockState STONE_MILL = ESInit.BLOCKS.STONE_MILL.getDefaultState();
		{
			int size = GenElfEdifice.getFakeCircleLen(treeSize, 0, 2);
			BlockPos aPos = pos.offset(towardRY, size - 4).offset(toward, -size / 3);
			BlockPos bPos = pos.offset(towardRY, size - 2);
			BlockPos cPos = pos.offset(towardRY, size - 4).offset(toward, size / 3);
			int times = helper.randNextInt(5) + 2;
			for (int i = 0; i < times; i++) {
				buildSame(helper, cPos, 5, 3, NETHERRACK, null, 0);
				// 一些astone
				buildSame(helper, aPos, 5, 3, ASTONE, MELT_CAULDRON, 5);
				// 一些矿
				buildSame(helper, bPos, 3, 2, IRON_ORE, null, 0);
				buildSame(helper, aPos, 3, 2, KYANITE_ORE, null, 0);
				buildSame(helper, bPos, 2, 2, GOLD_ORE, null, 0);
			}
			aPos = pos.offset(toward, -size + 3);
			times = helper.randNextInt(3) + 1;
			for (int i = 0; i < times; i++) {
				buildSame(helper, aPos, 0, 100, HEARTH, SMELT_BOX, 2);
				buildSame(helper, aPos, 0, 100, STONE_MILL, null, 2);
			}
			times = helper.randNextInt(5) + 2;
			for (int i = 0; i < times; i++) {
				buildSame(helper, aPos, 5, 3, STAR_SAND, null, 2);
			}
		}
		// 任务箱
		IBlockState WOOD = helper.blockPlank();
		{
			int size = GenElfEdifice.getFakeCircleLen(treeSize, 0, 2) - 3;
			BlockPos cPos = pos.offset(towardRY.getOpposite(), size);
			for (int y = 0; y < 3; y++) {
				builder.setBlockState(cPos.up(y), WOOD);
				for (int i = 1; i <= 2; i++) {
					BlockPos at = cPos.offset(toward, i).up(y);
					builder.setBlockState(at, CHEST.withProperty(BlockChest.FACING, towardRY));
					at = cPos.offset(toward, -i).up(y);
					builder.setBlockState(at, CHEST.withProperty(BlockChest.FACING, towardRY));
				}
			}

		}
	}

	private void buildSame(BuilderHelper helper, BlockPos pos, int high, int highP, IBlockState state,
			IBlockState moreBetter, int betterP) {
		IBuilder builder = helper.builder;
		int x = helper.randNextInt(5) - 2;
		int z = helper.randNextInt(5) - 2;
		BlockPos at = pos.add(x, 0, z);
		if (builder.trySetBlockState(at, state)) {
			if (moreBetter != null) {
				if (helper.randNextInt(betterP) == 0) {
					builder.trySetBlockState(at.up(), moreBetter);
					return;
				}
			}
			for (int j = 1; j < high; j++) {
				if (helper.randNextInt(highP) == 0) break;
				at = pos.add(x, j, z);
				builder.trySetBlockState(at, state);
			}
		}
	}

	@Override
	public void surprise(IBuilder builder, Random rand) {
	}

	@Override
	public void spawn(IBuilder builder) {
		BuilderHelper helper = new BuilderHelper(builder);
		TileElfTreeCore core = helper.treeCore();
		if (core == null) return;
		EnumFacing toward = helper.toward();
		BlockPos pos = builder.getFloorBasicPos();
		World world = builder.getWorld();
		int treeSize = builder.getEdificeSize();
		// 刷精灵
		double x = pos.getX() + 0.5;
		double y = pos.getY();
		double z = pos.getZ() + 0.5;
		List<EntityElfBase> elfs = EFloorHall.getFloorElf(builder, ElfProfession.IRONSMITH);
		if (elfs.size() < 2) {
			x = world.rand.nextInt(treeSize * 2) - treeSize + x;
			z = world.rand.nextInt(treeSize * 2) - treeSize + z;
			if (EFloorHall.canSpawnElf(world, new BlockPos(x, y, z))) {
				EntityElf elf = new EntityElf(world, ElfProfession.IRONSMITH);
				elf.setPosition(x, y, z);
				builder.spawn(elf);
				// 设置位置
				ElfProfession pro = elf.getProfession();
				if (!(pro instanceof ElfProfessionIronSmith)) return;
				ElfProfessionIronSmith ironSmith = (ElfProfessionIronSmith) pro;
				int size = GenElfEdifice.getFakeCircleLen(treeSize, 0, 2);
				BlockPos furnacePos = pos.offset(toward, size - 4).up();
				BlockPos standPos = furnacePos.offset(toward, -furnaceHalfSize - 1);
				BlockPos chestPos = pos.offset(toward.rotateY().getOpposite(), size - 3);
				ironSmith.setChestPos(elf, chestPos);
				ironSmith.setFurnacePos(elf, furnacePos, standPos);
			}
		}
	}
}

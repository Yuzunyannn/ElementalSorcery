package yuzunyannn.elementalsorcery.elf.edifice;

import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.elf.pro.ElfProfession;
import yuzunyannn.elementalsorcery.entity.elf.EntityElfBase;
import yuzunyannn.elementalsorcery.tile.TileElfTreeCore;

public class EFloorPostOffice extends ElfEdificeFloor {

	@Override
	public NBTTagCompound createBuildData(IBuilder builder, Random rand) {
		return new NBTTagCompound();
	}

	@Override
	public int getFloorHeight(IBuilder builder) {
		return 4;
	}

	@Override
	public int getInvestWeight() {
		return 50;
	}

	@Override
	public int getMaxCountInTree(TileElfTreeCore core) {
		return 1;
	}

	@Override
	public void build(IBuilder builder) {
		BlockPos pos = builder.getFloorBasicPos();
		int treeSize = builder.getEdificeSize();
		BuilderHelper helper = new BuilderHelper(builder);
		int high = this.getFloorHeight(builder);
		// 地毯
		helper.genCarpet(EnumDyeColor.GREEN);
		IBlockState PLANK = helper.blockPlank();
		IBlockState GLASS = Blocks.STAINED_GLASS.getDefaultState().withProperty(BlockStainedGlass.COLOR,
				EnumDyeColor.GRAY);
		{
			// 中心台子
			int size = 4;
			for (int y = 0; y < 3; y++) {
				for (int i = -size + 1; i < size; i++) {
					int n = GenElfEdifice.getFakeCircleLen(size, i, 2);
					IBlockState state = PLANK;
					if (y == 1) {
						if (Math.abs(i) <= 1) {
							if (i == 0) continue;
							else state = GLASS;
						}
					} else if (y == 2) {
						if (i % 2 != 0) state = PLANK;
						else state = GLASS;
					}

					builder.setBlockState(pos.add(i, y, n), state);
					builder.setBlockState(pos.add(i, y, -n), state);
					builder.setBlockState(pos.add(n, y, i), state);
					builder.setBlockState(pos.add(-n, y, i), state);
				}
			}
			// 吊灯
			int n = GenElfEdifice.getFakeCircleLen(size, 0, 2);
			helper.genLamp(pos.add(n, high - 1, 0), 0);
			helper.genLamp(pos.add(-n, high - 1, 0), 0);
			helper.genLamp(pos.add(0, high - 1, n), 0);
			helper.genLamp(pos.add(0, high - 1, -n), 0);
		}
		// 后面一排树叶
		{
			IBlockState LEAF = ESObjects.BLOCKS.ELF_LEAF.getDefaultState().withProperty(BlockLeaves.DECAYABLE, false);
			IBlockState GLOWSTONE = Blocks.GLOWSTONE.getDefaultState();
			int size = treeSize;
			for (int y = 0; y < 1; y++) {
				for (int i = -size + 1; i < size; i++) {
					int n = GenElfEdifice.getFakeCircleLen(size, i, 2) - 1;
					IBlockState state = LEAF;
					int test = Math.abs(i);
					if (test == size - 1 || test == 0) state = GLOWSTONE;
					builder.setBlockState(pos.add(i, y, n), state);
					builder.setBlockState(pos.add(i, y, -n), state);
					builder.setBlockState(pos.add(n, y, i), state);
					builder.setBlockState(pos.add(-n, y, i), state);
				}
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
		// 接待员
		List<EntityElfBase> elfs = EFloorHall.getFloorElf(builder, ElfProfession.POST_RECEPTIONIST);
		if (elfs.size() < 4) {
			BlockPos pos = builder.getFloorBasicPos();
			int size = 4;
			for (int y = 0; y < 3; y++) {
				int n = GenElfEdifice.getFakeCircleLen(size, 0, 2) + 1;
				EFloorHall.trySpawnElfAt(builder, ElfProfession.POST_RECEPTIONIST, pos.add(0, y, n));
				EFloorHall.trySpawnElfAt(builder, ElfProfession.POST_RECEPTIONIST, pos.add(0, y, -n));
				EFloorHall.trySpawnElfAt(builder, ElfProfession.POST_RECEPTIONIST, pos.add(n, y, 0));
				EFloorHall.trySpawnElfAt(builder, ElfProfession.POST_RECEPTIONIST, pos.add(-n, y, 0));

			}
		}
		World world = core.getWorld();
		this.trySpawnQuest(builder, 24000 * 2 + world.rand.nextInt(24000 * 2));
	}
}

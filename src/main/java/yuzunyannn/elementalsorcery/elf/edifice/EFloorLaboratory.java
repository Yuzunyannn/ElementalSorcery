package yuzunyannn.elementalsorcery.elf.edifice;

import java.util.Random;

import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockQuartz;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.tile.IGetItemStack;
import yuzunyannn.elementalsorcery.block.BlockCrystalFlower;
import yuzunyannn.elementalsorcery.block.BlocksEStone.EStone;
import yuzunyannn.elementalsorcery.block.container.BlockMagicPlatform;
import yuzunyannn.elementalsorcery.init.ESInitInstance;
import yuzunyannn.elementalsorcery.tile.TileCrystalFlower;
import yuzunyannn.elementalsorcery.tile.TileElfTreeCore;
import yuzunyannn.elementalsorcery.util.block.BlockHelper;

public class EFloorLaboratory extends ElfEdificeFloor {

	@Override
	public NBTTagCompound getBuildData(IBuilder builder, Random rand) {
		BuilderHelper help = new BuilderHelper(builder).toward(rand);
		return help.startRand(rand).getNBT();
	}

	@Override
	public int getFloorHeight(IBuilder builder) {
		return 5;
	}

	@Override
	public int getInvestWeight() {
		return 75;
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
		EnumFacing toward = helper.toward();
		EnumFacing towardRY = toward.rotateY();
		int high = this.getFloorHeight(builder);
		// 地毯
		helper.genCarpet(EnumDyeColor.CYAN);
		IBlockState ESTONE_Y = ESInitInstance.BLOCKS.ESTONE.getDefaultState().withProperty(EStone.VARIANT,
				BlockQuartz.EnumType.LINES_Y);
		IBlockState GLOWSTONE = Blocks.GLOWSTONE.getDefaultState();
		IBlockState LEAF = ESInitInstance.BLOCKS.ELF_LEAF.getDefaultState().withProperty(BlockLeaves.DECAYABLE, false);
		{
			// 石英
			int size = treeSize;
			for (int y = 0; y < high; y++) {
				for (int i = -size + 1; i < size; i++) {
					int n = GenElfEdifice.getFakeCircleLen(size, i, 2) - 1;
					IBlockState state = ESTONE_Y;
					if (y == high - 1) state = GLOWSTONE;
					builder.setBlockState(pos.add(i, y, n), state);
					builder.setBlockState(pos.add(i, y, -n), state);
					builder.setBlockState(pos.add(n, y, i), state);
					builder.setBlockState(pos.add(-n, y, i), state);
				}
			}
			int y = high - 1;
			for (int i = -size + 2; i < size - 1; i++) {
				int n = GenElfEdifice.getFakeCircleLen(size, i, 2) - 2;
				builder.setBlockState(pos.add(i, y, n), LEAF);
				builder.setBlockState(pos.add(i, y, -n), LEAF);
				builder.setBlockState(pos.add(n, y, i), LEAF);
				builder.setBlockState(pos.add(-n, y, i), LEAF);
			}
		}
		IBlockState ESTONE_CHISELED = ESTONE_Y.withProperty(EStone.VARIANT, BlockQuartz.EnumType.CHISELED);
		IBlockState ESTONE_STARIS = ESInitInstance.BLOCKS.ESTONE_STAIRS.getDefaultState();
		IBlockState MAGIC_PLATFORM = ESInitInstance.BLOCKS.MAGIC_PLATFORM.getDefaultState()
				.withProperty(BlockMagicPlatform.MATERIAL, BlockMagicPlatform.EnumMaterial.ESTONE);
		// 一张桌子
		{
			int offset = treeSize - 3;
			BlockPos at;
			for (int n = 0; n < 2; n++) {
				for (int i = -1; i <= 1; i++) {
					at = pos.offset(toward, offset + n).offset(towardRY, i);
					builder.setBlockState(at, ESTONE_CHISELED);
					// 桌上随机平台
					if (helper.randNextInt(3) == 0) builder.setBlockState(at.up(), MAGIC_PLATFORM);
				}
			}
			at = pos.offset(toward, offset - 2);
			ESTONE_STARIS = ESTONE_STARIS.withProperty(BlockStairs.FACING, toward.getOpposite());
			for (int i = -1; i <= 1; i++) builder.setBlockState(at.offset(towardRY, i), ESTONE_STARIS);
			at = pos.offset(toward, offset + 3);
			ESTONE_STARIS = ESTONE_STARIS.withProperty(BlockStairs.FACING, toward);
			for (int i = -1; i <= 1; i++) builder.setBlockState(at.offset(towardRY, i), ESTONE_STARIS);
			at = pos.offset(towardRY, 3);
			ESTONE_STARIS = ESTONE_STARIS.withProperty(BlockStairs.FACING, towardRY);
			for (int i = 0; i <= 1; i++) builder.setBlockState(at.offset(toward, offset + i), ESTONE_STARIS);
			at = pos.offset(towardRY, -3);
			ESTONE_STARIS = ESTONE_STARIS.withProperty(BlockStairs.FACING, towardRY.getOpposite());
			for (int i = 0; i <= 1; i++) builder.setBlockState(at.offset(toward, offset + i), ESTONE_STARIS);
		}
		// 意义不明的台子
		{
			int offset = treeSize - 3;
			BlockPos at = pos.offset(toward.getOpposite(), offset);
			for (int i = -2; i <= 2; i++) {
				builder.setBlockState(at.offset(towardRY, i), ESTONE_CHISELED);
				// 随机点好东西
				IBlockState GOOD = null;
				switch (helper.randNextInt(16)) {
				case 0:
					GOOD = ESInitInstance.BLOCKS.ELEMENT_CRAFTING_TABLE.getDefaultState();
					break;
				case 1:
					GOOD = ESInitInstance.BLOCKS.DECONSTRUCT_ALTAR_TABLE.getDefaultState();
					break;
				case 2:
					GOOD = ESInitInstance.BLOCKS.MAGIC_DESK.getDefaultState();
					break;
				default:
					break;
				}
				if (GOOD != null) builder.setBlockState(at.offset(towardRY, i).up(), GOOD);
			}
			for (int y = 0; y < 3; y++) {
				builder.setBlockState(at.offset(towardRY, 3).up(y), ESTONE_Y);
				builder.setBlockState(at.offset(towardRY, -3).up(y), ESTONE_Y);
			}
			builder.setBlockState(at.offset(towardRY, 3).up(3), ESTONE_CHISELED);
			builder.setBlockState(at.offset(towardRY, -3).up(3), ESTONE_CHISELED);
		}
		// 花房
		{
			int offset = treeSize - 1;
			IBlockState LIFE_DIRT = ESInitInstance.BLOCKS.LIFE_DIRT.getDefaultState();
			for (int i = -2; i <= 2; i++) {
				for (int j = -2; j <= 2; j++) {
					BlockPos at = pos.offset(towardRY, i + offset).offset(toward, j);
					boolean a = Math.abs(i) == 2, b = Math.abs(j) == 2;
					if (a && b) continue;
					if (a || b) builder.trySetBlockState(at, ESTONE_CHISELED);
					else builder.setBlockState(at, LIFE_DIRT);
				}
			}
		}
		// 几个书架
		{
			int offset = treeSize;
			IBlockState BOOKSHELF = Blocks.BOOKSHELF.getDefaultState();
			for (int i = -1; i <= 1; i++) {
				for (int j = -3; j <= 3; j++) {
					if (helper.randNextInt(2) != 0) continue;
					BlockPos at = pos.offset(towardRY.getOpposite(), i + offset).offset(toward, j);
					for (int y = 0; y < helper.randNextInt(3) + 1; y++) builder.trySetBlockState(at.up(y), BOOKSHELF);
				}
			}
		}
	}

	@Override
	public void surprise(IBuilder builder, Random rand) {
		BlockPos pos = builder.getFloorBasicPos();
		int treeSize = builder.getEdificeSize();
		BuilderHelper helper = new BuilderHelper(builder);
		EnumFacing toward = helper.toward();
		EnumFacing towardRY = toward.rotateY();
		World world = builder.getWorld();
		// 桌子上随机物品
		{
			int offset = treeSize - 3;
			BlockPos at;
			for (int n = 0; n < 2; n++) {
				for (int i = -1; i <= 1; i++) {
					at = pos.offset(toward, offset + n).offset(towardRY, i).up();
					IGetItemStack getter = BlockHelper.getTileEntity(world, at, IGetItemStack.class);
					if (getter == null) continue;
					ItemStack goodItem = ItemStack.EMPTY;
					switch (rand.nextInt(10)) {
					case 0:
						goodItem = new ItemStack(ESInitInstance.ITEMS.SPELLBOOK);
						break;
					case 1:
						goodItem = new ItemStack(ESInitInstance.ITEMS.SPELLBOOK_ENCHANTMENT);
						break;
					case 2:
						goodItem = new ItemStack(ESInitInstance.BLOCKS.ELEMENTAL_CUBE);
						break;
					case 3:
						goodItem = new ItemStack(ESInitInstance.ITEMS.ORDER_CRYSTAL);
						break;
					case 4:
						goodItem = new ItemStack(ESInitInstance.ITEMS.MAGIC_GOLD_SWORD);
						break;
					case 5:
						goodItem = new ItemStack(ESInitInstance.BLOCKS.ESTONE_PRISM);
						break;
					default:
						goodItem = new ItemStack(ESInitInstance.ITEMS.MAGIC_CRYSTAL);
						break;
					}
					getter.setStack(goodItem);
				}
			}
		}
		// 随机的水晶花
		{
			IBlockState CRYSTAL_FLOWER = ESInitInstance.BLOCKS.CRYSTAL_FLOWER.getDefaultState();
			CRYSTAL_FLOWER = CRYSTAL_FLOWER.withProperty(BlockCrystalFlower.STAGE, BlockCrystalFlower.MAX_STAGE);
			int offset = treeSize - 1;
			for (int i = -1; i <= 1; i++) {
				for (int j = -1; j <= 1; j++) {
					BlockPos at = pos.offset(towardRY, i + offset).offset(toward, j).up();
					world.setBlockState(at, CRYSTAL_FLOWER);
					TileCrystalFlower flower = BlockHelper.getTileEntity(world, at, TileCrystalFlower.class);
					if (flower == null) continue;
					ItemStack goodItem = ItemStack.EMPTY;
					switch (rand.nextInt(10)) {
					case 0:
						goodItem = new ItemStack(Items.DIAMOND);
						break;
					case 1:
						goodItem = new ItemStack(Items.GOLD_INGOT, rand.nextInt(5) + 1);
						break;
					case 2:
						goodItem = new ItemStack(ESInitInstance.ITEMS.ELEMENT_CRYSTAL);
						break;
					case 3:
						goodItem = new ItemStack(Items.REDSTONE, rand.nextInt(10) + 3);
						break;
					case 4:
						goodItem = new ItemStack(ESInitInstance.ITEMS.MAGIC_GOLD, 3);
						break;
					case 5:
						goodItem = new ItemStack(Items.CAKE);
						break;
					default:
						goodItem = new ItemStack(ESInitInstance.ITEMS.MAGIC_CRYSTAL);
						break;
					}
					flower.setCrystal(goodItem);
				}
			}
		}
	}

	@Override
	public void spawn(IBuilder builder) {
		BuilderHelper helper = new BuilderHelper(builder);
		TileElfTreeCore core = helper.treeCore();
		if (core == null) return;
	}
}

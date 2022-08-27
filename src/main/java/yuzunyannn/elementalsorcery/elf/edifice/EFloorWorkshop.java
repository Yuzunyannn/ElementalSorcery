package yuzunyannn.elementalsorcery.elf.edifice;

import java.util.Random;

import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockLog;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.block.container.BlockSmeltBox;
import yuzunyannn.elementalsorcery.elf.ElfChamberOfCommerce;
import yuzunyannn.elementalsorcery.elf.pro.ElfProfession;
import yuzunyannn.elementalsorcery.item.ItemElfPurse;
import yuzunyannn.elementalsorcery.tile.TileElfTreeCore;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;

public class EFloorWorkshop extends ElfEdificeFloor {

	public static final EFloorWorkshop instance = new EFloorWorkshop();

	@Override
	public int getInvestWeight() {
		return 20;
	}

	@Override
	public int getMaxCountInTree(TileElfTreeCore core) {
		return 2;
	}

	@Override
	public int getFloorHeight(IBuilder builder) {
		return 4;
	}

	@Override
	public NBTTagCompound createBuildData(IBuilder builder, Random rand) {
		return new BuilderHelper(builder).toward(rand).getNBT();
	}

	@Override
	public void build(IBuilder builder) {
		BlockPos pos = builder.getFloorBasicPos();
		int treeSize = builder.getEdificeSize();
		BuilderHelper helper = new BuilderHelper(builder);
		EnumFacing toward = helper.toward();
		int high = this.getFloorHeight(builder);
		// 地毯
		helper.genCarpet(EnumDyeColor.CYAN);
		for (EnumFacing facing : EnumFacing.HORIZONTALS) {
			// 灯
			BlockPos cPos = pos.offset(facing, treeSize - 1);
			int size = GenElfEdifice.getFakeCircleLen(treeSize, treeSize - 1, 2) - 1;
			helper.genRedstoneLamp(cPos.offset(facing.rotateY(), size));
			// 吊灯
			size = 3;
			cPos = pos.offset(facing, size).up(high - 1);
			helper.genLamp(cPos.offset(facing.rotateY(), size), 0);
		}
		// 右侧灯
		{
			int size = GenElfEdifice.getFakeCircleLen(treeSize, 0, 2) - 1;
			BlockPos cPos = pos.offset(toward.rotateY(), size);
			helper.genLeafLamp(cPos);
		}
		// 左手木墩
		IBlockState LOG = Blocks.LOG.getDefaultState();
		{
			int size = treeSize - 1;
			BlockPos cPos = pos.offset(toward.rotateY(), size);
			IBlockState XLOG = LOG.withProperty(BlockLog.LOG_AXIS, BlockLog.EnumAxis.fromFacingAxis(toward.getAxis()));
			size = GenElfEdifice.getFakeCircleLen(treeSize, size, 2) - 3;
			for (int i = 0; i <= size; i++) {
				if (i < 2) for (int y = 0; y <= 2 - i; y++)
					builder.setBlockState(cPos.offset(toward, i).offset(toward.rotateY(), -1).up(y), LOG);
				if (i < 3) builder.setBlockState(cPos.offset(toward, i).up(1), XLOG);
				builder.setBlockState(cPos.offset(toward, i), XLOG);
			}
		}
		// 一排工作台
		IBlockState CRAFTING_TABLE = Blocks.CRAFTING_TABLE.getDefaultState();
		{
			int size = GenElfEdifice.getFakeCircleLen(treeSize, treeSize + 1, 2) - 1;
			for (int i = -size; i <= size; i++) {
				BlockPos cPos = pos.offset(toward, treeSize + 1);
				builder.setBlockState(cPos.offset(toward.rotateY(), i), CRAFTING_TABLE);
			}
		}
		// 前方的玻璃
		IBlockState GLASS = Blocks.GLASS.getDefaultState();
		{
			int size = GenElfEdifice.getFakeCircleLen(treeSize, 0, 2);
			BlockPos cPos = pos.offset(toward, size);
			size = GenElfEdifice.getFakeCircleLen(treeSize, size - 1, 2);
			for (int i = -size; i <= size; i++)
				for (int y = 0; y < high; y++) builder.setBlockState(cPos.offset(toward.rotateY(), i).up(y), GLASS);
		}
		// 左侧墙
		IBlockState CEHST = Blocks.CHEST.getDefaultState().withProperty(BlockChest.FACING, toward.rotateY());
		IBlockState PLANK = helper.blockPlank();
		{
			int size = GenElfEdifice.getFakeCircleLen(treeSize, treeSize - 1, 2) - 2;
			BlockPos cPos = pos.offset(toward.rotateY().getOpposite(), treeSize - 1);
			for (int i = -size; i <= size; i++) {
				for (int y = 0; y < high; y++) builder.setBlockState(cPos.offset(toward, i).up(y), PLANK);
				if (i % 2 == 0) continue;
				builder.setBlockState(cPos.offset(toward, i).offset(toward.rotateY(), 1), CEHST);
			}
		}
		// 一排灶台
		IBlockState HEARTH = ESObjects.BLOCKS.HEARTH.getDefaultState();
		IBlockState SMELT_BOX = ESObjects.BLOCKS.SMELT_BOX.getDefaultState().withProperty(BlockSmeltBox.FACING, toward);
		{
			int size = GenElfEdifice.getFakeCircleLen(treeSize, treeSize + 1, 2) - 1;
			for (int i = -size; i <= size; i++) {
				BlockPos cPos = pos.offset(toward, -treeSize - 1);
				builder.setBlockState(cPos.offset(toward.rotateY(), i), HEARTH);
				if (i % 2 != 0) continue;
				builder.setBlockState(cPos.offset(toward.rotateY(), i).up(), SMELT_BOX);
			}
		}
	}

	@Override
	public void surprise(IBuilder builder, Random rand) {
		BlockPos pos = builder.getFloorBasicPos();
		int treeSize = builder.getEdificeSize();
		BuilderHelper helper = new BuilderHelper(builder);
		World world = builder.getWorld();
		EnumFacing toward = helper.toward();
		{
			int size = GenElfEdifice.getFakeCircleLen(treeSize, treeSize - 1, 2) - 2;
			BlockPos cPos = pos.offset(toward.rotateY().getOpposite(), treeSize - 2);
			for (int i = -size; i <= size; i++) {
				BlockPos at = cPos.offset(toward, i);
				TileEntityChest chest = BlockHelper.getTileEntity(world, at, TileEntityChest.class);
				EFloorLivingRoom.setChestLoot(chest, rand);
				if (i % 2 != 0) continue;
				EntityItemFrame frame = new EntityItemFrame(world, at.up(2), toward.rotateY());
				world.spawnEntity(frame);
				ItemStack stack = ItemStack.EMPTY;
				switch (rand.nextInt(10)) {
				case 0:
					stack = new ItemStack(ESObjects.ITEMS.KYANITE_PICKAXE);
					break;
				case 1:
					stack = new ItemStack(ESObjects.ITEMS.MANUAL);
					break;
				case 2:
				case 3:
					stack = new ItemStack(Items.BOOK);
					break;
				case 9:
					stack = ItemElfPurse.getPurse(rand.nextInt(100) + 10);
					break;
				default:
					for (int tryTimes = 0; tryTimes < 5; tryTimes++) {
						ItemStack item = new ItemStack(Item.REGISTRY.getRandomObject(rand));
						int price = ElfChamberOfCommerce.priceIt(item);
						if (price < 2 || price > 100) continue;
						stack = item;
						break;
					}

					break;
				}
				frame.setDisplayedItem(stack);
			}
		}
	}

	@Override
	public void spawn(IBuilder builder) {
		BuilderHelper helper = new BuilderHelper(builder);
		TileElfTreeCore core = helper.treeCore();
		if (core == null) return;
		EFloorHall.trySpawnElf(builder, ElfProfession.BUILDER, 2);
		World world = core.getWorld();
		this.trySpawnQuest(builder, 24000 * 2 + world.rand.nextInt(24000 * 2));
	}
}

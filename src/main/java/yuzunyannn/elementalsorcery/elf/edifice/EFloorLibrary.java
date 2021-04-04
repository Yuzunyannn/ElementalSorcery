package yuzunyannn.elementalsorcery.elf.edifice;

import java.util.Random;

import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.elf.pro.ElfProfession;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.tile.TileElfTreeCore;
import yuzunyannn.elementalsorcery.util.RandomHelper;

public class EFloorLibrary extends ElfEdificeFloor {

	@Override
	public NBTTagCompound createBuildData(IBuilder builder, Random rand) {
		return null;
	}

	@Override
	public int getFloorHeight(IBuilder builder) {
		return 9;
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
		int high = this.getFloorHeight(builder);
		// 地毯
		helper.genCarpet(EnumDyeColor.BROWN);
		IBlockState BOOKSHELF = Blocks.BOOKSHELF.getDefaultState();
		IBlockState PLANK = helper.blockPlank();
		IBlockState FENCE = helper.blockFence();
		IBlockState LADDER = Blocks.LADDER.getDefaultState();
		IBlockState STAIRS = helper.blockStairs();
		IBlockState GLOWSTONE = Blocks.GLOWSTONE.getDefaultState();

		// 内部书架
		int size = treeSize;
		int y;
		for (y = 0; y < high; y++) {
			for (int i = -size + 1; i < size; i++) {
				int n = GenElfEdifice.getFakeCircleLen(size, i, 2) - 1;
				IBlockState state = BOOKSHELF;
				if (y == 4) state = PLANK;
				else if (i % 6 == 0) {
					state = PLANK;
					if (y == 2 || y == 7) state = GLOWSTONE;
				}
				builder.setBlockState(pos.add(i, y, n), state);
				builder.setBlockState(pos.add(i, y, -n), state);
				builder.setBlockState(pos.add(n, y, i), state);
				builder.setBlockState(pos.add(-n, y, i), state);
			}
		}
		// 外部书架
		for (y = 0; y < 5; y++) {
			for (int i = -size + 1; i < size; i++) {
				int n = GenElfEdifice.getFakeCircleLen(size, i, 2);
				IBlockState state = BOOKSHELF;
				if (y == 4) state = PLANK;
				else if (i % 3 == 0) {
					state = PLANK;
					if (y == 2 || y == 7) state = GLOWSTONE;
				}
				builder.setBlockState(pos.add(i, y, n), state);
				builder.setBlockState(pos.add(i, y, -n), state);
				builder.setBlockState(pos.add(n, y, i), state);
				builder.setBlockState(pos.add(-n, y, i), state);
			}
		}
		// 边角装饰
		for (EnumFacing face : EnumFacing.HORIZONTALS) {
			int n = size - 1;
			STAIRS = STAIRS.withProperty(BlockStairs.FACING, face);
			builder.setBlockState(pos.offset(face, n - 2).offset(face.rotateY(), n), STAIRS);
			builder.setBlockState(pos.offset(face, n - 1).offset(face.rotateY(), n - 2), STAIRS);
			STAIRS = STAIRS.withProperty(BlockStairs.FACING, face.rotateY());
			builder.setBlockState(pos.offset(face, n).offset(face.rotateY(), n - 2), STAIRS);
			builder.setBlockState(pos.offset(face, n - 1).offset(face.rotateY(), n - 1), STAIRS);
			builder.setBlockState(pos.offset(face, n - 2).offset(face.rotateY(), n - 1), STAIRS);
			n = n - 3;
			BlockPos at = pos.offset(face, n).offset(face.rotateY(), n);
			for (EnumFacing face2 : EnumFacing.HORIZONTALS) {
				builder.setBlockState(at, PLANK);
				STAIRS = STAIRS.withProperty(BlockStairs.FACING, face2.getOpposite());
				builder.setBlockState(at.offset(face2), STAIRS);
				builder.setBlockState(at.offset(face2).offset(face2.rotateY()), STAIRS);
			}
			for (int i = 0; i < high; i++) builder.setBlockState(at.up(i), PLANK);
		}
		// 二层
		y = 4;
		int s;
		for (s = 2; s <= 3; s++) {
			for (int i = -size + s; i <= size - s; i++) {
				int n = GenElfEdifice.getFakeCircleLen(size, i, 2) - s;
				builder.setBlockState(pos.add(i, y, n), PLANK);
				builder.setBlockState(pos.add(i, y, -n), PLANK);
				builder.setBlockState(pos.add(n, y, i), PLANK);
				builder.setBlockState(pos.add(-n, y, i), PLANK);
			}
		}
		s = 1;
		for (int i = -size - s; i <= size + s; i++) {
			int n = GenElfEdifice.getFakeCircleLen(size, i, 2) + s;
			builder.setBlockState(pos.add(i, y, n), PLANK);
			builder.setBlockState(pos.add(i, y, -n), PLANK);
			builder.setBlockState(pos.add(n, y, i), PLANK);
			builder.setBlockState(pos.add(-n, y, i), PLANK);
		}
		// 灯楼梯
		helper.genLamp(pos.up(high - 1), 4);
		for (EnumFacing face : EnumFacing.HORIZONTALS) {
			helper.genLamp(pos.up(high - 2).offset(face, 2), 1);
			builder.setBlockState(pos.up(high - 2).offset(face, 1), FENCE);
			builder.setBlockState(pos.up(high - 2).offset(face, 1).offset(face.rotateY(), 1), FENCE);
			int n = GenElfEdifice.getFakeCircleLen(size, 0, 2) - 2;
			if (face.getHorizontalIndex() % 2 == 0) {
				builder.setBlockState(pos.offset(face, n), STAIRS.withProperty(BlockStairs.FACING, face));
				LADDER = LADDER.withProperty(BlockLadder.FACING, face.getOpposite());
				for (y = 1; y < 7; y++) builder.setBlockState(pos.up(y).offset(face, n), LADDER);
			} else {
				builder.setBlockState(pos.up(0).offset(face, n + 1), Blocks.AIR.getDefaultState());
				builder.setBlockState(pos.up(1).offset(face, n + 1), Blocks.AIR.getDefaultState());
				builder.setBlockState(pos.up(0).offset(face, n + 2), Blocks.AIR.getDefaultState());
				builder.setBlockState(pos.up(1).offset(face, n + 2), Blocks.AIR.getDefaultState());
			}
		}
		// 墙外
		y = -1;
		for (s = 1; s <= 3; s++) {
			for (int i = -size - s; i <= size + s; i++) {
				int n = GenElfEdifice.getFakeCircleLen(size + s, i, 2);
				builder.setBlockState(pos.add(i, y, n), PLANK);
				builder.setBlockState(pos.add(i, y, -n), PLANK);
				builder.setBlockState(pos.add(n, y, i), PLANK);
				builder.setBlockState(pos.add(-n, y, i), PLANK);
				n = n - 1;
				builder.trySetBlockState(pos.add(i, y, n), PLANK);
				builder.trySetBlockState(pos.add(i, y, -n), PLANK);
				builder.trySetBlockState(pos.add(n, y, i), PLANK);
				builder.trySetBlockState(pos.add(-n, y, i), PLANK);
			}
		}
		y = 0;
		s = 3;
		for (int i = -size - s; i <= size + s; i++) {
			int n = GenElfEdifice.getFakeCircleLen(size + s, i, 2);
			builder.setBlockState(pos.add(i, y, n), FENCE);
			builder.setBlockState(pos.add(i, y, -n), FENCE);
			builder.setBlockState(pos.add(n, y, i), FENCE);
			builder.setBlockState(pos.add(-n, y, i), FENCE);
			if ((i + 1) % 2 == 0) {
				n = n + 1;
				builder.trySetBlockState(pos.add(i, y, n), FENCE);
				builder.trySetBlockState(pos.add(i, y, -n), FENCE);
				builder.trySetBlockState(pos.add(n, y, i), FENCE);
				builder.trySetBlockState(pos.add(-n, y, i), FENCE);
				BlockPos p = pos.down();
				builder.trySetBlockState(p.add(i, y, n), PLANK);
				builder.trySetBlockState(p.add(i, y, -n), PLANK);
				builder.trySetBlockState(p.add(n, y, i), PLANK);
				builder.trySetBlockState(p.add(-n, y, i), PLANK);
			}
		}
		{
			int n = size + s;
			builder.trySetBlockState(pos.add(n, y, n), FENCE);
			builder.trySetBlockState(pos.add(n, y, -n), FENCE);
			builder.trySetBlockState(pos.add(-n, y, -n), FENCE);
			builder.trySetBlockState(pos.add(-n, y, n), FENCE);
			y = -1;
			builder.trySetBlockState(pos.add(n, y, n), PLANK);
			builder.trySetBlockState(pos.add(n, y, -n), PLANK);
			builder.trySetBlockState(pos.add(-n, y, -n), PLANK);
			builder.trySetBlockState(pos.add(-n, y, n), PLANK);
		}

	}

	@Override
	public void surprise(IBuilder builder, Random rand) {
		BlockPos pos = builder.getFloorBasicPos();
		World world = builder.getWorld();
		int treeSize = builder.getEdificeSize();
		int high = this.getFloorHeight(builder);
		int n = treeSize - 4;
		RandomHelper.WeightRandom<ItemStack> wr = new RandomHelper.WeightRandom();
		wr.add(new ItemStack(Items.BOOK), 1000);
		wr.add(new ItemStack(Items.KNOWLEDGE_BOOK), 500);
		wr.add(new ItemStack(Items.WRITABLE_BOOK), 50);
		wr.add(new ItemStack(Items.ENCHANTED_BOOK), 200);
		wr.add(new ItemStack(ESInit.ITEMS.MANUAL), 50);
		wr.add(new ItemStack(ESInit.ITEMS.RITE_MANUAL), 100);
		wr.add(new ItemStack(ESInit.ITEMS.UNSCRAMBLE_NOTE), 200);
		wr.add(new ItemStack(ESInit.ITEMS.SPELLBOOK_ENCHANTMENT), 20);
		wr.add(new ItemStack(ESInit.ITEMS.SPELLBOOK_ARCHITECTURE), 10);
		wr.add(new ItemStack(ESInit.ITEMS.SPELLBOOK_LAUNCH), 10);
		wr.add(new ItemStack(ESInit.ITEMS.SPELLBOOK_ELEMENT), 10);
		{
			// 没啥用的彩蛋
			int y = pos.getY();
			if (y > 130 - high / 2 && y <= 130 + high / 2) wr.add(new ItemStack(ESInit.ITEMS.GRIMOIRE), 1);
		}
		for (int y = 1; y < high; y++) for (EnumFacing face : EnumFacing.HORIZONTALS) {
			BlockPos at = pos.offset(face, n).offset(face.rotateY(), n).up(y);
			for (EnumFacing face2 : EnumFacing.HORIZONTALS) {
				if (rand.nextInt(3) != 0) continue;
				EntityItemFrame frame = new EntityItemFrame(world, at.offset(face2), face2);
				world.spawnEntity(frame);
				ItemStack stack = wr.get(rand).copy();
				// 知识之书，弄点合成表
				if (stack.getItem() == Items.KNOWLEDGE_BOOK) {
					NBTTagCompound nbt = new NBTTagCompound();
					NBTTagList list = new NBTTagList();
					int tryTimes = rand.nextInt(3) + 1;
					for (int i = 0; i < tryTimes; i++) {
						String r = CraftingManager.REGISTRY.getRandomObject(rand).getRegistryName().toString();
						list.appendTag(new NBTTagString(r));
					}
					stack.setTagCompound(nbt);
					nbt.setTag("Recipes", list);
				} else if (stack.getItem() == Items.ENCHANTED_BOOK) {
					Enchantment enchant = Enchantment.REGISTRY.getRandomObject(rand);
					int level = rand.nextInt(enchant.getMaxLevel()) + 1;
					// 有几率突破原有等级
					if (rand.nextInt(5) == 0) level = rand.nextInt(MathHelper.floor(enchant.getMaxLevel() * 1.5f)) + 1;
					ItemEnchantedBook.addEnchantment(stack, new EnchantmentData(enchant, level));
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
		EFloorHall.trySpawnElf(builder, ElfProfession.SCHOLAR_ADV, 2);
		World world = core.getWorld();
		this.trySpawnQuest(builder, 24000 * 2 + world.rand.nextInt(24000 * 2));
	}
}

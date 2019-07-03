package yuzunyannn.elementalsorcery.building;

import java.io.IOException;

import net.minecraft.block.BlockQuartz;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.init.ESInitInstance;

public class Buildings {

	static public Building LARGE_ALTAR;
	static public Building SPELLBOOK_ALTAR;
	static public Building ELEMENT_CRAFTING_ALTAR;
	static public Building DECONSTRUCT_ALTAR;

	static public void init() throws IOException {
		LARGE_ALTAR = new BuildingInherent(
				ElementalSorcery.data.getNBTForResourceWithException("structures/large_altar"), "largeAltar");
		SPELLBOOK_ALTAR = new BuildingInherent(
				ElementalSorcery.data.getNBTForResourceWithException("structures/spellbook_altar"), "spellbookAltar");
		ELEMENT_CRAFTING_ALTAR = new BuildingInherent(
				ElementalSorcery.data.getNBTForResourceWithException("structures/element_crafting_altar"),
				"elementCraftingAltar");
		DECONSTRUCT_ALTAR = new BuildingInherent(
				ElementalSorcery.data.getNBTForResourceWithException("structures/deconstruct_altar"),
				"deconstructAltar");
	}

	private static void horizontalS(IBlockState state, Building building, int d, int y) {
		building.add(state, new BlockPos(d, y, 0));
		building.add(state, new BlockPos(-d, y, 0));
		building.add(state, new BlockPos(0, y, d));
		building.add(state, new BlockPos(0, y, -d));
	}

	private static void horizontalSX(IBlockState state, Building building, int x, int y, int z) {
		building.add(state, new BlockPos(x, y, z));
		if (x == 0)
			return;
		building.add(state, new BlockPos(-x, y, z));
	}

	private static void horizontalSZ(IBlockState state, Building building, int x, int y, int z) {
		building.add(state, new BlockPos(x, y, z));
		if (z == 0)
			return;
		building.add(state, new BlockPos(x, y, -z));
	}

	private static void centralS(IBlockState state, Building building, int x, int y, int z) {
		building.add(state, new BlockPos(x, y, z));
		building.add(state, new BlockPos(-x, y, -z));
		building.add(state, new BlockPos(x, y, -z));
		building.add(state, new BlockPos(-x, y, z));
	}

	private static Building getDeconstructAltar() {
		Building altar = new Building().setAuthor("wanqi");
		IBlockState state = null;
		// 普通方块
		state = ESInitInstance.BLOCKS.ESTONE.getDefaultState();
		centralS(state, altar, 1, 0, 3);
		centralS(state, altar, 3, 0, 1);
		horizontalS(state, altar, 3, 0);
		// 錾制
		state = ESInitInstance.BLOCKS.ESTONE.getDefaultState().withProperty(BlockQuartz.VARIANT,
				BlockQuartz.EnumType.CHISELED);
		centralS(state, altar, 2, 0, 2);
		altar.add(state, new BlockPos(0, 0, 0));
		// 台阶
		state = ESInitInstance.BLOCKS.ESTONE_SLAB.getDefaultState();
		centralS(state, altar, 3, 0, 3);
		centralS(state, altar, 2, 0, 3);
		centralS(state, altar, 3, 0, 2);
		// 竖纹
		state = ESInitInstance.BLOCKS.ESTONE.getDefaultState().withProperty(BlockQuartz.VARIANT,
				BlockQuartz.EnumType.LINES_X);
		for (int i = -1; i <= 1; i++)
			horizontalSZ(state, altar, i, 0, 2);
		altar.add(state, new BlockPos(1, 0, 1));
		altar.add(state, new BlockPos(0, 0, 1));
		altar.add(state, new BlockPos(0, 0, -1));
		altar.add(state, new BlockPos(-1, 0, -1));
		state = ESInitInstance.BLOCKS.ESTONE.getDefaultState().withProperty(BlockQuartz.VARIANT,
				BlockQuartz.EnumType.LINES_Z);
		for (int i = -1; i <= 1; i++)
			horizontalSX(state, altar, 2, 0, i);
		altar.add(state, new BlockPos(-1, 0, 1));
		altar.add(state, new BlockPos(-1, 0, 0));
		altar.add(state, new BlockPos(1, 0, 0));
		altar.add(state, new BlockPos(1, 0, -1));
		return altar;
	}

	private static Building getElementCraftingAltar() {
		Building altar = new Building().setAuthor("wanqi");
		IBlockState state = null;
		// 普通方块
		state = ESInitInstance.BLOCKS.ESTONE.getDefaultState();

		horizontalS(state, altar, 1, 0);
		horizontalS(state, altar, 2, 0);
		for (int i = 1; i <= 3; i++)
			centralS(state, altar, i, 0, 1);
		for (int i = 1; i <= 3; i++)
			centralS(state, altar, i, 0, 2);
		for (int i = 1; i <= 2; i++)
			centralS(state, altar, i, 0, 3);
		altar.add(state, new BlockPos(0, 0, 0));
		// 錾制
		state = ESInitInstance.BLOCKS.ESTONE.getDefaultState().withProperty(BlockQuartz.VARIANT,
				BlockQuartz.EnumType.CHISELED);
		horizontalS(state, altar, 3, 0);
		altar.add(state, new BlockPos(0, 1, 0));
		centralS(state, altar, 3, 3, 3);
		// 楼梯
		addlt(state, altar, 0, 0, 1, 1);
		addltzj(state, altar, 1, 1);
		// 竖纹
		state = ESInitInstance.BLOCKS.ESTONE.getDefaultState().withProperty(BlockQuartz.VARIANT,
				BlockQuartz.EnumType.LINES_Y);
		for (int i = 0; i <= 2; i++)
			centralS(state, altar, 3, i, 3);
		return altar;
	}

	private static Building getSpellbookAltar() {
		Building altar = new Building().setAuthor("wanqi");
		IBlockState state = null;
		// 楼梯
		addlt(state, altar, 1, 2, 0, 3);
		addltzj(state, altar, 0, 3);
		// 錾制
		state = ESInitInstance.BLOCKS.ESTONE.getDefaultState().withProperty(BlockQuartz.VARIANT,
				BlockQuartz.EnumType.CHISELED);
		centralS(state, altar, 2, 0, 2);
		centralS(state, altar, 2, 2, 2);
		horizontalS(state, altar, 3, 0);
		// 竖纹
		state = ESInitInstance.BLOCKS.ESTONE.getDefaultState().withProperty(BlockQuartz.VARIANT,
				BlockQuartz.EnumType.LINES_Y);
		centralS(state, altar, 2, 1, 2);
		state = ESInitInstance.BLOCKS.ESTONE.getDefaultState().withProperty(BlockQuartz.VARIANT,
				BlockQuartz.EnumType.LINES_X);
		for (int i = -1; i <= 1; i++)
			horizontalSZ(state, altar, i, 0, 2);
		state = ESInitInstance.BLOCKS.ESTONE.getDefaultState().withProperty(BlockQuartz.VARIANT,
				BlockQuartz.EnumType.LINES_Z);
		for (int i = -1; i <= 1; i++)
			horizontalSX(state, altar, 2, 0, i);
		// 普通方块
		state = ESInitInstance.BLOCKS.ESTONE.getDefaultState();
		centralS(state, altar, 1, 0, 1);
		horizontalS(state, altar, 1, 0);
		altar.add(state, new BlockPos(0, 0, 0));
		return altar;
	}

	private static Building getLargeAltar() {
		Building altar = new Building().setAuthor("yuzunyan");
		IBlockState state;
		// 錾制
		state = ESInitInstance.BLOCKS.ESTONE.getDefaultState().withProperty(BlockQuartz.VARIANT,
				BlockQuartz.EnumType.CHISELED);
		// *****一层*****
		// altar.add(state, new BlockPos(0, 0, 0));
		// 下边四个
		centralS(state, altar, 2, 0, 2);
		// 所有放水晶的
		centralS(state, altar, 6, 0, 2);
		centralS(state, altar, 8, 0, 2);
		centralS(state, altar, 2, 0, 6);
		centralS(state, altar, 2, 0, 8);
		// 台阶
		state = ESInitInstance.BLOCKS.ESTONE_SLAB.getDefaultState();
		centralS(state, altar, 1, 0, 1);
		for (int i = 1; i <= 9; i++)
			horizontalS(state, altar, i, 0);
		centralS(state, altar, 1, 0, 2);
		centralS(state, altar, 2, 0, 1);
		// 楼梯
		addlt(state, altar, 2, 4, 0, 5);
		addltzj(state, altar, 0, 5);

		state = ESInitInstance.BLOCKS.ESTONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING,
				EnumFacing.SOUTH);
		for (int i = 5; i <= 9; i++)
			horizontalSX(state, altar, i, 0, 1);

		state = ESInitInstance.BLOCKS.ESTONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING,
				EnumFacing.NORTH);
		for (int i = 5; i <= 9; i++)
			horizontalSX(state, altar, i, 0, -1);

		state = ESInitInstance.BLOCKS.ESTONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.EAST);
		for (int i = 5; i <= 9; i++)
			horizontalSZ(state, altar, 1, 0, i);

		state = ESInitInstance.BLOCKS.ESTONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.WEST);
		for (int i = 5; i <= 9; i++)
			horizontalSZ(state, altar, -1, 0, i);

		// 普通方块
		state = ESInitInstance.BLOCKS.ESTONE.getDefaultState();
		centralS(state, altar, 1, 0, 3);
		centralS(state, altar, 1, 0, 4);
		centralS(state, altar, 3, 0, 1);
		centralS(state, altar, 4, 0, 1);

		// *****二层*****
		// 普通方块
		state = ESInitInstance.BLOCKS.ESTONE.getDefaultState();
		centralS(state, altar, 3, 1, 1);
		centralS(state, altar, 1, 1, 3);
		// 竖纹
		state = ESInitInstance.BLOCKS.ESTONE.getDefaultState().withProperty(BlockQuartz.VARIANT,
				BlockQuartz.EnumType.LINES_Y);
		centralS(state, altar, 2, 1, 2);
		// 台阶
		addlt(state, altar, 1, 3, 1, 4);
		// 錾制
		state = ESInitInstance.BLOCKS.ESTONE.getDefaultState().withProperty(BlockQuartz.VARIANT,
				BlockQuartz.EnumType.CHISELED);
		centralS(state, altar, 4, 1, 4);

		// *****三层*****
		// 錾制
		state = ESInitInstance.BLOCKS.ESTONE.getDefaultState().withProperty(BlockQuartz.VARIANT,
				BlockQuartz.EnumType.CHISELED);
		centralS(state, altar, 2, 2, 2);
		// 台阶
		addlt(state, altar, 1, 2, 2, 3);
		addltzj(state, altar, 2, 3);
		// 竖纹
		state = ESInitInstance.BLOCKS.ESTONE.getDefaultState().withProperty(BlockQuartz.VARIANT,
				BlockQuartz.EnumType.LINES_Y);
		centralS(state, altar, 4, 2, 4);

		// *****四层*****
		// 錾制
		state = ESInitInstance.BLOCKS.ESTONE.getDefaultState().withProperty(BlockQuartz.VARIANT,
				BlockQuartz.EnumType.CHISELED);
		horizontalS(state, altar, 1, 3);
		centralS(state, altar, 2, 3, 2);
		// 台阶
		addlt(state, altar, 1, 1, 3, 2);
		// 普通方块
		state = ESInitInstance.BLOCKS.ESTONE.getDefaultState();
		centralS(state, altar, 1, 3, 1);
		horizontalS(state, altar, 2, 3);
		// 竖纹
		state = ESInitInstance.BLOCKS.ESTONE.getDefaultState().withProperty(BlockQuartz.VARIANT,
				BlockQuartz.EnumType.LINES_Y);
		centralS(state, altar, 4, 3, 4);

		// *****四层以上*****
		// 錾制
		state = ESInitInstance.BLOCKS.ESTONE.getDefaultState().withProperty(BlockQuartz.VARIANT,
				BlockQuartz.EnumType.CHISELED);
		centralS(state, altar, 4, 4, 4);
		centralS(state, altar, 2, 7, 2);
		centralS(state, altar, 2, 5, 5);
		centralS(state, altar, 4, 5, 5);
		centralS(state, altar, 5, 5, 2);
		centralS(state, altar, 5, 5, 4);
		// 竖纹
		state = ESInitInstance.BLOCKS.ESTONE.getDefaultState().withProperty(BlockQuartz.VARIANT,
				BlockQuartz.EnumType.LINES_Y);
		for (int i = 4; i <= 6; i++)
			centralS(state, altar, 2, i, 2);
		state = ESInitInstance.BLOCKS.ESTONE.getDefaultState().withProperty(BlockQuartz.VARIANT,
				BlockQuartz.EnumType.LINES_X);
		centralS(state, altar, 3, 5, 5);
		state = ESInitInstance.BLOCKS.ESTONE.getDefaultState().withProperty(BlockQuartz.VARIANT,
				BlockQuartz.EnumType.LINES_Z);
		centralS(state, altar, 5, 5, 3);
		return altar;
	}

	private static void addlt(IBlockState state, Building building, int si, int max, int y, int bj) {
		state = ESInitInstance.BLOCKS.ESTONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING,
				EnumFacing.NORTH);
		for (int i = si; i <= max; i++)
			horizontalSX(state, building, i, y, bj);
		state = ESInitInstance.BLOCKS.ESTONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING,
				EnumFacing.SOUTH);
		for (int i = si; i <= max; i++)
			horizontalSX(state, building, i, y, -bj);
		state = ESInitInstance.BLOCKS.ESTONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.EAST);
		for (int i = si; i <= max; i++)
			horizontalSZ(state, building, -bj, y, i);
		state = ESInitInstance.BLOCKS.ESTONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.WEST);
		for (int i = si; i <= max; i++)
			horizontalSZ(state, building, bj, y, i);
	}

	private static void addltzj(IBlockState state, Building building, int y, int a) {
		state = ESInitInstance.BLOCKS.ESTONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.WEST);
		building.add(state.withProperty(BlockStairs.SHAPE, BlockStairs.EnumShape.OUTER_RIGHT), new BlockPos(a, y, a));
		building.add(state.withProperty(BlockStairs.SHAPE, BlockStairs.EnumShape.OUTER_LEFT), new BlockPos(a, y, -a));
		state = ESInitInstance.BLOCKS.ESTONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.EAST);
		building.add(state.withProperty(BlockStairs.SHAPE, BlockStairs.EnumShape.OUTER_RIGHT), new BlockPos(-a, y, a));
		building.add(state.withProperty(BlockStairs.SHAPE, BlockStairs.EnumShape.OUTER_LEFT), new BlockPos(-a, y, -a));
	}
}

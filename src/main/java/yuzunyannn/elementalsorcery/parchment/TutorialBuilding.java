package yuzunyannn.elementalsorcery.parchment;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import yuzunyannn.elementalsorcery.building.Building;
import yuzunyannn.elementalsorcery.nodegui.GTutorialBuilding;

public class TutorialBuilding {

	protected Building building;
	protected Building extra;

	public TutorialBuilding(Building building) {
		this.building = building;
	}

	public Building getBuilding() {
		return building;
	}

	public void addExtraBlock(BlockPos pos, IBlockState block) {
		if (block == null || block == Blocks.AIR.getDefaultState()) return;
		if (extra == null) extra = new Building();
		extra.add(block, pos);
	}

	public void addExtraBlockNotOverlap(BlockPos pos, IBlockState block) {
		if (block == null || block == Blocks.AIR.getDefaultState()) return;
		if (building.haveBlock(pos)) return;
		if (extra == null) extra = new Building();
		if (extra.haveBlock(pos)) return;
		extra.add(block, pos);
	}

	public void export(GTutorialBuilding node) {
		node.importBuilding(building);
		node.importBuilding(extra);
	}

}

package yuzunyannn.elementalsorcery.entity.fcube;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;

public class Behavior {

	public static Behavior harvestBlock(BlockPos pos, IBlockState state) {
		return new Behavior("harvest", "block").setTargetPos(pos).setTargetState(state);
	}

	final String type;
	final String subType;

	public Behavior(String type, String subType) {
		this.type = type;
		this.subType = subType == null ? "" : subType;
	}

	protected BlockPos targetPos;
	protected IBlockState targetState;

	public String getType() {
		return type;
	}

	public String getSubType() {
		return subType;
	}

	public BlockPos getTargetPos() {
		return targetPos;
	}

	public boolean is(String type) {
		return this.type.equals(type);
	}

	public boolean is(String type, String subType) {
		return this.type.equals(type) && this.subType.equals(subType);
	}

	public Behavior setTargetPos(BlockPos pos) {
		targetPos = pos;
		return this;
	}

	public IBlockState getTargetState() {
		return targetState;
	}

	public Behavior setTargetState(IBlockState targetState) {
		this.targetState = targetState;
		return this;
	}

	@Override
	public String toString() {
		return type + ":" + subType;
	}
}

package yuzunyannn.elementalsorcery.entity.fcube;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;

public class BehaviorBlock extends Behavior {

	public static BehaviorBlock harvestBlock(BlockPos pos, IBlockState state) {
		return new BehaviorBlock("block", "harvest").setTargetPos(pos).setTargetState(state);
	}

	protected BlockPos targetPos;
	protected IBlockState targetState;

	public BehaviorBlock(String type, String subType) {
		super(type, subType);
	}

	public BlockPos getTargetPos() {
		return targetPos;
	}

	public BehaviorBlock setTargetPos(BlockPos pos) {
		targetPos = pos;
		return this;
	}

	public IBlockState getTargetState() {
		return targetState;
	}

	public BehaviorBlock setTargetState(IBlockState targetState) {
		this.targetState = targetState;
		return this;
	}

}

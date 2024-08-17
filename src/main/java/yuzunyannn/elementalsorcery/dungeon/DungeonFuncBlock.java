package yuzunyannn.elementalsorcery.dungeon;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.gfunc.GameFuncExecuteContext;
import yuzunyannn.elementalsorcery.api.gfunc.GameFuncJsonCreateContext;
import yuzunyannn.elementalsorcery.api.gfunc.GameFuncTimes;
import yuzunyannn.elementalsorcery.building.BuildingFace;
import yuzunyannn.elementalsorcery.util.json.ItemRecord;
import yuzunyannn.elementalsorcery.util.json.JsonObject;

public class DungeonFuncBlock extends GameFuncTimes {

	IBlockState state = Blocks.AIR.getDefaultState();

	@Override
	public void loadFromJson(JsonObject json, GameFuncJsonCreateContext context) {
		super.loadFromJson(json, context);
		ItemRecord ir = json.needItem("block");
		ItemStack stack = ir.getStack();
		Block block = Block.getBlockFromItem(stack.getItem());
		state = block.getStateFromMeta(stack.getMetadata());
		if (json.has("facing")) {
			EnumFacing facing = EnumFacing.byIndex(json.needNumber("facing").intValue());
			state = BuildingFace.face(state, facing.getOpposite());
		}
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = super.serializeNBT();
		nbt.setInteger("state", Block.getStateId(state));
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		super.deserializeNBT(nbt);
		state = Block.getStateById(nbt.getInteger("state"));
		if (nbt.hasKey("facing")) {
			EnumFacing facing = EnumFacing.byIndex(nbt.getByte("facing"));
			state = BuildingFace.face(state, facing.getOpposite());
		}
	}

	@Override
	protected void execute(GameFuncExecuteContext context) {
		BlockPos pos = context.getBlockPos();
		World world = context.getWorld();
		DungeonAreaRoom room = null;
		if (context instanceof DungeonFuncExecuteContextBuild)
			room = ((DungeonFuncExecuteContextBuild) context).getRoom();
		if (room == null) world.setBlockState(pos, state);
		else world.setBlockState(pos, BuildingFace.face(state, room.getFacing()));
	}

	@Override
	public String toString() {
		return "<Block> state:" + state;
	}

}

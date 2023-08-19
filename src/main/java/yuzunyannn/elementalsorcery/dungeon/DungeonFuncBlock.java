package yuzunyannn.elementalsorcery.dungeon;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.gfunc.GameFuncExecuteContext;
import yuzunyannn.elementalsorcery.api.gfunc.GameFuncJsonCreateContext;
import yuzunyannn.elementalsorcery.api.gfunc.GameFuncTimes;
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
	}

	@Override
	protected void execute(GameFuncExecuteContext context) {
		BlockPos pos = context.getBlockPos();
		World world = context.getWorld();
		world.setBlockState(pos, state);
	}

	@Override
	public String toString() {
		return "<Block> state:" + state;
	}

}

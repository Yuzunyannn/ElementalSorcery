package yuzunyannn.elementalsorcery.dungeon;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.gfunc.GameFuncExecuteContext;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;

public class DungeonFuncChest extends DungeonFuncLootable {

	@Override
	protected void execute(GameFuncExecuteContext context) {
		Random rand = getCurrRandom();
		BlockPos pos = context.getBlockPos();
		World world = context.getWorld();
		Rotation[] rotations = Rotation.values();
		world.setBlockState(pos,
				Blocks.CHEST.getDefaultState().withRotation(rotations[rand.nextInt(rotations.length)]));
		TileEntityChest chest = BlockHelper.getTileEntity(world, pos, TileEntityChest.class);
		if (chest == null) return;
		this.getFuncCarrier().giveTo(chest);
		List<ItemStack> stacks = getLoots(context);
		DungeonLootLoader.fillInventory(chest, stacks, rand);
		for (ItemStack stack : stacks) Block.spawnAsEntity(world, pos, stack);
	}

	@Override
	public String toString() {
		return "<Chest> loot:" + lootLoader;
	}

}

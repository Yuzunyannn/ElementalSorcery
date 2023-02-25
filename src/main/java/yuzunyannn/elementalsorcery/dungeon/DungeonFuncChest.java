package yuzunyannn.elementalsorcery.dungeon;

import java.util.List;
import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.dungeon.DungeonFuncExecuteContext.DungeonFuncExecuteType;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;
import yuzunyannn.elementalsorcery.util.json.JsonObject;

public class DungeonFuncChest extends DungeonFunc {

	protected DungeonLootLoader lootLoader;

	public void loadFromJson(JsonObject json) {
		super.loadFromJson(json);
		lootLoader = DungeonLootLoader.get(json, "loot");
	};

	@Override
	protected void execute(DungeonFuncExecuteContext context) {
		if (context.executeType != DungeonFuncExecuteType.BUILD) return;
		BlockPos pos = context.getBlockPos();
		World world = context.getWorld();
		Random rand = context.getRand();
		Rotation[] rotations = Rotation.values();
		world.setBlockState(pos,
				Blocks.CHEST.getDefaultState().withRotation(rotations[rand.nextInt(rotations.length)]));
		TileEntityChest chest = BlockHelper.getTileEntity(world, pos, TileEntityChest.class);
		if (chest == null) return;
		List<ItemStack> stacks = lootLoader.getLoots(world, rand);
		DungeonLootLoader.fillInventory(chest, stacks, rand);
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = super.serializeNBT();
		nbt.setTag("loot", lootLoader.serializeNBT());
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		this.lootLoader = DungeonLootLoader.get(nbt.getCompoundTag("loot"));
		super.deserializeNBT(nbt);
	}

	@Override
	public String toString() {
		return "[DungeonFunc] Chest\nloot:" + lootLoader;
	}

}

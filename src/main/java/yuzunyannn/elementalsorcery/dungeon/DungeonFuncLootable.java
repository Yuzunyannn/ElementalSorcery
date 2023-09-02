package yuzunyannn.elementalsorcery.dungeon;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.gfunc.GameFuncExecuteContext;
import yuzunyannn.elementalsorcery.api.gfunc.GameFuncJsonCreateContext;
import yuzunyannn.elementalsorcery.api.gfunc.GameFuncTimes;
import yuzunyannn.elementalsorcery.item.IItemStronger;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;
import yuzunyannn.elementalsorcery.util.json.JsonObject;

public abstract class DungeonFuncLootable extends GameFuncTimes implements IDungeonFuncInit {

	protected DungeonLootLoader lootLoader;
	protected boolean acceptHideItem;
	protected int areaId, roomId;

	public void loadFromJson(JsonObject json, GameFuncJsonCreateContext context) {
		super.loadFromJson(json, context);
		lootLoader = DungeonLootLoader.get(json, "loot");
		if (json.hasBoolean("acceptHideItem")) acceptHideItem = json.getBoolean("acceptHideItem");
	};

	@Override
	public void init(World world, DungeonAreaRoom room) {
		this.areaId = room.getAreId();
		this.roomId = room.getId();
		if (this.acceptHideItem) room.canDropHideFuncCount++;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = super.serializeNBT();
		nbt.setTag("loot", lootLoader.serializeNBT());
		if (acceptHideItem) {
			nbt.setBoolean("acceptHideItem", true);
			nbt.setInteger("areaId", areaId);
			nbt.setInteger("roomId", roomId);
		}
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		this.lootLoader = DungeonLootLoader.get(nbt.getCompoundTag("loot"));
		this.acceptHideItem = nbt.getBoolean("acceptHideItem");
		if (this.acceptHideItem) {
			this.areaId = nbt.getInteger("areaId");
			this.roomId = nbt.getInteger("roomId");
		}
		super.deserializeNBT(nbt);
	}

	public List<ItemStack> getLoots(GameFuncExecuteContext context) {
		List<ItemStack> stacks = lootLoader.getLoots(context.getWorld(), getCurrRandom());

		for (ItemStack stack : stacks) {
			IItemStronger stronger = ItemHelper.getItemStronger(stack);
			if (stronger != null) stronger.onProduced(stack, context.getSrcObj());
		}

		if (this.acceptHideItem) out: {
			DungeonArea area = DungeonWorld.getDungeonWorld(context.getWorld()).getDungeon(areaId);
			DungeonAreaRoom room = area == null ? null : area.getRoomById(roomId);
			if (room == null) break out;
			DungeonFuncGlobal global = room.getFuncGlobal();
			if (global == null) break out;
			int maxCount = global.getHideItemCount();
			if (room.canDropHideFuncCount > 0)
				maxCount = Math.max(1, MathHelper.ceil(maxCount / (float) room.canDropHideFuncCount));
			while (maxCount-- > 0) {
				ItemStack stack = global.popHideItem();
				if (stack.isEmpty()) break;
				stacks.add(stack);
			}
		}
		return stacks;
	}
}

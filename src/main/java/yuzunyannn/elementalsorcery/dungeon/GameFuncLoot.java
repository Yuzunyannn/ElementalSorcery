package yuzunyannn.elementalsorcery.dungeon;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import yuzunyannn.elementalsorcery.api.gfunc.GameFuncExecuteContext;
import yuzunyannn.elementalsorcery.api.gfunc.GameFuncTimes;
import yuzunyannn.elementalsorcery.util.json.JsonObject;

public class GameFuncLoot extends GameFuncTimes {

	protected DungeonLootLoader lootLoader;
	protected boolean clearOrigin;

	public void loadFromJson(JsonObject json) {
		super.loadFromJson(json);
		lootLoader = DungeonLootLoader.get(json, "loot");
		clearOrigin = json.hasBoolean("clearOrigin") ? json.getBoolean("clearOrigin") : false;
	};

	@Override
	protected void execute(GameFuncExecuteContext context) {
		Event event = context.getEvent();
		Random rand = getCurrRandom();
		BlockPos pos = context.getBlockPos();
		World world = context.getWorld();
		List<ItemStack> stacks = lootLoader.getLoots(world, rand);
		if (event instanceof LivingDropsEvent) {
			List<EntityItem> list = ((LivingDropsEvent) event).getDrops();
			if (clearOrigin) list.clear();
			Entity entity = context.getEntity();
			for (ItemStack stack : stacks) {
				EntityItem entityitem = new EntityItem(entity.world, entity.posX, entity.posY + entity.height / 2,
						entity.posZ, stack);
				entityitem.setDefaultPickupDelay();
				list.add(entityitem);
			}

		} else for (ItemStack stack : stacks) Block.spawnAsEntity(world, pos, stack);
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = super.serializeNBT();
		nbt.setTag("loot", lootLoader.serializeNBT());
		nbt.setBoolean("clearOrigin", clearOrigin);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		this.lootLoader = DungeonLootLoader.get(nbt.getCompoundTag("loot"));
		this.clearOrigin = nbt.getBoolean("clearOrigin");
		super.deserializeNBT(nbt);
	}

	@Override
	public String toString() {
		return "<Drop> loot:" + lootLoader;
	}

}

package yuzunyannn.elementalsorcery.dungeon;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import yuzunyannn.elementalsorcery.api.gfunc.GameFuncExecuteContext;
import yuzunyannn.elementalsorcery.api.gfunc.GameFuncJsonCreateContext;
import yuzunyannn.elementalsorcery.entity.EntityItemGoods;
import yuzunyannn.elementalsorcery.util.item.InventoryVest;
import yuzunyannn.elementalsorcery.util.json.JsonObject;

public class DungeonFuncLoot extends DungeonFuncLootable {

	public static final int FLAG_CLEAR_ORIGIN = 1;
	public static final int FLAG_GOOD_PATTERN = 2;

	protected int flag;

	public void loadFromJson(JsonObject json, GameFuncJsonCreateContext context) {
		super.loadFromJson(json, context);
		flag = 0;
		flag |= json.hasBoolean("clearOrigin") ? FLAG_CLEAR_ORIGIN : 0;
		flag |= json.hasBoolean("goodPattern") ? FLAG_GOOD_PATTERN : 0;
	};

	@Override
	protected void execute(GameFuncExecuteContext context) {
		Event event = context.getEvent();
		Random rand = getCurrRandom();
		BlockPos pos = context.getBlockPos();
		World world = context.getWorld();
		List<ItemStack> stacks = getLoots(context);

		// 生物掉落插入掉落list
		if (event instanceof LivingDropsEvent) {
			List<EntityItem> list = ((LivingDropsEvent) event).getDrops();
			if ((flag & FLAG_CLEAR_ORIGIN) != 0) list.clear();
			Entity entity = context.getEntity();
			for (ItemStack stack : stacks) {
				EntityItem entityitem = new EntityItem(entity.world, entity.posX, entity.posY + entity.height / 2,
						entity.posZ, stack);
				entityitem.setDefaultPickupDelay();
				list.add(entityitem);
			}
			return;
		}

		TileEntity tile = world.getTileEntity(pos);

		if (tile != null) {

			int snapshotCount = stacks.size();

			// 先检查所在位置tile是否为IInventory
			if (tile instanceof IInventory) {
				DungeonLootLoader.fillInventory((IInventory) tile, stacks, rand);
			}

			// 再检查所在位置tile是否有IItemHandler
			IItemHandler itemHandler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,
					EnumFacing.NORTH);
			if (itemHandler instanceof IItemHandlerModifiable)
				DungeonLootLoader.fillInventory(new InventoryVest((IItemHandlerModifiable) itemHandler),
						stacks, rand);

			// 更新
			if (stacks.size() != snapshotCount) tile.markDirty();
		}

		// 都没有自然掉落，或者放不进去的自然掉落
		boolean isGoodPattern = (flag & FLAG_GOOD_PATTERN) != 0;
		for (ItemStack stack : stacks) {
			if (isGoodPattern) {
				double d0 = world.rand.nextGaussian() * 0.5;
				double d1 = world.rand.nextGaussian() * 1.0;
				double d2 = world.rand.nextGaussian() * 0.5;
				EntityItemGoods.dropGoods(world, new Vec3d(pos).add(0.5, 0.5, 0.5), stack, 0, true,
						new Vec3d(d0, d1, d2).scale(0.075));
			} else Block.spawnAsEntity(world, pos, stack);
		}
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = super.serializeNBT();
		nbt.setByte("flag", (byte) flag);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		this.flag = nbt.getInteger("flag");
		super.deserializeNBT(nbt);
	}

	@Override
	public String toString() {
		return "<Loot> loot:" + lootLoader;
	}

}

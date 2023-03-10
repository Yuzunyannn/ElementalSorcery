package yuzunyannn.elementalsorcery.dungeon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.common.util.INBTSerializable;
import yuzunyannn.elementalsorcery.elf.quest.loader.QuestLoadJson;
import yuzunyannn.elementalsorcery.util.json.ItemRecord;
import yuzunyannn.elementalsorcery.util.json.Json;
import yuzunyannn.elementalsorcery.util.json.JsonObject;

public abstract class DungeonLootLoader implements INBTSerializable<NBTTagCompound> {

	public static DungeonLootLoader get(JsonObject json, String lootKey) {
		if (json.hasString(lootKey)) return new DungeonMCLootLoader(new ResourceLocation(json.getString(lootKey)));
		return new DungeonQuestLootLoader(json.get(lootKey));
	}

	public static DungeonLootLoader get(NBTTagCompound nbt) {
		int type = nbt.getInteger("tk");
		if (type == 2) return new DungeonQuestLootLoader(nbt);
		return new DungeonMCLootLoader(nbt);
	}

	abstract public List<ItemStack> getLoots(World world, Random rand);

	protected List<ItemStack> satisfiy(List<ItemStack> list) {
		return list;
	}

	static protected class DungeonMCLootLoader extends DungeonLootLoader {

		ResourceLocation lootRes;

		public DungeonMCLootLoader(ResourceLocation res) {
			this.lootRes = res;
		}

		public DungeonMCLootLoader(NBTTagCompound nbt) {
			this.deserializeNBT(nbt);
		}

		@Override
		public NBTTagCompound serializeNBT() {
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setByte("tk", (byte) 1);
			nbt.setString("res", lootRes.toString());
			return nbt;
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt) {
			this.lootRes = new ResourceLocation(nbt.getString("res"));
		}

		@Override
		public List<ItemStack> getLoots(World world, Random rand) {
			LootTable lootTable = world.getLootTableManager().getLootTableFromLocation(lootRes);
			LootContext.Builder lootBuilder = new LootContext.Builder((WorldServer) world);
			LootContext lootContext = lootBuilder.build();
			return satisfiy(lootTable.generateLootForPools(rand, lootContext));
		}

		@Override
		public String toString() {
			return "mc loot : " + lootRes;
		}
	}

	static protected class DungeonQuestLootLoader extends DungeonLootLoader {

		Json json;

		public DungeonQuestLootLoader(Json json) {
			this.json = json;
		}

		public DungeonQuestLootLoader(NBTTagCompound nbt) {
			this.deserializeNBT(nbt);
		}

		@Override
		public NBTTagCompound serializeNBT() {
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setByte("tk", (byte) 2);
			nbt.setString("json", json.toString());
			return nbt;
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt) {
			try {
				json = Json.parser(nbt.getString("json"));
			} catch (Exception e) {
				json = new JsonObject();
			}
		}

		@Override
		public List<ItemStack> getLoots(World world, Random rand) {
			QuestLoadJson json = new QuestLoadJson();
			json.set("loot", this.json);
			List<ItemStack> list;
			try {
				list = ItemRecord.asItemStackList(json.needItems("loot"));
			} catch (Exception e) {
				list = new ArrayList<>();
			}
			return satisfiy(list);
		}

		@Override
		public String toString() {
			return "quest loot : " + json;
		}

	}

	static public List<Integer> getEmptySlotsRandomized(IInventory inventory, Random rand) {
		List<Integer> list = Lists.<Integer>newArrayList();
		for (int i = 0; i < inventory.getSizeInventory(); ++i) {
			if (inventory.getStackInSlot(i).isEmpty()) {
				list.add(Integer.valueOf(i));
			}
		}
		Collections.shuffle(list, rand);
		return list;
	}

	static public void shuffleItems(List<ItemStack> stacks, int invSize, Random rand) {
		List<ItemStack> list = Lists.<ItemStack>newArrayList();
		Iterator<ItemStack> iterator = stacks.iterator();

		while (iterator.hasNext()) {
			ItemStack itemstack = iterator.next();
			if (itemstack.isEmpty()) iterator.remove();
			else if (itemstack.getCount() > 1) {
				list.add(itemstack);
				iterator.remove();
			}
		}

		invSize = invSize - stacks.size();
		while (invSize > 0 && !list.isEmpty()) {
			ItemStack itemstack2 = list.remove(MathHelper.getInt(rand, 0, list.size() - 1));
			int i = MathHelper.getInt(rand, 1, itemstack2.getCount() / 2);
			ItemStack itemstack1 = itemstack2.splitStack(i);

			if (itemstack2.getCount() > 1 && rand.nextBoolean()) list.add(itemstack2);
			else stacks.add(itemstack2);

			if (itemstack1.getCount() > 1 && rand.nextBoolean()) list.add(itemstack1);
			else stacks.add(itemstack1);
		}

		stacks.addAll(list);
		Collections.shuffle(stacks, rand);
	}

	static public void fillInventory(IInventory inventory, List<ItemStack> stacks, Random rand) {
		List<Integer> cList = getEmptySlotsRandomized(inventory, rand);
		shuffleItems(stacks, cList.size(), rand);

		List<ItemStack> mustItems = new LinkedList<>();
		Iterator<ItemStack> iter = stacks.iterator();
		while (iter.hasNext()) {
			ItemStack stack = iter.next();
			if (stack.isEmpty()) iter.remove();
			else if (isMustItem(stack)) {
				mustItems.add(stack);
				iter.remove();
			}
		}

		for (ItemStack stack : mustItems) {
			if (cList.isEmpty()) return;
			inventory.setInventorySlotContents(cList.remove(cList.size() - 1), stack);
		}
		for (ItemStack itemstack : stacks) {
			if (cList.isEmpty()) return;
			inventory.setInventorySlotContents(cList.remove(cList.size() - 1), itemstack);
		}
	}

	static public boolean isMustItem(ItemStack stack) {
		return false;
	}
}

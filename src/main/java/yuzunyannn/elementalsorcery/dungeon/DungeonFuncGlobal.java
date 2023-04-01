package yuzunyannn.elementalsorcery.dungeon;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.gfunc.GameFunc;
import yuzunyannn.elementalsorcery.api.gfunc.GameFuncExecuteContext;
import yuzunyannn.elementalsorcery.api.gfunc.GameFuncFinOp;
import yuzunyannn.elementalsorcery.api.gfunc.GameFuncJsonCreateContext;
import yuzunyannn.elementalsorcery.item.ItemMemoryFragment;
import yuzunyannn.elementalsorcery.item.ItemMemoryFragment.MemoryFragment;
import yuzunyannn.elementalsorcery.util.helper.NBTHelper;
import yuzunyannn.elementalsorcery.util.json.JsonObject;

public class DungeonFuncGlobal extends GameFunc implements IDungeonFuncInit {

	protected List<MemoryFragment> memoryFragments = new ArrayList<>();
	protected List<MemoryFragment> hideFragments = new ArrayList<>();

	public static int[] serializeMFList(List<MemoryFragment> list) {
		int[] array = new int[list.size()];
		for (int i = 0; i < array.length; i++) array[i] = list.get(i).toMeta();
		return array;
	}

	public static List<MemoryFragment> deserializeMFList(int[] array) {
		List<MemoryFragment> list = new ArrayList<>();
		for (int meta : array) list.add(new ItemMemoryFragment.MemoryFragment(meta));
		return list;
	}

	public List<MemoryFragment> getRequireMemoryFragments() {
		return memoryFragments;
	}

	public List<MemoryFragment> getProduceFragments() {
		return hideFragments;
	}

	public static class GroupInfo {
		public int maxCount;
		public int minCount;
	}

	protected List<ItemStack> hideItems = new ArrayList<>();

	@Override
	public void loadFromJson(JsonObject json, GameFuncJsonCreateContext context) {
		this.memoryFragments = loadFragments(json, "keyRequire");
		this.hideFragments = loadFragments(json, "keySupport");
	};

	protected List<MemoryFragment> loadFragments(JsonObject json, String jkey) {
		List<MemoryFragment> list = new ArrayList<>();
		if (json.hasObject(jkey)) {
			json = json.getObject(jkey);
			for (String key : json.keySet()) {
				for (EnumDyeColor color : EnumDyeColor.values()) {
					if (color.getName().equals(key)) {
						list.add(new MemoryFragment(color, json.getNumber(key).intValue()));
						break;
					}
				}
			}
		}
		return list;
	}

	@Override
	public void init(World world, DungeonAreaRoom room) {
		for (MemoryFragment mf : hideFragments) {
			ItemStack stack = ItemMemoryFragment.getMemoryFragment(world.provider.getDimension(), room.getAreId(),
					mf.getColor());
			stack.setCount(mf.getCount());
			hideItems.add(stack);
		}
	}

	public ItemStack popHideItem() {
		if (hideItems.isEmpty()) return ItemStack.EMPTY;
		return hideItems.remove(hideItems.size() - 1);
	}

	public ItemStack topHideItem() {
		if (hideItems.isEmpty()) return ItemStack.EMPTY;
		return hideItems.get(hideItems.size() - 1);
	}

	public int getHideItemCount() {
		return hideItems.size();
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = super.serializeNBT();
		if (!memoryFragments.isEmpty()) nbt.setIntArray("mfs", serializeMFList(memoryFragments));
		if (!hideFragments.isEmpty()) nbt.setIntArray("mfh", serializeMFList(hideFragments));
		NBTHelper.setItemList(nbt, "hitms", hideItems);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		super.deserializeNBT(nbt);
		memoryFragments = deserializeMFList(nbt.getIntArray("mfs"));
		hideFragments = deserializeMFList(nbt.getIntArray("mfh"));
		hideItems = NBTHelper.getItemList(nbt, "hitms");
	}

	@Nullable
	public GroupInfo getGroupInfo(String key) {
		return null;
	}

	@Override
	public GameFuncFinOp afterExecute(GameFuncExecuteContext context) {
		return GameFuncFinOp.KEEP;
	}

	@Override
	public String toString() {
		return "<Global>";
	}

}

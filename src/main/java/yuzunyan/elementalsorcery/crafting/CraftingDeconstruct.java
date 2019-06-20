package yuzunyan.elementalsorcery.crafting;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import yuzunyan.elementalsorcery.ElementalSorcery;
import yuzunyan.elementalsorcery.api.element.Element;
import yuzunyan.elementalsorcery.api.element.ElementStack;
import yuzunyan.elementalsorcery.element.ElementMap;
import yuzunyan.elementalsorcery.tile.TileStaticMultiBlock;
import yuzunyan.elementalsorcery.util.NBTHelper;

public class CraftingDeconstruct implements ICraftingCommit {

	private List<ItemStack> itemList = new ArrayList<ItemStack>();
	// 操作结果链表，该变量同时起到标定作用
	private LinkedList<ElementStack> rest_estacks = null;

	public CraftingDeconstruct(ItemStack stack) {
		itemList.add(stack);
		ElementStack[] out_estacks = ElementMap.instance.toElement(stack);
		if (out_estacks == null)
			return;
		rest_estacks = new LinkedList<ElementStack>();
		for (ElementStack estack : out_estacks) {
			for (int i = 0; i < stack.getCount(); i++)
				rest_estacks.add(estack.copy().getElementWhenDeconstruct(stack, ElementMap.instance.complex(stack),Element.DP_ALTAR));
		}
	}

	public CraftingDeconstruct(NBTTagCompound nbt) {
		if (nbt == null) {
			ElementalSorcery.logger.warn("CraftingDeconstruct恢复的时候，nbt为NULL！");
			nbt = new NBTTagCompound();
		}
		this.deserializeNBT(nbt);
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		if (rest_estacks != null)
			NBTHelper.setElementist(nbt, "rest_estacks", rest_estacks);
		NBTHelper.setItemList(nbt, "itemList", itemList);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		if (nbt.hasKey("rest_estacks"))
			rest_estacks = NBTHelper.getElementList(nbt, "rest_estacks");
		if (nbt.hasKey("itemList"))
			itemList = NBTHelper.getItemList(nbt, "itemList");
	}

	@Override
	public List<ItemStack> getItems() {
		return itemList;
	}

	// 更新一次
	public boolean update(TileStaticMultiBlock tileMul) {
		if (rest_estacks == null || rest_estacks.isEmpty())
			return false;
		ElementStack estack = rest_estacks.getFirst();
		ElementStack put = estack.splitStack(1);
		if (tileMul.putElementToSpPlace(put, tileMul.getPos().up())) {
			if (estack.isEmpty())
				rest_estacks.removeFirst();
		} else {

		}
		if (rest_estacks.isEmpty()) {
			rest_estacks = null;
			return false;
		}
		return true;
	}
}

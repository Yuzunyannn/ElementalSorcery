package yuzunyannn.elementalsorcery.crafting.altar;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.api.element.Element;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.crafting.element.ElementMap;
import yuzunyannn.elementalsorcery.tile.altar.TileStaticMultiBlock;
import yuzunyannn.elementalsorcery.util.NBTHelper;

public class CraftingDeconstruct implements ICraftingAltar {

	private List<ItemStack> itemList = new ArrayList<ItemStack>();
	// 操作结果链表，该变量同时起到标定作用
	private LinkedList<ElementStack> restEStacks = null;
	// 是否ok
	private boolean isOk = true;

	public CraftingDeconstruct(ItemStack stack) {
		itemList.add(stack);
		ElementStack[] out_estacks = ElementMap.instance.toElement(stack);
		if (out_estacks == null)
			return;
		restEStacks = new LinkedList<ElementStack>();
		for (ElementStack estack : out_estacks) {
			for (int i = 0; i < stack.getCount(); i++)
				restEStacks.add(estack.copy().getElementWhenDeconstruct(stack, ElementMap.instance.complex(stack),
						Element.DP_ALTAR));
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
		if (restEStacks != null)
			NBTHelper.setElementist(nbt, "rest_estacks", restEStacks);
		NBTHelper.setItemList(nbt, "itemList", itemList);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		if (nbt.hasKey("rest_estacks"))
			restEStacks = NBTHelper.getElementList(nbt, "rest_estacks");
		if (nbt.hasKey("itemList"))
			itemList = NBTHelper.getItemList(nbt, "itemList");
	}

	@Override
	public List<ItemStack> getItems() {
		return itemList;
	}

	// 更新一次
	@Override
	public void update(TileStaticMultiBlock tileMul) {
		if (restEStacks == null || restEStacks.isEmpty()) {
			this.isOk = false;
			return;
		}
		ElementStack estack = restEStacks.getFirst();
		ElementStack put = estack.splitStack(1);
		if (tileMul.putElementToSpPlace(put, tileMul.getPos().up())) {
		} else {

		}
		if (estack.isEmpty())
			restEStacks.removeFirst();
		if (restEStacks.isEmpty()) {
			restEStacks = null;
			this.isOk = false;
			return;
		}
	}

	@Override
	public boolean canContinue(TileStaticMultiBlock tileMul) {
		return this.isOk;
	}

	@Override
	public boolean end(TileStaticMultiBlock tileMul) {
		ItemStack stack = itemList.get(0);
		itemList.clear();
		if (!tileMul.isIntact())
			return false;
		stack = ElementMap.instance.remain(stack);
		if (!stack.isEmpty())
			itemList.add(stack);
		return true;
	}
}

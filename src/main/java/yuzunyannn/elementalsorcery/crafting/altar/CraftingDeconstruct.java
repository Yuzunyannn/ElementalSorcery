package yuzunyannn.elementalsorcery.crafting.altar;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.api.crafting.IToElement;
import yuzunyannn.elementalsorcery.api.crafting.IToElementInfo;
import yuzunyannn.elementalsorcery.crafting.ICraftingLaunchAnime;
import yuzunyannn.elementalsorcery.crafting.element.ElementMap;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.render.entity.AnimeRenderDeconstruct;
import yuzunyannn.elementalsorcery.tile.altar.TileStaticMultiBlock;
import yuzunyannn.elementalsorcery.util.NBTHelper;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

public class CraftingDeconstruct implements ICraftingAltar {

	private List<ItemStack> itemList = new ArrayList<ItemStack>();
	// 操作结果链表，该变量同时起到标定作用
	private LinkedList<ElementStack> restEStacks = null;
	// 保留的结果
	private ItemStack[] remainStacks = null;
	// 是否ok
	private boolean isOk = true;
	// 当前没有放入的元素
	public ElementStack freeElement = ElementStack.EMPTY;

	public CraftingDeconstruct(World world, ItemStack stack, int lvPower, IToElement toElement) {
		itemList.add(stack);
		ElementStack[] outEstacks;
		if (toElement == null) toElement = ElementMap.instance;
		IToElementInfo teInfo = toElement.toElement(stack);
		outEstacks = teInfo == null ? null : teInfo.element();
		if (outEstacks == null) return;
		restEStacks = new LinkedList<ElementStack>();
		for (ElementStack estack : outEstacks) {
			for (int i = 0; i < stack.getCount(); i++)
				restEStacks.add(estack.copy().becomeElementWhenDeconstruct(world, stack, teInfo.complex(), lvPower));
		}
		remainStacks = teInfo.remain();
		remainStacks = ItemHelper.copy(remainStacks);
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
		if (restEStacks != null) NBTHelper.setElementist(nbt, "restEles", restEStacks);
		NBTHelper.setItemList(nbt, "itemList", itemList);
		if (remainStacks != null) NBTHelper.setItemArray(nbt, "remains", remainStacks);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		if (nbt.hasKey("restEles")) restEStacks = NBTHelper.getElementList(nbt, "restEles");
		if (nbt.hasKey("itemList")) itemList = NBTHelper.getItemList(nbt, "itemList");
		if (nbt.hasKey("remains")) {
			remainStacks = NBTHelper.getItemArray(nbt, "remains");
			if (remainStacks.length == 0) remainStacks = null;
		}
	}

	@Override
	public List<ItemStack> getItems() {
		return itemList;
	}

	@Override
	public void CraftingDisappear(World world, BlockPos pos) {
		this.itemList.clear();
	}

	public LinkedList<ElementStack> getRestElementStacks() {
		return restEStacks;
	}

	// 更新一次
	@Override
	public void update(TileStaticMultiBlock tileMul) {
		if (restEStacks == null || restEStacks.isEmpty()) {
			this.isOk = false;
			return;
		}
		freeElement = ElementStack.EMPTY;
		ElementStack estack = restEStacks.getFirst();
		ElementStack put = estack.splitStack(1);
		if (!tileMul.putElementToSpPlace(put, tileMul.getPos().up())) {
			freeElement = put;
		}
		if (estack.isEmpty()) restEStacks.removeFirst();
		if (restEStacks.isEmpty()) {
			restEStacks = null;
			this.isOk = false;
			return;
		}
	}

	@Override
	public boolean canContinue(TileStaticMultiBlock tileMul) {
		return tileMul.isIntact() && this.isOk;
	}

	@Override
	public boolean end(TileStaticMultiBlock tileMul) {
		itemList.clear();
		if (!tileMul.isIntact()) return false;
		if (remainStacks != null) {
			for (ItemStack stack : remainStacks) itemList.add(stack);
		}
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ICraftingLaunchAnime getAnime() {
		return new AnimeRenderDeconstruct();
	}
}

package yuzunyannn.elementalsorcery.crafting.altar;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.ability.IItemStructure;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.crafting.ICraftingLaunchAnime;
import yuzunyannn.elementalsorcery.render.entity.AnimeRenderConstruct;
import yuzunyannn.elementalsorcery.tile.altar.TileStaticMultiBlock;
import yuzunyannn.elementalsorcery.util.NBTHelper;

public class CraftingConstruct implements ICraftingAltar {

	private List<ItemStack> itemList = new ArrayList<ItemStack>();
	protected LinkedList<ElementStack> needEStacks;
	protected boolean ok = false;

	public CraftingConstruct(NBTTagCompound nbt) {
		if (nbt == null)
			return;
		this.deserializeNBT(nbt);
	}

	public CraftingConstruct(TileStaticMultiBlock tileMul, int cout, IItemStructure structure) {
		if (cout <= 0)
			return;
		itemList.add(structure.getStructureItem(0).copy());
		itemList.get(0).setCount(cout);
		ElementStack[] estacks = structure.toElement(itemList.get(0));
		if (estacks != null && estacks.length > 0) {
			needEStacks = new LinkedList<ElementStack>();
			for (ElementStack estack : estacks) {
				estack = estack.copy();
				estack.setCount(estack.getCount() * cout);
				needEStacks.add(estack);
			}

		}
	}

	@Override
	public List<ItemStack> getItems() {
		return itemList;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		if (needEStacks != null)
			NBTHelper.setElementist(nbt, "estacks", needEStacks);
		if (itemList.size() > 0)
			nbt.setTag("target", itemList.get(0).serializeNBT());
		nbt.setBoolean("ok", ok);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		if (nbt.hasKey("estacks"))
			needEStacks = NBTHelper.getElementList(nbt, "estacks");
		if (needEStacks != null && needEStacks.isEmpty())
			needEStacks = null;
		this.ok = nbt.getBoolean("ok");
		itemList.clear();
		itemList.add(new ItemStack(nbt.getCompoundTag("target")));
	}

	@Override
	public void update(TileStaticMultiBlock tileMul) {
		if (needEStacks == null)
			return;
		// 取元素
		int index = tileMul.getWorld().rand.nextInt(needEStacks.size());
		ElementStack need = needEStacks.get(index).splitStack(1);
		ElementStack get = tileMul.getElementFromSpPlace(need, tileMul.getPos().up());
		// 测试元素是否合格
		if (get.isEmpty()) {
			needEStacks.get(index).grow(need);
		} else {
			if (needEStacks.get(index).isEmpty()) {
				needEStacks.remove(index);
			}
			if (needEStacks.isEmpty()) {
				needEStacks = null;
				ok = true;
			}
		}
	}

	@Override
	public boolean canContinue(TileStaticMultiBlock tileMul) {
		return tileMul.isIntact() && needEStacks != null;
	}

	@Override
	public boolean end(TileStaticMultiBlock tileMul) {
		if (ok == false)
			itemList.clear();
		return ok;
	}

	@Override
	public ICraftingLaunchAnime getAnime() {
		return new AnimeRenderConstruct();
	}

	@Override
	public int getEndTime(TileStaticMultiBlock tileSupremeCraftingTable) {
		return 60;
	}

	@Override
	public void CraftingDisappear(World world, BlockPos pos) {
		itemList.clear();
	}

}
package yuzunyan.elementalsorcery.tile;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import yuzunyan.elementalsorcery.api.ability.IGetItemStack;
import yuzunyan.elementalsorcery.api.element.Element;
import yuzunyan.elementalsorcery.api.element.ElementStack;
import yuzunyan.elementalsorcery.building.Buildings;
import yuzunyan.elementalsorcery.building.MultiBlock;
import yuzunyan.elementalsorcery.crafting.ICraftingLaunch;
import yuzunyan.elementalsorcery.crafting.ICraftingLaunchAnime;
import yuzunyan.elementalsorcery.element.ElementMap;
import yuzunyan.elementalsorcery.render.entity.AnimeRenderDeconstruct;

public class TileDeconstructAltarTable extends TileStaticMultiBlock implements IGetItemStack, ICraftingLaunch {

	@Override
	public void initMultiBlock() {
		structure = new MultiBlock(Buildings.DECONSTRUCT_ALTAR, this, new BlockPos(0, -1, 0));
		structure.addSpecialBlock(new BlockPos(2, 1, 2));
		structure.addSpecialBlock(new BlockPos(2, 1, -2));
		structure.addSpecialBlock(new BlockPos(-2, 1, -2));
		structure.addSpecialBlock(new BlockPos(-2, 1, 2));
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		if (compound.hasKey("stack"))
			stack = new ItemStack(compound.getCompoundTag("stack"));
		else
			stack = ItemStack.EMPTY;
		if (compound.hasKey("rest_estacks")) {
			rest_estacks = new LinkedList<ElementStack>();
			NBTTagList list = (NBTTagList) compound.getTag("rest_estacks");
			for (NBTBase base : list) {
				rest_estacks.add(new ElementStack((NBTTagCompound) base));
			}
		}
		super.readFromNBT(compound);

	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setTag("stack", stack.serializeNBT());
		if (rest_estacks != null) {
			NBTTagList list = new NBTTagList();
			for (ElementStack estack : rest_estacks)
				list.appendTag(estack.serializeNBT());
			compound.setTag("rest_estacks", list);
		}
		return super.writeToNBT(compound);
	}

	private ItemStack stack = ItemStack.EMPTY;

	@Override
	public void setStack(ItemStack stack) {
		this.stack = stack;
		this.updateToClient();
		this.markDirty();
	}

	@Override
	public ItemStack getStack() {
		return stack;
	}

	// 产出的结果，该引用的对象不应该被修改
	ElementStack[] out_estacks = null;
	// 操作结果链表，该变量同时起到标定作用
	LinkedList<ElementStack> rest_estacks = null;
	// 正在进行
	protected boolean working = false;
	// 开始前等待时间
	int startTime = 0;

	@Override
	public boolean isWorking() {
		return working;
	}

	@Override
	public boolean craftingBegin(CraftingType type, EntityPlayer player) {
		if (!this.isIntact())
			return false;
		this.craftingRecovery(type, player);
		return rest_estacks != null;
	}

	@Override
	public void craftingRecovery(CraftingType type, EntityPlayer player) {
		// 如果不为null，则可以说明是从存档里恢复的
		if (rest_estacks == null) {
			this.recheckDeconstructResult();
			if (out_estacks == null)
				return;
			rest_estacks = new LinkedList<ElementStack>();
			for (ElementStack estack : out_estacks) {
				for (int i = 0; i < stack.getCount(); i++)
					rest_estacks.add(estack.copy().getElementWhenDeconstruct(stack, Element.DP_ALTAR));
			}
		}
		startTime = 40;
		this.working = true;
	}

	@Override
	public void craftingUpdate() {
		if (rest_estacks == null)
			return;
		if (startTime >= 0) {
			startTime--;
			return;
		}
		ElementStack estack = rest_estacks.getFirst();
		ElementStack put = estack.splitStack(1);
		if (this.putElementToSpPlace(put, this.pos.up())) {
			if (estack.isEmpty())
				rest_estacks.removeFirst();
		} else {

		}
		if (rest_estacks.isEmpty())
			rest_estacks = null;
	}

	@Override
	public void craftingUpdateClient() {
		this.craftingUpdate();
	}

	@Override
	public boolean canContinue() {
		return this.isIntact() && rest_estacks != null;
	}

	@Override
	public int craftingEnd(List<ItemStack> list) {
		rest_estacks = null;
		working = false;
		ItemStack stack = list.get(0);
		list.clear();
		if (!this.isIntact()) {
			return ICraftingLaunch.FAIL;
		}
		stack = ElementMap.instance.remain(stack);
		if (!stack.isEmpty())
			list.add(stack);
		return ICraftingLaunch.SUCCESS;
	}

	@Override
	public List<ItemStack> commitItems() {
		List<ItemStack> list = new ArrayList<ItemStack>();
		list.add(stack);
		stack = ItemStack.EMPTY;
		return list;
	}

	@Override
	public boolean checkType(CraftingType type) {
		return type == CraftingType.ELEMENT_DECONSTRUCT;
	}

	@Override
	public ICraftingLaunchAnime getAnime() {
		return new AnimeRenderDeconstruct();
	}

	private void recheckDeconstructResult() {
		if (stack.isEmpty()) {
			out_estacks = null;
			return;
		}
		out_estacks = ElementMap.instance.toElement(stack);
	}
}

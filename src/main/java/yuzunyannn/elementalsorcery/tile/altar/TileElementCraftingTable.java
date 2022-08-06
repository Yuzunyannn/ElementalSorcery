package yuzunyannn.elementalsorcery.tile.altar;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.crafting.IElementRecipe;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.building.Buildings;
import yuzunyannn.elementalsorcery.building.MultiBlock;
import yuzunyannn.elementalsorcery.crafting.ICraftingCommit;
import yuzunyannn.elementalsorcery.crafting.ICraftingLaunch;
import yuzunyannn.elementalsorcery.crafting.ICraftingLaunchAnime;
import yuzunyannn.elementalsorcery.crafting.altar.CraftingCrafting;
import yuzunyannn.elementalsorcery.util.TickOut;
import yuzunyannn.elementalsorcery.util.item.IItemStackHandlerInventory;

public class TileElementCraftingTable extends TileStaticMultiBlock
		implements ICraftingLaunch, IItemStackHandlerInventory {

	protected ItemStackHandler inventory = new ItemStackHandler(9);

	@Override
	public ItemStackHandler getItemStackHandler() {
		return inventory;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.equals(capability)) {
			if (facing == null) return true;
		}
		return super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.equals(capability)) {
			if (facing == null) return (T) inventory;
		}
		return super.getCapability(capability, facing);
	}

	public void initMultiBlock() {
		structure = new MultiBlock(Buildings.ELEMENT_CRAFTING_ALTAR, this, new BlockPos(0, -2, 0));
		structure.addSpecialBlock(new BlockPos(0, 1, 3));
		structure.addSpecialBlock(new BlockPos(0, 1, -3));
		structure.addSpecialBlock(new BlockPos(3, 1, 0));
		structure.addSpecialBlock(new BlockPos(-3, 1, 0));
	}

	// 开始前等待时间
	TickOut startTime = null;
	// 正在进行
	protected boolean working = false;

	@Override
	public boolean canCrafting(String type, @Nullable EntityLivingBase player) {
		if (!ICraftingLaunch.TYPE_ELEMENT_CRAFTING.equals(type)) return false;
		if (this.isEmpty()) return false;
		if (!this.isAndCheckIntact()) return false;
		this.onCraftMatrixChanged();
		if (this.getOutput().isEmpty()) return false;
		return true;
	}

	@Override
	public ICraftingCommit craftingBegin(String type, EntityLivingBase player) {
		working = true;
		startTime = new TickOut(80);
		CraftingCrafting cc = new CraftingCrafting(this);
		this.clear();
		this.markDirty();
		return cc;
	}

	@Override
	public ICraftingCommit recovery(String type, EntityLivingBase player, NBTTagCompound nbt) {
		working = true;
		startTime = new TickOut(80);
		this.markDirty();
		this.clear();
		CraftingCrafting cc = new CraftingCrafting(nbt);
		this.result = cc.getResult(world);
		return cc;
	}

	@Override
	public boolean isWorking() {
		return working;
	}

	@Override
	public void craftingUpdate(ICraftingCommit commit) {
		if (startTime.tick()) return;
		((CraftingCrafting) commit).update(this);
	}

	@Override
	public void craftingUpdateClient(ICraftingCommit commit) {
		this.craftingUpdate(commit);
	}

	@Override
	public int craftingEnd(ICraftingCommit commit) {
		working = false;
		result = ItemStack.EMPTY;
		// 如果祭坛不完整，或者没有生成任何物品
		if (!((CraftingCrafting) commit).end(this)) return ICraftingLaunch.FAIL;
		this.markDirty();
		return ICraftingLaunch.SUCCESS;
	}

	@Override
	public boolean canContinue(ICraftingCommit commit) {
		return ((CraftingCrafting) commit).canContinue(this);
	}

	// 产出结果，该引用的对象不应该被修改，该变量同时起到标定作用
	private ItemStack result = ItemStack.EMPTY;
	// 所需元素，该引用对象不应被修改
	private List<ElementStack> needEstacks = null;

	public ItemStack getOutput() {
		return result;
	}

	@SideOnly(Side.CLIENT)
	public void setOutput(ItemStack stack) {
		result = stack;
	}

	public List<ElementStack> getNeedElements() {
		return needEstacks;
	}

	@SideOnly(Side.CLIENT)
	public void setNeedElements(List<ElementStack> list) {
		needEstacks = list;
	}

	public void onCraftMatrixChanged() {
		IElementRecipe irecipe = ESAPI.recipeMgr.findMatchingRecipe(this, world);
		if (irecipe == null) {
			result = ItemStack.EMPTY;
			return;
		}
		result = irecipe.getCraftingResult(this).copy();
		needEstacks = irecipe.getNeedElements();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ICraftingLaunchAnime getAnime(ICraftingCommit commit) {
		return ((CraftingCrafting) commit).getAnime();
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		if (compound.hasKey("inventory")) inventory.deserializeNBT(compound.getCompoundTag("inventory"));
		super.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setTag("inventory", inventory.serializeNBT());
		return super.writeToNBT(compound);
	}

}

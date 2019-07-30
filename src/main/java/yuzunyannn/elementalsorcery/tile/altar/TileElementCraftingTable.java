package yuzunyannn.elementalsorcery.tile.altar;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import yuzunyannn.elementalsorcery.api.crafting.IRecipe;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.building.Buildings;
import yuzunyannn.elementalsorcery.building.MultiBlock;
import yuzunyannn.elementalsorcery.crafting.CraftingCrafting;
import yuzunyannn.elementalsorcery.crafting.ICraftingCommit;
import yuzunyannn.elementalsorcery.crafting.ICraftingLaunch;
import yuzunyannn.elementalsorcery.crafting.ICraftingLaunchAnime;
import yuzunyannn.elementalsorcery.crafting.RecipeManagement;
import yuzunyannn.elementalsorcery.crafting.ICraftingLaunch.CraftingType;
import yuzunyannn.elementalsorcery.render.entity.AnimeRenderCrafting;
import yuzunyannn.elementalsorcery.util.item.IItemStackHandlerInventory;

public class TileElementCraftingTable extends TileStaticMultiBlock
		implements ICraftingLaunch, IItemStackHandlerInventory {

	protected ItemStackHandler inventory = null;

	public TileElementCraftingTable() {
		inventory = new ItemStackHandler(9);
	}

	@Override
	public ItemStackHandler getItemStackHandler() {
		return inventory;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.equals(capability)) {
			if (facing == null)
				return true;
		}
		return super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.equals(capability)) {
			if (facing == null)
				return (T) inventory;
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

	// 玩家
	EntityLivingBase player = null;
	// 开始前等待时间
	int startTime = 0;
	// 结束前等待时间
	int endTime = 0;
	// 正在进行
	protected boolean working = false;

	@Override
	public boolean canCrafting(CraftingType type, @Nullable EntityLivingBase player) {
		if (type != CraftingType.ELEMENT_CRAFTING)
			return false;
		if (this.isEmpty())
			return false;
		if (!this.isIntact())
			return false;
		this.onCraftMatrixChanged();
		if (this.getOutput().isEmpty())
			return false;
		return true;
	}

	@Override
	public ICraftingCommit craftingBegin(CraftingType type, EntityLivingBase player) {
		this.player = player;
		working = true;
		startTime = 80;
		endTime = 80;
		this.markDirty();
		CraftingCrafting cc = new CraftingCrafting(this);
		this.clear();
		return cc;
	}

	@Override
	public ICraftingCommit recovery(CraftingType type, EntityLivingBase player, NBTTagCompound nbt) {
		this.player = player;
		working = true;
		startTime = 80;
		endTime = 80;
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
		if (result.isEmpty())
			return;
		if (startTime >= 0) {
			startTime--;
			return;
		}
		if (Math.random() < 0.25)
			return;
		boolean ok = ((CraftingCrafting) commit).update(this, this.player);
		if (ok == false) {
			result = ItemStack.EMPTY;
		}
	}

	@Override
	public void craftingUpdateClient(ICraftingCommit commit) {
		this.craftingUpdate(commit);
	}

	@Override
	public int craftingEnd(ICraftingCommit commit) {
		working = false;
		CraftingCrafting cc = ((CraftingCrafting) commit);
		// 如果祭坛不完整，或者没有生成任何物品
		if (!this.isIntact() || !cc.haveResult()) {
			player = null;
			return ICraftingLaunch.FAIL;
		}
		cc.end();
		player = null;
		this.markDirty();
		return ICraftingLaunch.SUCCESS;
	}

	@Override
	public boolean canContinue(ICraftingCommit commit) {
		if (result.isEmpty()) {
			if (endTime >= 0) {
				endTime--;
				return this.isIntact();
			} else
				return false;
		}
		return this.isIntact();
	}

	// 产出结果，该引用的对象不应该被修改，该变量同时起到标定作用
	private ItemStack result = ItemStack.EMPTY;
	// 所需元素，该引用对象不应被修改
	private List<ElementStack> needEstacks = null;

	public ItemStack getOutput() {
		return result;
	}

	public List<ElementStack> getNeedElements() {
		return needEstacks;
	}

	public void onCraftMatrixChanged() {
		IRecipe irecipe = RecipeManagement.instance.findMatchingRecipe(this, world);
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
		return new AnimeRenderCrafting();
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		if (compound.hasKey("inventory"))
			inventory.deserializeNBT(compound.getCompoundTag("inventory"));
		super.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setTag("inventory", inventory.serializeNBT());
		return super.writeToNBT(compound);
	}

}

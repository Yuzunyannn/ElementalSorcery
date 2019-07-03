package yuzunyan.elementalsorcery.tile;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import yuzunyan.elementalsorcery.api.crafting.IRecipe;
import yuzunyan.elementalsorcery.api.element.ElementStack;
import yuzunyan.elementalsorcery.building.Buildings;
import yuzunyan.elementalsorcery.building.MultiBlock;
import yuzunyan.elementalsorcery.crafting.CraftingCrafting;
import yuzunyan.elementalsorcery.crafting.ICraftingCommit;
import yuzunyan.elementalsorcery.crafting.ICraftingLaunch;
import yuzunyan.elementalsorcery.crafting.RecipeManagement;

public class TileElementCraftingTable extends TileStaticMultiBlockWithInventory implements ICraftingLaunch {

	public TileElementCraftingTable() {
		super(9);
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
	public boolean craftingBegin(CraftingType type, EntityPlayer player) {
		if (this.isEmpty())
			return false;
		if (!this.isIntact())
			return false;
		this.onCraftMatrixChanged();
		if (this.getOutput().isEmpty())
			return false;
		this.player = player;
		working = true;
		startTime = 80;
		endTime = 80;
		this.markDirty();
		return working;
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
	public ICraftingCommit commitItems() {
		CraftingCrafting cc = new CraftingCrafting(this);
		this.clear();
		return cc;
	}

	@Override
	public boolean canContinue() {
		if (result.isEmpty()) {
			if (endTime >= 0) {
				endTime--;
				return this.isIntact();
			} else
				return false;
		}
		return this.isIntact();
	}

	@Override
	public boolean checkType(CraftingType type) {
		return type == CraftingType.ELEMENT_CRAFTING;
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

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		return super.writeToNBT(compound);
	}

}

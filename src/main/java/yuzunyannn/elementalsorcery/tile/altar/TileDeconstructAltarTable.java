package yuzunyannn.elementalsorcery.tile.altar;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ability.IGetItemStack;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.building.Buildings;
import yuzunyannn.elementalsorcery.building.MultiBlock;
import yuzunyannn.elementalsorcery.crafting.CraftingDeconstruct;
import yuzunyannn.elementalsorcery.crafting.ICraftingCommit;
import yuzunyannn.elementalsorcery.crafting.ICraftingLaunch;
import yuzunyannn.elementalsorcery.crafting.ICraftingLaunchAnime;
import yuzunyannn.elementalsorcery.crafting.element.ElementMap;
import yuzunyannn.elementalsorcery.render.entity.AnimeRenderDeconstruct;

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
		super.readFromNBT(compound);

	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		if (!stack.isEmpty())
			compound.setTag("stack", stack.serializeNBT());
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

	// 产出的结果，该引用的对象不应该被修改，标定结束
	ElementStack[] outEstacks = null;
	// 正在进行
	protected boolean working = false;
	// 开始前等待时间
	int startTime = 0;

	@Override
	public boolean isWorking() {
		return working;
	}

	@Override
	public boolean canCrafting(CraftingType type, @Nullable EntityLivingBase player) {
		if (type != CraftingType.ELEMENT_DECONSTRUCT)
			return false;
		if (!this.isIntact())
			return false;
		this.recheckDeconstructResult();
		if (outEstacks == null)
			return false;
		return outEstacks != null;
	}

	@Override
	public ICraftingCommit craftingBegin(CraftingType type, EntityLivingBase player) {
		this.working = true;
		startTime = 40;
		ItemStack outStack = stack;
		stack = ItemStack.EMPTY;
		return new CraftingDeconstruct(outStack);
	}

	@Override
	public ICraftingCommit recovery(CraftingType type, EntityLivingBase player, NBTTagCompound nbt) {
		this.working = true;
		startTime = 40;
		stack = ItemStack.EMPTY;
		CraftingDeconstruct cd = new CraftingDeconstruct(nbt);
		this.outEstacks = new ElementStack[0];
		return cd;
	}

	@Override
	public void craftingUpdate(ICraftingCommit commit) {
		if (startTime >= 0) {
			startTime--;
			return;
		}
		boolean ok = ((CraftingDeconstruct) commit).update(this);
		if (ok == false)
			outEstacks = null;
	}

	@Override
	public void craftingUpdateClient(ICraftingCommit commit) {
		this.craftingUpdate(commit);
	}

	@Override
	public boolean canContinue(ICraftingCommit commit) {
		return this.isIntact() && outEstacks != null;
	}

	@Override
	public int craftingEnd(ICraftingCommit commit) {
		outEstacks = null;
		working = false;
		CraftingDeconstruct cd = ((CraftingDeconstruct) commit);
		ItemStack stack = commit.getItems().get(0);
		commit.getItems().clear();
		if (!this.isIntact()) {
			return ICraftingLaunch.FAIL;
		}
		stack = ElementMap.instance.remain(stack);
		if (!stack.isEmpty())
			commit.getItems().add(stack);
		return ICraftingLaunch.SUCCESS;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ICraftingLaunchAnime getAnime(ICraftingCommit commit) {
		return new AnimeRenderDeconstruct();
	}

	private void recheckDeconstructResult() {
		if (stack.isEmpty()) {
			outEstacks = null;
			return;
		}
		outEstacks = ElementMap.instance.toElement(stack);
	}
}
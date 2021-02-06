package yuzunyannn.elementalsorcery.tile.altar;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.crafting.IToElement;
import yuzunyannn.elementalsorcery.api.crafting.IToElementInfo;
import yuzunyannn.elementalsorcery.api.tile.IGetItemStack;
import yuzunyannn.elementalsorcery.building.Buildings;
import yuzunyannn.elementalsorcery.building.MultiBlock;
import yuzunyannn.elementalsorcery.crafting.ICraftingCommit;
import yuzunyannn.elementalsorcery.crafting.ICraftingLaunch;
import yuzunyannn.elementalsorcery.crafting.ICraftingLaunchAnime;
import yuzunyannn.elementalsorcery.crafting.altar.CraftingDeconstruct;
import yuzunyannn.elementalsorcery.crafting.element.ElementMap;
import yuzunyannn.elementalsorcery.element.Element;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.util.TickOut;

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
		if (compound.hasKey("stack")) stack = new ItemStack(compound.getCompoundTag("stack"));
		else stack = ItemStack.EMPTY;
		super.readFromNBT(compound);

	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		if (!stack.isEmpty()) compound.setTag("stack", stack.serializeNBT());
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

	/** 获取祭坛分解等级 */
	public int getAltarDecLevel() {
		return Element.DP_ALTAR;
	}

	/** 获取特殊的分级句柄 */
	public IToElement getToElement() {
		return null;
	}

	// 产出的结果，该引用的对象不应该被修改，标定结束
	ElementStack[] outEstacks = null;
	// 正在进行
	protected boolean working = false;
	// 开始前等待时间
	TickOut startTime = null;

	@Override
	public boolean isWorking() {
		return working;
	}

	@Override
	public boolean canCrafting(String type, @Nullable EntityLivingBase player) {
		if (!ICraftingLaunch.TYPE_ELEMENT_DECONSTRUCT.equals(type)) return false;
		if (!this.isIntact()) return false;
		this.recheckDeconstructResult();
		if (outEstacks == null) return false;
		return outEstacks != null;
	}

	@Override
	public ICraftingCommit craftingBegin(String type, EntityLivingBase player) {
		this.working = true;
		startTime = new TickOut(40);
		ItemStack outStack = stack;
		stack = ItemStack.EMPTY;
		return new CraftingDeconstruct(world, outStack, this.getAltarDecLevel(), this.getToElement());
	}

	@Override
	public ICraftingCommit recovery(String type, EntityLivingBase player, NBTTagCompound nbt) {
		this.working = true;
		startTime = new TickOut(40);
		stack = ItemStack.EMPTY;
		CraftingDeconstruct cd = new CraftingDeconstruct(nbt);
		return cd;
	}

	@Override
	public void craftingUpdate(ICraftingCommit commit) {
		if (startTime.tick()) return;
		((CraftingDeconstruct) commit).update(this);
	}

	@Override
	public void craftingUpdateClient(ICraftingCommit commit) {
		this.craftingUpdate(commit);
	}

	@Override
	public boolean canContinue(ICraftingCommit commit) {
		return ((CraftingDeconstruct) commit).canContinue(this);
	}

	@Override
	public int craftingEnd(ICraftingCommit commit) {
		outEstacks = null;
		working = false;
		if (!((CraftingDeconstruct) commit).end(this)) return ICraftingLaunch.FAIL;
		return ICraftingLaunch.SUCCESS;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ICraftingLaunchAnime getAnime(ICraftingCommit commit) {
		return ((CraftingDeconstruct) commit).getAnime();
	}

	private void recheckDeconstructResult() {
		if (stack.isEmpty()) {
			outEstacks = null;
			return;
		}
		IToElement toElement = this.getToElement();
		if (toElement == null) toElement = ElementMap.instance;
		IToElementInfo teInfo = toElement.toElement(stack);
		outEstacks = teInfo == null ? null : teInfo.element();
	}
}

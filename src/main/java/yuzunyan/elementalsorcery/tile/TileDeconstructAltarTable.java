package yuzunyan.elementalsorcery.tile;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyan.elementalsorcery.api.ability.IGetItemStack;
import yuzunyan.elementalsorcery.api.element.ElementStack;
import yuzunyan.elementalsorcery.building.Buildings;
import yuzunyan.elementalsorcery.building.MultiBlock;
import yuzunyan.elementalsorcery.crafting.CraftingDeconstruct;
import yuzunyan.elementalsorcery.crafting.ICraftingCommit;
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
		super.readFromNBT(compound);

	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
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
	ElementStack[] out_estacks = null;
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
		this.recheckDeconstructResult();
		if (out_estacks == null)
			return false;
		this.working = true;
		startTime = 40;
		return out_estacks != null;
	}

	@Override
	public ICraftingCommit recovery(CraftingType type, EntityLivingBase player, NBTTagCompound nbt) {
		this.working = true;
		startTime = 40;
		stack = ItemStack.EMPTY;
		CraftingDeconstruct cd = new CraftingDeconstruct(nbt);
		this.out_estacks = new ElementStack[0];
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
			out_estacks = null;
	}

	@Override
	public void craftingUpdateClient(ICraftingCommit commit) {
		this.craftingUpdate(commit);
	}

	@Override
	public boolean canContinue() {
		return this.isIntact() && out_estacks != null;
	}

	@Override
	public int craftingEnd(ICraftingCommit commit) {
		out_estacks = null;
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
	public ICraftingCommit commitItems() {
		ItemStack outStack = stack;
		stack = ItemStack.EMPTY;
		return new CraftingDeconstruct(outStack);
	}

	@Override
	public boolean checkType(CraftingType type) {
		return type == CraftingType.ELEMENT_DECONSTRUCT;
	}

	@Override
	@SideOnly(Side.CLIENT)
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

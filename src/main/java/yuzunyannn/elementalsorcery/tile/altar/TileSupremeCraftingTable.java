package yuzunyannn.elementalsorcery.tile.altar;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFirework;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.api.ability.IGetItemStack;
import yuzunyannn.elementalsorcery.api.ability.IItemStructure;
import yuzunyannn.elementalsorcery.api.crafting.IRecipe;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.building.Buildings;
import yuzunyannn.elementalsorcery.building.MultiBlock;
import yuzunyannn.elementalsorcery.crafting.ICraftingCommit;
import yuzunyannn.elementalsorcery.crafting.ICraftingLaunch;
import yuzunyannn.elementalsorcery.crafting.ICraftingLaunchAnime;
import yuzunyannn.elementalsorcery.crafting.RecipeManagement;
import yuzunyannn.elementalsorcery.crafting.altar.CraftingCrafting;
import yuzunyannn.elementalsorcery.crafting.altar.CraftingDeconstruct;
import yuzunyannn.elementalsorcery.crafting.altar.ICraftingAltar;
import yuzunyannn.elementalsorcery.crafting.element.ItemStructure;
import yuzunyannn.elementalsorcery.event.EventClient;
import yuzunyannn.elementalsorcery.render.model.ModelSupremeCraftingTable;
import yuzunyannn.elementalsorcery.util.TickOut;
import yuzunyannn.elementalsorcery.util.item.IItemStackHandlerInventory;

public class TileSupremeCraftingTable extends TileStaticMultiBlock
		implements ITickable, ICraftingLaunch, IItemStackHandlerInventory {

	protected ItemStackHandler inventory = new ItemStackHandler(25);

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

	private void addSpecialBlock(MultiBlock structure, int x, int y, int z) {
		structure.addSpecialBlock(new BlockPos(x, y, z));
		structure.addSpecialBlock(new BlockPos(-x, y, -z));
		structure.addSpecialBlock(new BlockPos(x, y, -z));
		structure.addSpecialBlock(new BlockPos(-x, y, z));
	}

	@Override
	public void initMultiBlock() {
		structure = new MultiBlock(Buildings.LARGE_ALTAR, this, new BlockPos(0, -4, 0));
		this.addSpecialBlock(structure, 6, 1, 2);
		this.addSpecialBlock(structure, 8, 1, 2);
		this.addSpecialBlock(structure, 2, 1, 6);
		this.addSpecialBlock(structure, 2, 1, 8);
	}

	@Override
	public boolean isIntact() {
		return this.ok;
	}

	@Override
	public void update() {
		checkTime++;
		if (checkTime % 40 == 0)
			this.checkIntact();
		if (this.world.isRemote)
			this.clientRender();
		if (!this.ok)
			return;
	}

	@Override
	public ItemStackHandler getItemStackHandler() {
		return inventory;
	}

	@Override
	public boolean isWorking() {
		return isWorking;
	}

	// 是否工作
	boolean isWorking = false;
	// 当前类型
	String nowType;
	// 开始前等待时间
	TickOut startTime = null;

	@Override
	public boolean canCrafting(String type, EntityLivingBase player) {
		if (!this.isIntact())
			return false;
		nowType = this.onCraftMatrixChanged();
		if (nowType == null)
			return false;
		if (!nowType.equals(type))
			return false;
		return true;
	}

	@Override
	public ICraftingCommit craftingBegin(String type, EntityLivingBase player) {
		isWorking = true;
		nowType = type;
		startTime = new TickOut(60);
		ICraftingAltar craftingAltar;
		switch (type) {
		case ICraftingLaunch.TYPE_ELEMENT_CRAFTING:
			craftingAltar = new CraftingCrafting(this);
			break;
		case ICraftingLaunch.TYPE_ELEMENT_DECONSTRUCT:
			craftingAltar = new CraftingDeconstruct(this.getCenterItem(),
					ItemStructure.getItemStructure(this.getPlatformItem()));
			break;
		default:
			craftingAltar = null;
			ElementalSorcery.logger.warn("在SupremeCraftingTable开始合成时，出现了不存在的类型！" + type);
			break;
		}
		this.clear();
		this.markDirty();
		return craftingAltar;
	}

	@Override
	public ICraftingCommit recovery(String type, EntityLivingBase player, NBTTagCompound nbt) {
		isWorking = true;
		nowType = type;
		startTime = new TickOut(60);
		this.clear();
		switch (type) {
		case ICraftingLaunch.TYPE_ELEMENT_CRAFTING:
			return new CraftingCrafting(nbt);
		case ICraftingLaunch.TYPE_ELEMENT_DECONSTRUCT:
			return new CraftingDeconstruct(nbt);
		default:
			ElementalSorcery.logger.warn("在SupremeCraftingTable恢复合成时，出现了不存在的类型！");
			break;
		}
		return null;
	}

	@Override
	public void craftingUpdate(ICraftingCommit commit) {
		if (startTime.tick())
			return;
		((ICraftingAltar) commit).update(this);
	}

	@Override
	public void craftingUpdateClient(ICraftingCommit commit) {
		this.craftingUpdate(commit);
	}

	@Override
	public boolean canContinue(ICraftingCommit commit) {
		return ((ICraftingAltar) commit).canContinue(this);
	}

	@Override
	public int craftingEnd(ICraftingCommit commit) {
		isWorking = false;
		if (!((ICraftingAltar) commit).end(this))
			return ICraftingLaunch.FAIL;
		this.markDirty();
		return ICraftingLaunch.SUCCESS;
	}

	@Override
	public ICraftingLaunchAnime getAnime(ICraftingCommit commit) {
		return ((ICraftingAltar) commit).getAnime();
	}

	private ItemStack output = ItemStack.EMPTY;
	private List<ElementStack> outEStacks = null;

	public String onCraftMatrixChanged() {
		// 检测是否平台上有物品
		ItemStack platformItem = this.getPlatformItem();
		if (platformItem.isEmpty()) {
			// 检测是否合成
			IRecipe irecipe = RecipeManagement.instance.findMatchingRecipe(this, world);
			if (irecipe != null) {
				output = irecipe.getCraftingResult(this).copy();
				outEStacks = irecipe.getNeedElements();
				return ICraftingLaunch.TYPE_ELEMENT_CRAFTING;
			}
		} else {
			if (this.checkJustCenter()) {
				IItemStructure structure = ItemStructure.getItemStructureWithoutNew(platformItem);
				ItemStack centerStack = this.getCenterItem();
				ElementStack[] estacks = structure.toElement(centerStack);
				outEStacks = new LinkedList<>();
				if (estacks != null) {
					for (ElementStack estack : estacks)
						outEStacks.add(estack);
					return ICraftingLaunch.TYPE_ELEMENT_DECONSTRUCT;
				}
			}

		}
		return null;
	}

	private boolean checkJustCenter() {
		for (int i = 0; i < 4; i++) {
			if (!this.getStackInSlot(i).isEmpty())
				return false;
		}
		for (int i = 5; i < this.getSizeInventory(); i++) {
			if (!this.getStackInSlot(i).isEmpty())
				return false;
		}
		return true;
	}

	private final ItemStack getCenterItem() {
		return this.getStackInSlot(4);
	}

	public ItemStack getOutput() {
		return output;
	}

	public List<ElementStack> getNeedElements() {
		return outEStacks;
	}

	public ItemStack getPlatformItem() {
		IGetItemStack getItem = this.getPlatform();
		if (getItem != null)
			return getItem.getStack();
		return ItemStack.EMPTY;
	}

	public IGetItemStack getPlatform() {
		BlockPos pos = this.pos.down(3);
		TileEntity tile = this.world.getTileEntity(pos);
		if (tile instanceof IGetItemStack) {
			return ((IGetItemStack) tile);
		}
		return null;
	}

	public float roate;
	public float prevRoate;
	public float legR;
	public float prevLegR;
	static final float ROATE_RATE = 1.0f;
	static final float LEG_RATE = 0.4f;
	static final float LEG_MAX = 16f;

	private void clientRender() {
		this.prevRoate = this.roate;
		this.prevLegR = this.legR;

		EntityPlayer entityplayer = this.world.getClosestPlayer((double) ((float) this.pos.getX() + 0.5F),
				(double) ((float) this.pos.getY() + 0.5F), (double) ((float) this.pos.getZ() + 0.5F), 4.0D, false);
		if ((entityplayer != null && this.ok) || this.isWorking) {
			if (legR < LEG_MAX)
				legR += LEG_RATE;
			roate += ROATE_RATE;
			this.clientParticle();
		} else {
			if (roate > 0) {
				this.clientParticle();
				roate += ROATE_RATE;
				if (roate < 180) {
					if (legR < LEG_MAX)
						legR += LEG_RATE;
				} else {
					if (roate % 360 > 360 - LEG_MAX / LEG_RATE * ROATE_RATE || legR < LEG_MAX) {
						legR -= LEG_RATE;
					}
					if (legR <= 0) {
						prevRoate = roate = 0;
						prevLegR = legR = 0;
					}
				}
			}
		}
	}

	private void clientParticle() {
		if (EventClient.tick % 10 != 0)
			return;
		float randRoate = EventClient.rand.nextFloat() * 2 * 3.1415926f;
		float r = EventClient.rand.nextFloat() * 0.35f;
		float x = this.pos.getX() + 0.5f + r * MathHelper.sin(randRoate);
		float z = this.pos.getZ() + 0.5f + r * MathHelper.cos(randRoate);

		float y = this.pos.getY() + ModelSupremeCraftingTable.roateToHight(roate / 180.0f * 3.1514926f) * 0.03125f
				+ 0.5f;

		Particle particle = new ParticleFirework.Spark(this.world, x, y, z, 0, 0, 0,
				Minecraft.getMinecraft().effectRenderer);
		Minecraft.getMinecraft().effectRenderer.addEffect(particle);
	}
}

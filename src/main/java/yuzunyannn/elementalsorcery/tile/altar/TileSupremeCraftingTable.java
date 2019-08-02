package yuzunyannn.elementalsorcery.tile.altar;

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
import yuzunyannn.elementalsorcery.api.ability.IGetItemStack;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.building.Buildings;
import yuzunyannn.elementalsorcery.building.MultiBlock;
import yuzunyannn.elementalsorcery.crafting.ICraftingCommit;
import yuzunyannn.elementalsorcery.crafting.ICraftingLaunch;
import yuzunyannn.elementalsorcery.event.EventClient;
import yuzunyannn.elementalsorcery.render.model.ModelSupremeCraftingTable;
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
		if (!this.ok)
			return;
		if (this.world.isRemote)
			this.clientRender();
	}

	@Override
	public ItemStackHandler getItemStackHandler() {
		return inventory;
	}

	@Override
	public boolean isWorking() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canCrafting(String type, EntityLivingBase player) {
		if (!this.isIntact())
			return false;
		switch (type) {
		case ICraftingLaunch.TYPE_ELEMENT_CRAFTING:
		case ICraftingLaunch.TYPE_ELEMENT_DECONSTRUCT:
			break;
		default:
			return false;
		}
		return false;
	}

	@Override
	public ICraftingCommit craftingBegin(String type, EntityLivingBase player) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ICraftingCommit recovery(String type, EntityLivingBase player, NBTTagCompound nbt) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void craftingUpdate(ICraftingCommit commit) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean canContinue(ICraftingCommit commit) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int craftingEnd(ICraftingCommit commit) {
		// TODO Auto-generated method stub
		return 0;
	}

	public String onCraftMatrixChanged() {
		return null;
	}

	public ItemStack getOutput() {
		return ItemStack.EMPTY;
	}

	public List<ElementStack> getNeedElements() {
		return null;
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
				(double) ((float) this.pos.getY() + 0.5F), (double) ((float) this.pos.getZ() + 0.5F), 3.5D, false);
		if (entityplayer != null) {
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

package yuzunyannn.elementalsorcery.tile.dungeon;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;

public class TileDungeonHaystack extends TileDungeonPropBase {

	protected AxisAlignedBB box = Block.FULL_BLOCK_AABB;
	protected boolean pressure;

	public TileDungeonHaystack() {
		this.setHightLevel(Math.random() * 16);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setByte("hLev", (byte) Math.floor(getHightLevel() * 8));
		if (!this.isSending()) {
			compound.setBoolean("pressure", pressure);
		}
		return super.writeToNBT(compound);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		if (!this.isSending()) {
			pressure = compound.getBoolean("pressure");
		}
		this.setHightLevel(compound.getInteger("hLev") / 8f);
		super.readFromNBT(compound);
	}

	public AxisAlignedBB getBoundingBox() {
		return box;
	}

	public double getHightRate() {
		return box.maxY;
	}

	public double getHightLevel() {
		return box.maxY * 16;
	}

	/**
	 * @param lev 1~16
	 */
	public void setHightLevel(double lev) {
		double height = Math.max(Math.min(lev / 16, 1 - 0.001), 1 / 16.0);
		box = new AxisAlignedBB(0, 0, 0, 1, height, 1);
	}

	public void setPressure(boolean pressure) {
		this.pressure = pressure;
	}

	public boolean getPressure() {
		return this.pressure;
	}

	@Override
	public void onDrops(NonNullList<ItemStack> drops) {
		drops.add(new ItemStack(Items.WHEAT, Math.max(1, MathHelper.floor(getHightRate() * 9))));
	}

	@Override
	public void onEntityCollision(Entity entityIn) {
		if (entityIn instanceof EntityLivingBase) {
			if (entityIn instanceof IMob) return;
			if (entityIn instanceof IAnimals) return;
			if (!getPressure()) return;
			world.destroyBlock(pos, true);
			onSweepOpen((EntityLivingBase) entityIn);
		}
	}

	@Override
	public boolean onActivated(EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY,
			float hitZ) {
		world.destroyBlock(pos, true);
		onSweepOpen(playerIn);
		return true;
	}

	@Override
	public void onHarvest(EntityPlayer player) {
		this.onSweepOpen(player);
	}

	@Override
	public void onDestroyed() {
		this.onSweepOpen(null);
	}

	public void onSweepOpen(EntityLivingBase player) {
		trigger("onClick", player);
	}

}

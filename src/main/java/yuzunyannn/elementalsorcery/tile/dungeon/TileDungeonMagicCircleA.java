package yuzunyannn.elementalsorcery.tile.dungeon;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import yuzunyannn.elementalsorcery.util.helper.Color;

public class TileDungeonMagicCircleA extends TileDungeonPropBase implements ITickable {

	protected int tick = 0;
	protected Color color = new Color(0x803298);
	protected int activeCD = 600;
	protected int currCD = 0;

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public void setActiveCD(int activeCD) {
		this.activeCD = activeCD;
	}

	public void onBuild() {
		this.currCD = world.rand.nextInt(activeCD / 2);
	}

	public boolean doMagic(@Nullable EntityPlayer player) {
		if (currCD > 0) return false;
		this.currCD = this.activeCD;
		return player == null ? trigger("onActived") : trigger("onActived", player);
	}

	@Override
	public boolean onActivated(EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY,
			float hitZ) {
		if (carrier.hasFunc("onClick")) return trigger("onClick", player);
		return doMagic(player);
	}

	@Override
	public void onHarvest(EntityPlayer player) {
		this.onDestroyed();
	}

	@Override
	public void onDestroyed() {
		trigger("onDestroyed");
	}

//	@Override
//	public void onEntityCollision(Entity entity) {
////		if (entity instanceof EntityPlayer) System.out.println("???");
//		super.onEntityCollision(entity);
//	}

	@Override
	public void update() {
		if (this.world.isRemote) return;

		tick++;

		if (carrier.isEmpty()) {
			this.onDead();
			return;
		}

		if (--currCD > 0) return;
		doMagic(null);
	}

	protected void onDead() {
		trigger("onDead");
		world.destroyBlock(pos, false);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("color", color.toInt());
		if (this.isSending()) return super.writeToNBT(nbt);
		nbt.setInteger("activeCD", activeCD);
		nbt.setInteger("currCD", currCD);
		return super.writeToNBT(nbt);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		color.setColor(nbt.getInteger("color"));
		activeCD = nbt.getInteger("activeCD");
		currCD = nbt.getInteger("currCD");
		super.readFromNBT(nbt);
	}

}

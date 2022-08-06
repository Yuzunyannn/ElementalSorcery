package yuzunyannn.elementalsorcery.tile;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.element.Element;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.element.IStarFlowerCast;
import yuzunyannn.elementalsorcery.capability.ElementInventory;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.batch.EffectElementMove;

public class TileStarFlower extends TileEntityNetwork implements ITickable {

	protected ElementInventory inventory = new ElementInventory() {
		@Override
		public boolean insertElement(int slot, ElementStack estack, boolean simulate) {
			if (estack.getElement() instanceof IStarFlowerCast) return super.insertElement(slot, estack, simulate);
			return false;
		};
	};
	protected int tick;

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (ElementInventory.ELEMENTINVENTORY_CAPABILITY == capability) return true;
		return super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (ElementInventory.ELEMENTINVENTORY_CAPABILITY == capability) return (T) inventory;
		return super.getCapability(capability, facing);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		inventory.loadState(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		inventory.saveState(compound);
		return super.writeToNBT(compound);
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		return oldState != newSate;
	}

	@Override
	public void update() {
		tick++;
		if (world.isRemote) this.updateClient();
		ElementStack estack = inventory.getStackInSlot(0);
		if (estack.isEmpty()) return;
		Element element = estack.getElement();
		if (element instanceof IStarFlowerCast) {
			ElementStack ret = ((IStarFlowerCast) element).starFlowerCasting(world, pos, estack, tick);
			if (ret == estack) {
				if (ret.isEmpty()) {
					inventory.setStackInSlot(0, ElementStack.EMPTY);
					this.updateToClient();
				}
				return;
			}
			inventory.setStackInSlot(0, estack);
			this.updateToClient();
		} else estack.shrink(1);
	}

	@SideOnly(Side.CLIENT)
	public void updateClient() {
		Random rand = world.rand;
		if (tick % 2 != 0) return;
		if (rand.nextInt(3) != 0) return;
		ElementStack estack = inventory.getStackInSlot(0);
		if (estack.isEmpty()) return;
		double x = pos.getX() + 0.5;
		double y = pos.getY() + 0.75;
		double z = pos.getZ() + 0.5;
		EffectElementMove effect = new EffectElementMove(world, new Vec3d(x, y, z));
		effect.yAccelerate = 0.0005;
		effect.setVelocity(rand.nextGaussian() * 0.01, rand.nextGaussian() * 0.01, rand.nextGaussian() * 0.01);
		effect.setColor(estack.getColor());
		Effect.addEffect(effect);
	}
}

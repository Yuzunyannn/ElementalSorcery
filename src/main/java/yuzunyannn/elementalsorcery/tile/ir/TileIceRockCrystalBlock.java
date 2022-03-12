package yuzunyannn.elementalsorcery.tile.ir;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import yuzunyannn.elementalsorcery.block.container.BlockIceRockCrystalBlock;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.tile.altar.TileDevolveCube;

public class TileIceRockCrystalBlock extends TileIceRockEnergy {

	public boolean canNotLinkMark = false;
	protected double magicFragment = 0;

	public double getMagicFragmentOwn() {
		return magicFragment;
	}

	public void setMagicFragmentOwn(double magicFragment) {
		this.magicFragment = magicFragment;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt = super.writeToNBT(nbt);
		if (this.isSending()) return nbt;
		nbt.setDouble("fragment", getMagicFragmentOwn());
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		setMagicFragmentOwn(nbt.getDouble("fragment"));
	}

	@Override
	public void link(BlockPos standPos) {
		IBlockState state = world.getBlockState(pos);
		IBlockState newState = state.withProperty(BlockIceRockCrystalBlock.STATUS,
				BlockIceRockCrystalBlock.EnumStatus.ACTIVE);
		if (newState != state) world.setBlockState(pos, newState);
		super.link(standPos);
	}

	@Override
	public void unlink() {
		changeState: {
			IBlockState state = world.getBlockState(pos);
			if (state.getBlock() != ESInit.BLOCKS.ICE_ROCK_CRYSTAL_BLOCK) break changeState;
			IBlockState newState = state.withProperty(BlockIceRockCrystalBlock.STATUS,
					BlockIceRockCrystalBlock.EnumStatus.NORMAL);
			if (newState == state) break changeState;
			world.setBlockState(pos, newState);
		}
		super.unlink();
	}

	public double transferMagicFragment() {
		double magicFragment = getMagicFragmentOwn();
		setMagicFragmentOwn(0);
		return magicFragment;
	}

	@Override
	public boolean canInventoryOperateBy(Object operater) {
		return !(operater instanceof TileDevolveCube);
	}

}

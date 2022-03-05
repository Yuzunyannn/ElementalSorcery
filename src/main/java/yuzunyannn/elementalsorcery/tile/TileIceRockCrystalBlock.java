package yuzunyannn.elementalsorcery.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.math.BlockPos;
import yuzunyannn.elementalsorcery.block.container.BlockIceRockCrystalBlock;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;
import yuzunyannn.elementalsorcery.util.helper.NBTHelper;

public class TileIceRockCrystalBlock extends TileIceRockSendRecv {

	protected BlockPos linkPos;

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		if (linkPos != null) NBTHelper.setBlockPos(compound, "linkPos", linkPos);
		return super.writeToNBT(compound);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		if (NBTHelper.hasBlockPos(compound, "linkPos")) linkPos = NBTHelper.getBlockPos(compound, "linkPos");
		else linkPos = null;
		super.readFromNBT(compound);
	}

	public boolean isLinked() {
		return linkPos != null;
	}

	public BlockPos getLinkPos() {
		return linkPos;
	}

	public void link(BlockPos standPos) {

		changeState: {
			IBlockState state = world.getBlockState(pos);
			IBlockState newState = state.withProperty(BlockIceRockCrystalBlock.STATUS,
					BlockIceRockCrystalBlock.EnumStatus.ACTIVE);
			if (newState == state) break changeState;
			world.setBlockState(pos, newState);
		}

		linkPos = standPos;
		this.markDirty();
	}

	public void unlink() {

		changeState: {
			IBlockState state = world.getBlockState(pos);
			if (state.getBlock() != ESInit.BLOCKS.ICE_ROCK_CRYSTAL_BLOCK) break changeState;
			IBlockState newState = state.withProperty(BlockIceRockCrystalBlock.STATUS,
					BlockIceRockCrystalBlock.EnumStatus.NORMAL);
			if (newState == state) break changeState;
			world.setBlockState(pos, newState);
		}

		linkPos = null;
		this.markDirty();

	}

	public double transferMagicFragment() {
		double magicFragment = getMagicFragment();
		setMagicFragment(0);
		return magicFragment;
	}

	@Override
	public void setMagicFragment(double magicFragment) {
		super.setMagicFragment(magicFragment);
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return null;
	}

	@Override
	public TileIceRockSendRecv getIceRockCore() {
		if (isLinked()) return BlockHelper.getTileEntity(world, linkPos, TileIceRockStand.class);
		return null;
	}

}

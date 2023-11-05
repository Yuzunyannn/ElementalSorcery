package yuzunyannn.elementalsorcery.tile.dungeon;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import yuzunyannn.elementalsorcery.block.env.BlockStoneDecoration;

public class TileStoneDecoration extends TileEntity {

	protected BlockStoneDecoration.EnumDecType enumType = BlockStoneDecoration.EnumDecType.values()[0];
	protected EnumFacing facing = EnumFacing.NORTH;

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("tId", enumType.getMeta());
		nbt.setInteger("facing", facing.getHorizontalIndex());
		return super.writeToNBT(nbt);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		facing = EnumFacing.byHorizontalIndex(nbt.getInteger("facing"));
		enumType = BlockStoneDecoration.EnumDecType.fromMeta(nbt.getInteger("tId"));
	}

	public EnumFacing getFacing() {
		return facing;
	}

	public void setFacing(EnumFacing facing) {
		this.facing = facing;
	}

	public BlockStoneDecoration.EnumDecType getDecorationType() {
		return enumType;
	}

	public void setDecorationType(BlockStoneDecoration.EnumDecType enumType) {
		this.enumType = enumType;
	}

	public void setDecorationType(int meta) {
		this.enumType = BlockStoneDecoration.EnumDecType.fromMeta(meta);
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(this.pos, this.getBlockMetadata(), this.getUpdateTag());
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return writeToNBT(new NBTTagCompound());
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		this.readFromNBT(pkt.getNbtCompound());
	}
}

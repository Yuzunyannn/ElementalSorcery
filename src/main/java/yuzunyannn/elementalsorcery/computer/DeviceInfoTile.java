package yuzunyannn.elementalsorcery.computer;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;

public class DeviceInfoTile extends DeviceInfo {

	protected final TileEntity tile;
	protected String typeName;

	public DeviceInfoTile(TileEntity tile) {
		this.tile = tile;
	}

	@Override
	public boolean isMobile() {
		return false;
	}

	@Override
	public String getTranslationWorkKey() {
		if (typeName == null) {
			try {
				IBlockState state = tile.getWorld().getBlockState(tile.getPos());
				typeName = state.getBlock().getTranslationKey() + ".name";
			} catch (Exception e) {
				typeName = "";
			}
		}
		return typeName;
	}

}

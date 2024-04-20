package yuzunyannn.elementalsorcery.computer;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class DeviceInfoTile extends DeviceInfo {

	protected final TileEntity tile;
	protected String typeName;

	public DeviceInfoTile(TileEntity tile) {
		this.tile = tile;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getDisplayWorkName() {
		if (typeName == null) {
			try {
				IBlockState state = tile.getWorld().getBlockState(tile.getPos());
				typeName = state.getBlock().getTranslationKey() + ".name";
			} catch (Exception e) {
				typeName = "";
			}
		}
		return I18n.format(typeName);
	}

}

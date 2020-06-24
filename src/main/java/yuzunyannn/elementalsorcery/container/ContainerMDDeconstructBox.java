package yuzunyannn.elementalsorcery.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import yuzunyannn.elementalsorcery.tile.md.TileMDDeconstructBox;

public class ContainerMDDeconstructBox extends ContainerMDBase<TileMDDeconstructBox> {

	public ContainerMDDeconstructBox(EntityPlayer player, TileEntity tileEntity) {
		super(player, (TileMDDeconstructBox) tileEntity);
		this.addSlotItemHandler(EnumFacing.NORTH, 0, 56, 31);
		this.addSlotItemHandler(EnumFacing.NORTH, 1, 105, 31);
	}
}

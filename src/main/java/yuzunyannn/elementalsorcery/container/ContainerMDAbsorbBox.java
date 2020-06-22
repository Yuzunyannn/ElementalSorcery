package yuzunyannn.elementalsorcery.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import yuzunyannn.elementalsorcery.tile.md.TileMDAbsorbBox;

public class ContainerMDAbsorbBox extends ContainerMDBase<TileMDAbsorbBox> {

	public ContainerMDAbsorbBox(EntityPlayer player, TileEntity tileEntity) {
		super(player, (TileMDAbsorbBox) tileEntity);
		this.addSlotItemHandler(EnumFacing.NORTH, 0, 80, 31);
	}
}

package yuzunyannn.elementalsorcery.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import yuzunyannn.elementalsorcery.tile.md.TileMDMagiclization;

public class ContainerMDMagiclization extends ContainerMDBase<TileMDMagiclization> {

	public ContainerMDMagiclization(EntityPlayer player, TileEntity tileEntity) {
		super(player, (TileMDMagiclization) tileEntity);
		this.addSlotItemHandler(EnumFacing.NORTH, 0, 80, 25);
	}
}

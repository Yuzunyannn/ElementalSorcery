package yuzunyannn.elementalsorcery.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import yuzunyannn.elementalsorcery.tile.md.TileMDInfusion;

public class ContainerMDInfusion extends ContainerMDBase<TileMDInfusion> {

	public ContainerMDInfusion(EntityPlayer player, TileEntity tileEntity) {
		super(player, (TileMDInfusion) tileEntity);
		this.addSlotItemHandler(EnumFacing.NORTH, 0, 36, 42);
		this.addSlotItemHandler(EnumFacing.NORTH, 1, 58, 50);
		this.addSlotItemHandler(EnumFacing.NORTH, 2, 80, 58);
		this.addSlotItemHandler(EnumFacing.NORTH, 3, 102, 50);
		this.addSlotItemHandler(EnumFacing.NORTH, 4, 124, 42);
	}
}

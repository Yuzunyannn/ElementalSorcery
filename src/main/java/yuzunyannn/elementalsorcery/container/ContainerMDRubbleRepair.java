package yuzunyannn.elementalsorcery.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import yuzunyannn.elementalsorcery.tile.md.TileMDRubbleRepair;

public class ContainerMDRubbleRepair extends ContainerMDBase<TileMDRubbleRepair> {
	
	public ContainerMDRubbleRepair(EntityPlayer player, TileEntity tileEntity) {
		super(player, (TileMDRubbleRepair) tileEntity);
		this.addSlotItemHandler(EnumFacing.NORTH, 0, 80, 31);
	}
}

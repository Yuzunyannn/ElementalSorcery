package yuzunyannn.elementalsorcery.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import yuzunyannn.elementalsorcery.tile.md.TileMDMagicGen;

public class ContainerMDMagicGen extends ContainerMDBase<TileMDMagicGen> {

	public ContainerMDMagicGen(EntityPlayer player, TileEntity tileEntity) {
		super(player, (TileMDMagicGen) tileEntity);
		this.addSlotItemHandler(EnumFacing.NORTH, 0, 80, 61);
	}

}

package yuzunyannn.elementalsorcery.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import yuzunyannn.elementalsorcery.tile.md.TileMDMagicSolidify;

public class ContainerMDMagicSolidify extends ContainerMDBase<TileMDMagicSolidify> {

	public ContainerMDMagicSolidify(EntityPlayer player, TileEntity tileEntity) {
		super(player, (TileMDMagicSolidify) tileEntity);
		this.addSlotItemHandler(EnumFacing.NORTH, 0, 80, 32);
	}

}

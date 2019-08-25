package yuzunyannn.elementalsorcery.block.md;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.container.ESGuiHandler;
import yuzunyannn.elementalsorcery.tile.md.TileMDRubbleRepair;

public class BlockMDRubbleRepair extends BlockMDBase {

	public BlockMDRubbleRepair() {
		super("MDRubbleRepair");
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileMDRubbleRepair();
	}

	@Override
	protected int guiId() {
		return ESGuiHandler.GUI_MD_RUBBLE_REPAIR;
	}

}

package yuzunyannn.elementalsorcery.block.md;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.container.ESGuiHandler;
import yuzunyannn.elementalsorcery.tile.md.TileMDInfusion;

public class BlockMDInfusion extends BlockMDBase {

	public BlockMDInfusion() {
		super("MDInfusion");
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileMDInfusion();
	}

	@Override
	protected int guiId() {
		return ESGuiHandler.GUI_MD_INFUSION;
	}

}

package yuzunyannn.elementalsorcery.block.md;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.container.ESGuiHandler;
import yuzunyannn.elementalsorcery.tile.md.TileMDDeconstructBox;

public class BlockMDDeconstructBox extends BlockMDBase {

	public BlockMDDeconstructBox() {
		super("MDDeconstructBox");
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileMDDeconstructBox();
	}

	@Override
	protected int guiId() {
		return ESGuiHandler.GUI_MD_DECONSTRUCTBOX;
	}
}

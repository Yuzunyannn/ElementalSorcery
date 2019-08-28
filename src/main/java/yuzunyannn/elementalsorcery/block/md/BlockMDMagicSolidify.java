package yuzunyannn.elementalsorcery.block.md;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.container.ESGuiHandler;
import yuzunyannn.elementalsorcery.tile.md.TileMDMagicSolidify;

public class BlockMDMagicSolidify extends BlockMDBase {

	public BlockMDMagicSolidify() {
		super("MDMagicSolidify");
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileMDMagicSolidify();
	}

	@Override
	protected int guiId() {
		return ESGuiHandler.GUI_MD_MAGIC_SOLIDIFY;
	}
}

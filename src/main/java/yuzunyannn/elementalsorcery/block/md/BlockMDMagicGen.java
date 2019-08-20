package yuzunyannn.elementalsorcery.block.md;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.container.ESGuiHandler;
import yuzunyannn.elementalsorcery.tile.md.TileMDMagicGen;

public class BlockMDMagicGen extends BlockMDBase {

	public BlockMDMagicGen() {
		super("magicGen");
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileMDMagicGen();
	}

	@Override
	protected int guiId() {
		return ESGuiHandler.GUI_MD_MAGIC_GEN;
	}
}

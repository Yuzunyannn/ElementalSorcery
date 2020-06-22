package yuzunyannn.elementalsorcery.block.md;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.container.ESGuiHandler;
import yuzunyannn.elementalsorcery.tile.md.TileMDMagiclization;

public class BlockMDMagiclization extends BlockMDBase {

	public BlockMDMagiclization() {
		super("MDMagiclization");
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileMDMagiclization();
	}

	@Override
	protected int guiId() {
		return ESGuiHandler.GUI_MD_MAGICLIZATION;
	}
}

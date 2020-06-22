package yuzunyannn.elementalsorcery.block.md;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import scala.reflect.runtime.ReflectionUtils;
import yuzunyannn.elementalsorcery.container.ESGuiHandler;
import yuzunyannn.elementalsorcery.tile.md.TileMDAbsorbBox;

public class BlockMDAbsorbBox extends BlockMDBase {

	public BlockMDAbsorbBox() {
		super("MDAbsorbBox");
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileMDAbsorbBox();
	}

	@Override
	protected int guiId() {
		return ESGuiHandler.GUI_MD_ABSORB_BOX;
	}
}

package yuzunyannn.elementalsorcery.block.altar;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.tile.altar.TileDeconstructAltarTableAdv;

public class BlockDeconstructAltarTableAdv extends BlockDeconstructAltarTable {
	public BlockDeconstructAltarTableAdv() {
		super(Material.ROCK, "deconstructAltarTableAdv", 5.5F);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileDeconstructAltarTableAdv();
	}
}

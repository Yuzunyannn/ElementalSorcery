package yuzunyannn.elementalsorcery.block.device;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.tile.device.TileMantraEmitter;

public class BlockMantraEmitter extends BlockDevice {

	public BlockMantraEmitter() {
		super(Material.ROCK, "mantraEmitter", 7.5F, MapColor.QUARTZ);
		this.setHarvestLevel("pickaxe", 2);
		autoDrop = true;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileMantraEmitter();
	}

}

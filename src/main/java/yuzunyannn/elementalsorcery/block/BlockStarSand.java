package yuzunyannn.elementalsorcery.block;

import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;

public class BlockStarSand extends BlockFalling {

	public BlockStarSand() {
		super(Material.SAND);
		this.setUnlocalizedName("starSand");
		this.setHarvestLevel("shovel", 0);
		this.setHardness(1.0f);
	}

}

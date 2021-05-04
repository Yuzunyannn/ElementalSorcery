package yuzunyannn.elementalsorcery.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class BlockCrudeQuartz extends Block {

	public BlockCrudeQuartz() {
		super(Material.ROCK);

		this.setUnlocalizedName("crudeQuartz");
		this.setHarvestLevel("pickaxe", 1);
		this.setSoundType(SoundType.STONE);
		this.setHardness(0.7F);
	}
}

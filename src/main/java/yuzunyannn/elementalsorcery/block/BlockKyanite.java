package yuzunyannn.elementalsorcery.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockKyanite extends Block {
	public BlockKyanite() {
		super(Material.ROCK);
		this.setHarvestLevel("pickaxe", 2);
		this.setUnlocalizedName("kyaniteBlock");
		this.setHardness(12.5F);
	}

	public static class BlockKyaniteOre extends Block {
		public BlockKyaniteOre() {
			super(Material.ROCK);
			this.setUnlocalizedName("kyaniteOre");
			this.setHarvestLevel("pickaxe", 2);
			this.setHardness(9.5F);
		}
	}
}

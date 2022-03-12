package yuzunyannn.elementalsorcery.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockKyanite extends Block {

	public BlockKyanite() {
		super(Material.ROCK);
		this.setHarvestLevel("pickaxe", 2);
		this.setTranslationKey("kyaniteBlock");
		this.setHardness(8);
	}

	public static class BlockKyaniteOre extends Block {
		public BlockKyaniteOre() {
			super(Material.ROCK);
			this.setTranslationKey("kyaniteOre");
			this.setHarvestLevel("pickaxe", 2);
			this.setHardness(4.5F);
		}
	}
}

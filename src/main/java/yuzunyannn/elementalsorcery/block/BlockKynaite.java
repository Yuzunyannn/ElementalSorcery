package yuzunyannn.elementalsorcery.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockKynaite extends Block {
	public BlockKynaite() {
		super(Material.ROCK);
		this.setHarvestLevel("pickaxe", 2);
		this.setUnlocalizedName("kynaiteBlock");
		this.setHardness(12.5F);
	}

	public static class BlockKynaiteOre extends Block {
		public BlockKynaiteOre() {
			super(Material.ROCK);
			this.setUnlocalizedName("kynaiteOre");
			this.setHarvestLevel("pickaxe", 2);
			this.setHardness(9.5F);
		}
	}
}

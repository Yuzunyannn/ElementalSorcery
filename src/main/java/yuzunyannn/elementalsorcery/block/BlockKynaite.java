package yuzunyannn.elementalsorcery.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.ESCreativeTabs;

public class BlockKynaite extends Block {
	public BlockKynaite() {
		super(Material.ROCK);
		this.setHarvestLevel("pickaxe", 2);
		this.setUnlocalizedName("kynaiteBlock");
		this.setHardness(12.5F);
	}

	// ��Ӧ�Ŀ���
	public static class BlockKynaiteOre extends Block {
		public BlockKynaiteOre() {
			super(Material.ROCK);
			this.setUnlocalizedName("kynaiteOre");
			this.setHarvestLevel("pickaxe", 2);
			this.setHardness(9.5F);
		}
	}
}

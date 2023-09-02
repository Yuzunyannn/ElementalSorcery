package yuzunyannn.elementalsorcery.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.util.IWorldObject;
import yuzunyannn.elementalsorcery.item.IItemStronger;
import yuzunyannn.elementalsorcery.util.helper.RandomHelper;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

public class BlockScarletCrystalOre extends Block {

	public BlockScarletCrystalOre() {
		super(Material.ROCK);
		this.setHarvestLevel("pickaxe", 2);
		this.setHardness(2F);
		this.setTranslationKey("scarletCrystalOre");
		this.setLightLevel(0.25f);
	}

	@Override
	public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
		if (entityIn instanceof EntityLivingBase) entityIn.setFire(2);
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return ESObjects.ITEMS.SCARLET_CRYSTAL;
	}

	@Override
	public int getExpDrop(IBlockState state, IBlockAccess world, BlockPos pos, int fortune) {
		Random rand = world instanceof World ? ((World) world).rand : RandomHelper.rand;
		return MathHelper.getInt(rand, 0, 8);
	}

	@Override
	public int quantityDroppedWithBonus(int fortune, Random random) {
		int i = random.nextInt(fortune + 3);
		i = MathHelper.clamp(i, 0, 8);
		return i + 2;
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state,
			int fortune) {
		super.getDrops(drops, world, pos, state, fortune);
		IWorldObject wo = IWorldObject.of(world, pos);
		for (ItemStack stack : drops) {
			IItemStronger stronger = ItemHelper.getItemStronger(stack);
			if (stronger != null) stronger.onProduced(stack, wo);
		}
	}

}

package yuzunyannn.elementalsorcery.element;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.element.IElementSpell;

public class ElementWater extends ElementInner {
	public ElementWater() {
		super(0x6472f7, "water");
	}

	@Override
	public int spellBegin(World world, EntityLivingBase entity, ElementStack estack, SpellPackage pack) {
		return IElementSpell.SPELL_ONCE | IElementSpell.NEED_BLOCK;
	}

	@Override
	public void spellEnd(World world, EntityLivingBase entity, ElementStack estack, SpellPackage pack) {
		if (world.isRemote)
			return;
		if (pack.isFail())
			return;
		if (pack.pos != null) {
			BlockPos pos = pack.pos.offset(pack.face);
			if (world.getBlockState(pos).getBlock().isReplaceable(world, pos)) {
				// 如果水会被蒸发
				if (world.provider.doesWaterVaporize()) {
					int l = pos.getX();
					int i = pos.getY();
					int j = pos.getZ();
					world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F,
							2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);
					for (int k = 0; k < 8; ++k) {
						world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, (double) l + Math.random(),
								(double) i + Math.random(), (double) j + Math.random(), 0.0D, 0.0D, 0.0D);
					}
				} else {
					if (!world.isAirBlock(pos)) {
						world.destroyBlock(pos, true);
					}
					world.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
					world.setBlockState(pos, Blocks.WATER.getDefaultState(), 11);
					world.neighborChanged(pos, Blocks.WATER, pos);
				}
			}
		}
	}

	@Override
	public int cast(ElementStack estack, int level) {
		return 5;
	}

	@Override
	public int cost(ElementStack estack, int level) {
		return 20;
	}

	@Override
	public int lowestPower(ElementStack estack, int level) {
		return 10;
	}

}
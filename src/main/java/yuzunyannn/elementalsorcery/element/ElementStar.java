package yuzunyannn.elementalsorcery.element;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.element.explosion.EEStar;
import yuzunyannn.elementalsorcery.element.explosion.ElementExplosion;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.util.element.DrinkJuiceEffectAdder;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;
import yuzunyannn.elementalsorcery.world.Juice.JuiceMaterial;

public class ElementStar extends ElementCommon {

	public ElementStar() {
		super(0xcaf4ff, "star");
	}

	@Override
	public int complexWith(ItemStack stack, ElementStack estack, ElementStack other) {
		return 8;
	}

	@Override
	public ElementStack starFlowerCasting(World world, BlockPos pos, ElementStack estack, int tick) {
		if (world.isRemote) return estack;
		if (tick % 100 != 0) return estack;

		if (world.rand.nextFloat() < 0.05f) {
			if (estack.getCount() > 50) {
				estack.shrink(40);
				ItemStack star = new ItemStack(ESInit.BLOCKS.STAR_FLOWER, 1);
				ItemHelper.dropItem(world, pos, star);
			}
		}

		int range = getStarFlowerRange(estack);
		BlockPos at = BlockHelper.tryFind(world, (w, p) -> {
			IBlockState state = w.getBlockState(p);
			return state.getBlock() == Blocks.SAND;
		}, pos, Math.min(estack.getPower() / 100, 8), range, range);

		estack.shrink(1);
		if (at == null) return estack;

		estack.shrink(10);
		world.setBlockState(at, ESInit.BLOCKS.STAR_SAND.getDefaultState());
		world.playEvent(2001, at, Block.getStateId(world.getBlockState(at)));

		return estack;
	}

	@Override
	public ElementExplosion newExplosion(World world, Vec3d pos, ElementStack eStack, EntityLivingBase attacker) {
		return new EEStar(world, pos, ElementExplosion.getStrength(eStack) + 0.5f, eStack);
	}

	@Override
	protected void addDrinkJuiceEffect(DrinkJuiceEffectAdder helper) {
		helper.preparatory(ESInit.POTIONS.STAR, 32, 256);
		helper.check(JuiceMaterial.APPLE, 512).checkRatio(JuiceMaterial.MELON, 0.95f, 1.05f).join();

		helper.preparatory(MobEffects.LUCK, 32, 128);
		helper.check(JuiceMaterial.ELF_FRUIT, 100).join();

	}
}

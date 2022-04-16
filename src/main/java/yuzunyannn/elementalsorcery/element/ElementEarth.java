package yuzunyannn.elementalsorcery.element;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.element.explosion.EEEarth;
import yuzunyannn.elementalsorcery.element.explosion.ElementExplosion;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.util.element.DrinkJuiceEffectAdder;
import yuzunyannn.elementalsorcery.world.JuiceMaterial;

public class ElementEarth extends ElementCommon {

	public ElementEarth() {
		super(0x785439, "earth");
		setTransition(2.5f, 22.5f, 120);
	}

	@Override
	public int complexWith(ItemStack stack, ElementStack estack, ElementStack other) {
		if (other.getElement() == ESInit.ELEMENTS.METAL) return 2;
		return super.complexWith(stack, estack, other);
	}

	@Override
	public ElementStack starFlowerCasting(World world, BlockPos pos, ElementStack estack, int tick) {
		if (world.isRemote) return estack;
		int power = estack.getPower();
		int n = (int) Math.max((1 - Math.min(1, power / 1000f)) * 20, 2);
		if (tick % n != 0) return estack;

		NBTTagCompound data = estack.getOrCreateTagCompound();
		int range = getStarFlowerRange(estack);
		int size = range * 2 + 1;
		int t = data.getInteger("t");
		int num = t % (size * size * range);
		data.setInteger("t", num + 1);

		int x = num % size - range;
		int z = (num / size) % size - range;
		int y = -(num / (size * size)) % range - 1;

		BlockPos at = pos.add(x, y, z);
		if (world.isAirBlock(at)) {
			world.setBlockState(at, Blocks.DIRT.getDefaultState());
			world.playEvent(2001, at, Block.getStateId(Blocks.DIRT.getDefaultState()));
			if (t % 2 == 0) estack.shrink(1);
		}

		return estack;
	}

	@Override
	public ElementExplosion newExplosion(World world, Vec3d pos, ElementStack eStack, EntityLivingBase attacker) {
		return new EEEarth(world, pos, ElementExplosion.getStrength(eStack), eStack);
	}

	@Override
	protected void addDrinkJuiceEffect(DrinkJuiceEffectAdder helper) {

		helper.preparatory(ESInit.POTIONS.POWER_PITCHER, 25, 100);
		helper.check(JuiceMaterial.APPLE, 150).checkRatio(JuiceMaterial.ELF_FRUIT, 0.4f, 0.6f).join();
		helper.descend(JuiceMaterial.ELF_FRUIT, 50, 0.8f);

		helper.preparatory(MobEffects.STRENGTH, 11, 40);
		helper.check(JuiceMaterial.ELF_FRUIT, 50).join();
		helper.descend(JuiceMaterial.ELF_FRUIT, 10, 0.9f);

		helper.preparatory(MobEffects.HASTE, 11, 60);
		helper.check(JuiceMaterial.ELF_FRUIT, 50).join();
		helper.descend(JuiceMaterial.ELF_FRUIT, 10, 0.9f);

		helper.preparatory(ESInit.POTIONS.POUND_WALKER, 32, 85);
		helper.check(JuiceMaterial.MELON, 75).join();

	}
}

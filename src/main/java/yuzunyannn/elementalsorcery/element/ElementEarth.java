package yuzunyannn.elementalsorcery.element;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.element.JuiceMaterial;
import yuzunyannn.elementalsorcery.api.mantra.SilentLevel;
import yuzunyannn.elementalsorcery.api.util.target.IWorldObject;
import yuzunyannn.elementalsorcery.api.util.target.WorldTarget;
import yuzunyannn.elementalsorcery.api.util.var.VariableSet;
import yuzunyannn.elementalsorcery.element.explosion.EEEarth;
import yuzunyannn.elementalsorcery.element.explosion.EEOnSilent;
import yuzunyannn.elementalsorcery.element.explosion.ElementExplosion;
import yuzunyannn.elementalsorcery.util.element.DrinkJuiceEffectAdder;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;
import yuzunyannn.elementalsorcery.util.helper.DamageHelper;

public class ElementEarth extends ElementCommon {

	public ElementEarth() {
		super(0x785439, "earth");
		setTransition(2.5f, 22.5f, 120);
		setLaserCostOnce(1, 5);
	}

	@Override
	public int complexWith(ItemStack stack, ElementStack estack, ElementStack other) {
		if (other.getElement() == ESObjects.ELEMENTS.METAL) return 2;
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
		if (ESAPI.silent.isSilent(world, pos, SilentLevel.PHENOMENON))
			return new EEOnSilent(world, pos, ElementExplosion.getStrength(eStack), eStack);
		return new EEEarth(world, pos, ElementExplosion.getStrength(eStack), eStack);
	}

	@Override
	protected void addDrinkJuiceEffect(DrinkJuiceEffectAdder helper) {

		helper.preparatory(ESObjects.POTIONS.POWER_PITCHER, 25, 100);
		helper.check(JuiceMaterial.APPLE, 150).checkRatio(JuiceMaterial.ELF_FRUIT, 0.3f, 0.6f).join();
		helper.descend(JuiceMaterial.ELF_FRUIT, 50, 0.8f);

		helper.preparatory(MobEffects.STRENGTH, 11, 40);
		helper.check(JuiceMaterial.ELF_FRUIT, 50).join();
		helper.descend(JuiceMaterial.ELF_FRUIT, 10, 0.9f);

		helper.preparatory(MobEffects.HASTE, 11, 60);
		helper.check(JuiceMaterial.ELF_FRUIT, 50).join();
		helper.descend(JuiceMaterial.ELF_FRUIT, 10, 0.9f);

		helper.preparatory(ESObjects.POTIONS.POUND_WALKER, 32, 85);
		helper.check(JuiceMaterial.MELON, 75).join();

	}

	@Override
	protected void onExecuteLaser(World world, IWorldObject caster, WorldTarget target, ElementStack storage,
			VariableSet content) {
		if (world.isRemote) return;

		Entity entity = target.getEntity();
		BlockPos pos;
		if (entity != null) {
			float dmg = MathHelper.sqrt(storage.getPower()) / 16;
			DamageSource ds = DamageHelper.getDamageSource(storage, caster.asEntityLivingBase(), null);
			entity.attackEntityFrom(ds, dmg);
			pos = new BlockPos(entity.posX, entity.posY - 0.5, entity.posZ);
		} else pos = target.getPos();
		if (BlockHelper.isBedrock(world, pos)) return;
		if (BlockHelper.isFluid(world, pos)) return;
		IBlockState state = world.getBlockState(pos);
		if (state.getBlockHardness(world, pos) < Math.max(1, storage.getPower() / 8f))
			BlockHelper.destroyBlock(world, pos, true, 0);
	}
}

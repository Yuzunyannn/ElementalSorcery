package yuzunyannn.elementalsorcery.element;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.util.IWorldObject;
import yuzunyannn.elementalsorcery.api.util.WorldTarget;
import yuzunyannn.elementalsorcery.block.BlockLifeFlower;
import yuzunyannn.elementalsorcery.element.explosion.EEWood;
import yuzunyannn.elementalsorcery.element.explosion.ElementExplosion;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.util.element.DrinkJuiceEffectAdder;
import yuzunyannn.elementalsorcery.util.var.VariableSet;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;
import yuzunyannn.elementalsorcery.world.JuiceMaterial;

public class ElementWood extends ElementCommon {

	public ElementWood() {
		super(0x32CD32, "wood");
		setTransition(2.5f, 112.5f, 60);
		setLaserCostOnce(1, 100);
	}

	@Override
	public ElementStack starFlowerCasting(World world, BlockPos pos, ElementStack estack, int tick) {
		if (world.isRemote) return estack;

		int power = estack.getPower();
		int n = (int) Math.max((1 - Math.min(1, power / 1000f)) * 30, 4);
		if (tick % n != 0) return estack;

		int range = getStarFlowerRange(estack);
		int x = world.rand.nextInt(range * 2 + 1) - range;
		int z = world.rand.nextInt(range * 2 + 1) - range;
		BlockPos at = pos.add(x, 3, z);
		for (int i = 0; i < 6; i++) {
			if (!world.isAirBlock(at) || at.getY() <= 1) break;
			at = at.down();
		}
		IBlockState state = world.getBlockState(at);
		if (state.getBlock() instanceof IGrowable) {
			ItemDye.applyBonemeal(new ItemStack(Items.DYE), world, at);
			estack.shrink(1);
		}

		if (world.rand.nextInt(n) == 0) {
			AxisAlignedBB aabb = WorldHelper.createAABB(pos, range, range, 2);
			List<EntityPlayer> entities = world.getEntitiesWithinAABB(EntityPlayer.class, aabb);
			for (EntityPlayer entity : entities) {
				entity.getFoodStats().addStats(1, 0.25f);
			}
			if (!entities.isEmpty()) estack.shrink(entities.size());
		}

		return estack;
	}

	@Override
	public ElementExplosion newExplosion(World world, Vec3d pos, ElementStack eStack, EntityLivingBase attacker) {
		return new EEWood(world, pos, ElementExplosion.getStrength(eStack) * 1.05f, eStack);
	}

	@Override
	protected void addDrinkJuiceEffect(DrinkJuiceEffectAdder helper) {

		helper.preparatory(ESInit.POTIONS.VERDANT_WALKER, 40, 125);
		helper.check(JuiceMaterial.ELF_FRUIT, 75).checkRatio(JuiceMaterial.APPLE, 0.25f, 0.75f).join();
		helper.descend(JuiceMaterial.APPLE, 0, 0.8f);

		helper.preparatory(ESInit.POTIONS.HEALTH_BALANCE, 40, 125);
		helper.check(JuiceMaterial.ELF_FRUIT, 75).checkRatio(JuiceMaterial.MELON, 0.25f, 0.75f).join();
		helper.descend(JuiceMaterial.MELON, 0, 0.8f);

		helper.preparatory(MobEffects.REGENERATION, 10, 250);
		helper.check(JuiceMaterial.APPLE, 50).join();

		helper.preparatory(MobEffects.HEALTH_BOOST, 30, 150);
		helper.check(JuiceMaterial.MELON, 75).join();

	}

	@Override
	protected void onExecuteLaser(World world, IWorldObject caster, WorldTarget target, ElementStack storage,
			VariableSet content) {
		if (world.isRemote) return;

		Entity entity = target.getEntity();
		if (entity != null) {
			if (entity instanceof EntityLivingBase) {
				EntityLivingBase living = (EntityLivingBase) entity;
				living.heal(MathHelper.sqrt(storage.getPower()) / 16f + 0.125f);
			}
			return;
		}

		BlockPos pos = target.getPos();
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		float p = 0.1f + MathHelper.sqrt(storage.getPower()) / 32f;
		if (p < rand.nextFloat()) return;

		if (block instanceof BlockLifeFlower) {
			p = 32f / (32f + p);
			if (p < rand.nextFloat()) world.scheduleUpdate(pos, block, 0);
		} else if (block instanceof IGrowable) {
			IGrowable grow = (IGrowable) block;
			if (grow.canUseBonemeal(world, rand, pos, state)) {
				if (grow.canGrow(world, pos, state, world.isRemote)) {
					grow.grow(world, rand, pos, state);
				}
			}
		}

	}
}

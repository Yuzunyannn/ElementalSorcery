package yuzunyannn.elementalsorcery.element;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockTNT;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.element.JuiceMaterial;
import yuzunyannn.elementalsorcery.api.mantra.SilentLevel;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.api.util.IWorldObject;
import yuzunyannn.elementalsorcery.api.util.WorldTarget;
import yuzunyannn.elementalsorcery.api.util.var.VariableSet;
import yuzunyannn.elementalsorcery.block.altar.BlockElementContainer;
import yuzunyannn.elementalsorcery.element.explosion.EEFire;
import yuzunyannn.elementalsorcery.element.explosion.EEOnSilent;
import yuzunyannn.elementalsorcery.element.explosion.ElementExplosion;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraFireArea;
import yuzunyannn.elementalsorcery.util.element.DrinkJuiceEffectAdder;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;
import yuzunyannn.elementalsorcery.util.helper.DamageHelper;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class ElementFire extends ElementCommon {

	public static final int COLOR = 0xff9902;

	public ElementFire() {
		super(COLOR, "fire");
		setTransition(2, 0, 180);
		setLaserCostOnce(1, 75);
	}

	@Override
	public ElementStack starFlowerCasting(World world, BlockPos pos, ElementStack estack, int tick) {
		if (tick % 20 != 0) return estack;
		int range = getStarFlowerRange(estack);
		AxisAlignedBB aabb = WorldHelper.createAABB(pos, range, range, 2);
		int power = estack.getPower();
		float addDamage = power > 25 ? MathHelper.sqrt((power - 25) / 50) : 0;
		List<Entity> entities = world.getEntitiesWithinAABB(EntityMob.class, aabb);
		for (Entity entity : entities) {
			entity.setFire(3);
			if (addDamage > 0) entity.attackEntityFrom(DamageSource.IN_FIRE, addDamage);
			if (world.isRemote)
				MantraFireArea.addEffect(world, entity.getPositionVector().add(0, entity.height / 2, 0));
		}
		if (entities.isEmpty()) return estack;
		estack.shrink(Math.max(1, entities.size() / 5));
		return estack;
	}

	@Override
	public ElementExplosion newExplosion(World world, Vec3d pos, ElementStack eStack, EntityLivingBase attacker) {
		if (ESAPI.silent.isSilent(world, pos, SilentLevel.PHENOMENON))
			return new EEOnSilent(world, pos, ElementExplosion.getStrength(eStack), eStack);
		return new EEFire(world, pos, ElementExplosion.getStrength(eStack), eStack);
	}

	@Override
	protected void addDrinkJuiceEffect(DrinkJuiceEffectAdder helper) {

		helper.preparatory(MobEffects.STRENGTH, 35, 80);
		helper.check(JuiceMaterial.APPLE, 100).join();
		helper.descend(JuiceMaterial.APPLE, 40, 0.9f);

		helper.preparatory(ESObjects.POTIONS.REBIRTH_FROM_FIRE, 45, 100);
		helper.check(JuiceMaterial.APPLE, 240).checkRatio(JuiceMaterial.MELON, 0.25f, 0.5f).join();
		helper.descend(JuiceMaterial.MELON, 30, 1);

		helper.preparatory(ESObjects.POTIONS.FIRE_WALKER, 20, 60);
		helper.check(JuiceMaterial.MELON, 60).join();
		helper.descend(JuiceMaterial.MELON, 10, 0.9f);
	}

	@Override
	public void onExecuteLaser(World world, IWorldObject caster, WorldTarget target, ElementStack storage,
			VariableSet content) {

		Entity entity = target.getEntity();
		if (entity != null) {
			if (world.isRemote) return;
			entity.setFire((int) (20 * MathHelper.sqrt(storage.getPower())));
			float dmg = MathHelper.sqrt(storage.getPower()) / 4;
			DamageSource ds = DamageHelper.getDamageSource(storage, caster.asEntityLivingBase(), null);
			entity.attackEntityFrom(ds, dmg);
			return;
		}

		BlockPos pos = target.getPos();
		if (BlockHelper.isBedrock(world, pos)) return;
		EnumFacing facing = target.getFace();
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();

		if (BlockHelper.isFluid(world, pos)) {
			if (block == Blocks.WATER || block == Blocks.FLOWING_WATER) {
				if (!world.isRemote) world.setBlockToAir(pos);
				else {
					Vec3d vec = new Vec3d(pos);
					for (int k = 0; k < 8; ++k) world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL,
							vec.x + Math.random(), vec.y + Math.random(), vec.z + Math.random(), 0.0D, 0.0D, 0.0D);
				}
			}
			return;
		}

		if (world.isRemote) return;

		if (block instanceof BlockElementContainer) {
			TileEntity tileEntity = world.getTileEntity(pos);
			IElementInventory eInv = ElementHelper.getElementInventory(tileEntity);
			if (eInv != null && !ElementHelper.isEmpty(eInv)) {
				world.setBlockToAir(pos);
				BlockElementContainer.doExploded(world, pos, eInv, caster.asEntityLivingBase());
				try {
					block.dropBlockAsItemWithChance(world, pos, state, 0.75f, 0);
				} catch (Exception e) {}
				return;
			}
		}

		if (block instanceof BlockTNT) {
			((BlockTNT) block).explode(world, pos, state.withProperty(BlockTNT.EXPLODE, true),
					caster.asEntityLivingBase());
			return;
		}

		NonNullList<ItemStack> list = NonNullList.create();
		block.getDrops(list, world, pos, state, 0);
		boolean isDrop = false;
		List<ItemStack> dropList = new ArrayList<>(list.size());
		for (ItemStack stack : list) {
			ItemStack result = FurnaceRecipes.instance().getSmeltingResult(stack);
			if (result.isEmpty()) {
				dropList.add(stack);
				continue;
			}
			isDrop = true;
			result = result.copy();
			result.setCount(result.getCount() * stack.getCount());
			dropList.add(result);
		}

		if (isDrop) {
			for (ItemStack stack : dropList) ItemHelper.dropItem(world, pos, stack);
			world.setBlockToAir(pos);
			return;
		}

		BlockPos at = pos.offset(facing);
		if (world.isAirBlock(at)) {
			world.setBlockState(at, Blocks.FIRE.getDefaultState());
			world.notifyBlockUpdate(pos, Blocks.AIR.getDefaultState(), Blocks.FIRE.getDefaultState(), 0);
		}

	}

}

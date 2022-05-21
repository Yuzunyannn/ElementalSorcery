package yuzunyannn.elementalsorcery.element;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;
import yuzunyannn.elementalsorcery.api.util.IWorldObject;
import yuzunyannn.elementalsorcery.api.util.WorldTarget;
import yuzunyannn.elementalsorcery.element.explosion.EEWater;
import yuzunyannn.elementalsorcery.element.explosion.ElementExplosion;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.util.VariableSet;
import yuzunyannn.elementalsorcery.util.element.DrinkJuiceEffectAdder;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;
import yuzunyannn.elementalsorcery.world.JuiceMaterial;

public class ElementWater extends ElementCommon {

	public ElementWater() {
		super(0x6472f7, "water");
		setTransition(2, 180, 180);
		setLaserCostOnce(1, 5);
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
		int num = t % (size * size * 3);
		data.setInteger("t", num + 1);

		int x = num % size - range;
		int z = (num / size) % size - range;
		int y = -(num / (size * size)) % 3 - 1;

		BlockPos at = pos.add(x, y, z);
		if (!world.isAirBlock(at)) {
			IBlockState state = world.getBlockState(at);
			IBlockState newState = transform(state);
			if (newState != null) {
				world.setBlockState(at, newState);
				world.playEvent(2001, at, Block.getStateId(Blocks.WATER.getDefaultState()));
				estack.shrink(2);
			}
		}

		return estack;
	}

	public static IBlockState transform(IBlockState state) {
		Block block = state.getBlock();
		if (block == Blocks.FIRE) return Blocks.AIR.getDefaultState();
		if (block == Blocks.DIRT) return Blocks.GRASS.getDefaultState();
		if (block == Blocks.MAGMA) return Blocks.COBBLESTONE.getDefaultState();
		if (block == Blocks.CAULDRON) return Blocks.CAULDRON.getDefaultState().withProperty(BlockCauldron.LEVEL, 3);
		if (block == Blocks.WATER || block == Blocks.FLOWING_WATER) {
			if (state.getValue(BlockFluidBase.LEVEL) != 0) return Blocks.FLOWING_WATER.getDefaultState();
		}
		if (block == Blocks.LAVA || block == Blocks.FLOWING_LAVA) {
			if (state.getValue(BlockFluidBase.LEVEL) == 0) return Blocks.OBSIDIAN.getDefaultState();
			else return Blocks.COBBLESTONE.getDefaultState();
		}
		return null;
	}

	@Override
	public ElementExplosion newExplosion(World world, Vec3d pos, ElementStack eStack, EntityLivingBase attacker) {
		return new EEWater(world, pos, ElementExplosion.getStrength(eStack), eStack);
	}

	@Override
	protected void addDrinkJuiceEffect(DrinkJuiceEffectAdder helper) {

		helper.preparatory(MobEffects.RESISTANCE, 28, 135);
		helper.check(JuiceMaterial.ELF_FRUIT, 50).join();

		helper.preparatory(MobEffects.WATER_BREATHING, 32, 75);
		helper.check(JuiceMaterial.MELON, 75).join();
		helper.descend(JuiceMaterial.MELON, 20, 1);

		helper.preparatory(MobEffects.FIRE_RESISTANCE, 30, 0);
		helper.check(JuiceMaterial.MELON, 90).join();

		helper.preparatory(ESInit.POTIONS.TIDE_WALKER, 40, 100);
		helper.check(JuiceMaterial.MELON, 100).checkRatio(JuiceMaterial.APPLE, 1.25f, 2).join();
		helper.descend(JuiceMaterial.APPLE, 50, 0.8f).descend(JuiceMaterial.MELON, 40, 0.8f);

		helper.preparatory(ESInit.POTIONS.WATER_CALAMITY, 30, 80);
		helper.check(JuiceMaterial.MELON, 120).checkRatio(JuiceMaterial.APPLE, 0.5f, 1.5f).join();

	}

	@Override
	protected void onExecuteLaser(World world, IWorldObject caster, WorldTarget target, ElementStack storage,
			VariableSet content) {
		if (world.isRemote) return;

		Entity entity = target.getEntity();
		BlockPos pos;
		EnumFacing face = target.getFace();
		if (entity != null) {
			if (entity instanceof EntityLivingBase) {
				EntityLivingBase living = (EntityLivingBase) entity;
				living.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, storage.getPower() * 10,
						storage.getPower() > 200 ? 2 : 1));
			}
			pos = new BlockPos(entity.posX, entity.posY - 0.5, entity.posZ);
			face = EnumFacing.UP;
		} else pos = target.getPos();
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		if (block == Blocks.AIR) return;
		else if (block == Blocks.LAVA) world.setBlockState(pos, Blocks.OBSIDIAN.getDefaultState());
		else if (block == Blocks.FLOWING_LAVA) world.setBlockState(pos, Blocks.COBBLESTONE.getDefaultState());
		else if (block == Blocks.WATER) world.setBlockState(pos, Blocks.ICE.getDefaultState());
		else if (block == Blocks.ICE);
		else if (!world.provider.doesWaterVaporize()) {
			BlockPos at = pos.offset(face);
			if (BlockHelper.isReplaceBlock(world, at)) {
				IBlockState waterState = Blocks.FLOWING_WATER.getDefaultState().withProperty(BlockFluidBase.LEVEL, 8);
				world.setBlockState(at, waterState);
				waterState = Blocks.FLOWING_WATER.getDefaultState().withProperty(BlockFluidBase.LEVEL, 1);
				for (EnumFacing facing : EnumFacing.HORIZONTALS) {
					BlockPos dAt = at.offset(facing);
					if (BlockHelper.isReplaceBlock(world, dAt)) world.setBlockState(dAt, waterState);
				}
			}
		}
	}

}

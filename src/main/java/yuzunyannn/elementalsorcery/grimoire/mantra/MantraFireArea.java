package yuzunyannn.elementalsorcery.grimoire.mantra;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.element.ElementKnowledge;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.grimoire.ICaster;
import yuzunyannn.elementalsorcery.grimoire.ICasterObject;
import yuzunyannn.elementalsorcery.grimoire.MantraDataCommon.ConditionEffect;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.item.prop.ItemQuill;
import yuzunyannn.elementalsorcery.render.effect.grimoire.EffectMagicSquare;
import yuzunyannn.elementalsorcery.util.helper.ColorHelper;
import yuzunyannn.elementalsorcery.util.helper.EntityHelper;

public class MantraFireArea extends MantraSquareAreaAdv {

	public MantraFireArea() {
		this.setTranslationKey("fireArea");
		this.setColor(0xee5a00);
		this.setIcon("fire_area");
		this.setRarity(60);
		this.setOccupation(3);
		this.addElementCollect(new ElementStack(ESInit.ELEMENTS.FIRE, 2, 40), 80, 20);
		this.addElementCollect(new ElementStack(ESInit.ELEMENTS.KNOWLEDGE, 1, 100), 20, -1);
		this.setPotentPowerCollect(0.1f, 2);
	}

	@Override
	public void potentAttack(World world, ItemStack grimoire, ICaster caster, Entity target) {
		super.potentAttack(world, grimoire, caster, target);
		target.setFire(200);
	}

	@Override
	public void init(World world, SquareData data, ICaster caster, BlockPos pos) {
		ElementStack fire = data.get(ESInit.ELEMENTS.FIRE);
		float rate = 0.6f * caster.iWantBePotent(0.75f, false) + 1;
		data.setSize(Math.min(fire.getPower() / 80, 12) * rate + 6);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addAfterEffect(SquareData data, ICaster caster, int size) {
		if (size <= 0) return;
		if (data.hasMarkEffect(1000)) return;
		ICasterObject co = caster.iWantDirectCaster();
		Entity entity = co.asEntity();
		if (entity == null) return;
		EffectMagicSquare ems = new EffectMagicSquare(entity.world, entity, size, this.getColor(data));
		ems.setCondition(new ConditionEffect(entity, data, 1000, false));
		data.addEffect(caster, ems, 1000);
		ems.setIcon(this.getMagicCircleIcon());
		List<Vec3d> vec = new ArrayList<>();
		vec.add(ColorHelper.color(getColor(data)));
		if (data.get(ESInit.ELEMENTS.KNOWLEDGE).getCount() >= 20) vec.add(ColorHelper.color(ElementKnowledge.COLOR));
		ems.effectColors = vec.toArray(new Vec3d[vec.size()]);
	}

	@Override
	public boolean tick(World world, SquareData data, ICaster caster, BlockPos originPos) {
		int tick = caster.iWantKnowCastTick();
		ElementStack fire = data.get(ESInit.ELEMENTS.FIRE);
		if (fire.isEmpty()) return false;
		if (tick % 20 != 0) return true;

		float pp = data.get(POTENT_POWER);

		ElementStack knowledge = data.get(ESInit.ELEMENTS.KNOWLEDGE);
		Random rand = world.rand;
		fire.shrink(8);

		Entity entityCaster = caster.iWantCaster().asEntity();

		final float size = data.getSize() / 2;
		AxisAlignedBB aabb = new AxisAlignedBB(originPos.getX() - size, originPos.getY(), originPos.getZ() - size,
				originPos.getX() + size, originPos.getY() + 3, originPos.getZ() + size);
		List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, aabb);
		for (Entity entity : entities) {
			if (!(entity instanceof EntityLivingBase) && !(entity instanceof EntityItem)) continue;
			if (knowledge.getCount() >= 20) {
				if (EntityHelper.isSameTeam(entityCaster, entity)) continue;
				if (entity instanceof EntityItem) continue;
			}
			entity.setFire(5);
			if (world.isRemote) addEffect(world, entity.getPositionVector().add(0, entity.height / 2, 0));
			if (fire.getPower() > 50) {
				float addDamage = MathHelper.sqrt((fire.getPower() - 50) / 25) * (1 + pp * 0.5f);
				entity.attackEntityFrom(DamageSource.IN_FIRE, addDamage);
			}
		}

		int times = rand.nextInt((int) Math.max(3, size / 2)) + 2;
		int l = (int) Math.max(1, size - 1);
		for (int i = 0; i < times; i++) {
			int x = rand.nextInt(l * 2) - l;
			int z = rand.nextInt(l * 2) - l;
			BlockPos at = originPos.add(x, 0, z);

			if (world.isRemote) addEffect(world, new Vec3d(at).add(0.5, 0.5, 0.5));

			if (world.isAirBlock(at)) {
				if (fire.getPower() > 250) {
					int level = rand.nextInt(10) + 5;
					if (!world.isRemote) {
						IBlockState LAVA = Blocks.FLOWING_LAVA.getDefaultState();
						LAVA = LAVA.withProperty(BlockLiquid.LEVEL, level);
						world.setBlockState(at, LAVA);
						for (EnumFacing facing : EnumFacing.HORIZONTALS) {
							if (world.isAirBlock(at.offset(facing))) world.setBlockState(at.offset(facing), LAVA);
						}
					}
				} else {
					world.setBlockState(at, Blocks.FIRE.getDefaultState());
				}
			} else {
				IBlockState state = world.getBlockState(at);
				if (state.getBlock() == Blocks.WATER) {

					world.setBlockToAir(at);
					ItemQuill.playFireExtinguish(world, at);

				} else if (state.getBlock() instanceof IPlantable) {
					if (!world.isRemote) {
						world.setBlockToAir(at);
						world.setBlockState(at, Blocks.FIRE.getDefaultState());
					}
				} else {
					if (!world.isRemote) {
						EnumFacing facing = EnumFacing.random(rand);
						int fb = state.getBlock().getFlammability(world, at, facing);
						if (fb > 0 && world.isAirBlock(at.offset(facing))) {
							world.setBlockState(at.offset(facing), Blocks.FIRE.getDefaultState());
						}
					}
				}
			}
		}

		return true;
	}

	@SideOnly(Side.CLIENT)
	public static void addEffect(World world, Vec3d center) {
		Random rand = world.rand;
		for (int i = 0; i < 10; i++) {
			Vec3d vec = new Vec3d(rand.nextGaussian(), rand.nextGaussian(), rand.nextGaussian()).normalize();
			vec = vec.scale(0.1);
			Vec3d at = center.add(vec.scale(0.5));
			world.spawnParticle(EnumParticleTypes.FLAME, at.x, at.y, at.z, vec.x, vec.y, vec.z);
		}
		world.playSound(center.x, center.y, center.z, SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.BLOCKS, 1, 1,
				true);
	}
}

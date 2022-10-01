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
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.mantra.ICaster;
import yuzunyannn.elementalsorcery.api.mantra.MantraEffectType;
import yuzunyannn.elementalsorcery.element.ElementKnowledge;
import yuzunyannn.elementalsorcery.item.prop.ItemQuill;
import yuzunyannn.elementalsorcery.render.effect.grimoire.EffectMagicSquare;
import yuzunyannn.elementalsorcery.util.helper.ColorHelper;

public class MantraFireArea extends MantraSquareAreaAdv {

	public MantraFireArea() {
		this.setTranslationKey("fireArea");
		this.setColor(0xee5a00);
		this.setIcon("fire_area");
		this.setRarity(60);
		this.setOccupation(3);
		this.addElementCollect(new ElementStack(ESObjects.ELEMENTS.FIRE, 2, 40), 80, 20);
		this.addElementCollect(new ElementStack(ESObjects.ELEMENTS.KNOWLEDGE, 1, 100), 20, -1);
		this.setPotentPowerCollect(0.1f, 2);
		this.initAndAddDefaultMantraLauncher(0.002);
	}

	@Override
	public void potentAttack(World world, ItemStack grimoire, ICaster caster, Entity target) {
		super.potentAttack(world, grimoire, caster, target);
		target.setFire(200);
	}

	@Override
	public void init(World world, SquareData data, ICaster caster, BlockPos pos) {
		ElementStack fire = data.get(ESObjects.ELEMENTS.FIRE);
		float rate = 0.6f * caster.iWantBePotent(0.75f, false) + 1;
		data.setSize(Math.min(fire.getPower() / 80, 12) * rate + 6);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addAfterEffect(SquareData data, ICaster caster, int size) {
		super.addAfterEffect(data, caster, size);
		EffectMagicSquare ems = data.getEffectMap().getMark(MantraEffectType.MANTRA_EFFECT_1, EffectMagicSquare.class);
		if (ems == null) return;
		List<Vec3d> vec = new ArrayList<>();
		vec.add(ColorHelper.color(getColor(data)));
		if (data.get(ESObjects.ELEMENTS.KNOWLEDGE).getCount() >= 20) vec.add(ColorHelper.color(ElementKnowledge.COLOR));
		ems.effectColors = vec.toArray(new Vec3d[vec.size()]);
	}

	@Override
	public boolean tick(World world, SquareData data, ICaster caster, BlockPos originPos) {
		int tick = caster.iWantKnowCastTick();
		ElementStack fire = data.get(ESObjects.ELEMENTS.FIRE);
		if (fire.isEmpty()) return false;

		float pp = data.get(POTENT_POWER);
		int preTick = pp >= 1 ? 20 : 30;
		if (tick % preTick != 0) return true;

		ElementStack knowledge = data.get(ESObjects.ELEMENTS.KNOWLEDGE);
		Random rand = world.rand;
		fire.shrink(8);

		final float size = data.getSize() / 2;
		AxisAlignedBB aabb = new AxisAlignedBB(originPos.getX() - size, originPos.getY(), originPos.getZ() - size,
				originPos.getX() + size, originPos.getY() + 3, originPos.getZ() + size);
		List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, aabb);
		for (Entity entity : entities) {
			if (!(entity instanceof EntityLivingBase) && !(entity instanceof EntityItem)) continue;
			if (knowledge.getCount() >= 20) {
				if (isCasterFriend(caster, entity)) continue;
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

package yuzunyannn.elementalsorcery.grimoire.mantra;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.mantra.CastStatus;
import yuzunyannn.elementalsorcery.api.mantra.ICaster;
import yuzunyannn.elementalsorcery.api.mantra.ICasterObject;
import yuzunyannn.elementalsorcery.api.mantra.MantraEffectType;
import yuzunyannn.elementalsorcery.grimoire.MantraEffectMap;
import yuzunyannn.elementalsorcery.render.effect.grimoire.EffectMagicSquare;
import yuzunyannn.elementalsorcery.render.effect.grimoire.EffectMagicSquareNoEntry;

public class MantraSturdyArea extends MantraTypeSquareArea {

	public MantraSturdyArea() {
		this.setTranslationKey("sturdyArea");
		this.setColor(0x7b4a2d);
		this.setIcon("sturdy_area");
		this.setRarity(80);
		this.setOccupation(2);
		this.addElementCollect(new ElementStack(ESObjects.ELEMENTS.EARTH, 2, 50), 120, 20);
		this.initAndAddDefaultMantraLauncher(0.01);
	}

	@Override
	public void potentAttack(World world, ItemStack grimoire, ICaster caster, Entity target) {
		super.potentAttack(world, grimoire, caster, target);
		if (target instanceof EntityLivingBase)
			((EntityLivingBase) target).addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 20 * 5, 2));
	}

	@Override
	public void init(World world, SquareData data, ICaster caster, BlockPos pos) {
		ElementStack earth = data.get(ESObjects.ELEMENTS.EARTH);
		float rate = 0.75f * caster.iWantBePotent(0.5f, false) + 1;
		data.setSize(Math.min(earth.getPower() / 80, 12) * rate + 6);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addAfterEffect(SquareData data, ICaster caster, int size) {
		super.addAfterEffect(data, caster, size);

		EffectMagicSquare ems = data.getEffectMap().getMark(MantraEffectType.MANTRA_EFFECT_1, EffectMagicSquare.class);
		if (ems == null) return;
		ems.enableElementEffect = false;

		if (data.getEffectMap().hasMark(MantraEffectType.MANTRA_EFFECT_2)) return;
		ICasterObject casterObject = caster.iWantDirectCaster();
		EffectMagicSquareNoEntry emse = new EffectMagicSquareNoEntry(casterObject.getWorld(), casterObject.asEntity(),
				size - 0.5f, this.getColor(data));
		emse.setCondition(MantraEffectMap.condition(caster, data, CastStatus.AFTER_SPELLING));
		data.getEffectMap().addAndMark(MantraEffectType.MANTRA_EFFECT_2, emse);
		emse.hight = Math.max(3, size / 3);
	}

	@Override
	public boolean tick(World world, SquareData data, ICaster caster, BlockPos originPos) {
		int tick = caster.iWantKnowCastTick();
		ElementStack earth = data.get(ESObjects.ELEMENTS.EARTH);
		if (earth.isEmpty()) return false;
		if (tick % 20 == 0) earth.shrink(1);

		final float size = data.getSize() / 2;
		final float hight = Math.max(3, data.getSize() / 3);
		AxisAlignedBB aabb = new AxisAlignedBB(originPos.getX() - size, originPos.getY() - hight,
				originPos.getZ() - size, originPos.getX() + size, originPos.getY() + hight, originPos.getZ() + size);

		List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, aabb);
		for (Entity entity : entities) {
			if (isCasterFriend(caster, entity)) continue;
			if (entity instanceof EntityLivingBase || entity instanceof EntityItem || entity instanceof IProjectile
					|| entity instanceof EntityFireball) {
				Vec3d speed = new Vec3d(entity.motionX, 0, entity.motionZ);
				if (speed.lengthSquared() < 1) speed = speed.normalize();
				Vec3d vec = new Vec3d(entity.posX, entity.posY, entity.posZ);
				Vec3d latestVec = vec.add(speed.scale(-2));
				RayTraceResult ray = aabb.calculateIntercept(latestVec, vec);
				if (ray != null && ray.sideHit.getHorizontalIndex() != -1
						&& aabb.contains(vec.add(speed.scale(0.1).add(0, 0.1, 0)))) {
					Vec3d tPos = ray.hitVec.add(speed.scale(-0.05));
					entity.setPosition(tPos.x, entity.posY, tPos.z);
					entity.motionX = 0;
					entity.motionZ = 0;
				}
			}
		}

		return true;
	}
}

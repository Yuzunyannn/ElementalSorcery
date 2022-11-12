package yuzunyannn.elementalsorcery.grimoire.mantra;

import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.mantra.ICaster;
import yuzunyannn.elementalsorcery.api.mantra.IMantraData;
import yuzunyannn.elementalsorcery.api.mantra.MantraEffectType;
import yuzunyannn.elementalsorcery.api.util.WorldTarget;
import yuzunyannn.elementalsorcery.grimoire.MantraDataCommon;
import yuzunyannn.elementalsorcery.item.tool.ItemMagicBlastWand;
import yuzunyannn.elementalsorcery.render.effect.particle.ParticleMagicFall;
import yuzunyannn.elementalsorcery.render.effect.scrappy.FirewrokShap;
import yuzunyannn.elementalsorcery.tile.md.TileMDBase;

public class MantraMagicStrafe extends MantraTypePersistent {

	public MantraMagicStrafe() {
		this.setTranslationKey("magicStrafe");
		this.setColor(0x7d17e3);
		this.setIcon("magic_strafe");
		this.setRarity(100);
		this.setOccupation(4);
		this.addElementCollect(ElementStack.magic(5, 10), true);
		this.setInterval(3);
	}

	@Override
	protected void onUpdate(World world, IMantraData data, ICaster caster) {
		int tick = caster.iWantKnowCastTick();
		if (tick % 3 != 0) return;
		MantraDataCommon mData = (MantraDataCommon) data;
		ElementStack magic = mData.get(ESObjects.ELEMENTS.MAGIC);
		if (magic.isEmpty()) return;

		WorldTarget result = caster.iWantEntityTarget(EntityLivingBase.class);
		EntityLivingBase target = (EntityLivingBase) result.getEntity();

		if (target == null) {
			if (world.isRemote) onAttackBlock(world, data, caster);
			return;
		}

		if (world.isRemote) {
			onAttackEffect(world, result.getHitVec(), data, caster);
			return;
		}

		float potent = caster.iWantBePotent(0.05f, false);

		magic.grow(36);
		magic.weaken(Math.min(tick / 4, 60) / 20f);
		float dmg = Math.max(ItemMagicBlastWand.getDamage(magic), 0.25f) * (1 + potent * 0.5f);
		DamageSource ds = caster.iWantDamageSource(ESObjects.ELEMENTS.MAGIC);
		target.attackEntityFrom(ds, dmg);
		target.hurtResistantTime = 0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onSpellingEffect(World world, IMantraData mData, ICaster caster) {
		super.onSpellingEffect(world, mData, caster);
		addSpellingEffect(world, mData, caster, MantraEffectType.EMIT);
	}

	@SideOnly(Side.CLIENT)
	public void onAttackBlock(World world, IMantraData data, ICaster caster) {
		WorldTarget result = caster.iWantBlockTarget();
		BlockPos pos = result.getPos();
		if (pos == null) {
			Random rand = world.rand;
			Entity entity = caster.iWantCaster().asEntity();
			Vec3d vec;
			if (entity == null) {
				vec = new Vec3d(rand.nextGaussian(), rand.nextGaussian(), rand.nextGaussian())
						.scale(rand.nextFloat() * 2);
			} else vec = entity.getPositionEyes(0).add(entity.getLookVec().scale(rand.nextInt(5) + 3));
			onAttackEffect(world, vec, data, caster);
			return;
		}
		onAttackEffect(world, result.getHitVec(), data, caster);
	}

	@SideOnly(Side.CLIENT)
	public void onAttackEffect(World world, Vec3d vec, IMantraData data, ICaster caster) {
		world.playSound(vec.x, vec.y, vec.z, SoundEvents.ENTITY_FIREWORK_SHOOT, SoundCategory.PLAYERS, 1.5F,
				0.95F + world.rand.nextFloat() * 0.1F, true);
		for (int i = 0; i < 16; i++) {
			ParticleMagicFall p = new ParticleMagicFall(world, vec);
			p.setColor(TileMDBase.PARTICLE_COLOR[0]);
			p.setColorFade(TileMDBase.PARTICLE_COLOR_FADE[0]);
			p.setMotionH(world.rand.nextGaussian() * 0.05, world.rand.nextGaussian() * 0.05);
			p.setMaxAge(world.rand.nextInt(15) + 5);
			FirewrokShap.manager.addEffect(p);
		}
	}

}

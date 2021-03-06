package yuzunyannn.elementalsorcery.grimoire.mantra;

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
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.grimoire.ICaster;
import yuzunyannn.elementalsorcery.grimoire.IMantraData;
import yuzunyannn.elementalsorcery.grimoire.MantraDataCommon;
import yuzunyannn.elementalsorcery.grimoire.MantraEffectFlags;
import yuzunyannn.elementalsorcery.grimoire.WantedTargetResult;
import yuzunyannn.elementalsorcery.item.ItemMagicBlastWand;
import yuzunyannn.elementalsorcery.render.effect.FirewrokShap;
import yuzunyannn.elementalsorcery.render.effect.particle.ParticleMagicFall;
import yuzunyannn.elementalsorcery.tile.md.TileMDBase;

public class MantraMagicStrafe extends MantraCommon {

	public MantraMagicStrafe() {
		this.setUnlocalizedName("magicStrafe");
		this.setColor(0x7d17e3);
		this.setIcon("magic_strafe");
		this.setRarity(100);
		this.setOccupation(4);
	}

	@Override
	public void onSpelling(World world, IMantraData data, ICaster caster) {
		int tick = caster.iWantKnowCastTick();
		if (tick < 20) return;
		if (tick % 3 != 0) return;

		((MantraDataCommon) data).markContinue(true);
		ElementStack need = ElementStack.magic(5, 10);
		ElementStack get = caster.iWantSomeElement(need, true);
		if (get.isEmpty()) return;

		if (world.isRemote) this.onSpellingEffect(world, data, caster);

		WantedTargetResult result = caster.iWantLivingTarget(EntityLivingBase.class);
		EntityLivingBase target = (EntityLivingBase) result.getEntity();

		if (target == null) {
			if (world.isRemote) onAttackBlock(world, data, caster);
			return;
		}

		if (world.isRemote) {
			onAttackEffect(world, result.getHitVec(), data, caster);
			return;
		}

		get.grow(36);
		get.weaken(Math.min(tick / 4, 60) / 20f);
		float dmg = Math.max(ItemMagicBlastWand.getDamage(get), 0.25f);
		DamageSource ds = ItemMagicBlastWand.getMagicDamageSource(caster.iWantCaster(), caster.iWantDirectCaster());
		target.attackEntityFrom(ds, dmg);
		target.hurtResistantTime = 0;
	}

	@SideOnly(Side.CLIENT)
	public void onAttackBlock(World world, IMantraData data, ICaster caster) {
		WantedTargetResult result = caster.iWantBlockTarget();
		BlockPos pos = result.getPos();
		if (pos == null) {
			Entity entity = caster.iWantCaster();
			Vec3d vec = entity.getPositionEyes(0).add(entity.getLookVec().scale(world.rand.nextInt(5) + 3));
			onAttackEffect(world, vec, data, caster);
			return;
		}
		onAttackEffect(world, result.getHitVec(), data, caster);
	}

	@SideOnly(Side.CLIENT)
	public void onAttackEffect(World world, Vec3d vec, IMantraData data, ICaster caster) {
		world.playSound(vec.x, vec.y, vec.z, SoundEvents.ENTITY_FIREWORK_SHOOT, SoundCategory.PLAYERS, 1.5F,
				0.95F + world.rand.nextFloat() * 0.1F, true);
		if (!this.hasEffectFlags(world, data, caster, MantraEffectFlags.DECORATE)) return;
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

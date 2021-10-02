package yuzunyannn.elementalsorcery.grimoire.mantra;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.grimoire.ICaster;
import yuzunyannn.elementalsorcery.grimoire.ICasterObject;
import yuzunyannn.elementalsorcery.grimoire.IMantraData;
import yuzunyannn.elementalsorcery.grimoire.MantraDataCommon;
import yuzunyannn.elementalsorcery.grimoire.MantraDataCommon.CollectResult;
import yuzunyannn.elementalsorcery.grimoire.MantraEffectFlags;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.render.effect.grimoire.EffectTimeHourglass;
import yuzunyannn.elementalsorcery.ts.PocketWatch;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class MantraTimeHourglass extends MantraCommon {

	public static final float TIME_STOP_MIN_POTENT = 0.9f;

	public MantraTimeHourglass() {
		this.setUnlocalizedName("timeHourglass");
		this.setColor(0xe0e0e0);
		this.setIcon("time_hourglass");
		this.setRarity(20);
	}

	@Override
	public boolean canPotentAttack(World world, ItemStack grimoire, ICaster caster, Entity target) {
		float potent = caster.iWantBePotent(0.3f, true);
		return potent > 0.5f && super.canPotentAttack(world, grimoire, caster, target);
	}

	@Override
	public void potentAttack(World world, ItemStack grimoire, ICaster caster, Entity target) {
		ElementStack stack = getElement(caster, ESInit.ELEMENTS.STAR, 1, 2);
		if (stack.isEmpty()) return;

		if (target instanceof EntityLivingBase) {
			float potent = caster.iWantBePotent(0.3f, false);
			if (!world.isRemote) {
				int time = (int) ((1 + potent) * stack.getPower());
				((EntityLivingBase) target).addPotionEffect(new PotionEffect(ESInit.POTIONS.TIME_SLOW, time, 3));
			}
		}

		super.potentAttack(world, grimoire, caster, target);
	}

	@Override
	public void startSpelling(World world, IMantraData data, ICaster caster) {
		MantraDataCommon mdc = (MantraDataCommon) data;
		if (PocketWatch.isActive(world)) return;
		float potent = caster.iWantBePotent(10, true);
		if (potent >= TIME_STOP_MIN_POTENT) {
			caster.iWantBePotent(10, false);
			mdc.set(POTENT_POWER, potent);
		}
		mdc.markContinue(true);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onSpellingEffect(World world, IMantraData data, ICaster caster) {
		if (beforeGeneralStartTime(caster)) return;
		MantraDataCommon mdc = (MantraDataCommon) data;
		if (!mdc.isMarkContinue()) return;
		super.onSpellingEffect(world, data, caster);
		ElementStack star = mdc.get(ESInit.ELEMENTS.STAR);
		if (star.getCount() > 50 && hasEffectFlags(world, data, caster, MantraEffectFlags.MAGIC_CIRCLE)) out: {
			ICasterObject co = caster.iWantCaster();
			EntityLivingBase eb = co.asEntityLivingBase();
			if (eb == null) break out;
			EffectTimeHourglass eth = mdc.getMarkEffect(0, EffectTimeHourglass.class);
			if (eth == null) {
				mdc.removeMarkEffect(0);
				eth = new EffectTimeHourglass(world, eb);
				eth.setColor(getColor(mdc));
				mdc.addEffect(caster, eth, 0);
			}
			eth.nextPotinerRate = mdc.getProgress();
		}
	}

	@Override
	public void onCollectElement(World world, IMantraData data, ICaster caster, int speedTick) {
		if (beforeGeneralStartTime(caster)) return;
		MantraDataCommon mdc = (MantraDataCommon) data;
		float potent = mdc.get(POTENT_POWER);
		if (potent >= TIME_STOP_MIN_POTENT) {
			if (speedTick % 30 != 0) return;
			CollectResult cr = mdc.tryCollect(caster, ESInit.ELEMENTS.STAR, 1, 160, 200);
			mdc.setProgress(cr.getStackCount(), 200);
		} else {
			CollectResult cr = mdc.tryCollect(caster, ESInit.ELEMENTS.STAR, 1, 5, 50);
			mdc.setProgress(cr.getStackCount(), 50);
		}
	}

	@Override
	public void endSpelling(World world, IMantraData data, ICaster caster) {
		MantraDataCommon mdc = (MantraDataCommon) data;

		ElementStack star = mdc.get(ESInit.ELEMENTS.STAR);
		if (star.isEmpty()) return;
		float potent = mdc.get(POTENT_POWER);

		// time stop
		if (star.getCount() >= 200 && potent >= TIME_STOP_MIN_POTENT) {
			if (world.isRemote) return;
			float sec = Math.min(star.getPower() / 12f, 30) * (1 + potent - 0.7f);
			PocketWatch.stopWorld(world, (int) (sec * 20), caster.iWantCaster().asEntityLivingBase());
			return;
		}

		// debuff
		int size = Math.min(star.getPower() / 2, 8);
		int count = star.getCount() * 2;

		AxisAlignedBB aabb = WorldHelper.createAABB(caster.iWantCaster().getPosition(), size, 3, 2);
		List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, aabb);

		EntityLivingBase self = caster.iWantCaster().asEntityLivingBase();
		int time = Math.min(star.getPower() * 8, 16 * 20);

		for (EntityLivingBase entity : entities) {
			if (self != null) {
				if (self == entity) continue;
				if (self.isOnSameTeam(entity)) continue;
			}
			if (world.isRemote) MantraBlockCrash.addBlockElementEffect(entity.getPositionEyes(0), getColor(data));
			else entity.addPotionEffect(new PotionEffect(ESInit.POTIONS.TIME_SLOW, time, 3));
			if (--count <= 0) break;
		}
	}

}

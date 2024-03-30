package yuzunyannn.elementalsorcery.grimoire.mantra;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.mantra.ICaster;
import yuzunyannn.elementalsorcery.api.mantra.IMantraData;
import yuzunyannn.elementalsorcery.api.mantra.MantraEffectType;
import yuzunyannn.elementalsorcery.api.util.target.IWorldObject;
import yuzunyannn.elementalsorcery.grimoire.Grimoire;
import yuzunyannn.elementalsorcery.grimoire.MantraDataCommon;
import yuzunyannn.elementalsorcery.grimoire.MantraEffectMap;
import yuzunyannn.elementalsorcery.render.effect.IEffectBinder;
import yuzunyannn.elementalsorcery.render.effect.grimoire.EffectTimeHourglass;
import yuzunyannn.elementalsorcery.ts.PocketWatch;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class MantraTimeHourglass extends MantraTypeAccumulative {

	public static final float TIME_STOP_MIN_POTENT = 0.9f;

	protected CollectRule superRule = new CollectRule();

	public MantraTimeHourglass() {
		this.setTranslationKey("timeHourglass");
		this.setColor(0xe0e0e0);
		this.setIcon("time_hourglass");
		this.setRarity(30);
		superRule.addElementCollect(new ElementStack(ESObjects.ELEMENTS.STAR, 1, 160), 200, 200);
		superRule.accumulatePreTick = 20;
		mainRule.addElementCollect(new ElementStack(ESObjects.ELEMENTS.STAR, 1, 5), 50, 10);
	}

	@Override
	public boolean canPotentAttack(World world, ItemStack grimoire, ICaster caster, Entity target) {
		float potent = caster.iWantBePotent(0.3f, true);
		return potent > 0.5f && super.canPotentAttack(world, grimoire, caster, target);
	}

	@Override
	public void potentAttack(World world, ItemStack grimoire, ICaster caster, Entity target) {
		ElementStack stack = getElement(caster, ESObjects.ELEMENTS.STAR, 1, 2);
		if (stack.isEmpty()) return;

		if (target instanceof EntityLivingBase) {
			float potent = caster.iWantBePotent(0.3f, false);
			if (!world.isRemote) {
				int time = (int) ((1 + potent) * stack.getPower());
				((EntityLivingBase) target).addPotionEffect(new PotionEffect(ESObjects.POTIONS.TIME_SLOW, time, 3));
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
	public void onCollectElement(World world, IMantraData data, ICaster caster, int speedTick) {
		MantraDataCommon mData = (MantraDataCommon) data;
		if (!mData.isMarkContinue()) return;
		super.onCollectElement(world, data, caster, speedTick);
	}

	@Override
	protected CollectRule getCurrCollectRule(World world, MantraDataCommon mData, ICaster caster) {
		float potent = mData.get(POTENT_POWER);
		if (potent >= TIME_STOP_MIN_POTENT) return superRule;
		return mainRule;
	}

	@Override
	protected void addRuleInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		Grimoire grimoire = stack.getCapability(Grimoire.GRIMOIRE_CAPABILITY, null);
		boolean isTheWorld = grimoire == null ? false
				: (grimoire.potentPoint >= 10 && grimoire.getPotent() > TIME_STOP_MIN_POTENT);
		if (isTheWorld) superRule.addInformation(stack, worldIn, tooltip, flagIn);
		else mainRule.addInformation(stack, worldIn, tooltip, flagIn);
	}

	@Override
	public void endSpelling(World world, IMantraData data, ICaster caster) {
		MantraDataCommon mdc = (MantraDataCommon) data;

		ElementStack star = mdc.get(ESObjects.ELEMENTS.STAR);
		if (star.isEmpty()) return;

		if (star.getCount() < 10) return;

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

		int time = Math.min(star.getPower() * 8, 16 * 20);

		for (EntityLivingBase entity : entities) {
			if (isCasterFriend(caster, entity)) continue;
			if (world.isRemote) MantraBlockCrash.addBlockElementEffect(entity.getPositionEyes(0), getColor(data));
			else entity.addPotionEffect(new PotionEffect(ESObjects.POTIONS.TIME_SLOW, time, 3));
			if (--count <= 0) break;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onSpellingEffect(World world, IMantraData data, ICaster caster) {
		MantraDataCommon mdc = (MantraDataCommon) data;
		if (!mdc.isMarkContinue()) return;
		super.onSpellingEffect(world, data, caster);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void initEffectCreator() {
		super.initEffectCreator();
		setEffectCreator(MantraEffectType.MAGIC_CIRCLE, MantraCommon::createEffectMagicCircle,
				(world, mantra, mData, caster, effect) -> {
					MantraDataCommon mdc = (MantraDataCommon) mData;
					ElementStack star = mdc.get(ESObjects.ELEMENTS.STAR);
					float potent = mdc.get(POTENT_POWER);
					if (star.getCount() < 50 || potent < TIME_STOP_MIN_POTENT) return;

					IWorldObject co = caster.iWantCaster();
					if (!(effect instanceof EffectTimeHourglass)) {
						mdc.getEffectMap().removeMark(MantraEffectType.MAGIC_CIRCLE);
						EffectTimeHourglass eth = new EffectTimeHourglass(world, IEffectBinder.asBinder(co));
						eth.setColor(getColor(mdc));
						eth.setCondition(MantraEffectMap.condition(caster, mdc));
						mdc.getEffectMap().addAndMark(MantraEffectType.MAGIC_CIRCLE, effect = eth);
					}
					((EffectTimeHourglass) effect).nextPotinerRate = (float) mdc.getProgress();
				});
	}
}

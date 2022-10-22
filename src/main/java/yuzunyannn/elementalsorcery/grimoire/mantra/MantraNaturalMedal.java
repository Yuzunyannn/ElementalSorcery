package yuzunyannn.elementalsorcery.grimoire.mantra;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.mantra.ICaster;
import yuzunyannn.elementalsorcery.api.mantra.IMantraData;
import yuzunyannn.elementalsorcery.grimoire.MantraDataCommon;
import yuzunyannn.elementalsorcery.potion.PotionNaturalMedal;
import yuzunyannn.elementalsorcery.render.effect.Effects;

public class MantraNaturalMedal extends MantraTypeAccumulative {

	public MantraNaturalMedal() {
		this.setTranslationKey("naturalMedal");
		this.setColor(0x63b91e);
		this.setIcon("natural_medal");
		this.setRarity(35);
		this.setOccupation(4);
		this.addElementCollect(new ElementStack(ESObjects.ELEMENTS.WOOD, 1, 128), 300, 300);
	}

	@Override
	public void potentAttack(World world, ItemStack grimoire, ICaster caster, Entity target) {
		float potent = caster.iWantBePotent(0.5f, true);
		if (potent > 0.1 && target instanceof EntityLivingBase) {
			PotionNaturalMedal.growMedal((EntityLivingBase) target);
			caster.iWantBePotent(0.4f, false);
		}
	}

	@Override
	public void endSpelling(World world, IMantraData data, ICaster caster) {
		if (!isAllElementMeetMinNeed(data)) return;
		MantraDataCommon mData = (MantraDataCommon) data;
		ElementStack eStack = mData.get(ESObjects.ELEMENTS.WOOD);
		EntityLivingBase entity = (EntityLivingBase) caster.iWantEntityTarget(EntityLivingBase.class).getEntity();
		if (entity == null) entity = caster.iWantCaster().asEntityLivingBase();
		if (entity == null) return;
		if (world.isRemote) {
			Effects.spawnTreatEntity(entity, new int[] { 0x63b91e });
			return;
		}
		PotionEffect effect = entity.getActivePotionEffect(ESObjects.POTIONS.NATURAL_MEDAL);
		int amplifier = effect == null ? -1 : effect.getAmplifier();
		int lev = eStack.getPower() / 128;
		if (caster.iWantBePotent(1, true) > 0.25) {
			caster.iWantBePotent(1, false);
			amplifier = amplifier + lev + 2;
		} else amplifier = MathHelper.clamp(amplifier, lev, 127);
		entity.addPotionEffect(new PotionEffect(ESObjects.POTIONS.NATURAL_MEDAL,
				PotionNaturalMedal.DEFAULT_LEVEL_TICK * 4, amplifier));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onSpellingEffect(World world, IMantraData data, ICaster caster) {
		super.onSpellingEffect(world, data, caster);
		MantraDataCommon mData = (MantraDataCommon) data;
		if (mData.getProgress() >= 0.999f) addEffectEntityIndicatorEffect(world, data, caster);
	}

}

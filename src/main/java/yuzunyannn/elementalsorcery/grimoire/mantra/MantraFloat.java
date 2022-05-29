package yuzunyannn.elementalsorcery.grimoire.mantra;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.grimoire.ICaster;
import yuzunyannn.elementalsorcery.grimoire.IMantraData;
import yuzunyannn.elementalsorcery.grimoire.MantraDataCommon;
import yuzunyannn.elementalsorcery.grimoire.remote.FMantraFloat;
import yuzunyannn.elementalsorcery.init.ESInit;

public class MantraFloat extends MantraCommon {

	public MantraFloat() {
		this.setTranslationKey("float");
		this.setColor(0xacffff);
		this.setIcon("float");
		this.setRarity(125);
		this.setOccupation(1);
		this.addFragmentMantraLauncher(new FMantraFloat(this));
	}

	@Override
	public void potentAttack(World world, ItemStack grimoire, ICaster caster, Entity target) {
		super.potentAttack(world, grimoire, caster, target);

		ElementStack stack = getElement(caster, ESInit.ELEMENTS.AIR, 1, 10);
		if (stack.isEmpty()) return;
		if (target instanceof EntityLivingBase) {
			((EntityLivingBase) target).addPotionEffect(new PotionEffect(MobEffects.LEVITATION, 80, 1));
		}
	}

	@Override
	public void startSpelling(World world, IMantraData data, ICaster caster) {
		onSpelling(world, data, caster);
	}

	@Override
	public void onSpelling(World world, IMantraData data, ICaster caster) {
		MantraDataCommon dataEffect = (MantraDataCommon) data;
		float potent = caster.iWantBePotent(0.0005f, true);
		if (potent > 0.2f) caster.iWantBePotent(0.0005f, false);
		else {
			if (caster.iWantKnowCastTick() % 20 == 0 || !dataEffect.isMarkContinue()) {
				dataEffect.markContinue(false);
				ElementStack get = getElement(caster, ESInit.ELEMENTS.AIR, 1, 20);
				if (get.isEmpty()) return;
			}
		}
		Entity entity = caster.iWantCaster().asEntity();
		if (entity == null) return;
		dataEffect.markContinue(true);
		entity.motionY = 0.15;
		entity.fallDistance = 0;
		if (world.isRemote) onSpellingEffect(world, data, caster);
	}

}

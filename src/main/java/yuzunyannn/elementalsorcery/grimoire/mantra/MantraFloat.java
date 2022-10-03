package yuzunyannn.elementalsorcery.grimoire.mantra;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.mantra.ICaster;
import yuzunyannn.elementalsorcery.api.mantra.IMantraData;
import yuzunyannn.elementalsorcery.grimoire.MantraDataCommon;
import yuzunyannn.elementalsorcery.grimoire.remote.FMantraFloat;

public class MantraFloat extends MantraTypePersistent {

	public MantraFloat() {
		this.setTranslationKey("float");
		this.setColor(0xacffff);
		this.setIcon("float");
		this.setRarity(125);
		this.setOccupation(1);
		this.addElementCollect(new ElementStack(ESObjects.ELEMENTS.AIR, 1, 20), true);
		this.setInterval(20);
		this.addFragmentMantraLauncher(new FMantraFloat(this));
	}

	@Override
	public void potentAttack(World world, ItemStack grimoire, ICaster caster, Entity target) {
		super.potentAttack(world, grimoire, caster, target);

		ElementStack stack = getElement(caster, ESObjects.ELEMENTS.AIR, 1, 10);
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
	protected void onUpdate(World world, IMantraData data, ICaster caster) {
		Entity entity = caster.iWantCaster().asEntity();
		if (entity == null) return;
		entity.motionY = 0.15;
		entity.fallDistance = 0;
	}

	@Override
	public void onCollectElement(World world, IMantraData data, ICaster caster, int speedTick) {
		float potent = caster.iWantBePotent(0.0005f, true);
		if (potent > 0.2f) {
			MantraDataCommon mData = (MantraDataCommon) data;
			caster.iWantBePotent(0.0005f, false);
			mData.markContinue(true);
			return;
		}
		super.onCollectElement(world, data, caster, speedTick);
	}

}

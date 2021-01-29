package yuzunyannn.elementalsorcery.grimoire.mantra;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.grimoire.ICaster;
import yuzunyannn.elementalsorcery.grimoire.IMantraData;
import yuzunyannn.elementalsorcery.grimoire.MantraDataCommon;
import yuzunyannn.elementalsorcery.init.ESInit;

public class MantraFloat extends MantraCommon {

	public MantraFloat() {
		this.setUnlocalizedName("float");
		this.setColor(0xacffff);
		this.setIcon("float");
		this.setRarity(125);
		this.setOccupation(1);
	}

	@Override
	public void startSpelling(World world, IMantraData data, ICaster caster) {
		onSpelling(world, data, caster);
	}

	@Override
	public void onSpelling(World world, IMantraData data, ICaster caster) {
		MantraDataCommon dataEffect = (MantraDataCommon) data;
		if (caster.iWantKnowCastTick() % 20 == 0 || !dataEffect.isMarkContinue()) {
			dataEffect.markContinue(false);
			ElementStack need = new ElementStack(ESInit.ELEMENTS.AIR, 1, 20);
			ElementStack get = caster.iWantSomeElement(need, true);
			if (get.isEmpty()) return;
		}
		dataEffect.markContinue(true);
		Entity entity = caster.iWantCaster();
		entity.motionY = 0.15;
		entity.fallDistance = 0;
		if (world.isRemote) onSpellingEffect(world, data, caster);
	}

}

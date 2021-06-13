package yuzunyannn.elementalsorcery.entity.fcube;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.init.ESInit;

public class FCMAttackRange extends FairyCubeModule {

	public FCMAttackRange(EntityFairyCube fairyCube) {
		super(fairyCube);
		this.setPriority(PriorityType.MODIFY_ADD);
		this.setElementNeedPerExp(new ElementStack(ESInit.ELEMENTS.WATER, 9, 111), 16);
	}

	public static float commonRange(int level) {
		if (level > 20) return 16;
		float range = 1 + (float) Math.pow(level / 2f, 1.2);
		return Math.min(16, range);
	}

	public float getRange() {
		return commonRange(this.getLevelUsed());
	}

	@Override
	public float modifyAttribute(String attribute, float value) {
		if ("attack:range".equals(attribute)) return value + this.getRange();
		return value;
	}

	@Override
	public String getStatusUnlocalizedValue(int status) {
		if (status == 1) return "fairy.cube.attack.range";
		return super.getStatusUnlocalizedValue(status);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getStatusValue(int status) {
		if (status == 1) {
			String r = String.format("%.1f", this.getRange());
			return I18n.format(this.getStatusUnlocalizedValue(status)) + r;
		}
		return super.getStatusValue(status);
	}

}

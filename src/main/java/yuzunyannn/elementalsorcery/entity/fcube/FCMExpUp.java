package yuzunyannn.elementalsorcery.entity.fcube;

import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.init.ESInit;

public class FCMExpUp extends FairyCubeModule {

	public FCMExpUp(EntityFairyCube fairyCube) {
		super(fairyCube);
		this.setPriority(PriorityType.MODIFY_MULTI);
		this.setElementNeedPerExp(new ElementStack(ESInit.ELEMENTS.KNOWLEDGE, 12, 65), 16);
	}

	public float getExpIncr() {
		int level = this.getLevelUsed();
		return 0.1f + level * 0.1f;
	}

	@Override
	public float modifyAttribute(String attribute, float value) {
		if ("experience:cube".equals(attribute)) return value * (1 + this.getExpIncr());
		else if ("experience:module".equals(attribute)) return value * (1 + this.getExpIncr() / 2f);
		return value;
	}

	@Override
	public String getStatusUnlocalizedValue(int status) {
		if (status == 1) return "fairy.cube.expUp.name";
		return super.getStatusUnlocalizedValue(status);
	}

}

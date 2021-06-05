package yuzunyannn.elementalsorcery.entity.fcube;

public class FCMSilk extends FairyCubeModule {

	public FCMSilk(EntityFairyCube fairyCube) {
		super(fairyCube);
		this.setPriority(PriorityType.MODIFY_ADD);
	}

	@Override
	public float modifyAttribute(String attribute, float value) {
		if ("silk".equals(attribute)) return value + 1;
		return value;
	}

	@Override
	public String getStatusUnlocalizedValue(int status) {
		if (status == 1) return "enchantment.untouching";
		return super.getStatusUnlocalizedValue(status);
	}

}

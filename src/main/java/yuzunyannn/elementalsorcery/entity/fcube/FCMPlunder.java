package yuzunyannn.elementalsorcery.entity.fcube;

import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.init.ESInit;

public class FCMPlunder extends FairyCubeModule {

	protected float absorbRate = 0;

	public FCMPlunder(EntityFairyCube fairyCube) {
		super(fairyCube);
		this.setPriority(PriorityType.MODIFY_ADD);
		this.expExample = new ElementStack(ESInit.ELEMENTS.STAR, 4, 300);
	}

	public int getPlunderLevel() {
		int level = this.getLevelUsed();
		int plunder = (int) (1 + Math.pow(level / 4f, 1.15));
		return plunder;
	}

	@Override
	public float modifyAttribute(String attribute, float value) {
		if ("plunder".equals(attribute)) return value + this.getPlunderLevel();
		return value;
	}

	@Override
	public boolean absorbElements(IElementInventory einv) {
		int dCount = (int) (10 * (1 + Math.abs(absorbRate)));
		if (absorbRate > 0) {
			ElementStack metal = new ElementStack(ESInit.ELEMENTS.METAL, dCount, 125);
			if (absorbElementToExp(metal, einv, onceExpGetMax)) {
				absorbRate -= 0.5f;
				return true;
			}
			ElementStack wood = new ElementStack(ESInit.ELEMENTS.WATER, dCount, 125);
			if (absorbElementToExp(wood, einv, onceExpGetMax)) {
				absorbRate += 0.5f;
				return true;
			}
		} else {
			ElementStack wood = new ElementStack(ESInit.ELEMENTS.WATER, dCount, 125);
			if (absorbElementToExp(wood, einv, onceExpGetMax)) {
				absorbRate += 0.5f;
				return true;
			}
			ElementStack metal = new ElementStack(ESInit.ELEMENTS.METAL, dCount, 125);
			if (absorbElementToExp(metal, einv, onceExpGetMax)) {
				absorbRate -= 0.5f;
				return true;
			}
		}
		if (absorbElementToExp(expExample, einv, onceExpGetMax)) return true;
		return false;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = super.serializeNBT();
		nbt.setFloat("abRa", absorbRate);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		super.deserializeNBT(nbt);
		absorbRate = nbt.getFloat("abRa");
	}

	@Override
	public String getStatusUnlocalizedValue(int status) {
		if (status == 1) return "enchantment.lootBonus";
		return super.getStatusUnlocalizedValue(status);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getStatusValue(int status) {
		if (status == 1) return I18n.format(this.getStatusUnlocalizedValue(status)) + this.getPlunderLevel();
		return super.getStatusValue(status);
	}

}

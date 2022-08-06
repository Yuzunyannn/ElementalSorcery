package yuzunyannn.elementalsorcery.crafting.element;

import net.minecraft.item.ItemStack;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import yuzunyannn.elementalsorcery.api.crafting.IToElement;
import yuzunyannn.elementalsorcery.api.crafting.IToElementInfo;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.tile.ir.TileIceRockEnergy;

public class DefaultEnergyToElement implements IToElement {

	@Override
	public IToElementInfo toElement(ItemStack stack) {
		IEnergyStorage storage = stack.getCapability(CapabilityEnergy.ENERGY, null);
		if (storage == null) return null;
		int stored = storage.getEnergyStored();
		if (stored <= 0) return null;
		ItemStack remain = stack.copy();
		storage = remain.getCapability(CapabilityEnergy.ENERGY, null);
		int needStored = stored;
		for (int i = 0; i < 8; i++) {
			int realExtrect = storage.extractEnergy(needStored, false);
			if (realExtrect >= needStored) break;
			needStored -= realExtrect;
		}
		int newStored = storage.getEnergyStored();
		if (newStored >= stored) return null;
		return ToElementInfoStatic.create(0, remain,
				ElementStack.magic((int) ((stored - newStored) * TileIceRockEnergy.FRAGMENT_RF), 1));
	}

}

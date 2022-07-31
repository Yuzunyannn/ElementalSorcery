package yuzunyannn.elementalsorcery.mods.ic2;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import yuzunyannn.elementalsorcery.api.crafting.IToElement;
import yuzunyannn.elementalsorcery.api.crafting.IToElementInfo;
import yuzunyannn.elementalsorcery.crafting.element.ToElementInfoStatic;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.tile.ir.TileIceRockEnergy;
import yuzunyannn.elementalsorcery.util.NBTTag;

public class IC2EngeryToElement implements IToElement {

	@Override
	public IToElementInfo toElement(ItemStack stack) {
		Item item = stack.getItem();
		if (item instanceof IElectricItem) {
			double charge = ElectricItem.manager.getCharge(stack);
			if (charge <= 0) return null;
			ItemStack remain = stack.copy();
			int tier = ElectricItem.manager.getTier(remain);
			ElectricItem.manager.discharge(remain, charge, tier, true, false, false);
			double newCharge = ElectricItem.manager.getCharge(remain);
			// 取出來后不能減少？創造模式的电池吗？恕我无力处理，溜了溜了
			if (newCharge >= charge) return null;
			return toElement(charge - newCharge, remain);
		} else {
			NBTTagCompound nbt = stack.getTagCompound();
			if (nbt == null) return null;
			// wwwwwwwww 电箱之类的居然没提供获取接口，无奈只能读取nbt
			if (nbt.hasKey("energy", NBTTag.TAG_NUMBER)) {
				double charge = nbt.getDouble("energy");
				if (charge <= 0) return null;
				ItemStack remain = stack.copy();
				remain.getTagCompound().removeTag("energy");
				return toElement(charge, remain);
			}
		}
		return null;
	}

	static public IToElementInfo toElement(double charge, ItemStack remain) {
		if (charge < 1) return null;
		return ToElementInfoStatic.create(0, remain,
				ElementStack.magic((int) (charge * TileIceRockEnergy.FRAGMENT_UE), 1));
	}

}

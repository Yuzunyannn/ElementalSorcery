package yuzunyannn.elementalsorcery.mods.ae2;

import appeng.api.config.Actionable;
import appeng.api.implementations.items.IAEItemPowerStorage;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import yuzunyannn.elementalsorcery.api.crafting.IToElement;
import yuzunyannn.elementalsorcery.api.crafting.IToElementInfo;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.crafting.element.ToElementInfoStatic;
import yuzunyannn.elementalsorcery.tile.ir.TileIceRockEnergy;

public class IAE2EngeryToElement implements IToElement {

	@Override
	public IToElementInfo toElement(ItemStack stack) {
		Item item = stack.getItem();
		if (item instanceof IAEItemPowerStorage) {
			IAEItemPowerStorage powerStorage = (IAEItemPowerStorage) item;
			double ae = powerStorage.getAECurrentPower(stack);
			if (ae <= 0) return null;
			ItemStack remain = stack.copy();
			powerStorage.extractAEPower(remain, ae, Actionable.MODULATE);
			double newAE = powerStorage.getAECurrentPower(remain);
			// 取出來后不能減少？創造模式的AE吗？恕我无力处理，溜了溜了
			if (newAE >= ae) return null;
			return ToElementInfoStatic.create(0, remain,
					ElementStack.magic((int) ((ae - newAE) * TileIceRockEnergy.FRAGMENT_UE), 1));
		}
		return null;
	}

}

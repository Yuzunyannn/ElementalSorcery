package yuzunyannn.elementalsorcery.computer;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class DeviceInfoItem extends DeviceInfo {

	public final ItemStack stack;

	@Override
	public boolean isMobile() {
		return true;
	}

	public DeviceInfoItem(ItemStack stack) {
		this.stack = stack;
	}

	@Override
	public String getTranslationWorkKey() {
		return stack.getTranslationKey();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public String getDisplayWorkName() {
		return stack.getDisplayName();
	}

}

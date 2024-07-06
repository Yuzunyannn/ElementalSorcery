package yuzunyannn.elementalsorcery.api.computer;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IDeviceInfo {

	String getName();

	boolean isMobile();

	String getTranslationWorkKey();

	default ItemStack getIcon() {
		return ItemStack.EMPTY;
	}

	@Nonnull
	@SideOnly(Side.CLIENT)
	default String getDisplayWorkName() {
		return I18n.format(getTranslationWorkKey());
	}

	/**
	 * @param tooltip display objs
	 */
	@SideOnly(Side.CLIENT)
	public void addInformation(List<Object> tooltip);
}

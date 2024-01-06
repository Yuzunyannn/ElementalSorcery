package yuzunyannn.elementalsorcery.api.computer.soft;

import java.util.List;
import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag.TooltipFlags;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IAPPGuiRuntime {

	int getWidth();

	int getHeight();

	int getDisplayWidth();

	int getDisplayHeight();

	void sendOperation(NBTTagCompound nbt);

	void sendNotice(String str);

	void setTooltip(String key, Vec3d vec, int duration, Supplier<List<String>> factory);

	default void setTooltip(String key, Vec3d vec, int duration, ItemStack stack) {
		setTooltip(key, vec, duration, () -> {
			List<String> list = stack.getTooltip(Minecraft.getMinecraft().player, TooltipFlags.NORMAL);
			for (int i = 0; i < list.size(); ++i) {
				if (i == 0) list.set(i, stack.getItem().getForgeRarity(stack).getColor() + (String) list.get(i));
				else list.set(i, TextFormatting.GRAY + (String) list.get(i));
			}
			return list;
		});
	}
}

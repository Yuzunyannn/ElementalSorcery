package yuzunyannn.elementalsorcery.api.computer.soft;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag.TooltipFlags;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface ISoftGuiRuntime {

	static final Vec3d MOUSE_FOLLOW_VEC = new Vec3d(0, 0, -99);

	int getWidth();

	int getHeight();

	int getDisplayWidth();

	int getDisplayHeight();

	void sendOperation(NBTTagCompound nbt);

	void sendNotice(String str);

	void setTooltip(String key, Vec3d vec, int duration, Supplier<List<String>> factory);

	void exception(Throwable err);

	default void setTooltip(String key, Vec3d vec, int duration, String str) {
		setTooltip(key, vec, duration, () -> {
			List<String> list = new ArrayList<>(1);
			list.add(str);
			return list;
		});
	}

	default void setTooltip(String key, Vec3d vec, int duration, ItemStack stack,
			@Nullable Consumer<List<String>> hook) {
		setTooltip(key, vec, duration, () -> {
			List<String> list = stack.getTooltip(Minecraft.getMinecraft().player, TooltipFlags.NORMAL);
			for (int i = 0; i < list.size(); ++i) {
				if (i == 0) list.set(i, stack.getItem().getForgeRarity(stack).getColor() + (String) list.get(i));
				else list.set(i, TextFormatting.GRAY + (String) list.get(i));
			}
			if (hook != null) hook.accept(list);
			return list;
		});
	}
}

package yuzunyannn.elementalsorcery.api.util.render;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IRenderLayoutFix {

	@SideOnly(Side.CLIENT)
	void fixLauout(ItemStack stack);

}

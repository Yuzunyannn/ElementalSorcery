package yuzunyannn.elementalsorcery.api.util.render;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface ITheme {

	int getColor(int themeIndex);

}

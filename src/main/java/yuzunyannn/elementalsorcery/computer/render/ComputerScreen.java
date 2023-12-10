package yuzunyannn.elementalsorcery.computer.render;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ComputerScreen {

	public final ComputRenderKey key;
	public int renderCounter = 0;

	public ComputerScreen(ComputRenderKey key) {
		this.key = key;
	}

	public void bindTexture() {
		renderCounter = 0;
		
	}

}

package yuzunyan.elementalsorcery.render;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyan.elementalsorcery.event.IRenderClient;

@SideOnly(Side.CLIENT)
public class RenderRulerSelectRegion implements IRenderClient {
	@Override
	public int onRender(float partialTicks) {
		return 0;
	}
}

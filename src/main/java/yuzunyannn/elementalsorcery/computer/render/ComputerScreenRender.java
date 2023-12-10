package yuzunyannn.elementalsorcery.computer.render;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.util.render.Framebuffer;
import yuzunyannn.elementalsorcery.util.render.MCFramebufferModify;

@SideOnly(Side.CLIENT)
public class ComputerScreenRender {

	public final List<ComputerScreen> renderList = new ArrayList<>();
	
	static public void doUpdate() {
		
	}

	static public void doRenderUpdate(float partialTicks) {

	}

//	@Nullable
//	static public Framebuffer createFrameBuffer(int w, int h) {
//		return new Framebuffer(new MCFramebufferModify(w, h));
//	}

}

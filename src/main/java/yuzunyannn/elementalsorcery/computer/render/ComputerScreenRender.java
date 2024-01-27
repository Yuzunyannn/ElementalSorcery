package yuzunyannn.elementalsorcery.computer.render;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ComputerScreenRender {

	public final static List<ComputerScreen> renderList = new ArrayList<>();
	public final static LinkedList<ComputerScreen> renderPoolList = new LinkedList<>();

	static public ComputerScreen apply() {

		if (!renderPoolList.isEmpty()) {
			ComputerScreen screen = renderPoolList.pop();
			screen.reuse();
			renderList.add(screen);
			return screen;
		}

		ComputerScreen screen = new ComputerScreen();
		screen.init();
		renderList.add(screen);

		return screen;
	}

	static public void doUpdate() {

		// normal
		Iterator<ComputerScreen> iter = renderList.iterator();
		while (iter.hasNext()) {
			ComputerScreen screen = iter.next();
//			if (Effect.displayChange) screen.buffer.resize(screen.frameBufferWidth, screen.frameBufferHeight);
			if (screen.currGui != null) screen.currGui.update();
			screen.renderCounter++;
			if (screen.renderCounter > 20 * 60 || screen.waitPoolMark) {
				screen.waitPoolMark = false;
				renderPoolList.add(screen);
				screen.renderCounter = 20;
				iter.remove();
			}
		}

		// pool
		iter = renderPoolList.iterator();
		while (iter.hasNext()) {
			ComputerScreen screen = iter.next();
			screen.renderCounter++;
			if (screen.renderCounter > 20 * 60) {
				screen.close();
				iter.remove();
			}
		}
	}

	static public void doRenderUpdate(float partialTicks) {
		for (ComputerScreen screen : renderList) {
			if (screen.renderCounter < 20) screen.doRender(partialTicks);
		}
	}

//	@Nullable
//	static public Framebuffer createFrameBuffer(int w, int h) {
//		return new Framebuffer(new MCFramebufferModify(w, h));
//	}

}

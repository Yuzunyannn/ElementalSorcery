package yuzunyannn.elementalsorcery.nodegui;

import org.lwjgl.opengl.GL11;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.render.RenderRect;

@SideOnly(Side.CLIENT)
public class GuiScene extends GScene {

	@Override
	protected void doScissor(RenderRect rect) {
		float xScale = displayWidth / (float) this.width;
		float yScale = displayHeight / (float) this.height;
		float width = rect.right - rect.left;
		float height = rect.bottom - rect.top;
		int rx = (int) (xScale * rect.left);
		int ry = (int) (yScale * (this.height - rect.top - height));
		int rw = (int) (xScale * width);
		int rh = (int) (yScale * height);
		GL11.glScissor(rx, ry, rw, rh);
	}
}

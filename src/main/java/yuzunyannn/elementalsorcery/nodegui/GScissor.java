package yuzunyannn.elementalsorcery.nodegui;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.client.RenderRect;

@SideOnly(Side.CLIENT)
public class GScissor extends GNode {

	RenderRect rect = new RenderRect(0, 0, 0, 0);

	public GScissor() {

	}

	public GScissor(RenderRect text) {
		this.setRect(text);
	}

	public void setRect(RenderRect rect) {
		this.rect = rect;
		this.width = this.rect.right - this.rect.left;
		this.height = this.rect.bottom - this.rect.top;
	}

	@Override
	public void draw(float partialTicks) {
		if (scene == null) {
			super.draw(partialTicks);
			return;
		}
		scene.pushScissor(rect.move(gX - anchorX * this.width, gY - anchorY * this.height));
		super.draw(partialTicks);
		scene.popScissor();
	}

}

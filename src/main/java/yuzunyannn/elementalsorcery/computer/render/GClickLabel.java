package yuzunyannn.elementalsorcery.computer.render;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.Vec3d;
import yuzunyannn.elementalsorcery.nodegui.GNode;
import yuzunyannn.elementalsorcery.util.helper.JavaHelper;

public class GClickLabel extends GLabelCanLayout {

	BtnBaseInteractor interactor;

	public GClickLabel() {
		super();
	}

	public GClickLabel(String str) {
		super(str);
	}

	public void enableClick(Runnable run, GNode scissor) {
		interactor = new BtnBaseInteractor() {
			@Override
			public boolean blockMouseEvent(GNode node, Vec3d worldPos) {
				return false;
			};

			@Override
			public void onClick() {
				if (run != null) run.run();
				else JavaHelper.clipboardWrite(text);
			};

			@Override
			public boolean testHit(GNode node, Vec3d worldPos) {
				if (scissor != null && !scissor.testHit(worldPos)) return false;
				return super.testHit(node, worldPos);
			}
		};
		interactor.afterSound = true;
		setInteractor(interactor);
	}

	@Override
	protected void render(float partialTicks) {
		super.render(partialTicks);
		if (interactor != null && interactor.isHover) {
			GlStateManager.disableTexture2D();
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferbuilder = tessellator.getBuffer();
			GlStateManager.glLineWidth(3);
			bufferbuilder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
			bufferbuilder.pos(0, height - 1, 0).color(color.r, color.g, color.b, rAlpha).endVertex();
			bufferbuilder.pos(width, height - 1, 0).color(color.r, color.g, color.b, rAlpha).endVertex();
			tessellator.draw();
			GlStateManager.enableTexture2D();
		}
	}
}

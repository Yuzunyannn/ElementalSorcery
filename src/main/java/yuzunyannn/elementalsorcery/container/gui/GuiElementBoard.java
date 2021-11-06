package yuzunyannn.elementalsorcery.container.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.container.ContainerElementBoard;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.util.helper.ColorHelper;
import yuzunyannn.elementalsorcery.util.render.RenderHelper;

@SideOnly(Side.CLIENT)
public class GuiElementBoard extends GuiContainer {

	public static final ResourceLocation TEXTURE = new ResourceLocation(ElementalSorcery.MODID,
			"textures/gui/element_board.png");
	final ContainerElementBoard container;

	public float rate, prevRate;

	public GuiElementBoard(ContainerElementBoard inventorySlotsIn) {
		super(inventorySlotsIn);
		container = inventorySlotsIn;
		xSize = ySize = 186;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
		mc.getTextureManager().bindTexture(TEXTURE);
		drawTexturedModalRect(offsetX, offsetY, 0, 0, this.xSize, this.ySize);

		if (container.checkResults == null) return;
		ElementStack sample = container.sample;

		Vec3d c = ColorHelper.color(sample.getColor());
		float rate = RenderHelper.getPartialTicks(this.rate, this.prevRate, partialTicks);
		float progress = Math.min(1, rate);

		GlStateManager.pushMatrix();
		GlStateManager.translate(offsetX + 93, offsetY + 93, 0);
		sample.getElement().drawElemntIcon(sample, 1);
		mc.getTextureManager().bindTexture(TEXTURE);

		GlStateManager.color((float) c.x, (float) c.y, (float) c.z, 1);
		for (int i = 0; i < 4; i++) {
			drawTexturedModalRect(9, 8, 186, 18, (int) (35 * progress), (int) (35 * progress));
			GlStateManager.rotate(90, 0, 0, 1);
		}
		GlStateManager.popMatrix();

		if (progress < 1) return;
		progress = Math.min(1, rate - 1);

		float samplePower = sample.getPower();

		GlStateManager.disableAlpha();
		ElementStack[] checkResults = container.checkResults;
		for (int i = 0; i < checkResults.length; i++) {
			ElementStack estack = checkResults[i];
			if (estack.isEmpty()) continue;
			float power = estack.getPower();
			float alpha = MathHelper.clamp(power / samplePower, 0.15f, 1) * progress;
			float shine = MathHelper.clamp(estack.getCount() / (float) sample.getCount(), 0.25f, 1);
			float da = MathHelper.sin(rate / (shine * shine) * 1.25f);
			GlStateManager.color((float) c.x, (float) c.y, (float) c.z, alpha * (shine + da * (1 - shine)));
			Vec3i p = ContainerElementBoard.getPositionWithIndex(i);
			drawTexturedModalRect(offsetX + 12 + p.getX(), offsetY + 12 + p.getY(), 186, 0, 18, 18);
		}
		GlStateManager.enableAlpha();
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		if (container.checkResults == null) {
			prevRate = rate = 0;
			return;
		}
		prevRate = rate;
		rate = rate + 0.05f;
	}

}

package yuzunyannn.elementalsorcery.nodegui;

import java.util.function.Supplier;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.mantra.Mantra;

@SideOnly(Side.CLIENT)
public class GMantra extends GNode {

	public static boolean inDraw = false;

	protected Mantra mantra;
	protected Supplier<Mantra> mantraGetter;

	public GMantra() {
		this.anchorX = this.anchorY = 0.5f;
	}

	public GMantra(Mantra stack) {
		setMantra(stack);
	}

	public GMantra(Supplier<Mantra> stackGetter) {
		setMantra(stackGetter);
	}

	public void setMantra(Mantra mantra) {
		this.mantra = mantra;
		this.width = 16;
		this.height = 16;
		if (mantra == null) return;
		this.setColor(mantra.getColor(null));
	}

	public void setMantra(Supplier<Mantra> stackGetter) {
		this.mantraGetter = stackGetter;
		setMantra(stackGetter.get());
	}

	public Mantra getMantra() {
		if (mantraGetter != null) return mantraGetter.get();
		return mantra;
	}

	@Override
	protected void render(float partialTicks) {
		if (this.mantra == null) return;
//		GlStateManager.translate(8 * scale, 8 * scale, 0);
//		Color c = new Color(this.mantra.getColor(null));
//		GlStateManager.color(c.r, c.g, c.b);
		this.mantra.renderShiftIcon(null, (float) this.width, 1, partialTicks);
//		GlStateManager.translate(-8 * scale, -8 * scale, 0);
	}

}

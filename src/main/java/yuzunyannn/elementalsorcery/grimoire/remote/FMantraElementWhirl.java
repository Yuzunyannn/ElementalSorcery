package yuzunyannn.elementalsorcery.grimoire.remote;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.container.gui.GuiMantraShitf;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraCommon;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraElementWhirl;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.util.VariableSet;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;
import yuzunyannn.elementalsorcery.util.render.RenderHelper;
import yuzunyannn.elementalsorcery.util.render.RenderObjects;
import yuzunyannn.elementalsorcery.util.render.TextureBinder;
import yuzunyannn.elementalsorcery.util.world.WorldLocation;

public class FMantraElementWhirl extends FMantraBase {

	public FMantraElementWhirl(MantraCommon mantra) {
		setMaxCharge(16384 * 16384);
		setChargeSpeedRatio(0.0002);
		setMinChargeRatio(0.1);
		setIconRes(mantra.getIconResource());
	}

	@Override
	public void cast(World world, BlockPos pos, WorldLocation to, VariableSet content) {
		double charge = content.get(CHARGE);
		MantraElementWhirl.booom(to.getWorld(world), new Vec3d(to.getPos()).add(0.5, 0.5, 0.5),
				ElementStack.magic((int) ElementHelper.fromFragmentByPower(ESInit.ELEMENTS.MAGIC, charge, 100), 100),
				null);

	}

	@Override
	public void renderIcon(float suggestSize, float alpha, float partialTicks) {
		ResourceLocation res = getIconRes();
		if (res == null) res = RenderObjects.MANTRA_VOID;
		TextureBinder.bindTexture(GuiMantraShitf.CIRCLE);
		RenderHelper.drawTexturedRectInCenter(0, 0, suggestSize, suggestSize);
		GlStateManager.color(1, 1, 1, alpha);
		TextureBinder.bindTexture(res);
		suggestSize = suggestSize * 0.5f;
		RenderHelper.drawTexturedRectInCenter(0, 0, suggestSize, suggestSize);
	}

}

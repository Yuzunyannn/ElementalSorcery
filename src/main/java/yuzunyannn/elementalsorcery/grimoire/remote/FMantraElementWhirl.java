package yuzunyannn.elementalsorcery.grimoire.remote;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.element.ElementTransition;
import yuzunyannn.elementalsorcery.api.util.render.ESResources;
import yuzunyannn.elementalsorcery.api.util.render.RenderFriend;
import yuzunyannn.elementalsorcery.api.util.render.TextureBinder;
import yuzunyannn.elementalsorcery.api.util.target.WorldLocation;
import yuzunyannn.elementalsorcery.api.util.var.VariableSet;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraCommon;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraElementWhirl;

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
				ElementStack.magic((int) ElementTransition.fromFragmentByPower(ESObjects.ELEMENTS.MAGIC, charge, 100), 100),
				null);

	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderIcon(float suggestSize, float alpha, float partialTicks) {
		ResourceLocation res = getIconRes();
		if (res == null) res = ESResources.MANTRA_VOID.getResource();
		ESResources.MANTRA_COMMON_CIRCLE.bind();
		RenderFriend.drawTextureRectInCenter(0, 0, suggestSize, suggestSize);
		GlStateManager.color(1, 1, 1, alpha);
		TextureBinder.bindTexture(res);
		suggestSize = suggestSize * 0.5f;
		RenderFriend.drawTextureRectInCenter(0, 0, suggestSize, suggestSize);
	}

}

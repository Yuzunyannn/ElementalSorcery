package yuzunyannn.elementalsorcery.grimoire.remote;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.container.gui.GuiMantraShitf;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraCommon;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraFootbridge;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.util.VariableSet;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;
import yuzunyannn.elementalsorcery.util.render.RenderHelper;
import yuzunyannn.elementalsorcery.util.render.RenderObjects;
import yuzunyannn.elementalsorcery.util.render.TextureBinder;
import yuzunyannn.elementalsorcery.util.world.WorldLocation;

public class FMantraFlyIsland extends FMantraBase {

	public FMantraFlyIsland() {
		addCanUseElementWithSameLevel(ESInit.ELEMENTS.EARTH);
		setMaxCharge(new ElementStack(ESInit.ELEMENTS.EARTH, 600, 400));
		setMinChargeRatio(0.75);
		setChargeSpeedRatio(0.002);
		setIconRes("textures/mantras/f_fly_island.png");
	}

	@Override
	public void cast(World world, BlockPos pos, WorldLocation to, VariableSet content) {
		World toWorld = to.getWorld(world);
		BlockPos toPos = to.getPos();
		double charge = content.get(CHARGE);

		final int maxRange = (int) MathHelper.clamp(charge / this.maxCharge * 12, 2, 12);
		int range = maxRange;
		int upHight = 60 + toWorld.rand.nextInt(20);
		LinkedList<BlockPos> posList = new LinkedList<>();
		Set<BlockPos> checkSet = new HashSet<>();
		for (int y = range; y > 0; y--) {
			for (int x = -range; x <= range; x++) {
				for (int z = -range; z <= range; z++) {
					BlockPos at = toPos.add(x, y + upHight, z);
					float dis = MathHelper.sqrt(x * x + z * z);
					if (dis > range) continue;
					if (y < maxRange) {
						float r = y / (float) maxRange;
						if (r < toWorld.rand.nextFloat()) continue;
						if (!checkSet.contains(at.up())) continue;
					}
					if (!toWorld.isOutsideBuildHeight(at) && BlockHelper.isReplaceBlock(toWorld, at)) posList.add(at);
					checkSet.add(at);
				}
			}
			range--;
		}

		VariableSet parmas = new VariableSet();
		parmas.set(MantraCommon.LAYER, (short) 175);
		parmas.set(MantraCommon.POTENT_POWER, 0.5f);
		parmas.set(MantraCommon.POWERI, (int) ElementHelper.fromFragmentByCount(ESInit.ELEMENTS.EARTH, charge, 500));
		parmas.set(MantraFootbridge.POS_LIST, posList);
		parmas.set(MantraCommon.POS, toPos);
		MantraCommon.fireMantra(toWorld, ESInit.MANTRAS.FOOTBRIDGE, null, parmas);
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

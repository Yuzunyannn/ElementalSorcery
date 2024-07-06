package yuzunyannn.elementalsorcery.render.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import yuzunyannn.elementalsorcery.api.util.render.RenderFriend;
import yuzunyannn.elementalsorcery.grimoire.Grimoire;
import yuzunyannn.elementalsorcery.logics.EventClient;
import yuzunyannn.elementalsorcery.render.item.RenderItemGrimoireInfo;
import yuzunyannn.elementalsorcery.tile.altar.TileTranscribeTable;

public class RenderTileTranscribeTable extends TileEntitySpecialRenderer<TileTranscribeTable> {

	@Override
	public void render(TileTranscribeTable tile, double x, double y, double z, float partialTicks, int destroyStage,
			float alpha) {
		ItemStack stack = tile.getStack();
		if (stack.isEmpty()) return;
		GlStateManager.pushMatrix();
		Grimoire grimoire = stack.getCapability(Grimoire.GRIMOIRE_CAPABILITY, null);
		if (grimoire == null) {
			double f = MathHelper.sin((EventClient.tickRender + partialTicks) / 20f) * 0.025 + 0.1;
			GlStateManager.translate(x + 0.5, y + 0.2 + f, z + 0.5);
			RenderFriend.layItemPositionFix(stack);
		} else {
			RenderItemGrimoireInfo info = grimoire.getRenderInfo();
			info.bookSpreadPrev = info.bookSpread = 1;
			info.tickCount = EventClient.tickRender;
			float _float = MathHelper.cos(EventClient.getGlobalRotateInRender(partialTicks) / 180 * 3.1415f);
			GlStateManager.translate(x + 0.5, y + 0.65 + _float * 0.02f, z + 0.5);
			GlStateManager.rotate(90, 0, 0, 1);
		}
		Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
		GlStateManager.popMatrix();
	}

}

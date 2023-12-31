package yuzunyannn.elementalsorcery.nodegui;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.util.client.RenderFriend;
import yuzunyannn.elementalsorcery.render.tile.RenderTileElementReactor;
import yuzunyannn.elementalsorcery.util.TextHelper;

@SideOnly(Side.CLIENT)
public class GItemStack extends GNode {

	public static boolean inDraw = false;

	protected ItemStack stack = ItemStack.EMPTY;

	public GItemStack() {

	}

	public GItemStack(ItemStack stack) {
		setStack(stack);
	}

	public void setStack(ItemStack stack) {
		this.stack = stack;
		this.width = 16;
		this.height = 16;
	}

	public ItemStack getStack() {
		return stack;
	}

	@Override
	protected void render(float partialTicks) {
		String s = null;
		if (stack.getCount() > 64)
			s = TextFormatting.WHITE.toString() + TextHelper.toAbbreviatedNumber(stack.getCount(), 0);
		else if (stack.getCount() > 1) s = TextFormatting.WHITE.toString() + stack.getCount();
		GlStateManager.pushMatrix();
//		GlStateManager.pushAttrib();
		RenderHelper.enableGUIStandardItemLighting();
		GlStateManager.enableRescaleNormal();
		RenderItem itemRender = mc.getRenderItem();
		itemRender.zLevel = -150;
		inDraw = true;
		itemRender.renderItemAndEffectIntoGUI(stack, -7, -8);
		itemRender.renderItemOverlayIntoGUI(mc.fontRenderer, stack, -7, -8, s);
		inDraw = false;
		itemRender.zLevel = 0;
		GlStateManager.disableRescaleNormal();
		GlStateManager.disableCull();
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
		RenderHelper.disableStandardItemLighting();
//		GlStateManager.popAttrib();
		GlStateManager.popMatrix();
	}

}

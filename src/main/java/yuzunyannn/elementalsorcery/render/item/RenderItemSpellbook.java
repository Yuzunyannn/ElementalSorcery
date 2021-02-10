package yuzunyannn.elementalsorcery.render.item;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.capability.Spellbook;
import yuzunyannn.elementalsorcery.render.IRenderItem;
import yuzunyannn.elementalsorcery.render.model.ModelSpellbook;
import yuzunyannn.elementalsorcery.util.render.TextureBinder;

@SideOnly(Side.CLIENT)
public class RenderItemSpellbook implements IRenderItem {

	static public RenderItemSpellbook instance = new RenderItemSpellbook();

	public final TextureBinder TEXTURE_ENCHANTING_BOOK = new TextureBinder("minecraft",
			"textures/entity/enchanting_table_book.png");
	public final TextureBinder TEXTURE_SPELLBOOK = new TextureBinder("textures/items/book/spellbook_01.png");
	public final TextureBinder TEXTURE_SPELLBOOK_ARC = new TextureBinder("textures/items/book/spellbook_02.png");
	public final TextureBinder TEXTURE_SPELLBOOK_LAUNCH = new TextureBinder("textures/items/book/spellbook_03.png");
	public final TextureBinder TEXTURE_SPELLBOOK_ELEMENT = new TextureBinder("textures/items/book/spellbook_04.png");

	private final ModelSpellbook modelBook = new ModelSpellbook(1);

	public RenderItemSpellbook() {

	}

	@Override
	public void render(ItemStack stack, float partialTicks) {
		SpellbookRenderInfo info = stack.getCapability(Spellbook.SPELLBOOK_CAPABILITY, null).renderInfo;
		GlStateManager.pushMatrix();
		GlStateManager.enableCull();
		if (IRenderItem.isGUI(stack)) {
			GlStateManager.translate((float) 0.275F, (float) 0.55F, (float) 0.5F);
			GlStateManager.rotate(-55, 0.0F, 0.0F, 1.0F);
			GlStateManager.rotate(-50, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(-40, 0.0F, 1.0F, 0.0F);
			info.texture.bind();
			GlStateManager.scale(0.09F, 0.09F, 0.09F);
			this.modelBook.render(null, 0, 0, 0, 0, 0.0F, 1.0F);
		} else if (stack.getItemFrame() != null) {
			GlStateManager.translate(0.75f, 0.55F, 0.5F);
			GlStateManager.rotate(180, 0.0F, 1.0F, 0.0F);
			info.texture.bind();
			GlStateManager.scale(0.09F, 0.09F, 0.09F);
			this.modelBook.render(null, 0, 0, 0, 0, 0.0F, 1.0F);
		} else {

			float dtick = (float) info.tickCount + partialTicks;
			// 计算展开
			float spread = info.bookSpreadPrev + (info.bookSpread - info.bookSpreadPrev) * partialTicks;
			// 设置位移
			GlStateManager.translate((float) 0.3F + spread * 0.2F,
					(float) 0.75F + spread * (MathHelper.sin(dtick * 0.025F) * 0.5F + 0.5F) * 0.15F, (float) 0.6F);
			// 计算旋转
			float drotation;
			for (drotation = info.bookRotation
					- info.bookRotationPrev; drotation >= (float) Math.PI; drotation -= ((float) Math.PI * 2F))
				;
			while (drotation < -(float) Math.PI) {
				drotation += ((float) Math.PI * 2F);
			}
			float rotation = info.bookRotationPrev + drotation * partialTicks;
			GlStateManager.rotate(-rotation * (180F / (float) Math.PI), 0.0F, 1.0F, 0.0F);
			// 计算翻页
			float flip_right = info.pageFlipPrev + (info.pageFlip - info.pageFlipPrev) * partialTicks + 0.25F;
			float flip_left = info.pageFlipPrev + (info.pageFlip - info.pageFlipPrev) * partialTicks + 0.75F;
			flip_right = (flip_right - (float) MathHelper.fastFloor((double) flip_right)) * 1.6F - 0.3F;
			flip_left = (flip_left - (float) MathHelper.fastFloor((double) flip_left)) * 1.6F - 0.3F;
			if (flip_right < 0.0F) flip_right = 0.0F;
			if (flip_left < 0.0F) flip_left = 0.0F;
			if (flip_right > 1.0F) flip_right = 1.0F;
			if (flip_left > 1.0F) flip_left = 1.0F;
			// 渲染
			GlStateManager.scale(0.0625F, 0.0625F, 0.0625F);
			info.texture.bind();
			this.modelBook.render(null, dtick, flip_right, flip_left, spread, 0.0F, 1.0F);
		}
		GlStateManager.popMatrix();
	}
}

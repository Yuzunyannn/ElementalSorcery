package yuzunyannn.elementalsorcery.render.item;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.client.IRenderItem;
import yuzunyannn.elementalsorcery.api.util.client.TextureBinder;
import yuzunyannn.elementalsorcery.logics.EventClient;
import yuzunyannn.elementalsorcery.render.effect.grimoire.EffectIceCrystalBomb;
import yuzunyannn.elementalsorcery.util.helper.Color;

@SideOnly(Side.CLIENT)
public class RenderItemMemoryFragment implements IRenderItem {

	public final static TextureBinder TEXTURE = new TextureBinder("textures/items/memory_fragment.png");

	@Override
	public void render(ItemStack stack, float partialTicks) {
		GlStateManager.pushMatrix();
		TEXTURE.bind();

		if (IRenderItem.isGUI(stack)) {
			GlStateManager.translate(0.5, 0.5, 0.5);
			GlStateManager.scale(0.25, 0.25, 0.25);
			GlStateManager.rotate(EventClient.getGlobalRotateInRender(partialTicks), 0, 1, 0);
			renderModel(stack);
		} else {
			if (IRenderItem.isTransform(stack, TransformType.GROUND)) {
				GlStateManager.translate(0.5, 0.5, 0.5);
				GlStateManager.scale(0.075, 0.075, 0.075);
				renderModel(stack);
			} else if (IRenderItem.isTransform(stack, TransformType.FIXED)) {
				GlStateManager.translate(0.5, 0.5, 0.5);
				GlStateManager.rotate(-90, 1, 0, 0);
				GlStateManager.scale(0.2, 0.2, 0.2);
				renderModel(stack);
			} else {
				GlStateManager.translate(0.5, 0.52, 0.5);
				GlStateManager.scale(0.075, 0.075, 0.075);
				GlStateManager.rotate(EventClient.getGlobalRotateInRender(partialTicks), 0, 1, 0);
				renderModel(stack);
			}
		}

		GlStateManager.popMatrix();
	}

	private void renderModel(ItemStack stack) {
		EnumDyeColor dyColor = EnumDyeColor.WHITE;
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt != null) {
			int cmeta = nbt.getInteger("cmeta");
			EnumDyeColor[] colors = EnumDyeColor.values();
			dyColor = cmeta < 0 ? colors[(EventClient.tick / 4) % colors.length] : EnumDyeColor.byMetadata(cmeta);
		}
		Color color = new Color(dyColor.getColorValue());
		GlStateManager.color(color.r, color.g, color.b);
		GlStateManager.disableLighting();

//		GlStateManager.enableColorLogic();
//		GlStateManager.colorLogicOp(LogicOp.XOR);

		EffectIceCrystalBomb.MODEL.render();
		GlStateManager.rotate(90, 1, 0, 0);
		EffectIceCrystalBomb.MODEL.render();
		GlStateManager.rotate(90, 0, 0, 1);
		EffectIceCrystalBomb.MODEL.render();

		GlStateManager.enableLighting();
//		GlStateManager.disableColorLogic();
	}

}

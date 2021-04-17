package yuzunyannn.elementalsorcery.render.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.capability.ElementInventory;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.event.EventClient;
import yuzunyannn.elementalsorcery.render.IRenderItem;
import yuzunyannn.elementalsorcery.render.model.ModelElementCube;
import yuzunyannn.elementalsorcery.tile.altar.TileElementalCube;
import yuzunyannn.elementalsorcery.util.obj.Vertex;
import yuzunyannn.elementalsorcery.util.render.RenderHelper;
import yuzunyannn.elementalsorcery.util.render.TextureBinder;

@SideOnly(Side.CLIENT)
public class RenderTileElementalCube extends TileEntitySpecialRenderer<TileElementalCube> implements IRenderItem {

	static public final TextureBinder TEXTURE = new TextureBinder("textures/blocks/element_cube.png");
	static public final TextureBinder TEXTURE_COVER = new TextureBinder("textures/blocks/element_cube_cover.png");
	static public final ModelElementCube MODEL = new ModelElementCube();

	static public final TextureBinder TEXTURE_ITEM = new TextureBinder("textures/blocks/element_cube_item.png");
	static public final TextureBinder TEXTURE_ITEM_COVER = new TextureBinder("textures/blocks/element_cube_item_cover.png");

	public RenderTileElementalCube() {
	}

	@Override
	public void render(TileElementalCube tile, double x, double y, double z, float partialTicks, int destroyStage,
			float alpha) {
		super.render(tile, x, y, z, partialTicks, destroyStage, alpha);
		RenderHelper.startRender(x + 0.5, y + 0.3, z + 0.5, 0.5, alpha);
		GlStateManager.scale(0.04, 0.04, 0.04);

		float wake = RenderHelper.getPartialTicks(tile.wakeRate, tile.preWakeRate, partialTicks);
		TEXTURE.bind();
		MODEL.render(null, wake, EventClient.tick + partialTicks, 0, 0, 0, 1);
		GlStateManager.color(tile.color.x * tile.colorRate + (1.0F - tile.colorRate) * TileElementalCube.ORIGIN_COLOR.x,
				tile.color.y * tile.colorRate + (1.0F - tile.colorRate) * TileElementalCube.ORIGIN_COLOR.y,
				tile.color.z * tile.colorRate + (1.0F - tile.colorRate) * TileElementalCube.ORIGIN_COLOR.z);
		TEXTURE_COVER.bind();
		MODEL.render(null, wake, EventClient.tick + partialTicks, 0, 0, 0, 1);

		RenderHelper.endRender();
	}

	@Override
	public void render(ItemStack stack, float partialTicks) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(0.5F, 0.5F, 0.5F);
		if (stack.isOnItemFrame()) GlStateManager.scale(0.5F, 0.5F, 0.5F);
		else if (IRenderItem.isGUI(stack)) {
			GlStateManager.rotate(45.0F, 1, 1, 1);
			GlStateManager.scale(0.5F, 0.5F, 0.5F);
		} else if (IRenderItem.isTransform(stack, TransformType.FIXED)) {
			GlStateManager.scale(0.5F, 0.5F, 0.5F);
		} else {
			GlStateManager.translate(0.0F, 0.025F, 0.0F);
			GlStateManager.scale(0.2F, 0.2F, 0.2F);
		}

		GlStateManager.scale(0.0325, 0.0325, 0.0325);
		TEXTURE_ITEM.bind();
		MODEL.render(null, 0, 0, 0, 0, 0, 1);

		Vertex color = TileElementalCube.ORIGIN_COLOR;
		IElementInventory inventory = stack.getCapability(ElementInventory.ELEMENTINVENTORY_CAPABILITY, null);
		if (inventory != null) {
			inventory.loadState(stack);
			ElementStack estack = inventory.getStackInSlot(0);
			if (!estack.isEmpty()) color = new Vertex().toColor(estack.getColor());
		}

		GlStateManager.color(color.x, color.y, color.z);
		TEXTURE_ITEM_COVER.bind();
		MODEL.render(null, 0, 0, 0, 0, 0, 1);

		GlStateManager.popMatrix();
	}

//	static public final TextureBinder TEXTURE_OLD = new TextureBinder("textures/blocks/elemental_cube.png");
//	static public final TextureBinder TEXTURE_OLD_OVER = new TextureBinder("textures/blocks/elemental_cube_cover.png");

//	@Override
//	public void render(TileElementalCube tile, double x, double y, double z, float partialTicks, int destroyStage,
//			float alpha) {
//		super.render(tile, x, y, z, partialTicks, destroyStage, alpha);
//		float rotate = tile.rotate + TileElementalCube.SOTATE_PRE_TICK * partialTicks;
//		float wake = tile.wake > 0 ? tile.wakeUp + TileElementalCube.WAKE_UP_RARE * partialTicks
//				: tile.wakeUp - TileElementalCube.WAKE_UP_RARE * partialTicks;
//		wake = MathHelper.clamp(wake, 0, 1);
//		GlStateManager.disableLighting();
//		RenderHelper.startRender(x + 0.5, y + tile.getHigh(rotate, wake), z + 0.5, 0.5, alpha);
//		GlStateManager.rotate(tile.getRoate((float) (rotate * 180.0f / Math.PI), wake), 1, 1, 1);
//		TEXTURE_OLD.bind();
//		MODEL_OLD.render();
//		GlStateManager.color(tile.color.x * tile.colorRate + (1.0F - tile.colorRate) * TileElementalCube.ORIGIN_COLOR.x,
//				tile.color.y * tile.colorRate + (1.0F - tile.colorRate) * TileElementalCube.ORIGIN_COLOR.y,
//				tile.color.z * tile.colorRate + (1.0F - tile.colorRate) * TileElementalCube.ORIGIN_COLOR.z);
//		TEXTURE_OLD_OVER.bind();
//		MODEL_OLD.render();
//		RenderHelper.endRender();
//	}
//
//	@Override
//	public void render(ItemStack stack, float partialTicks) {
//		GlStateManager.pushMatrix();
//		GlStateManager.translate(0.5F, 0.5F, 0.5F);
//		if (stack.isOnItemFrame()) GlStateManager.scale(0.5F, 0.5F, 0.5F);
//		else if (IRenderItem.isGUI(stack)) {
//			GlStateManager.rotate(45.0F, 1, 1, 1);
//			GlStateManager.scale(0.5F, 0.5F, 0.5F);
//		} else if (IRenderItem.isTransform(stack, TransformType.FIXED)) {
//			GlStateManager.translate(0.0F, 0.275F, 0.0F);
//			GlStateManager.scale(0.5F, 0.5F, 0.5F);
//		} else {
//			GlStateManager.disableLighting();
//			GlStateManager.translate(0.0F, 0.025F, 0.0F);
//			GlStateManager.scale(0.25F, 0.25F, 0.25F);
//		}
//		TEXTURE_OLD.bind();
//		MODEL_OLD.render();
//		IElementInventory inventory = stack.getCapability(ElementInventory.ELEMENTINVENTORY_CAPABILITY, null);
//		if (inventory != null) {
//			inventory.loadState(stack);
//			ElementStack estack = inventory.getStackInSlot(0);
//			if (!estack.isEmpty()) {
//				Vertex color = new Vertex().toColor(estack.getColor());
//				GlStateManager.color(color.x, color.y, color.z);
//				TEXTURE_OLD_OVER.bind();
//				MODEL_OLD.render();
//			}
//
//		}
//		GlStateManager.popMatrix();
//		GlStateManager.enableLighting();
//	}

//	private Model MODEL_OLD = new Model();
//
//	private void init() {
//		// ��
//		MODEL_OLD.addFace(new Vertex(-0.5F, -0.5F, -0.5F), new Vertex(0.5F, -0.5F, -0.5F),
//				new Vertex(0.5F, -0.5F, 0.5F), new Vertex(-0.5F, -0.5F, 0.5F)).setTexVetices(new Vertex(0.0F, 0.0F),
//						new Vertex(1.0F, 0.0F), new Vertex(1.0F, 1.0F), new Vertex(0.0F, 1.0F));
//		// ��
//		MODEL_OLD.addFace(new Vertex(-0.5F, 0.5F, -0.5F), new Vertex(-0.5F, 0.5F, 0.5F), new Vertex(0.5F, 0.5F, 0.5F),
//				new Vertex(0.5F, 0.5F, -0.5F)).setTexVetices(new Vertex(0.0F, 0.0F), new Vertex(0.0F, 1.0F),
//						new Vertex(1.0F, 1.0F), new Vertex(1.0F, 0.0F));
//		// ��
//		MODEL_OLD.addFace(new Vertex(-0.5F, -0.5F, -0.5F), new Vertex(-0.5F, -0.5F, 0.5F),
//				new Vertex(-0.5F, 0.5F, 0.5F), new Vertex(-0.5F, 0.5F, -0.5F)).setTexVetices(new Vertex(0.0F, 0.0F),
//						new Vertex(0.0F, 1.0F), new Vertex(1.0F, 1.0F), new Vertex(1.0F, 0.0F));
//		// ��
//		MODEL_OLD.addFace(new Vertex(0.5F, -0.5F, -0.5F), new Vertex(0.5F, 0.5F, -0.5F), new Vertex(0.5F, 0.5F, 0.5F),
//				new Vertex(0.5F, -0.5F, 0.5F)).setTexVetices(new Vertex(0.0F, 0.0F), new Vertex(1.0F, 0.0F),
//						new Vertex(1.0F, 1.0F), new Vertex(0.0F, 1.0F));
//		// ��
//		MODEL_OLD.addFace(new Vertex(-0.5F, -0.5F, -0.5F), new Vertex(-0.5F, 0.5F, -0.5F),
//				new Vertex(0.5F, 0.5F, -0.5F), new Vertex(0.5F, -0.5F, -0.5F)).setTexVetices(new Vertex(0.0F, 0.0F),
//						new Vertex(0.0F, 1.0F), new Vertex(1.0F, 1.0F), new Vertex(1.0F, 0.0F));
//		// ǰ
//		MODEL_OLD.addFace(new Vertex(-0.5F, -0.5F, 0.5F), new Vertex(0.5F, -0.5F, 0.5F), new Vertex(0.5F, 0.5F, 0.5F),
//				new Vertex(-0.5F, 0.5F, 0.5F)).setTexVetices(new Vertex(0.0F, 0.0F), new Vertex(1.0F, 0.0F),
//						new Vertex(1.0F, 1.0F), new Vertex(0.0F, 1.0F));
//	}

}

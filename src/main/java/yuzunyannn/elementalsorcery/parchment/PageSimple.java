package yuzunyannn.elementalsorcery.parchment;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class PageSimple extends Page {

	private final String name;
	private final ItemStack icon;
	private final ItemStack blockground;

	public PageSimple(String name) {
		this.name = name;
		this.icon = ItemStack.EMPTY;
		this.blockground = ItemStack.EMPTY;
	}

	public PageSimple(String name, ItemStack icon) {
		this.name = name;
		this.icon = icon;
		this.blockground = ItemStack.EMPTY;
	}

	public PageSimple(String name, Item icon) {
		this(name, new ItemStack(icon));
	}

	public PageSimple(String name, Block icon) {
		this(name, new ItemStack(icon));
	}

	public PageSimple(String name, ItemStack icon, ItemStack background) {
		this.name = name;
		this.icon = icon;
		this.blockground = background;
	}

	@Override
	public String getTitle() {
		return "page." + name;
	}

	@Override
	public String getContext() {
		return "page." + name + ".ct";
	}

	@Override
	public ItemStack getIcon() {
		return icon;
	}

	@Override
	public void drawBackground(GuiContainer gui, int offsetX, int offsetY) {
		if (this.blockground.isEmpty())
			return;
		RenderItem itemRender = Minecraft.getMinecraft().getRenderItem();
		IBakedModel bakedmodel = itemRender.getItemModelWithOverrides(blockground, (World) null,
				(EntityLivingBase) null);
		GlStateManager.pushMatrix();
		GlStateManager.translate(offsetX + 175, offsetY + 110, 100.0F);
		GlStateManager.translate(8.0F, 8.0F, 0.0F);
		GlStateManager.scale(1.0F, -1.0F, 1.0F);
		GlStateManager.scale(16.0F, 16.0F, 16.0F);
		GlStateManager.scale(5f, 5f, 5f);
		itemRender.renderItem(this.blockground, bakedmodel);
		GlStateManager.popMatrix();
	}
}

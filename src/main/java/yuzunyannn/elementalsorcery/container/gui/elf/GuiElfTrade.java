package yuzunyannn.elementalsorcery.container.gui.elf;

import java.util.List;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.container.ContainerElfTrade;
import yuzunyannn.elementalsorcery.elf.trade.TradeList;
import yuzunyannn.elementalsorcery.util.TextHelper;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

public class GuiElfTrade extends GuiContainer {

	public static final ResourceLocation TEXTURE = new ResourceLocation(ESAPI.MODID,
			"textures/gui/elf/elf_trade.png");

	public final ContainerElfTrade container;

	public GuiElfTrade(EntityPlayer player) {
		super(new ContainerElfTrade(player));
		container = (ContainerElfTrade) inventorySlots;
		this.ySize = 185;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		String s = container.elf == null ? "" : container.elf.getElfName();
		this.fontRenderer.drawString(s, this.xSize / 2 - this.fontRenderer.getStringWidth(s) / 2, 6, 4210752);
		this.fontRenderer.drawString(container.player.inventory.getDisplayName().getUnformattedText(), 8,
				this.ySize - 96 + 2, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1, 1, 1);
		this.mc.getTextureManager().bindTexture(TEXTURE);
		int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(offsetX, offsetY, 0, 0, this.xSize, this.ySize);
		if (container.trade == null) return;
		for (int i = 0; i < container.trade.getTradeListSize(); i++) {
			TradeList.TradeInfo info = container.trade.getTradeInfo(i);
			int cost = container.trade.cost(i);
			int xoff = (i % 6) * 17 + offsetX + 16;
			int yoff = (i / 6) * 21 + offsetY + 34;
			GlStateManager.color(1, 1, 1);
			this.mc.getTextureManager().bindTexture(TEXTURE);
			if (info.isReclaim()) this.drawTexturedModalRect(xoff, yoff, 176 + 3, 0, 3, 3);
			else this.drawTexturedModalRect(xoff, yoff, 176, 0, 3, 3);
			if (container.trade.stock(i) <= 0) {
				GlStateManager.pushMatrix();
				GlStateManager.translate(xoff + 1, yoff - 17, 300);
				GlStateManager.scale(0.4, 0.4, 1);
				GlStateManager.rotate(45, 0, 0, 1);
				this.drawTexturedModalRect(0, 0, 176, 3, 47, 11);
				GlStateManager.popMatrix();
			}
			GlStateManager.pushMatrix();
			final float scale = 0.5f;
			GlStateManager.scale(scale, scale, 1);
			String s = TextHelper.toAbbreviatedNumber(cost, 2);
			this.fontRenderer.drawString(s, (int) ((xoff + 4) / scale), (int) ((yoff - 1) / scale + 1),
					EnumDyeColor.BLACK.getColorValue());
			GlStateManager.popMatrix();
		}
	}

	@Override
	public List<String> getItemToolTip(ItemStack itemStack) {
		List<String> list = super.getItemToolTip(itemStack);
		if (list.isEmpty()) return list;
		for (int i = 0; i < container.trade.getTradeListSize(); i++) {
			TradeList.TradeInfo info = container.trade.getTradeInfo(i);
			if (ItemHelper.areItemsEqual(info.getCommodity(), itemStack)) {
				String str = I18n.format("info.elf.goods.coin", info.getCost());
				if (info.isReclaim()) str = TextFormatting.RED + str;
				else str = TextFormatting.GREEN + str;
				list.add(0, str);
				break;
			}
		}
		return list;
	}

}

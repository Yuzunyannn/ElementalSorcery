package yuzunyannn.elementalsorcery.container.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.container.ContainerRiteManual;
import yuzunyannn.elementalsorcery.tile.TileRiteTable;

@SideOnly(Side.CLIENT)
public class GuiRiteManual extends GuiContainer {

	public static final ResourceLocation TEXTURE = new ResourceLocation(ESAPI.MODID,
			"textures/gui/rite_manual.png");
	final ContainerRiteManual container;

	public GuiRiteManual(ContainerRiteManual inventorySlotsIn) {
		super(inventorySlotsIn);
		container = inventorySlotsIn;
		this.xSize = 256;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
		mc.getTextureManager().bindTexture(TEXTURE);
		drawTexturedModalRect(offsetX, offsetY, 0, 0, this.xSize, this.ySize);
		if (!container.getSlot(36).getHasStack()) return;
		if (container.power == 0) {
			Level lev = Level.UNSUITED;
			String str = I18n.format("lev." + lev.name().toLowerCase());
			this.fontRenderer.drawString(str, offsetX + 150, offsetY + 20, lev.getColor());
		} else if (container.power < 0) {
			Level lev = Level.DEFECTIVE;
			String str = I18n.format("lev." + lev.name().toLowerCase());
			this.fontRenderer.drawString(str, offsetX + 150, offsetY + 20, lev.getColor());
		} else {

			Level[] levels = toLevels(container.level, container.power);
			for (int i = 0; i < levels.length; i++) {
				Level lev = levels[i];
				int y = i * this.fontRenderer.FONT_HEIGHT;
				String str = I18n.format("info.level", i);
				this.fontRenderer.drawString(str, offsetX + 150, offsetY + 20 + y, 0);
				str = I18n.format("lev." + lev.name().toLowerCase());
				this.fontRenderer.drawString(str, offsetX + 175, offsetY + 20 + y, lev.getColor());
			}
		}
		if (container.summonShow) this.fontRenderer.drawString("★", offsetX + 135, offsetY + 5, 0xda003e);
	}

	public static Level[] toLevels(int level, int power) {
		Level[] levels = new Level[TileRiteTable.MAX_LEVEL + 1];
		for (int i = 0; i <= TileRiteTable.MAX_LEVEL; i++) {
			Level lev = Level.UNSUITED;
			if (i >= level) {
				int pw = power / (i + 1 - level);
				lev = Level.getLevel(pw);
			}
			levels[i] = lev;
		}
		return levels;
	}

	public static enum Level {
		DEFECTIVE(EnumDyeColor.GRAY),
		UNSUITED(EnumDyeColor.GRAY),
		SUBORDINATE(EnumDyeColor.GREEN),
		INTERMEDIATE(EnumDyeColor.LIGHT_BLUE),
		SENIOR(EnumDyeColor.PURPLE),
		SUPER(EnumDyeColor.YELLOW);

		static Level getLevel(int power) {
			if (power == 0) return UNSUITED;
			else if (power < 0) return DEFECTIVE;
			else if (power <= 10) return SUBORDINATE;
			else if (power <= 30) return INTERMEDIATE;
			else if (power <= 60) return SENIOR;
			return SUPER;
		}

		final EnumDyeColor color;

		Level(EnumDyeColor color) {
			this.color = color;
		}

		public int getColor() {
			return color.getColorValue();
		}

		public EnumDyeColor getDyeColor() {
			return color;
		}
	}

}

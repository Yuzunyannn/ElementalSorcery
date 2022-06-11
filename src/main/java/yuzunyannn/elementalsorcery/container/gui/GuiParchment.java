package yuzunyannn.elementalsorcery.container.gui;

import java.io.IOException;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.building.Building;
import yuzunyannn.elementalsorcery.building.BuildingBlocks;
import yuzunyannn.elementalsorcery.container.ContainerParchment;
import yuzunyannn.elementalsorcery.parchment.IPageManager;
import yuzunyannn.elementalsorcery.parchment.Page;
import yuzunyannn.elementalsorcery.parchment.Pages;

public class GuiParchment extends GuiContainer implements IPageManager {

	public static final ResourceLocation TEXTURE = new ResourceLocation(ElementalSorcery.MODID,
			"textures/gui/parchment.png");
	public static final ResourceLocation TEXTURE_EXTRA = new ResourceLocation(ElementalSorcery.MODID,
			"textures/gui/parchment_book.png");
	protected final ContainerParchment container;
	/** 物品槽按钮计数 */
	protected List<SlotButton> slotButtonList = Lists.<SlotButton>newArrayList();
	/** 处理使用的页面 */
	protected Page page;
	/** 下一页按钮 */
	protected PageButton nextButton;
	/** 上一页按钮 */
	protected PageButton prevButton;
	/** 下一页按钮ID */
	protected final int NEXT_BUTTON_ID = 0;
	/** 上一页按钮ID */
	protected final int PREV_BUTTON_ID = 1;
	/** 需求重新初始化 */
	protected boolean needInit = false;
	/** 将要去的页面 */
	protected Page toPage = null;

	public GuiParchment(ContainerParchment inventorySlotsIn) {
		super(inventorySlotsIn);
		this.xSize = 256;
		this.container = (ContainerParchment) inventorySlotsIn;
		this.page = Pages.getPage(this.container.heldItem);
		this.page.open(this);
	}

	@Override
	public void initGui() {
		super.initGui();
		this.init();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		try {
			GlStateManager.color(1, 1, 1);
			this.mc.getTextureManager().bindTexture(TEXTURE);
			int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
			this.drawTexturedModalRect(offsetX, offsetY, 0, 0, this.xSize, this.ySize);
			this.page.drawBackground(offsetX, offsetY, this);
			for (SlotButton slot : this.slotButtonList) if (!slot.stack.isEmpty()) drawItem(slot.stack, slot.x, slot.y);
			// 文字
			GlStateManager.pushMatrix();
			GlStateManager.translate(offsetX, offsetY, 10);
			this.page.drawValue(this);
			GlStateManager.popMatrix();
		} catch (Exception e) {
			String id = this.page == null ? "null" : this.page.getId();
			ElementalSorcery.logger.warn("羊皮卷(" + id + ")gui:drawGuiContainerBackgroundLayer异常", e);
			this.toPage = Pages.getErrorPage();
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		try {
			if (button instanceof SlotButton) this.page.slotAction(button.id, this);
			else if (button instanceof PageButton) {
				if (button.id == PREV_BUTTON_ID) this.page.pageAction(false, this);
				else this.page.pageAction(true, this);
			} else {
				this.page.customButtonAction(button, this);
			}
		} catch (Exception e) {
			String id = this.page == null ? "null" : this.page.getId();
			ElementalSorcery.logger.warn("羊皮卷(" + id + ")gui:actionPerformed异常", e);
			this.toPage = Pages.getErrorPage();
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		if (mouseButton == 1) {
			if (this.page.prevPage != null) {
				this.toPage = this.page.prevPage;
				this.gotoPage(false);
			}
		}
		this.page.mouseClick(mouseX, mouseY, mouseButton, this);
	}

	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
		super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
		this.page.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick, this);
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		if (this.toPage != null) this.gotoPage(true);
		if (needInit) {
			this.init();
			needInit = false;
		}
		try {
			this.page.update(this);
		} catch (Exception e) {
			String id = this.page == null ? "null" : this.page.getId();
			ElementalSorcery.logger.warn("羊皮卷(" + id + ")gui:update异常", e);
			this.toPage = Pages.getErrorPage();
		}
	}

	private void gotoPage(boolean rPrevPage) {
		if (this.toPage == this.page) {
			this.toPage = null;
			return;
		}
		if (rPrevPage) {
			if (this.page.getId() != null) this.toPage.prevPage = this.page;
			else this.toPage.prevPage = this.page.prevPage;
		}
		this.page = this.toPage;
		this.toPage = null;
		this.needInit = true;
	}

	/** 表述一个物品的按钮槽 */
	class SlotButton extends GuiButton {

		ItemStack stack = ItemStack.EMPTY;

		public SlotButton(int buttonId, int x, int y, ItemStack stack) {
			super(buttonId, x, y, 16, 16, null);
			this.stack = stack;
		}

		public void setItem(ItemStack stack) {
			this.stack = stack;
		}

		public ItemStack getItem() {
			return this.stack;
		}

		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
			if (this.visible) {
				int x = mouseX - this.x, y = mouseY - this.y;
				if (x >= 0 && y >= 0 && x < this.width && y < this.height) {
					GlStateManager.disableLighting();
					GlStateManager.disableDepth();
					GlStateManager.colorMask(true, true, true, false);
					this.drawGradientRect(this.x, this.y, this.x + this.width, this.y + this.height, -2130706433,
							-2130706433);
					GlStateManager.colorMask(true, true, true, true);

					if (stack.isEmpty()) return;
					renderToolTip(stack, mouseX, mouseY);
				}
			}
		}
	}

	/** 翻页按钮 */
	class PageButton extends GuiButton {
		final int textureOffsetY;

		public PageButton(int buttonId, int x, int y, boolean next) {
			super(buttonId, x, y, 18, 10, null);
			if (next) textureOffsetY = 166;
			else textureOffsetY = 176;
		}

		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
			if (this.visible) {
				GlStateManager.disableLighting();
				GlStateManager.color(1, 1, 1);
				mc.getTextureManager().bindTexture(TEXTURE);
				int x = mouseX - this.x, y = mouseY - this.y;
				if (x >= 0 && y >= 0 && x < this.width && y < this.height)
					this.drawTexturedModalRect(this.x, this.y, 23, textureOffsetY, this.width, this.height);
				else this.drawTexturedModalRect(this.x, this.y, 0, textureOffsetY, this.width, this.height);
			}
		}

	}

	/** 当切换页面之后，进行一次初始化 */
	public void init() {
		this.buttonList.clear();
		this.slotButtonList.clear();
		this.nextButton = this.prevButton = null;
		this.page.init(this);
	}

	@Override
	public GuiContainer getGui() {
		return this;
	}

	@Override
	public void reinit() {
		this.needInit = true;
	}

	@Override
	public int getAxisOff(boolean isX) {
		if (isX) return (this.width - this.xSize) / 2;
		else return (this.height - this.ySize) / 2;
	}

	@Override
	public void addCustomButton(GuiButton button) {
		this.buttonList.add(button);
	}

	@Override
	public int addSlot(int x, int y, ItemStack stack) {
		int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
		SlotButton button = new SlotButton(slotButtonList.size(), offsetX + x, offsetY + y, stack);
		this.buttonList.add(button);
		this.slotButtonList.add(button);
		return this.slotButtonList.size() - 1;
	}

	@Override
	public void setSlot(int slot, ItemStack stack) {
		this.slotButtonList.get(slot).setItem(stack);
	}

	@Override
	public ItemStack getSlot(int slot) {
		return this.slotButtonList.get(slot).getItem();
	}

	@Override
	public int getSlots() {
		return this.slotButtonList.size();
	}

	@Override
	public void setSlotState(int slot, boolean visible) {
		this.slotButtonList.get(slot).visible = visible;
	}

	@Override
	public void toPage(Page page) {
		this.toPage = page;
	}

	@Override
	public void setNextButton(boolean has) {
		if (has) {
			if (this.nextButton == null) {
				int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
				this.nextButton = new PageButton(NEXT_BUTTON_ID, offsetX + 221, offsetY + 146, true);
				this.buttonList.add(this.nextButton);
			} else this.nextButton.visible = true;
		} else {
			if (this.nextButton != null) this.nextButton.visible = false;
		}
	}

	@Override
	public void setPrevButton(boolean has) {
		if (has) {
			if (this.prevButton == null) {
				int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
				this.prevButton = new PageButton(PREV_BUTTON_ID, offsetX + 17, offsetY + 146, false);
				this.buttonList.add(this.prevButton);
			} else this.prevButton.visible = true;
		} else {
			if (this.prevButton != null) this.prevButton.visible = false;
		}
	}

	@Override
	public int getFontHeight() {
		return this.fontRenderer.FONT_HEIGHT;
	}

	@Override
	public void drawString(String str, int x, int y, int color) {
		this.fontRenderer.drawString(str, x, y, color);
	}

	@Override
	public int drawString(String str, int x, int y, int width, int color) {
		if (str == null) return 0;
		for (String s : this.fontRenderer.listFormattedStringToWidth(str, width)) {
			this.fontRenderer.drawString(s, x, y, color);
			y += this.fontRenderer.FONT_HEIGHT;
		}
		return y;
	}

	@Override
	public void drawTitle(String str, int x, int y, int xSize, int color) {
		this.fontRenderer.drawString(str, x + xSize / 2 - this.fontRenderer.getStringWidth(str) / 2, y, color);
	}

	@Override
	public void drawItem(ItemStack stack, int x, int y) {
		RenderHelper.enableGUIStandardItemLighting();
		this.zLevel = 100.0F;
		this.itemRender.zLevel = 100.0F;
		String s = null;
		if (stack.getCount() > 1) {
			s = TextFormatting.WHITE.toString() + stack.getCount();
		}
		this.itemRender.renderItemAndEffectIntoGUI(this.mc.player, stack, x, y);
		this.itemRender.renderItemOverlayIntoGUI(this.fontRenderer, stack, x, y, s);
		this.zLevel = 0F;
		this.itemRender.zLevel = 0F;
	}

	@Override
	public void drawBuilding(Building building, int x, int y, float roateX, float roateY, float roateZ, float scale) {
		try {
			GlStateManager.pushMatrix();
//			yuzunyannn.elementalsorcery.util.render.RenderHelper.disableLightmap(true);
			RenderHelper.disableStandardItemLighting();
			// 移动到位置
			GlStateManager.translate(x, y, 512);
			// 绑定逻辑材质
			mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			GlStateManager.rotate(roateX, 1, 0, 0);
			GlStateManager.rotate(roateZ, 0, 0, 1);
			GlStateManager.rotate(roateY, 0, 1, 0);
			scale *= 10;
			GlStateManager.scale(scale, scale, scale);
			GlStateManager.translate(-0.5, -0.5, -0.5);
			// 获取必要绘图实例
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferbuilder = tessellator.getBuffer();
			BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
			BlockModelRenderer render = blockrendererdispatcher.getBlockModelRenderer();
			// 建筑遍历器
			BuildingBlocks iter = building.getBuildingIterator();
			// 开始
			while (iter.next()) {
				BlockPos blockpos = iter.getPos();
				blockpos = new BlockPos(blockpos.getX(), blockpos.getY(), blockpos.getZ());
				IBlockState iblockstate = iter.getState();
				if (iblockstate.getRenderType() != EnumBlockRenderType.MODEL) {
					if (iblockstate.getBlock() instanceof BlockContainer) {
						try {
							TileEntity tile = iblockstate.getBlock().createTileEntity(mc.world, iblockstate);
							TileEntitySpecialRenderer<TileEntity> tileRender = TileEntityRendererDispatcher.instance
									.getRenderer(tile);
							if (tileRender == null) continue;
							tileRender.render(tile, blockpos.getX(), blockpos.getY(), blockpos.getZ(),
									mc.getRenderPartialTicks(), -1, 1);
						} catch (Exception e) {}
						// 再次绑定
						mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
					}
				} else {
					GlStateManager.disableCull();
					bufferbuilder.begin(7, DefaultVertexFormats.BLOCK);
					render.renderModelFlat(mc.world, blockrendererdispatcher.getModelForState(iblockstate), iblockstate,
							blockpos, bufferbuilder, false, MathHelper.getPositionRandom(blockpos));
					tessellator.draw();
				}
			}
			RenderHelper.enableStandardItemLighting();
			GlStateManager.popMatrix();
		} catch (Exception e) {
			String id = this.page == null ? "null" : this.page.getId();
			ElementalSorcery.logger.warn("羊皮卷(" + id + ")gui:drawBuilding异常", e);
			this.toPage = Pages.getErrorPage();
		}
	}

}

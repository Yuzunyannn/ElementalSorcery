package yuzunyannn.elementalsorcery.container.gui;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

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
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.building.Building;
import yuzunyannn.elementalsorcery.container.ContainerParchment;
import yuzunyannn.elementalsorcery.event.EventClient;
import yuzunyannn.elementalsorcery.item.ItemKynaiteTools;
import yuzunyannn.elementalsorcery.parchment.Page;
import yuzunyannn.elementalsorcery.parchment.PageCraftingTemp;
import yuzunyannn.elementalsorcery.parchment.Pages;

public class GuiParchment extends GuiContainer {

	public final static int NONE = 0;
	public final static int CRAFTING = 1;
	public final static int TRANSFORM = 2;
	public final static int BUILDING = 3;
	public final static int CATALOG = 4;

	public final static int CATALOG_LOCAL_X = 20;
	public final static int CATALOG_LOCAL_Y = 20;
	public final static int CATALOG_LOCAL_INTERVAL = 25;

	private static final ResourceLocation TEXTURE = new ResourceLocation(ElementalSorcery.MODID,
			"textures/gui/parchment.png");
	private ContainerParchment container;
	private Page.PageSate page_state = Page.PageSate.EMPTY;
	private int show = NONE;
	private boolean has_extra = false;
	private boolean has_list = false;

	public GuiParchment(Container inventorySlotsIn) {
		super(inventorySlotsIn);
		this.xSize = 256;
		this.container = (ContainerParchment) inventorySlotsIn;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		this.mc.getTextureManager().bindTexture(TEXTURE);
		int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(offsetX, offsetY, 0, 0, this.xSize, this.ySize);

		container.page.drawBackground(this, offsetX, offsetY);

		// 画合成表
		if (this.show == CRAFTING) {
			NonNullList<Ingredient> ingList = container.page.getCrafting();
			int cX = this.getDefaultCX(offsetX);
			int cY = this.getDefaultCY(offsetY);
			this.drawCraftingBackGround(ingList.size(), cX, cY);
			this.drawCrafting(cX, cY, ingList, container.page.getOutput());
		}
		// 画转化
		else if (this.show == TRANSFORM) {
			int cX = this.getDefaultCX(offsetX);
			int cY = this.getDefaultCY(offsetY);
			this.drawTransformBackGround(this.container.page.getTransformGui(), cX, cY);
			this.drawTransform(cX, cY, container.page.getItemList(), container.page.getOrigin(),
					container.page.getOutput(), container.page.getExtra());
		}
		// 画建筑
		else if (this.show == BUILDING) {
			int cX = offsetX + (int) (this.xSize * 0.55f);
			int cY = offsetY + (int) (this.ySize * 0.6f);
			this.drawBuilding(cX, cY, this.container.page.getBuilding());
		} // 画目录
		else if (this.show == CATALOG) {
			this.drawCatalog(offsetX + CATALOG_LOCAL_X, offsetY + CATALOG_LOCAL_Y, this.container.page.getCatalog());
		}
	}

	private int getDefaultCX(int offsetX) {
		if (this.page_state == Page.PageSate.EXCLUSIVE)
			return offsetX + 101 + (this.show == TRANSFORM ? 5 : 0);
		return offsetX + 147 + (this.show == TRANSFORM ? 5 : 0);
	}

	private int getDefaultCY(int offsetY) {
		if (this.page_state == Page.PageSate.EXCLUSIVE)
			return offsetY + 55 + (this.show == TRANSFORM ? 10 : 0);
		return offsetY + 44 + (this.show == TRANSFORM ? 10 : 0);
	}

	private int getUseXSize() {
		switch (this.page_state) {
		case SMALL:
			return (int) (this.xSize * 0.575f);
		case BIG:
			return (int) (this.xSize * 0.4f);
		default:
			return this.xSize;
		}
	}

	// 信息
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		String str;
		// 计算最右边
		int use_xSize = this.getUseXSize();
		// 画标题
		str = container.page.getTitle();
		if (str != null) {
			str = I18n.format(str);
			this.fontRenderer.drawString(str, use_xSize / 2 - this.fontRenderer.getStringWidth(str) / 2, 6, 4210752);
		}
		// 画内容
		if (this.page_state != Page.PageSate.EXCLUSIVE) {
			LinkedList<String> list = new LinkedList<String>();
			// 添加内容
			container.page.addContexts(list);
			// 如果存在内容
			if (!list.isEmpty()) {
				int width = use_xSize / 10 * 8;
				int yoff = 13 + this.fontRenderer.FONT_HEIGHT;
				// 显示
				for (String s : list) {
					yoff = this.drawSplitString(s, use_xSize / 10, yoff, width, 4210752);
				}
			}
		} else {
			// 特殊情况，画目录
			if (this.show == CATALOG) {
				this.drawCatalogString(CATALOG_LOCAL_X, CATALOG_LOCAL_Y, this.container.page.getCatalog());
			}
		}
	}

	// 更新
	@Override
	public void updateScreen() {
		super.updateScreen();
		container.page.onUpdate();
	}

	final int PRE_PAGE = 0;
	GuiButton pre_page;
	final int NEXT_PAGE = 1;
	GuiButton next_page;
	final int PRE_ITEM = 2;
	GuiButton pre_item;
	final int NEXT_ITEM = 3;
	GuiButton next_item;
	final int ITEM_START = 4;
	GuiButton[] item_buts;

	private int list_at = 0;

	// 重置所有按钮状态
	public void resetButtonState() {
		int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
		// 重置
		pre_page.visible = container.page.prePage() > 0;
		next_page.visible = container.page.nextPage() > 0;
		pre_item.visible = false;
		next_item.visible = false;
		// 检测各种状态
		this.page_state = container.page.getState();
		NonNullList<Ingredient> ingList = container.page.getCrafting();
		int[] cat = container.page.getCatalog();
		if (cat != null) {
			this.show = CATALOG;
		} else if (this.page_state != Page.PageSate.EMPTY) {
			if (this.container.page.getBuilding() != null)
				this.show = BUILDING;
			else {
				this.show = (ingList != null) ? CRAFTING : NONE;
				if (this.show == NONE) {
					this.show = container.page.getOrigin().isEmpty() ? NONE : TRANSFORM;
				}
			}
		} else
			this.show = NONE;

		// 设置可用的按钮
		if (this.show == CRAFTING) {
			// 重新初始化按钮位置
			int cX = this.getDefaultCX(offsetX) + 1;
			int cY = this.getDefaultCY(offsetY) + 1;
			boolean only_nine = ingList.size() <= 9;
			// 重新设置按钮，并初始化位置
			for (int i = 0; i < item_buts.length; i++) {
				if (only_nine && i >= 9)
					item_buts[i].visible = false;
				else
					item_buts[i].visible = true;
				int x = ContainerParchment.craftingRelative[i * 2];
				int y = ContainerParchment.craftingRelative[i * 2 + 1];
				item_buts[i].x = x + cX;
				item_buts[i].y = y + cY;
			}
		} else if (this.show == TRANSFORM) {
			// 重新初始化按钮位置
			int cX = this.getDefaultCX(offsetX) + 1;
			int cY = this.getDefaultCY(offsetY) + 1;
			item_buts[0].visible = true;
			item_buts[0].x = cX;
			item_buts[0].y = cY;
			item_buts[1].visible = true;
			item_buts[1].x = cX + 45;
			item_buts[1].y = cY;
			for (int i = 2; i < item_buts.length; i++)
				item_buts[i].visible = false;
			this.has_extra = !this.container.page.getExtra().isEmpty();
			this.has_list = this.container.page.getItemList() != null && !this.container.page.getItemList().isEmpty();
			if (has_extra) {
				item_buts[2].visible = true;
				item_buts[2].x = cX + 4 + 18;
				item_buts[2].y = cY + 18;
			}
			// 有list
			if (has_list) {
				for (int i = 3; i < 7; i++) {
					item_buts[i].visible = true;
					item_buts[i].x = cX - 9 + 5 + (i - 3) * 18;
					item_buts[i].y = cY + 37;
				}
				pre_item.visible = true;
				cX--;
				cY--;
				pre_item.x = cX - 9;
				pre_item.y = cY + 37 + 4;
				next_item.visible = true;
				next_item.x = cX - 9 + 5 + 18 * 4;
				next_item.y = cY + 37 + 4;
			}
		} else if (this.show == CATALOG) {
			// 重新初始化按钮位置
			int cX = offsetX + CATALOG_LOCAL_X + 1;
			int cY = offsetY + CATALOG_LOCAL_Y + 1;
			for (int i = 0; i < item_buts.length; i++)
				item_buts[i].visible = false;
			for (int i = 0; i < cat.length; i++) {
				item_buts[i].visible = true;
				item_buts[i].x = cX;
				item_buts[i].y = cY + i * CATALOG_LOCAL_INTERVAL;
			}
		} else {
			for (int i = 0; i < item_buts.length; i++)
				item_buts[i].visible = false;
		}
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		switch (button.id) {
		case PRE_PAGE:
			if (container.page.getId() == this.container.page.prePage()) {
				container.page.prePageUpdate();
				this.resetButtonState();
			} else
				this.changePage(this.container.page.prePage());
			break;
		case NEXT_PAGE:
			if (container.page.getId() == this.container.page.nextPage()) {
				container.page.nextPageUpdate();
				this.resetButtonState();
			} else
				this.changePage(this.container.page.nextPage());
			break;
		case PRE_ITEM:
			if (list_at > 0)
				this.list_at--;
			break;
		case NEXT_ITEM:
			if (list_at < this.container.page.getItemList().size() - 1)
				this.list_at++;
			break;
		default:
			if (button.id >= ITEM_START && button.id < ITEM_START + 25)
				this.actionItem(button.id - ITEM_START);
			else
				super.actionPerformed(button);
			return;
		}
	}

	protected void actionItem(int index) {
		int id = this.container.page.getCraftingTo(index);
		if (id > 0) {
			this.changePage(id);
			return;
		}
		ItemStack stack = this.getItem(index);

		if (stack.isEmpty())
			return;
		id = this.findDefaultPageId(stack);
		if (id > 0) {
			this.changePage(id);
			return;
		}
		PageCraftingTemp page = new PageCraftingTemp(stack);
		if (page.test())
			this.changePage(page);
	}

	private ItemStack getItem(int index) {
		if (this.show == CRAFTING) {
			NonNullList<Ingredient> ingList = container.page.getCrafting();
			if (index < ingList.size()) {
				ItemStack[] stacks = ingList.get(index).getMatchingStacks();
				if (stacks.length > 0)
					return stacks[EventClient.rand_int % stacks.length];
			}
		} else if (this.show == TRANSFORM) {
			switch (index) {
			case 0:
				return container.page.getOrigin();
			case 1:
				return container.page.getOutput();
			case 2:
				return container.page.getExtra();
			case 3:
			case 4:
			case 5:
			case 6:
				List<ItemStack> list = this.container.page.getItemList();
				if (index - 3 < list.size())
					return list.get(index - 3);
			}
		}
		return ItemStack.EMPTY;
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		if (mouseButton == 1) {
			this.backPage();
		}
		if (mouseButton == 0) {
			last_x = mouseX;
			last_y = mouseY;
		}
	}

	private int last_x;
	private int last_y;
	private float rX, rY, rZ;

	// 鼠标移动
	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
		super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
		if (clickedMouseButton == 0) {
			int dx = mouseX - last_x;
			int dy = last_y - mouseY;
			last_x = mouseX;
			last_y = mouseY;
			rY += dx;
			rX += dy;
		}
	}

	public void backPage() {
		int back_id = container.page.back();
		if (back_id <= 0)
			return;
		container.changePage(back_id, true);
		this.resetButtonState();
	}

	public void changePage(int id) {
		if (id == container.page.getId())
			return;
		container.changePage(id, false);
		this.resetButtonState();
	}

	public void changePage(Page page) {
		container.changePage(page);
		this.resetButtonState();
	}

	public int findDefaultPageId(ItemStack stack) {
		Item item = stack.getItem();
		if (item instanceof ItemKynaiteTools.toolsCapability)
			return Pages.ABOUT_KYNATIE_TOOLS;
		else {
			for (Entry<Item, Integer> entry : Pages.Item_Id) {
				if (entry.getKey() == item)
					return entry.getValue();
			}
		}
		return -1;
	}

	@Override
	public void initGui() {
		super.initGui();
		int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
		// 前一个页面的按钮
		pre_page = new GuiButton(PRE_PAGE, offsetX + 17, offsetY + 146, 18, 10, null) {
			@Override
			public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
				if (this.visible) {
					mc.getTextureManager().bindTexture(TEXTURE);
					int x = mouseX - this.x, y = mouseY - this.y;
					if (x >= 0 && y >= 0 && x < this.width && y < this.height) {
						this.drawTexturedModalRect(this.x, this.y, 23, 176, this.width, this.height);
					} else {
						this.drawTexturedModalRect(this.x, this.y, 0, 176, this.width, this.height);
					}
				}
			}
		};
		// 下一页的按钮
		next_page = new GuiButton(NEXT_PAGE, offsetX + 221, offsetY + 146, 18, 10, null) {
			@Override
			public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
				if (this.visible) {
					mc.getTextureManager().bindTexture(TEXTURE);
					int x = mouseX - this.x, y = mouseY - this.y;
					if (x >= 0 && y >= 0 && x < this.width && y < this.height) {
						this.drawTexturedModalRect(this.x, this.y, 23, 166, this.width, this.height);
					} else {
						this.drawTexturedModalRect(this.x, this.y, 0, 166, this.width, this.height);
					}
				}
			}
		};
		// 前一个物品按钮
		pre_item = new GuiButton(PRE_ITEM, 0, 0, 5, 9, null) {
			@Override
			public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
				if (this.visible) {
					mc.getTextureManager().bindTexture(TEXTURE);
					int x = mouseX - this.x, y = mouseY - this.y;
					if (x >= 0 && y >= 0 && x < this.width && y < this.height) {
						this.drawTexturedModalRect(this.x, this.y, 10, 186, this.width, this.height);
					} else {
						this.drawTexturedModalRect(this.x, this.y, 0, 186, this.width, this.height);
					}
				}
			}
		};
		// 后一个物品按钮
		next_item = new GuiButton(NEXT_ITEM, 0, 0, 5, 9, null) {
			@Override
			public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
				if (this.visible) {
					mc.getTextureManager().bindTexture(TEXTURE);
					int x = mouseX - this.x, y = mouseY - this.y;
					if (x >= 0 && y >= 0 && x < this.width && y < this.height) {
						this.drawTexturedModalRect(this.x, this.y, 15, 186, this.width, this.height);
					} else {
						this.drawTexturedModalRect(this.x, this.y, 5, 186, this.width, this.height);
					}
				}
			}
		};
		// item按钮
		item_buts = new GuiButton[25];
		for (int i = 0; i < item_buts.length; i++) {
			item_buts[i] = new ItemButton(i + ITEM_START);
		}
		// 添加按钮
		this.buttonList.add(pre_page);
		this.buttonList.add(next_page);
		this.buttonList.add(pre_item);
		this.buttonList.add(next_item);
		for (int i = 0; i < item_buts.length; i++)
			this.buttonList.add(item_buts[i]);
		// 复位
		this.resetButtonState();
	}

	class ItemButton extends GuiButton {

		public ItemButton(int buttonId) {
			super(buttonId, 0, 0, 16, 16, null);
		}

		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
			if (this.visible) {
				mc.getTextureManager().bindTexture(TEXTURE);
				int x = mouseX - this.x, y = mouseY - this.y;
				if (x >= 0 && y >= 0 && x < this.width && y < this.height) {

					GlStateManager.disableLighting();
					GlStateManager.disableDepth();
					GlStateManager.colorMask(true, true, true, false);
					this.drawGradientRect(this.x, this.y, this.x + this.width, this.y + this.height, -2130706433,
							-2130706433);
					GlStateManager.colorMask(true, true, true, true);
					GlStateManager.enableLighting();
					GlStateManager.enableDepth();

					int index = this.id - ITEM_START;
					ItemStack stack = getItem(index);
					if (stack.isEmpty())
						return;
					renderToolTip(stack, mouseX, mouseY);
				}
			}
		}

	}

	// 分割显示
	public int drawSplitString(String str, int x, int y, int width, int color) {
		if (str == null)
			return 0;
		for (String s : this.fontRenderer.listFormattedStringToWidth(str, width)) {
			this.fontRenderer.drawString(s, x, y, color);
			y += this.fontRenderer.FONT_HEIGHT;
		}
		return y;
	}

	// 画合成台界面
	private void drawCraftingBackGround(int size, int cX, int cY) {
		if (size > 9) {
			this.drawTexturedModalRect(cX, cY, 41, 166, 54, 54);
			this.drawTexturedModalRect(cX - 36, cY - 36, 41, 166, 36, 36);
			this.drawTexturedModalRect(cX + 54, cY - 36, 41, 166, 36, 36);
			this.drawTexturedModalRect(cX - 36, cY + 54, 41, 166, 36, 36);
			this.drawTexturedModalRect(cX + 54, cY + 54, 41, 166, 36, 36);
		} else
			this.drawTexturedModalRect(cX, cY, 41, 166, 54, 54);
	}

	// 画合转化界面
	private void drawTransformBackGround(int type, int cX, int cY) {
		this.drawTexturedModalRect(cX, cY, 95, 166, 63, 18);
		if (type == 1) {
			this.drawTexturedModalRect(cX + 18, cY, 95 + 18, 166 + 18, 27, 18);
		} else if (type == 2) {
			this.drawTexturedModalRect(cX + 18, cY, 95 + 18, 166 + 54, 27, 18);
		} else if (type == 3) {
			this.drawTexturedModalRect(cX + 18, cY, 95 + 18, 166 + 72, 27, 18);
		} else {
			this.drawTexturedModalRect(cX, cY + 18, 95, 166 + 18, 18, 18);
			this.drawTexturedModalRect(cX + 18, cY, 95 + 18, 166 + 36, 27, 18);
		}
		if (has_extra) {
			this.drawTexturedModalRect(cX + 18 + 4, cY + 18, 95, 166, 18, 18);
		}
		if (has_list) {
			this.drawTexturedModalRect(cX - 9 + 5, cY + 37, 41, 166, 72, 18);
		}

	}

	// 画合成台物品
	private void drawCrafting(int cX, int cY, NonNullList<Ingredient> ingList, ItemStack output) {
		cX++;
		cY++;
		this.startDrawItem();
		for (int i = 0; i < ingList.size(); i++) {
			int x = ContainerParchment.craftingRelative[i * 2];
			int y = ContainerParchment.craftingRelative[i * 2 + 1];
			ItemStack[] stacks = ingList.get(i).getMatchingStacks();
			if (stacks.length == 0)
				continue;
			ItemStack stack = stacks[EventClient.rand_int % stacks.length];
			this.drawOnceItem(stack, x + cX, y + cY);
		}
		this.drawOnceItem(output, cX + 18, cY + 64);
		this.endDrawItem();
	}

	// 画合转化
	private void drawTransform(int cX, int cY, List<ItemStack> list, ItemStack... stacks) {
		this.startDrawItem();
		cX++;
		cY++;
		this.drawOnceItem(stacks[0], cX, cY);
		this.drawOnceItem(stacks[1], cX + 45, cY);
		if (this.has_extra) {
			this.drawOnceItem(stacks[2], cX + 18 + 4, cY + 18);
		}
		if (this.has_list) {
			for (int i = list_at; i < list.size(); i++) {
				ItemStack stack = list.get(i);
				this.drawOnceItem(stack, cX - 9 + 5 + (i - list_at) * 18, cY + 37);
			}
		}
		this.endDrawItem();
	}

	// 开始画物品
	protected void startDrawItem() {
		RenderHelper.enableGUIStandardItemLighting();
		this.zLevel = 200.0F;
		this.itemRender.zLevel = 200.0F;
	}

	// 结束画物品
	protected void endDrawItem() {
		this.zLevel = 0.0F;
		this.itemRender.zLevel = 0F;
		RenderHelper.enableStandardItemLighting();
	}

	// 画一次物品
	protected void drawOnceItem(ItemStack stack, int x, int y) {
		String s = null;
		if (stack.getCount() > 1) {
			s = TextFormatting.WHITE.toString() + stack.getCount();
		}
		this.itemRender.renderItemAndEffectIntoGUI(this.mc.player, stack, x, y);
		this.itemRender.renderItemOverlayIntoGUI(this.fontRenderer, stack, x, y, s);
	}

	// 画目录
	private void drawCatalog(int cx, int cy, int[] ids) {
		if (ids == null)
			return;
		for (int i = 0; i < ids.length; i++) {
			int x = cx;
			int y = cy + i * CATALOG_LOCAL_INTERVAL;
			this.drawTexturedModalRect(x, y, 41, 166, 18, 18);
		}
		startDrawItem();
		for (int i = 0; i < ids.length; i++) {
			int x = cx;
			int y = cy + i * CATALOG_LOCAL_INTERVAL + 4;
			Page page = Pages.getPage(ids[i]);
			ItemStack stack = page.getIcon();
			if (stack.isEmpty())
				stack = new ItemStack(Blocks.BARRIER);
			drawOnceItem(stack, x + 1, y - 3);
		}
		endDrawItem();
	}

	// 画目录文字
	private void drawCatalogString(int cx, int cy, int[] ids) {
		if (ids == null || ids.length == 0) {
			this.fontRenderer.drawString(I18n.format("page.catalog.none"), cx, cy, 4210752);
			return;
		}
		for (int i = 0; i < ids.length; i++) {
			int x = cx + 20;
			int y = cy + i * CATALOG_LOCAL_INTERVAL + 4;
			Page page = Pages.getPage(ids[i]);
			String str = I18n.format(page.getTitle());
			int width = this.fontRenderer.getStringWidth(str);
			str += " - - - - - - - - - - - - - - - - - - - - - - - - -".substring(0, (180 - width) / 4);
			this.fontRenderer.drawString(str, x, y, 4210752);
			this.fontRenderer.drawString(page.getId() + "", x + 185, y, 4210752);
		}
	}

	// 测试更改数据
	static public float s = 10.0f;
	static public float t = 5.75f;

	// 画一个建筑
	private void drawBuilding(int x, int y, Building building) {
		GlStateManager.pushMatrix();
		RenderHelper.disableStandardItemLighting();
		// 移动到位置
		GlStateManager.translate(x, y, 512);
		// 绑定逻辑材质
		this.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		// 获取建筑的边框
		AxisAlignedBB box = building.getBox();
		int lx = (int) (box.maxX - box.minX);
		int lz = (int) (box.maxZ - box.minZ);
		int length = Math.max(lx, lz);
		length = length - 5;
		if (length < 0)
			length = 0;
		double size = 2 - length * 0.1f;
		double tmove = -t * size;
		GlStateManager.translate(tmove, tmove, tmove);
		GlStateManager.rotate(rY, 0, 1, 0);
		GlStateManager.rotate(rX, 1, 0, 0);
		GlStateManager.rotate(rZ, 0, 0, 1);
		GlStateManager.translate(-tmove, -tmove, -tmove);
		double scale = -s * size;
		GlStateManager.scale(scale, scale, scale);
		// 获取必要绘图实例
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
		BlockModelRenderer render = blockrendererdispatcher.getBlockModelRenderer();
		// 建筑遍历器
		Building.BuildingBlocks iter = building.getBuildingBlocks();
		// 开始
		bufferbuilder.begin(7, DefaultVertexFormats.BLOCK);
		while (iter.next()) {
			BlockPos blockpos = iter.getPos();
			IBlockState iblockstate = iter.getState();
			render.renderModel(Minecraft.getMinecraft().world, blockrendererdispatcher.getModelForState(iblockstate),
					iblockstate, blockpos, bufferbuilder, true, MathHelper.getPositionRandom(blockpos));
		}
		tessellator.draw();
		RenderHelper.enableStandardItemLighting();
		GlStateManager.popMatrix();
	}

	/** 画物品 */
	public void renderItem(ItemStack stack, int x, int y) {
		IBakedModel bakedmodel = this.itemRender.getItemModelWithOverrides(stack, (World) null,
				(EntityLivingBase) null);
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, 100.0F + this.zLevel);
		GlStateManager.translate(8.0F, 8.0F, 0.0F);
		GlStateManager.scale(1.0F, -1.0F, 1.0F);
		GlStateManager.scale(16.0F, 16.0F, 16.0F);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 0F);
		GlStateManager.enableAlpha();
		this.itemRender.renderItem(stack, bakedmodel);
		GlStateManager.popMatrix();
	}
}

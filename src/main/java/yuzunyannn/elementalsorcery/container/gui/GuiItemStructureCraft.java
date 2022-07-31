package yuzunyannn.elementalsorcery.container.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import yuzunyannn.elementalsorcery.container.ContainerItemStructureCraft;
import yuzunyannn.elementalsorcery.tile.TileItemStructureCraftCC;
import yuzunyannn.elementalsorcery.util.helper.RandomHelper;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;
import yuzunyannn.elementalsorcery.util.render.RenderHelper;

public abstract class GuiItemStructureCraft extends GuiContainer {

	protected final ContainerItemStructureCraft container;

	protected List<SlotView> slotViewList = new ArrayList<>();
	protected int maxX, minX, maxY, minY;

	protected int slotMapCenterXOffset, slotMapCenterYOffset;
	protected int typeStackXOffset, typeStackYOffset;
	protected int ouputXOffset, ouputYOffset;
	protected boolean hasAnime = true;
	protected boolean hasTypeStack = true;

	class SlotView {
		public final int slotIndex;
		public final byte spIndex;
		public float x, prevX;
		public float y, prevY;
		public float scale, prevScale;

		public SlotView(int slotIndex, byte spIndex) {
			this.slotIndex = slotIndex;
			this.spIndex = (byte) spIndex;
		}

		public float getX(float partialTicks) {
			return hasAnime ? RenderHelper.getPartialTicks(x, prevX, partialTicks) : x;
		}

		public float getY(float partialTicks) {
			return hasAnime ? RenderHelper.getPartialTicks(y, prevY, partialTicks) : y;
		}

		public float getScale(float partialTicks) {
			if (!hasAnime) return 1;
			float scale = RenderHelper.getPartialTicks(this.scale, prevScale, partialTicks);
			return scale == 0 ? 0.00001f : scale;
		}

		public void update(int targetX, int targetY) {
			if (hasAnime) {
				this.prevScale = this.scale;
				this.prevX = this.x;
				this.prevY = this.y;

				if (scale < 1) scale = scale + MathHelper.sqrt(1 - scale) * 0.1f;
				this.x = this.x + (targetX - this.x) * 0.3f;
				this.y = this.y + (targetY - this.y) * 0.3f;
			} else {
				this.prevScale = this.scale = 1;
				this.prevX = this.x = targetX;
				this.prevY = this.y = targetY;
			}
		}

		public ItemStack getItemStack() {
			ItemStack stack = ItemStack.EMPTY;
			if (spIndex == ContainerItemStructureCraft.SP_OUTPUT) {
				stack = container.tileEntity.getOutput();
			} else if (spIndex == ContainerItemStructureCraft.SP_TYPE_STACK) {
				stack = container.tileEntity.getTypeStack();
			} else {
				int x = (slotIndex >> 16) & 0xffff;
				int y = slotIndex & 0xffff;
				stack = container.tileEntity.getSlotItemStack(x, y);
			}
			return stack;
		}
	}

	public GuiItemStructureCraft(ContainerItemStructureCraft inventorySlotsIn) {
		super(inventorySlotsIn);
		this.container = inventorySlotsIn;
	}

	@Override
	public void initGui() {
		super.initGui();
		this.initSlot();
	}

	protected abstract void initSlot();

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		partialTicks = mc.getRenderPartialTicks();
		this.drawBackground();
		this.drawSlotMap(partialTicks, mouseX, mouseY);
	}

	protected void drawBackground() {
		
	}

	protected void drawSlot(boolean hasSelect) {
		
	}

	protected void drawSlotMap(float partialTicks, int mouseX, int mouseY) {
		if (slotViewList.isEmpty()) return;

		for (SlotView view : slotViewList) {
			float dx = view.getX(partialTicks);
			float dy = view.getY(partialTicks);
			float scale = view.getScale(partialTicks);
			GlStateManager.translate(dx + 9, dy + 9, 0);
			if (scale < 0.9999f) GlStateManager.scale(scale, scale, scale);
			drawSlot(GuiNormal.isMouseIn(mouseX, mouseY, dx, dy, 18, 18));
			if (scale < 0.9999f) GlStateManager.scale(1 / scale, 1 / scale, 1 / scale);
			GlStateManager.translate(-dx - 9, -dy - 9, 0);
		}

		for (SlotView view : slotViewList) {
			ItemStack stack = view.getItemStack();
			if (stack.isEmpty()) continue;
			float dx = view.getX(partialTicks);
			float dy = view.getY(partialTicks);
			float scale = view.getScale(partialTicks);
			if (scale == 0) scale = 0.0001f;
			GlStateManager.translate(dx, dy, 0);
			if (scale < 0.99999f) {
				GlStateManager.translate(9, 9, 0);
				GlStateManager.scale(scale, scale, scale);
				GlStateManager.translate(-9, -9, 0);
			}
			drawItem(stack, 1, 1);
			if (scale < 0.99999f) {
				GlStateManager.translate(9, 9, 0);
				GlStateManager.scale(1 / scale, 1 / scale, 1 / scale);
				GlStateManager.translate(-9, -9, 0);
			}
			GlStateManager.translate(-dx, -dy, 0);
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);

		for (SlotView view : slotViewList) {
			ItemStack stack = view.getItemStack();
			if (stack.isEmpty()) continue;
			float dx = RenderHelper.getPartialTicks(view.x, view.prevX, partialTicks);
			float dy = RenderHelper.getPartialTicks(view.y, view.prevY, partialTicks);
			if (GuiNormal.isMouseIn(mouseX, mouseY, dx, dy, 18, 18)) {
				this.renderToolTip(stack, mouseX, mouseY);
			}
		}
	}

	@Override
	public void updateScreen() {
		super.updateScreen();

		int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
		int xLen = (maxX - minX + 1) * 18;
		int yLen = (maxY - minY + 1) * 18;
		int cX = offsetX + this.slotMapCenterXOffset - xLen / 2;
		int cY = offsetY + this.slotMapCenterYOffset - yLen / 2;

		if (!container.lastSlotMapName.equals(container.tileEntity.getTypeName())) {
			container.lastSlotMapName = container.tileEntity.getTypeName();
			slotViewList.clear();
			Collection<Integer> slotIndexs = container.tileEntity.getSlotIndexs();
			minX = minY = Integer.MAX_VALUE;
			maxX = maxY = Integer.MIN_VALUE;
			for (int slotIndex : slotIndexs) {
				int x = (slotIndex >> 16) & 0xffff;
				int y = slotIndex & 0xffff;
				if (minX > x) minX = x;
				if (minY > y) minY = y;
				if (maxX < x) maxX = x;
				if (maxY < y) maxY = y;
				SlotView view = new SlotView(slotIndex, (byte) 0);
				float dx = x * 18 - xLen / 2 - 9;
				float dy = y * 18 - yLen / 2 - 9;
				float dl = MathHelper.sqrt(dx * dx + dy * dy);
				if (dl != 0) {
					dx = dx / dl * RandomHelper.rand.nextFloat() * slotMapCenterXOffset;
					dy = dy / dl * RandomHelper.rand.nextFloat() * slotMapCenterYOffset;
				}
				view.prevX = view.x = dx + (cX + xLen / 2);
				view.prevY = view.y = dy + (cY + yLen / 2);
				slotViewList.add(view);
			}
			if (!slotViewList.isEmpty()) {
				SlotView view = new SlotView(-1, ContainerItemStructureCraft.SP_OUTPUT);
				view.prevX = view.x = cX + xLen / 2 - 9;
				view.prevY = view.y = cY + yLen / 2 - 9;
				slotViewList.add(view);
			}
			if (hasTypeStack) {
				SlotView view = new SlotView(-1, ContainerItemStructureCraft.SP_TYPE_STACK);
				view.prevX = view.x = offsetX + typeStackXOffset;
				view.prevY = view.y = offsetY + typeStackYOffset;
				slotViewList.add(view);
			}

			xLen = (maxX - minX + 1) * 18;
			yLen = (maxY - minY + 1) * 18;
			cX = offsetX + this.slotMapCenterXOffset - xLen / 2;
			cY = offsetY + this.slotMapCenterYOffset - yLen / 2;
		}

		for (SlotView view : slotViewList) {
			if (view.spIndex == ContainerItemStructureCraft.SP_OUTPUT) {
				view.update(offsetX + this.ouputXOffset - 9, offsetY + this.ouputYOffset - 9);
				continue;
			} else if (view.spIndex == ContainerItemStructureCraft.SP_TYPE_STACK) {
				view.update(offsetX + typeStackXOffset, offsetY + typeStackYOffset);
				continue;
			}
			int x = (view.slotIndex >> 16) & 0xffff;
			int y = view.slotIndex & 0xffff;
			int dx = x * 18;
			int dy = y * 18;
			view.update(cX + dx, cY + dy);
		}

		if (lastClickTick > 0) {
			lastClickTick--;
			if (lastClickTick == 0) lastRemove = ItemStack.EMPTY;
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		for (SlotView view : slotViewList) {
			if (view.spIndex == ContainerItemStructureCraft.SP_OUTPUT) continue;
			if (GuiNormal.isMouseIn(mouseX, mouseY, view.x, view.y, 18, 18)) {
				if (view.spIndex == ContainerItemStructureCraft.SP_TYPE_STACK) {
					this.sendOP(view.slotIndex, ContainerItemStructureCraft.OP_TSTACK);
					return;
				}
				int x = (view.slotIndex >> 16) & 0xffff;
				int y = view.slotIndex & 0xffff;
				mouseClickedSlot(x, y, mouseButton);
				return;
			}
		}
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
		for (SlotView view : slotViewList) {
			if (view.spIndex == ContainerItemStructureCraft.SP_OUTPUT) continue;
			else if (view.spIndex == ContainerItemStructureCraft.SP_TYPE_STACK) continue;
			if (GuiNormal.isMouseIn(mouseX, mouseY, view.x, view.y, 18, 18)) {
				int x = (view.slotIndex >> 16) & 0xffff;
				int y = view.slotIndex & 0xffff;
				mouseClickedMoveSlot(x, y, clickedMouseButton);
				return;
			}
		}
		super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
	}

	protected void sendOP(int x, int y, byte op) {
		int index = TileItemStructureCraftCC.getSlotIndex(x, y);
		sendOP(index, op);
	}

	protected void sendOP(int index, byte op) {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setByte("op", op);
		nbt.setInteger("si", index);
		container.dealSlotOP(index, op);
		container.sendToServer(nbt);
	}

	public int lastDealIndex = -1;
	public int lastClickTick = 0;
	public ItemStack lastRemove = ItemStack.EMPTY;

	protected void mouseClickedSlot(int x, int y, int mouseButton) {
		ItemStack playerHold = TileItemStructureCraftCC.getRealItemStack(container.player.inventory.getItemStack());
		ItemStack slotItem = container.tileEntity.getSlotItemStack(x, y);
		int index = TileItemStructureCraftCC.getSlotIndex(x, y);
		if (lastClickTick > 0 && lastDealIndex == index && mouseButton == 0) {
			lastClickTick = 0;
			lastDealIndex = -1;
			Map<Integer, ItemStack> slotMap = container.tileEntity.getSlotMap();
			for (Entry<Integer, ItemStack> entry : slotMap.entrySet()) {
				if (ItemHelper.areItemsEqual(entry.getValue(), lastRemove)) {
					this.sendOP(entry.getKey(), ContainerItemStructureCraft.OP_REMOVE_SAME);
					return;
				}
			}
			return;
		}
		lastDealIndex = index;
		lastClickTick = 3;
		if (playerHold.isEmpty()) {
			if (slotItem.isEmpty()) return;
			if (mouseButton == 0) {
				lastRemove = slotItem.copy();
				this.sendOP(x, y, ContainerItemStructureCraft.OP_REMOVE);
			} else if (mouseButton == 1) this.sendOP(x, y, ContainerItemStructureCraft.OP_REMOVE_HALF);
		} else {
			if (slotItem.isEmpty()) {
				if (mouseButton == 0) this.sendOP(x, y, ContainerItemStructureCraft.OP_ADD);
				else if (mouseButton == 1) this.sendOP(x, y, ContainerItemStructureCraft.OP_ADD_HALF);
			} else if (ItemHelper.areItemsEqual(playerHold, slotItem)) {
				if (mouseButton == 0) this.sendOP(x, y, ContainerItemStructureCraft.OP_ADD);
				else if (mouseButton == 1) this.sendOP(x, y, ContainerItemStructureCraft.OP_ADD_HALF);
			} else {
				if (mouseButton == 0) this.sendOP(x, y, ContainerItemStructureCraft.OP_ADD);
			}
		}
	}

	protected void mouseClickedMoveSlot(int x, int y, int mouseButton) {
		ItemStack playerHold = TileItemStructureCraftCC.getRealItemStack(container.player.inventory.getItemStack());
		ItemStack slotItem = container.tileEntity.getSlotItemStack(x, y);
		int index = TileItemStructureCraftCC.getSlotIndex(x, y);
		if (lastDealIndex == index) return;
		lastDealIndex = index;
		if (playerHold.isEmpty()) {
			if (slotItem.isEmpty()) return;
			if (mouseButton == 0) this.sendOP(x, y, ContainerItemStructureCraft.OP_REMOVE);
			else if (mouseButton == 1) this.sendOP(x, y, ContainerItemStructureCraft.OP_REMOVE_HALF);
		} else {
			if (slotItem.isEmpty()) {
				if (mouseButton == 0) this.sendOP(x, y, ContainerItemStructureCraft.OP_ADD);
				else if (mouseButton == 1) this.sendOP(x, y, ContainerItemStructureCraft.OP_ADD_HALF);
			} else if (ItemHelper.areItemsEqual(playerHold, slotItem)) {
				if (mouseButton == 0) this.sendOP(x, y, ContainerItemStructureCraft.OP_ADD);
				else if (mouseButton == 1) this.sendOP(x, y, ContainerItemStructureCraft.OP_ADD_HALF);
			}
		}
	}

	public void drawItem(ItemStack stack, int x, int y) {
		RenderHelper.enableGUIStandardItemLighting();
		this.zLevel = 100.0F;
		this.itemRender.zLevel = 100.0F;
		String s = null;
		if (stack.getCount() > 1) s = TextFormatting.WHITE.toString() + stack.getCount();
		this.itemRender.renderItemAndEffectIntoGUI(this.mc.player, stack, x, y);
		this.itemRender.renderItemOverlayIntoGUI(this.fontRenderer, stack, x, y, s);
		this.zLevel = 0F;
		this.itemRender.zLevel = 0F;
	}
}

package yuzunyannn.elementalsorcery.container.gui;

import java.io.IOException;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.container.ContainerSimpleMaterialContainer;

public class GuiSimpleMaterialContainer extends GuiNormal<ContainerSimpleMaterialContainer> {

	public static final ResourceLocation TEXTURE = new ResourceLocation(ESAPI.MODID,
			"textures/gui/container/simple_material_container.png");
	protected ContainerSimpleMaterialContainer inventory;

	/** 物品槽按钮计数 */
	protected List<SlotButton> slotButtonList = Lists.<SlotButton>newArrayList();
	protected ItemStack hoveredStack = ItemStack.EMPTY;

	public GuiSimpleMaterialContainer(EntityPlayer player) {
		super(new ContainerSimpleMaterialContainer(player), player.inventory);
		this.ySize = 195;

	}

	@Override
	public void initGui() {
		super.initGui();

		slotButtonList.clear();
		this.buttonList.clear();
		
		this.addSlot(80, 19);

		this.addSlot(80 - 18, 19 + 18);
		this.addSlot(80, 19 + 18);
		this.addSlot(80 + 18, 19 + 18);

		this.addSlot(80 - 18 * 2, 19 + 18 * 2);
		this.addSlot(80 - 18 * 1, 19 + 18 * 2);
		this.addSlot(80 + 18 * 1, 19 + 18 * 2);
		this.addSlot(80 + 18 * 2, 19 + 18 * 2);

		this.addSlot(80 - 18, 19 + 18 * 3);
		this.addSlot(80, 19 + 18 * 3);
		this.addSlot(80 + 18, 19 + 18 * 3);

		this.addSlot(80, 19 + 18 * 4);

		for (int i = 0; i < slotButtonList.size(); i++) {
			ItemStack stack = container.handler.getStackInSlot(i);
			slotButtonList.get(i).setItem(stack);
		}
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button instanceof SlotButton) {
			SlotButton slot = (SlotButton) button;

			if (isShiftKeyDown()) this.container.onClickBigSlotFast(slot.id, ItemStack.EMPTY);
			else this.container.onClickBigSlot(slot.id);

			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setByte("slot", (byte) slot.id);
			nbt.setBoolean("fast", isShiftKeyDown());
			this.container.sendToServer(nbt);
		}
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		if (this.container.isHanlderDirty) {
			this.container.isHanlderDirty = false;
			for (int i = 0; i < slotButtonList.size(); i++) {
				slotButtonList.get(i).setItem(this.container.handler.getStackInSlot(i));
			}
		}
	}

	public String getUnlocalizedTitle() {
		return "item.simpleMaterialContainer.name";
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F);

		this.mc.getTextureManager().bindTexture(TEXTURE);
		int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(offsetX, offsetY, 0, 0, this.xSize, this.ySize);

		hoveredStack = ItemStack.EMPTY;
		for (SlotButton slot : this.slotButtonList) {
			
			if (slot.id == this.container.handler.getAutoUseIndex()) {
				GlStateManager.color(1, 1, 1, 1);
				GlStateManager.disableLighting();
				this.mc.getTextureManager().bindTexture(TEXTURE);
				this.drawTexturedModalRect(slot.x - 1, slot.y - 1, 176, 0, 18, 18);
			}
			
			if (!slot.stack.isEmpty()) {
				if (slot.isMouseOver()) hoveredStack = slot.stack;
				drawItem(slot.stack, slot.x, slot.y);
			}
			
		}
	}

	@Override
	protected void renderHoveredToolTip(int x, int y) {
		if (this.mc.player.inventory.getItemStack().isEmpty() && !hoveredStack.isEmpty()) {
			super.renderToolTip(hoveredStack, x, y);
			return;
		}
		super.renderHoveredToolTip(x, y);
	}

	public int addSlot(int x, int y) {
		int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
		SlotButton button = new SlotButton(slotButtonList.size(), offsetX + x, offsetY + y, ItemStack.EMPTY);
		this.buttonList.add(button);
		this.slotButtonList.add(button);
		return this.slotButtonList.size() - 1;
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
					this.hovered = true;
					GlStateManager.disableLighting();
					GlStateManager.disableDepth();
					GlStateManager.colorMask(true, true, true, false);
					this.drawGradientRect(this.x, this.y, this.x + this.width, this.y + this.height, -2130706433,
							-2130706433);
					GlStateManager.colorMask(true, true, true, true);
				} else this.hovered = false;
			}
		}
	}

}

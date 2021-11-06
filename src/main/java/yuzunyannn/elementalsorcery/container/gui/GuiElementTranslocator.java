package yuzunyannn.elementalsorcery.container.gui;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.api.tile.IAltarWake;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.container.ContainerElementTranslocator;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;
import yuzunyannn.elementalsorcery.util.helper.ColorHelper;
import yuzunyannn.elementalsorcery.util.helper.NBTHelper;

@SideOnly(Side.CLIENT)
public class GuiElementTranslocator extends GuiNormal<ContainerElementTranslocator> {

	private static final ResourceLocation TEXTURE = new ResourceLocation(ElementalSorcery.MODID,
			"textures/gui/container/element_translocator.png");

	protected GuiTextField[] counts = new GuiTextField[2];
	protected int clickCD = 0;
	protected List<EffectRate> effects = new LinkedList<>();
	protected ElementStack estack = ElementStack.EMPTY;

	static public int[] lastRecordCounts = new int[] { 1, 1 };

	public GuiElementTranslocator(ContainerElementTranslocator inventorySlotsIn) {
		super(inventorySlotsIn, inventorySlotsIn.player.inventory);
		inventorySlotsIn.gui = this;
	}

	@Override
	public void initGui() {
		super.initGui();
		this.buttonList.add(new Button(0, guiLeft + 44 + 7, guiTop + 52, ""));
		this.buttonList.add(new Button(1, guiLeft + 97 + 7, guiTop + 52, ""));

		int offset = 3;

		counts[0] = new GuiTextFieldOnlyNumber(0, this.fontRenderer, guiLeft + 44 + offset, guiTop + 46 - 8, 32, 8);
		counts[1] = new GuiTextFieldOnlyNumber(1, this.fontRenderer, guiLeft + 97 + offset, guiTop + 46 - 8, 32, 8);
		for (GuiTextField tf : counts) {
			tf.setMaxStringLength(7);
			tf.setEnableBackgroundDrawing(false);
			tf.setTextColor(0xe1e1f0);
			tf.setText(String.valueOf(1));
		}

		try {
			for (int i = 0; i < lastRecordCounts.length; i++) {
				counts[i].setText(String.valueOf(lastRecordCounts[i]));
			}
		} catch (Exception e) {}

	}

	public void updateElement(int id, ElementStack estack) {
		if (id == IAltarWake.OBTAIN) {
			effects.add(new EffectRate(false, estack.getColor()));
		} else if (id == IAltarWake.SEND) {
			effects.add(new EffectRate(true, estack.getColor()));
		} else if (id == 0xf) {
			if (this.estack.getCount() < estack.getCount()) effects.add(new EffectRate(false, estack.getColor()));
			else if (this.estack.getCount() > estack.getCount()) effects.add(new EffectRate(true, estack.getColor()));
		}
		this.estack = estack;
		container.tileEntity.setElementStack(estack);
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		int id = button.id;
		if (clickCD > 0) return;
		ItemStackHandler items = container.getItemStackHandler();
		if (id == 0) {
			doTransfer(IAltarWake.OBTAIN, items.getStackInSlot(0));
		} else if (id == 1) {
			doTransfer(IAltarWake.SEND, items.getStackInSlot(1));
		}
	}

	protected void doTransfer(int id, ItemStack stack) {
		if (stack.isEmpty()) return;
		IElementInventory einv = ElementHelper.getElementInventory(stack);
		if (einv == null) return;
		clickCD = 5;

		int count = 1;
		if (id == IAltarWake.OBTAIN) {
			ElementStack estack = ElementStack.EMPTY;
			for (int i = 0; i < einv.getSlots(); i++) {
				estack = einv.getStackInSlot(i);
				if (!estack.isEmpty()) break;
			}
			if (estack.isEmpty()) return;
			count = getCountNumber(0);
			if (count <= 0) return;
		} else {
			if (container.tileEntity.isElementEmpty()) return;
			count = getCountNumber(1);
			if (count <= 0) return;
		}

		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setByte("id", (byte) id);
		NBTHelper.setIntegerForSend(nbt, "c", count);
		container.sendToServer(nbt);
	}

	public static final float EFFECT_RATE_DELTA = 0.05f;

	@Override
	public void updateScreen() {
		super.updateScreen();
		if (clickCD > 0) clickCD--;

		Iterator<EffectRate> iter = effects.iterator();
		while (iter.hasNext()) {
			EffectRate effect = iter.next();
			effect.rate = effect.rate + EFFECT_RATE_DELTA;
			if (effect.rate >= 1.4) iter.remove();
		}
	}

	@Override
	public String getUnlocalizedTitle() {
		return "tile.elementTranslocator.name";
	}

	@Override
	public int getTitleColor() {
		return 0x1a1a45;
	}

	protected int getCountNumber(int index) {
		if (index < 0 || index >= counts.length) return 0;
		try {
			return Integer.parseInt(counts[index].getText());
		} catch (Exception e) {
			counts[index].setText(String.valueOf(0));
			return 0;
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(TEXTURE);
		int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(offsetX, offsetY, 0, 0, this.xSize, this.ySize);
		for (GuiTextField tf : counts) tf.drawTextBox();

		if (!estack.isEmpty()) {
			estack.getElement().drawElemntIconInGUI(estack, offsetX + 80, offsetY + 39, mc);
		}

		this.mc.getTextureManager().bindTexture(TEXTURE);
		Iterator<EffectRate> iter = effects.iterator();
		while (iter.hasNext()) {
			EffectRate effect = iter.next();
			drawTransfer(effect.isTwo ? 53 : 0, effect.rate + EFFECT_RATE_DELTA * partialTicks, effect.color);
		}

	}

	public final Vec3d originColor = ColorHelper.color(0x8888b5);

	protected void drawTransfer(int xoff, float rate, Vec3d targetColor) {
		if (targetColor == null) targetColor = originColor;
		if (rate <= 0) return;

		int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
		float length = rate * 35;
		for (int i = Math.min((int) length, 35); i > 0; i--) {
			float n = Math.min(Math.abs(length - i) / 10, 1);
			if (n == 1) break;
			float r = MathHelper.sin(n * 3.1415926f);

			GlStateManager.color((float) originColor.x * (1 - r) + (float) targetColor.x * r,
					(float) originColor.y * (1 - r) + (float) targetColor.y * r,
					(float) originColor.z * (1 - r) + (float) targetColor.z * r, 1 - n);

			float x = 44 + i - 1 + xoff;
			float y = 46;
			float yoff = r * MathHelper.sin(i * 0.3f) * 3;

			this.drawTexturedModalRect(offsetX + x, offsetY + y - yoff, 2, 2, 1, 1);
			this.drawTexturedModalRect(offsetX + x, offsetY + y + yoff + 1, 2, 2, 1, 1);
		}
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		for (GuiTextField tf : counts) if (tf.textboxKeyTyped(typedChar, keyCode)) return;
		super.keyTyped(typedChar, keyCode);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		for (GuiTextField tf : counts) tf.mouseClicked(mouseX, mouseY, mouseButton);
	}

	static class GuiTextFieldOnlyNumber extends GuiTextField {

		public GuiTextFieldOnlyNumber(int componentId, FontRenderer fontrendererObj, int x, int y, int par5Width,
				int par6Height) {
			super(componentId, fontrendererObj, x, y, par5Width, par6Height);
		}

		@Override
		public void writeText(String textToWrite) {
			if (textToWrite.isEmpty()) super.writeText(textToWrite);
			else {
				try {
					super.writeText(String.valueOf(Integer.parseInt(textToWrite)));
				} catch (Exception e) {}
			}
		}

		@Override
		public void setResponderEntryValue(int idIn, String textIn) {
			super.setResponderEntryValue(idIn, textIn);
			updateRecord();
		}

		public void updateRecord() {
			try {
				lastRecordCounts[getId()] = Integer.parseInt(getText());
			} catch (Exception e) {}
		}
	}

	/** 转移器按钮 */
	class Button extends GuiButton {

		public Button(int buttonId, int x, int y, String text) {
			super(buttonId, x, y, 21, 9, text);
		}

		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
			if (!this.visible) return;
			GlStateManager.disableLighting();
			GlStateManager.color(1, 1, 1);
			mc.getTextureManager().bindTexture(TEXTURE);
			int x = mouseX - this.x, y = mouseY - this.y;
			this.drawTexturedModalRect(this.x, this.y, 176, 0, this.width, this.height);
			if (x >= 0 && y >= 0 && x < this.width && y < this.height)
				this.drawTexturedModalRect(this.x - 1, this.y - 1, 176, 9, this.width + 2, this.height + 2);
		}

	}

	protected static class EffectRate {
		public float rate;
		public boolean isTwo;
		public Vec3d color;

		public EffectRate(boolean isTwo, int color) {
			this.isTwo = isTwo;
			this.color = ColorHelper.color(color);
		}
	}

}

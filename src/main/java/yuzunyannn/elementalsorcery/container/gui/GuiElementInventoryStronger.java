package yuzunyannn.elementalsorcery.container.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.element.Element;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.tile.IAltarWake;
import yuzunyannn.elementalsorcery.api.util.client.RenderFriend;
import yuzunyannn.elementalsorcery.container.ContainerElementInventoryStronger;
import yuzunyannn.elementalsorcery.container.gui.GuiElementTranslocator.GuiTextFieldOnlyNumber;
import yuzunyannn.elementalsorcery.event.EventClient;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.gui.GUIEffectBatch;
import yuzunyannn.elementalsorcery.render.effect.gui.GUIEffectBatchList;
import yuzunyannn.elementalsorcery.util.element.ElementInventoryStronger;
import yuzunyannn.elementalsorcery.util.helper.Color;
import yuzunyannn.elementalsorcery.util.render.Shaders;

@SideOnly(Side.CLIENT)
public class GuiElementInventoryStronger extends GuiContainer {

	public static final ResourceLocation TEXTURE = new ResourceLocation(ESAPI.MODID,
			"textures/gui/element_inventory_stronger.png");
	protected final ContainerElementInventoryStronger container;

	public float centerRatio = 0;
	public float prevCenterRatio = 0;
	public float capacityRatio = 0;
	public float prevCapacityRatio = 0;

	public float updateRatio = 0;
	public float prevUpdateRatio = 0;

	public int eInvIndex;

	public final Color defaultColor = new Color(0x2bb9b0);

	protected GuiTextField limitUpper, limitLower;

	protected List<GuiTextField> fields = new ArrayList<>();

	public GuiElementInventoryStronger(ContainerElementInventoryStronger inventorySlotsIn) {
		super(inventorySlotsIn);
		this.container = inventorySlotsIn;
		this.xSize = this.ySize = 160;
		inventorySlotsIn.guiObj = this;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {

	}

	@Override
	public void initGui() {
		super.initGui();
		limitUpper = new GuiTextFieldOnlyNumber(0, this.fontRenderer, guiLeft + 84, guiTop + 108, 28, 8);
		limitUpper.setMaxStringLength(6);
		limitUpper.setEnableBackgroundDrawing(false);
		limitUpper.setTextColor(defaultColor.toInt());

		limitLower = new GuiTextFieldOnlyNumber(1, this.fontRenderer, guiLeft + 53, guiTop + 108, 28, 8);
		limitLower.setMaxStringLength(6);
		limitLower.setEnableBackgroundDrawing(false);
		limitLower.setTextColor(defaultColor.toInt());

		fields.add(limitUpper);
		fields.add(limitLower);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (keyCode == 28) {
			for (GuiTextField field : fields) {
				if (field.isFocused()) {
					field.setFocused(false);
					return;
				}
			}
		}
		for (GuiTextField field : fields) {
			if (field.textboxKeyTyped(typedChar, keyCode)) return;
		}
		super.keyTyped(typedChar, keyCode);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		for (GuiTextField field : fields) field.mouseClicked(mouseX, mouseY, mouseButton);
		if (mouseX >= guiLeft + 70 && mouseX <= guiLeft + 89 && mouseY >= guiTop + 146 && mouseY <= guiTop + 154) {
			mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
			trySendData();
		}
	}

	public ElementStack currStack = ElementStack.EMPTY;
	public int maxCount = 0;

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		Color color = defaultColor;
		partialTicks = mc.getRenderPartialTicks();
		if (!currStack.isEmpty()) color = new Color(currStack.getColor());
		int offsetX = guiLeft, offsetY = guiTop;

		GlStateManager.disableTexture2D();
		GlStateManager.color(color.r, color.g, color.b, 0.2f);
		this.drawTexturedModalRect(offsetX + 1, offsetY + 1, 0, 0, this.xSize - 2, this.ySize - 2);
		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.enableTexture2D();

		this.mc.getTextureManager().bindTexture(TEXTURE);
		Shaders.RGBColorMapping.bind();
		Shaders.RGBColorMapping.setUniform("color", color);
		this.drawTexturedModalRect(offsetX, offsetY, 0, 0, this.xSize, this.ySize);

		float pratio = RenderFriend.getPartialTicks(capacityRatio, prevCapacityRatio, partialTicks);
		RenderFriend.drawTextureModalRect(guiLeft + 24, guiTop + 86, 0, 160, 112 * pratio, 2, 256, 256);

		if (mouseX >= guiLeft + 70 && mouseX <= guiLeft + 89 && mouseY >= guiTop + 146 && mouseY <= guiTop + 154)
			this.drawTexturedModalRect(guiLeft + 71, guiTop + 147, 0, 162, 18, 6);

		{
			float cr = RenderFriend.getPartialTicks(centerRatio, prevCenterRatio, partialTicks);
			float x = offsetX + 46 + 33, y = offsetY + 12 + 33;
			GlStateManager.translate(x, y, 0);
			float rotation = MathHelper.sin(cr / 20) * 180;
			GlStateManager.rotate(rotation, 0, 0, 1);
			RenderFriend.drawTextureRectInCenter(0, 0, 66, 66, 160, 0, 66, 66, 256, 256);
			GlStateManager.rotate(-rotation, 0, 0, 1);
			GlStateManager.translate(-x, -y, 0);
		}

		Shaders.RGBColorMapping.unbind();
		int colorInt = color.toInt();

		if (!currStack.isEmpty()) {
			color = new Color(currStack.getColor());
			int x = offsetX + 79, y = offsetY + 45;
			currStack.getElement().drawElemntIconInGUI(currStack, x, y,
					Element.DRAW_GUI_FLAG_CENTER | Element.DRAW_GUI_FLAG_NO_INFO);

			String str;
			if (maxCount > 0) str = String.format("%s/%s", currStack.getCount(), maxCount);
			else str = String.format("%s/∞", currStack.getCount());

			int xoff = guiLeft + 24 + (int) (112 * pratio / 2);
			this.fontRenderer.drawString(str, xoff - this.fontRenderer.getStringWidth(str) / 2, guiTop + 87, colorInt);

			str = String.format("%s", currStack.getPower());
			this.fontRenderer.drawString(str, xoff - this.fontRenderer.getStringWidth(str) / 2, guiTop + 77, colorInt);

			String name = currStack.getDisplayName();
			this.fontRenderer.drawString(name, guiLeft + 80 - this.fontRenderer.getStringWidth(name) / 2, guiTop + 2,
					colorInt);
		}
		GlStateManager.translate(0, 0, 1);
		guiEffectList.render(partialTicks);

		for (GuiTextField field : fields) field.drawTextBox();

		float fR = RenderFriend.getPartialTicks(updateRatio, prevUpdateRatio, partialTicks);
		if (fR > 0.0001f) {
			fR = 1 - fR;
			fR = fR * fR * fR;
			float tx = offsetX + 46 + 33, ty = offsetY + 12 + 33;
			for (GuiTextField field : fields) {
				String str = field.getText();
				if (str == null || str.isEmpty()) continue;
				int w = this.fontRenderer.getStringWidth(str);
				float x = field.x + w / 2, y = field.y + 4;
				float ration = fR * 90;
				float scale = 1 - fR;
				x = x * (1 - fR) + tx * fR;
				y = y * (1 - fR) + ty * fR;
				GlStateManager.translate(x, y, 0);
				GlStateManager.rotate(ration, 0, 0, 1);
				GlStateManager.scale(scale, scale, 1);
				this.fontRenderer.drawString(str, -w / 2, -4, color.toInt());
				GlStateManager.scale(1 / scale, 1 / scale, 1);
				GlStateManager.rotate(-ration, 0, 0, 1);
				GlStateManager.translate(-x, -y, 0);
			}
		}

	}

	public static int parseInt(String str) {
		try {
			return Integer.parseInt(str);
		} catch (Exception e) {
			return -1;
		}
	}

	public NBTTagCompound lastSendData;

	protected boolean trySendData() {
		NBTTagCompound dat = new NBTTagCompound();
		int lLower = parseInt(limitLower.getText());
		int lUpper = parseInt(limitUpper.getText());
		if (lUpper > 0 && lLower > 0) if (lUpper < lLower) return false;
		if (lLower > 0) dat.setInteger("ll", lLower);
		if (lUpper > 0) dat.setInteger("lu", lUpper);
		if (dat.equals(lastSendData)) {
			updateRatio = 1;
			return false;
		}
		lastSendData = dat.copy();
		container.updateInventoryToServer(dat);
		updateRatio = 1;
		return true;
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		prevCenterRatio = centerRatio;
		prevCapacityRatio = capacityRatio;
		prevUpdateRatio = updateRatio;

		centerRatio += 0.1f;

		if (updateRatio > 0) updateRatio = Math.max(0, updateRatio - 0.1f);

		refreshData();
		if (currStack.isEmpty()) return;

		int targetCount;
		if (maxCount > 0) targetCount = maxCount;
		else targetCount = currStack.getCount();

		float targetRatio = Math.min(currStack.getCount(), targetCount) / (float) targetCount;
		capacityRatio = capacityRatio + (targetRatio - capacityRatio) * 0.25f;

		guiEffectList.update();

		float t = 0.2f + 20f / Math.max(1, MathHelper.sqrt(currStack.getPower())) + 2f - capacityRatio * 2f;
		if (t > 1) {
			if (EventClient.tick % (int) t == 0) {
				Part p = new Part();
				p.color.setColor(currStack.getColor()).add(0.25f);
				p.setPosition(guiLeft + 24 + (float) Math.random() * capacityRatio * 112, guiTop + 86);
				guiEffectList.add(p);
			}
		} else {
			int times = (int) (1 / t);
			for (int i = 0; i < times; i++) {
				Part p = new Part();
				p.color.setColor(currStack.getColor()).add(0.25f);
				p.setPosition(guiLeft + 24 + (float) Math.random() * capacityRatio * 112, guiTop + 86);
				guiEffectList.add(p);
			}
		}

		if (container.tileEntity instanceof IAltarWake)
			((IAltarWake) container.tileEntity).wake(IAltarWake.OBTAIN, null);
	}

	public void refreshData() {
		ElementInventoryStronger stronger = container.stronger;
		if (stronger == null) return;
		ElementStack eStack = ElementStack.EMPTY;
		if (stronger.getSlots() > 0) {
			int index = eInvIndex % stronger.getSlots();
			eStack = stronger.getStackInSlot(index);
			maxCount = stronger.getMaxSizeInSlot(index);
		}
		if (currStack == eStack) return;
		if (currStack.getElement() != eStack.getElement()) {
			for (GuiTextField field : fields)
				field.setTextColor(eStack.isEmpty() ? defaultColor.toInt() : eStack.getColor());
		}
		currStack = eStack;
		if (stronger.getLowerLimit() > 0 && !limitLower.isFocused())
			limitLower.setText(String.valueOf(stronger.getLowerLimit()));
		if (stronger.getUpperLimit() > 0 && !limitUpper.isFocused())
			limitUpper.setText(String.valueOf(stronger.getUpperLimit()));
	}

	protected GUIEffectBatchList<Part> guiEffectList = new GUIEffectBatchList<>();

	public static class Part extends GUIEffectBatch {

		public float vx, vy;
		public int startLifeTime = 40;

		public Part() {
			drawSize = 2;
			prevScale = scale = Effect.rand.nextFloat() * 0.75f + 1.25f;
			lifeTime = startLifeTime;
			vy = -(Effect.rand.nextFloat() * 0.5f + 0.25f);
		}

		public void update() {
			super.update();
			this.lifeTime--;
			x += vx;
			y += vy;
			alpha = scale = this.lifeTime / (float) this.startLifeTime;
		}

	}

}

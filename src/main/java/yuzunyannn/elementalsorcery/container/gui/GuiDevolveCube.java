package yuzunyannn.elementalsorcery.container.gui;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.api.util.client.RenderFriend;
import yuzunyannn.elementalsorcery.container.ContainerDevolveCube;
import yuzunyannn.elementalsorcery.container.gui.GuiElementTranslocator.GuiTextFieldOnlyNumber;
import yuzunyannn.elementalsorcery.logics.EventClient;
import yuzunyannn.elementalsorcery.tile.altar.TileDevolveCube;
import yuzunyannn.elementalsorcery.tile.altar.TileDevolveCube.DevolveData;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;
import yuzunyannn.elementalsorcery.util.element.ElementStackDoubleExchanger;
import yuzunyannn.elementalsorcery.util.helper.NBTHelper;

public class GuiDevolveCube extends GuiContainer {

	public static final ResourceLocation TEXTURE = new ResourceLocation(ESAPI.MODID,
			"textures/gui/devolve_cube.png");

	// 动态材质，全局就一个
	private static DynamicTexture mapTexture;

	public static DynamicTexture getDynamicTexture() {
		if (mapTexture == null)
			mapTexture = new DynamicTexture(TileDevolveCube.DETECTION_RANGE * 2, TileDevolveCube.DETECTION_RANGE * 2);
		return mapTexture;
	}

	public final ContainerDevolveCube container;
	public Map<BlockPos, List<Entry<BlockPos, DevolveData>>> classifyDevolveMap = new HashMap<>();
	public BlockPos selected;
	public int listIndex = 0;
	protected GuiTextField textField;

	public GuiDevolveCube(ContainerDevolveCube container) {
		super(container);
		this.container = container;
		this.xSize = 198;
		this.ySize = 198;
	}

	@Override
	public void initGui() {
		super.initGui();
		textField = new GuiTextFieldOnlyNumber(0, this.fontRenderer, guiLeft + 120, guiTop + 202, 32, 8) {
			@Override
			public void setFocused(boolean isFocusedIn) {
				super.setFocused(isFocusedIn);
				if (isFocusedIn) textField.setTextColor(0x8e2eff);
				else textField.setTextColor(0xcfaef6);
			}
		};
		textField.setMaxStringLength(5);
		textField.setEnableBackgroundDrawing(false);
		textField.setTextColor(0xcfaef6);
		textField.setText(String.valueOf(1));
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	public static boolean isInRect(int mouseX, int mouseY, float x, float y, float width, float height) {
		return mouseX >= x && mouseY > y && mouseX <= x + width && mouseY <= y + height;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1f, 1f, 1f, 1f);
		this.mc.getTextureManager().bindTexture(TEXTURE);
		int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(offsetX, offsetY, 0, 0, this.xSize, this.ySize);

		int centerX = offsetX + this.xSize / 2, centerY = offsetY + this.ySize / 2;

		if (container.lastColorVersion == 0) return;

		DynamicTexture texture = getDynamicTexture();
		GlStateManager.bindTexture(texture.getGlTextureId());
		RenderFriend.drawTextureRectInCenter(centerX, centerY, this.xSize - 6, this.ySize - 6, 0, 0, 1, 1, 1, -1);

		this.mc.getTextureManager().bindTexture(TEXTURE);
		final int onePixel = 6;
		final float hPixel = onePixel / 2;
		BlockPos tilePos = container.tile.getPos();
		float roate = -EventClient.getGlobalRotateInRender(partialTicks);
		// 元素容器
		for (Entry<BlockPos, List<Entry<BlockPos, DevolveData>>> entry : classifyDevolveMap.entrySet()) {

			BlockPos pos = entry.getKey();
			int x = pos.getX() - tilePos.getX();
			int z = pos.getZ() - tilePos.getZ();
			float xoff = centerX + x * onePixel + hPixel;
			float yoff = centerY - (z + 1) * onePixel + hPixel;
			boolean isSelect = isInRect(mouseX, mouseY, xoff - hPixel, yoff - hPixel, hPixel * 2, hPixel * 2);

			List<Entry<BlockPos, DevolveData>> cList = entry.getValue();

			GlStateManager.translate(xoff, yoff, 0);
			if (cList.size() > 1) {
				float scale = Math.max(1f / cList.size() * 1.2f, 0.25f);
				float mx = 1 / scale, dx = mx / cList.size() * 2;
				float my = 1 / scale, dy = my / cList.size() * 2;
				for (int i = 0; i < cList.size(); i++) {
					DevolveData dat = cList.get(i).getValue();
					if (dat.inEnable) GlStateManager.color(255 / 255f, 162 / 255f, 171 / 255f);
					else if (dat.outEnable) GlStateManager.color(168 / 255f, 178 / 255f, 255 / 255f);
					else GlStateManager.color(1f, 1f, 1f);
					float xf = -mx + dx * i + dx / 2, yf = -my + dy * i + dy / 2;
					GlStateManager.translate(xf, yf, 0);
					GlStateManager.rotate(roate, 0, 0, 1);
					GlStateManager.scale(scale, scale, 1);
					RenderFriend.drawTextureRectInCenter(0, 0, 4, 4, 198, 0, 4, 4, 256, 256);
					GlStateManager.scale(1 / scale, 1 / scale, 1);
					GlStateManager.rotate(-roate, 0, 0, 1);
					GlStateManager.translate(-xf, -yf, 0);
				}
			} else {
				DevolveData dat = cList.get(0).getValue();
				GlStateManager.rotate(roate, 0, 0, 1);
				if (dat.inEnable) GlStateManager.color(255 / 255f, 162 / 255f, 171 / 255f);
				else if (dat.outEnable) GlStateManager.color(168 / 255f, 178 / 255f, 255 / 255f);
				else GlStateManager.color(1f, 1f, 1f);
				RenderFriend.drawTextureRectInCenter(0, 0, 4, 4, 198, 0, 4, 4, 256, 256);
				GlStateManager.rotate(-roate, 0, 0, 1);
			}

			GlStateManager.color(1, 1f, 1f, 1f);

			if (isSelect && !pos.equals(selected)) {
				GlStateManager.color(0.5f, 1f, 1f, 0.8f);
				RenderFriend.drawTextureRectInCenter(0, 0, 8, 8, 198, 4, 8, 8, 256, 256);
				GlStateManager.color(1f, 1f, 1f, 1f);
			}

			GlStateManager.translate(-xoff, -yoff, 0);
		}

		// 上方操作栏
		{
			float y = offsetY - 19;
			float x = offsetX + 6;
			this.drawTexturedModalRect(x, y, 6, 237, 186, 19);

			ElementStackDoubleExchanger exchanger = container.tile.getExchanger();
			for (int i = 0; i < exchanger.getSlots(); i++) {
				ElementStack estack = exchanger.getStackInSlot(i);
				if (estack.isEmpty()) continue;
				estack.getElement().drawElemntIconInGUI(estack, (int) x + 14 + i * 17, (int) y + 2, 0);
			}

			this.mc.getTextureManager().bindTexture(TEXTURE);
			GlStateManager.color(1f, 1f, 1f);
		}

		// 选择内容，及选择操作栏
		if (selected != null) ctrl: {
			float x = selected.getX() - tilePos.getX();
			float y = selected.getZ() - tilePos.getZ();
			float xoff = centerX + x * onePixel + hPixel;
			float yoff = centerY - (y + 1) * onePixel + hPixel;
			RenderFriend.drawTextureRectInCenter(xoff, yoff, 8, 8, 198, 4, 8, 8, 256, 256);

			List<Entry<BlockPos, DevolveData>> cList = classifyDevolveMap.get(selected);
			if (cList == null || cList.isEmpty()) {
				selected = null;
				break ctrl;
			}
			
			y = offsetY + this.ySize;
			x = offsetX + 6;
			this.drawTexturedModalRect(x, y, 6, 198, 186, 19);

			// 切页按钮
			if (cList.size() > 1) {
				if (listIndex > 0) {
					float bx = x + 2, by = y + 5;
					if (isInRect(mouseX, mouseY, bx, by, 2, 8)) this.drawTexturedModalRect(bx, by, 0, 198 + 8, 3, 8);
					else this.drawTexturedModalRect(bx, by, 0, 198, 3, 8);
				}
				if (listIndex < cList.size() - 1) {
					float bx = x + 181, by = y + 5;
					if (isInRect(mouseX, mouseY, bx, by, 2, 8)) this.drawTexturedModalRect(bx, by, 3, 198 + 8, 3, 8);
					else this.drawTexturedModalRect(bx, by, 3, 198, 3, 8);
				}
			}

			Entry<BlockPos, DevolveData> entry = cList.get(listIndex % cList.size());
			DevolveData dat = entry.getValue();
			// 转移按钮
			{
				float bx = x + 82, by = y + 4;
				boolean isInEnable = dat.inEnable;
				boolean isOutEnable = dat.outEnable;

				if (isOutEnable) this.drawTexturedModalRect(bx, by, 0, 217, 9, 9);
				else if (isInRect(mouseX, mouseY, bx, by, 9, 9)) this.drawTexturedModalRect(bx, by, 0, 226, 9, 9);

				bx = bx + 13;
				if (isInEnable) this.drawTexturedModalRect(bx, by, 9, 217, 9, 9);
				else if (isInRect(mouseX, mouseY, bx, by, 9, 9)) this.drawTexturedModalRect(bx, by, 9, 226, 9, 9);
			}

			// 坐标
			BlockPos pos = entry.getKey();
			String locate = String.format("%d,%d,%d", pos.getX(), pos.getY(), pos.getZ());
			int width = this.fontRenderer.getStringWidth(locate);
			float scale = 40f / width;
			xoff = x + 28;
			yoff = y + 0.5f + 3 / scale;
			GlStateManager.translate(xoff, yoff, 0);
			GlStateManager.scale(scale, scale, 1);
			this.fontRenderer.drawString(locate, 0, 0, 0xcfaef6);
			GlStateManager.scale(1 / scale, 1 / scale, 1);
			GlStateManager.translate(-xoff, -yoff, 0);

			// 数量
			if (!textField.isFocused()) textField.setText(String.valueOf(dat.count));

			// 元素
			IElementInventory eInv = ElementHelper.getElementInventory(container.tile.getWorld().getTileEntity(pos));
			if (eInv != null) {
				int i = EventClient.tick / 20;
				ElementStack estack = eInv.getStackInSlot(i % eInv.getSlots());
				if (!estack.isEmpty()) {
					estack.getElement().drawElemntIconInGUI(estack, (int) x + 145, (int) y + 1, 0);
				}
			}

			textField.drawTextBox();
		}
	}

	public Entry<BlockPos, DevolveData> getCurrSelectedData() {
		if (selected == null) return null;
		List<Entry<BlockPos, DevolveData>> cList = classifyDevolveMap.get(selected);
		return cList.get(listIndex % cList.size());
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (keyCode == 28) {
			if (textField.isFocused()) {
				textField.setFocused(false);
				return;
			}
		} else if (textField.textboxKeyTyped(typedChar, keyCode)) return;
		super.keyTyped(typedChar, keyCode);
	}

	public void playPressSound() {
		mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
	}

	public void onClickCheck() {
		textField.setFocused(false);
		updateCheckTextField();
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);

		int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
		// 大地图
		if (isInRect(mouseX, mouseY, offsetX, offsetY, this.xSize, this.ySize)) {
			if (mouseButton != 0) return;
			onClickCheck();
			selected = null;
			int centerX = offsetX + this.xSize / 2, centerY = offsetY + this.ySize / 2;
			BlockPos tilePos = container.tile.getPos();
			final int onePixel = 6;
			final float hPixel = onePixel / 2;

			for (Entry<BlockPos, ?> entry : classifyDevolveMap.entrySet()) {
				BlockPos pos = entry.getKey();
				int x = pos.getX() - tilePos.getX();
				int z = pos.getZ() - tilePos.getZ();
				float xoff = centerX + x * onePixel + hPixel;
				float yoff = centerY - (z + 1) * onePixel + hPixel;
				if (isInRect(mouseX, mouseY, xoff - hPixel, yoff - hPixel, hPixel * 2, hPixel * 2)) selected = pos;
			}
			if (selected != null) playPressSound();
			listIndex = 0;
			return;
		}
		// 操作栏
		if (selected == null) return;
		textField.mouseClicked(mouseX, mouseY, mouseButton);
		float y = offsetY + this.ySize;
		float x = offsetX + 6;
		// 切换统一高的按钮
		{
			float bx = x + 2, by = y + 5;
			if (isInRect(mouseX, mouseY, bx, by, 2, 8)) {
				if (listIndex > 0) {
					onClickCheck();
					listIndex--;
				}
				playPressSound();
				return;
			}
		}
		{
			float bx = x + 181, by = y + 5;
			if (isInRect(mouseX, mouseY, bx, by, 2, 8)) {
				List<Entry<BlockPos, DevolveData>> cList = classifyDevolveMap.get(selected);
				if (listIndex < cList.size() - 1) {
					onClickCheck();
					listIndex++;
				}
				playPressSound();
				return;
			}
		}
		// 转移按钮
		Entry<BlockPos, DevolveData> entry = getCurrSelectedData();
		if (entry == null) return;
		DevolveData dat = entry.getValue();
		BlockPos pos = entry.getKey();
		{
			float bx = x + 82, by = y + 4;

			if (isInRect(mouseX, mouseY, bx, by, 9, 9)) {
				onClickCheck();
				if (mouseButton == 0) {
					if (!dat.outEnable) {
						if (sendTranferToUpdate(pos, false)) playPressSound();
					}
				} else if (mouseButton == 1) {
					DevolveData nDat = dat.copy();
					nDat.outEnable = !nDat.outEnable;
					if (nDat.outEnable) nDat.inEnable = false;
					if (sendDevolveToUpdate(pos, nDat)) playPressSound();
				}
			}

			bx = bx + 13;
			if (isInRect(mouseX, mouseY, bx, by, 9, 9)) {
				onClickCheck();
				if (mouseButton == 0) {
					if (!dat.inEnable) {
						if (sendTranferToUpdate(pos, true)) playPressSound();
					}
				} else if (mouseButton == 1) {
					DevolveData nDat = dat.copy();
					nDat.inEnable = !nDat.inEnable;
					if (nDat.inEnable) nDat.outEnable = false;
					if (sendDevolveToUpdate(pos, nDat)) playPressSound();
				}
			}
		}
	}

	int updateCD = 0;

	public boolean sendTranferToUpdate(BlockPos blockPos, boolean isIn) {
		if (updateCD > 0) return false;
		NBTTagCompound nbt = new NBTTagCompound();
		NBTHelper.setBlockPos(nbt, "P", blockPos);
		nbt.setBoolean("RT", isIn);
		container.sendToServer(nbt);
		updateCD = 5;
		return true;
	}

	public boolean sendDevolveToUpdate(BlockPos blockPos, DevolveData data) {
		if (updateCD > 0) return false;
		NBTTagCompound nbt = new NBTTagCompound();
		NBTHelper.setBlockPos(nbt, "P", blockPos);
		nbt.setInteger("F", data.toFlag());
		container.sendToServer(nbt);
		updateCD = 5;
		return true;
	}

	String lastFeildValue;

	protected void updateCheckTextField() {
		if (textField.isFocused()) {
			if (lastFeildValue == null) lastFeildValue = textField.getText();
		} else {
			if (lastFeildValue == null) return;
			String str = textField.getText();
			if (!lastFeildValue.equals(str)) {
				Entry<BlockPos, DevolveData> entry = getCurrSelectedData();
				if (entry != null) {
					try {
						BlockPos ps = entry.getKey();
						DevolveData data = entry.getValue().copy();
						int n = Integer.parseInt(str);
						if (n > Short.MAX_VALUE) n = Short.MAX_VALUE;
						data.count = (short) n;
						sendDevolveToUpdate(ps, data);
					} catch (NumberFormatException e) {}
				}
			}
			lastFeildValue = null;
		}
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		if (container.lastColorVersion != container.tile.colorVersion) {
			container.lastColorVersion = container.tile.colorVersion;
			DynamicTexture texture = getDynamicTexture();
			int[] colors = texture.getTextureData();
			byte[] cbs = container.tile.getColors();
			for (int i = 0; i < cbs.length; i++) colors[i] = container.tile.getColorByColorByteValue(cbs[i]);
			texture.updateDynamicTexture();
		}
		if (container.lastSelectedVersion != container.tile.selectedVersion) {
			container.lastSelectedVersion = container.tile.selectedVersion;
			classifyDevolveMap.clear();
			Map<BlockPos, DevolveData> map = container.tile.getElementContainers();
			for (Entry<BlockPos, DevolveData> entry : map.entrySet()) {
				BlockPos pos = entry.getKey();
				BlockPos at = new BlockPos(pos.getX(), 0, pos.getZ());
				List<Entry<BlockPos, DevolveData>> cList = classifyDevolveMap.get(at);
				if (cList == null) cList = new ArrayList();
				classifyDevolveMap.put(at, cList);
				cList.add(new AbstractMap.SimpleEntry<>(pos, entry.getValue()));
			}
		}

		if (updateCD > 0) updateCD--;
		updateCheckTextField();
	}

}

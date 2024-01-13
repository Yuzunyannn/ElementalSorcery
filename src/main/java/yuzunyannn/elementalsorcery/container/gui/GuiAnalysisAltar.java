package yuzunyannn.elementalsorcery.container.gui;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.container.ContainerAnalysisAltar;
import yuzunyannn.elementalsorcery.logics.EventClient;

@SideOnly(Side.CLIENT)
public class GuiAnalysisAltar extends GuiNormal<ContainerAnalysisAltar> {

	private static final ResourceLocation TEXTURE = new ResourceLocation(ESAPI.MODID,
			"textures/gui/container/analysis_altar.png");

	public GuiAnalysisAltar(ContainerAnalysisAltar inventorySlotsIn) {
		super(inventorySlotsIn, inventorySlotsIn.player.inventory);
	}

	@Override
	public String getUnlocalizedTitle() {
		return "tile.analysisAltar.name";
	}

	@Override
	public int getTitleColor() {
		return 0x1a1a45;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		if (!container.tileEntity.isOk()) return;
		ItemStack stack = container.tileEntity.getDAStack();
		if (stack.isEmpty()) {
			this.drawStringWithWidth(I18n.format("info.analysisAltar.none"), 52, 26, 60);
			return;
		}
		ElementStack[] estacks = container.tileEntity.getDAEstacks();
		if (estacks == null) {
			String str = I18n.format("info.analysisAltar.itema");
			this.fontRenderer.drawString(str, 52, 26, 4210752);
			this.drawStringWithWidth(stack.getDisplayName(), 52, 35, 60);
			return;
		}
		String str = I18n.format("info.analysisAltar.item");
		this.fontRenderer.drawString(str, 52, 26, 4210752);
		this.drawStringWithWidth(stack.getDisplayName(), 52, 35, 60);
		if (estacks.length == 0) {
			this.drawStringWithWidth(TextFormatting.RED + I18n.format("info.analysisAltar.itemfail"), 52, 44, 60);
			return;
		}
		int complex = container.tileEntity.getDAComplex();
		this.drawStringWithWidth(I18n.format("info.analysisAltar.complex", complex), 52, 44, 60);
	}

	private void drawStringWithWidth(String str, int x, int y, int width) {
		int w = this.fontRenderer.getStringWidth(str);
		float scale = (width > w) ? 1.0f : (width / (float) w);
		GlStateManager.scale(scale, scale, 1.0f);
		this.fontRenderer.drawString(str, (int) (x / scale), (int) (y / scale), 4210752);
		GlStateManager.scale(1.0f / scale, 1.0f / scale, 1.0f);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(TEXTURE);
		int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(offsetX, offsetY, 0, 0, this.xSize, this.ySize);
		// 画记录进度
		float powerTime = container.tileEntity.getPowerTime();
		float totalPowerTime = container.tileEntity.getTotalPowerTime();
		int texWidth = 1 + (int) Math.ceil(22.0 * powerTime / totalPowerTime);
		this.drawTexturedModalRect(offsetX + 113, offsetY + 43, 176, 0, texWidth, 10);

		ItemStack stack = container.tileEntity.getDAStack();
		if (stack.isEmpty()) return;
		RenderHelper.enableGUIStandardItemLighting();
		this.drawRoateItem(stack, offsetX + 24, offsetY + 47, partialTicks);
		// 开始画元素图标
		ElementStack[] estacks = container.tileEntity.getDAEstacks();
		if (estacks == null || estacks.length == 0) return;
		RenderHelper.disableStandardItemLighting();
		if (estacks.length <= 3) {
			for (int i = 0; i < estacks.length; i++) {
				ElementStack estack = estacks[i];
				estack.getElement().drawElemntIconInGUI(estack, offsetX + 56 + i * 17, offsetY + 58, 0);
			}
		} else {
			for (int i = 0; i < 3; i++) {
				ElementStack estack = estacks[i + at];
				estack.getElement().drawElemntIconInGUI(estack, offsetX + 56 + i * 17, offsetY + 58, 0);
			}
		}
		this.mc.getTextureManager().bindTexture(TEXTURE);
		GlStateManager.color(1.0f, 1.0f, 1.0f);
	}

	private void drawRoateItem(ItemStack stack, int x, int y, float partialTicks) {
		this.itemRender.zLevel += 50.0F;
		GlStateManager.pushMatrix();
		this.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		this.mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
		IBakedModel bakedmodel = this.itemRender.getItemModelWithOverrides(stack, null, this.mc.player);
		GlStateManager.enableRescaleNormal();
		GlStateManager.enableAlpha();
		GlStateManager.alphaFunc(516, 0.1F);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.translate(x, y, 100.0F + this.zLevel);
		GlStateManager.translate(8.0F, 8.0F, 0.0F);
		GlStateManager.scale(1.0F, -1.0F, 1.0F);
		GlStateManager.scale(32.0F, 32.0F, 32.0F);
		if (bakedmodel.isGui3d()) GlStateManager.enableLighting();
		else GlStateManager.disableLighting();
		GlStateManager.disableCull();
		bakedmodel = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(bakedmodel,
				ItemCameraTransforms.TransformType.GROUND, false);
		GlStateManager.rotate(30, 1, 0, 0);
		GlStateManager.rotate(EventClient.getGlobalRotateInRender(partialTicks), 0, 1, 0);
		this.itemRender.renderItem(stack, bakedmodel);
		GlStateManager.enableCull();
		GlStateManager.disableAlpha();
		GlStateManager.disableRescaleNormal();
		GlStateManager.disableLighting();
		GlStateManager.popMatrix();
		this.itemRender.zLevel -= 50.0F;
	}

	int at = 0;
	int max = 0;
	GuiButton preBut;
	GuiButton nextBut;

	@Override
	public void updateScreen() {
		super.updateScreen();
		ElementStack[] estacks = container.tileEntity.getDAEstacks();
		if (estacks == null || estacks.length <= 3) {
			preBut.visible = nextBut.visible = false;
			at = 0;
		} else {
			max = estacks.length - 3;
			if (at != max) nextBut.visible = true;
		}
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.id == 0) {
			if (at > 0) at--;
			if (at == 0) preBut.visible = false;
			if (at < max) nextBut.visible = true;
		} else {
			if (at < max) at++;
			if (at == max) nextBut.visible = false;
			if (at > 0) preBut.visible = true;
		}
	}

	@Override
	public void initGui() {
		super.initGui();
		int offsetX = (this.width - this.xSize) / 2, offsetY = (this.height - this.ySize) / 2;
		preBut = new GuiButton(0, offsetX + 52, offsetY + 63, 3, 6, null) {
			@Override
			public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
				if (this.visible) {
					int x = mouseX - this.x, y = mouseY - this.y;
					if (x >= 0 && y >= 0 && x < this.width && y < this.height) {
						this.drawTexturedModalRect(this.x, this.y, 176, 10, this.width, this.height);
					} else {
						this.drawTexturedModalRect(this.x, this.y, 176, 16, this.width, this.height);
					}
				}
			}
		};
		this.addButton(preBut);
		nextBut = new GuiButton(1, offsetX + 107, offsetY + 63, 3, 6, null) {
			@Override
			public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
				if (this.visible) {
					int x = mouseX - this.x, y = mouseY - this.y;
					if (x >= 0 && y >= 0 && x < this.width && y < this.height) {
						this.drawTexturedModalRect(this.x, this.y, 179, 10, this.width, this.height);
					} else {
						this.drawTexturedModalRect(this.x, this.y, 179, 16, this.width, this.height);
					}
				}
			}
		};
		this.addButton(nextBut);
		if (at == max) nextBut.visible = false;
		else nextBut.visible = true;
		if (at == 0) preBut.visible = false;
		else preBut.visible = true;
	}

}

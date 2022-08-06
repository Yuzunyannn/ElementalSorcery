package yuzunyannn.elementalsorcery.container.gui.elf;

import java.io.IOException;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.util.client.RenderFriend;
import yuzunyannn.elementalsorcery.elf.edifice.FloorInfo;
import yuzunyannn.elementalsorcery.event.KeyBoard;
import yuzunyannn.elementalsorcery.network.ESNetwork;
import yuzunyannn.elementalsorcery.network.MessageElfTreeElevator;
import yuzunyannn.elementalsorcery.tile.TileElfTreeCore;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;

@SideOnly(Side.CLIENT)
public class GuiElfTreeElevator extends GuiScreen {

	public static final ResourceLocation TEXTURE = new ResourceLocation(ESAPI.MODID,
			"textures/gui/elf/tree_elevator.png");

	final public TileElfTreeCore core;
	private int selected = -1;

	public GuiElfTreeElevator(EntityPlayer player, BlockPos corePos) {
		this.core = BlockHelper.getTileEntity(player.world, corePos, TileElfTreeCore.class);
	}

	/** 关闭gui同时将新数据发送给服务器 */
	public void close() {
		this.mc.player.closeScreen();
		if (selected != -1 && core != null)
			ESNetwork.instance.sendToServer(new MessageElfTreeElevator(core.getPos(), selected));
		selected = -1;
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	public void handleKeyboardInput() throws IOException {
		if (Keyboard.getEventKeyState() == false) {
			int key = Keyboard.getEventKey();
			if (key == KeyBoard.KEY_MANTRA_SHITF.getKeyCode() || key == Keyboard.KEY_ESCAPE) {
				this.close();
			}
		}
	}

	/** 直接处理鼠标消息 */
	@Override
	public void handleMouseInput() throws IOException {
		if (core == null) return;
		selected = -1;
		int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
		int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
		int cX = this.width / 2;
		int cY = this.height / 2;
		if (Math.abs(mouseX - cX) > 42) return;
		int floors = core.getFloorCount();
		int high = 0;
		for (int i = 0; i < floors; i++) {
			FloorInfo floor = core.getFloor(i);
			float ccigh = floor.getHigh() + 1;
			high += ccigh;
			float yoff = high / (float) core.getTreeHigh() * 200;
			ccigh = ccigh / (float) core.getTreeHigh() * 200;
			if (mouseY > cY + 109f - yoff && mouseY < cY + 109f - yoff + ccigh) {
				selected = i;
				break;
			}
		}
	}

	public float a = 0;
	public float prevA = a;

	@Override
	public void updateScreen() {
		if (this.core == null) this.close();
		prevA = a;
		a = Math.min(1, a + 0.25f);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		partialTicks = mc.getRenderPartialTicks();
		this.drawDefaultBackground();
		int cX = this.width / 2;
		int cY = this.height / 2;
		if (core == null) return;
		int floors = core.getFloorCount();
		int high = 0;
		GlStateManager.enableBlend();
		float a = RenderFriend.getPartialTicks(this.a, this.prevA, partialTicks);
		for (int i = 0; i < floors; i++) {
			mc.getTextureManager().bindTexture(TEXTURE);
			if (selected == i) GlStateManager.color(0.5f, 0.5f, 0.5f, a);
			else GlStateManager.color(1, 1, 1, a);
			FloorInfo floor = core.getFloor(i);
			float ccigh = floor.getHigh() + 1;
			high += ccigh;
			float yoff = high / (float) core.getTreeHigh() * 200;
			ccigh = ccigh / (float) core.getTreeHigh() * 200;
			yoff = cY + 109f - yoff + ccigh / 2;
			RenderFriend.drawTexturedRectInCenter(cX + 0.5f, yoff, 84, ccigh, 153, 25, 84, ccigh, 256, 256);
			fontRenderer.drawStringWithShadow(Integer.toString(i + 1), cX - 40, (int) (yoff - ccigh / 2) + 2, 0x844700);
		}
		GlStateManager.color(1, 1, 1, a);
		mc.getTextureManager().bindTexture(TEXTURE);
		RenderFriend.drawTexturedRectInCenter(cX, cY, 153, 232, 0, 0, 153, 232, 256, 256);
		GlStateManager.disableBlend();
	}

}

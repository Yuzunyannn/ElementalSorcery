package yuzunyannn.elementalsorcery.computer.render;

import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import yuzunyannn.elementalsorcery.api.computer.soft.ISoftGuiRuntime;
import yuzunyannn.elementalsorcery.computer.soft.AppTutorialGui;
import yuzunyannn.elementalsorcery.nodegui.GImage;
import yuzunyannn.elementalsorcery.nodegui.GItemStack;
import yuzunyannn.elementalsorcery.nodegui.GNode;

public class GItemFrame extends GImage {

	protected GItemStack gStack;

	protected static final GImage HIGHLIGHT = new GImage(SoftGuiCommon.TEXTURE_1, AppTutorialGui.FRAME_ITEM_HOVER);
	protected static final Vec3d MOUSE_FOLLOW_VEC = new Vec3d(0, 0, -99);

	static {
		HIGHLIGHT.setAnchor(0.5, 0.5, 0);
		HIGHLIGHT.setAlpha(0.5f);
	}

	protected boolean isHover = false;
	protected ISoftGuiRuntime runtime;

	public GItemFrame() {
		this(ItemStack.EMPTY);
	}

	public GItemFrame(ItemStack stack) {
		super(SoftGuiCommon.TEXTURE_1, AppTutorialGui.FRAME_ITEM);
		setAnchor(0.5, 0.5);

		gStack = new GItemStack(stack);
		gStack.setPositionZ(20);
		addChild(gStack);
	}

	public void setRuntime(ISoftGuiRuntime runtime) {
		this.runtime = runtime;
	}

	public void setItemStack(ItemStack itemStack) {
		gStack.setStack(itemStack);
	}

	public ItemStack getItemStack() {
		return gStack.getStack();
	}

	public void enableClick(Runnable run, GNode scissor) {
		setInteractor(new BtnBaseInteractor() {

			@Override
			public boolean testHit(GNode node, Vec3d worldPos) {
				if (scissor != null && !scissor.testHit(worldPos)) return false;
				return super.testHit(node, worldPos);
			}

			@Override
			public boolean blockMouseEvent(GNode node, Vec3d worldPos) {
				return scissor == null;
			}

			@Override
			public void onHoverChange(GNode node) {
				GItemFrame.this.isHover = this.isHover;
			}

			@Override
			public void onClick() {
				run.run();
			}
		});
	}

	@Override
	public void update() {
		super.update();
		if (this.isHover && this.runtime != null) {
			ItemStack stack = this.getItemStack();
			if (!stack.isEmpty()) this.runtime.setTooltip("item", MOUSE_FOLLOW_VEC, 0, stack);
		}
	}

	@Override
	public void draw(float partialTicks) {
		super.draw(partialTicks);
		if (isHover) {
			GlStateManager.translate(x, y, z + 40);
			HIGHLIGHT.draw(partialTicks);
			GlStateManager.translate(-x, -y, -z + 40);
		}
	}

}

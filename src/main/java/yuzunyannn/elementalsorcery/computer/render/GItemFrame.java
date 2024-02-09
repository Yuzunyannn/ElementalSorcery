package yuzunyannn.elementalsorcery.computer.render;

import java.util.function.Supplier;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import yuzunyannn.elementalsorcery.api.computer.soft.ISoftGuiRuntime;
import yuzunyannn.elementalsorcery.computer.soft.AppTutorialGui;
import yuzunyannn.elementalsorcery.nodegui.GImage;
import yuzunyannn.elementalsorcery.nodegui.GItemStack;
import yuzunyannn.elementalsorcery.nodegui.GNode;

public class GItemFrame extends GImage {

	protected static final GImage HIGHLIGHT = new GImage(SoftGuiCommon.TEXTURE_1, AppTutorialGui.FRAME_ITEM_HOVER);
	protected static final Vec3d MOUSE_FOLLOW_VEC = new Vec3d(0, 0, -99);

	static {
		HIGHLIGHT.setAnchor(0.5, 0.5, 0);
		HIGHLIGHT.setAlpha(0.5f);
	}

	protected GItemStack gStack;
	protected boolean showDisabled;
	protected boolean isHover;
	protected boolean hasHoverEffect = true;
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

	public GItemFrame(Supplier<ItemStack> stackGetter) {
		super(SoftGuiCommon.TEXTURE_1, AppTutorialGui.FRAME_ITEM);
		setAnchor(0.5, 0.5);
		gStack = new GItemStack(stackGetter);
		gStack.setPositionZ(20);
		addChild(gStack);
	}

	public void setRuntime(ISoftGuiRuntime runtime) {
		this.runtime = runtime;
	}

	public void setItemStack(ItemStack itemStack) {
		gStack.setStack(itemStack);
	}

	public void setItemStack(Supplier<ItemStack> stackGetter) {
		gStack.setStack(stackGetter);
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

	public void setShowDisabled(boolean showDisabled) {
		this.showDisabled = showDisabled;
	}

	public void setHasHoverEffect(boolean hasHoverEffect) {
		this.hasHoverEffect = hasHoverEffect;
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
		if (showDisabled) {
			GlStateManager.translate(x, y, z + 40);
			HIGHLIGHT.draw(partialTicks);
			GlStateManager.translate(-x, -y, -z + 40);
		} else if (isHover && hasHoverEffect) {
			GlStateManager.translate(x, y, z + 40);
			HIGHLIGHT.draw(partialTicks);
			GlStateManager.translate(-x, -y, -z + 40);
		}
	}

}

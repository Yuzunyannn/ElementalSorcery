package yuzunyannn.elementalsorcery.computer.softs;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandlerModifiable;
import yuzunyannn.elementalsorcery.api.computer.soft.ISoftGuiRuntime;
import yuzunyannn.elementalsorcery.api.util.MatchHelper;
import yuzunyannn.elementalsorcery.api.util.client.RenderTexutreFrame;
import yuzunyannn.elementalsorcery.computer.render.BtnBaseInteractor;
import yuzunyannn.elementalsorcery.computer.render.GItemFrame;
import yuzunyannn.elementalsorcery.computer.render.GStringBtn;
import yuzunyannn.elementalsorcery.computer.render.SoftGuiCommon;
import yuzunyannn.elementalsorcery.computer.render.SoftGuiThemePart;
import yuzunyannn.elementalsorcery.computer.render.TaskGuiCommon;
import yuzunyannn.elementalsorcery.nodegui.GImage;
import yuzunyannn.elementalsorcery.nodegui.GLabel;
import yuzunyannn.elementalsorcery.nodegui.GNode;
import yuzunyannn.elementalsorcery.util.helper.Color;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

public class TaskInventoryItemSelectGui extends TaskGuiCommon {

	public TaskInventoryItemSelectGui(TaskInventoryItemSelect appInst) {
		super(appInst);
	}

	protected GNode handlerArea;
	protected GItemFrame selectedFrame;
	protected GStringBtn btnConfirm;
	protected GLabel title;
	protected boolean isTagHover;
	protected int selectedSlot = -1;
	protected double lWidth;

	@Override
	protected void onInit(ISoftGuiRuntime runtime) {
		super.onInit(runtime);

		Color color2 = this.getThemeColor(SoftGuiThemePart.BACKGROUND_2);
		Color colorObj1 = this.getThemeColor(SoftGuiThemePart.OBJECT_1);
		Color colorObj2 = this.getThemeColor(SoftGuiThemePart.OBJECT_2);

		lWidth = 40;

		GImage input = new GImage(SoftGuiCommon.TEXTURE_1, FRAME_P2_LEFT);
		input.setColorRef(color2);
		input.setSplit9();
		input.setSize(lWidth, cHeight);
		bg.addChild(input);

		GStringBtn btnClose = new GStringBtn(() -> onCloseCurrAPP(), color2, colorObj2);
		btnClose.setTranslateKey("es.app.cancel");
		btnClose.setPosition((lWidth - btnClose.getWidth()) / 2, cHeight - 10, 10);
		input.addChild(btnClose);

		btnConfirm = new GStringBtn(() -> onConfirm(), color2, colorObj2);
		btnConfirm.setTranslateKey("es.app.confirm");
		btnConfirm.setPosition((lWidth - btnClose.getWidth()) / 2, cHeight - 24, 10);
		btnConfirm.setClickEnabled(false);
		input.addChild(btnConfirm);

		GImage img = new GImage(SoftGuiCommon.TEXTURE_1, new RenderTexutreFrame(0, 88, 18, 24, 256, 256));
		img.setColorRef(color2);
		img.setAnchor(0.5, 0);
		img.setPosition(lWidth / 2, 10, 0);
		img.setInteractor(new BtnBaseInteractor() {
			public boolean testHit(GNode node, Vec3d worldPos) {
				worldPos = worldPos.add(1, 1, 0);
				if (node.testHit(worldPos)) {
					Vec3d vec = node.getPostionInWorldPos();
					double dy = worldPos.y - vec.y;
					if (dy > 11) return false;
					return true;
				}
				return false;
			};

			public void onHoverChange(GNode node) {
				isTagHover = this.isHover;
			}
		});
		input.addChild(img);

		selectedFrame = new GItemFrame();
		selectedFrame.setColor(color2);
		selectedFrame.setPosition(lWidth / 2, 44, 0);
		input.addChild(selectedFrame);

		title = new GLabel();
		title.setColorRef(colorObj1);
		title.setPosition(lWidth + 2, 3, 40);
		bg.addChild(title);

		handlerArea = new GNode();
		bg.addChild(handlerArea);
	}

	public IItemHandlerModifiable cacheHandler;
	public ItemStack enabledStackCache = ItemStack.EMPTY;
	public int tick;

	@Override
	public void update() {
		super.update();
		tick++;
		TaskInventoryItemSelect app = ((TaskInventoryItemSelect) appInst);
		IItemHandlerModifiable handler = app.getItemHandler();
		ItemStack enabledStack = app.getEnabledStack();
		if (enabledStack != enabledStackCache) {
			enabledStackCache = enabledStack;
			updateTitle();
		}
		if (cacheHandler != handler) {
			cacheHandler = handler;
			handlerArea.removeAllChild();
			Color color1 = this.getThemeColor(SoftGuiThemePart.BACKGROUND_1);
			int lineCount = 9;
			for (int i = 0; i < handler.getSlots(); i++) {
				final int slot = i;
				GItemFrame frame = new GItemFrame() {

					@Override
					public void update() {
						super.update();
						if (tick % 4 != 0) return;
						ItemStack eStack = app.getEnabledStack();
						boolean isEnabled = false;
						if (eStack.isEmpty()) isEnabled = true;
						else if (MatchHelper.isItemMatch(eStack, getItemStack())) isEnabled = true;
						if (isEnabled == !showDisabled) return;
						showDisabled = !isEnabled;
						if (isEnabled) enableClick(() -> onSelect(slot, getItemStack()), null);
						else setInteractor(null);
					}
				};
				frame.setColorRef(color1);
				frame.setShowDisabled(true);
				frame.setRuntime(runtime);
				handlerArea.addChild(frame);
				frame.setItemStack(() -> cacheHandler.getStackInSlot(slot));
				int x = (i % lineCount) * 18;
				int y = (i / lineCount) * 18;
				frame.setPosition(x, y);
			}
			handlerArea.setPositionX(lWidth + (cWidth - lWidth - lineCount * 18) / 2 + 9);
			handlerArea.setPositionY(18 + 9);
		}
		ItemStack currStack = selectedFrame.getItemStack();
		if (!currStack.isEmpty()) {
			try {
				ItemStack stack = handler.getStackInSlot(selectedSlot);
				if (stack == currStack || ItemHelper.areItemsEqual(currStack, stack)) btnConfirm.setClickEnabled(true);
				else onSelect(-1, ItemStack.EMPTY);
			} catch (Exception e) {
				onSelect(-1, ItemStack.EMPTY);
			}
		} else btnConfirm.setClickEnabled(false);

		if (isTagHover) {
			String str = app.getWriteTagDisplayValue();
			if (!str.isEmpty()) this.runtime.setTooltip("write-tag", ISoftGuiRuntime.MOUSE_FOLLOW_VEC, 0, str);
		}
	}

	protected void onSelect(int slot, ItemStack stack) {
		selectedFrame.setItemStack(stack);
		selectedSlot = slot;
	}

	protected void updateTitle() {
		String str = "";
		if (enabledStackCache.isEmpty()) str = I18n.format("es.app.choose", I18n.format("es.app.anyItem"));
		else str = I18n.format("es.app.choose", enabledStackCache.getDisplayName());
		title.setString(str);
	}

	protected void onConfirm() {
		if (selectedSlot < 0) return;
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setString(":m", "wd");
		nbt.setShort("1", (short) selectedSlot);
		runtime.sendOperation(nbt);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onRecvMessage(NBTTagCompound nbt) {
		super.onRecvMessage(nbt);
		int code = nbt.getByte("code");
		if (code < 0) tip(I18n.format("es.app.writeError"));
		else {
			onSelect(-1, ItemStack.EMPTY);
			tip(I18n.format("es.app.writeSuccess"));
		}
	}
}

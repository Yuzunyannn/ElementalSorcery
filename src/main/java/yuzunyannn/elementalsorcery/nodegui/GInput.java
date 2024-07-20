package yuzunyannn.elementalsorcery.nodegui;

import java.util.function.Consumer;
import java.util.function.Supplier;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import yuzunyannn.elementalsorcery.api.util.render.RenderRect;
import yuzunyannn.elementalsorcery.util.helper.Color;
import yuzunyannn.elementalsorcery.util.helper.JavaHelper;

public class GInput extends GNode implements IGInteractor {

	private int cursorStart = 0;
	private int cursorEnd = 0;
	private String val = "";

	protected GScissor scissor;
	protected int blinkTick;
	protected Vec3d cursorStartVec = Vec3d.ZERO;
	protected Vec3d cursorEndVec = null;
	protected boolean isForce = false;
	protected GInputShift wordShift;
	protected IAutoCompletable completer;
	protected Consumer<String> publisher;
	protected Supplier<GInputShift> historian;
	protected GLabel label = new GLabel() {
		@Override
		protected void render(float partialTicks) {
			super.render(partialTicks);
			renderCursor(partialTicks);
		}
	};

	public GInput() {
		this.setInteractor(this);
		this.addChild(scissor = new GScissor());
		scissor.addChild(this.label);
		this.label.setColorRef(color);
	}

	public void setCompleter(IAutoCompletable completer) {
		this.completer = completer;
	}

	public void setPublisher(Consumer<String> publisher) {
		this.publisher = publisher;
	}

	public void setHistorian(Supplier<GInputShift> historian) {
		this.historian = historian;
	}

	@Override
	public void setSize(double width, double height) {
		this.setSize(new Vec3d(width, height, depth));
	}

	@Override
	public void setSize(double width, double height, double depth) {
		this.setSize(new Vec3d(width, height, depth));
	}

	@Override
	public void setSize(Vec3d size) {
		super.setSize(size);
		scissor.setRect(new RenderRect(0, size.y, 0, size.x));
	}

	@Override
	public void setColorRef(Color color) {
		super.setColorRef(color);
		this.label.setColorRef(color);
	}

	public GLabel getLabel() {
		return label;
	}

	public void setForce(boolean isForce) {
		if (this.isForce == isForce) return;
		this.isForce = isForce;
	}

	@Override
	public boolean blockMouseEvent(GNode node, Vec3d worldPos) {
		return false;
	}

	@Override
	public boolean testHit(GNode node, Vec3d worldPos) {
		boolean hitMy = this.testHit(worldPos);
		if (!hitMy) setForce(false);
		return hitMy;
	}

	private int selectStart = -1;

	@Override
	public boolean onMousePressed(GNode node, Vec3d worldPos) {
		setForce(true);
		int btnId = Mouse.getEventButton();
		if (btnId == 1) {
			if (cursorStart == cursorEnd) pasteVal();
			else copyVal();
			return false;
		}
		wordShift = null;
		worldPos = worldPos.subtract(label.x, label.y, 0);
		int currIndex = label.getCharIndex(worldPos);
		Vec3d aVec = label.getCharPosition(currIndex + 1);
		Vec3d bVec = label.getCharPosition(currIndex);
		double h = (aVec.x + bVec.x) / 2;
		if (worldPos.x <= h) currIndex = Math.max(0, currIndex - 1);
		cursorAdjustIndex = selectStart = cursorStart = cursorEnd = currIndex;
		updateCursor();
		return true;
	}

	protected void copyVal() {
		if (val.isEmpty()) return;
		String str = val.substring(cursorStart, cursorEnd).trim();
		cursorStart = cursorEnd;
		updateCursor();
		if (str.isEmpty()) return;
		JavaHelper.clipboardWrite(str);
	}

	protected void pasteVal() {
		String val = JavaHelper.clipboardRead();
		if (val.isEmpty()) return;
		insert(val);
	}

	@Override
	public void onMouseDrag(GNode node, Vec3d worldPos) {
		if (selectStart < 0) return;
		worldPos = worldPos.subtract(label.x, label.y, 0);
		cursorAdjustIndex = label.getCharIndex(worldPos);
		if (cursorAdjustIndex < selectStart) cursorAdjustIndex -= 1;
		updateSelect(Math.max(cursorAdjustIndex, 0));
	}

	protected void updateSelect(int index) {
		if (selectStart < 0) return;
		if (index > selectStart) {
			cursorStart = selectStart;
			cursorEnd = index;
			updateCursor(false);
		} else {
			cursorEnd = selectStart;
			cursorStart = index;
			updateCursor(true);
		}
	}

	@Override
	public void onMouseReleased(GNode node, Vec3d worldPos) {
		cursorAdjustIndex = -1;
		selectStart = -1;
	}

	@Override
	public boolean isListenKeyboard(GNode node) {
		return true;
	}

	protected int cursorAdjustIndex = -1;

	@Override
	public boolean onKeyPressed(GNode node, int keyCode) {
		if (!isForce) return false;
		GInputShift shift = wordShift;
		wordShift = null;

		switch (keyCode) {
		case Keyboard.KEY_LEFT:
			if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
				if (cursorAdjustIndex < 0 || selectStart < 0) {
					cursorAdjustIndex = cursorStart;
					selectStart = cursorEnd;
				}
				updateSelect(Math.max(--cursorAdjustIndex, 0));
			} else {
				selectStart = -1;
				if (cursorStart != cursorEnd) cursorEnd = cursorStart;
				else cursorStart = cursorEnd = Math.max(0, cursorStart - 1);
				updateCursor();
			}
			break;
		case Keyboard.KEY_RIGHT:
			if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
				if (cursorAdjustIndex < 0 || selectStart < 0) {
					cursorAdjustIndex = cursorEnd;
					selectStart = cursorStart;
				}
				updateSelect(Math.min(++cursorAdjustIndex, val.length()));
			} else {
				selectStart = -1;
				if (cursorStart != cursorEnd) cursorStart = cursorEnd;
				else cursorStart = cursorEnd = Math.min(val.length(), cursorEnd + 1);
				updateCursor();
			}
			break;
		case Keyboard.KEY_UP:
		case Keyboard.KEY_DOWN:
			if (shift == null) {
				if (historian != null) {
					wordShift = historian.get();
					if (wordShift != null) {
						wordShift.flag = 0x01f;
						wordShift.startIndex = 0;
						wordShift.endIndex = val.length();
					}
				}
			} else wordShift = shift;
			if (wordShift != null) doWordShift(keyCode == Keyboard.KEY_UP ? -1 : 1);
			break;
		case Keyboard.KEY_TAB:
			wordShift = shift;
			if (wordShift != null && (wordShift.flag & 0x01) == 0) {
				if (wordShift.selectIndex <= 0) wordShift.selectIndex = wordShift.size() - 1;
				doWordShift(-1);
			} else autoComplete();
			break;
		case Keyboard.KEY_DELETE:
			remove(false);
			break;
		case Keyboard.KEY_BACK:
			remove(true);
			break;
		}
		return true;
	}

	@Override
	public boolean onKeyRelease(GNode node, int keyCode) {
		switch (keyCode) {
		case Keyboard.KEY_LSHIFT:
			selectStart = -1;
			cursorAdjustIndex = -1;
			break;
		}
		return isForce;
	}

	@Override
	public void onKeyInput(GNode node, char ch) {
		if (!isForce) return;
		switch (ch) {
		case '\r':
			publish();
			break;
		case 1:
			cursorStart = 0;
			cursorEnd = val.length();
			updateCursor();
			break;
		default:
			if (ch <= 31) break;
			insert(ch);
		}
	}

	public void clear() {
		val = "";
		cursorStart = cursorEnd = 0;
		label.setString(val);
		updateCursor();
	}

	public void setString(String str) {
		val = str;
		cursorStart = cursorEnd = str.length();
		label.setString(val);
		updateCursor();
	}

	public void remove(boolean front) {
		String _start = val.substring(0, cursorStart);
		String _end = val.substring(cursorEnd);
		if (cursorStart != cursorEnd) {
			val = _start + _end;
			cursorEnd = cursorStart;
		} else {
			if (front) {
				cursorEnd = cursorStart = Math.max(0, cursorStart - 1);
				val = _start.substring(0, Math.max(0, _start.length() - 1)) + _end;
			} else val = _start + (_end.length() > 0 ? _end.substring(1) : "");
		}
		label.setString(val);
		updateCursor();
	}

	public void insert(char ch) {
		String _start = val.substring(0, cursorStart);
		String _end = val.substring(cursorEnd);
		if (cursorStart != cursorEnd) {
			val = _start + ch + _end;
			cursorEnd = cursorStart = cursorStart + 1;
		} else {
			val = _start + ch + _end;
			cursorEnd = cursorStart = cursorStart + 1;
		}
		label.setString(val);
		updateCursor();
	}

	public void insert(String str) {
		String _start = val.substring(0, cursorStart);
		String _end = val.substring(cursorEnd);
		if (cursorStart != cursorEnd) {
			val = _start + str + _end;
			cursorEnd = cursorStart = cursorStart + str.length();
		} else {
			val = _start + str + _end;
			cursorEnd = cursorStart = cursorStart + str.length();
		}
		label.setString(val);
		updateCursor();
	}

	private void updateCursor() {
		updateCursor(null);
	}

	protected void updateCursor(Boolean isLeft) {
		blinkTick = 0;
		cursorStartVec = label.getCharPosition(cursorStart);
		if (cursorStart < cursorEnd) cursorEndVec = label.getCharPosition(cursorEnd);
		else cursorEndVec = null;

		Vec3d useVec = cursorEndVec == null ? cursorStartVec : cursorEndVec;
		if (isLeft != null) {
			if (isLeft) useVec = cursorStartVec;
			else useVec = cursorEndVec == null ? cursorStartVec : cursorEndVec;
		}
		if (this.label.x + useVec.x > this.width) this.label.setPositionX(this.width - useVec.x);
		else if (this.label.x + useVec.x < 0) this.label.setPositionX(-useVec.x);
	}

	public void publish() {
		if (this.publisher != null) this.publisher.accept(val);
	}

	protected GInputShift findAutoComplete() {
		return completer != null ? completer.tryComplete(val, cursorStart) : null;
	}

	protected void doWordShift(int dindex) {
		wordShift.selectIndex = wordShift.selectIndex + dindex;
		if (wordShift.selectIndex < 0) {
			wordShift.selectIndex = 0;
			return;
		}
		if (wordShift.selectIndex >= wordShift.size()) {
			wordShift.selectIndex = wordShift.size() - 1;
			return;
		}
		String str = wordShift.get(wordShift.selectIndex);
		cursorStart = wordShift.startIndex;
		cursorEnd = wordShift.endIndex;
		insert(str);
		wordShift.endIndex = cursorStart = cursorEnd = wordShift.startIndex + str.length();
		updateCursor();
	}

	public void autoComplete() {
		GInputShift aComplete = findAutoComplete();
		if (aComplete == null) return;
		if (aComplete.isEmpty()) return;
		this.wordShift = aComplete;
		aComplete.startIndex = MathHelper.clamp(aComplete.startIndex, 0, val.length());
		aComplete.endIndex = MathHelper.clamp(aComplete.endIndex, 0, val.length());
		if (aComplete.startIndex > aComplete.endIndex) {
			int tmp = aComplete.startIndex;
			aComplete.startIndex = aComplete.endIndex;
			aComplete.endIndex = tmp;
		}
		wordShift.selectIndex = aComplete.size();
		doWordShift(-1);
	}

	@Override
	public void update() {
		super.update();
		blinkTick++;
	}

	protected void renderCursor(float partialTicks) {
		if (!isForce) return;
		if (blinkTick % 16 > 8) return;
		Color color = this.label.color;
		int fheight = mc.fontRenderer.FONT_HEIGHT;
		GlStateManager.disableTexture2D();
		GlStateManager.enableColorLogic();
		GlStateManager.colorLogicOp(GlStateManager.LogicOp.OR_REVERSE);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();

		double c_sx = cursorStartVec.x;
		double c_sy = cursorStartVec.y;

		if (cursorEndVec != null) {
			double c_ex = cursorEndVec.x;
			double c_ey = cursorEndVec.y;
			float alpha = label.rAlpha;
			bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
			bufferbuilder.pos(c_sx, c_sy, 0).color(color.r, color.g, color.b, alpha).endVertex();
			bufferbuilder.pos(c_sx, c_sy + fheight, 0).color(color.r, color.g, color.b, alpha).endVertex();
			bufferbuilder.pos(c_ex, c_ey + fheight, 0).color(color.r, color.g, color.b, alpha).endVertex();
			bufferbuilder.pos(c_ex, c_ey, 0).color(color.r, color.g, color.b, alpha).endVertex();
			tessellator.draw();
		} else {
			GlStateManager.glLineWidth(3);
			bufferbuilder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
			bufferbuilder.pos(c_sx, c_sy, 0).color(color.r, color.g, color.b, label.rAlpha).endVertex();
			bufferbuilder.pos(c_sx, c_sy + fheight, 0).color(color.r, color.g, color.b, label.rAlpha).endVertex();
			tessellator.draw();
		}

		GlStateManager.disableColorLogic();
		GlStateManager.enableTexture2D();
	}

}

package yuzunyannn.elementalsorcery.parchment;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import yuzunyannn.elementalsorcery.api.computer.soft.ISoftGuiRuntime;
import yuzunyannn.elementalsorcery.api.util.render.RenderTexutreFrame;
import yuzunyannn.elementalsorcery.computer.render.GItemFrame;
import yuzunyannn.elementalsorcery.container.gui.GuiComputerTutorialPad;
import yuzunyannn.elementalsorcery.nodegui.GImage;
import yuzunyannn.elementalsorcery.nodegui.GNode;
import yuzunyannn.elementalsorcery.util.helper.Color;

public class TutorialCraft {

	public static class TutorialCraftNodeParams {
		public double width;
		public double height;
		public Color color;
		public ISoftGuiRuntime gui;
		public Consumer<ItemStack> click;
		public Tutorial tutorial;
	}

	static public final List<Function<ItemStack, TutorialCraft>> tryCreators = new ArrayList<>();

	static {
		init();
	}

	public static void init() {
		tryCreators.clear();
		tryCreators.add(TutorialCraftSP::tryCreate);
		tryCreators.add(TutorialCraftDesk::tryCreate);
		tryCreators.add(TutorialCraftSeek::tryCreate);
		tryCreators.add(TutorialCraftInfusion::tryCreate);
		tryCreators.add(TutorialCraftSmashHammer::tryCreate);
		tryCreators.add(TutorialCraftMeltCauldron::tryCreate);
		tryCreators.add(TutorialCraftES::tryCreate);
		tryCreators.add(TutorialCraftMC::tryCreate);
		tryCreators.add(TutorialCraftResearch::tryCreate);
		tryCreators.add(TutorialCraftSmelting::tryCreate);
	}

	static public List<TutorialCraft> create(ItemStack result) {
		List<TutorialCraft> list = new ArrayList<>();
		for (Function<ItemStack, TutorialCraft> tryCreator : tryCreators) {
			TutorialCraft craft = tryCreator.apply(result);
			if (craft != null) list.add(craft);
		}
		return list;
	}

	static protected <T, V> Entry<T, V> entryOf(T t, V v) {
		return new AbstractMap.SimpleEntry(t, v);
	}

	static public boolean isItemStackThinkSame(ItemStack sample, ItemStack other) {
		// return MatchHelper.isItemMatch(sample, other)
		if (sample.getItem() != other.getItem()) return false;
		if (sample.getHasSubtypes() && sample.getMetadata() != other.getMetadata()) return false;
		if (sample.getTagCompound() != null) {
			if (!ItemStack.areItemStackTagsEqual(sample, other)) return false;
		}
		return true;
	}

	protected abstract class GShowCommon extends GNode {

		protected int showIndex;
		protected int tick;
		protected int shiftTick = 40;
		protected final TutorialCraftNodeParams params;
		protected GItemFrame craft = new GItemFrame();

		public GShowCommon(TutorialCraftNodeParams params) {
			this.params = params;
		}

		protected void initCraft(TutorialCraftNodeParams params, ItemStack icon) {
			craft.setColorRef(params.color);
			craft.setPosition(params.width / 2 - 10, -params.height / 2 + 10);
			craft.setItemStack(icon);
//			craft.enableClick(null, null);
//			craft.setRuntime(params.gui);
			addChild(craft);
		}

		protected void initCraft(TutorialCraftNodeParams params, Item icon) {
			initCraft(params, new ItemStack(icon));
		}

		protected void initCraft(TutorialCraftNodeParams params, Block icon) {
			initCraft(params, new ItemStack(icon));
		}

		protected void onClick(GItemFrame frame) {
			ItemStack stack = frame.getItemStack();
			if (stack.isEmpty()) return;
			this.params.click.accept(stack);
		}

		protected GItemFrame addSlot(double x, double y) {
			GItemFrame frame = new GItemFrame(ItemStack.EMPTY);
			frame.setColorRef(params.color);
			frame.setPosition(x, y, 10);
			frame.enableClick(() -> onClick(frame), null);
			frame.setRuntime(params.gui);
			addChild(frame);
			return frame;
		}

		protected GImage addArrow(double x, double y) {
			GImage arrow = new GImage(GuiComputerTutorialPad.TEXTURE,
					new RenderTexutreFrame(19, 196, 22, 15, 256, 256));
			arrow.setColorRef(params.color);
			arrow.setAnchor(0.5, 0.5);
			arrow.setPosition(x, y);
			addChild(arrow);
			return arrow;
		}

		protected ItemStack getItemStack(Ingredient ingredient) {
			ItemStack[] stacks = ingredient.getMatchingStacks();
			if (stacks.length == 0) return ItemStack.EMPTY;
			return stacks[getDTIndex(stacks.length)];
		}

		protected ItemStack getItemStack(List<ItemStack> list) {
			if (list.isEmpty()) return ItemStack.EMPTY;
			return list.get(getDTIndex(list.size()));
		}

		protected <T> T getElement(List<T> list) {
			if (list.isEmpty()) return null;
			return list.get(getDTIndex(list.size()));
		}

		protected int getDTIndex(int length) {
			int dt = tick / 20;
			return dt % length;
		}

		protected abstract void updateCraft();

		protected Collection<?> getElements() {
			return null;
		}

		@Override
		public void update() {
			super.update();
			tick++;
			if (tick % 20 == 0) {
				Collection<?> collection = getElements();
				int size = collection == null ? 1 : collection.size();
				if (size > 0) {
					if (tick % shiftTick == 0) showIndex = (showIndex + 1) % size;
					updateCraft();
				}
			}
		}
	}

	final public GNode createNodeContainer(TutorialCraftNodeParams params) {
		GShowCommon container = createMyContainer(params);
		if (container == null) return null;
		container.setPosition(params.width / 2, params.height / 2, 0);
		container.updateCraft();
		return container;
	}

	protected GShowCommon createMyContainer(TutorialCraftNodeParams params) {
		return null;
	}

}

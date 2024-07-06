package yuzunyannn.elementalsorcery.parchment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.util.render.RenderTexutreFrame;
import yuzunyannn.elementalsorcery.computer.render.GItemFrame;
import yuzunyannn.elementalsorcery.container.gui.GuiComputerTutorialPad;
import yuzunyannn.elementalsorcery.nodegui.GImage;
import yuzunyannn.elementalsorcery.tile.TileMeltCauldron;
import yuzunyannn.elementalsorcery.tile.TileMeltCauldron.MeltCauldronRecipe;

public class TutorialCraftMeltCauldron extends TutorialCraft {

	static public TutorialCraftMeltCauldron tryCreate(ItemStack itemStack) {
		List<MeltCauldronRecipe> recipes = TileMeltCauldron.recipes;
		List<MeltCauldronRecipe> finded = new ArrayList<>();
		for (MeltCauldronRecipe recipe : recipes) {
			for (ItemStack result : recipe.getResultList()) {
				if (isItemStackThinkSame(itemStack, result)) {
					finded.add(recipe);
				}
				// 目前只关心主产物
				break;
			}
		}
		if (finded.isEmpty()) return null;
		return new TutorialCraftMeltCauldron(finded);
	}

	protected List<MeltCauldronRecipe> list;

	protected TutorialCraftMeltCauldron(List<MeltCauldronRecipe> recipes) {
		this.list = recipes;
	}

	@Override
	public GShowCommon createMyContainer(TutorialCraftNodeParams params) {
		return new GShow(params);
	}

	protected class GShow extends GShowCommon {

		protected List<GItemFrame> inputs = new ArrayList<>(8);
		protected GItemFrame output;

		public GShow(TutorialCraftNodeParams params) {
			super(params);
			initCraft(params, ESObjects.BLOCKS.MELT_CAULDRON);

			shiftTick = 100;
			double xOffset = 0;
			double yOffset = 0;

			int length = 8;
			double dx = -length * 20 / 2 + 20 - 9;
			for (int i = 0; i < length; i++) inputs.add(addSlot(dx + xOffset + i * 20, yOffset - 30));

			output = addSlot(xOffset, 10 + yOffset);

			double width = params.width * 0.85;

			GImage bg = new GImage(GuiComputerTutorialPad.TEXTURE, new RenderTexutreFrame(70, 178, 17, 12, 256, 256));
			bg.setSplit9();
			bg.setColorRef(params.color);
			bg.setSize(width, 60);
			bg.setAnchor(0.5, 0.5);
			bg.setPosition(0, 0);
			addChild(bg);

			width = width - 13;
			for (int i = 0; i < 8; i++) {
				GImage fire = new GImage(GuiComputerTutorialPad.TEXTURE,
						new RenderTexutreFrame(0, 180, 13, 13, 256, 256));
				fire.setColorRef(params.color);
				fire.setAnchor(0.5, 0);
				fire.setPosition(-width / 2 + (i / 7.0) * width, 30);
				addChild(fire);
			}

			width = width - 38;
			for (int i = 0; i < 4; i++) {
				GImage arrow = new GImage(GuiComputerTutorialPad.TEXTURE,
						new RenderTexutreFrame(0, 194, 15, 22, 256, 256));
				arrow.setColorRef(params.color);
				arrow.setAnchor(0.5, 0.5);
				arrow.setPosition(-width / 2 + (i / 3.0) * width + 1, -10);
				arrow.setScale(new Vec3d(1, -1, 1));
				addChild(arrow);
			}

		}

		@Override
		protected Collection<?> getElements() {
			return list;
		}

		public void updateCraft() {
			if (list.isEmpty()) return;
			MeltCauldronRecipe recipe = list.get(showIndex);
			List<Ingredient> list = recipe.getNeedList();
			for (int i = 0; i < inputs.size(); i++) {
				if (i < list.size()) inputs.get(i).setItemStack(getItemStack(list.get(i)));
				else inputs.get(i).setItemStack(ItemStack.EMPTY);
			}
			List<Entry<Float, ItemStack>> results = recipe.getResultEntryList();
			if (list.isEmpty()) output.setItemStack(ItemStack.EMPTY);
			else {
				int dt = tick / 50;
				Entry<Float, ItemStack> entry = results.get(dt % results.size());
				output.setItemStack(entry.getValue());
				output.setTooltipHook(tooltip -> {
					tooltip.add(TextFormatting.YELLOW + I18n.format("info.standardDeviation") + " < " + entry.getKey());
				});
			}
		}

	}

}

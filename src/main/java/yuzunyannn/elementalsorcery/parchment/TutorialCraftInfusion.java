package yuzunyannn.elementalsorcery.parchment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.util.client.RenderTexutreFrame;
import yuzunyannn.elementalsorcery.computer.render.GItemFrame;
import yuzunyannn.elementalsorcery.computer.render.SoftGuiCommon;
import yuzunyannn.elementalsorcery.computer.soft.AppTutorialGui;
import yuzunyannn.elementalsorcery.container.gui.GuiComputerTutorialPad;
import yuzunyannn.elementalsorcery.nodegui.GImage;
import yuzunyannn.elementalsorcery.nodegui.GLabel;
import yuzunyannn.elementalsorcery.tile.md.TileMDInfusion;
import yuzunyannn.elementalsorcery.tile.md.TileMDInfusion.Recipe;
import yuzunyannn.elementalsorcery.util.helper.Color;

public class TutorialCraftInfusion extends TutorialCraft {

	static public TutorialCraftInfusion tryCreate(ItemStack itemStack) {
		List<Recipe> recipes = TileMDInfusion.recipes;
		List<Recipe> finded = new ArrayList<>();
		for (Recipe recipe : recipes) {
			if (isItemStackThinkSame(itemStack, recipe.getOutput())) finded.add(recipe);
		}
		if (finded.isEmpty()) return null;
		return new TutorialCraftInfusion(finded);
	}

	protected List<Recipe> list;

	protected TutorialCraftInfusion(List<Recipe> recipes) {
		this.list = recipes;
	}

	@Override
	public GShowCommon createMyContainer(TutorialCraftNodeParams params) {
		return new GShow(params);
	}

	protected class GShow extends GShowCommon {

		protected GItemFrame input;
		protected GItemFrame outputs[];
		protected GLabel label;

		public GShow(TutorialCraftNodeParams params) {
			super(params);
			initCraft(params, ESObjects.BLOCKS.MD_INFUSION);
			
			double xOffset = 0;
			double yOffset = -10;

			outputs = new GItemFrame[5];

			input = addSlot(xOffset, -21 + yOffset);

			double offsetX = xOffset - 44;
			double offsetY = -25 + yOffset;

			outputs[0] = addSlot(offsetX + 0, offsetY + 41);
			outputs[1] = addSlot(offsetX + 22, offsetY + 49);
			outputs[2] = addSlot(offsetX + 44, offsetY + 57);
			outputs[3] = addSlot(offsetX + 66, offsetY + 49);
			outputs[4] = addSlot(offsetX + 88, offsetY + 41);

			for (int i = 0; i < outputs.length; i++) {
				int diff = 2 - Math.abs(i - 2);
				Vec3d vec = outputs[i].getPostion();
				GImage line = new GImage(GuiComputerTutorialPad.TEXTURE,
						new RenderTexutreFrame(46 + diff * 5, 196, 5, 13 + diff * 8, 256, 256));
				line.setColorRef(params.color);
				line.setAnchor(0.5, 1);
				line.setPosition(vec.x, vec.y - 9);
				addChild(line);
			}

			GImage magicContainer = new GImage(SoftGuiCommon.TEXTURE_1, AppTutorialGui.FRAME_ITEM);
			magicContainer.setSplit9();
			magicContainer.setColorRef(params.color);
			magicContainer.setSize(130, 12);
			magicContainer.setAnchor(0.5, 1);
			magicContainer.setPosition(xOffset, yOffset, 100);
			addChild(magicContainer);

			label = new GLabel();
			magicContainer.addChild(label);
			label.setColor(new Color(0x7900d8));
			label.setAnchor(0.5, 1);
			label.setPositionY(-1);
		}

		@Override
		protected Collection<?> getElements() {
			return list;
		}

		public void updateCraft() {
			if (list.isEmpty()) return;
			Recipe recipe = list.get(showIndex);
			input.setItemStack(recipe.getInput());

			int power = recipe.getCost().getPower();
			int cost = -Integer.MAX_VALUE;
			for (int i = 0; i < 5; i++) {
				ElementStack maxMagic = TileMDInfusion.getMaxOfferMagic(i);
				if (maxMagic.getCount() >= recipe.getCost().getCount() && maxMagic.getPower() >= power) {
					if (cost < maxMagic.getCount()) cost = maxMagic.getCount();
					outputs[i].setItemStack(recipe.getOutput());
				} else outputs[i].setItemStack(ItemStack.EMPTY);
			}

			String str = TextFormatting.BOLD
					+ I18n.format("page.crafting.show", I18n.format("element.magic.name"), cost, power);
			label.setString(str);
		}

	}

}

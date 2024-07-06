package yuzunyannn.elementalsorcery.parchment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.util.render.RenderTexutreFrame;
import yuzunyannn.elementalsorcery.computer.render.GItemFrame;
import yuzunyannn.elementalsorcery.container.gui.GuiComputerTutorialPad;
import yuzunyannn.elementalsorcery.nodegui.GImage;

public class TutorialCraftSmelting extends TutorialCraft {

	static public TutorialCraftSmelting tryCreate(ItemStack itemStack) {
		FurnaceRecipes recipes = FurnaceRecipes.instance();
		Map<ItemStack, ItemStack> map = recipes.getSmeltingList();
		List<Entry<ItemStack, ItemStack>> useRecipes = new ArrayList<>();
		for (Entry<ItemStack, ItemStack> entry : map.entrySet()) {
			if (isItemStackThinkSame(itemStack, entry.getValue())) {
				ItemStack input = entry.getKey();
				if (input.getMetadata() == 32767) input = new ItemStack(input.getItem(), input.getCount());
				useRecipes.add(entryOf(input, entry.getValue()));
			}
		}
		if (useRecipes.isEmpty()) return null;
		return new TutorialCraftSmelting(useRecipes);
	}

	protected List<Entry<ItemStack, ItemStack>> list;

	protected TutorialCraftSmelting(List<Entry<ItemStack, ItemStack>> recipes) {
		this.list = recipes;
	}

	@Override
	public GShowCommon createMyContainer(TutorialCraftNodeParams params) {
		return new GShow(params);
	}

	protected class GShow extends GShowCommon {

		protected GItemFrame input;
		protected GItemFrame output;

		public GShow(TutorialCraftNodeParams params) {
			super(params);
			initCraft(params, ESObjects.BLOCKS.SMELT_BOX);
			
			double xOffset = -8;
			input = addSlot(xOffset - 25, 0);
			output = addSlot(xOffset + 25, 0);
			addArrow(xOffset, 0);

			GImage fire = new GImage(GuiComputerTutorialPad.TEXTURE, new RenderTexutreFrame(0, 180, 13, 13, 256, 256));
			fire.setColorRef(params.color);
			fire.setAnchor(0.5, 0);
			fire.setPosition(xOffset - 25, 10);
			addChild(fire);
		}

		@Override
		protected Collection<?> getElements() {
			return list;
		}

		public void updateCraft() {
			if (list.isEmpty()) return;
			Entry<ItemStack, ItemStack> entry = list.get(showIndex);
			input.setItemStack(entry.getKey());
			output.setItemStack(entry.getValue());
		}

	}

}

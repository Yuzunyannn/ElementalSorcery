package yuzunyannn.elementalsorcery.parchment;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.crafting.IResearchRecipe;
import yuzunyannn.elementalsorcery.computer.render.GItemFrame;
import yuzunyannn.elementalsorcery.elf.research.ResearchRecipeManagement;
import yuzunyannn.elementalsorcery.elf.research.Topic;
import yuzunyannn.elementalsorcery.elf.research.Topics;
import yuzunyannn.elementalsorcery.logics.EventClient;
import yuzunyannn.elementalsorcery.util.helper.ColorHelper;
import yuzunyannn.elementalsorcery.util.helper.JavaHelper;

public class TutorialCraftResearch extends TutorialCraft {

	static public TutorialCraftResearch tryCreate(ItemStack itemStack) {
		List<IResearchRecipe> list = new ArrayList<>();
		Map<ResourceLocation, IResearchRecipe> map = ResearchRecipeManagement.instance.getRecipes();
		for (IResearchRecipe ire : map.values()) {
			if (isItemStackThinkSame(itemStack, ire.getRecipeOutput())) list.add(ire);
		}
		if (list.isEmpty()) return null;
		return new TutorialCraftResearch(list);
	}

	protected List<IResearchRecipe> list;

	public TutorialCraftResearch(List<IResearchRecipe> irecipes) {
		list = irecipes;
	}

	@Override
	public GShowCommon createMyContainer(TutorialCraftNodeParams params) {
		return new GShow(params);
	}

	protected class GShow extends GShowCommon {

		protected List<GItemFrame> inputs = new ArrayList<>();
		protected GItemFrame output;
		protected List<Entry<Topic, Integer>> topics = new ArrayList<>();

		@Override
		protected Collection<?> getElements() {
			return list;
		}

		public GShow(TutorialCraftNodeParams params) {
			super(params);
			initCraft(params, ESObjects.BLOCKS.RESEARCHER);
			
			double xoffset = 0;
			double padOffsetX = -5 * 18 + xoffset;
			double padOffsetY = 50;
			for (int i = 0; i < 11; i++) {
				inputs.add(addSlot(i * 18 + padOffsetX, padOffsetY));
			}
			output = addSlot(xoffset, -11);
		}

		@Override
		public void updateCraft() {
			if (list.isEmpty()) return;
			IResearchRecipe recipe = list.get(showIndex);
			output.setItemStack(recipe.getRecipeOutput());
			NonNullList<Ingredient> list = recipe.getIngredients();
			for (int i = 0; i < inputs.size(); i++) {
				if (i < list.size()) {
					Ingredient ingredient = list.get(i);
					inputs.get(i).setItemStack(getItemStack(ingredient));
				} else inputs.get(i).setItemStack(ItemStack.EMPTY);
			}
			updateTopics(recipe);
		}

		@Override
		protected void render(float partialTicks) {
			super.render(partialTicks);
			if (topics != null) {
				GlStateManager.pushMatrix();
				GlStateManager.translate(output.getPostionX(), output.getPostionY(), 100);
				renderLine();
				GlStateManager.popMatrix();
			}
		}

		float topicSize = 0;
		float allNeed = 0;
		float mapR = 1, mapG = 1, mapB = 1;

		protected void updateTopics(IResearchRecipe recipe) {
			topics.clear();
			allNeed = 0;
			Set<String> all = Topics.getDefaultTopics();
			List<Entry<String, Integer>> list = recipe.getRecipeInput();
			float size = list.size();
			for (Entry<String, Integer> entry : list) {
				allNeed += entry.getValue() / size;
				Topic topic = Topic.create(entry.getKey());
				topics.add(new AbstractMap.SimpleEntry(topic, entry.getValue()));
				all.remove(entry.getKey());
			}
			for (String key : all) {
				if (topics.size() >= 5) break;
				topics.add(JavaHelper.entry(Topic.create(key), 0));
			}
			int len = topics.size();
			if (len > 8) topicSize = (float) (30 * Math.pow(0.925, len - 8)) * 0.25f;
			else topicSize = 30;
			if (allNeed <= 0.1f) {
				mapR = mapG = mapB = 1;
				return;
			}
			mapR = mapG = mapB = 0;
			for (int i = 0; i < topics.size(); i++) {
				Topic topic = topics.get(i).getKey();
				float rate = topics.get(i).getValue() / (allNeed * size);
				Vec3d color = ColorHelper.color(topic.getColor());
				mapR += color.x * rate;
				mapG += color.y * rate;
				mapB += color.z * rate;
			}
			mapR = Math.min(mapR, 1);
			mapG = Math.min(mapG, 1);
			mapB = Math.min(mapB, 1);
		}

		protected void renderLine() {
			GlStateManager.rotate(EventClient.getGlobalRotateInRender(mc.getRenderPartialTicks()) / 8, 0, 0, 1);
			int size = topics.size();
			float dR = 360f / size;
			for (int n = 0; n < size; n++) {
				GlStateManager.pushMatrix();
				float roate = n * dR;
				GlStateManager.rotate(roate, 0, 0, 1);
				Entry<Topic, Integer> entry = topics.get(n);
				GlStateManager.translate(0, -42, 0);
				entry.getKey().render(mc, topicSize, 1, 0);
				GlStateManager.popMatrix();
			}
			GlStateManager.color(1, 1, 1, rAlpha);
			dR = dR / 180.0f * 3.1415926f;
			GlStateManager.disableTexture2D();
			for (int n = 1; n <= size; n++) {
				Entry<Topic, Integer> entry1 = topics.get(n - 1);
				Entry<Topic, Integer> entry2 = topics.get(n % size);
				float roate = n * dR;
				float r1 = Math.min(1, entry1.getValue() / allNeed);
				float l1 = r1 * 18 + 16;
				float x1 = MathHelper.sin(roate - dR) * l1;
				float y1 = -MathHelper.cos(roate - dR) * l1;
				float r2 = Math.min(1, entry2.getValue() / allNeed);
				float l2 = r2 * 18 + 16;
				float x2 = MathHelper.sin(roate) * l2;
				float y2 = -MathHelper.cos(roate) * l2;
				drawLine(x1, y1, x2, y2, mapR, mapG, mapB, rAlpha);
			}
			GlStateManager.enableTexture2D();
		}

		protected void drawLine(float x1, float y1, float x2, float y2, float r, float g, float b, float alpha) {
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferbuilder = tessellator.getBuffer();
			GlStateManager.glLineWidth(4);
			bufferbuilder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
			bufferbuilder.pos(x1, y1, 0).color(r, g, b, alpha).endVertex();
			bufferbuilder.pos(x2, y2, 0).color(r, g, b, alpha).endVertex();
			tessellator.draw();
		}
	}

}

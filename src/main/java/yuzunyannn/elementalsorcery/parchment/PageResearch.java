package yuzunyannn.elementalsorcery.parchment;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.crafting.IResearchRecipe;
import yuzunyannn.elementalsorcery.elf.research.ResearchRecipeManagement;
import yuzunyannn.elementalsorcery.elf.research.Topic;
import yuzunyannn.elementalsorcery.elf.research.Topics;
import yuzunyannn.elementalsorcery.event.EventClient;
import yuzunyannn.elementalsorcery.util.ColorHelper;
import yuzunyannn.elementalsorcery.util.GameHelper;

public class PageResearch extends PageTransform {

	protected List<Entry<Topic, Integer>> topics = new ArrayList<>();;
	protected ItemStack output = ItemStack.EMPTY;
	protected NonNullList<Ingredient> ingredient;

	protected int nowIndex = 0;
	protected float topicSize = 0;
	protected float allNeed = 0;
	protected float mapR = 1, mapG = 1, mapB = 1;

	public PageResearch(ItemStack stack) {
		List<IResearchRecipe> allRecipe = new LinkedList();
		for (IResearchRecipe ire : ResearchRecipeManagement.instance.getRecipes().values()) {
			if (ire.getRecipeOutput().isItemEqual(stack)) allRecipe.add(ire);
		}
		IResearchRecipe recipe = allRecipe.get(0);
		output = recipe.getRecipeOutput();
		ingredient = recipe.getIngredients();
		GameHelper.clientRun(() -> {
			this.initView(recipe);
		});
	}

	private void initView(IResearchRecipe recipe) {
		List<Entry<String, Integer>> topicInput = recipe.getRecipeInput();
		allNeed = 0;
		Set<String> all = Topics.getDefaultTopics();
		float size = topicInput.size();
		for (Entry<String, Integer> entry : topicInput) {
			allNeed += entry.getValue() / size;
			Topic topic = Topic.create(entry.getKey());
			topics.add(new AbstractMap.SimpleEntry(topic, entry.getValue()));
			all.remove(entry.getKey());
		}
		for (String key : all) {
			if (topics.size() >= 5) break;
			topics.add(new AbstractMap.SimpleEntry(Topic.create(key), 0));
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

	@Override
	public ItemStack getOutput() {
		return output;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void init(IPageManager pageManager) {
		int cX = this.getCX() + 1;
		int cY = this.getCY() + 1;
		pageManager.addSlot(cX + 23, cY, this.getOutput());
		this.initList(pageManager, cX, cY + 20);
	}

	@Override
	public void update(IPageManager pageManager) {
		if (EventClient.tick % 20 == 0) {
			nowIndex++;
			if (nowIndex < 0) nowIndex = 0;
			this.reflushListShow(pageManager);
		}
	}

	@Override
	public void customButtonAction(GuiButton button, IPageManager pageManager) {
		if (button.id == 0) {
			if (listAt > 0) {
				listAt--;
				this.reflushListShow(pageManager);
			}
		} else {
			if (listAt < ingredient.size() - 4) {
				listAt++;
				this.reflushListShow(pageManager);
			}
		}
	}

	@Override
	protected void reflushListShow(IPageManager pageManager) {
		for (int i = 0; i < 4 && listAt + i < ingredient.size(); i++) {
			ItemStack[] stacks = ingredient.get(listAt + i).getMatchingStacks();
			pageManager.setSlot(i + 1, stacks[nowIndex % stacks.length]);
		}
	}

	@Override
	public void drawBackground(int xoff, int yoff, IPageManager pageManager) {
		int cX = this.getCX() + xoff;
		int cY = this.getCY() + yoff;
		GuiContainer gui = pageManager.getGui();
		gui.drawTexturedModalRect(cX + 23, cY, 95, 166, 18, 18);
		gui.drawTexturedModalRect(cX - 9 + 5, cY + 37 + 20, 41, 166, 72, 18);
		GlStateManager.pushMatrix();
		GlStateManager.translate(cX + 32, cY + 8, 0);
		int size = topics.size();
		float dR = 360f / size;
		for (int n = 0; n < size; n++) {
			GlStateManager.pushMatrix();
			float roate = n * dR;
			GlStateManager.rotate(roate, 0, 0, 1);
			Entry<Topic, Integer> entry = topics.get(n);
			GlStateManager.translate(0, -42, 0);
			entry.getKey().render(Minecraft.getMinecraft(), topicSize, 1, 0);
			GlStateManager.popMatrix();
		}

		// 画线
		GlStateManager.color(1, 1, 1, 1);
		dR = dR / 180.0f * 3.1415926f;
		GlStateManager.disableTexture2D();
		for (int n = 1; n <= size; n++) {
			Entry<Topic, Integer> entry1 = topics.get(n - 1);
			Entry<Topic, Integer> entry2 = topics.get(n % size);
			float roate = n * dR;
			float r1 = Math.min(1, entry1.getValue() / allNeed);
			float l1 = r1 * 18 + 14;
			float x1 = MathHelper.sin(roate - dR) * l1;
			float y1 = -MathHelper.cos(roate - dR) * l1;
			float r2 = Math.min(1, entry2.getValue() / allNeed);
			float l2 = r2 * 18 + 14;
			float x2 = MathHelper.sin(roate) * l2;
			float y2 = -MathHelper.cos(roate) * l2;
			drawLine(x1, y1, x2, y2, mapR, mapG, mapB, 1);
		}
		GlStateManager.enableTexture2D();

		GlStateManager.popMatrix();
	}

	@SideOnly(Side.CLIENT)
	protected void drawLine(float x1, float y1, float x2, float y2, float r, float g, float b, float alpha) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		GlStateManager.glLineWidth(4);
		bufferbuilder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
		bufferbuilder.pos(x1, y1, 0).color(r, g, b, alpha).endVertex();
		bufferbuilder.pos(x2, y2, 0).color(r, g, b, alpha).endVertex();
		tessellator.draw();
	}

	@Override
	protected int getType() {
		return PageTransform.RESEARCH;
	}

}

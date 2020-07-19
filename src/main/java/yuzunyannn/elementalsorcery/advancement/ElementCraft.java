package yuzunyannn.elementalsorcery.advancement;

import com.google.gson.JsonDeserializationContext;

import net.minecraft.advancements.critereon.AbstractCriterionInstance;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.api.crafting.IRecipe;
import yuzunyannn.elementalsorcery.crafting.RecipeManagement;
import yuzunyannn.elementalsorcery.util.json.JsonObject;

public class ElementCraft extends CriterionTriggerAdapter<ElementCraft.Instance> {

	public ElementCraft(String id) {
		super(new ResourceLocation(ElementalSorcery.MODID, id));
	}

	@Override
	public ElementCraft.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
		String id = "";
		if (json.hasString("recipe")) id = json.getString("recipe");
		return new ElementCraft.Instance(this.getId(), id);
	}

	@Override
	boolean test(EntityPlayerMP player, ElementCraft.Instance criterion, Object... objs) {
		return criterion.test(player, (IRecipe) objs[0]);
	}

	public static class Instance extends AbstractCriterionInstance {
		final IRecipe recipe;

		public Instance(ResourceLocation criterionIn, String id) {
			super(criterionIn);
			this.recipe = RecipeManagement.instance.getValue(new ResourceLocation(id));
		}

		public boolean test(EntityPlayerMP player, IRecipe recipeIn) {
			return recipe == null ? true : recipeIn == recipe;
		}
	}

}

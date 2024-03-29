package yuzunyannn.elementalsorcery.advancement;

import com.google.gson.JsonDeserializationContext;

import net.minecraft.advancements.critereon.AbstractCriterionInstance;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.crafting.IElementRecipe;
import yuzunyannn.elementalsorcery.util.json.JsonObject;

public class ElementCraft extends CriterionTriggerAdapter<ElementCraft.Instance> {

	public ElementCraft(String id) {
		super(new ResourceLocation(ESAPI.MODID, id));
	}

	@Override
	public ElementCraft.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
		String id = "";
		if (json.hasString("recipe")) id = json.getString("recipe");
		return new ElementCraft.Instance(this.getId(), id);
	}

	@Override
	boolean test(EntityPlayerMP player, ElementCraft.Instance criterion, Object... objs) {
		return criterion.test(player, (IElementRecipe) objs[0]);
	}

	public static class Instance extends AbstractCriterionInstance {
		final IElementRecipe recipe;

		public Instance(ResourceLocation criterionIn, String id) {
			super(criterionIn);
			this.recipe = ESAPI.recipeMgr.getValue(new ResourceLocation(id));
		}

		public boolean test(EntityPlayerMP player, IElementRecipe recipeIn) {
			return recipe == null ? true : recipeIn == recipe;
		}
	}

}

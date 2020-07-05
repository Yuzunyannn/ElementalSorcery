package yuzunyannn.elementalsorcery.advancement;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import net.minecraft.advancements.critereon.AbstractCriterionInstance;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import yuzunyannn.elementalsorcery.ElementalSorcery;

public class CriterionTriggerFast extends CriterionTriggerAdapter<CriterionTriggerFast.Instance> {

	public CriterionTriggerFast(String id) {
		super(new ResourceLocation(ElementalSorcery.MODID, id));
	}

	@Override
	public CriterionTriggerFast.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
		return new CriterionTriggerFast.Instance(this.getId());
	}

	@Override
	boolean test(EntityPlayerMP player, CriterionTriggerFast.Instance criterion, Object... objs) {
		return criterion.test(player);
	}

	public static class Instance extends AbstractCriterionInstance {

		public Instance(ResourceLocation criterionIn) {
			super(criterionIn);
		}

		public boolean test(EntityPlayerMP player) {
			return true;
		}
	}

}

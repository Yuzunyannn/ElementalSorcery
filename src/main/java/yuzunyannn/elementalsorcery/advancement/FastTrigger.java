package yuzunyannn.elementalsorcery.advancement;

import com.google.gson.JsonDeserializationContext;

import net.minecraft.advancements.critereon.AbstractCriterionInstance;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.util.json.JsonObject;

public class FastTrigger extends CriterionTriggerAdapter<FastTrigger.Instance> {

	public FastTrigger(String id) {
		super(new ResourceLocation(ElementalSorcery.MODID, id));
	}

	@Override
	public FastTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
		return new FastTrigger.Instance(this.getId());
	}

	@Override
	boolean test(EntityPlayerMP player, FastTrigger.Instance criterion, Object... objs) {
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

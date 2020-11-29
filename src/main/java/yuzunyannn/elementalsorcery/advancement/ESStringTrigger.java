package yuzunyannn.elementalsorcery.advancement;

import java.util.regex.Pattern;

import com.google.gson.JsonDeserializationContext;

import net.minecraft.advancements.critereon.AbstractCriterionInstance;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.util.json.JsonObject;

public class ESStringTrigger extends CriterionTriggerAdapter<ESStringTrigger.Instance> {

	public ESStringTrigger(String id) {
		super(new ResourceLocation(ElementalSorcery.MODID, id));
	}

	@Override
	public ESStringTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
		String pro = json.needString("type");
		return new ESStringTrigger.Instance(this.getId(), pro);
	}

	@Override
	boolean test(EntityPlayerMP player, ESStringTrigger.Instance criterion, Object... objs) {
		return criterion.test(player, objs[0].toString());
	}

	public static class Instance extends AbstractCriterionInstance {
		Pattern test = null;

		public Instance(ResourceLocation criterionIn, String type) {
			super(criterionIn);
			this.test = Pattern.compile(type);
		}

		public boolean test(EntityPlayerMP player, String type) {
			if (this.test != null) return this.test.matcher(type).find();
			return false;
		}
	}

}

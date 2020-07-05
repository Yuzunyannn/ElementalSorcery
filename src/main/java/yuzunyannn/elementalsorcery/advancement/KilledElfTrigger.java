package yuzunyannn.elementalsorcery.advancement;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import net.minecraft.advancements.critereon.AbstractCriterionInstance;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.entity.elf.EntityElfBase;
import yuzunyannn.elementalsorcery.util.JsonHelper;

public class KilledElfTrigger extends CriterionTriggerAdapter<KilledElfTrigger.Instance> {

	public KilledElfTrigger(String id) {
		super(new ResourceLocation(ElementalSorcery.MODID, id));
	}

	@Override
	public KilledElfTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
		String pro = null;
		if (JsonHelper.isString(json, "profession")) pro = json.get("profession").getAsString();
		return new KilledElfTrigger.Instance(this.getId(), pro);
	}

	@Override
	boolean test(EntityPlayerMP player, KilledElfTrigger.Instance criterion, Object... objs) {
		return criterion.test(player, (EntityElfBase) objs[0], (DamageSource) objs[1]);
	}

	public static class Instance extends AbstractCriterionInstance {
		final String pro;

		public Instance(ResourceLocation criterionIn, String pro) {
			super(criterionIn);
			this.pro = pro;
		}

		public boolean test(EntityPlayerMP player, EntityElfBase elf, DamageSource source) {
			if (pro == null) return false;
			return pro.equals(elf.getProfession().getRegistryName().toString());
		}
	}

}

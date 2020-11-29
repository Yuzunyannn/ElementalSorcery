package yuzunyannn.elementalsorcery.advancement;

import com.google.gson.JsonDeserializationContext;

import net.minecraft.advancements.critereon.AbstractCriterionInstance;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.util.json.JsonObject;

public class ESItemStackTrigger extends CriterionTriggerAdapter<ESItemStackTrigger.Instance> {

	public ESItemStackTrigger(String id) {
		super(new ResourceLocation(ElementalSorcery.MODID, id));
	}

	@Override
	public ESItemStackTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
		String type = json.needString("type");
		ItemStack stack = json.needItem("item").getStack();
		return new ESItemStackTrigger.Instance(this.getId(), type, stack);
	}

	@Override
	boolean test(EntityPlayerMP player, ESItemStackTrigger.Instance criterion, Object... objs) {
		return criterion.test(player, objs[0].toString(), (ItemStack) objs[1]);
	}

	public static class Instance extends AbstractCriterionInstance {
		final String type;
		final ItemStack stack;

		public Instance(ResourceLocation criterionIn, String type, ItemStack stack) {
			super(criterionIn);
			this.type = type;
			this.stack = stack;
		}

		public boolean test(EntityPlayerMP player, String type, ItemStack stack) {
			if (this.type == null) return false;
			return this.type.equals(type) && ItemStack.areItemsEqual(this.stack, stack);
		}
	}

}

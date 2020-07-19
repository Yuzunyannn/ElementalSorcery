package yuzunyannn.elementalsorcery.advancement;

import com.google.gson.JsonDeserializationContext;

import net.minecraft.advancements.critereon.AbstractCriterionInstance;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.util.json.JsonObject;

public class InfusionTake extends CriterionTriggerAdapter<InfusionTake.Instance> {

	public InfusionTake(String id) {
		super(new ResourceLocation(ElementalSorcery.MODID, id));
	}

	@Override
	public InfusionTake.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
		ItemStack stack = json.needItem("item").getStack();
		return new InfusionTake.Instance(this.getId(), stack);
	}

	@Override
	boolean test(EntityPlayerMP player, InfusionTake.Instance criterion, Object... objs) {
		return criterion.test(player, (ItemStack) objs[0]);
	}

	public static class Instance extends AbstractCriterionInstance {
		final ItemStack stack;

		public Instance(ResourceLocation criterionIn, ItemStack stack) {
			super(criterionIn);
			this.stack = stack;
		}

		public boolean test(EntityPlayerMP player, ItemStack stackIn) {
			if (stack.isEmpty()) return false;
			return ItemStack.areItemsEqual(stack, stackIn);
		}
	}

}

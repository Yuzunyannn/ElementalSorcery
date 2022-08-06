package yuzunyannn.elementalsorcery.advancement;

import com.google.gson.JsonDeserializationContext;

import net.minecraft.advancements.critereon.AbstractCriterionInstance;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;
import yuzunyannn.elementalsorcery.util.json.JsonObject;

public class MagicDeskCraft extends CriterionTriggerAdapter<MagicDeskCraft.Instance> {

	public MagicDeskCraft(String id) {
		super(new ResourceLocation(ESAPI.MODID, id));
	}

	@Override
	public MagicDeskCraft.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
		return new MagicDeskCraft.Instance(this.getId(), json.needItem("item").getStack());
	}

	@Override
	boolean test(EntityPlayerMP player, MagicDeskCraft.Instance criterion, Object... objs) {
		return criterion.test(player, (ItemStack) objs[0]);
	}

	public static class Instance extends AbstractCriterionInstance {
		final ItemStack output;

		public Instance(ResourceLocation criterionIn, ItemStack output) {
			super(criterionIn);
			this.output = output;
		}

		public boolean test(EntityPlayerMP player, ItemStack output) {
			return ItemHelper.areItemsEqual(this.output, output);
		}
	}

}

package yuzunyannn.elementalsorcery.world;

import java.util.Random;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import yuzunyannn.elementalsorcery.api.mantra.Mantra;
import yuzunyannn.elementalsorcery.block.BlockSealStone;
import yuzunyannn.elementalsorcery.elf.research.AncientPaper;

public class LootFunctionRandomMantra extends LootFunction {

	protected LootFunctionRandomMantra(LootCondition[] conditionsIn) {
		super(conditionsIn);
	}

	@Override
	public ItemStack apply(ItemStack stack, Random rand, LootContext context) {
		Mantra mantra = BlockSealStone.randomMantra(rand, 3, true);
		AncientPaper ap = BlockSealStone.randomAncientPaper(rand, 3, true);
		return ap.setProgress(1).setMantra(mantra).saveState(stack);
	}

	public static class Serializer extends LootFunction.Serializer<LootFunctionRandomMantra> {
		public Serializer() {
			super(new ResourceLocation("mantra_randomly"), LootFunctionRandomMantra.class);
		}

		public void serialize(JsonObject object, LootFunctionRandomMantra functionClazz,
				JsonSerializationContext serializationContext) {
		}

		public LootFunctionRandomMantra deserialize(JsonObject object,
				JsonDeserializationContext deserializationContext, LootCondition[] conditionsIn) {
			return new LootFunctionRandomMantra(conditionsIn);
		}
	}

}

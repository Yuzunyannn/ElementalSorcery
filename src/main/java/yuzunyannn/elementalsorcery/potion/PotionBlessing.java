package yuzunyannn.elementalsorcery.potion;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.IMob;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeedFood;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public class PotionBlessing extends PotionCommon {

	public PotionBlessing() {
		super(false, 0x4186df, "blessing");
		iconIndex = 44;
		this.setBeneficial();
		registerPotionAttributeModifier(SharedMonsterAttributes.LUCK, "49f3620f-2fa4-4dbe-a5b3-a2eae4f9cbb3", 1, 0);
	}

	@Override
	public List<ItemStack> getCurativeItems() {
		return new ArrayList<>();
	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		return duration % 200 == 0;
	}

	@Override
	public void performEffect(EntityLivingBase entity, int amplifier) {
		World world = entity.world;
		if (world.isRemote) return;

		float hp = entity.getHealth();
		float maxHp = entity.getMaxHealth();

		if (hp / maxHp <= 0.333f) {

			boolean isUndead = false;
			if (entity instanceof IMob) isUndead = entity.isEntityUndead();
			if (isUndead) entity.heal((amplifier + 1) * 2);
			else {
				int level = Math.min(amplifier, 2);
				entity.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 100, level));
			}
		}

		if (amplifier >= 1) {
			int level = Math.min(amplifier - 1, 2);
			if (hp / maxHp <= 0.5f) entity.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 160, level));
			else entity.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 100, level));
		}

		if (amplifier >= 2 && entity.isBurning()) {
			entity.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 180));
		}

		if (amplifier >= 3) {
			ItemStack stack = entity.getHeldItemMainhand();
			if (stack.getItem().isRepairable() && stack.getItem().isDamageable()) {
				stack.setItemDamage(Math.max(0, stack.getItemDamage() - entity.getRNG().nextInt(5)));
			}
		}
	}

	public static void addOres(int amplifier, float chance, List<ItemStack> drops, Random rand) {
		for (ItemStack stack : drops) {
			if (!isItemCanDouble(stack)) continue;
			for (int i = 0; i < amplifier + 1; i++) {
				if (rand.nextDouble() > chance) break;
				chance = chance * 0.9f;
				int count = (int) Math.max(1, stack.getCount() * 0.1f);
				stack.setCount(stack.getCount() + count);
			}
		}
	}

	public static boolean isItemCanDouble(ItemStack stack) {
		int[] ids = OreDictionary.getOreIDs(stack);
		for (int id : ids) {
			String name = OreDictionary.getOreName(id);
			switch (name) {
			case "gemLapis":
			case "gemDiamond":
			case "gemQuartz":
			case "gemEmerald":
				return true;
			}
		}
		Item item = stack.getItem();
		Block block = Block.getBlockFromItem(item);
		if (block instanceof BlockFlower) return true;
		if (item == Items.COAL) return true;
		if (item instanceof ItemSeedFood) return true;
		if (item instanceof ItemSeeds) return true;
		if (item == Items.WHEAT) return true;
		return false;
	}

}

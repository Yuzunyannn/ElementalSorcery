package yuzunyannn.elementalsorcery.enchant;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.DamageSource;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.item.prop.ItemBlessingJadePiece;

public class EnchantmentGatherSouls extends EnchantmentES {

	public EnchantmentGatherSouls() {
		super(Rarity.UNCOMMON, EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[] { EntityEquipmentSlot.MAINHAND });
		this.setName("gatherSouls");
	}

	@Override
	public int getMinEnchantability(int enchantmentLevel) {
		return 2 + (enchantmentLevel - 1) * 10;
	}

	@Override
	public int getMaxEnchantability(int enchantmentLevel) {
		return this.getMinEnchantability(enchantmentLevel) + 20;
	}

	@Override
	public int getMaxLevel() {
		return 5;
	}

	@Override
	public float calcDamageByCreature(int level, EnumCreatureAttribute creatureType) {
		return level * 0.15f;
	}

	@Override
	public void onLivingDead(EntityLivingBase living, DamageSource source, int level) {
		if (living.world.isRemote) return;
		int size = 1 + living.world.rand.nextInt(level);
		living.dropItem(ESInit.ITEMS.SOUL_FRAGMENT, Math.min(8, size));
		if (living.getRNG().nextFloat() <= (0.004f * level))
			living.entityDropItem(ItemBlessingJadePiece.createPiece(1), living.getEyeHeight());
	}
}

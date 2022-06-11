package yuzunyannn.elementalsorcery.enchant;

import java.util.UUID;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.DamageSource;
import net.minecraft.world.DimensionType;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.item.prop.ItemBlessingJadePiece;
import yuzunyannn.elementalsorcery.util.helper.EntityHelper;

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
		// 虚空碎片
		if (living.world.provider.getDimensionType() == DimensionType.THE_END) {
			if (EntityHelper.isEnder(living)) {
				UUID uuid = living.getUniqueID();
				long n = uuid.getLeastSignificantBits() * (uuid.getMostSignificantBits() + 37);
				double a = Math.abs(n % 100000) / 100000.0;
				if (a < 0.15 + 0.02 * level) {
					living.dropItem(ESInit.ITEMS.VOID_FRAGMENT, 1);
					return;
				}
			}
		}
		// 灵魂
		int size = 1 + living.world.rand.nextInt(level);
		living.dropItem(ESInit.ITEMS.SOUL_FRAGMENT, Math.min(8, size));
		if (living.getRNG().nextFloat() <= (0.004f * level))
			living.entityDropItem(ItemBlessingJadePiece.createPiece(1), living.getEyeHeight());
	}
}

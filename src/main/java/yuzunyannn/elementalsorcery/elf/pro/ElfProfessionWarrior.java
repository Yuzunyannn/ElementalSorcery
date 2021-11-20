package yuzunyannn.elementalsorcery.elf.pro;

import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.entity.elf.EntityElfBase;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.render.entity.living.RenderEntityElf;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

public class ElfProfessionWarrior extends ElfProfession {

	@Override
	public void initElf(EntityElfBase elf, ElfProfession origin) {
		if (origin == ElfProfession.MERCHANT) {
			ItemStack sword = new ItemStack(ESInit.ITEMS.KYANITE_SWORD);
			ItemHelper.addEnchantment(sword, Enchantments.LOOTING, 1);
			ItemHelper.addEnchantment(sword, Enchantments.POWER, 3);
			elf.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, sword);
			elf.setDropChance(EntityEquipmentSlot.MAINHAND, 0.005f);
		} else {
			elf.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(ESInit.ITEMS.KYANITE_SWORD));
			elf.setDropChance(EntityEquipmentSlot.MAINHAND, 0.02f);
		}
		elf.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, new ItemStack(Items.SHIELD));
		elf.setDropChance(EntityEquipmentSlot.MAINHAND, 0.02f);
	}

	@Override
	public Float attackedFrom(EntityElfBase elf, DamageSource source, float amount) {
		if (elf.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND).getItem() instanceof ItemShield) {
			if (!source.isUnblockable() && !source.isFireDamage()) {
				if (elf.getRNG().nextInt(5) == 0) {
					elf.playSound(SoundEvents.ITEM_SHIELD_BLOCK, 1.0F, 0.8F + elf.world.rand.nextFloat() * 0.4F);
					return null;
				}
			}
		}
		return super.attackedFrom(elf, source, amount);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ResourceLocation getTexture(EntityElfBase elf) {
		return RenderEntityElf.TEXTURE_WARRIOR;
	}
}

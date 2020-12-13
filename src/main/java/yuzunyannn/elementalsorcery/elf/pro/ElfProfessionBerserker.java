package yuzunyannn.elementalsorcery.elf.pro;

import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.entity.elf.EntityElfBase;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.render.entity.RenderEntityElf;

public class ElfProfessionBerserker extends ElfProfession {

	@Override
	public void initElf(EntityElfBase elf, ElfProfession origin) {
		elf.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(ESInit.ITEMS.KYANITE_SWORD));
		elf.setDropChance(EntityEquipmentSlot.MAINHAND, 0.02f);
		elf.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, new ItemStack(ESInit.ITEMS.KYANITE_SWORD));
		elf.setDropChance(EntityEquipmentSlot.MAINHAND, 0.02f);
	}

	@Override
	public Float attackedFrom(EntityElfBase elf, DamageSource source, float amount) {
		elf.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 20 * 10, 2));
		elf.addPotionEffect(new PotionEffect(MobEffects.SPEED, 20 * 10, 3));
		elf.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 20 * 10, 2));
		elf.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 20 * 10));
		return super.attackedFrom(elf, source, amount);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ResourceLocation getTexture(EntityElfBase elf) {
		return RenderEntityElf.TEXTURE_BERSERKER;
	}
}

package yuzunyannn.elementalsorcery.elf.pro;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.entity.elf.EntityElfBase;
import yuzunyannn.elementalsorcery.render.entity.living.RenderEntityElf;

public class ElfProfessionPrototype extends ElfProfession {

	@Override
	public void initElf(EntityElfBase elf, ElfProfession origin) {
		elf.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);
		elf.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, ItemStack.EMPTY);
	}

	@Override
	public void dropFewItems(EntityElfBase elf, boolean wasRecentlyHit, int lootingModifier) {
	}

	@Override
	public boolean needPickup(EntityElfBase elf, ItemStack stack) {
		return false;
	}

	@Override
	public Float attackedFrom(EntityElfBase elf, DamageSource source, float amount) {
		return amount;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ResourceLocation getTexture(EntityElfBase elf) {
		return RenderEntityElf.TEXTURE_PROTOTYPE;
	}

	@Override
	public boolean canDespawn(EntityElfBase elf) {
		return false;
	}
}

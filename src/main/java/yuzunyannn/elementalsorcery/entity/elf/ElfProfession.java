package yuzunyannn.elementalsorcery.entity.elf;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.init.ESInitInstance;

public class ElfProfession extends net.minecraftforge.registries.IForgeRegistryEntry.Impl<ElfProfession> {

	static public final ElfProfession NONE = new ElfProfessionNone();
	static public final ElfProfession MASTER = new ElfProfessionMaster();
	static public final ElfProfession WARRIOR = new ElfProfessionWarrior();
	static public final ElfProfession BERSERKER = new ElfProfessionBerserker();

	public void initElf(EntityElfBase elf) {

	}

	/** 是否可以自动装备上装备 */
	public boolean canEquip(EntityElfBase elf, ItemStack stack, EntityEquipmentSlot slot) {
		return true;
	}

	/** 是否要捡起来 */
	public boolean needPickup(EntityElfBase elf, ItemStack stack) {
		return stack.getItem() == Item.getItemFromBlock(ESInitInstance.BLOCKS.ELF_FRUIT);
	}

	/**
	 * 获取攻击距离
	 * 
	 * @return 返回-1表示不能攻击，返回0表示默认
	 */
	public float getAttackDistance() {
		return 0;
	}

	/** 对敌人进行攻击 */
	public boolean attackEntity(EntityElfBase elf, Entity target) {
		return elf.attackEntityAsMobDefault(target);
	}

	/**
	 * 受到攻击
	 * 
	 * @return 返回0表示默认处理，返回-1表示拒绝，返回1表示接受
	 */
	public int attackedFrom(EntityElfBase elf, DamageSource source, float amount) {
		return 0;
	}

	public void tick(EntityElfBase elf) {

	}

	@SideOnly(Side.CLIENT)
	public void render(EntityElfBase elf, double x, double y, double z, float entityYaw, float partialTicks) {

	}

}

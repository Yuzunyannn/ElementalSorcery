package yuzunyannn.elementalsorcery.elf.pro;

import javax.annotation.Nullable;

import net.minecraft.advancements.Advancement;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.container.ESGuiHandler;
import yuzunyannn.elementalsorcery.elf.talk.TalkChapter;
import yuzunyannn.elementalsorcery.entity.elf.EntityElfBase;
import yuzunyannn.elementalsorcery.init.ESInitInstance;

public class ElfProfession extends net.minecraftforge.registries.IForgeRegistryEntry.Impl<ElfProfession> {

	static public final ElfProfession NONE = new ElfProfessionNone();
	static public final ElfProfession MASTER = new ElfProfessionMaster();
	static public final ElfProfession WARRIOR = new ElfProfessionWarrior();
	static public final ElfProfession BERSERKER = new ElfProfessionBerserker();
	static public final ElfProfession SCHOLAR = new ElfProfessionScholar();

	public String getUnlocalizedProfessionName() {
		return "pro." + this.getRegistryName().getResourcePath();
	}

	/**
	 * 职业相关内容的初始化
	 * 
	 * @param origin 上一个职业
	 */
	public void initElf(EntityElfBase elf, ElfProfession origin) {

	}

	/** 是否可以自动装备上装备，不可以自动装备替换的话，会询问是否可以捡起来 */
	public boolean canEquip(EntityElfBase elf, ItemStack stack, EntityEquipmentSlot slot) {
		return true;
	}

	/**
	 * 是否要捡起来
	 * 
	 * @return true表示要建起来，false表示不要建起来
	 */
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

	/**
	 * 交互
	 * 
	 * @return ture表示启动交互，false表示没有交互
	 */
	public boolean interact(EntityElfBase elf, EntityPlayer player) {
		return false;
	}

	/** 获取精灵要说的内容，仅在打开talkgui时有效 */
	@Nullable
	public TalkChapter getChapter(EntityElfBase elf, EntityPlayer player) {
		return null;
	}

	/** 精灵职业tick */
	public void tick(EntityElfBase elf) {

	}

	/** 客户端渲染 */
	@SideOnly(Side.CLIENT)
	public void render(EntityElfBase elf, double x, double y, double z, float entityYaw, float partialTicks) {
		
	}

	protected void openTalkGui(EntityPlayer player, EntityElfBase elf) {
		if (player.world.isRemote) return;
		NBTTagCompound nbt = ElementalSorcery.getPlayerData(player);
		nbt.setInteger("elfId", elf.getEntityId());
		player.openGui(ElementalSorcery.instance, ESGuiHandler.GUI_ELF_TALK, player.world, 0, 0, 0);
	}

}

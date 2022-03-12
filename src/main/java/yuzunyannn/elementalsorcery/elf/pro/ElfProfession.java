package yuzunyannn.elementalsorcery.elf.pro;

import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistryEntry;
import yuzunyannn.elementalsorcery.ESData;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.container.ESGuiHandler;
import yuzunyannn.elementalsorcery.elf.talk.TalkChapter;
import yuzunyannn.elementalsorcery.elf.trade.Trade;
import yuzunyannn.elementalsorcery.entity.elf.EntityElf;
import yuzunyannn.elementalsorcery.entity.elf.EntityElfBase;
import yuzunyannn.elementalsorcery.init.ESImplRegister;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.render.entity.living.RenderEntityElf;

public class ElfProfession extends IForgeRegistryEntry.Impl<ElfProfession> {

	public static final ESImplRegister<ElfProfession> REGISTRY = new ESImplRegister(ElfProfession.class);

	static public final ElfProfession NONE = new ElfProfessionNone();
	static public final ElfProfession MASTER = new ElfProfessionMaster();
	static public final ElfProfession WARRIOR = new ElfProfessionWarrior();
	static public final ElfProfession BERSERKER = new ElfProfessionBerserker();
	static public final ElfProfession SCHOLAR = new ElfProfessionScholar();
	static public final ElfProfession CRAZY = new ElfProfessionCrazy();
	static public final ElfProfession MERCHANT = new ElfProfessionMerchant();
	static public final ElfProfession BUILDER = new ElfProfessionBuilder();
	static public final ElfProfession RECEPTIONIST = new ElfProfessionReceptionist();
	static public final ElfProfession IRONSMITH = new ElfProfessionIronSmith();
	static public final ElfProfession POST_RECEPTIONIST = new ElfProfessionPostReceptionist();
	static public final ElfProfession POSTMAN = new ElfProfessionPostman();
	static public final ElfProfession RESEARCHER = new ElfProfessionResearcher();
	static public final ElfProfession SCHOLAR_ADV = new ElfProfessionScholarAdv();

	protected String unlocalizedName;

	public ElfProfession setTranslationKey(String unlocalizedName) {
		this.unlocalizedName = unlocalizedName;
		return this;
	}

	public String getTranslationKey() {
		return "pro." + unlocalizedName + ".name";
	}

	/**
	 * 职业相关内容的初始化
	 * 
	 * @param origin 上一个职业
	 */
	public void initElf(EntityElfBase elf, ElfProfession origin) {

	}

	/**
	 * 直接被切换
	 * 
	 * @param next 要转到的职业
	 */
	public void transferElf(EntityElfBase elf, ElfProfession next) {

	}

	/** 当死亡 */
	public void onDead(EntityElfBase elf) {

	}

	/** 是否会自动删除 */
	public boolean canDespawn(EntityElfBase elf) {
		return false;
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
		return stack.getItem() == Item.getItemFromBlock(ESInit.BLOCKS.ELF_FRUIT)
				|| stack.getItem() == ESInit.ITEMS.ELF_COIN;
	}

	/** 获取攻击的目标 */
	public List<EntityLivingBase> getAttackTarget(EntityElfBase elf) {
		return null;
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
	 * @return 返回null表示拒绝，整数表示伤害量，返回-1表示接受，但是不进行默认处理，返回>=0表示伤害
	 */
	public Float attackedFrom(EntityElfBase elf, DamageSource source, float amount) {
		Random rand = elf.getRNG();
		// 群体效应,攻击一个精灵，周围所有精灵生气
		if (rand.nextInt(4) == 0 && source.getTrueSource() instanceof EntityPlayer) {
			final int size = 8;
			AxisAlignedBB aabb = new AxisAlignedBB(elf.posX - size, elf.posY - size, elf.posZ - size, elf.posX + size,
					elf.posY + size, elf.posZ + size);
			List<EntityElf> list = elf.world.getEntitiesWithinAABB(EntityElf.class, aabb);
			for (EntityElf e : list)
				if (e.getRevengeTarget() == null) e.setRevengeTarget((EntityPlayer) source.getTrueSource());
		}
		return amount;
	}

	/**
	 * 交互
	 * 
	 * @return ture表示启动交互，false表示没有交互
	 */
	public boolean interact(EntityElfBase elf, EntityPlayer player) {
		return false;
	}

	/**
	 * 获取精灵要说的内容，仅在打开talkgui时有效
	 * 
	 * @param shiftData 从其他类型的地方，切换过来的数据，可能为null
	 */
	@Nullable
	public TalkChapter getChapter(EntityElfBase elf, EntityPlayer player, @Nullable NBTTagCompound shiftData) {
		return null;
	}

	/** 获得精灵交易的内容，仅在而elftrade时有效 */
	@Nullable
	public Trade getTrade(EntityElfBase elf, EntityPlayer player, @Nullable NBTTagCompound shiftData) {
		return null;
	}

	/** 精灵职业tick */
	public void tick(EntityElfBase elf) {

	}

	/** 客户端自定义渲染 */
	@SideOnly(Side.CLIENT)
	public void render(EntityElfBase elf, double x, double y, double z, float entityYaw, float partialTicks) {

	}

	/** 客户端渲染的材质 */
	@SideOnly(Side.CLIENT)
	@Nonnull
	public ResourceLocation getTexture(EntityElfBase elf) {
		return RenderEntityElf.TEXTURE;
	}

	/** 客户端渲染的模型 */
	@SideOnly(Side.CLIENT)
	@Nonnull
	public ModelBase getModel(EntityElfBase elf) {
		return RenderEntityElf.MODEL;
	}

	protected void openTalkGui(EntityPlayer player, EntityElfBase elf) {
		if (player.world.isRemote) return;
		NBTTagCompound nbt = ESData.getRuntimeData(player);
		nbt.setInteger("elfId", elf.getEntityId());
		nbt.removeTag("shiftData");
		player.openGui(ElementalSorcery.instance, ESGuiHandler.GUI_ELF_TALK, player.world, 0, 0, 0);
		elf.getNavigator().clearPath();
	}

}

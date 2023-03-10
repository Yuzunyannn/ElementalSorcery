package yuzunyannn.elementalsorcery.elf.pro;

import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistryEntry;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.util.ESImplRegister;
import yuzunyannn.elementalsorcery.api.util.var.VariableSet;
import yuzunyannn.elementalsorcery.api.util.var.VariableSet.Variable;
import yuzunyannn.elementalsorcery.block.BlockElfFruit;
import yuzunyannn.elementalsorcery.capability.Adventurer;
import yuzunyannn.elementalsorcery.elf.ElfConfig;
import yuzunyannn.elementalsorcery.elf.pro.merchant.ElfMerchantType;
import yuzunyannn.elementalsorcery.elf.quest.IAdventurer;
import yuzunyannn.elementalsorcery.elf.talk.TalkActionEnd;
import yuzunyannn.elementalsorcery.elf.talk.TalkActionGoTo;
import yuzunyannn.elementalsorcery.elf.talk.TalkChapter;
import yuzunyannn.elementalsorcery.elf.talk.TalkSceneSay;
import yuzunyannn.elementalsorcery.elf.talk.TalkSceneSelect;
import yuzunyannn.elementalsorcery.elf.talk.Talker;
import yuzunyannn.elementalsorcery.elf.trade.Trade;
import yuzunyannn.elementalsorcery.elf.trade.TradeList;
import yuzunyannn.elementalsorcery.entity.EntityItemGoods;
import yuzunyannn.elementalsorcery.entity.elf.EntityElf;
import yuzunyannn.elementalsorcery.entity.elf.EntityElfBase;
import yuzunyannn.elementalsorcery.render.entity.living.RenderEntityElf;
import yuzunyannn.elementalsorcery.util.var.VariableTypes;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class ElfProfession extends IForgeRegistryEntry.Impl<ElfProfession> {

	static public Random getRandomFromName(String str) {
		return new Random(str.hashCode());
	}

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
	static public final ElfProfession DEBT_COLLECTOR = new ElfProfessionDebtCollector();

	public static final Variable<Integer> REMAIN_TICK = new Variable("remainTick", VariableSet.INT);
	public static final Variable<ElfMerchantType> M_TYPE = new Variable("mType", VariableTypes.ELF_MERCHANT_TYPE);
	public static final Variable<NBTTagCompound> MEET = new Variable("meet", VariableSet.NBT_TAG);

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
		if (elf.world.isRemote) return;
		VariableSet storage = elf.getProfessionStorage();
		if (!storage.has(M_TYPE)) return;
		ElfMerchantType merchantType = storage.get(M_TYPE);
		Trade trade = merchantType.getTrade(storage);
		if (trade == null) return;
		int size = trade.getTradeListSize();
		for (int i = 0; i < size; i++) {
			TradeList.TradeInfo info = trade.getTradeInfo(i);
			if (info.isReclaim()) continue;
			if (trade.stock(i) > 0) {
				ItemStack stack = info.getCommodity();
				EntityItemGoods.dropGoods(elf, stack, info.getCost(), false)
						.setLife(20 * (30 + elf.getRNG().nextInt(30)));
			}
		}
	}

	public void dropFewItems(EntityElfBase elf, boolean wasRecentlyHit, int lootingModifier) {
		Random rand = elf.getRNG();
		if (rand.nextInt(100) < 50) return;
		int i = rand.nextInt(3);
		if (lootingModifier > 0) i += rand.nextInt(lootingModifier + 1);
		for (int j = 0; j < i; ++j) {
			if (rand.nextInt(3) == 0)
				elf.entityDropItem(new ItemStack(ESObjects.ITEMS.ELF_COIN, rand.nextInt(8) + 2), 0);
			else elf.entityDropItem(new ItemStack(ESObjects.BLOCKS.ELF_FRUIT, 1, BlockElfFruit.MAX_STATE), 0);
		}
	}

	/** 当被杀，onDead后调用，TrueSource一定是EntityLivingBase */
	public void onBeKilled(EntityElfBase elf, DamageSource ds) {
		EntityLivingBase player = (EntityLivingBase) ds.getTrueSource();
		World world = elf.world;
		IAdventurer adventurer = player.getCapability(Adventurer.ADVENTURER_CAPABILITY, null);
		if (adventurer == null) return;
		final int size = 16;
		AxisAlignedBB aabb = WorldHelper.createAABB(player.getPosition(), size, size, size);
		List<EntityElf> list = world.getEntitiesWithinAABB(EntityElf.class, aabb, (e) -> {
			if (e.isDeading()) return false;
			if (!e.canEntityBeSeen(player)) return false;
			Vec3d look = e.getLookVec();
			Vec3d vec = player.getPositionEyes(0).subtract(e.getPositionEyes(0));
			double cos = look.dotProduct(vec) / (look.length() * vec.length());
			return cos > 0;
		});
		if (list.size() > 0) {
			float point = 0.2f * list.size();
			ElfConfig.changeFame(player, -point);
			player.sendMessage(new TextComponentTranslation("info.fame.decline", String.valueOf(list.size()),
					String.format("%.1f", point)).setStyle(new Style().setColor(TextFormatting.DARK_RED)));
		}
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
		return stack.getItem() == Item.getItemFromBlock(ESObjects.BLOCKS.ELF_FRUIT)
				|| stack.getItem() == ESObjects.ITEMS.ELF_COIN;
	}

	public void onPickupItem(EntityElfBase elf, EntityItem itemEntity) {

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
		ItemStack stack = player.getHeldItemMainhand();
		if (stack.getItem() == ESObjects.ITEMS.ELF_DIAMOND) {
			elf.openTalkGui(player);
			return true;
		}
		return false;
	}

	/**
	 * 获取精灵要说的内容，仅在打开talkgui时有效
	 * 
	 * @param shiftData 从其他类型的地方，切换过来的数据，可能为null
	 */
	@Nullable
	public TalkChapter getChapter(EntityElfBase elf, EntityPlayer player, @Nullable NBTTagCompound shiftData) {
		if (shiftData != null) return null;
		if (ElfConfig.isPublicEnemy(player)) {
			TalkChapter chapter = new TalkChapter();
			return chapter;
		}
		ItemStack stack = player.getHeldItemMainhand();
		if (stack.getItem() == ESObjects.ITEMS.ELF_DIAMOND) {
			TalkChapter chapter = new TalkChapter();
			chapter.addScene(new TalkSceneSay("say.elf.is.get.present", Talker.OPPOSING));
			TalkSceneSelect select = new TalkSceneSelect();
			select.addString("say.ok", (p, e, c, i, s, t) -> {
				elf.givePresent(player, stack.splitStack(1));
				TalkActionGoTo.goTo("thank", chapter, s, i);
				return true;
			});
			select.addString("say.no", new TalkActionEnd());
			chapter.addScene(select);
			chapter.addScene(new TalkSceneSay("say.very.thank", Talker.OPPOSING).setLabel("thank"));
			return chapter;
		}
		return null;
	}

	/** 获得精灵交易的内容，仅在而elftrade时有效 */
	@Nullable
	public Trade getTrade(EntityElfBase elf, EntityPlayer player, @Nullable NBTTagCompound shiftData) {
		VariableSet storage = elf.getProfessionStorage();
		if (!storage.has(M_TYPE)) return null;
		ElfMerchantType merchantType = storage.get(M_TYPE);
		return merchantType.getTrade(storage);
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

}

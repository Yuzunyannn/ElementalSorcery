package yuzunyannn.elementalsorcery.elf.pro;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.advancement.ESCriteriaTriggers;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.util.var.VariableSet;
import yuzunyannn.elementalsorcery.block.BlockElfFruit;
import yuzunyannn.elementalsorcery.container.ESGuiHandler;
import yuzunyannn.elementalsorcery.elf.ElfConfig;
import yuzunyannn.elementalsorcery.elf.pro.merchant.ElfMerchantType;
import yuzunyannn.elementalsorcery.elf.talk.TalkActionToGui;
import yuzunyannn.elementalsorcery.elf.talk.TalkChapter;
import yuzunyannn.elementalsorcery.elf.talk.TalkSceneSay;
import yuzunyannn.elementalsorcery.elf.talk.Talker;
import yuzunyannn.elementalsorcery.elf.trade.Trade;
import yuzunyannn.elementalsorcery.elf.trade.TradeList;
import yuzunyannn.elementalsorcery.entity.elf.EntityElfBase;
import yuzunyannn.elementalsorcery.entity.elf.EntityElfTravelling;
import yuzunyannn.elementalsorcery.item.ItemMerchantInvitation;
import yuzunyannn.elementalsorcery.render.entity.living.RenderEntityElf;
import yuzunyannn.elementalsorcery.util.helper.EntityHelper;
import yuzunyannn.elementalsorcery.util.helper.RandomHelper;
import yuzunyannn.elementalsorcery.util.helper.RandomHelper.WeightRandom;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

public class ElfProfessionMerchant extends ElfProfessionUndetermined {

	public static final float MERCHANT_GIFT_CHANCE_WHEN_VH = 0.25f;
	public static final int MERCHANT_GIFT_MAX_VALUE = 2500;

	@Override
	public void initElf(EntityElfBase elf, ElfProfession origin) {
		if (elf.world.isRemote) return;
		elf.setDropChance(EntityEquipmentSlot.MAINHAND, 0);
		elf.setDropChance(EntityEquipmentSlot.OFFHAND, 0);

		VariableSet storage = elf.getProfessionStorage();
		ElfMerchantType merchantType = storage.get(M_TYPE);
		if (!merchantType.hasTrade(storage))
			merchantType.renewTrade(elf.world, elf.getPosition(), elf.getRNG(), storage);
		elf.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, merchantType.getHoldItem(elf.world, storage));
		elf.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, new ItemStack(ESObjects.ITEMS.ELF_COIN));
	}

	public static void setRemainTimeBeforeLeave(EntityElfBase elf, int tick) {
		VariableSet storage = elf.getProfessionStorage();
		storage.set(REMAIN_TICK, tick);
	}

	@Override
	public Float attackedFrom(EntityElfBase elf, DamageSource source, float amount) {
		if (elf instanceof EntityElfTravelling) return super.attackedFrom(elf, source, amount);
		return amount;
	}

	@Override
	public boolean needPickup(EntityElfBase elf, ItemStack stack) {
		return super.needPickup(elf, stack) || stack.getItem() == Items.STRING;
	}

	@Override
	public boolean canEquip(EntityElfBase elf, ItemStack stack, EntityEquipmentSlot slot) {
		return slot != EntityEquipmentSlot.MAINHAND;
	}

	@Override
	public boolean interact(EntityElfBase elf, EntityPlayer player) {
		elf.openTalkGui(player);
		return true;
	}

	@Override
	public void tick(EntityElfBase elf) {
		if (elf.tick % 100 != 0) return;
		if (elf.getTalker() != null) return;
		if (elf.world.isRemote) return;

		if (elf.getTalker() != null) return;

		// 吃药
		ItemStack potion = elf.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND);
		if (potion.getItem() == Items.POTIONITEM) {
			elf.playSound(SoundEvents.ENTITY_GENERIC_DRINK, 0.5F, elf.world.rand.nextFloat() * 0.1F + 0.9F);
			for (PotionEffect effect : PotionUtils.getEffectsFromStack(potion)) elf.addPotionEffect(effect);
			elf.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, ItemStack.EMPTY);
		} else if (elf.getHealth() < 10) {
			if (!potion.isEmpty()) elf.entityDropItem(potion, elf.height / 2);
			potion = new ItemStack(Items.POTIONITEM);
			List<PotionEffect> effects = new ArrayList<>();
			effects.add(new PotionEffect(MobEffects.REGENERATION, 20 * 8, 2));
			PotionUtils.appendEffects(potion, effects);
			elf.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, potion);
		}

		// 判断是否要离开
		ItemStack stack = elf.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
		if (stack.getItem() == ESObjects.ITEMS.SPELLBOOK) {
			elf.leave();
			return;
		}

		VariableSet storage = elf.getProfessionStorage();
		ElfMerchantType merchantType = storage.get(M_TYPE);

		// 如果有剩余时间，检查剩余时间
		if (storage.has(REMAIN_TICK)) {
			int remainTick = storage.get(REMAIN_TICK) - 100;// 因为100tick进入一次，不精准，但足够
			if (remainTick <= 0) {
				this.setLeave(elf);
				storage.remove(REMAIN_TICK);
				return;
			}
			storage.set(REMAIN_TICK, remainTick);
		}

		// 检查是否所有东西全卖出去了
		Trade trade = merchantType.getTrade(storage);
		if (trade != null) {
			int size = trade.getTradeListSize();
			for (int i = 0; i < size; i++) {
				TradeList.TradeInfo info = trade.getTradeInfo(i);
				if (info.isReclaim()) continue;
				if (trade.stock(i) > 0) return;
			}
		}
		// 是的情况下，离开
		this.setLeave(elf);
	}

	protected void setLeave(EntityElfBase elf) {
		ItemStack stack = elf.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
		if (stack.getItem() == ESObjects.ITEMS.SPELLBOOK) return;

		elf.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(ESObjects.ITEMS.SPELLBOOK));
		elf.setDropChance(EntityEquipmentSlot.MAINHAND, 0);
		float size = 6;
		AxisAlignedBB aabb = new AxisAlignedBB(elf.posX - size, elf.posY - size, elf.posZ - size, elf.posX + size,
				elf.posY + size, elf.posZ + size);
		List<EntityPlayer> list = elf.world.getEntitiesWithinAABB(EntityPlayer.class, aabb);
		for (EntityPlayer player : list) {
			ITextComponent text = new TextComponentString("[")
					.appendSibling(new TextComponentTranslation("pro.merchant"))
					.appendText("]" + elf.getName() + ":Bye~");
			player.sendMessage(text);
		}
	}

	@Override
	public TalkChapter getChapter(EntityElfBase elf, EntityPlayer player, NBTTagCompound shiftData) {
		TalkChapter superChapter = super.getChapter(elf, player, shiftData);
		if (superChapter != null) return superChapter;

		if (ElfConfig.isVeryDishonest(player))
			return new TalkChapter().addScene(new TalkSceneSay("say.merchant.dishonest", Talker.OPPOSING));
		if (ElfConfig.isVeryHonest(player)) proceed: {
			double n = EntityHelper.getChanceFrom2UUID(elf.getUniqueID(), player.getUniqueID());
			if (n <= MERCHANT_GIFT_CHANCE_WHEN_VH) {
				VariableSet storage = elf.getProfessionStorage();
				NBTTagCompound meetNBT = storage.get(MEET);
				if (meetNBT.hasKey(player.getName())) break proceed;
				meetNBT.setBoolean(player.getName(), true);
				ElfMerchantType merchantType = storage.get(M_TYPE);
				Trade trade = merchantType.getTrade(storage);
				if (trade != null) return getGiftChapter(elf, player, trade);
			}
		}
		TalkChapter chapter = new TalkChapter();
		TalkSceneSay scene = new TalkSceneSay();
		chapter.addScene(scene);
		scene.addString("say.merchant.good.goods", Talker.OPPOSING);
		scene.addAction(new TalkActionToGui(ESGuiHandler.GUI_ELF_TRADE));
		return chapter;
	}

	public TalkChapter getGiftChapter(EntityElfBase elf, EntityPlayer player, Trade trade) {
		TalkChapter chapter = new TalkChapter();
		TalkSceneSay scene = new TalkSceneSay();
		chapter.addScene(scene);
		scene.addString("say.merchant.give.gift", Talker.OPPOSING);
		scene.addString("say.very.thank", Talker.PLAYER);
		scene.addAction((p, e, c, i, s, t) -> {
			int totalCost = 1;
			int size = trade.getTradeListSize();
			WeightRandom<Integer> wr = new WeightRandom();
			for (int n = 0; n < size; n++) {
				TradeList.TradeInfo info = trade.getTradeInfo(n);
				if (info.isReclaim()) continue;
				if (trade.stock(n) <= 0) continue;
				if (info.getCost() > MERCHANT_GIFT_MAX_VALUE) continue;
				totalCost += info.getCost();
			}
			for (int n = 0; n < size; n++) {
				TradeList.TradeInfo info = trade.getTradeInfo(n);
				if (info.isReclaim()) continue;
				if (trade.stock(n) <= 0) continue;
				if (info.getCost() > MERCHANT_GIFT_MAX_VALUE) continue;
				wr.add(n, totalCost - info.getCost());
			}
			wr.add(-1, totalCost * 0.05);
			wr.add(-2, totalCost * 0.05);
			wr.add(-2, totalCost * 0.02);
			int n = wr.get();
			switch (n) {
			case -1:
				ItemHelper.addItemStackToPlayer(p, new ItemStack(ESObjects.BLOCKS.ELF_FRUIT,
						RandomHelper.rand.nextInt(8) + 8, BlockElfFruit.MAX_STATE));
				break;
			case -2:
				ItemHelper.addItemStackToPlayer(p,
						new ItemStack(ESObjects.ITEMS.RESONANT_CRYSTAL, RandomHelper.rand.nextInt(4) + 2));
				break;
			case -3:
				VariableSet storage = elf.getProfessionStorage();
				ElfMerchantType merchantType = storage.get(M_TYPE);
				ItemHelper.addItemStackToPlayer(p,
						ItemMerchantInvitation.createInvitationWithMerchantType(merchantType));
				break;
			default:
				TradeList.TradeInfo info = trade.getTradeInfo(n);
				ItemHelper.addItemStackToPlayer(p, info.getCommodity().copy());
				trade.sell(n, 1);
				break;
			}
			if (player instanceof EntityPlayerMP)
				ESCriteriaTriggers.ES_TRING.trigger((EntityPlayerMP) player, "elf:gift");
			return true;
		});
		return chapter;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ResourceLocation getTexture(EntityElfBase elf) {
		return RenderEntityElf.TEXTURE_MERCHANT;
	}
}

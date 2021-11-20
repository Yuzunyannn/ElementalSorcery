package yuzunyannn.elementalsorcery.elf.pro;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.crafting.IToElementInfo;
import yuzunyannn.elementalsorcery.container.ESGuiHandler;
import yuzunyannn.elementalsorcery.crafting.element.ElementMap;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.elf.talk.TalkActionToGui;
import yuzunyannn.elementalsorcery.elf.talk.TalkChapter;
import yuzunyannn.elementalsorcery.elf.talk.TalkSceneSay;
import yuzunyannn.elementalsorcery.elf.talk.Talker;
import yuzunyannn.elementalsorcery.elf.trade.Trade;
import yuzunyannn.elementalsorcery.elf.trade.TradeCount;
import yuzunyannn.elementalsorcery.elf.trade.TradeList;
import yuzunyannn.elementalsorcery.entity.elf.EntityElfBase;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.item.prop.ItemKeepsake;
import yuzunyannn.elementalsorcery.render.entity.living.RenderEntityElf;
import yuzunyannn.elementalsorcery.util.NBTTag;
import yuzunyannn.elementalsorcery.util.helper.RandomHelper;
import yuzunyannn.elementalsorcery.world.Juice;

public class ElfProfessionMerchant extends ElfProfessionUndetermined {

	@Override
	public void initElf(EntityElfBase elf, ElfProfession origin) {
		elf.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(ESInit.ITEMS.ELF_COIN));
		if (elf.world.isRemote) return;
		NBTTagCompound nbt = elf.getEntityData();
		if (nbt.hasKey(TradeCount.Bind.TAG)) return;
		this.randomGoods(elf);
	}

	/** 随机一些物品 */
	public void randomGoods(EntityElfBase elf) {
		ESObjects.Items ITEMS = ESInit.ITEMS;
		ESObjects.Blocks BLOCKS = ESInit.BLOCKS;
		NBTTagCompound nbt = elf.getEntityData();
		TradeCount trade = new TradeCount();
		Random rand = elf.getRNG();
		if (rand.nextInt(2) == 0) addACommodity(trade, new ItemStack(BLOCKS.ELF_FRUIT, 1, 2), 5, 20, 1000);
		if (rand.nextInt(3) == 0) addACommodity(trade, new ItemStack(ITEMS.RESONANT_CRYSTAL), 50, 8, 1000);
		if (rand.nextInt(2) == 0) {
			for (int i = 0; i < 3; i++) {
				if (rand.nextFloat() < 0.25) break;
				ItemStack cup = Juice.randomJuice(rand, false);
				addACommodity(trade, cup, priceIt(cup), 3, 0);
			}
		}

		switch (rand.nextInt(3)) {
		case 2:
			addACommodity(trade, new ItemStack(ITEMS.ELF_WATCH, 1, 0), 450, 1, 1000);
		case 1:
			addACommodity(trade, new ItemStack(ITEMS.RITE_MANUAL, 1, 0), 500, 1, 1000);
		case 0:
			addACommodity(trade, new ItemStack(ITEMS.NATURE_DUST, 1, 1), 150, 3, 1000);
		}

		if (rand.nextInt(10) == 0) {
			int c = (int) RandomHelper.randomRange(50, 150);
			ItemStack letter = new ItemStack(ESInit.ITEMS.KEEPSAKE, 1,
					ItemKeepsake.EnumType.UNDELIVERED_LETTER.getMeta());
			trade.addCommodity(letter, c, 3, true);
		}

		for (int i = 0; i < 12 && trade.getTradeList().size() < 16; i++) {
			ItemStack item = new ItemStack(Item.REGISTRY.getRandomObject(rand));
			int price = priceIt(item);
			if (price < 1) continue;
			addACommodity(trade, item, price, Math.max(16 - (int) MathHelper.sqrt(price), 1), 125);
		}
		nbt.setTag(TradeCount.Bind.TAG, trade.serializeNBT());
	}

	/** 给一个物品定价 */
	public static int priceIt(ItemStack item) {
		float ret = priceIt(item, 0);
		if (ret == -1) return -1;
		return MathHelper.ceil(ret);
	}

	private static float priceIt(ItemStack item, int deep) {
		if (deep > 5) return -1;
		IToElementInfo info = ElementMap.instance.toElement(item);
		if (info == null) return -1;
		ElementStack[] estacks = info.element();
		float n = 0;
		for (ElementStack estack : estacks) {
			float count = estack.getPower() / 10f * MathHelper.sqrt(estack.getCount());
			if (estack.isMagic()) count = count / 4;
			if (estack.getElement() == ESInit.ELEMENTS.STAR) count = count * 3;
			n += count;
		}
		float count = (float) Math.pow(1.4, MathHelper.sqrt(info.complex()));
		float money = n * count;
		ItemStack[] remains = info.remain();
		if (remains != null) {
			for (ItemStack stack : remains) {
				float ret = priceIt(stack, deep + 1);
				if (ret == -1) return -1;
				money = money + ret;
			}
		}
		return money;
	}

	public static void addACommodity(TradeCount trade, ItemStack item, int price, int count, int checkPrice) {
		int sellPrice = (int) (price + price * RandomHelper.rand.nextFloat() * 2);
		int reclaimPrice = (int) (price - price * RandomHelper.rand.nextFloat() * 0.5f);

		sellPrice = Math.max(1, sellPrice);
		reclaimPrice = Math.max(1, reclaimPrice);

		if (price < checkPrice) {
			int c = (int) RandomHelper.randomRange(count * 0.5f, count * 1.5f);
			c = Math.max(1, c);
			trade.addCommodity(item, sellPrice, c);
		}
		count = (int) RandomHelper.randomRange(count * 0.5f, count * 1.5f) + 2;
		trade.addCommodity(item, reclaimPrice, count, true);
	}

	public static void setRemainTimeBeforeLeave(EntityElfBase elf, int tick) {
		NBTTagCompound nbt = elf.getEntityData();
		nbt.setInteger("tradeRemainTime", tick);
	}

	@Override
	public void transferElf(EntityElfBase elf, ElfProfession next) {
//		NBTTagCompound nbt = elf.getEntityData();
//		nbt.removeTag(TradeCount.Bind.TAG);
//		nbt.removeTag("tradeRemainTime");
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
		openTalkGui(player, elf);
		return true;
	}

	@Override
	public void tick(EntityElfBase elf) {
		if (elf.tick % 100 != 0) return;
		if (elf.getTalker() != null) return;
		if (elf.world.isRemote) return;

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
		if (stack.getItem() == ESInit.ITEMS.SPELLBOOK) {
			elf.setDead();
			elf.world.playSound(null, elf.posX, elf.posY, elf.posZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT,
					SoundCategory.HOSTILE, 1, 1);
			return;
		}
		NBTTagCompound nbt = elf.getEntityData();

		// 如果有剩余时间，检查剩余时间
		if (nbt.hasKey("tradeRemainTime", NBTTag.TAG_NUMBER)) {
			int remainTick = nbt.getInteger("tradeRemainTime") - 100;// 因为100tick进入一次，不精准，但足够
			if (remainTick <= 0) {
				this.setLeave(elf);
				nbt.removeTag("tradeRemainTime");
				return;
			}
			nbt.setInteger("remainTime", remainTick);
		}

		// 检查是否所有东西全卖出去了
		if (nbt.hasKey(TradeCount.Bind.TAG)) {
			TradeCount tradeCount = new TradeCount();
			tradeCount.deserializeNBT(nbt.getCompoundTag(TradeCount.Bind.TAG));
			int size = tradeCount.getTradeListSize();
			for (int i = 0; i < size; i++) {
				TradeList.TradeInfo info = tradeCount.getTradeInfo(i);
				if (info.isReclaim()) continue;
				if (tradeCount.stock(i) > 0) return;
			}
		}

		// 是的情况下，离开
		this.setLeave(elf);
	}

	protected void setLeave(EntityElfBase elf) {
		ItemStack stack = elf.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
		if (stack.getItem() == ESInit.ITEMS.SPELLBOOK) return;

		elf.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(ESInit.ITEMS.SPELLBOOK));
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
		TalkChapter chapter = new TalkChapter();
		TalkSceneSay scene = new TalkSceneSay();
		chapter.addScene(scene);
		scene.addString("say.merchant.good.goods", Talker.OPPOSING);
		scene.addAction(new TalkActionToGui(ESGuiHandler.GUI_ELF_TRADE));
		return chapter;
	}

	@Override
	public Trade getTrade(EntityElfBase elf, EntityPlayer player, @Nullable NBTTagCompound shiftData) {
		return new TradeCount.Bind(elf);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ResourceLocation getTexture(EntityElfBase elf) {
		return RenderEntityElf.TEXTURE_MERCHANT;
	}
}

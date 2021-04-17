package yuzunyannn.elementalsorcery.elf.pro;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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
import yuzunyannn.elementalsorcery.render.entity.RenderEntityElf;
import yuzunyannn.elementalsorcery.util.RandomHelper;

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
		int rn = rand.nextInt(3);
		switch (rn) {
		case 2:
			addACommodity(trade, new ItemStack(ITEMS.NATURE_DUST, 1, 1), 150, 3, 1000);
		case 1:
			addACommodity(trade, new ItemStack(ITEMS.RESONANT_CRYSTAL), 50, 8, 1000);
		default:
			addACommodity(trade, new ItemStack(BLOCKS.ELF_FRUIT, 1, 2), 10, 20, 1000);
		}

		for (int i = 0; i < 12 && trade.getTradeList().size() < 16; i++) {
			ItemStack item = new ItemStack(Item.REGISTRY.getRandomObject(rand));
			int price = priceIt(item);
			if (price < 2) continue;
			addACommodity(trade, item, price, Math.max(12 - (int) MathHelper.sqrt(price), 1), 125);
		}
		nbt.setTag(TradeCount.Bind.TAG, trade.serializeNBT());
	}

	/** 给一个物品定价 */
	public static int priceIt(ItemStack item) {
		ElementStack[] estacks = ElementMap.instance.toElementStack(item);
		if (estacks == null) return -1;
		int n = -1;
		for (ElementStack estack : estacks)
			n += MathHelper.sqrt(estack.getPower()) * MathHelper.sqrt(estack.getCount());
		return n;
	}

	public static void addACommodity(TradeCount trade, ItemStack item, int price, int count, int checkPrice) {
		int sellPrice = (int) (price + price * RandomHelper.rand.nextFloat() * 2);
		int reclaimPrice = (int) (price - price * RandomHelper.rand.nextFloat() * 0.5f);
		if (price < checkPrice)
			trade.addCommodity(item, sellPrice, (int) RandomHelper.randomRange(count * 0.5f, count * 1.5f));
		trade.addCommodity(item, reclaimPrice, 1, true);
	}

	@Override
	public void transferElf(EntityElfBase elf, ElfProfession next) {
		NBTTagCompound nbt = elf.getEntityData();
		nbt.removeTag(TradeCount.Bind.TAG);
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
		NBTTagCompound nbt = elf.getEntityData();
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

		ItemStack stack = elf.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
		if (stack.getItem() == ESInit.ITEMS.SPELLBOOK) {
			elf.setDead();
			elf.world.playSound(null, elf.posX, elf.posY, elf.posZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT,
					SoundCategory.HOSTILE, 1, 1);
		} else {
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

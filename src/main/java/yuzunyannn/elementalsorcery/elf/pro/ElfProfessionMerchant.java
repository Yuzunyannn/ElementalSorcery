package yuzunyannn.elementalsorcery.elf.pro;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
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
			addACommodity(trade, new ItemStack(ITEMS.RESONANT_CRYSTAL), 85, 8, 1000);
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

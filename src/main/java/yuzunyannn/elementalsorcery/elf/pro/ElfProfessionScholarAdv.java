package yuzunyannn.elementalsorcery.elf.pro;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import yuzunyannn.elementalsorcery.container.ESGuiHandler;
import yuzunyannn.elementalsorcery.elf.talk.TalkActionEnd;
import yuzunyannn.elementalsorcery.elf.talk.TalkActionGoTo;
import yuzunyannn.elementalsorcery.elf.talk.TalkActionToGui;
import yuzunyannn.elementalsorcery.elf.talk.TalkChapter;
import yuzunyannn.elementalsorcery.elf.talk.TalkSceneSay;
import yuzunyannn.elementalsorcery.elf.talk.TalkSceneSelect;
import yuzunyannn.elementalsorcery.elf.talk.Talker;
import yuzunyannn.elementalsorcery.elf.trade.TradeCount;
import yuzunyannn.elementalsorcery.entity.elf.EntityElfBase;
import yuzunyannn.elementalsorcery.item.ItemElfPurse;
import yuzunyannn.elementalsorcery.item.ItemParchment;
import yuzunyannn.elementalsorcery.parchment.Page;
import yuzunyannn.elementalsorcery.parchment.Pages;
import yuzunyannn.elementalsorcery.tile.TileElfTreeCore;
import yuzunyannn.elementalsorcery.util.helper.RandomHelper;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

public class ElfProfessionScholarAdv extends ElfProfessionScholar {

	public ElfProfessionScholarAdv() {
		this.setTranslationKey("scholar");
	}

	public static TalkChapter getChapterForBuyParchment(Page page, EntityPlayer player) {
		TalkChapter chapter = new TalkChapter();
		int coin = 50;
		if (page.level > 0) coin = coin + page.level * 20;
		chapter.addScene(new TalkSceneSay("#say.buy.parchment?" + Integer.toString(coin)));
		// 确认
		final int needMoney = coin;
		TalkSceneSelect confirm = new TalkSceneSelect();
		chapter.addScene(confirm);
		confirm.addString("say.ok", (p, e, c, i, s, t) -> {
			int rest = ItemElfPurse.extract(player.inventory, needMoney, true);
			if (rest > 0) {
				TalkActionGoTo.goTo("nomoney", chapter, s, i);
				return false;
			}
			ItemElfPurse.extract(player.inventory, needMoney, false);
			TalkActionGoTo.goTo("finish", chapter, s, i);
			ItemHelper.addItemStackToPlayer(player, ItemParchment.getParchment(page.getId()));
			return true;
		});
		confirm.addString("say.no", new TalkActionEnd());
		// 没钱
		chapter.addScene(new TalkSceneSay("say.nomoney").setLabel("nomoney").setEnd());
		// 支付
		chapter.addScene(new TalkSceneSay("say.ok").setLabel("finish"));
		return chapter;
	}

	@Override
	public TalkChapter getChapter(EntityElfBase elf, EntityPlayer player, NBTTagCompound shiftData) {
		TileElfTreeCore core = elf.getEdificeCore();
		if (core == null) return new TalkChapter().addScene(new TalkSceneSay("say.edifice.broken"));
		// 检查手上物品
		ItemStack stack = player.getHeldItemMainhand();
		Page page = Pages.itemToPage(stack);
		if (page != null) return getChapterForBuyParchment(page, player);

		// 其余对话
		TalkChapter chapter = new TalkChapter();
		chapter.addScene(new TalkSceneSay("say.can.help"));

		TalkSceneSelect what = new TalkSceneSelect();
		chapter.addScene(what);
		what.addString("say.teach.me", new TalkActionGoTo("teach"));
		what.addString("say.buy.something", new TalkActionToGui(ESGuiHandler.GUI_ELF_TRADE));
		what.addString("say.no", new TalkActionEnd());

		// 信息
		TalkSceneSay teach = new TalkSceneSay().setLabel("teach");
		chapter.addScene(teach);
		String[] g = (String[]) RandomHelper.randomSelect(2, tips.toArray());
		for (String s : g) teach.addString(s, Talker.OPPOSING);

		TalkSceneSelect isKnow = new TalkSceneSelect();
		chapter.addScene(isKnow);
		isKnow.addString("say.scholar.konw", new TalkActionGoTo("konw"));
		isKnow.addString("say.scholar.unkonw", new TalkActionGoTo("unkonw"));

		chapter.addScene(new TalkSceneSay("say.ok").setLabel("konw").setEnd());
		chapter.addScene(new TalkSceneSay("say.scholar.pretendKnow").setLabel("unkonw").setEnd());

		return chapter;
	}

	@Override
	public void tick(EntityElfBase elf) {
		if (elf.tick % 20 * 30 != 0) return;
		if (elf.world.isRemote) return;
		if (elf.getTalker() != null) return;
		NBTTagCompound nbt = elf.getEntityData();
		long ut = nbt.getLong("ut");
		long now = elf.world.getWorldTime();
		long dt = now - ut;
		if (dt > 24000 / 2) {
			nbt.setLong("ut", now);
			nbt.setTag(TradeCount.Bind.TAG, randomSale(elf).serializeNBT());
		} else if (dt < 0) {
			nbt.setLong("ut", now); // 时间调整过重置下
		}
	}

}

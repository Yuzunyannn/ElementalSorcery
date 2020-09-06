package yuzunyannn.elementalsorcery.elf.pro;

import java.util.ArrayList;
import java.util.Arrays;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.elf.talk.TalkActionEnd;
import yuzunyannn.elementalsorcery.elf.talk.TalkActionGoTo;
import yuzunyannn.elementalsorcery.elf.talk.TalkActionToTrade;
import yuzunyannn.elementalsorcery.elf.talk.TalkChapter;
import yuzunyannn.elementalsorcery.elf.talk.TalkSceneSay;
import yuzunyannn.elementalsorcery.elf.talk.TalkSceneSelect;
import yuzunyannn.elementalsorcery.elf.talk.Talker;
import yuzunyannn.elementalsorcery.elf.trade.Trade;
import yuzunyannn.elementalsorcery.elf.trade.TradeCount;
import yuzunyannn.elementalsorcery.elf.trade.TradeList;
import yuzunyannn.elementalsorcery.entity.elf.EntityElfBase;
import yuzunyannn.elementalsorcery.init.ESInitInstance;
import yuzunyannn.elementalsorcery.item.ItemParchment;
import yuzunyannn.elementalsorcery.parchment.Page;
import yuzunyannn.elementalsorcery.parchment.PageEasy;
import yuzunyannn.elementalsorcery.parchment.PageMult;
import yuzunyannn.elementalsorcery.parchment.Pages;
import yuzunyannn.elementalsorcery.render.entity.RenderEntityElf;
import yuzunyannn.elementalsorcery.util.RandomHelper;

public class ElfProfessionScholar extends ElfProfessionNone {

	protected static final ArrayList<String> pages = new ArrayList<String>();
	protected static final ArrayList<String> tips = new ArrayList<String>();

	/** page的lev为-2时候，进行回调 */
	public static void addScholarPage(Page page) {
		pages.add(page.getId());
		addPageInfo(page);
	}

	private static void addPageInfo(Page page) {
		if (page instanceof PageMult) {
			PageMult mult = (PageMult) page;
			for (Page p : mult.getPages()) addPageInfo(p);
		} else if (page instanceof PageEasy) {
			String value = ((PageEasy) page).getContext();
			if (value == null || value.isEmpty() || tips.contains(value)) return;
			tips.add(value);
		}
	}

	/** 初始化 */
	public static void init() {
		pages.clear();
		tips.clear();
		tips.addAll(Arrays.asList(new String[] { "page.riteTable.ct.fir", "page.riteTable.ct.sec",
				"page.sacrifice.ct.fir", "page.sacrifice.ct.sec", "page.rite.ct", "page.riteManual.ct",
				"page.riteCraft.ct", "page.element.ct", "page.elfTree.ct" }));
		tips.add("say.scholar.becare");
		tips.add("say.scholar.master");
	}

	@Override
	public void initElf(EntityElfBase elf, ElfProfession origin) {
		elf.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(ESInitInstance.ITEMS.MANUAL));
		elf.setDropChance(EntityEquipmentSlot.MAINHAND, 0);
		// 初始化交易信息
		NBTTagCompound nbt = elf.getEntityData();
		// 如果存在标签，则表示是回复时初始化的，直接走人
		if (nbt.hasKey(TradeCount.Bind.TAG)) return;
		TradeCount trade = new TradeCount();
		TradeList list = trade.getTradeList();
		list.add(new ItemStack(ESInitInstance.ITEMS.PARCHMENT), 1, true);
		Object[] needPages = RandomHelper.randomSelect(5, pages.toArray());
		for (int i = 0; i < needPages.length; i++) {
			String id = needPages[i].toString();
			list.add(ItemParchment.getParchment(id), 8, false);
		}
		for (int i = 0; i < 11 - needPages.length; i++) {
			String id = Pages.getPage(RandomHelper.rand.nextInt(Pages.getCount() - 2) + 2).getId();
			list.add(ItemParchment.getParchment(id), 8, false);
		}
		// 一些其余东西
		if (RandomHelper.rand.nextInt(3) == 0) {
			list.add(new ItemStack(ESInitInstance.ITEMS.RESONANT_CRYSTAL), 80, false);
			trade.setStock(list.size() - 1, RandomHelper.rand.nextInt(12) + 4);
		} else {
			list.add(new ItemStack(ESInitInstance.BLOCKS.ELF_FRUIT, 1, 2), 1, false);
			trade.setStock(list.size() - 1, 1000);
		}
		if (RandomHelper.rand.nextInt(2) == 0) {
			list.add(new ItemStack(Items.BOOK), 16, false);
			trade.setStock(list.size() - 1, RandomHelper.rand.nextInt(12) + 4);
		}
		if (RandomHelper.rand.nextInt(2) == 0) {
			list.add(new ItemStack(Items.PAPER), 2, false);
			trade.setStock(list.size() - 1, RandomHelper.rand.nextInt(16) + 8);
		}
		if (RandomHelper.rand.nextInt(4) == 0) list.add(new ItemStack(ESInitInstance.ITEMS.RITE_MANUAL), 120, false);

		nbt.setTag(TradeCount.Bind.TAG, trade.serializeNBT());
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
	public TalkChapter getChapter(EntityElfBase elf, EntityPlayer player) {
		TalkChapter chapter = new TalkChapter();
		TalkSceneSay scene = new TalkSceneSay();
		chapter.addScene(scene);
		String[] g = (String[]) RandomHelper.randomSelect(3, tips.toArray());
		for (String s : g) scene.addString(s, Talker.OPPOSING);
		// 没有返回
		if (scene.isEmpty()) {
			scene.addString("...", Talker.OPPOSING);
			return chapter;
		}
		// 有的话增加内容
		TalkSceneSelect sceneS = new TalkSceneSelect();
		chapter.addScene(sceneS);
		sceneS.addString("say.scholar.konw", new TalkActionGoTo("konw"));
		sceneS.addString("say.scholar.unkonw", new TalkActionGoTo("unkonw"));
		sceneS.addString("say.scholar.buy", new TalkActionGoTo("buy"));

		scene = new TalkSceneSay().setLabel("buy");
		chapter.addScene(scene);
		scene.addString("say.scholar.nice.goods", Talker.OPPOSING);
		scene.addAction(new TalkActionToTrade());

		scene = new TalkSceneSay().setLabel("konw");
		chapter.addScene(scene);
		scene.addString("say.scholar.ok", Talker.OPPOSING);
		scene.addAction(new TalkActionEnd());

		scene = new TalkSceneSay().setLabel("unkonw");
		chapter.addScene(scene);
		scene.addString("say.scholar.pretendKnow", Talker.OPPOSING);

		return chapter;
	}

	@Override
	public Trade getTrade(EntityElfBase elf, EntityPlayer player) {
		return new TradeCount.Bind(elf);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ResourceLocation getTexture(EntityElfBase elf) {
		return RenderEntityElf.TEXTURE_SCHOLAR;
	}
}

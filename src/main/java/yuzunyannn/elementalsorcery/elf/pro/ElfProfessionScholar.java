package yuzunyannn.elementalsorcery.elf.pro;

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
import yuzunyannn.elementalsorcery.parchment.Pages;
import yuzunyannn.elementalsorcery.render.entity.RenderEntityElf;
import yuzunyannn.elementalsorcery.util.RandomHelper;

public class ElfProfessionScholar extends ElfProfessionNone {

	@Override
	public void initElf(EntityElfBase elf, ElfProfession origin) {
		elf.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(ESInitInstance.ITEMS.MANUAL));
		elf.setDropChance(EntityEquipmentSlot.MAINHAND, 0);
		// 初始化交易信息
		NBTTagCompound nbt = elf.getEntityData();
		TradeCount trade = new TradeCount();
		TradeList list = trade.getTradeList();
		list.add(new ItemStack(ESInitInstance.ITEMS.PARCHMENT), 1, true);
		for (int i = 0; i < 11; i++) {
			String id = Pages.getPage(RandomHelper.rand.nextInt(Pages.getCount() - 2) + 2).getId();
			list.add(ItemParchment.getParchment(id), 8, false);
		}
		if (RandomHelper.rand.nextInt(3) == 0) {
			list.add(new ItemStack(ESInitInstance.ITEMS.RESONANT_CRYSTAL), 80, false);
			trade.setStock(list.size() - 1, RandomHelper.rand.nextInt(7) + 3);
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

	static final String[] GIVE = new String[] { "riteTable.ct.fir", "riteTable.ct.sec", "sacrifice.ct.fir",
			"sacrifice.ct.sec", "rite.ct", "riteManual.ct", "riteCraft.ct", "element.ct" };

	@Override
	public TalkChapter getChapter(EntityElfBase elf, EntityPlayer player) {
		TalkChapter chapter = new TalkChapter();
		TalkSceneSay scene = new TalkSceneSay();
		chapter.addScene(scene);
		String[] g = RandomHelper.randomSelect(3, GIVE);
		for (String s : g) scene.addString("page." + s, Talker.OPPOSING);
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

package yuzunyannn.elementalsorcery.elf.pro;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.util.var.VariableSet;
import yuzunyannn.elementalsorcery.container.ESGuiHandler;
import yuzunyannn.elementalsorcery.elf.ElfConfig;
import yuzunyannn.elementalsorcery.elf.pro.merchant.ElfMerchantType;
import yuzunyannn.elementalsorcery.elf.talk.TalkActionEnd;
import yuzunyannn.elementalsorcery.elf.talk.TalkActionGoTo;
import yuzunyannn.elementalsorcery.elf.talk.TalkActionToGui;
import yuzunyannn.elementalsorcery.elf.talk.TalkChapter;
import yuzunyannn.elementalsorcery.elf.talk.TalkSceneSay;
import yuzunyannn.elementalsorcery.elf.talk.TalkSceneSelect;
import yuzunyannn.elementalsorcery.elf.talk.Talker;
import yuzunyannn.elementalsorcery.entity.elf.EntityElfBase;
import yuzunyannn.elementalsorcery.parchment.Page;
import yuzunyannn.elementalsorcery.parchment.PageEasy;
import yuzunyannn.elementalsorcery.parchment.PageMult;
import yuzunyannn.elementalsorcery.render.entity.living.RenderEntityElf;
import yuzunyannn.elementalsorcery.util.helper.RandomHelper;

public class ElfProfessionScholar extends ElfProfessionUndetermined {

	public static final ArrayList<String> pages = new ArrayList<String>();
	public static final ArrayList<String> tips = new ArrayList<String>();

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
		tips.add("say.scholar.becare");
		tips.add("say.scholar.master");
		tips.add("say.scholar.more.floor");
		tips.add("say.scholar.watch");
		tips.add("say.scholar.origin");
	}

	@Override
	public void initElf(EntityElfBase elf, ElfProfession origin) {
		elf.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(ESObjects.ITEMS.MANUAL));
		elf.setDropChance(EntityEquipmentSlot.MAINHAND, 0);
		if (elf.world.isRemote) return;
		// 如果存在标签，则表示是回复时初始化的，直接走人，反之初始化
		VariableSet storage = elf.getProfessionStorage();
		storage.set(M_TYPE, ElfMerchantType.SCHOLAR);
		if (ElfMerchantType.SCHOLAR.hasTrade(storage)) return;
		ElfMerchantType.SCHOLAR.renewTrade(elf.world, elf.getPosition(), elf.getRNG(), storage);
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
	public TalkChapter getChapter(EntityElfBase elf, EntityPlayer player, NBTTagCompound shiftData) {
		TalkChapter superChapter = super.getChapter(elf, player, shiftData);
		if (superChapter != null) return superChapter;

		TalkChapter chapter = new TalkChapter();
		if (ElfConfig.isSuperDishonest(player)) return chapter.addScene(new TalkSceneSay("say.dishonest.not.say"));
		TalkSceneSay scene = new TalkSceneSay();
		chapter.addScene(scene);
		String[] g = (String[]) RandomHelper.randomSelect(2, tips.toArray());
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
		scene.addAction(new TalkActionToGui(ESGuiHandler.GUI_ELF_TRADE));

		scene = new TalkSceneSay().setLabel("konw");
		chapter.addScene(scene);
		scene.addString("say.ok", Talker.OPPOSING);
		scene.addAction(new TalkActionEnd());

		scene = new TalkSceneSay().setLabel("unkonw");
		chapter.addScene(scene);
		scene.addString("say.scholar.pretendKnow", Talker.OPPOSING);

		return chapter;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ResourceLocation getTexture(EntityElfBase elf) {
		return RenderEntityElf.TEXTURE_SCHOLAR;
	}
}

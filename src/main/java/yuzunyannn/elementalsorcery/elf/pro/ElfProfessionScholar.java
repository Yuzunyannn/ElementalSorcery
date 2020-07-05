package yuzunyannn.elementalsorcery.elf.pro;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.elf.talk.TalkActionEnd;
import yuzunyannn.elementalsorcery.elf.talk.TalkActionGoTo;
import yuzunyannn.elementalsorcery.elf.talk.TalkChapter;
import yuzunyannn.elementalsorcery.elf.talk.TalkSceneSay;
import yuzunyannn.elementalsorcery.elf.talk.TalkSceneSelect;
import yuzunyannn.elementalsorcery.elf.talk.Talker;
import yuzunyannn.elementalsorcery.entity.elf.EntityElfBase;
import yuzunyannn.elementalsorcery.init.ESInitInstance;
import yuzunyannn.elementalsorcery.render.entity.RenderEntityElf;
import yuzunyannn.elementalsorcery.util.RandomHelper;

public class ElfProfessionScholar extends ElfProfessionNone {

	@Override
	public void initElf(EntityElfBase elf, ElfProfession origin) {
		elf.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(ESInitInstance.ITEMS.MANUAL));
		elf.setDropChance(EntityEquipmentSlot.MAINHAND, 0);
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
		sceneS.addString("say.konw", new TalkActionGoTo("konw"));
		sceneS.addString("say.unkonw", new TalkActionGoTo("unkonw"));

		scene = new TalkSceneSay().setLabel("konw");
		chapter.addScene(scene);
		scene.addString("say.ok", Talker.OPPOSING);
		scene.addAction(new TalkActionEnd());

		scene = new TalkSceneSay().setLabel("unkonw");
		chapter.addScene(scene);
		scene.addString("say.pretendKnow", Talker.OPPOSING);

		return chapter;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ResourceLocation getTexture(EntityElfBase elf) {
		return RenderEntityElf.TEXTURE_SCHOLAR;
	}
}

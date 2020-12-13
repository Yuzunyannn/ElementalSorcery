package yuzunyannn.elementalsorcery.elf.pro;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.capability.Adventurer;
import yuzunyannn.elementalsorcery.elf.quest.IAdventurer;
import yuzunyannn.elementalsorcery.elf.quest.Quest;
import yuzunyannn.elementalsorcery.elf.quest.QuestStatus;
import yuzunyannn.elementalsorcery.elf.talk.ITalkAction;
import yuzunyannn.elementalsorcery.elf.talk.TalkActionEnd;
import yuzunyannn.elementalsorcery.elf.talk.TalkActionGoTo;
import yuzunyannn.elementalsorcery.elf.talk.TalkChapter;
import yuzunyannn.elementalsorcery.elf.talk.TalkChapter.Iter;
import yuzunyannn.elementalsorcery.elf.talk.TalkScene;
import yuzunyannn.elementalsorcery.elf.talk.TalkSceneSay;
import yuzunyannn.elementalsorcery.elf.talk.TalkSceneSelect;
import yuzunyannn.elementalsorcery.elf.talk.Talker;
import yuzunyannn.elementalsorcery.entity.elf.EntityElfBase;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.item.ItemQuest;
import yuzunyannn.elementalsorcery.render.entity.RenderEntityElf;
import yuzunyannn.elementalsorcery.tile.TileElfTreeCore;

public class ElfProfessionReceptionist extends ElfProfessionNPCBase {

	@Override
	public void initElf(EntityElfBase elf, ElfProfession origin) {
		super.initElf(elf, origin);
		elf.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(ESInit.ITEMS.QUEST));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ResourceLocation getTexture(EntityElfBase elf) {
		return RenderEntityElf.TEXTURE_RECEPTIONIST;
	}

	@Override
	public boolean interact(EntityElfBase elf, EntityPlayer player) {
		openTalkGui(player, elf);
		return true;
	}

	@Override
	public TalkChapter getChapter(EntityElfBase elf, EntityPlayer player, NBTTagCompound shiftData) {
		TileElfTreeCore core = elf.getEdificeCore();
		TalkChapter chapter = new TalkChapter();

		if (core == null) {
			chapter.addScene(new TalkSceneSay("say.edifice.broken"));
			return chapter;
		}

		ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);
		IAdventurer adventurer = player.getCapability(Adventurer.ADVENTURER_CAPABILITY, null);
		Quest quest = null;
		boolean hasOverdue = false;
		if (adventurer != null) {
			if (ItemQuest.isQuest(stack)) quest = ItemQuest.getQuest(stack);
			// 检查过期的任务
			long now = elf.world.getWorldTime();
			for (Quest q : adventurer) if (q.isOverdue(now)) {
				hasOverdue = true;
				break;
			}
		}
		// 有超时的任务，删除，并提示
		if (hasOverdue) {
			Quest.unsignOverdueQuest(player, true);
			chapter.addScene(new TalkSceneSay("say.reputation.decline"));
			return chapter;
		}
		if (quest != null) {
			// 委托正在进行中
			if (quest.getStatus() == QuestStatus.UNDERWAY && quest.isAdventurer(player)) {
				if (Quest.finishQuest(player, quest, stack)) {
					chapter.addScene(new TalkSceneSay("say.quest.finish"));
					return chapter;
				} else {
					chapter.addScene(new TalkSceneSay("say.nofinish.quest"));
					return chapter;
				}
			}
			// 委托过期了，无法再接了
			if (quest.isOverdue(player.world.getWorldTime())) {
				chapter.addScene(new TalkSceneSay("say.overdue.quest"));
				return chapter;
			}
			// 委托已经进行，无法再接了
			if (quest.getStatus() != QuestStatus.NONE) {
				chapter.addScene(new TalkSceneSay("say.underway.quest"));
				return chapter;
			}
			// 委托满了，无法再接了
			if (adventurer.getQuests() >= adventurer.getMaxQuests()) {
				chapter.addScene(new TalkSceneSay("say.cannot.quest"));
				return chapter;
			}
			// 检查初期条件，不满足无法再接
			if (quest.getType().checkPre(quest, player) != null) {
				chapter.addScene(new TalkSceneSay("say.cannot.quest"));
				return chapter;
			}
			// 可以接委托时，进行确认
			TalkSceneSay confirmQuest = new TalkSceneSay();
			chapter.addScene(confirmQuest);
			String title = quest.getType().getDescribe().getTitle();
			confirmQuest.addString("#say.take.quest.confirm?" + title, Talker.OPPOSING);
			TalkSceneSelect confirm = new TalkSceneSelect();
			chapter.addScene(confirm);
			confirm.addString("say.ok", new ConfirmQuest());
			confirm.addString("say.no", new TalkActionEnd());
			// 告知玩家任务领取完成
			TalkSceneSay takeQuestFin = new TalkSceneSay().setLabel("takeQuestFin");
			chapter.addScene(takeQuestFin);
			takeQuestFin.addString("say.take.quest.finish", Talker.OPPOSING);
			takeQuestFin.addAction(new TalkActionEnd());
			return chapter;
		}
		// 正常情况下的进程
		TalkSceneSay canHelp = new TalkSceneSay();
		chapter.addScene(canHelp);
		canHelp.addString("say.can.help", Talker.OPPOSING);
		// 询问你要做啥
		TalkSceneSelect what = new TalkSceneSelect();
		chapter.addScene(what);
		what.addString("say.take.quest", new TalkActionGoTo("howTQ"));
		if (core.getFloorCount() > 1) what.addString("say.want.go.upstair", new TalkActionGoTo("howGS"));
		what.addString("say.no", new TalkActionEnd());
		// 接任务指导
		TalkSceneSay howTQ = new TalkSceneSay().setLabel("howTQ");
		chapter.addScene(howTQ);
		howTQ.addString("say.how.take.quest", Talker.OPPOSING);
		howTQ.addString("say.very.thank", Talker.PLAYER);
		howTQ.addAction(new TalkActionEnd());
		// 电梯指导
		TalkSceneSay howGS = new TalkSceneSay().setLabel("howGS");
		chapter.addScene(howGS);
		howGS.addString("say.how.go.upstair", Talker.OPPOSING);
		howGS.addString("say.very.thank", Talker.PLAYER);
		howGS.addAction(new TalkActionEnd());
		return chapter;
	}

	/** 确认接任务 */
	public static class ConfirmQuest implements ITalkAction {
		@Override
		public Object invoke(EntityPlayer player, EntityElfBase elf, TalkChapter chapter, Iter iter, TalkScene scene,
				int talkAt) {
			ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);
			if (Quest.signQuest(player, stack)) return TalkActionGoTo.goTo("takeQuestFin", chapter, scene, iter);
			return TalkActionGoTo.goTo("failTakeQuest", chapter, scene, iter);
		}
	}

}

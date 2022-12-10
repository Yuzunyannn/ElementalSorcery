package yuzunyannn.elementalsorcery.elf.pro;

import java.util.LinkedList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.ESData;
import yuzunyannn.elementalsorcery.advancement.ESCriteriaTriggers;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.container.ESGuiHandler;
import yuzunyannn.elementalsorcery.elf.ElfConfig;
import yuzunyannn.elementalsorcery.elf.ElfPostOffice;
import yuzunyannn.elementalsorcery.elf.quest.Quest;
import yuzunyannn.elementalsorcery.elf.quest.Quests;
import yuzunyannn.elementalsorcery.elf.talk.TalkActionEnd;
import yuzunyannn.elementalsorcery.elf.talk.TalkActionGoTo;
import yuzunyannn.elementalsorcery.elf.talk.TalkActionToGui;
import yuzunyannn.elementalsorcery.elf.talk.TalkChapter;
import yuzunyannn.elementalsorcery.elf.talk.TalkSceneSay;
import yuzunyannn.elementalsorcery.elf.talk.TalkSceneSelect;
import yuzunyannn.elementalsorcery.entity.elf.EntityElfBase;
import yuzunyannn.elementalsorcery.item.ItemElfPurse;
import yuzunyannn.elementalsorcery.item.ItemQuest;
import yuzunyannn.elementalsorcery.render.entity.living.RenderEntityElf;
import yuzunyannn.elementalsorcery.tile.TileElfTreeCore;
import yuzunyannn.elementalsorcery.util.helper.NBTHelper;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

public class ElfProfessionPostReceptionist extends ElfProfessionNPCBase {

	public static final int UPGRADE_ADDRESS_PLATE_COST = 2000;

	@Override
	public void initElf(EntityElfBase elf, ElfProfession origin) {
		super.initElf(elf, origin);
		elf.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(ESObjects.ITEMS.ADDRESS_PLATE));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ResourceLocation getTexture(EntityElfBase elf) {
		return RenderEntityElf.TEXTURE_RECEPTIONIST;
	}

	@Override
	public boolean interact(EntityElfBase elf, EntityPlayer player) {
		elf.openTalkGui(player);
		return true;
	}

	/** 获取寄送邮报的chapter */
	public static TalkChapter getChapterForSendParcel(EntityElfBase elf, EntityPlayer player,
			NBTTagCompound shiftData) {
		TalkChapter chapter = new TalkChapter();
		String address = shiftData.getString("address");
		LinkedList<ItemStack> items = NBTHelper.getItemList(shiftData, "items");
		if (items.isEmpty()) {
			chapter.addScene(new TalkSceneSay("say.submit.empty.item"));
			return chapter;
		}
		// 设置返还信息
		NBTTagCompound playerData = ESData.getPlayerNBT(player);
		NBTHelper.setItemList(playerData, "elfGuiItemBack", items);
		if (address.isEmpty()) {
			chapter.addScene(new TalkSceneSay("say.submit.empty"));
			return chapter;
		}
		int _needMoney = 50 + items.size() * 20;
		if (ElfConfig.isVeryHonest(player)) _needMoney = (int) (_needMoney * 0.5f);
		int needMoney = _needMoney;
		chapter.addScene(new TalkSceneSay("#say.send.mail.money?" + needMoney));
		// 确认
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
			playerData.removeTag("elfGuiItemBack");
			ElfPostOffice postOffice = ElfPostOffice.getPostOffice(player.world);
			postOffice.pushParcel(player, address, items);
			if (player instanceof EntityPlayerMP)
				ESCriteriaTriggers.ES_TRING.trigger((EntityPlayerMP) player, "post:send");
			return true;
		});
		confirm.addString("say.no", new TalkActionEnd());
		// 没钱
		chapter.addScene(new TalkSceneSay("say.nomoney").setLabel("nomoney").setEnd());
		// 支付
		chapter.addScene(new TalkSceneSay("say.as.soon.to.sned").setLabel("finish"));
		return chapter;
	}

	public static boolean checkAddress(String address) {
		for (int i = 0; i < address.length(); i++) {
			char ch = address.charAt(i);
			switch (ch) {
			case '$':
			case '?':
			case '%':
			case '*':
				return false;
			default:
				break;
			}
		}
		return true;
	}

	public static TalkChapter getChapterForApplyAddressPlate(EntityElfBase elf, EntityPlayer player,
			NBTTagCompound shiftData) {
		TalkChapter chapter = new TalkChapter();
		String address = shiftData.getString("address");
		// 没地址
		if (address.isEmpty()) {
			chapter.addScene(new TalkSceneSay("say.submit.empty"));
			return chapter;
		}
		// 检查地址内容
		if (!checkAddress(address)) {
			chapter.addScene(new TalkSceneSay("say.address.illegal"));
			return chapter;
		}
		int rest = ItemElfPurse.extract(player.inventory, 500, true);
		// 没钱
		if (rest > 0) {
			chapter.addScene(new TalkSceneSay("say.nomoney"));
			return chapter;
		}
		ItemElfPurse.extract(player.inventory, 500, false);
		chapter.addScene(new TalkSceneSay("say.apply.address.success"));
		ElfPostOffice postOffice = ElfPostOffice.getPostOffice(player.world);
		ItemHelper.addItemStackToPlayer(player, postOffice.createAddressPlate(player, address));
		if (player instanceof EntityPlayerMP)
			ESCriteriaTriggers.ES_TRING.trigger((EntityPlayerMP) player, "post:apply");
		// 首次
		NBTTagCompound playerData = ESData.getPlayerNBT(player);
		if (!playerData.getBoolean("HPFW")) {
			playerData.setBoolean("HPFW", true);
			chapter.addScene(new TalkSceneSay("say.post.first.welfare"));
			Quest quest = Quests.createQuest("newbie_post_office", player);
//			quest.getType().addReward(QuestRewardExp.create(50));
			quest.setEndTime(player.world.getWorldTime() + 24000 * 10);
			ItemStack stack = ItemQuest.createQuest(quest);
			ItemHelper.addItemStackToPlayer(player, stack);
		}
		return chapter;
	}

	/** 创建升级地址牌 */
	public static void addChapterForUpgradeAddressPlate(TalkChapter chapter, EntityElfBase elf, EntityPlayer player) {
		int needMoney = (int) (ElfConfig.isVeryHonest(player) ? UPGRADE_ADDRESS_PLATE_COST * 0.5f
				: UPGRADE_ADDRESS_PLATE_COST);
		// 升级
		chapter.addScene(new TalkSceneSay("#say.upgrade.help?" + needMoney).setLabel("upgrade"));
		// 确认
		TalkSceneSelect confirm = new TalkSceneSelect();
		chapter.addScene(confirm);
		confirm.addString("say.ok", (p, e, c, i, s, t) -> {
			int rest = ItemElfPurse.extract(player.inventory, needMoney, true);
			if (rest > 0) {
				TalkActionGoTo.goTo("nomoney", chapter, s, i);
				return false;
			}
			ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);
			String address = ElfPostOffice.getAddress(stack);
			if (address.isEmpty()) return false;
			ElfPostOffice postOffice = ElfPostOffice.getPostOffice(player.world);
			stack = stack.splitStack(1);
			stack = postOffice.changeAddressPlate(player, stack);
			ElfPostOffice.addAddressPlateServiceCount(stack, 10);
			ItemHelper.addItemStackToPlayer(player, stack);
			ItemElfPurse.extract(player.inventory, needMoney, false);
			TalkActionGoTo.goTo("applySuccess", c, s, i);
			if (player instanceof EntityPlayerMP)
				ESCriteriaTriggers.ES_TRING.trigger((EntityPlayerMP) player, "post:upgrade");
			return true;
		});
		confirm.addString("say.no", new TalkActionEnd());
		// 没钱
		TalkSceneSay nomoney = new TalkSceneSay("say.nomoney").setLabel("nomoney");
		chapter.addScene(nomoney);
		nomoney.addAction(new TalkActionEnd());
		// 申请成功
		TalkSceneSay applySuccess = new TalkSceneSay("say.apply.address.success").setLabel("applySuccess");
		chapter.addScene(applySuccess);
		applySuccess.addAction(new TalkActionEnd());
	}

	public static void addChapterForAddAddressPlateServiceCount(TalkChapter chapter, EntityElfBase elf,
			EntityPlayer player) {
		// 升级
		chapter.addScene(new TalkSceneSay("#say.add.service.help?200").setLabel("addService"));
		// 确认
		TalkSceneSelect confirm = new TalkSceneSelect();
		chapter.addScene(confirm);
		confirm.addString("say.ok", (p, e, c, i, s, t) -> {
			int rest = ItemElfPurse.extract(player.inventory, 200, true);
			if (rest > 0) {
				TalkActionGoTo.goTo("nomoney", chapter, s, i);
				return false;
			}
			ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);
			String address = ElfPostOffice.getAddress(stack);
			if (address.isEmpty()) return false;
			ElfPostOffice.addAddressPlateServiceCount(stack, 10);
			ItemElfPurse.extract(player.inventory, 200, false);
			TalkActionGoTo.goTo("applySuccess", c, s, i);
			return true;
		});
		confirm.addString("say.no", new TalkActionEnd());
		// 没钱
		TalkSceneSay nomoney = new TalkSceneSay("say.nomoney").setLabel("nomoney");
		chapter.addScene(nomoney);
		nomoney.addAction(new TalkActionEnd());
		// 申请成功
		TalkSceneSay applySuccess = new TalkSceneSay("say.add.service.success").setLabel("applySuccess");
		chapter.addScene(applySuccess);
		applySuccess.addAction(new TalkActionEnd());
	}

	@Override
	public TalkChapter getChapter(EntityElfBase elf, EntityPlayer player, NBTTagCompound shiftData) {
		TalkChapter superChapter = super.getChapter(elf, player, shiftData);
		if (superChapter != null) return superChapter;
		
		TileElfTreeCore core = elf.getEdificeCore();
		if (core == null) return new TalkChapter().addScene(new TalkSceneSay("say.edifice.broken"));
		if (ElfConfig.isSuperDishonest(player))
			return new TalkChapter().addScene(new TalkSceneSay("say.dishonest.not.service"));
		// 切换过来的时候
		if (shiftData != null) {
			boolean isApply = shiftData.getBoolean("apply");
			if (isApply) return getChapterForApplyAddressPlate(elf, player, shiftData);
			return getChapterForSendParcel(elf, player, shiftData);
		}
		TalkChapter chapter = new TalkChapter();
		ItemStack heldItem = player.getHeldItem(EnumHand.MAIN_HAND);
		// 询问帮助
		chapter.addScene(new TalkSceneSay("say.can.help"));
		// 询问要做啥
		TalkSceneSelect what = new TalkSceneSelect();
		chapter.addScene(what);
		what.addString("say.send.parcel", new TalkActionToGui(ESGuiHandler.GUI_ELF_SEND_PARCEL));
		what.addString("say.apply.address.plate", new TalkActionGoTo("pah"));
		// 拿卡的情况
		if (ElfPostOffice.isAddressPlate(heldItem)) {
			if (ElfPostOffice.isVIPAddressPlate(heldItem)) {
				what.addString("say.add.service.plate", new TalkActionGoTo("addService"));
				addChapterForAddAddressPlateServiceCount(chapter, elf, player);
			} else {
				what.addString("say.upgrade.plate", new TalkActionGoTo("upgrade"));
				addChapterForUpgradeAddressPlate(chapter, elf, player);
			}
		}
		what.addString("say.no", new TalkActionEnd());
		// 申请地址说明
		TalkSceneSay applyAddressHelpr = new TalkSceneSay("#say.apply.address.help?500").setLabel("pah");
		applyAddressHelpr.addAction(new TalkActionToGui(ESGuiHandler.GUI_ELF_APPLY_ADDRESS_PLATE));
		chapter.addScene(applyAddressHelpr);
		return chapter;
	}

}

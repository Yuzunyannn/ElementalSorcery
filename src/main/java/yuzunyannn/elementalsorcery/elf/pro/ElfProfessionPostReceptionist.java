package yuzunyannn.elementalsorcery.elf.pro;

import java.util.LinkedList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.container.ESGuiHandler;
import yuzunyannn.elementalsorcery.elf.ElfPostOffice;
import yuzunyannn.elementalsorcery.elf.talk.ITalkAction;
import yuzunyannn.elementalsorcery.elf.talk.TalkActionEnd;
import yuzunyannn.elementalsorcery.elf.talk.TalkActionGoTo;
import yuzunyannn.elementalsorcery.elf.talk.TalkActionToGui;
import yuzunyannn.elementalsorcery.elf.talk.TalkChapter;
import yuzunyannn.elementalsorcery.elf.talk.TalkChapter.Iter;
import yuzunyannn.elementalsorcery.elf.talk.TalkScene;
import yuzunyannn.elementalsorcery.elf.talk.TalkSceneSay;
import yuzunyannn.elementalsorcery.elf.talk.TalkSceneSelect;
import yuzunyannn.elementalsorcery.entity.elf.EntityElfBase;
import yuzunyannn.elementalsorcery.event.EventServer;
import yuzunyannn.elementalsorcery.init.ESInitInstance;
import yuzunyannn.elementalsorcery.item.ItemElfPurse;
import yuzunyannn.elementalsorcery.render.entity.RenderEntityElf;
import yuzunyannn.elementalsorcery.tile.TileElfTreeCore;
import yuzunyannn.elementalsorcery.util.NBTHelper;

public class ElfProfessionPostReceptionist extends ElfProfessionNPCBase {

	@Override
	public void initElf(EntityElfBase elf, ElfProfession origin) {
		super.initElf(elf, origin);
		elf.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(ESInitInstance.ITEMS.ADDRESS_PLATE));
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
		NBTTagCompound playerData = EventServer.getPlayerNBT(player);
		NBTHelper.setItemList(playerData, "elfGuiItemBack", items);
		if (address.isEmpty()) {
			chapter.addScene(new TalkSceneSay("say.submit.empty"));
			return chapter;
		}
		int needMoney = 50 + items.size() * 20;
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
			return true;
		});
		confirm.addString("say.no", new TalkActionEnd());
		// 没钱
		TalkSceneSay nomoney = new TalkSceneSay("say.nomoney").setLabel("nomoney");
		chapter.addScene(nomoney);
		nomoney.addAction(new TalkActionEnd());
		// 支付
		chapter.addScene(new TalkSceneSay("say.as.soon.to.sned").setLabel("finish"));
		return chapter;
	}

	private TalkChapter getChapterForApplyAddressPlate(EntityElfBase elf, EntityPlayer player,
			NBTTagCompound shiftData) {
		TalkChapter chapter = new TalkChapter();
		String address = shiftData.getString("address");
		// 没地址
		if (address.isEmpty()) {
			chapter.addScene(new TalkSceneSay("say.submit.empty"));
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
		player.inventory.addItemStackToInventory(postOffice.createAddressPlate(player, address));
		return chapter;
	}

	@Override
	public TalkChapter getChapter(EntityElfBase elf, EntityPlayer player, NBTTagCompound shiftData) {
		TileElfTreeCore core = elf.getEdificeCore();
		TalkChapter chapter = new TalkChapter();
		if (core == null) {
			chapter.addScene(new TalkSceneSay("say.edifice.broken"));
			return chapter;
		}
		// 切换过来的时候
		if (shiftData != null) {
			boolean isApply = shiftData.getBoolean("apply");
			if (isApply) return getChapterForApplyAddressPlate(elf, player, shiftData);
			return getChapterForSendParcel(elf, player, shiftData);
		}
		// 询问帮助
		chapter.addScene(new TalkSceneSay("say.can.help"));
		// 询问要做啥
		TalkSceneSelect what = new TalkSceneSelect();
		chapter.addScene(what);
		what.addString("say.send.parcel", new TalkActionToGui(ESGuiHandler.GUI_ELF_SEND_PARCEL));
		what.addString("say.apply.address.plate", new TalkActionGoTo("pah"));
		what.addString("say.no", new TalkActionEnd());
		// 申请地址说明
		TalkSceneSay applyAddressHelpr = new TalkSceneSay("#say.apply.address.help?500").setLabel("pah");
		applyAddressHelpr.addAction(new TalkActionToGui(ESGuiHandler.GUI_ELF_APPLY_ADDRESS_PLATE));
		chapter.addScene(applyAddressHelpr);
		return chapter;
	}

	/** 确认支付进行快递 */
	public static class ConfirmSendParcel implements ITalkAction {
		@Override
		public Object invoke(EntityPlayer player, EntityElfBase elf, TalkChapter chapter, Iter iter, TalkScene scene,
				int talkAt) {

			return TalkActionGoTo.goTo("failTakeQuest", chapter, scene, iter);
		}
	}
}

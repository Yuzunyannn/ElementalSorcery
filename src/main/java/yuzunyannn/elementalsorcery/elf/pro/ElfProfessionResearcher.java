package yuzunyannn.elementalsorcery.elf.pro;

import java.util.List;
import java.util.Map.Entry;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.elf.ElfConfig;
import yuzunyannn.elementalsorcery.elf.edifice.EFloorLaboratory;
import yuzunyannn.elementalsorcery.elf.edifice.FloorInfo;
import yuzunyannn.elementalsorcery.elf.research.AncientPaper;
import yuzunyannn.elementalsorcery.elf.research.KnowledgeType;
import yuzunyannn.elementalsorcery.elf.research.Researcher;
import yuzunyannn.elementalsorcery.elf.talk.TalkActionEnd;
import yuzunyannn.elementalsorcery.elf.talk.TalkActionGoTo;
import yuzunyannn.elementalsorcery.elf.talk.TalkChapter;
import yuzunyannn.elementalsorcery.elf.talk.TalkSceneSay;
import yuzunyannn.elementalsorcery.elf.talk.TalkSceneSelect;
import yuzunyannn.elementalsorcery.elf.talk.Talker;
import yuzunyannn.elementalsorcery.entity.elf.EntityElfBase;
import yuzunyannn.elementalsorcery.render.entity.living.RenderEntityElf;
import yuzunyannn.elementalsorcery.tile.TileElfTreeCore;
import yuzunyannn.elementalsorcery.util.helper.NBTHelper;

public class ElfProfessionResearcher extends ElfProfession {

	@Override
	public void initElf(EntityElfBase elf, ElfProfession origin) {
		elf.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, new ItemStack(ESObjects.ITEMS.ANCIENT_PAPER, 1, 1));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ResourceLocation getTexture(EntityElfBase elf) {
		return RenderEntityElf.TEXTURE_RESEARCHER;
	}

	@Override
	public boolean interact(EntityElfBase elf, EntityPlayer player) {
		elf.openTalkGui(player);
		return true;
	}

	public void giveTopicPoint(EntityPlayer player, KnowledgeType type, float rate) {
		rate = rate * (player.world.rand.nextFloat() * 0.5f + 1);
		List<Entry<String, Integer>> entries = type.getTopics();
		for (Entry<String, Integer> entry : entries)
			Researcher.research(player, entry.getKey(), rate * entry.getValue());
	}

	@Override
	public TalkChapter getChapter(EntityElfBase elf, EntityPlayer player, NBTTagCompound shiftData) {
		TalkChapter superChapter = super.getChapter(elf, player, shiftData);
		if (superChapter != null) return superChapter;

		TileElfTreeCore core = elf.getEdificeCore();
		TalkChapter chapter = new TalkChapter();
		if (core == null) return chapter.addScene(new TalkSceneSay("say.edifice.broken"));
		if (ElfConfig.isVeryDishonest(player)) return chapter.addScene(new TalkSceneSay("say.dishonest.not.say"));

		// 第一次
		if (!Researcher.isPlayerResearchable(player)) {
			chapter.addScene(new TalkSceneSay("say.how.research.first"));
			chapter.addScene(new TalkSceneSay("say.how.research.1"));
			TalkSceneSay finScene = new TalkSceneSay("say.how.research.point.reget");
			chapter.addScene(finScene);
			finScene.addAction((p, e, c, i, s, t) -> {
				Researcher.letPlayerResearchable(player);
				return true;
			});
			return chapter;
		}

		ItemStack stack = player.getHeldItemMainhand();

		if (stack.getItem() == ESObjects.ITEMS.ANCIENT_PAPER) {
			AncientPaper ap = new AncientPaper(stack);
			if (ap.isLocked() && ap.getProgress() <= 0) {
				chapter.addScene(new TalkSceneSay("say.want.paper"));
				// 确认
				TalkSceneSelect confirm = new TalkSceneSelect();
				chapter.addScene(confirm);
				confirm.addString("say.ok", new TalkActionGoTo("whenOK"));
				confirm.addString("say.no", new TalkActionEnd());
				// 对话
				TalkSceneSay wok = new TalkSceneSay().setLabel("whenOK");
				chapter.addScene(wok);
				wok.addString("@#$@#%%^!@$!%%@$!@#!$%%!", Talker.OPPOSING);
				wok.addString("say.scholar.konw", Talker.PLAYER);
				wok.addAction((p, e, c, i, s, t) -> {
					stack.shrink(1);
					KnowledgeType type = ap.getType();
					giveTopicPoint(player, type, (ap.getEnd() - ap.getStart()) / 100.0f);
					return true;
				});
				return chapter;
			}
		}

		chapter.addScene(new TalkSceneSay("say.how.research.1"));
		return chapter;
	}

	@Override
	public void tick(EntityElfBase elf) {
		if (elf.world.isRemote) return;
		if (elf.tick % 200 != 0) return;
		TileElfTreeCore core = elf.getEdificeCore();
		if (core == null) return;
		NBTTagCompound nbt = elf.getEntityData();
		BlockPos table;
		if (NBTHelper.hasBlockPos(nbt, "sTable")) table = NBTHelper.getBlockPos(nbt, "sTable");
		else {
			FloorInfo info = core.getFloor(EFloorLaboratory.class);
			if (info == null) {
				ESAPI.logger.warn("精灵研究者找不到研究室了！");
				elf.setProfession(ElfProfession.NONE);
				return;
			}
			NBTTagCompound data = info.getFloorData();
			table = NBTHelper.getBlockPos(data, "sTable");
			NBTHelper.setBlockPos(nbt, "sTable", table);
		}
		IBlockState state = elf.world.getBlockState(table);
		if (state.getBlock() != ESObjects.BLOCKS.RESEARCHER) {
			if (table.distanceSq(elf.getPosition()) <= 3 * 3) {
				elf.tryHarvestBlock(table);
				elf.tryPlaceBlock(table, ESObjects.BLOCKS.RESEARCHER.getDefaultState());
			}
		}
		if (!elf.getNavigator().noPath()) return;
		Vec3d vec = new Vec3d(table.offset(EnumFacing.HORIZONTALS[elf.getRNG().nextInt(4)]));
		elf.getNavigator().tryMoveToXYZ(vec.x + 0.5, vec.y + 0.1, vec.z + 0.5, 1);
	}

}

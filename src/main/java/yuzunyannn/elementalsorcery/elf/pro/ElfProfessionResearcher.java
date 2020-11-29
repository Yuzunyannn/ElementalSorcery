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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.ElementalSorcery;
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
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.item.ItemAncientPaper;
import yuzunyannn.elementalsorcery.render.entity.RenderEntityElf;
import yuzunyannn.elementalsorcery.tile.TileElfTreeCore;
import yuzunyannn.elementalsorcery.util.NBTHelper;

public class ElfProfessionResearcher extends ElfProfession {

	@Override
	public void initElf(EntityElfBase elf, ElfProfession origin) {
		elf.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, new ItemStack(ESInit.ITEMS.ANCIENT_PAPER, 1, 1));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ResourceLocation getTexture(EntityElfBase elf) {
		return RenderEntityElf.TEXTURE_RESEARCHER;
	}

	@Override
	public boolean interact(EntityElfBase elf, EntityPlayer player) {
		openTalkGui(player, elf);
		return true;
	}

	public void giveTopicPoint(EntityPlayer player, KnowledgeType type, float rate) {
		rate = rate * (player.world.rand.nextFloat() * 0.5f + 1);
		List<Entry<String, Integer>> entries = type.getTopics();
		Researcher researcher = new Researcher(player);
		for (Entry<String, Integer> entry : entries) {
			researcher.grow(entry.getKey(), MathHelper.ceil(rate * entry.getValue()));
			ItemAncientPaper.sendTopicGrowMessage(player, entry.getKey());
		}
		researcher.save(player);
	}

	@Override
	public TalkChapter getChapter(EntityElfBase elf, EntityPlayer player, NBTTagCompound shiftData) {
		TileElfTreeCore core = elf.getEdificeCore();
		TalkChapter chapter = new TalkChapter();
		if (core == null) {
			chapter.addScene(new TalkSceneSay("say.edifice.broken"));
			return chapter;
		}
		ItemStack stack = player.getHeldItemMainhand();
		if (stack.getItem() == ESInit.ITEMS.ANCIENT_PAPER) {
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
		chapter.addScene(new TalkSceneSay("say.how.research"));
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
				ElementalSorcery.logger.warn("精灵研究者找不到研究室了！");
				elf.setProfession(ElfProfession.NONE);
				return;
			}
			NBTTagCompound data = info.getFloorData();
			table = NBTHelper.getBlockPos(data, "sTable");
			NBTHelper.setBlockPos(nbt, "sTable", table);
		}
		IBlockState state = elf.world.getBlockState(table);
		if (state.getBlock() != ESInit.BLOCKS.RESEARCHER) {
			if (table.distanceSq(elf.getPosition()) <= 3 * 3) {
				elf.tryHarvestBlock(table);
				elf.tryPlaceBlock(table, ESInit.BLOCKS.RESEARCHER.getDefaultState());
			}
		}
		if (!elf.getNavigator().noPath()) return;
		Vec3d vec = new Vec3d(table.offset(EnumFacing.HORIZONTALS[elf.getRNG().nextInt(4)]));
		elf.getNavigator().tryMoveToXYZ(vec.x + 0.5, vec.y + 0.1, vec.z + 0.5, 1);
	}

}

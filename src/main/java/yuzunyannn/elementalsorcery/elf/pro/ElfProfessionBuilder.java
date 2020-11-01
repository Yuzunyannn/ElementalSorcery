package yuzunyannn.elementalsorcery.elf.pro;

import java.util.Map.Entry;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.block.BlockElfLog;
import yuzunyannn.elementalsorcery.elf.edifice.BuildProgress;
import yuzunyannn.elementalsorcery.elf.talk.ITalkAction;
import yuzunyannn.elementalsorcery.elf.talk.TalkActionCoin;
import yuzunyannn.elementalsorcery.elf.talk.TalkActionEnd;
import yuzunyannn.elementalsorcery.elf.talk.TalkActionGoTo;
import yuzunyannn.elementalsorcery.elf.talk.TalkActionIf;
import yuzunyannn.elementalsorcery.elf.talk.TalkActionMulti;
import yuzunyannn.elementalsorcery.elf.talk.TalkChapter;
import yuzunyannn.elementalsorcery.elf.talk.TalkSceneSay;
import yuzunyannn.elementalsorcery.elf.talk.TalkSceneSelect;
import yuzunyannn.elementalsorcery.elf.talk.Talker;
import yuzunyannn.elementalsorcery.entity.elf.EntityElfBase;
import yuzunyannn.elementalsorcery.init.ESInitInstance;
import yuzunyannn.elementalsorcery.render.entity.RenderEntityElf;
import yuzunyannn.elementalsorcery.tile.TileElfTreeCore;
import yuzunyannn.elementalsorcery.util.NBTHelper;

public class ElfProfessionBuilder extends ElfProfessionNone {

	@Override
	public void initElf(EntityElfBase elf, ElfProfession origin) {
		elf.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, new ItemStack(Blocks.CRAFTING_TABLE));
	}

	@Override
	public void transferElf(EntityElfBase elf, ElfProfession next) {
	}

	@Override
	public boolean needPickup(EntityElfBase elf, ItemStack stack) {
		Item item = stack.getItem();
		Block block = Block.getBlockFromItem(item);
		return item == Item.getItemFromBlock(ESInitInstance.BLOCKS.ELF_FRUIT) || item == ESInitInstance.ITEMS.ELF_COIN
				|| block instanceof BlockElfLog;
	}

	@Override
	public boolean canEquip(EntityElfBase elf, ItemStack stack, EntityEquipmentSlot slot) {
		return false;
	}

	@Override
	public boolean interact(EntityElfBase elf, EntityPlayer player) {
		openTalkGui(player, elf);
		return true;
	}

	@Override
	public TalkChapter getChapter(EntityElfBase elf, EntityPlayer player, NBTTagCompound shiftData) {
		// 没有核心
		if (elf.getEdificeCore() == null) {
			TalkChapter chapter = new TalkChapter();
			chapter.addScene(new TalkSceneSay("say.edifice.broken"));
			return chapter;
		}
		// 其他情况
		NBTTagCompound data = elf.getEntityData();
		int flags = data.getInteger("flags");
		if (flags == 0) return null;
		TalkChapter chapter = new TalkChapter();
		if ((flags & FLAG_WORKING) != 0) {
			TalkSceneSay scene = new TalkSceneSay();
			chapter.addScene(scene);
			scene.addString("say.builder.working", Talker.OPPOSING);
		} else if ((flags & FLAG_HOPE) != 0) {
			TalkSceneSay scene = new TalkSceneSay();
			chapter.addScene(scene);
			scene.addString("say.builder.hope", Talker.OPPOSING);
			scene.addString("say.builder.help", Talker.PLAYER);
			scene.addString("say.builder.need", Talker.OPPOSING);

			TalkSceneSelect scene2 = new TalkSceneSelect();
			chapter.addScene(scene2);
			ITalkAction ok = new TalkActionMulti(new TalkActionGoTo("ok"), (a, b, c, d, e, f) -> {
				data.setInteger("flags", flags | FLAG_WORKING);
				elf.clearTempNBT();
				return true;
			});
			ITalkAction action = new TalkActionIf(new TalkActionCoin(false, 500), ok, new TalkActionGoTo("nomoney"));
			scene2.addString("say.builder.ok", action);
			scene2.addString("say.builder.no", new TalkActionEnd());

			TalkSceneSay scene3 = new TalkSceneSay().setLabel("ok");
			chapter.addScene(scene3);
			scene3.addString("say.very.thank", Talker.OPPOSING);
			scene3.addAction(new TalkActionEnd());

			TalkSceneSay scene4 = new TalkSceneSay().setLabel("nomoney");
			chapter.addScene(scene4);
			scene4.addString("say.nomoney", Talker.OPPOSING);
		}
		return chapter;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ResourceLocation getTexture(EntityElfBase elf) {
		return RenderEntityElf.TEXTURE_BUILDER;
	}

	public static final int FLAG_WORKING = 0x1;
	public static final int FLAG_HOPE = 0x2;

	public boolean hasTag(int flags, int tag) {
		return (flags & FLAG_WORKING) != 0;
	}

	@Override
	public void tick(EntityElfBase elf) {
		World world = elf.world;
		if (world.isRemote) return;
		if (elf.tick % 2 != 0) return;
		Random rand = elf.getRNG();
		TileElfTreeCore core = elf.getEdificeCore();
		if (core == null) return;
		NBTTagCompound data = elf.getEntityData();
		int flags = data.getInteger("flags");
		if (hasTag(flags, FLAG_WORKING)) onWork(core, elf);
		// 没工作的情况下， 隔一定时间检查一次
		if (elf.tick % 100 != 0) return;
		if (rand.nextInt(5) != 0) return;
		if (core.getFloorCount() == 0) {
			flags |= FLAG_HOPE;
			data.setInteger("flags", flags);
			return;
		}
		flags &= ~FLAG_HOPE;
		data.setInteger("flags", flags);
		int task = core.applyBuildTask(true);
		if (task == -1) return;
		data.setInteger("flags", flags | FLAG_WORKING);
	}

	/** 清除工作标签 */
	private void clearWork(EntityElfBase elf) {
		NBTTagCompound data = elf.getEntityData();
		int flags = data.getInteger("flags");
		data.setInteger("flags", flags & ~FLAG_WORKING);
		elf.clearTempNBT();
		elf.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);
	}

	private void onWork(TileElfTreeCore core, EntityElfBase elf) {
		NBTTagCompound cache = elf.getTempNBT();
		if (cache.hasKey("do")) {
			onRealWork(core, elf);
			return;
		}
		// 检测是否有目标地点
		if (NBTHelper.hasBlockPos(cache, "to")) {
			if (!elf.getNavigator().noPath()) return;
			BlockPos to = NBTHelper.getBlockPos(cache, "to");
			BlockPos elfPos = elf.getPosition();
			if (Math.abs(to.getX() - elfPos.getX()) <= 2 && Math.abs(to.getZ() - elfPos.getZ()) <= 2) {
				// 到达了中心，进行任务获取
				int id = core.applyBuildTask(false);
				if (id == -1) clearWork(elf);
				else {
					cache.setInteger("do", id);
					int high = core.getFloorHigh(id - 1);
					to = core.getTreeBasicPos();
					to = to.add(0, high, 0);
					elf.tryPlaceBlock(to, Blocks.DIRT.getDefaultState());
					to = to.up();
					elf.setPosition(to.getX() + 0.5, to.getY(), to.getZ() + 0.5);
				}
			} else tryMoveTo(core, elf, to);
		} else {
			BlockPos to = core.getTreeBasicPos();
			NBTHelper.setBlockPos(cache, "to", to);
		}
	}

	private void tryMoveTo(TileElfTreeCore core, EntityElfBase elf, BlockPos pos) {
		World world = elf.world;
		BlockPos elfPos = elf.getPosition();
		NBTTagCompound cache = elf.getTempNBT();
		// 尝试走到中心
		Vec3d at = elf.getPositionVector().addVector(0, 1, 0);
		Vec3d tar = new Vec3d(pos).subtract(at).normalize().scale(1.1);
		BlockPos to = new BlockPos(at.add(tar));
		int r = elf.getRNG().nextInt(8);
		int walkTick = cache.getInteger("wt");
		cache.setInteger("wt", walkTick + 1);
		if (world.isAirBlock(to) && r != 0 && walkTick < 600) {
			if (!elf.getNavigator().noPath()) return;
			elf.getNavigator().tryMoveToXYZ(pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5, 1.5);
		} else {
			if (pos.getY() + 1 > elf.posY && world.isAirBlock(elfPos.up(2))) {
				elf.getJumpHelper().setJumping();
				elf.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Blocks.DIRT));
				BlockPos down = elf.getPosition().down();
				elf.tryPlaceBlock(down, Blocks.DIRT.getDefaultState());
			} else {
				if (to.equals(elfPos.down())) return;
				if (!world.isAirBlock(to)) {
					// 走不了了，用魔法书传送了
					Random rand = elf.getRNG();
					r = rand.nextInt(20);
					if (r == 0) {
						elf.setItemStackToSlot(EntityEquipmentSlot.MAINHAND,
								new ItemStack(ESInitInstance.ITEMS.SPELLBOOK_ELEMENT));
						elf.setDropChance(EntityEquipmentSlot.MAINHAND, 0);
					}
					ItemStack hold = elf.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
					if (hold.getItem() == ESInitInstance.ITEMS.SPELLBOOK_ELEMENT) {
						r = rand.nextInt(50);
						if (r == 0) {
							pos = pos.add(rand.nextInt(4) - 2, 0, rand.nextInt(4) - 2);
							elf.swingArm(EnumHand.MAIN_HAND);
							elf.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
						}
					}
				}
			}
		}
	}

	private void onRealWork(TileElfTreeCore core, EntityElfBase elf) {
		if (elf.tick % 10 != 0) return;
		NBTTagCompound cache = elf.getTempNBT();
		World world = elf.world;
		// 开始工作
		int id = cache.getInteger("do");
		boolean fastMode = false;
		for (int i = 0; i < 100; i++) {
			BuildProgress progress = core.getBuildTask(id);
			if (progress == null || progress.isFinish()) {
				if (progress != null && progress.isFinish()) core.notifyComplete(id);
				clearWork(elf);
				return;
			}
			Entry<BlockPos, IBlockState> entry = progress.next();
			if (entry == null) return;
			if (world.getBlockState(entry.getKey()) == entry.getValue()) {
				elf.tick--;
				return;
			}
			elf.tryHarvestBlock(entry.getKey());
			elf.tryPlaceBlock(entry.getKey(), entry.getValue());
			if (!fastMode) return;
		}

	}
}

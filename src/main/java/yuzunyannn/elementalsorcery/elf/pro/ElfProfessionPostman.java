package yuzunyannn.elementalsorcery.elf.pro;

import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;
import yuzunyannn.elementalsorcery.container.ESGuiHandler;
import yuzunyannn.elementalsorcery.elf.ElfPostOffice;
import yuzunyannn.elementalsorcery.elf.talk.TalkActionToGui;
import yuzunyannn.elementalsorcery.elf.talk.TalkChapter;
import yuzunyannn.elementalsorcery.elf.talk.TalkSceneSay;
import yuzunyannn.elementalsorcery.elf.talk.Talker;
import yuzunyannn.elementalsorcery.entity.elf.EntityElfBase;
import yuzunyannn.elementalsorcery.init.ESInitInstance;
import yuzunyannn.elementalsorcery.render.entity.RenderEntityElf;
import yuzunyannn.elementalsorcery.util.NBTHelper;
import yuzunyannn.elementalsorcery.util.NBTTag;
import yuzunyannn.elementalsorcery.util.RandomHelper;
import yuzunyannn.elementalsorcery.util.block.BlockHelper;

public class ElfProfessionPostman extends ElfProfessionNone {

	@Override
	public void initElf(EntityElfBase elf, ElfProfession origin) {
		elf.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(ESInitInstance.ITEMS.PARCEL));
		elf.world.playSound((EntityPlayer) null, elf.posX, elf.posY, elf.posZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT,
				SoundCategory.HOSTILE, 1.0F, 1.0F);
		if (elf.world.isRemote) {
			for (int i = 0; i < 32; ++i) {
				elf.world.spawnParticle(EnumParticleTypes.PORTAL, elf.posX,
						elf.posY + RandomHelper.rand.nextDouble() * 2.0D, elf.posZ, RandomHelper.rand.nextGaussian(),
						0.0D, RandomHelper.rand.nextGaussian());
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ResourceLocation getTexture(EntityElfBase elf) {
		return RenderEntityElf.TEXTURE_POSTMAN;
	}

	@Override
	public boolean interact(EntityElfBase elf, EntityPlayer player) {
		openTalkGui(player, elf);
		return true;
	}

	@Override
	public TalkChapter getChapter(EntityElfBase elf, EntityPlayer player, NBTTagCompound shiftData) {
		if (shiftData != null) return ElfProfessionPostReceptionist.getChapterForSendParcel(elf, player, shiftData);
		TalkChapter chapter = new TalkChapter();
		TalkSceneSay scene = new TalkSceneSay();
		chapter.addScene(scene);
		scene.addString("#say.i.want?say.send.parcel", Talker.PLAYER);
		scene.addAction(new TalkActionToGui(ESGuiHandler.GUI_ELF_SEND_PARCEL));
		return chapter;
	}

	@Override
	public void tick(EntityElfBase elf) {
		if (elf.world.isRemote) return;
		if (elf.tick % 20 != 0) return;
		this.tryGoBack(elf);
		NBTTagCompound nbt = elf.getEntityData();
		if (!nbt.hasKey("address", NBTTag.TAG_STRING)) return;
		String address = nbt.getString("address");
		if (address.isEmpty()) return;
		BlockPos pos = NBTHelper.getBlockPos(nbt, "chest");
		if (pos.distanceSq(elf.getPosition()) > 8 * 8) return;
		// 走过去
		PathNavigate navi = elf.getNavigator();
		if (navi.noPath() && elf.getTalker() == null) {
			int x = elf.getRNG().nextInt(3) - 1 + pos.getX();
			int z = elf.getRNG().nextInt(3) - 1 + pos.getZ();
			elf.getNavigator().tryMoveToXYZ(x, pos.getY(), z, 1);
		}
		if (pos.distanceSq(elf.getPosition()) > 3 * 3) return;
		IItemHandler inv = BlockHelper.getItemHandler(elf.world, pos, null);
		if (inv == null) return;
		// 放包裹
		ElfPostOffice postOffice = ElfPostOffice.getPostOffice(elf.world);
		ItemStack parcel = postOffice.popParcel(address);
		if (parcel.isEmpty()) return;
		elf.swingArm(EnumHand.MAIN_HAND);
		parcel = BlockHelper.insertInto(inv, parcel);
		if (parcel.isEmpty()) return;
		postOffice.pushParcel(address, parcel);
	}

	public void tryGoBack(EntityElfBase elf) {
		if (elf.getTalker() != null) return;
		int tick = elf.tick;
		Random rand = elf.getRNG();
		if (tick / 20 > rand.nextInt(100)) this.setGoBack(elf);
	}

	public void setGoBack(EntityElfBase elf) {
		elf.setDead();
		elf.world.playSound((EntityPlayer) null, elf.posX, elf.posY, elf.posZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT,
				SoundCategory.HOSTILE, 1.0F, 1.0F);
	}
}

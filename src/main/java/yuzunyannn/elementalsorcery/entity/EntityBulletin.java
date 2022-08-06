package yuzunyannn.elementalsorcery.entity;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.NBTTag;
import yuzunyannn.elementalsorcery.elf.quest.Quest;
import yuzunyannn.elementalsorcery.elf.quest.QuestStatus;
import yuzunyannn.elementalsorcery.item.ItemQuest;
import yuzunyannn.elementalsorcery.network.MessageEntitySync;

public class EntityBulletin extends EntityHanging implements IEntityAdditionalSpawnData, MessageEntitySync.IRecvData {

	/** 记录任务的类 */
	public class QuestInfo implements INBTSerializable<NBTTagCompound> {
		protected Quest quest;
		protected float x;
		protected float y;

		public QuestInfo(Quest quest) {
			this.quest = quest;
		}

		public QuestInfo(NBTTagCompound tag) {
			this.deserializeNBT(tag);
			x = tag.getFloat("px");
			y = tag.getFloat("py");
		}

		@Override
		public NBTTagCompound serializeNBT() {
			NBTTagCompound nbt = quest.serializeNBT();
			nbt.setFloat("px", x);
			nbt.setFloat("py", y);
			return nbt;
		}

		public float getX() {
			return x;
		}

		public float getY() {
			return y;
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt) {
			quest = new Quest(nbt);
		}

		public Quest getQuest() {
			return quest;
		}
	}

	/** 任务队列，越往前，越靠前 */
	protected LinkedList<QuestInfo> quests = new LinkedList<>();

	public EntityBulletin(World worldIn) {
		super(worldIn);
		this.setEntityInvulnerable(true);
	}

	public void setPosition(BlockPos pos, EnumFacing facing) {
		posX = pos.getX();
		posY = pos.getX();
		posZ = pos.getX();
		hangingPosition = pos;
		this.updateFacingWithBoundingBox(facing);
	}

	@Override
	public int getWidthPixels() {
		return 16 * 5;
	}

	@Override
	public int getHeightPixels() {
		return 16 * 3;
	}

	public static final int MODEL_FRAME_SIZE = 3;
	public static final int MODEL_HEIGHT = 18;
	public static final int MODEL_WIDTH = 30;
	public static final int MODEL_QUEST_SIZE = 12;

	@Override
	@SideOnly(Side.CLIENT)
	public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch,
			int posRotationIncrements, boolean teleport) {
		BlockPos blockpos = this.hangingPosition.add(x - this.posX, y - this.posY, z - this.posZ);
		this.setPosition((double) blockpos.getX(), (double) blockpos.getY(), (double) blockpos.getZ());
	}

	@Override
	public void setLocationAndAngles(double x, double y, double z, float yaw, float pitch) {
		this.setPosition(x, y, z);
	}

	@Override
	public void onBroken(Entity brokenEntity) {

	}

	@Override
	public void playPlaceSound() {
		this.playSound(SoundEvents.ENTITY_PAINTING_PLACE, 1.0F, 1.0F);
	}

	public List<QuestInfo> getQuests() {
		return quests;
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		NBTTagList list = new NBTTagList();
		for (QuestInfo info : quests) list.appendTag(info.serializeNBT());
		compound.setTag("quest", list);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		quests.clear();
		NBTTagList list = compound.getTagList("quest", NBTTag.TAG_COMPOUND);
		for (NBTBase base : list) quests.add(new QuestInfo((NBTTagCompound) base));
	}

	@Override
	public void writeSpawnData(ByteBuf buffer) {
		NBTTagCompound tag = new NBTTagCompound();
		this.writeEntityToNBT(tag);
		ByteBufUtils.writeTag(buffer, tag);
	}

	@Override
	public void readSpawnData(ByteBuf additionalData) {
		this.readEntityFromNBT(ByteBufUtils.readTag(additionalData));
	}

	public float getValueWidth() {
		float w = MODEL_FRAME_SIZE * getWidthPixels() / MODEL_WIDTH;
		return (this.getWidthPixels() - w * 2) / 16f;
	}

	public float getValueHeight() {
		float h = MODEL_FRAME_SIZE * getHeightPixels() / MODEL_HEIGHT;
		return (this.getHeightPixels() - h * 2) / 16f;
	}

	@Override
	public EnumActionResult applyPlayerInteraction(EntityPlayer player, Vec3d vec, EnumHand hand) {
		double x, y;
		y = vec.y;
		switch (this.facingDirection) {
		case SOUTH:
			x = vec.x;
			break;
		case EAST:
			x = -vec.z;
			break;
		case WEST:
			x = vec.z;
			break;
		default:
			x = -vec.x;
		}
		float width = this.getValueWidth();
		float height = this.getValueHeight();
		if (Math.abs(x) >= width / 2 || Math.abs(y) >= height / 2) return EnumActionResult.PASS;
		ItemStack stack = player.getHeldItem(hand);
		x += width / 2;
		y = -(y - height / 2);
		if (world.isRemote) return EnumActionResult.SUCCESS;
		// 手上没有物品，开始查看是否可以摘下来某个
		if (stack.isEmpty()) tryTakeOff(x, y, player);
		else if (ItemQuest.isQuest(stack)) tryPutUp(x, y, stack);
		return EnumActionResult.SUCCESS;
	}

	private void tryPutUp(double x, double y, ItemStack stack) {
		if (quests.size() > 20) return;
		Quest quest = ItemQuest.getQuest(stack);
		if (quest.getStatus() != QuestStatus.NONE) return;
		stack.shrink(1);
		putUp((float) x, (float) y, quest);
	}

	protected void putUp(float x, float y, Quest quest) {
		// 约束位置
		float w = MODEL_QUEST_SIZE / 16.0f;
		float h = w * 1.05797f;
		x = x - w / 2;
		y = y - h / 2;
		x = MathHelper.clamp(x, 0, this.getValueWidth() - w);
		y = MathHelper.clamp(y, 0, this.getValueHeight() - h);
		// 添加
		QuestInfo info = new QuestInfo(quest);
		info.x = x;
		info.y = y;
		quests.addFirst(info);
		if (world.isRemote) return;
		// 发送
		NBTTagCompound data = new NBTTagCompound();
		data.setByte("type", (byte) 2);
		data.setTag("info", info.serializeNBT());
		MessageEntitySync.sendToClient(this, data);
	}

	private void tryTakeOff(double x, double y, EntityPlayer player) {
		float w = MODEL_QUEST_SIZE / 16.0f;
		float h = w * 1.05797f;
		int i = -1;
		Iterator<QuestInfo> iter = quests.iterator();
		while (iter.hasNext()) {
			i++;
			QuestInfo info = iter.next();
			float qx = info.getX();
			float qy = info.getY();
			// 左上角为0，0，向下为正，向右为正
			if (x < qx || y < qy) continue;
			if (x > qx + w || y > qy + h) continue;
			takeOff(i, player);
			return;
		}
	}

	protected void takeOff(int index, @Nonnull EntityPlayer player) {
		if (index < 0 || index >= quests.size()) return;
		QuestInfo info = quests.get(index);
		quests.remove(index);
		if (world.isRemote) return;
		// 摘取
		if (player != null) {
			Quest quest = info.getQuest();
			ItemStack stack = ItemQuest.createQuest(quest);
			if (player.getHeldItemMainhand().isEmpty()) player.setHeldItem(EnumHand.MAIN_HAND, stack);
			else player.inventory.addItemStackToInventory(stack);
		}
		// 发送
		NBTTagCompound data = new NBTTagCompound();
		data.setByte("type", (byte) 1);
		data.setInteger("index", index);
		MessageEntitySync.sendToClient(this, data);
	}

	@Override
	public void onRecv(NBTTagCompound data) {
		int type = data.getByte("type");
		switch (type) {
		case 1:
			int index = data.getInteger("index");
			this.takeOff(index, null);
			break;
		case 2:
			QuestInfo info = new QuestInfo(data.getCompoundTag("info"));
			quests.addFirst(info);
		default:
			break;
		}
	}

	/** 添加一个任务 */
	public void addQuest(Quest quest) {
		float w = this.getValueWidth();
		float h = this.getValueHeight();
		this.putUp(rand.nextFloat() * w, rand.nextFloat() * h, quest);
	}

	/** 删除一个任务 */
	public void removeQuest(int index) {
		this.takeOff(index, null);
	}

	public int getQuestCount() {
		return this.quests.size();
	}

}

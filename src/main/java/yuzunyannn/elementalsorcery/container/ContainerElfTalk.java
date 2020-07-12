package yuzunyannn.elementalsorcery.container;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.elf.talk.TalkChapter;
import yuzunyannn.elementalsorcery.entity.elf.EntityElfBase;
import yuzunyannn.elementalsorcery.event.EventServer;
import yuzunyannn.elementalsorcery.event.ITickTask;
import yuzunyannn.elementalsorcery.network.MessageSyncContainer.IContainerNetwork;

public class ContainerElfTalk extends Container implements IContainerNetwork {
	public EntityElfBase elf;
	public final EntityPlayer player;
	public final BlockPos pos;
	protected TalkChapter chapter;
	protected TalkChapter.Iter iter;
	protected boolean noEnd = true;

	public ContainerElfTalk(EntityPlayer player) {
		this.player = player;
		NBTTagCompound nbt = ElementalSorcery.getPlayerData(player);
		Entity elf = (EntityElfBase) player.world.getEntityByID(nbt.getInteger("elfId"));
		if (elf instanceof EntityElfBase) this.elf = (EntityElfBase) elf;
		else this.elf = null;
		if (this.elf != null) {
			this.elf.setTalker(player);
			if (!player.world.isRemote) {
				// 推后一帧
				EventServer.addTickTask(() -> {
					this.setChapter(this.elf.getProfession().getChapter(this.elf, player));
					return ITickTask.END;
				});
			}
		}
		this.pos = this.elf == null ? player.getPosition() : elf.getPosition();
	}

	@Override
	public void onContainerClosed(EntityPlayer playerIn) {
		super.onContainerClosed(playerIn);
		if (this.elf != null) this.elf.setTalker(null);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return noEnd && (elf == null ? playerIn.getDistanceSq(pos) <= 64 : playerIn.getDistanceSq(this.elf) <= 64);
	}

	public TalkChapter.Iter getChapterIter() {
		return iter;
	}

	public void setChapter(TalkChapter chapterIn) {
		if (chapter == chapterIn) return;
		chapter = chapterIn;
		if (chapter == null) return;
		iter = chapter.createIter();
		if (player.world.isRemote) return;
		NBTTagCompound nbt = chapter.serializeNBTToSend();
		if (elf != null) nbt.setInteger("elfId", elf.getEntityId());
		this.sendToClient(nbt, player);
	}

	public int toOrPassIndex(int index, int selectAt) {
		if (player.world.isRemote) {
			if (iter != null) iter.setIndex(index);
			return 0;
		}
		// 没有内容直接结束
		if (iter == null) return setEnd();
		// 计算下一个内容
		int serIndex = iter.getIndex();
		if (serIndex > index) return serIndex;
		for (; serIndex < index; serIndex++) {
			// 如果是核心點，需要重新看
			if (iter.isPointScene()) return iter.getIndex();
			// 向后移动
			else if (iter.hasNextScene()) {
				iter.dealAction(-1);// 这里的deal应为不是point所以不应当出现转跳的情况
				iter.nextScene();
			} else return setEnd();
		}
		int lastIndex = iter.getIndex();
		if (iter.isPointScene()) {
			TalkChapter newChapter = iter.dealAction(selectAt);
			// 新的章节就切换
			if (newChapter != null && this.chapter != newChapter) {
				this.setChapter(newChapter);
				return -1;
			}
		}
		// 这里的动作可能发生跳转，所以检测是否改变，如果没改变则自动下移
		if (lastIndex == iter.getIndex()) {
			if (iter.hasNextScene()) iter.nextScene();
			else return setEnd();
		} else if (iter.isEnd()) return setEnd();
		// 返回当前应该去的位置
		return iter.getIndex();
	}

	private int setEnd() {
		noEnd = false;
		return -1;
	}

	@SideOnly(Side.CLIENT)
	public void sendToServer(int nowIndex, int select) {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("to", nowIndex);
		nbt.setInteger("select", select);
		this.sendToServer(nbt);
	}

	@Override
	public void recvData(NBTTagCompound nbt, Side side) {
		if (side == Side.CLIENT) {
			if (nbt.hasKey("scenes")) setChapter(new TalkChapter().deserializeNBTFromSend(nbt));
			if (nbt.hasKey("to")) toOrPassIndex(nbt.getInteger("to"), 0);
		} else {
			int ret = toOrPassIndex(nbt.getInteger("to"), nbt.getInteger("select"));
			if (ret >= 0) {
				nbt = new NBTTagCompound();
				nbt.setInteger("to", ret);
				this.sendToClient(nbt, player);
			}
		}
	}
}

package yuzunyannn.elementalsorcery.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.relauncher.Side;
import yuzunyannn.elementalsorcery.capability.Adventurer;
import yuzunyannn.elementalsorcery.elf.quest.IAdventurer;
import yuzunyannn.elementalsorcery.elf.quest.Quest;
import yuzunyannn.elementalsorcery.event.EventServer;
import yuzunyannn.elementalsorcery.item.ItemQuest;
import yuzunyannn.elementalsorcery.network.MessageSyncContainer.IContainerNetwork;

public class ContainerQuest extends Container implements IContainerNetwork {
	/** 是否关闭 */
	protected boolean isEnd = true;
	/** 玩家 */
	public final EntityPlayer player;
	/** 任务 */
	protected Quest quest;

	public ContainerQuest(EntityPlayer player) {
		this.player = player;
		ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);
		if (ItemQuest.isQuest(stack)) quest = ItemQuest.getQuest(stack);
		else {
			stack = player.getHeldItem(EnumHand.OFF_HAND);
			if (ItemQuest.isQuest(stack)) quest = ItemQuest.getQuest(stack);
		}
		if (quest == null) {
			isEnd = true;
			return;
		}
		if (player.world.isRemote) {
			quest.getType().checkPre(quest, player);
			quest.getType().check(quest, player);
			return;
		}

		// 获取一些需要同步的数据，如果有就将进行数据给客户端，进行显示
		Quest q = quest;
		int i = quest.findSignQuest(player);
		if (i != -1) {
			IAdventurer adventurer = player.getCapability(Adventurer.ADVENTURER_CAPABILITY, null);
			q = adventurer.getQuest(i);
		}

		NBTTagCompound nbt = new NBTTagCompound();
		q.getType().openContainerSync(q, player, nbt);

		NBTTagCompound data = q.getData().getNBT();
		if (!data.isEmpty()) nbt.setTag("$$", data);

		if (nbt.isEmpty()) return;

		EventServer.addTask(() -> {
			this.sendToClient(nbt, player);
		});
	}

	@Override
	public void recvData(NBTTagCompound nbt, Side side) {
		if (side == Side.SERVER) return;
		if (quest != null) {
			quest.getType().openContainerSyncRecv(quest, player, nbt);
			quest.updateData(nbt.getCompoundTag("$$"));
			quest.getType().check(quest, player);
		}
	}

	public Quest getQuest() {
		return quest;
	}

	@Override
	public void onContainerClosed(EntityPlayer playerIn) {
		super.onContainerClosed(playerIn);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return isEnd;
	}

}

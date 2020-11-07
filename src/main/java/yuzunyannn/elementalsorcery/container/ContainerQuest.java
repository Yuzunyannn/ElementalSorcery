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
		// 如果是当前玩家正在进行，就将进行数据给客户端，进行显示
		int i = quest.findSignQuest(player);
		if (i == -1) return;
		IAdventurer adventurer = player.getCapability(Adventurer.ADVENTURER_CAPABILITY, null);
		Quest q = adventurer.getQuest(i);
		EventServer.addTask(() -> {
			this.sendToClient(q.getData().getNBT(), player);
		});
	}

	@Override
	public void recvData(NBTTagCompound nbt, Side side) {
		if (side == Side.SERVER) return;
		if (quest != null) {
			quest.updateData(nbt);
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

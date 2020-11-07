package yuzunyannn.elementalsorcery.elf.quest;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public class QuestTriggerDataSendParcel {

	public final EntityLivingBase player;
	public final String address;
	public final List<ItemStack> items;

	public QuestTriggerDataSendParcel(EntityLivingBase player, String address, List<ItemStack> items) {
		this.player = player;
		this.address = address;
		this.items = items;
	}

}

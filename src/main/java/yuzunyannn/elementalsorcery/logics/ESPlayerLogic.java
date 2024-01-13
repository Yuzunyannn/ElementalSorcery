package yuzunyannn.elementalsorcery.logics;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.item.ItemManual;

public class ESPlayerLogic {

	public static void onPlayerFirstJoinInWorld(EntityPlayerMP player) {
		NBTTagList list = new NBTTagList();
		list.appendTag(new NBTTagString("rite"));
		player.inventory.addItemStackToInventory(ItemManual.setIds(new ItemStack(ESObjects.ITEMS.MANUAL), list));
	}

}

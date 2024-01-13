package yuzunyannn.elementalsorcery.logics;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import yuzunyannn.elementalsorcery.ESData;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.item.ItemManual;

public class ESPlayerLogic {

	public final static int FIRST_JOIN = 0x01;
	public final static int FIRST_TUTORIAL = 0x02;
	
	public static boolean checkPlayerFlagAndSet(EntityPlayer player, int flag) {
		NBTTagCompound data = ESData.getPlayerNBT(player);
		int flags = data.getInteger("flags");
		if ((flags & flag) != 0) return false;
		data.setInteger("flags", flags | flag);
		return true;
	}

	public static void onPlayerFirstJoinInWorld(EntityPlayerMP player) {
		NBTTagList list = new NBTTagList();
		list.appendTag(new NBTTagString("startup"));
		player.inventory.addItemStackToInventory(ItemManual.setIds(new ItemStack(ESObjects.ITEMS.MANUAL), list));
	}

}

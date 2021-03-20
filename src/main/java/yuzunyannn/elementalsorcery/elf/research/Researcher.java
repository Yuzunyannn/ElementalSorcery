package yuzunyannn.elementalsorcery.elf.research;

import java.util.Set;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import yuzunyannn.elementalsorcery.event.EventServer;
import yuzunyannn.elementalsorcery.item.ItemAncientPaper;

public class Researcher implements INBTSerializable<NBTTagCompound> {

	public static void research(EntityLivingBase player, String topic, int count) {
		Researcher researcher = new Researcher(player);
		researcher.grow(topic, count);
		researcher.save(player);
		if (player instanceof EntityPlayerMP) ItemAncientPaper.sendTopicGrowMessage((EntityPlayerMP) player, topic);
	}

	protected NBTTagCompound map;

	public Researcher() {
		this(new NBTTagCompound());
	}

	public Researcher(EntityLivingBase player) {
		this(EventServer.getPlayerNBT(player).getCompoundTag("knowPoint"));
	}

	public Researcher(NBTTagCompound nbt) {
		this.deserializeNBT(nbt);
	}

	public int get(String key) {
		return map.getInteger(key);
	}

	public void shrink(String key, int point) {
		map.setInteger(key, Math.max(this.get(key) - point, 0));
	}

	public void grow(String key, int point) {
		map.setInteger(key, this.get(key) + point);
	}

	public Set<String> keySet() {
		return map.getKeySet();
	}

	public void save(EntityLivingBase player) {
		NBTTagCompound nbt = EventServer.getPlayerNBT(player);
		nbt.setTag("knowPoint", map);
	}

	@Override
	public NBTTagCompound serializeNBT() {
		return map;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		map = nbt.copy();
	}

}

package yuzunyannn.elementalsorcery.elf.researcher;

import java.util.Set;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import yuzunyannn.elementalsorcery.event.EventServer;

public class Researcher implements INBTSerializable<NBTTagCompound> {

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

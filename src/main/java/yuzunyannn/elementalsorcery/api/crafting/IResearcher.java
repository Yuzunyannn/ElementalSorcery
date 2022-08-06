package yuzunyannn.elementalsorcery.api.crafting;

import java.util.Collection;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

public interface IResearcher extends INBTSerializable<NBTTagCompound> {

	int get(String topic);

	void set(String topic, int count);

	Collection<String> getTopics();

	default public void shrink(String key, int point) {
		set(key, Math.max(get(key) - point, 0));
	}

	default public void grow(String key, int point) {
		set(key, get(key) + point);
	}

}

package yuzunyannn.elementalsorcery.util.helper;

import java.util.List;
import java.util.UUID;

import net.minecraftforge.common.util.INBTSerializable;

public interface INBTWriter {

	void write(String key, UUID uuid);

	void write(String key, INBTSerializable<?> serializable);

	void write(String key, List<? extends INBTSerializable<?>> list);

	void write(String key, String str);
}

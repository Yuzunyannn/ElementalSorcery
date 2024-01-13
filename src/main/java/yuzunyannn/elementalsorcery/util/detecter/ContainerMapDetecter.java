package yuzunyannn.elementalsorcery.util.detecter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Deprecated // use IDataDetectable
public class ContainerMapDetecter<K, V, UK extends NBTBase, UV extends NBTBase> {

	public static interface ICanMapDetected<K, V, UK extends NBTBase, UV extends NBTBase> {

		Collection<K> getKeys();

		boolean hasChange(K key, @Nullable V oldValue);

		V copyCurrValue(K key);

		UV serializeCurrValueToSend(K key);

		UK serializeCurrKeyToSend(K key);

		@SideOnly(Side.CLIENT)
		void deserializeCurrKVFromSend(UK key, @Nullable UV nbtData);
	}

	public static final byte T_KEY_REMOVE = 1;
	public static final byte T_KEY_UPDATE = 2;

	public Map<K, V> lastObjMap = new HashMap<>();

	public NBTTagList detecte(ICanMapDetected<K, V, UK, UV> access) {
		NBTTagList changeList = new NBTTagList();

		Map<K, V> newLastObjMap = new HashMap<>();

		for (K key : access.getKeys()) {
			V lastObj = lastObjMap.get(key);
			boolean hasAnyChange = access.hasChange(key, lastObj);
			if (!hasAnyChange) {
				newLastObjMap.put(key, lastObj);
				continue;
			}
			V obj = access.copyCurrValue(key);
			newLastObjMap.put(key, obj);
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setByte("T", T_KEY_UPDATE);
			nbt.setTag("V", access.serializeCurrValueToSend(key));
			nbt.setTag("K", access.serializeCurrKeyToSend(key));
			changeList.appendTag(nbt);
		}

		for (K key : lastObjMap.keySet()) {
			if (!newLastObjMap.containsKey(key)) {
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setByte("T", T_KEY_REMOVE);
				nbt.setTag("K", access.serializeCurrKeyToSend(key));
				changeList.appendTag(nbt);
			}
		}

		lastObjMap = newLastObjMap;

		return changeList;
	}

	@SideOnly(Side.CLIENT)
	public void recvChangeList(NBTTagList changeList, ICanMapDetected<K, V, UK, UV> access) {
		for (int i = 0; i < changeList.tagCount(); i++) {
			NBTTagCompound dat = changeList.getCompoundTagAt(i);
			byte type = dat.getByte("T");
			UK key = (UK) dat.getTag("K");
			switch (type) {
			case T_KEY_REMOVE:
				access.deserializeCurrKVFromSend(key, null);
				break;
			case T_KEY_UPDATE:
				access.deserializeCurrKVFromSend(key, (UV) dat.getTag("V"));
				break;
			}
		}
	}
}

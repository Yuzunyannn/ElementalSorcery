package yuzunyannn.elementalsorcery.elf;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.elf.quest.QuestTriggerDataSendParcel;
import yuzunyannn.elementalsorcery.elf.quest.QuestTriggers;
import yuzunyannn.elementalsorcery.init.ESInitInstance;
import yuzunyannn.elementalsorcery.util.NBTHelper;
import yuzunyannn.elementalsorcery.util.NBTTag;

/** 精灵邮局 */
public class ElfPostOffice extends WorldSavedData {

	static private long lastGC;

	/** 对长时间没有收获的包裹进行刪除处理 */
	public static void GC(World world) {
		if (ElementalSorcery.config.MAX_LIFE_TIME_OF_PARCEL < 0) return;
		long now = System.currentTimeMillis();
		if (now - lastGC < 1000 * 60 * 60) return;// 一小时一次
		lastGC = now;
		final long MAX_HOLD_TIME = (int) (1000 * 60 * 60 * ElementalSorcery.config.MAX_LIFE_TIME_OF_PARCEL);
		MapStorage storage = world.getMapStorage();
		WorldSavedData worldSave = storage.getOrLoadData(ElfPostOffice.class, "ESPostOffice");
		if (worldSave == null) return;
		ElfPostOffice office = (ElfPostOffice) worldSave;
		for (Entry<String, LinkedList<NBTTagCompound>> entry : office.parcels.entrySet()) {
			LinkedList<NBTTagCompound> list = entry.getValue();
			Iterator<NBTTagCompound> iter = list.iterator();
			while (iter.hasNext()) {
				NBTTagCompound nbt = iter.next();
				long time = nbt.getLong("checkTime");
				if (now - time > MAX_HOLD_TIME) iter.remove();
			}
		}
	}

	/** 获取邮局对象 */
	public static ElfPostOffice getPostOffice(World world) {
		MapStorage storage = world.getMapStorage();
		WorldSavedData worldSave = storage.getOrLoadData(ElfPostOffice.class, "ESPostOffice");
		if (worldSave == null) {
			worldSave = new ElfPostOffice("ESPostOffice");
			storage.setData("ESPostOffice", worldSave);
		}
		return (ElfPostOffice) worldSave;
	}

	/** 增加地址牌的使用次数 */
	public static void addAddressPlateServiceCount(ItemStack addressPlate, int count) {
		NBTTagCompound nbt = addressPlate.getTagCompound();
		if (nbt == null) return;
		nbt.setInteger("service", nbt.getInteger("service") + count);
	}

	public static int getAddressPlateServiceCount(ItemStack addressPlate) {
		NBTTagCompound nbt = addressPlate.getTagCompound();
		if (nbt == null) return 0;
		return nbt.getInteger("service");
	}

	public static boolean isAddressPlate(ItemStack stack) {
		if (stack.getItem() != ESInitInstance.ITEMS.ADDRESS_PLATE) return false;
		return !ElfPostOffice.getAddress(stack).isEmpty();
	}

	public static String getAddress(ItemStack addressPlate) {
		NBTTagCompound nbt = addressPlate.getTagCompound();
		if (nbt == null) return "";
		return nbt.getString("address");
	}

	public static boolean isVIPAddressPlate(ItemStack addressPlate) {
		return addressPlate.getItem() == ESInitInstance.ITEMS.ADDRESS_PLATE && addressPlate.getMetadata() == 1;
	}

	/** 快递包裹信息 */
	private Map<String, LinkedList<NBTTagCompound>> parcels = new HashMap<>();

	public ElfPostOffice(String name) {
		super(name);
	}

	/** 添加一个包裹 */
	public void pushParcel(EntityLivingBase sender, String address, List<ItemStack> goods) {
		if (address.isEmpty()) return;
		LinkedList<NBTTagCompound> list = parcels.get(address);
		if (list == null) {
			list = new LinkedList<NBTTagCompound>();
			parcels.put(address, list);
		}
		NBTTagCompound nbt = new NBTTagCompound();
		NBTHelper.setItemList(nbt, "goods", goods);
		nbt.setString("sender", sender.getName());
		nbt.setLong("checkTime", System.currentTimeMillis());
		list.addLast(nbt);
		this.markDirty();
		QuestTriggers.SEND_PARCEL.trigger(sender, new QuestTriggerDataSendParcel(sender, address, goods));
	}

	public void pushParcel(String address, ItemStack parcel) {
		NBTTagCompound nbt = parcel.getTagCompound();
		if (nbt == null) return;
		LinkedList<NBTTagCompound> list = parcels.get(address);
		if (list == null) {
			list = new LinkedList<NBTTagCompound>();
			parcels.put(address, list);
		}
		nbt.setLong("checkTime", System.currentTimeMillis());
		list.addLast(nbt);
		this.markDirty();
	}

	/** 移除并获取一个包裹 */
	public ItemStack popParcel(String address) {
		LinkedList<NBTTagCompound> list = parcels.get(address);
		if (list == null || list.isEmpty()) return ItemStack.EMPTY;
		NBTTagCompound nbt = list.getFirst();
		list.removeFirst();
		ItemStack parcel = new ItemStack(ESInitInstance.ITEMS.PARCEL);
		parcel.setTagCompound(nbt);
		nbt.removeTag("checkTime");
		this.markDirty();
		return parcel;
	}

	public boolean hasParcel(String address) {
		LinkedList<NBTTagCompound> list = parcels.get(address);
		if (list == null || list.isEmpty()) return false;
		return true;
	}

	/** 创建一个新的地址牌 */
	public ItemStack createAddressPlate(EntityLivingBase owner, String address) {
		ItemStack stack = new ItemStack(ESInitInstance.ITEMS.ADDRESS_PLATE);
		this.setAddressPlateNBT(stack, owner, address);
		return stack;
	}

	/** 更新地址牌子到高级 */
	public ItemStack changeAddressPlate(EntityLivingBase owner, ItemStack addressPlate) {
		String address = ElfPostOffice.getAddress(addressPlate);
		if (address.isEmpty()) return ItemStack.EMPTY;
		ItemStack stack = new ItemStack(ESInitInstance.ITEMS.ADDRESS_PLATE, 1, 1);
		this.setAddressPlateNBT(stack, owner, address);
		return stack;
	}

	public void setAddressPlateNBT(ItemStack addressPlate, EntityLivingBase owner, String address) {
		NBTTagCompound nbt = new NBTTagCompound();
		addressPlate.setTagCompound(nbt);
		nbt.setString("address", address);
		nbt.setString("signature", owner.getName());
	}

	public void readParcelFromNBT(NBTTagCompound nbt) {
		parcels.clear();
		for (String key : nbt.getKeySet()) {
			NBTTagList nbtList = nbt.getTagList(key, NBTTag.TAG_COMPOUND);
			LinkedList<NBTTagCompound> list = new LinkedList<NBTTagCompound>();
			for (NBTBase base : nbtList) list.addLast((NBTTagCompound) base);
			parcels.put(key, list);
		}
	}

	public NBTTagCompound writeParcelToNBT(NBTTagCompound nbt) {
		for (Entry<String, LinkedList<NBTTagCompound>> entry : parcels.entrySet()) {
			NBTTagList nbtList = new NBTTagList();
			LinkedList<NBTTagCompound> list = entry.getValue();
			for (NBTTagCompound base : list) nbtList.appendTag(base);
			nbt.setTag(entry.getKey(), nbtList);
		}
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		readParcelFromNBT(nbt.getCompoundTag("parcels"));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setTag("parcels", writeParcelToNBT(new NBTTagCompound()));
		return nbt;
	}

}

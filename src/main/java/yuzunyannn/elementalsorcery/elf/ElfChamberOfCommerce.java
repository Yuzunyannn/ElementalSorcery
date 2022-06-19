package yuzunyannn.elementalsorcery.elf;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import yuzunyannn.elementalsorcery.api.crafting.IToElementInfo;
import yuzunyannn.elementalsorcery.crafting.element.ElementMap;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.element.ElementTransition;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.item.ItemElfPurse;
import yuzunyannn.elementalsorcery.tile.altar.TileAnalysisAltar;
import yuzunyannn.elementalsorcery.tile.altar.TileInstantConstitute;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;

public class ElfChamberOfCommerce extends WorldSavedData {

	/** 给一个物品定价 */
	public static int priceIt(ItemStack item) {
		double ret = priceIt(item, 0);
		if (ret == -1) return -1;
		return MathHelper.ceil(ret);
	}

	public static double priceIt(ItemStack item, int deep) {
		if (deep > 5) return -1;
		if (item.getItem() == ESInit.ITEMS.ELF_COIN) return 1;
		if (item.getItem() == ESInit.ITEMS.ELF_PURSE) return ItemElfPurse.getCoinFromPurse(item) + 100;
		IToElementInfo info = TileAnalysisAltar.analysisItem(item, ElementMap.instance, true);
		if (info == null) return -1;
		ElementStack[] estacks = info.element();
		double fragment = 0;
		for (ElementStack estack : estacks) {
			double fr = ElementHelper.toFragment(estack);
			ElementTransition et = estack.getElement().getTransition();
			if (et == null) fragment += fr;
			else fragment += ElementHelper.transitionFrom(estack.getElement(), fr, et.getLevel());
		}
		double count = Math.pow(TileInstantConstitute.getOrderValUsed(info), 0.5);
		double money = Math.max(1, Math.pow(fragment, 0.8) / 50) * count;
		ItemStack[] remains = info.remain();
		if (remains != null) {
			for (ItemStack stack : remains) {
				double ret = priceIt(stack, deep + 1);
				if (ret == -1) return -1;
				money = money + ret;
			}
		}
		return money;
	}

	/** 获取商会对象 */
	public static ElfChamberOfCommerce getChamberOfCommerce(World world) {
		MapStorage storage = world.getMapStorage();
		WorldSavedData worldSave = storage.getOrLoadData(ElfChamberOfCommerce.class, "ESChamberOfCommerce");
		if (worldSave == null) {
			worldSave = new ElfPostOffice("ESChamberOfCommerce");
			storage.setData("ESChamberOfCommerce", worldSave);
		}
		return (ElfChamberOfCommerce) worldSave;
	}

	public ElfChamberOfCommerce(String name) {
		super(name);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {

	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		return new NBTTagCompound();
	}
}

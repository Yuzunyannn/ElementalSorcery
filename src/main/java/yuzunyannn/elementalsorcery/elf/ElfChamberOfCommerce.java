package yuzunyannn.elementalsorcery.elf;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.crafting.IToElementInfo;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.element.ElementTransition;
import yuzunyannn.elementalsorcery.capability.Adventurer;
import yuzunyannn.elementalsorcery.crafting.element.ElementMap;
import yuzunyannn.elementalsorcery.elf.pro.ElfProfession;
import yuzunyannn.elementalsorcery.elf.pro.ElfProfessionDebtCollector;
import yuzunyannn.elementalsorcery.elf.quest.IAdventurer;
import yuzunyannn.elementalsorcery.entity.elf.EntityElf;
import yuzunyannn.elementalsorcery.entity.elf.EntityElfBase;
import yuzunyannn.elementalsorcery.item.ItemElfPurse;
import yuzunyannn.elementalsorcery.tile.altar.TileAnalysisAltar;
import yuzunyannn.elementalsorcery.tile.altar.TileInstantConstitute;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class ElfChamberOfCommerce extends WorldSavedData {
	/** 每分钟债务的利息 */
	public static final float DEBT_INTEREST_PER_SEC = 0.001f;
	/** 生成一个讨债人的价值 */
	public static final int COLLECT_DEBT_VALUE = 10;
	/** 开始生成讨债人的限制 */
	public static final int COLLECT_DEBT_START_LIMIT = 500000;
	/** 失信情况下,开始生成讨债人的限制 */
	public static final int COLLECT_DEBT_START_LIMIT_DISHONEST = 100000;

	/** 给一个物品定价 */
	public static int priceIt(ItemStack item) {
		if (item.isEmpty()) return -1;
		double ret = priceIt(item, 0);
		if (ret == -1) return -1;
		return MathHelper.ceil(ret);
	}

	public static double priceIt(ItemStack item, int deep) {
		if (deep > 5) return -1;
		if (item.getItem() == ESObjects.ITEMS.ELF_COIN) return 1;
		if (item.getItem() == ESObjects.ITEMS.ELF_PURSE) return ItemElfPurse.getCoinFromPurse(item) + 100;
		IToElementInfo info = TileAnalysisAltar.analysisItem(item, ElementMap.instance, true);
		if (info == null) return -1;
		ElementStack[] estacks = info.element();
		double fragment = 0;
		for (ElementStack estack : estacks) fragment += ElementTransition.toMagicFragment(estack);
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

	public static boolean isShouldDebtCollection(EntityLivingBase player) {
		IAdventurer adventurer = player.getCapability(Adventurer.ADVENTURER_CAPABILITY, null);
		if (adventurer == null) return false;
		int limit = ElfConfig.isDishonest(player) ? COLLECT_DEBT_START_LIMIT_DISHONEST : COLLECT_DEBT_START_LIMIT;
		return adventurer.getDebts() > limit;
	}

	public static boolean callDebtCollector(EntityLivingBase player) {
		if (player instanceof EntityPlayer) {
			EntityPlayer eplayer = (EntityPlayer) player;
			if (eplayer.inventory.isEmpty()) return false;
		}
		World world = player.getEntityWorld();
		AxisAlignedBB aabb = WorldHelper.createAABB(player.getPosition(), 16, 16, 16);
		int size = world.getEntitiesWithinAABB(EntityElfBase.class, aabb,
				(e) -> e.getProfession() == ElfProfession.DEBT_COLLECTOR).size();
		if (size >= 8) return false;
		BlockPos pos = WorldHelper.tryFindPlaceToSpawn(world, player.getRNG(), player.getPosition(), 4);
		if (pos == null) return false;
		EntityElfBase elf = new EntityElf(world, ElfProfession.DEBT_COLLECTOR);
		elf.setPosition(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
		elf.getProfessionStorage().set(ElfProfessionDebtCollector.DEBTOR_ID, player.getEntityId());
		world.spawnEntity(elf);
		return true;
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

package yuzunyannn.elementalsorcery.elf.pro.merchant;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.elf.trade.Trade;
import yuzunyannn.elementalsorcery.elf.trade.TradeCount;
import yuzunyannn.elementalsorcery.util.var.VariableSet;
import yuzunyannn.elementalsorcery.util.var.VariableSet.Variable;

public class ElfMerchantType {

	public static final Variable<TradeCount> TRADE = new Variable("trade", VariableSet.TRADE_COUNT_OBJ);

	public static final ElfMerchantType DEFAULT = new ElfMerchantTypeDefault();
	public static final ElfMerchantType SCHOLAR = new ElfMerchantTypeScholar();

	private static final Map<String, ElfMerchantType> map = new HashMap<>();
	private static String[] keyArray;

	static public void register(String registryName, ElfMerchantType type) {
		map.put(type.registryName = registryName, type);
		keyArray = null;
	}

	static {
		register("@", DEFAULT);
		register("scholar", SCHOLAR);
		register("nether", new ElfMerchantTypeNether());
		register("ore", new ElfMerchantTypeOre());
		register("juice", new ElfMerchantTypeJuice());
		register("ancient", new ElfMerchantTypeArchaeologist());
		register("food", new ElfMerchantTypeFood());
		register("weapon", new ElfMerchantTypeWeapon());
		register("armor", new ElfMerchantTypeArmor());
		register("tool", new ElfMerchantTypeTool());
	}

	@Nonnull
	static public ElfMerchantType getMerchantType(String id) {
		if (id == null || id.isEmpty()) return DEFAULT;
		ElfMerchantType merchantType = map.get(id);
		return merchantType == null ? DEFAULT : merchantType;
	}

	static public ElfMerchantType getRandomMerchantType(Random rand) {
		if (keyArray == null) keyArray = map.keySet().toArray(new String[map.size()]);
		return getMerchantType(keyArray[rand.nextInt(keyArray.length)]);
	}

	private String registryName;

	public String getRegistryName() {
		return registryName;
	}

	public ElfMerchantType setRegistryName(String registryName) {
		this.registryName = registryName;
		return this;
	}

	public void renewTrade(World world, BlockPos pos, Random rand, VariableSet storage) {

	}

	public boolean hasTrade(VariableSet storage) {
		return getTrade(storage) != null;
	}

	public Trade getTrade(VariableSet storage) {
		return null;
	}

	public ItemStack getHoldItem(World world, VariableSet storage) {
		return ItemStack.EMPTY;
	}
}

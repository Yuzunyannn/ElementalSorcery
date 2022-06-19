package yuzunyannn.elementalsorcery.util.helper;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

public class OreHelper {

	final static public Map<String, OreEnum> oreMap = new HashMap();

	static {
		OreEnum.values();
	}

	public enum OreEnum {
		DIAMOND("oreDiamond", 0x5decf5, "gemDiamond"),
		LAPIS("oreLapis", 0x1445bc, "gemLapis"),
		KYANITE("oreKyanite", 0x43a0df, "kyanite"),
		GOLD("oreGold", 0xfcee4b, "ingotGold"),
		REDSTONE("oreRedstone", 0xff0000, "dustRedstone"),
		EMERALD("oreEmerald", 0x17dd62, "gemEmerald"),
		QUARTZ("oreQuartz", 0xe8e2d8, "gemQuartz"),
		COAL("oreCoal", 0x454545, new String[0]),
		SCARLET_CRYSTAL("oreScarletCrystal", 0xbc2120, "scarletCrystal", "chipIceRock"),
		COPPER("oreCopper", 0xc16e36, "ingotCopper"),
		TIN("oreTin", 0xbebebe, "ingotTin"),
		LEAD("oreLead", 0x76888a, "ingotLead"),
		ZINC("oreZinc", 0xb8ac96, "ingotZinc"),
		OSMIUM("oreOsmium", 0x9eb7df, "ingotOsmium"),
		IRON("oreIron", 0xd8af93, "ingotIron");

		final public int c;
		final public String oreDictName;
		final public String[] productDirctNames;

		OreEnum(String oreDictName, int color, String... productDirctNames) {
			this.c = color;
			this.oreDictName = oreDictName;
			this.productDirctNames = productDirctNames;
			oreMap.put(oreDictName, this);
		}

		public int getColor() {
			return c;
		}

		public ItemStack createOreProduct(int index) {
			if (productDirctNames == null) return ItemStack.EMPTY;
			if (productDirctNames.length == 0) return ItemStack.EMPTY;
			String name = productDirctNames[index % productDirctNames.length];
			NonNullList<ItemStack> list = OreDictionary.getOres(name);
			if (list.isEmpty()) return ItemStack.EMPTY;
			return list.get(0).copy();
		}

		public ItemStack createOre() {
			NonNullList<ItemStack> ores = OreDictionary.getOres(oreDictName);
			if (ores.isEmpty()) return ItemStack.EMPTY;
			return ores.get(0).copy();
		}
	}

	static public OreEnum[] getOreEnums() {
		return OreEnum.values();
	}

	/** 根据名称获取矿物矿物信息，返回null表示不是矿物 */
	static public OreEnum getOreInfo(String oreName) {
		return oreMap.get(oreName);
	}

	static public OreEnum getOreInfo(ItemStack oreStack) {
		return getOreInfo(BlockHelper.getOreName(oreStack));
	}

	static public OreEnum getOreInfo(IBlockState oreState) {
		return getOreInfo(ItemHelper.toItemStack(oreState));
	}

	/** 根据名称获取矿物颜色，如果返回-1则表示不是矿物 */
	static public int getOreColor(String oreName) {
		OreEnum ore = getOreInfo(oreName);
		return ore == null ? -1 : ore.getColor();
	}

	static public int getOreColor(ItemStack oreStack) {
		OreEnum ore = getOreInfo(oreStack);
		return ore == null ? -1 : ore.getColor();
	}

	static public int getOreColor(IBlockState oreState) {
		OreEnum ore = getOreInfo(oreState);
		return ore == null ? -1 : ore.getColor();
	}

	static public boolean isOre(String oreName) {
		return getOreInfo(oreName) != null;
	}

	static public boolean isOre(ItemStack oreStack) {
		return getOreInfo(oreStack) != null;
	}

	static public boolean isOre(IBlockState oreState) {
		return getOreInfo(oreState) != null;
	}

}

package yuzunyannn.elementalsorcery.util.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import yuzunyannn.elementalsorcery.api.util.target.IWorldObject;
import yuzunyannn.elementalsorcery.item.IItemStronger;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

public class OreHelper {

	final static public Map<String, OreEnum> oreMap = new HashMap();
	final static public List<OreEnum> ores = new ArrayList<>();

	public static class OreEnum {

		protected int c;
		protected String oreDictName;
		protected String[] productDictNames;
		protected String crushedDictName;

		OreEnum(String oreDictName, int color) {
			this.c = color;
			this.oreDictName = oreDictName;
			oreMap.put(oreDictName, this);
			ores.add(this);
		}

		OreEnum setProducts(String... productDirctNames) {
			this.productDictNames = productDirctNames;
			return this;
		}

		OreEnum setCrushed(String crushDirctName) {
			this.crushedDictName = crushDirctName;
			return this;
		}

		public String getOreDictName() {
			return oreDictName;
		}

		public int getColor() {
			return c;
		}

		public int getProductTypeCount() {
			return this.productDictNames.length;
		}

		public ItemStack produceOreProduct(int index, World world, @Nullable IWorldObject obj) {
			ItemStack stack = this.createOreProduct(index);
			IItemStronger stronger = ItemHelper.getItemStronger(stack);
			if (stronger != null) stronger.onProduced(stack, obj);
			return stack;
		}

		public ItemStack createOreProduct(int index) {
			if (productDictNames == null) return ItemStack.EMPTY;
			if (productDictNames.length == 0) return ItemStack.EMPTY;
			String name = productDictNames[index % productDictNames.length];
			NonNullList<ItemStack> list = OreDictionary.getOres(name);
			if (list.isEmpty()) return ItemStack.EMPTY;
			return list.get(0).copy();
		}

		protected ItemStack createByDict(String oreKey) {
			if (oreKey == null || oreKey.isEmpty()) return ItemStack.EMPTY;
			NonNullList<ItemStack> ores = OreDictionary.getOres(oreKey);
			if (ores.isEmpty()) return ItemStack.EMPTY;
			return ores.get(0).copy();
		}

		protected NonNullList<ItemStack> getInDict(String oreKey) {
			if (oreKey == null || oreKey.isEmpty()) return OreDictionary.EMPTY_LIST;
			return OreDictionary.getOres(oreKey);
		}

		public ItemStack createOre() {
			return createByDict(oreDictName);
		}

		public NonNullList<ItemStack> getOres() {
			return getInDict(oreDictName);
		}

		public ItemStack createCrushedOre() {
			return createByDict(crushedDictName);
		}
	}

	public final static OreEnum SCARLET_CRYSTAL;
	public final static OreEnum DIAMOND;
	public final static OreEnum LAPIS;
	public final static OreEnum KYANITE;
	public final static OreEnum REDSTONE;
	public final static OreEnum EMERALD;
	public final static OreEnum QUARTZ;
	public final static OreEnum COAL;
	public final static OreEnum MAGNESIUM;// 12
	public final static OreEnum ALUMINIUM; // 13
	public final static OreEnum TITANIUM; // 22
	public final static OreEnum MANGANESE;// 25
	public final static OreEnum IRON;// 26
	public final static OreEnum COBALT;// 27
	public final static OreEnum COPPER;// 29
	public final static OreEnum ZINC;// 30
	public final static OreEnum SILVER; // 47
	public final static OreEnum TIN;// 50
	public final static OreEnum OSMIUM;// 76
	public final static OreEnum GOLD;// 79
	public final static OreEnum LEAD;// 82
	public final static OreEnum URANIUM; // 92

	static {
		SCARLET_CRYSTAL = new OreEnum("oreScarletCrystal", 0xbc2120).setProducts("scarletCrystal", "chipIceRock");
		DIAMOND = new OreEnum("oreDiamond", 0x5decf5).setProducts("gemDiamond").setCrushed("crushedDiamond");
		LAPIS = new OreEnum("oreLapis", 0x1445bc).setProducts("gemLapis").setCrushed("crushedLapis");
		KYANITE = new OreEnum("oreKyanite", 0x43a0df).setProducts("kyanite").setCrushed("crushedKyanite");
		GOLD = new OreEnum("oreGold", 0xfcee4b).setProducts("ingotGold").setCrushed("crushedGold");
		SILVER = new OreEnum("oreSilver", 0xf0f7ff).setProducts("ingotSilver").setCrushed("crushedSilver");
		REDSTONE = new OreEnum("oreRedstone", 0xff0000).setProducts("dustRedstone").setCrushed("crushedRedstone");
		EMERALD = new OreEnum("oreEmerald", 0x17dd62).setProducts("gemEmerald").setProducts("crushedEmerald");
		QUARTZ = new OreEnum("oreQuartz", 0xe8e2d8).setProducts("gemQuartz").setCrushed("crushedQuartz");
		COAL = new OreEnum("oreCoal", 0x454545).setProducts(new String[0]).setCrushed("crushedCoal");
		COPPER = new OreEnum("oreCopper", 0xc16e36).setProducts("ingotCopper").setCrushed("crushedCopper");
		TIN = new OreEnum("oreTin", 0xbebebe).setProducts("ingotTin").setCrushed("crushedTin");
		LEAD = new OreEnum("oreLead", 0x76888a).setProducts("ingotLead").setCrushed("crushedLead");
		ZINC = new OreEnum("oreZinc", 0xb8ac96).setProducts("ingotZinc").setCrushed("crushedZinc");
		OSMIUM = new OreEnum("oreOsmium", 0x9eb7df).setProducts("ingotOsmium").setCrushed("crushedOsmium");
		IRON = new OreEnum("oreIron", 0xd8af93).setProducts("ingotIron").setCrushed("crushedIron");
		URANIUM = new OreEnum("oreUranium", 0xd4ff89).setProducts("ingotUranium").setCrushed("crushedUranium");
		TITANIUM = new OreEnum("oreTitanium", 0xaeb1cb).setProducts("ingotTitanium").setCrushed("crushedTitanium");
		MANGANESE = new OreEnum("oreManganese", 0x546771).setProducts("ingotManganese").setCrushed("crushedManganese");
		MAGNESIUM = new OreEnum("oreMagnesium", 0xdec6dc).setProducts("ingotMagnesium").setCrushed("crushedMagnesium");
		COBALT = new OreEnum("oreCobalt", 0x4d99f6).setProducts("ingotCobalt").setCrushed("crushedCobalt");
		ALUMINIUM = new OreEnum("oreAluminium", 0xa16c56).setProducts("ingotAluminium").setCrushed("crushedAluminium");
	}

	public static Ingredient toIngredient(Collection<ItemStack> oreStacks) {
		return Ingredient.fromStacks(oreStacks.toArray(new ItemStack[oreStacks.size()]));
	}

	static public List<OreEnum> getOreEnumList() {
		return ores;
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

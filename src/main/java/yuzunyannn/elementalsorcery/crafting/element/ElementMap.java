package yuzunyannn.elementalsorcery.crafting.element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.gson.JsonParseException;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.api.crafting.IToElement;
import yuzunyannn.elementalsorcery.api.crafting.IToElementInfo;
import yuzunyannn.elementalsorcery.api.tile.IItemStructureCraft;
import yuzunyannn.elementalsorcery.element.Element;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.tile.altar.TileAnalysisAltar;
import yuzunyannn.elementalsorcery.util.element.ElementAnalysisPacket;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;
import yuzunyannn.elementalsorcery.util.json.ItemRecord;
import yuzunyannn.elementalsorcery.util.json.Json;
import yuzunyannn.elementalsorcery.util.json.Json.ParseExceptionCode;
import yuzunyannn.elementalsorcery.util.json.JsonArray;
import yuzunyannn.elementalsorcery.util.json.JsonObject;

public class ElementMap implements IToElement {

	public final static ElementMap instance = new ElementMap();

	public final static DefaultToElement defaultToElementMap = new DefaultToElement();
	public final static DefaultBucketToElement defaultBucketToElement = new DefaultBucketToElement();

	private final List<IToElement> toList = new LinkedList<IToElement>();

	public ElementMap() {
	}

	public boolean isEmpty() {
		return toList.isEmpty();
	}

	@Override
	public IToElementInfo toElement(ItemStack stack) {
		for (IToElement to : toList) {
			IToElementInfo info = to.toElement(stack);
			if (info != null) return info;
		}
		return null;
	}

	// ---------------------------------ADD----------------------------------------

	public void add(IToElement toElement) {
		if (toElement == null) return;
		if (toList.contains(toElement)) return;
		toList.add(toElement);
	}

	public void add(ItemStack stack, ItemStack[] remain, ElementStack[] estacks) {
		this.add(stack, ElementHelper.getComplexFromElements(stack, estacks), remain, estacks);
	}

	public void add(Item item, ItemStack[] remain, ElementStack[] estacks) {
		this.add(item, ElementHelper.getComplexFromElements(new ItemStack(item), estacks), remain, estacks);
	}

	public void add(ItemStack stack, int complex, ItemStack[] remain, ElementStack[] estacks) {
		if (stack.isEmpty()) return;
		check(estacks);
		defaultToElementMap.add(stack, complex, remain, estacks);
	}

	public void add(Item item, int complex, ItemStack[] remain, ElementStack[] estacks) {
		if (item == null) return;
		check(estacks);
		defaultToElementMap.add(item, complex, remain, estacks);
	}

	public void add(Fluid fluid, int complex, ElementStack[] estacks) {
		if (fluid == null) return;
		check(estacks);
		defaultBucketToElement.add(fluid, complex, estacks);
	}

	private void check(ElementStack... estacks) {
		if (estacks == null) throw new IllegalArgumentException("estacks为null");
		for (ElementStack estack : estacks) {
			if (estack == null) throw new IllegalArgumentException("estacks中存在null项目");
			if (estack.isEmpty()) throw new IllegalArgumentException("estacks中存在empty项目");
		}
	}

	// ---------------------------------ADDEND----------------------------------------

	// 方便操作
	static public ElementStack newES(Element element, int size, int power) {
		return new ElementStack(element, size, power);
	}

	// 再更新，debug用
	static public void reflush() {
		defaultToElementMap.itemToElementMap.clear();
		defaultToElementMap.stackToElementMap.clear();
		defaultBucketToElement.fluidToElementMap.clear();
		for (ModContainer mod : Loader.instance().getActiveModList()) loadElementMap(mod);
		loadRegisterFromFile();
		findAndRegisterCraft();
	}

	static public void loadRegisterFromFile() {
		Json.ergodicFile("recipes/element_mapping", (file, json) -> {
			loadElementMap(json, file.toString());
			return true;
		});
	}

	static public void registerAll() throws IOException {
		instance.add(new DefaultInterfaceToElement());
		instance.add(new DefaultEnchanmentToElement());
		instance.add(new DefaultPotionToElement());
		instance.add(new DefaultElementToElement());
		instance.add(defaultToElementMap);
		instance.add(defaultBucketToElement);
		// 自动扫描并加载json
		for (ModContainer mod : Loader.instance().getActiveModList()) loadElementMap(mod);
		// 自动扫描玩家自定义目录
		loadRegisterFromFile();

		// instance.add(Items.BUCKET, newES(E.METAL, 24, 200));
	}

	static public void findAndRegisterCraft() {
		for (int i = 0; i < 2; i++) {
			// 进行递归记录
			DefaultToElement newMap = dealCrafting();
			defaultToElementMap.merge(newMap);
		}
	}

	public static void loadElementMap(ModContainer mod) {
		Json.ergodicAssets(mod, "/element_maps", (file, json) -> {
			loadElementMap(json, file.toString());
			return true;
		});
	}

	/** 检查是否满足mod的加载需求 */
	public static boolean checkModDemands(JsonObject json) {
		// 条件mod调查
		String[] demandMods = null;
		try {
			demandMods = json.needStrings("demand", "demands", "demandMods", "demandMod", "mods");
		} catch (RuntimeException e) {}
		if (demandMods != null && demandMods.length > 0) {
			for (String modId : demandMods) {
				if (Loader.isModLoaded(modId)) continue;
				// 存在没有加载mod的时候，直接走人
				return false;
			}
		}
		return true;
	}

	// 对一个json进行处理
	public static void loadElementMap(JsonObject json, String fileName) {
		if (!checkModDemands(json)) return;
		JsonArray jarray = json.needArray("maps");
		for (int i = 0; i < jarray.size(); i++) {
			try {
				JsonObject jobj = jarray.needObject(i);
				if (!checkModDemands(jobj)) continue;
				List<ElementStack> estacks = jobj.needElements("element");
				ElementStack[] es = estacks.toArray(new ElementStack[estacks.size()]);
				int complex = jobj.hasNumber("complex") ? jobj.getNumber("complex").intValue() : -1;

				if (jobj.hasString("fluid")) {
					Fluid fluid = FluidRegistry.getFluid(jobj.getString("fluid"));
					if (fluid == null) throw Json.exception(ParseExceptionCode.NOT_HAVE, fluid);
					instance.add(fluid, complex > -1 ? complex : 2, es);
				} else {
					ItemStack[] remains = null;
					try {
						List<ItemRecord> remainList = jobj.needItems("remain");
						remains = ItemRecord.asItemStackArray(remainList);
					} catch (JsonParseException e) {}
					List<ItemRecord> items = jobj.needItems("item");
					for (ItemRecord ir : items) {
						if (complex > -1) {
							if (ir.isJustItem()) instance.add(ir.getItem(), complex, remains, es);
							else instance.add(ir.getStack(), complex, remains, es);
						} else {
							if (ir.isJustItem()) instance.add(ir.getItem(), remains, es);
							else instance.add(ir.getStack(), remains, es);
						}
					}
				}

			} catch (JsonParseException e) {
				ElementalSorcery.logger.warn("解析json出现异常：" + fileName, e);
			}
		}
	}

	private static boolean doAnalysis(DefaultToElement newMap, ItemStack output, NonNullList<Ingredient> inputs) {

		List<ItemStack> list = new ArrayList<>(inputs.size());
		for (Ingredient ingredient : inputs) {
			ItemStack[] s = ingredient.getMatchingStacks();
			if (s == null || s.length == 0) continue;
			list.add(s[0]);
		}

		List<ItemStack> remains = new ArrayList<>();
		for (ItemStack stack : list) {
			stack = ForgeHooks.getContainerItem(stack);
			if (stack.isEmpty()) continue;
			remains.add(stack.copy());
		}

		ElementAnalysisPacket ans = TileAnalysisAltar.analysisItems(new IItemStructureCraft() {

			@Override
			public Collection<ItemStack> getInputs() {
				return list;
			}

			@Override
			public ItemStack getOutput() {
				return output;
			}
			
			@Override
			public Collection<ItemStack> getRemains() {
				return remains;
			}

		}, instance);

		if (ans == null) return false;

		if (output.getHasSubtypes()) newMap.add(output, ans.daComplex, null, ans.daEstacks);
		else newMap.add(output.getItem(), ans.daComplex, null, ans.daEstacks);

		return true;
	}

	private static DefaultToElement dealCrafting() {
		DefaultToElement newMap = new DefaultToElement();
		Set<String> dealSet = new HashSet<>();
		dealSet.add("minecraft");
		dealSet.add(ElementalSorcery.MODID);
		// 检测所有物品
		for (Item item : Item.REGISTRY) {
			String modId = item.getRegistryName().getNamespace();
			if (!dealSet.contains(modId)) continue;
			// 遍历所有类别
			NonNullList<ItemStack> stacks = NonNullList.<ItemStack>create();
			item.getSubItems(CreativeTabs.SEARCH, stacks);
			for (ItemStack stack : stacks) {
				if (stack.getTagCompound() != null) continue;
				// 存在的话，就不查找了
				if (instance.toElement(stack) != null) continue;
				// 寻找普通合成表
				for (IRecipe irecipe : CraftingManager.REGISTRY) {
					ItemStack output = irecipe.getRecipeOutput();
					// 产出与物品相同，开始分析
					if (output.isItemEqual(stack)) {
						boolean ret = doAnalysis(newMap, output, irecipe.getIngredients());
						if (ret) break;
					}
				}
			}
		}
		return newMap;
	}

}

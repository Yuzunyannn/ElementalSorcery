package yuzunyannn.elementalsorcery.crafting.element;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.JsonParseException;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.oredict.OreDictionary;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.IElementMap;
import yuzunyannn.elementalsorcery.api.crafting.IToElement;
import yuzunyannn.elementalsorcery.api.crafting.IToElementInfo;
import yuzunyannn.elementalsorcery.api.crafting.IToElementItem;
import yuzunyannn.elementalsorcery.element.Element;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.tile.altar.TileAnalysisAltar;
import yuzunyannn.elementalsorcery.tile.altar.TileAnalysisAltar.AnalysisPacket;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;
import yuzunyannn.elementalsorcery.util.json.ItemRecord;
import yuzunyannn.elementalsorcery.util.json.Json;
import yuzunyannn.elementalsorcery.util.json.JsonArray;
import yuzunyannn.elementalsorcery.util.json.JsonObject;

public class ElementMap implements IElementMap {

	public final static ElementMap instance = new ElementMap();

	public final static DefaultToElement defaultToElementMap = new DefaultToElement();

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

	@Override
	public void add(IToElement toElement) {
		if (toElement == null) return;
		if (toList.contains(toElement)) return;
		toList.add(toElement);
	}

	@Override
	public void add(ItemStack stack, ElementStack... estacks) {
		this.add(stack, ElementHelper.getComplexFromElements(stack, estacks), estacks);
	}

	@Override
	public void add(Item item, ElementStack... estacks) {
		this.add(item, ElementHelper.getComplexFromElements(new ItemStack(item), estacks), estacks);
	}

	@Override
	public void add(Block block, ElementStack... estacks) {
		this.add(block, ElementHelper.getComplexFromElements(new ItemStack(block), estacks), estacks);
	}

	private void check(ElementStack... estacks) {
		if (estacks == null) throw new IllegalArgumentException("estacks为null");
		for (ElementStack estack : estacks) {
			if (estack == null) throw new IllegalArgumentException("estacks中存在null项目");
			if (estack.isEmpty()) throw new IllegalArgumentException("estacks中存在empty项目");
		}
	}

	@Override
	public void add(ItemStack stack, int complex, ElementStack... estacks) {
		if (stack.isEmpty()) return;
		this.check(estacks);
		defaultToElementMap.add(stack, complex, estacks);
	}

	@Override
	public void add(Item item, int complex, ElementStack... estacks) {
		if (item == null) return;
		this.check(estacks);
		defaultToElementMap.add(item, complex, estacks);
	}

	@Override
	public void add(Block block, int complex, ElementStack... estacks) {
		this.add(Item.getItemFromBlock(block), complex, estacks);
	}

	// ---------------------------------ADDEND----------------------------------------

	// 默认的接口转化
	private static class DefaultInterfaceToElement implements IToElement {

		@Override
		public IToElementInfo toElement(ItemStack stack) {
			Item item = stack.getItem();
			if (item instanceof IToElementItem) return ((IToElementItem) item).toElement(stack);
			return null;
		}

	}

	// 默认的实例化
	private static class DefaultToElement implements IToElement {

		public List<ElementInfo> stackToElementMap = new ArrayList<ElementInfo>();
		public Map<Item, ElementInfo> itemToElementMap = new HashMap<Item, ElementInfo>();

		public void add(ItemStack stack, int complex, ElementStack... estacks) {
			boolean checkNBT = false;
			// 如果存在"checkNBTTag"字段，则添加标签，这个标签只是标记用的，所以要删除
			if (stack.hasTagCompound()) {
				NBTTagCompound nbt = stack.getTagCompound();
				if (nbt.hasKey("checkNBTTag")) {
					checkNBT = true;
					nbt.removeTag("checkNBTTag");
				}
			}
			// 检查是否存在
			for (int i = 0; i < stackToElementMap.size(); i++) {
				ElementInfo info = stackToElementMap.get(i);
				// 有相同的就替换
				if (ItemStack.areItemsEqual(info.stack, stack)) {
					if (checkNBT && !ItemStack.areItemStackTagsEqual(info.stack, stack)) continue;
					stackToElementMap.set(i, new ElementInfo(stack, estacks, complex));
					return;
				}
			}
			stackToElementMap.add(new ElementInfo(stack, estacks, complex));
		}

		public void add(Item item, int complex, ElementStack... estacks) {
			itemToElementMap.put(item, new ElementInfo(ItemStack.EMPTY, estacks, complex));
		}

		@Override
		public IToElementInfo toElement(ItemStack stack) {
			for (ElementInfo info : this.stackToElementMap) {
				if (this.compareItemStacks(stack, info.stack)) return info;
			}
			return this.toElement(stack.getItem());
		}

		public IToElementInfo toElement(Item item) {
			if (itemToElementMap.containsKey(item)) return itemToElementMap.get(item);
			return null;
		}

		private boolean compareItemStacks(ItemStack stack1, ItemStack stack2) {
			return ItemHelper.areItemsEqual(stack1, stack2);
		}

		protected void merge(DefaultToElement other) {
			for (Entry<Item, ElementInfo> entry : other.itemToElementMap.entrySet()) {
				if (this.itemToElementMap.containsKey(entry.getKey())) continue;
				this.itemToElementMap.put(entry.getKey(), entry.getValue());
			}
			for (ElementInfo info : other.stackToElementMap) {
				if (this.toElement(info.stack) != null) continue;
				this.stackToElementMap.add(info);
			}
		}

		static public class ElementInfo implements IToElementInfo {
			final public ItemStack stack;
			final public ElementStack[] estacks;
			final public int complex;

			public ElementInfo(ItemStack stack, ElementStack[] estacks, int complex) {
				this.stack = stack;
				this.estacks = estacks;
				this.complex = complex;
			}

			@Override
			public ElementStack[] element() {
				return estacks;
			}

			@Override
			public int complex() {
				return complex;
			}
		}

	}

	// 默认的容器转化到元素
	private static class DefaultBucketToElement implements IToElement {

		protected static IToElementInfo water;
		protected static IToElementInfo fire;

		@Override
		public IToElementInfo toElement(ItemStack stack) {
			if (stack.getItem() == Items.WATER_BUCKET) return water;
			else if (stack.getItem() == Items.LAVA_BUCKET) return fire;
			return null;
			// IFluidHandlerItem fhi =
			// stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY,
			// null);
			// if (fhi == null) return null;
		}

	}

	// 方便操作
	static public ElementStack newES(Element element, int size, int power) {
		return new ElementStack(element, size, power);
	}

	// 再更新，debug用
	static public void reflush() {
		defaultToElementMap.itemToElementMap.clear();
		defaultToElementMap.stackToElementMap.clear();
		for (ModContainer mod : Loader.instance().getActiveModList()) loadElementMap(mod);
		loadRegisterFromFile();
		findAndRegisterCraft();
	}

	static public void loadRegisterFromFile() {
		File folder = ElementalSorcery.data.getFile("element/mapping", "");
		File[] files = folder.listFiles();
		for (File file : files) {
			if (!file.isFile()) continue;
			try {
				JsonObject json = new JsonObject(file);
				loadElementMap(json, file.getPath());
			} catch (IOException e) {
				ElementalSorcery.logger.warn("自定义json数据读取失败:" + file);
			}
		}
	}

	static public void registerAll() throws IOException {
		instance.add(new DefaultInterfaceToElement());
		instance.add(defaultToElementMap);
		instance.add(new DefaultBucketToElement());

		final ESObjects.Elements E = ESInit.ELEMENTS;

		// 自动扫描并加载json
		for (ModContainer mod : Loader.instance().getActiveModList()) loadElementMap(mod);
		// 自动扫描玩家自定义目录
		loadRegisterFromFile();

		DefaultBucketToElement.water = ToElementInfoStatic.create(3, new ItemStack(Items.BUCKET),
				newES(E.WATER, 25, 100));
		DefaultBucketToElement.fire = ToElementInfoStatic.create(7, new ItemStack(Items.BUCKET),
				newES(E.FIRE, 100, 500));

		instance.add(Items.BUCKET, newES(E.METAL, 24, 200));
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

	// 对一个json进行处理
	public static void loadElementMap(JsonObject json, String fileName) {
		JsonArray jarray = json.needArray("maps");
		for (int i = 0; i < jarray.size(); i++) {
			try {
				JsonObject jobj = jarray.needObject(i);
				List<ElementStack> estacks = jobj.needElements("element");
				List<ItemRecord> items = jobj.needItems("item");
				int complex = -1;
				if (jobj.hasNumber("complex")) complex = jobj.getNumber("complex").intValue();
				ElementStack[] es = estacks.toArray(new ElementStack[estacks.size()]);
				for (ItemRecord ir : items) {
					ir = dealItemRecord(ir);
					if (complex > -1) {
						if (ir.isJustItem()) instance.add(ir.getItem(), complex, es);
						else instance.add(ir.getStack(), complex, es);
					} else {
						if (ir.isJustItem()) instance.add(ir.getItem(), es);
						else instance.add(ir.getStack(), es);
					}
				}
			} catch (JsonParseException e) {
				ElementalSorcery.logger.warn("解析json出现异常：" + fileName, e);
			}
		}
	}

	private static ItemRecord dealItemRecord(ItemRecord ir) {
		if (ir.isJustItem()) return ir;
		if (ir.getStack().getMetadata() == OreDictionary.WILDCARD_VALUE) return new ItemRecord(ir.getStack().getItem());
		return ir;
	}

	private static DefaultToElement dealCrafting() {
		DefaultToElement newMap = new DefaultToElement();
		// 检测所有物品
		for (Item item : Item.REGISTRY) {
			// 只留下minecraft的
			if (!"minecraft".equals(item.getRegistryName().getResourceDomain())) continue;
			NonNullList<ItemStack> stacks = NonNullList.<ItemStack>create();
			// 遍历所有类别
			item.getSubItems(CreativeTabs.SEARCH, stacks);
			for (ItemStack stack : stacks) next: {
				if (stack.getTagCompound() != null) continue;
				// 存在的话，就不查找了
				if (instance.toElement(stack) != null) continue;
				// 寻找合成表
				for (IRecipe irecipe : CraftingManager.REGISTRY) {
					ItemStack output = irecipe.getRecipeOutput();
					// 产出与物品相同，开始分析
					if (output.isItemEqual(stack)) {
						AnalysisPacket ans = null;
						NonNullList<Ingredient> inputs = irecipe.getIngredients();
						for (Ingredient ingredient : inputs) {
							ItemStack[] s = ingredient.getMatchingStacks();
							if (s == null || s.length == 0) continue;
							AnalysisPacket ansTemp = TileAnalysisAltar.analysisItem(s[0], instance, true);
							if (ansTemp == null) break next;
							if (ans == null) ans = ansTemp;
							else ans.merge(ansTemp);
						}
						// 如果解析成功就记录
						if (ans != null) {
							// 处理多结果
							if (output.getCount() > 1) {
								int n = output.getCount();
								for (ElementStack es : ans.daEstacks) {
									if (es.getCount() > n) es.setCount(es.getCount() / n);
									else {
										es.weaken(es.getCount() / (float) (n + 1));
										es.setCount(1);
									}
								}
							}
							ans.daComplex = ans.daComplex * 2 / 3;
							if (output.getHasSubtypes()) newMap.add(output, ans.daComplex, ans.daEstacks);
							else newMap.add(output.getItem(), ans.daComplex, ans.daEstacks);
						}
						break;
					}
				}
			}
		}
		return newMap;
	}

}

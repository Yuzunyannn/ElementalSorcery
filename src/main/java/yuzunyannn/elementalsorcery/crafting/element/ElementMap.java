package yuzunyannn.elementalsorcery.crafting.element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import yuzunyannn.elementalsorcery.ESData;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.crafting.IToElement;
import yuzunyannn.elementalsorcery.api.register.IElementMap;
import yuzunyannn.elementalsorcery.element.Element;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.init.ESInitInstance;
import yuzunyannn.elementalsorcery.util.JsonHelper;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;

public class ElementMap implements IElementMap {

	public final static ElementMap instance = new ElementMap();

	public final static DefaultToElement defaultToElementMap = new DefaultToElement();

	private final List<IToElement> toList = new LinkedList<IToElement>();

	public ElementMap() {
	}

	@Override
	public ElementStack[] toElement(ItemStack stack) {
		for (IToElement to : toList) {
			ElementStack[] stacks = to.toElement(stack);
			if (stacks != null) return stacks;
		}
		return null;
	}

	@Override
	public ElementStack[] toElement(Item item) {
		for (IToElement to : toList) {
			ElementStack[] stacks = to.toElement(item);
			if (stacks != null) return stacks;
		}
		return null;
	}

	@Override
	public ElementStack[] toElement(Block block) {
		return this.toElement(Item.getItemFromBlock(block));
	}

	@Override
	public ItemStack remain(ItemStack stack) {
		for (IToElement to : toList) {
			ElementStack[] stacks = to.toElement(stack);
			if (stacks != null) return to.remain(stack);
		}
		return ItemStack.EMPTY;
	}

	@Override
	public int complex(ItemStack stack) {
		for (IToElement to : toList) {
			int complex = to.complex(stack);
			if (complex > 0) return complex;
		}
		return 0;
	}

	@Override
	public int complex(Item item) {
		for (IToElement to : toList) {
			int complex = to.complex(item);
			if (complex > 0) return complex;
		}
		return 0;
	}

	@Override
	public int complex(Block block) {
		return this.complex(Item.getItemFromBlock(block));
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
		defaultToElementMap.stackToElementMap.add(new DefaultToElement.ElementInfo(stack, estacks, complex));
	}

	@Override
	public void add(Item item, int complex, ElementStack... estacks) {
		if (item == null) return;
		this.check(estacks);
		defaultToElementMap.itemToElementMap.put(item,
				new DefaultToElement.ElementInfo(ItemStack.EMPTY, estacks, complex));
	}

	@Override
	public void add(Block block, int complex, ElementStack... estacks) {
		this.add(Item.getItemFromBlock(block), complex, estacks);
	}

	// ---------------------------------ADDEND----------------------------------------

	// 默认的实例化
	private static class DefaultToElement implements IToElement {

		public List<ElementInfo> stackToElementMap = new ArrayList<ElementInfo>();
		public Map<Item, ElementInfo> itemToElementMap = new HashMap<Item, ElementInfo>();

		@Override
		public ElementStack[] toElement(ItemStack stack) {
			for (ElementInfo info : this.stackToElementMap) {
				if (this.compareItemStacks(stack, info.stack)) { return info.estacks; }
			}
			return this.toElement(stack.getItem());
		}

		public ElementStack[] toElement(Item item) {
			if (itemToElementMap.containsKey(item)) return itemToElementMap.get(item).estacks;
			return null;
		}

		@Override
		public int complex(ItemStack stack) {
			for (ElementInfo info : this.stackToElementMap) {
				if (this.compareItemStacks(stack, info.stack)) { return info.complex; }
			}
			return this.complex(stack.getItem());
		}

		@Override
		public int complex(Item item) {
			if (itemToElementMap.containsKey(item)) return itemToElementMap.get(item).complex;
			return 0;
		}

		private boolean compareItemStacks(ItemStack stack1, ItemStack stack2) {
			return stack2.getItem() == stack1.getItem() && stack2.getMetadata() == stack1.getMetadata();
		}

		static public class ElementInfo {
			final public ItemStack stack;
			final public ElementStack[] estacks;
			final public int complex;

			public ElementInfo(ItemStack stack, ElementStack[] estacks, int complex) {
				this.stack = stack;
				this.estacks = estacks;
				this.complex = complex;
			}
		}

	}

	// 默认的容器转化到元素
	private static class DefaultBucketToElement implements IToElement {

		protected static ElementStack[] water;
		protected static ElementStack[] fire;

		@Override
		public ElementStack[] toElement(ItemStack stack) {
			if (stack.getItem() == Items.WATER_BUCKET) {
				return water;
			} else if (stack.getItem() == Items.LAVA_BUCKET) { return fire; }
			return null;
			// IFluidHandlerItem fhi =
			// stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY,
			// null);
			// if (fhi == null) return null;
		}

		@Override
		public ElementStack[] toElement(Item item) {
			return null;
		}

		@Override
		public ItemStack remain(ItemStack stack) {
			if (stack.getItem() == Items.WATER_BUCKET || stack.getItem() == Items.LAVA_BUCKET) {
				return new ItemStack(Items.BUCKET);
			}
			return ItemStack.EMPTY;
		}

		@Override
		public int complex(ItemStack stack) {
			if (stack.getItem() == Items.WATER_BUCKET || stack.getItem() == Items.LAVA_BUCKET) return 5;
			return 0;
		}

		@Override
		public int complex(Item item) {
			return 0;
		}

	}

	// 方便操作
	static public ElementStack newES(Element element, int size, int power) {
		return new ElementStack(element, size, power);
	}

	static public void registerAll() throws IOException {
		instance.add(defaultToElementMap);
		instance.add(new DefaultBucketToElement());

		final ESObjects.Elements E = ESInitInstance.ELEMENTS;
		final ESData data = ElementalSorcery.data;
		final String MODID = ElementalSorcery.MODID;

		// 自动扫描element_maps文件夹下所有json
		String[] mapJsonNames = data.getFilesFromResource(new ResourceLocation(MODID, "element_maps"));
		for (String path : mapJsonNames) {
			try {
				if (!path.endsWith(".json")) continue;
				JsonObject jobj = data.getJsonFromResource(new ResourceLocation(MODID, "element_maps/" + path));
				if (!JsonHelper.isArray(jobj, "maps")) continue;
				JsonArray jarray = jobj.get("maps").getAsJsonArray();
				// 读取所有映射
				for (JsonElement je : jarray) {
					if (!je.isJsonObject()) continue;
					jobj = je.getAsJsonObject();
					if (!jobj.has("element")) continue;
					if (!jobj.has("item")) continue;
					// 这里进行try来防止一个没加载到导致全局没法加载
					try {
						List<ElementStack> estacks = JsonHelper.readElements(jobj.get("element"));
						List<JsonHelper.ItemRecord> items = JsonHelper.readItems(jobj.get("item"));
						if (estacks.isEmpty()) continue;
						if (items.isEmpty()) continue;
						// 复杂度
						int complex = -1;
						if (JsonHelper.isNumber(jobj, "complex")) complex = Math.max(-1, jobj.getAsInt());
						ElementStack[] es = estacks.toArray(new ElementStack[estacks.size()]);
						for (JsonHelper.ItemRecord ir : items) {
							if (complex > -1) {
								if (ir.isJustItem()) instance.add(ir.getItem(), complex, es);
								else instance.add(ir.getStack(), complex, es);
							} else {
								if (ir.isJustItem()) instance.add(ir.getItem(), es);
								else instance.add(ir.getStack(), es);
							}
						}
					} catch (JsonParseException e) {
						if (e.getMessage().startsWith(JsonHelper.ERROR_CODE_OTHER_MOD))
							ElementalSorcery.logger.warn(e.getMessage());
						else ElementalSorcery.logger.warn("读取json元素映射过程中出现异常：" + path, e);
					}
				}
			} catch (Exception e) {
				ElementalSorcery.logger.warn("读取json文件过程中出现异常：" + path, e);
			}
		}

		DefaultBucketToElement.water = new ElementStack[] { newES(E.WATER, 25, 100) };
		DefaultBucketToElement.fire = new ElementStack[] { newES(E.FIRE, 100, 500) };

		instance.add(Items.BUCKET,

				newES(E.METAL, 24, 200));
	}

}

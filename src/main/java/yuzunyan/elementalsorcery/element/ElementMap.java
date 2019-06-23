package yuzunyan.elementalsorcery.element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.crash.CrashReport;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import yuzunyan.elementalsorcery.api.ESObjects;
import yuzunyan.elementalsorcery.api.ESRegister.IElementMap;
import yuzunyan.elementalsorcery.api.element.Element;
import yuzunyan.elementalsorcery.api.element.ElementStack;
import yuzunyan.elementalsorcery.api.element.IToElement;
import yuzunyan.elementalsorcery.api.util.ElementHelper;
import yuzunyan.elementalsorcery.init.ESInitInstance;

public class ElementMap implements IElementMap {

	public static ElementMap instance = new ElementMap();

	private DefaultToElement toElementMap = new DefaultToElement();
	private List<IToElement> toList = new LinkedList<IToElement>();

	public ElementMap() {
		this.add(toElementMap);
		this.add(new DefaultBucketToElement());
	}

	@Override
	public ElementStack[] toElement(ItemStack stack) {
		for (IToElement to : toList) {
			ElementStack[] stacks = to.toElement(stack);
			if (stacks != null)
				return stacks;
		}
		return null;
	}

	@Override
	public ElementStack[] toElement(Item item) {
		for (IToElement to : toList) {
			ElementStack[] stacks = to.toElement(item);
			if (stacks != null)
				return stacks;
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
			if (stacks != null) {
				return to.remain(stack);
			}
		}
		return ItemStack.EMPTY;
	}

	@Override
	public int complex(ItemStack stack) {
		for (IToElement to : toList) {
			int complex = to.complex(stack);
			if (complex > 0) {
				return complex;
			}
		}
		return 0;
	}

	@Override
	public int complex(Item item) {
		for (IToElement to : toList) {
			int complex = to.complex(item);
			if (complex > 0) {
				return complex;
			}
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
		if (toElement == null)
			return;
		if (toList.contains(toElement))
			return;
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

	@Override
	public void add(ItemStack stack, int complex, ElementStack... estacks) {
		if (stack.isEmpty())
			return;
		if (estacks == null)
			return;
		for (ElementStack estack : estacks) {
			if (estack == null)
				return;
			if (estack.isEmpty())
				return;
		}
		toElementMap.stack_to_element_map.add(new DefaultToElement.ElementInfo(stack, estacks, complex));
	}

	@Override
	public void add(Item item, int complex, ElementStack... estacks) {
		if (item == null)
			return;
		if (estacks == null)
			return;
		for (ElementStack estack : estacks) {
			if (estack == null)
				return;
			if (estack.isEmpty())
				return;
		}
		toElementMap.item_to_element_map.put(item, new DefaultToElement.ElementInfo(ItemStack.EMPTY, estacks, complex));
	}

	@Override
	public void add(Block block, int complex, ElementStack... estacks) {
		this.add(Item.getItemFromBlock(block), complex, estacks);
	}

	// ---------------------------------ADDEND----------------------------------------

	// 默认的实例化
	private static class DefaultToElement implements IToElement {

		public List<ElementInfo> stack_to_element_map = new ArrayList<ElementInfo>();
		public Map<Item, ElementInfo> item_to_element_map = new HashMap<Item, ElementInfo>();

		@Override
		public ElementStack[] toElement(ItemStack stack) {
			for (ElementInfo info : this.stack_to_element_map) {
				if (this.compareItemStacks(stack, info.stack)) {
					return info.estacks;
				}
			}
			return this.toElement(stack.getItem());
		}

		public ElementStack[] toElement(Item item) {
			if (item_to_element_map.containsKey(item))
				return item_to_element_map.get(item).estacks;
			return null;
		}

		@Override
		public int complex(ItemStack stack) {
			for (ElementInfo info : this.stack_to_element_map) {
				if (this.compareItemStacks(stack, info.stack)) {
					return info.complex;
				}
			}
			return this.complex(stack.getItem());
		}

		@Override
		public int complex(Item item) {
			if (item_to_element_map.containsKey(item))
				return item_to_element_map.get(item).complex;
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
			} else if (stack.getItem() == Items.LAVA_BUCKET) {
				return fire;
			}
			return null;
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

	static public void registerAll() {
		ESObjects.Elements E = ESInitInstance.ELEMENTS;
		DefaultBucketToElement.water = new ElementStack[] { newES(E.WATER, 25, 100) };
		DefaultBucketToElement.fire = new ElementStack[] { newES(E.FIRE, 100, 500) };
		
		instance.add(Blocks.STONE, newES(E.EARTH, 1, 12));
		instance.add(Blocks.COBBLESTONE, newES(E.EARTH, 1, 10));
		instance.add(Blocks.GRASS, newES(E.EARTH, 1, 10));
		instance.add(Blocks.DIRT, newES(E.EARTH, 1, 10));
		instance.add(Blocks.SAND, newES(E.EARTH, 2, 5));
		
		instance.add(Blocks.WOODEN_SLAB, newES(E.WOOD, 1, 10));
		instance.add(Blocks.PLANKS, newES(E.WOOD, 1, 10));
		instance.add(Blocks.LOG, newES(E.WOOD, 4, 12));
		instance.add(Blocks.LOG2, newES(E.WOOD, 4, 12));
		
		instance.add(Blocks.COAL_ORE, newES(E.EARTH, 20, 25), newES(E.METAL, 15, 100));
		instance.add(Blocks.IRON_ORE, newES(E.EARTH, 17, 25), newES(E.METAL, 21, 220));
		instance.add(Items.IRON_INGOT, newES(E.METAL, 8, 200));
		instance.add(Items.BUCKET, newES(E.METAL, 24, 200));
		instance.add(Blocks.GOLD_ORE, newES(E.EARTH, 15, 25), newES(E.METAL, 22, 250));
		instance.add(Items.GOLD_INGOT, newES(E.METAL, 9, 220));
		
		instance.add(Blocks.END_STONE, newES(E.ENDER, 1, 20));
		instance.add(Items.ENDER_PEARL, newES(E.ENDER, 75, 1000));
		instance.add(Items.ENDER_EYE, newES(E.ENDER, 75, 1000), newES(E.FIRE, 20, 450));
		
		instance.add(Items.BOOK, newES(E.KNOWLEDGE, 10, 20));
		instance.add(ESInitInstance.ITEMS.SPELLBOOK_ENCHANTMENT, newES(E.KNOWLEDGE, 100, 200));
	}

}

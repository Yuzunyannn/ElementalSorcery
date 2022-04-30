package yuzunyannn.elementalsorcery.tile.md;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraftforge.items.ItemStackHandler;
import yuzunyannn.elementalsorcery.block.BlockAStone;
import yuzunyannn.elementalsorcery.crafting.element.ElementMap;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.render.effect.scrappy.FirewrokShap;
import yuzunyannn.elementalsorcery.util.helper.GameHelper;
import yuzunyannn.elementalsorcery.util.json.ItemRecord;
import yuzunyannn.elementalsorcery.util.json.Json;

public class TileMDRubbleRepair extends TileMDBase implements ITickable {

	public static interface IExtendRepair {
		ItemStack getRepairOutput(ItemStack input);

		int getRepairCost(ItemStack input);
	}

	/** 修复合成表 */
	static public class Recipe {
		protected ItemStack input = ItemStack.EMPTY;
		protected ItemStack output = ItemStack.EMPTY;
		protected int cost;

		public ItemStack getInput() {
			return input;
		}

		public ItemStack getOutput() {
			return output;
		}

		public int getCost() {
			return cost;
		}
	}

	static final private List<Recipe> recipes = new ArrayList<>();

	public static List<Recipe> getRecipes() {
		return recipes;
	}

	static public void addRecipe(Block input, Block output, int cost) {
		addRecipe(new ItemStack(input), new ItemStack(output), cost);
	}

	static public void addRecipe(ItemStack input, ItemStack output, int cost) {
		if (input.isEmpty() || output.isEmpty()) return;
		Recipe r = new Recipe();
		r.input = input;
		r.output = output;
		r.cost = Math.max(1, cost);
		recipes.add(r);
	}

	static public void init() {
		addRecipe(Blocks.COBBLESTONE, Blocks.STONE, 1);
		addRecipe(new ItemStack(ESInit.BLOCKS.ASTONE, 1, BlockAStone.EnumType.FRAGMENTED.ordinal()),
				new ItemStack(ESInit.BLOCKS.ASTONE), 10);
		addRecipe(new ItemStack(Blocks.STONEBRICK, 1, 2), new ItemStack(Blocks.STONEBRICK), 1);
		addRecipe(new ItemStack(ESInit.BLOCKS.CRUDE_QUARTZ), new ItemStack(Blocks.QUARTZ_BLOCK), 6);

		// 自定义
		Json.ergodicFile("recipes/stone_repairer", (file, json) -> {
			if (!ElementMap.checkModDemands(json)) return false;
			ItemRecord input = json.needItem("input");
			ItemRecord output = json.needItem("output");
			int count = json.needNumber("magic", "count", "cost").intValue();
			addRecipe(input.getStack(), output.getStack(), count);
			return true;
		});

		// 展示用
		GameHelper.clientRun(() -> {
			BiFunction<Item, Integer, Void> addRepair = (item, magic) -> {
				ItemStack input = new ItemStack(item, 1, 1);
				ItemStack output = new ItemStack(item, 1, 0);
				addRecipe(input, output, magic);
				return null;
			};
			addRepair.apply(ESInit.ITEMS.MAGIC_GOLD_AXE, 10);
			addRepair.apply(ESInit.ITEMS.MAGIC_GOLD_PICKAXE, 10);
			addRepair.apply(ESInit.ITEMS.MAGIC_GOLD_HOE, 10);
			addRepair.apply(ESInit.ITEMS.MAGIC_GOLD_SPADE, 10);
			addRepair.apply(ESInit.ITEMS.MAGIC_GOLD_SWORD, 10);
			addRepair.apply(ESInit.ITEMS.SCAPEGOAT, 5);
			addRepair.apply(ESInit.ITEMS.CUBE_DEMARCATOR, 20);
		});
	}

	/** 完成度 */
	protected int complete;

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("complete", complete);
		return super.writeToNBT(nbt);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		complete = nbt.getInteger("complete");
		super.readFromNBT(nbt);
	}

	@Override
	protected ItemStackHandler initItemStackHandler() {
		return new ItemStackHandler(1) {
			@Override
			public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
				if (TileMDRubbleRepair.getDefaultRepairResult(stack).isEmpty()) return stack;
				return super.insertItem(slot, stack, simulate);
			}
		};
	}

	/** 获取物品 */
	public ItemStack getStack() {
		return inventory.getStackInSlot(0);
	}

	/** 设置物品 */
	public void setStack(Block block) {
		int count = this.getStack().getCount();
		this.inventory.setStackInSlot(0, new ItemStack(block, count == 0 ? 1 : count, isStart ? 1 : 0));
	}

	public int getComplete() {
		return complete;
	}

	public int getTotalComplete() {
		return this.getStack().isEmpty() ? 1 : this.getStack().getCount() * 16;
	}

	int lastId;
	ItemStack lastStack = ItemStack.EMPTY;
	private int lastField2 = 0;
	boolean isStart = false;

	@Override
	public int getField(int id) {
		switch (id) {
		case 0:
		case 1:
			return super.getField(id);
		case 2:
			return isStart ? 1 : 0;
		case 3:
			return complete;
		default:
			return 0;
		}
	}

	@Override
	public void setField(int id, int value) {
		switch (id) {
		case 0:
		case 1:
			super.setField(id, value);
			break;
		case 2:
			isStart = value == 0 ? false : true;
			break;
		case 3:
			this.complete = value;
			break;
		default:
			break;
		}
	}

	@Override
	public int getFieldCount() {
		return 5;
	}

	NBTTagCompound sndTemp = new NBTTagCompound();

	protected void detectAndSendItemType() {
		ItemStack stack = inventory.getStackInSlot(0);
		if (ItemStack.areItemsEqual(lastStack, stack)) {

			if (lastField2 != this.getField(2)) {
				lastField2 = this.getField(2);
				this.updateDataWriteToNBT(sndTemp, 2);
				this.updateToClient(sndTemp);
			}

		} else {

			lastStack = stack;
			this.updateToClient(this.writeInventoryChangeToNBT(new NBTTagCompound(), 0, lastStack));

		}
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return this.writeToNBT(new NBTTagCompound());
	}

	/** 获取默认的修复结果 */
	public static ItemStack getDefaultRepairResult(ItemStack stack) {
		if (stack.isEmpty()) return ItemStack.EMPTY;
		// 扩展修复
		Item item = stack.getItem();
		Block block = Block.getBlockFromItem(item);
		if (item instanceof IExtendRepair) return ((IExtendRepair) item).getRepairOutput(stack);
		else if (block instanceof IExtendRepair) return ((IExtendRepair) block).getRepairOutput(stack);
		// 合成表修复
		for (Recipe r : recipes) {
			if (ItemStack.areItemsEqual(stack, r.input)) return r.output;
		}
		return ItemStack.EMPTY;
	}

	/** 获取修复一个的花费 */
	public static int getDefaultRepairCost(ItemStack stack) {
		// 扩展修复
		Item item = stack.getItem();
		Block block = Block.getBlockFromItem(item);
		if (item instanceof IExtendRepair) return ((IExtendRepair) item).getRepairCost(stack);
		else if (block instanceof IExtendRepair) return ((IExtendRepair) block).getRepairCost(stack);
		// 合成表修复
		for (Recipe r : recipes) {
			if (ItemStack.areItemsEqual(stack, r.input)) return r.cost;
		}
		return 0;
	}

	@Override
	public void update() {
		this.autoTransfer();
		if (this.world.isRemote) {
			if (this.isStart && Math.random() < 0.2f) {
				float x = this.pos.getX() + 0.2f + (float) Math.random() * 0.6f;
				float y = this.pos.getY() + 0.1f;
				float z = this.pos.getZ() + 0.2f + (float) Math.random() * 0.6f;
				FirewrokShap.createSpark(world, x, y, z, 0, (float) Math.random() * 0.04f + 0.08f, 0, PARTICLE_COLOR,
						PARTICLE_COLOR_FADE, false, false);
			}
			return;
		}
		if (tick % 5 == 0) this.detectAndSendItemType();
		ItemStack stack = this.getStack();
		ItemStack result = TileMDRubbleRepair.getDefaultRepairResult(stack);
		if (result.isEmpty()) {
			this.complete = 0;
			this.isStart = false;
			return;
		}
		int cost = TileMDRubbleRepair.getDefaultRepairCost(stack);
		int need = cost * stack.getCount();
		if (need > this.getCurrentCapacity()) {
			this.complete = 0;
			this.isStart = false;
			return;
		}
		this.isStart = true;
		this.complete++;
		if (this.complete >= this.getTotalComplete()) {
			this.complete = 0;
			this.magic.shrink(need);
			result = result.copy();
			result.setCount(stack.getCount());
			this.inventory.setStackInSlot(0, result);
			this.markDirty();
		}
	}

}

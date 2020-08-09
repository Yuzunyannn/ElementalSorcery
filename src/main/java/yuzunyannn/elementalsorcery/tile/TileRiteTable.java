package yuzunyannn.elementalsorcery.tile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.oredict.OreDictionary;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.crafting.element.ElementMap;
import yuzunyannn.elementalsorcery.crafting.mc.RecipeRiteWrite;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.init.ESInitInstance;
import yuzunyannn.elementalsorcery.item.ItemScroll;
import yuzunyannn.elementalsorcery.tile.TileRiteTable.Recipe.Happiness;
import yuzunyannn.elementalsorcery.util.RandomHelper;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

public class TileRiteTable extends TileEntityNetwork {

	/** 桌子的仓库 */
	protected ItemStackHandler inventory = new ItemStackHandler(6) {
		@Override
		public int getSlotLimit(int slot) {
			return 1;
		}
	};

	@Override
	public void onLoad() {
		super.onLoad();
	}

	/** 仪式桌等级 */
	protected int level;

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		this.inventory.deserializeNBT(compound.getCompoundTag("inv"));
		level = compound.getInteger("level");
		super.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setTag("inv", this.inventory.serializeNBT());
		compound.setInteger("level", level);
		return super.writeToNBT(compound);
	}

	/** 获取仓库，但是不提供能力 */
	public ItemStackHandler getInventory() {
		return inventory;
	}

	public ItemStack insert(ItemStack stack) {
		if (stack.isEmpty()) return stack;
		int n = stack.getCount();
		for (int i = 0; i < inventory.getSlots(); i++) stack = inventory.insertItem(i, stack, false);
		if (n != stack.getCount()) {
			this.updateToClient();
			this.markDirty();
			return stack;
		}
		return stack;
	}

	public ItemStack extract() {
		for (int i = inventory.getSlots() - 1; i >= 0; i--) {
			ItemStack stack = inventory.extractItem(i, 64, false);
			if (!stack.isEmpty()) {
				this.updateToClient();
				this.markDirty();
				return stack;
			}
		}
		return ItemStack.EMPTY;
	}

	public boolean rite(EntityLivingBase entity) {
		// 检测周边环境
		if (this.checkAround() == false) return false;
		if (world.isRemote) return true;
		// 寻求仪式判定
		ItemStack seekRite = ItemStack.EMPTY;
		TileRiteTable.Recipe recipe = null;
		for (int i = 0; i < inventory.getSlots(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if (stack.isEmpty()) continue;
			if (seekRite.isEmpty()) {
				if (stack.getItem() == ESInitInstance.ITEMS.PARCHMENT) {
					seekRite = stack;
					ItemStack s = RecipeRiteWrite.getInnerStack(seekRite);
					recipe = findRecipe(s);
					if (recipe != null) break;
				}
			}
		}
		// 是寻求仪式
		if (recipe != null) {
			if (this.level < recipe.needLevel()) {
				this.punish(entity);
				Block.spawnAsEntity(world, pos.up(), seekRite);
				return true;
			}
		} else seekRite = ItemStack.EMPTY;
		// 获取总能量
		List<String> pool = new LinkedList<String>();
		int power = 0;
		for (int i = 0; i < inventory.getSlots(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if (stack.isEmpty()) continue;
			if (recipe != null && stack == seekRite) continue;
			int x = sacrifice.getPower(stack);
			if (x == 0) continue;
			int level = sacrifice.getLevel(stack);
			if (level > this.level) continue;
			if (x < 0) {
				power += x;// 不好的物品直接削减
			} else {
				level = this.level + 1 - level;
				power += x / level;// 低等级的物品会受到衰减
			}
			pool.addAll(sacrifice.getHope(stack));// 期望物品加入蛋池
		}
		int rnum = recipe == null ? 100 : recipe.needPower();
		// 随机可能性
		if (power < RandomHelper.rand.nextInt(rnum)) {
			this.punish(entity);
			if (!seekRite.isEmpty()) Block.spawnAsEntity(world, pos.up(), seekRite);
			return true;
		}
		// 是寻求仪式
		if (recipe != null) {
			ItemStack output = recipe.getOutput().copy();
			output.setCount(recipe.getHappyCount(power, entity));
			ItemHelper.clear(inventory);
			Block.spawnAsEntity(world, pos.up(), output);
			Block.spawnAsEntity(world, pos.up(), seekRite);
			EntityLightningBolt lightning = new EntityLightningBolt(world, pos.getX() + 0.5f, pos.getY() + 1,
					pos.getZ() + 0.5f, true);
			world.addWeatherEffect(lightning);
			this.updateToClient();
			this.markDirty();
			return true;
		}
		// 进行随机获取
		List<String> ids = levelPages[TileRiteTable.pLevel(this.level)];
		if (ids != null) pool.addAll(ids);
		int size = pool.size();
		String[] selects;
		if (size < 4) selects = pool.toArray(new String[pool.size()]);
		else {
			// 选择25%-75%张
			size = RandomHelper.rand.nextInt(size / 2) + size / 4;
			selects = RandomHelper.randomSelect(size, pool.toArray(new String[pool.size()]));
		}
		ItemStack scroll = ItemScroll.getScroll(selects);
		Block.spawnAsEntity(world, pos.up(), scroll);
		EntityLightningBolt lightning = new EntityLightningBolt(world, pos.getX() + 0.5f, pos.getY() + 1,
				pos.getZ() + 0.5f, true);
		world.addWeatherEffect(lightning);
		ItemHelper.clear(inventory);
		this.markDirty();
		this.updateToClient();
		return true;
	}

	protected void punish(EntityLivingBase entity) {
		if (!ItemHelper.isEmpty(inventory)) {
			ItemHelper.clear(inventory);
			this.updateToClient();
			this.markDirty();
		}
		// 爆炸
		world.createExplosion(null, entity.posX, entity.posY, entity.posZ, 0.15f * this.level * this.level + 0.2f,
				false);
		// 雷劈
		if (this.level > 0) {
			EntityLightningBolt lightning = new EntityLightningBolt(world, entity.posX, entity.posY, entity.posZ,
					false);
			world.addWeatherEffect(lightning);
		}
		// 岩浆
		if (this.level > 2) {
			IBlockState state = Blocks.LAVA.getDefaultState().withProperty(BlockLiquid.LEVEL, 15);
			BlockPos pos = new BlockPos(entity.posX, entity.posY, entity.posZ);
			int size = this.level - 3;
			for (int x = -size; x <= size; x++) {
				for (int z = -size; z <= size; z++) {
					for (int y = 0; y <= size; y++) {
						BlockPos p = pos.add(x, y, z);
						if (world.getBlockState(p).getBlock().isReplaceable(world, p)) {
							world.setBlockState(p, state);
							world.neighborChanged(p, state.getBlock(), p);
						}
					}
				}
			}
		}
	}

	protected boolean checkAround() {
		BlockPos down = pos.down();
		int size = 2;
		for (int y = -1; y < 1; y++) {
			for (int x = -size; x <= size; x++) {
				for (int z = -size; z <= size; z++) {
					BlockPos p = pos.add(x, y, z);
					if (p.equals(pos) || p.equals(down)) continue;
					if (y == -1) {
						IBlockState state = world.getBlockState(p);
						if (state.getBlock() instanceof BlockFlower) continue;
						else if (state.getBlock() instanceof BlockTorch) continue;
						return false;
					} else if (!world.isAirBlock(p)) return false;
				}
			}
		}
		return true;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	static public interface ISacrificeHandle {
		public int getLevel(ItemStack stack);

		/**
		 * 获取该物品的能量点数，能量点数用来计算是否成功
		 * 
		 * @return 返回0表示不支持该物品,返回负数表示拉到车
		 */
		public int getPower(ItemStack stack);

		@Nonnull
		public List<String> getHope(ItemStack stack);

		// public NonNullList<ItemStack> getIngredients();
	}

	public static final SacrificeHandle sacrifice = new SacrificeHandle();

	static public class SacrificeHandle implements ISacrificeHandle {

		private final List<ISacrificeHandle> sacrifices = new ArrayList<>();

		public void addSacrificeHandle(ISacrificeHandle sacrifice) {
			sacrifices.add(sacrifice);
		}

		@Override
		public int getLevel(ItemStack stack) {
			for (ISacrificeHandle sacrifice : sacrifices) {
				int level = sacrifice.getLevel(stack);
				if (level > 0) return level;
			}
			return 0;
		}

		@Override
		public int getPower(ItemStack stack) {
			for (ISacrificeHandle sacrifice : sacrifices) {
				int power = sacrifice.getPower(stack);
				if (power > 0) return power;
			}
			return 0;
		}

		@Override
		public List<String> getHope(ItemStack stack) {
			for (ISacrificeHandle sacrifice : sacrifices) {
				List<String> hope = sacrifice.getHope(stack);
				if (!hope.isEmpty()) return hope;
			}
			return Collections.emptyList();
		}

	}

	// 祭品转元素处理
	static private class ElementSacrificeHandle implements ISacrificeHandle {

		@Override
		public int getLevel(ItemStack stack) {
			ElementStack[] estacks = ElementMap.instance.toElement(stack);
			if (estacks == null || estacks.length == 0) return 0;
			ElementStack estack = estacks[0];
			return estack.getPower() / 225;
		}

		@Override
		public int getPower(ItemStack stack) {
			ElementStack[] estacks = ElementMap.instance.toElement(stack);
			if (estacks == null || estacks.length == 0) return 0;
			ElementStack estack = estacks[0];
			return (int) (Math.max(MathHelper.log2(estack.getCount()), 0.5) * 2);
		}

		@Override
		public List<String> getHope(ItemStack stack) {
			return Collections.emptyList();
		}

	}

	// 祭品的处理句柄
	static private class ItemSacrificeHandle implements ISacrificeHandle {

		public static final ItemSacrificeHandle instance = new ItemSacrificeHandle();

		private static class Info {
			ItemStack stack = ItemStack.EMPTY;
			int level;
			int power;
			List<String> hope;
		}

		private final Map<ResourceLocation, Info> map = new HashMap<>();
		private final List<Info> list = new ArrayList<>();

		@Override
		public int getLevel(ItemStack stack) {
			for (Info info : list) if (ItemStack.areItemsEqual(info.stack, stack)) return info.level;
			Info info = map.get(Item.REGISTRY.getNameForObject(stack.getItem()));
			return info == null ? 0 : info.level;
		}

		@Override
		public int getPower(ItemStack stack) {
			for (Info info : list) if (ItemStack.areItemsEqual(info.stack, stack)) return info.power;
			Info info = map.get(Item.REGISTRY.getNameForObject(stack.getItem()));
			return info == null ? 0 : info.power;
		}

		@Override
		public List<String> getHope(ItemStack stack) {
			for (Info info : list) if (ItemStack.areItemsEqual(info.stack, stack)) return info.hope;
			Info info = map.get(Item.REGISTRY.getNameForObject(stack.getItem()));
			return info == null ? Collections.emptyList() : (info.hope == null ? Collections.emptyList() : info.hope);
		}

	}

	public static void addSacrifice(String ore, int power, int level, String... hope) {
		NonNullList<ItemStack> list = OreDictionary.getOres(ore);
		if (list == null || list.isEmpty()) return;
		for (ItemStack stack : list) {
			if (stack.getHasSubtypes()) addSacrifice(stack, power, level, hope);
			else addSacrifice(stack.getItem(), power, level, hope);
		}
	}

	public static void addSacrifice(Block block, int power, int level, String... hope) {
		addSacrifice(Item.getItemFromBlock(block), power, level, hope);
	}

	public static void addSacrifice(Item item, int power, int level, String... hope) {
		ItemSacrificeHandle.Info info = new ItemSacrificeHandle.Info();
		info.power = power;
		info.level = level;
		if (hope.length > 0) info.hope = Arrays.asList(hope);
		ItemSacrificeHandle.instance.map.put(Item.REGISTRY.getNameForObject(item), info);
	}

	public static void addSacrifice(ItemStack item, int power, int level, String... hope) {
		ItemSacrificeHandle.Info info = new ItemSacrificeHandle.Info();
		info.stack = item;
		info.power = power;
		info.level = level;
		if (hope.length > 0) info.hope = Arrays.asList(hope);
		ItemSacrificeHandle.instance.list.add(info);
	}

	public static void init() {
		initRecipe();
		sacrifice.addSacrificeHandle(ItemSacrificeHandle.instance);
		sacrifice.addSacrificeHandle(new ElementSacrificeHandle());

		ESObjects.Blocks BLOCKS = ESInitInstance.BLOCKS;
		ESObjects.Items ITEMS = ESInitInstance.ITEMS;
		addSacrifice(ITEMS.RESONANT_CRYSTAL, 100, 0, "resonant_crystal");
		addSacrifice(Blocks.COBBLESTONE, -10, 0, "hearth", "smelt_box");
		addSacrifice(Blocks.DIRT, -5, 0);
		addSacrifice(Blocks.STONE, 8, 0);
		addSacrifice(Blocks.LOG, 10, 0);
		addSacrifice(Blocks.LOG2, 10, 0);
		addSacrifice(Items.COAL, 15, 0);
		addSacrifice(Blocks.COAL_BLOCK, 25, 0);

		addSacrifice("oreIron", 11, 1);
		addSacrifice(BLOCKS.KYANITE_BLOCK, 18, 1, "kyanite");
		addSacrifice(ITEMS.KYANITE, 9, 1, "kyanite");
		addSacrifice(ITEMS.KYANITE_AXE, 15, 1, "kyanite_tools");
		addSacrifice(ITEMS.KYANITE_HOE, 15, 1, "kyanite_tools");
		addSacrifice(ITEMS.KYANITE_PICKAXE, 15, 1, "kyanite_tools");
		addSacrifice(ITEMS.KYANITE_SPADE, 10, 1, "kyanite_tools");
		addSacrifice(ITEMS.KYANITE_SWORD, 12, 1, "kyanite_tools");
		addSacrifice(BLOCKS.STAR_SAND, 15, 1, "star_sand");
		addSacrifice(BLOCKS.STAR_STONE, 9, 1, "star_sand");

		addSacrifice(Items.DIAMOND, 95, 2);
		addSacrifice(Items.DIAMOND_AXE, 130, 2);
		addSacrifice(Items.DIAMOND_HOE, 130, 2);
		addSacrifice(Items.DIAMOND_PICKAXE, 130, 2);
		addSacrifice(Items.DIAMOND_SHOVEL, 110, 2);
		addSacrifice(Items.DIAMOND_SWORD, 120, 2);
		addSacrifice(Blocks.DIAMOND_BLOCK, 500, 2);

		addSacrifice(ITEMS.MAGIC_CRYSTAL, 10, 3, "magic_crystal");
		addSacrifice(ITEMS.ELEMENT_CRYSTAL, 25, 3, "element_crystal");
		addSacrifice(ITEMS.AZURE_CRYSTAL, 15, 3, "azure_crystal");

		addSacrifice(BLOCKS.LIFE_DIRT, 16, 4);
		addSacrifice(ITEMS.ARCHITECTURE_CRYSTAL, 30, 4);
		addSacrifice(ITEMS.ITEM_CRYSTAL, 30, 4);
		addSacrifice(ITEMS.ORDER_CRYSTAL, 20, 4);
	}

	/** 所有的页面等级，从0开始 */
	public static final int MAX_LEVEL = 5;
	private static final List<String>[] levelPages = new List[MAX_LEVEL + 1];

	/** 添加一个页面到指定等级上 */
	public static void addPage(String id, int lev) {
		if (id == null || id.isEmpty()) return;
		lev = pLevel(lev);
		List<String> list = levelPages[lev];
		if (list == null) list = levelPages[lev] = new ArrayList<>();
		list.add(id);
	}

	public static int pLevel(int level) {
		if (level < 0) return 0;
		if (level > MAX_LEVEL) return MAX_LEVEL;
		return level;
	}

	/** 仪式合成表 */
	public static class Recipe {

		public interface Happiness {
			int getHappyCount(int power, EntityLivingBase entity);
		}

		ItemStack parchmentInput = ItemStack.EMPTY;
		ItemStack output = ItemStack.EMPTY;
		int needPower = 100;
		int needLevel = 0;
		Happiness happy = null;

		public ItemStack parchmentInput() {
			return parchmentInput;
		}

		public ItemStack getOutput() {
			return output;
		}

		public int needPower() {
			return needPower;
		}

		public int needLevel() {
			return needLevel;
		}

		public int getHappyCount(int power, EntityLivingBase entity) {
			return happy == null ? output.getCount() : happy.getHappyCount(power, entity);
		}
	}

	private static final List<Recipe> recipes = new ArrayList<TileRiteTable.Recipe>();

	public static TileRiteTable.Recipe findRecipe(ItemStack parchmentInput) {
		if (parchmentInput.isEmpty()) return null;
		for (TileRiteTable.Recipe r : TileRiteTable.getRecipes())
			if (ItemHelper.areItemsEqual(parchmentInput, r.parchmentInput())) return r;
		return null;
	}

	public static List<Recipe> getRecipes() {
		return recipes;
	}

	public static void addRecipe(ItemStack parchmentInput, ItemStack output, int needPower, int needLevel,
			Happiness happy) {
		if (parchmentInput.isEmpty() || output.isEmpty()) return;
		Recipe r = new Recipe();
		r.needPower = needPower;
		r.needLevel = pLevel(needLevel);
		r.parchmentInput = parchmentInput;
		r.output = output;
		r.happy = happy;
		recipes.add(r);
	}

	public static void addRecipe(ItemStack parchmentInput, ItemStack output, int needPower, int needLevel) {
		addRecipe(parchmentInput, output, needPower, needLevel, null);
	}

	public static void initRecipe() {
		addRecipe(new ItemStack(Blocks.TORCH), new ItemStack(ESInitInstance.BLOCKS.LANTERN), 70, 0, (power, entity) -> {
			int x = power - 80;
			if (x > 0) return Math.max(1, x / 12);
			return 1;
		});
		addRecipe(new ItemStack(ESInitInstance.ITEMS.ELF_COIN), new ItemStack(ESInitInstance.ITEMS.ELF_PURSE), 120, 0);
	}
}

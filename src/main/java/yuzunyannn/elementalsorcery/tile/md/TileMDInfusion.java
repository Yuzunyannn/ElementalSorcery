package yuzunyannn.elementalsorcery.tile.md;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.items.ItemStackHandler;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.building.Buildings;
import yuzunyannn.elementalsorcery.building.MultiBlock;
import yuzunyannn.elementalsorcery.config.Config;
import yuzunyannn.elementalsorcery.crafting.element.ElementMap;
import yuzunyannn.elementalsorcery.elf.ElfTime;
import yuzunyannn.elementalsorcery.util.json.ItemRecord;
import yuzunyannn.elementalsorcery.util.json.Json;

public class TileMDInfusion extends TileMDBase implements ITickable {

	@Config
	static private int GEN_MAX_MAIGC_PER_SEC = 10;

	@Config(sync = true)
	static private int MAX_CAPACITY = 1000;

	@Override
	protected ItemStackHandler initItemStackHandler() {
		return new ItemStackHandler(5) {
			@Override
			public int getSlotLimit(int slot) {
				return 1;
			}
		};
	}

	private int[] infusionPower = new int[5];
	private MultiBlock structure;
	private boolean ok;

	@Override
	public void onLoad() {
		structure = new MultiBlock(Buildings.INFUSION, this, new BlockPos(0, -2, 0));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setIntArray("powers", infusionPower);
		return super.writeToNBT(nbt);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		this.infusionPower = nbt.getIntArray("powers");
		if (this.infusionPower.length < 5) this.infusionPower = new int[5];
		super.readFromNBT(nbt);
	}

	@Override
	public int getMaxCapacity() {
		return MAX_CAPACITY;
	}

	@Override
	protected int getOverflow() {
		return MAX_CAPACITY / 2;
	}

	public void updateMagicGrow() {
		if (tick % 20 != 0) return;
		if (world.isRemote) return;

		ElfTime time = new ElfTime(world);
		if (time.at(ElfTime.Period.DAY)) return;

		if (!world.canBlockSeeSky(pos.up())) return;

		int midNight = ElfTime.Period.MIDNIGHT.center();
		int diff = Math.abs(midNight - time.getTime());
		int length = (ElfTime.Period.NIGHT.length() + ElfTime.Period.DUSK.length()) / 2;

		float rate = MathHelper.clamp(1 - diff / (float) length, 0, 1);
		float high = MathHelper.sqrt(pos.getY() / 256f);
		float weather = world.isRaining() ? (float) Math.random() * 0.15f : 1;

		float light = 1 - world.getLightBrightness(pos);
		light = light * light;
		rate *= light;

		rate *= (world.rand.nextFloat() * 0.2f + 0.9f);

		int count = MathHelper.floor((rate * high * weather) * GEN_MAX_MAIGC_PER_SEC);
		if (count <= 0) return;

		if (getCurrentCapacity() < getMaxCapacity()) {
			this.magic.grow(ElementStack.magic(count, (int) (10 + (rate * 50 + high * 20) * weather)));
			if (getCurrentCapacity() > getMaxCapacity()) this.magic.setCount(getMaxCapacity());
		}
	}

	@Override
	public int getField(int id) {
		switch (id) {
		case 0:
		case 1:
			return super.getField(id);
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
			return infusionPower[id - 2];
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
		case 3:
		case 4:
		case 5:
		case 6:
			infusionPower[id - 2] = value;
			break;
		}
	}

	@Override
	public int getFieldCount() {
		return 7;
	}

	public int getInfusionPower(int index) {
		return infusionPower[index];
	}

	public int getInfusionPowerMax(int index) {
		switch (index) {
		case 0:
		case 4:
		case 1:
		case 3:
		case 2:
			return TileMDInfusion.getMaxOfferMagic(index).getCount() * 2;
		default:
			return 1;
		}
	}

	public final static ElementStack MAGIC1 = new ElementStack.Unchangeable(ESObjects.ELEMENTS.MAGIC, 20, 25);
	public final static ElementStack MAGIC2 = new ElementStack.Unchangeable(ESObjects.ELEMENTS.MAGIC, 40, 100);
	public final static ElementStack MAGIC3 = new ElementStack.Unchangeable(ESObjects.ELEMENTS.MAGIC, 100, 200);

	public static ElementStack getMaxOfferMagic(int index) {
		switch (index) {
		case 0:
		case 4:
			return MAGIC1;
		case 1:
		case 3:
			return MAGIC2;
		case 2:
			return MAGIC3;
		default:
			return ElementStack.EMPTY;
		}
	}

	@Override
	public void update() {
		this.autoTransfer();
		if (world.isRemote) return;
		if (tick % 30 == 0) ok = structure.check(EnumFacing.NORTH);
		if (!ok) {
			this.allPowerDrop();
			return;
		}
		this.updateMagicGrow();

		// 对有所物品进行遍历
		for (int i = 0; i < infusionPower.length; i++) {
			if (this.magic.isEmpty()) {
				this.powerDrop(i);
				continue;
			}
			ItemStack stack = this.inventory.getStackInSlot(i);
			ElementStack maxMagic = TileMDInfusion.getMaxOfferMagic(i).copy();
			maxMagic.setPower(this.magic.getPower());
			// 寻找注魔结果，并判断
			ItemStack result = TileMDInfusion.infusionInto(stack, maxMagic, world, pos);
			if (result.isEmpty()) {
				this.powerDrop(i);
				continue;
			}
			// 增加注魔进度
			this.infusionPower[i]++;
			if (this.infusionPower[i] % 2 == 0) this.magic.shrink(1);
			if (this.infusionPower[i] >= this.getInfusionPowerMax(i)) {
				this.infusionPower[i] = 0;
				this.inventory.setStackInSlot(i, result.copy());
				this.markDirty();
			}
		}
	}

	private void allPowerDrop() {
		for (int i = 0; i < this.infusionPower.length; i++) this.powerDrop(i);
	}

	private final void powerDrop(int index) {
		if (this.infusionPower[index] > 0) this.infusionPower[index]--;
	}

	/**
	 * 寻找某个注魔结果
	 * 
	 * @param stack      原始物品
	 * @param offerMagic 提供的魔力值
	 * @return 注魔后的物品
	 * 
	 */
	public static ItemStack infusionInto(ItemStack stack, ElementStack offerMaxMagic, World world, BlockPos pos) {
		for (Recipe r : recipes) {
			if (!ItemStack.areItemsEqual(r.getInput(), stack)) continue;
			ElementStack magic = r.getCost();
			if (magic.getCount() > offerMaxMagic.getCount()) continue;
			if (magic.getPower() > offerMaxMagic.getPower()) continue;
			if (r.test != null && !r.test.test(world, pos)) continue;
			return r.getOutput();
		}
		return ItemStack.EMPTY;
	}

	static public class Recipe {

		static public interface ITest {
			boolean test(World world, BlockPos pos);
		}

		protected ItemStack input = ItemStack.EMPTY;
		protected ItemStack output = ItemStack.EMPTY;
		protected ElementStack cost;
		protected ITest test = null;

		public ItemStack getInput() {
			return input;
		}

		public ItemStack getOutput() {
			return output;
		}

		public ElementStack getCost() {
			return cost;
		}
	}

	static final private List<Recipe> recipes = new ArrayList<>();

	public static List<Recipe> getRecipes() {
		return recipes;
	}

	static public void addRecipe(ItemStack input, ItemStack output, ElementStack cost, Recipe.ITest test) {
		if (input.isEmpty() || output.isEmpty() || cost == null || cost.isEmpty()) return;
		if (!cost.isMagic()) return;
		Recipe r = new Recipe();
		r.input = input;
		r.output = output;
		r.cost = cost;
		r.test = test;
		recipes.add(r);
	}

	static public void addRecipe(Item input, Item output, int lowerLimitCount, int lowerLimitPower, Recipe.ITest test) {
		addRecipe(new ItemStack(input), new ItemStack(output), ElementStack.magic(lowerLimitCount, lowerLimitPower),
				test);
	}

	static public void init() {
		recipes.clear();
		final ESObjects.Items ITEMS = ESObjects.ITEMS;
		Recipe.ITest awalysTrue = (world, pos) -> {
			return true;
		};
		addRecipe(new ItemStack(ITEMS.QUILL, 1, 0), new ItemStack(ITEMS.QUILL, 1, 1), ElementStack.magic(40, 20),
				awalysTrue);
		addRecipe(Items.REEDS, ITEMS.NATURE_CRYSTAL, 20, 20, awalysTrue);
		addRecipe(Items.GOLD_INGOT, ITEMS.MAGIC_GOLD, 40, 20, awalysTrue);
		addRecipe(ITEMS.MAGIC_CRYSTAL, ESObjects.ITEMS.ELEMENT_CRYSTAL, 100, 20, (world, pos) -> {
			Biome biome = world.getBiome(pos);
			ElfTime time = new ElfTime(world);
			if (biome != Biomes.PLAINS) return false;
			if (time.at(ElfTime.Period.DAWN) || time.at(ElfTime.Period.DUSK)) return true;
			return false;
		});
		addRecipe(ESObjects.ITEMS.KYANITE, ESObjects.ITEMS.MAGIC_CRYSTAL, 20, 10, (world, pos) -> {
			Biome biome = world.getBiome(pos);
			ElfTime time = new ElfTime(world);
			if (time.at(ElfTime.Period.DAY)) return biome.getRainfall() <= 0.5f && !world.isRaining();
			return false;
		});
		addRecipe(ESObjects.ITEMS.MAGIC_CRYSTAL, ESObjects.ITEMS.SPELL_CRYSTAL, 40, 20, (world, pos) -> {
			ElfTime time = new ElfTime(world);
			if (!time.at(ElfTime.Period.MIDNIGHT)) return false;
			int count = 0;
			for (int y = -1; y <= 1; y++) {
				for (int x = -2; x <= 2; x++) {
					for (int z = -2; z <= 2; z++) {
						BlockPos movePos = pos.add(x, y, z);
						if (world.getBlockState(movePos).getBlock() == Blocks.BOOKSHELF) count++;
						if (count >= 14) {
							y = 3;
							x = 3;
							z = 3;
							break;
						}
					}
				}
			}
			return count >= 14;
		});
		addRecipe(ESObjects.ITEMS.ELF_CRYSTAL, ESObjects.ITEMS.ARCHITECTURE_CRYSTAL, 40, 20, (world, pos) -> {
			Biome biome = world.getBiome(pos);
			if (biome != Biomes.DESERT && biome != Biomes.DESERT_HILLS && biome != Biomes.MUTATED_DESERT) return false;
			final int size = 4;
			Vec3d v3d = new Vec3d(pos).add(0.5, 0.5, 0.5);
			AxisAlignedBB aabb = new AxisAlignedBB(v3d.x - size, v3d.y - size, v3d.z - size, v3d.x + size, v3d.y + size,
					v3d.z + size);
			List<EntityItem> list = world.getEntitiesWithinAABB(EntityItem.class, aabb);
			int count = 0;
			for (EntityItem ei : list) {
				ItemStack stack = ei.getItem();
				Block block = Block.getBlockFromItem(stack.getItem());
				if (block == null || block == Blocks.AIR) continue;
				if (!block.getDefaultState().isFullBlock()) continue;
				count += stack.getCount();
			}
			return count >= 320;
		});

		// 自定义
		Json.ergodicFile("recipes/enchanting_box", (file, json) -> {
			if (!ElementMap.checkModDemands(json)) return false;
			ItemRecord input = json.needItem("input");
			ItemRecord output = json.needItem("output");
			int count = json.needNumber("magic", "count", "lowerLimitMagic", "lowerLimitConut").intValue();
			int power = json.needNumber("power", "lowerLimitPower").intValue();
			addRecipe(input.getStack(), output.getStack(), ElementStack.magic(count, power), awalysTrue);
			return true;
		});
	}
}

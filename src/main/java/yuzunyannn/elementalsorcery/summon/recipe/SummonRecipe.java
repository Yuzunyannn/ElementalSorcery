package yuzunyannn.elementalsorcery.summon.recipe;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiFunction;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.item.prop.ItemKeepsake;
import yuzunyannn.elementalsorcery.summon.Summon;
import yuzunyannn.elementalsorcery.summon.SummonDejectedSkeleton;
import yuzunyannn.elementalsorcery.summon.SummonDreadCube;
import yuzunyannn.elementalsorcery.summon.SummonMaze;
import yuzunyannn.elementalsorcery.summon.SummonRabidRabbit;
import yuzunyannn.elementalsorcery.summon.SummonRelicZombie;
import yuzunyannn.elementalsorcery.summon.SummonSilverfishSpring;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

public class SummonRecipe extends IForgeRegistryEntry.Impl<SummonRecipe> {

	public static final Map<ResourceLocation, SummonRecipe> REGISTRY = new HashMap<>();

	public static void register(SummonRecipe summonRecipe) {
		REGISTRY.put(summonRecipe.getRegistryName(), summonRecipe);
	}

	public static SummonRecipe get(ResourceLocation id) {
		return REGISTRY.get(id);
	}

	public static SummonRecipe get(String id) {
		return get(new ResourceLocation(id));
	}

	public static SummonRecipe findRecipeWithKeepsake(ItemStack keepsake, World world, BlockPos pos) {
		for (Entry<?, SummonRecipe> entry : REGISTRY.entrySet()) {
			SummonRecipe summonRecipe = entry.getValue();
			if (summonRecipe.canBeKeepsake(keepsake, world, pos)) return summonRecipe;
		}
		return null;
	}

	protected BiFunction<World, BlockPos, Summon> summonFatory = (world, pos) -> {
		return new Summon(world, pos);
	};
	protected ItemStack[] keepsakes = new ItemStack[0];
	protected int cost = 50;
	protected int color = 0xda003e;
	protected int buildHeight = 0;

	public SummonRecipe setKeepsakes(ItemStack... keepsake) {
		this.keepsakes = keepsake;
		return this;
	}

	public SummonRecipe setCost(int cost) {
		this.cost = cost;
		return this;
	}

	public SummonRecipe setColor(int color) {
		this.color = color;
		return this;
	}

	public SummonRecipe setBuildHeight(int buildHeight) {
		this.buildHeight = buildHeight;
		return this;
	}

	public SummonRecipe setSummonClass(Class<? extends Summon> summonClass) {
		this.summonFatory = (world, pos) -> {
			try {
				Constructor<Summon> constructor = (Constructor<Summon>) summonClass.getConstructor(World.class,
						BlockPos.class);
				return constructor.newInstance(world, pos);
			} catch (Exception e) {
				ElementalSorcery.logger.warn("创建召唤任务异常", e);
				return new Summon(world, pos);
			}
		};
		return this;
	}

	public SummonRecipe setSummonFatory(BiFunction<World, BlockPos, Summon> summonFatory) {
		this.summonFatory = summonFatory;
		return this;
	}

	/** 该方案的颜色 */
	public int getColor(ItemStack keepsake) {
		return color;
	}

	/**
	 * 是否可以作为信物
	 * 
	 * @param pos 召唤处理的地点
	 */
	public boolean canBeKeepsake(ItemStack keepsake, World world, BlockPos pos) {
		if (world.isOutsideBuildHeight(pos.up(buildHeight))) return false;
		for (ItemStack stack : this.keepsakes) if (stack.isItemEqual(keepsake)) return true;
		return false;
	}

	/**
	 * 获取灵魂的消耗
	 * 
	 * @return 返回消耗值
	 */
	public int getSoulCost(ItemStack keepsake, World world, BlockPos pos) {
		return cost;
	}

	/**
	 * 创建信物
	 * 
	 * @param pos 召唤处理的地点
	 */
	public Summon createSummon(ItemStack keepsake, World world, BlockPos pos) {
		return summonFatory.apply(world, pos);
	}

	public static void registerAll() {
		reg("silverfish_spring", SummonSilverfishSpring.class, 96, 0x109e41, new ItemStack(Items.EXPERIENCE_BOTTLE));
		reg("zombie_cage", new SummonRecipeZombieCage());
		reg("rabid_rabbit_spring", SummonRabidRabbit.class, 128, 0xf2e9ea,
				ItemHelper.toArray(Items.RABBIT_FOOT, Items.RABBIT_HIDE));
		reg("mob_create", new SummonRecipeMob());
		reg("relic_zombie", SummonRelicZombie.class, 192, 0x285f57,
				ItemKeepsake.create(ItemKeepsake.EnumType.RELIC_FRAGMENT, 1));
		reg("dread_cube", SummonDreadCube.class, 256, 0x671111,
				ItemKeepsake.create(ItemKeepsake.EnumType.DREAD_FRAGMENT, 1));
		reg("dejected_skeleton", SummonDejectedSkeleton.class, 125, 0x757575,
				ItemKeepsake.create(ItemKeepsake.EnumType.UNDELIVERED_LETTER, 1));
		reg("maze", SummonMaze.class, 200, 0x3f9e15, new ItemStack(ESInit.BLOCKS.ELF_LEAF)).setBuildHeight(3);
		reg("arrogant_sheep", new SummonRecipeArrogantSheep());

	}

	private static SummonRecipe reg(String name, SummonRecipe m) {
		register(m.setRegistryName(new ResourceLocation(ElementalSorcery.MODID, name)));
		return m;
	}

	private static SummonRecipe reg(String name, Class<? extends Summon> summonClass, int cost, int color,
			ItemStack... keepsake) {
		return reg(name,
				new SummonRecipe().setCost(cost).setKeepsakes(keepsake).setSummonClass(summonClass).setColor(color));
	}

	@SuppressWarnings("unused")
	private static SummonRecipe reg(String name, BiFunction<World, BlockPos, Summon> summonFatory, int cost, int color,
			ItemStack... keepsake) {
		SummonRecipe sr = new SummonRecipe().setCost(cost).setKeepsakes(keepsake).setSummonFatory(summonFatory)
				.setColor(color);
		return reg(name, sr);
	}

}

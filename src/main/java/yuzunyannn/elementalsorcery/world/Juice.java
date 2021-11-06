package yuzunyannn.elementalsorcery.world;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.api.element.IElemetJuice;
import yuzunyannn.elementalsorcery.api.item.IJuice;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.block.BlockElfFruit;
import yuzunyannn.elementalsorcery.capability.ElementInventory;
import yuzunyannn.elementalsorcery.element.Element;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.item.ItemGlassCup;
import yuzunyannn.elementalsorcery.render.item.RenderItemGlassCup;
import yuzunyannn.elementalsorcery.util.MultiRets;
import yuzunyannn.elementalsorcery.util.helper.ColorHelper;
import yuzunyannn.elementalsorcery.util.helper.EntityHelper;
import yuzunyannn.elementalsorcery.util.helper.NBTHelper;

public class Juice implements IJuice {

	public final ItemStack glassCup;
	public final NBTTagCompound juiceData;

	public Juice(ItemStack stack) {
		glassCup = stack;
		juiceData = glassCup.getOrCreateSubCompound("juice");
	}

	public Juice(NBTTagCompound juiceData) {
		glassCup = ItemStack.EMPTY;
		this.juiceData = juiceData;
	}

	public NBTTagCompound getJuiceData() {
		return juiceData;
	}

	@Override
	public float getMaxJuiceCount() {
		return 1000;
	}

	@Override
	public float getJuiceCount() {
		return juiceData.getFloat("water");
	}

	public int getColor() {
		if (juiceData.hasKey("colorCache")) return juiceData.getInteger("colorCache");
		float r = 0, g = 0, b = 0;

		float allRate = 1;
		NBTTagCompound materials = NBTHelper.getOrCreateNBTTagCompound(juiceData, "material");
		float water = getJuiceCount();
		for (String key : materials.getKeySet()) {
			JuiceMaterial material = JuiceMaterial.fromKey(key);
			if (material == null) continue;
			if (material.color == 0) continue;

			float rate = materials.getFloat(key) * material.occupancy / water;
			allRate -= rate;

			Vec3d color = ColorHelper.color(material.color);
			r += color.x * rate;
			g += color.y * rate;
			b += color.z * rate;
		}

		if (allRate > 0) {
			r += 0.2f * allRate;
			g += 0.3f * allRate;
			b += 1f * allRate;
		}

		int c = ColorHelper.color(new Vec3d(r, g, b));
		juiceData.setInteger("colorCache", c);

		return c;
	}

	/** 获取成分 */
	public float component(JuiceMaterial material) {
		if (JuiceMaterial.WATER == material) return juiceData.getFloat("water");
		NBTTagCompound materials = NBTHelper.getOrCreateNBTTagCompound(juiceData, "material");
		return materials.getFloat(material.key);
	}

	/** 添加一种材料 */
	public float modulate(JuiceMaterial material, float count) {
		float originWater = getJuiceCount();
		if (originWater >= getMaxJuiceCount()) return count;
		float needWater = getMaxJuiceCount() - originWater;
		float remainCount = 0;
		float addWater = material.occupancy * count;
		if (needWater < addWater) {
			remainCount = (addWater - needWater) / material.occupancy;
			addWater = needWater;
		}
		juiceData.setFloat("water", addWater + originWater);
		juiceData.removeTag("colorCache");

		if (JuiceMaterial.WATER == material) return remainCount;

		NBTTagCompound materials = NBTHelper.getOrCreateNBTTagCompound(juiceData, "material");
		float mc = materials.getFloat(material.key);
		materials.setFloat(material.key, mc + (count - remainCount));

		return remainCount;
	}

	/** 喝一定量的果汁，返回材料浓度 */
	public MultiRets drink(float water, boolean justTry) {
		float originWater = getJuiceCount();

		Map<JuiceMaterial, Float> drinkMap = new HashMap<>();
		if (water >= originWater) {
			NBTTagCompound materials = juiceData.getCompoundTag("material");
			for (String key : materials.getKeySet()) {
				JuiceMaterial material = JuiceMaterial.fromKey(key);
				if (material != null) {
					float count = materials.getFloat(key);
					drinkMap.put(material, count);
				}
			}
			if (!justTry) {
				juiceData.removeTag("material");
				juiceData.setFloat("water", 0);
			}
			return MultiRets.ret(drinkMap, 1);
		}

		float r = water / originWater;
		NBTTagCompound materials = juiceData.getCompoundTag("material");
		List<String> removeKeys = new LinkedList<>();
		for (String key : materials.getKeySet()) {
			JuiceMaterial material = JuiceMaterial.fromKey(key);
			if (material != null) {
				float count = materials.getFloat(key);
				float realCount = r * count;
				drinkMap.put(material, realCount);
				if (!justTry) {
					float remainCount = count - realCount;
					if (remainCount < 0.1f) removeKeys.add(key);
					else materials.setFloat(key, remainCount);
				}
			}
		}
		for (String key : removeKeys) materials.removeTag(key);
		if (!justTry) juiceData.setFloat("water", originWater - water);
		return MultiRets.ret(drinkMap, r);
	}

	@Override
	public boolean canDrink(World world, EntityLivingBase drinker) {
		return getJuiceCount() >= 20;
	}

	@Override
	public void onDrink(World world, EntityLivingBase drinker, IElementInventory eInv) {
		if (world.isRemote) return;
		float drinkCount = drinker.getRNG().nextFloat() * 300 + 300;
		MultiRets rets = drink(drinkCount, false);
		Map<JuiceMaterial, Float> drinkMap = rets.get(0, Map.class);
		float rate = rets.getNumber(1, Float.class);

		if (drinkMap.isEmpty()) return;

		float sugar = 0;
		float coco = 0;
		float elfFruit = 0;

		boolean isAddFoodStats = false;
		for (Entry<JuiceMaterial, Float> entry : drinkMap.entrySet()) {
			JuiceMaterial material = entry.getKey();
			float count = entry.getValue();
			// 吃饭，回复饱和
			if (!material.item.isEmpty()) {
				if (drinker instanceof EntityPlayer) {
					if (addFoodStats((EntityPlayer) drinker, material, count)) isAddFoodStats = true;
				}
			}
			// 计数
			if (material == JuiceMaterial.SUGAR) sugar += count;
			else if (material == JuiceMaterial.COCO) coco += count;
			else if (material == JuiceMaterial.ELF_FRUIT) elfFruit += count;
		}

		if (isAddFoodStats) {
			world.playSound((EntityPlayer) null, drinker.posX, drinker.posY, drinker.posZ,
					SoundEvents.BLOCK_ANVIL_BREAK, SoundCategory.PLAYERS, 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
		}

		if (sugar > 6) drinker.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, (int) (20 * 1.5f * sugar)));
		if (coco > 6) {
			int level = Math.min(3, (int) (coco / 10));
			drinker.addPotionEffect(new PotionEffect(MobEffects.POISON, (int) (20 * 2f * coco), level));
		}

		sugar = MathHelper.sqrt(sugar) / 4;
		coco = MathHelper.sqrt(coco) / 6;

		if (elfFruit > 0) {
			if (drinkMap.containsKey(JuiceMaterial.MELON)) {
				float melon = drinkMap.get(JuiceMaterial.MELON);
				int time = (int) (elfFruit * 8 * 20 * (sugar * 0.8f + 1));
				int level = (int) Math.min(2, melon / 4);
				drinker.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, time, level));
			}
			if (drinkMap.containsKey(JuiceMaterial.APPLE)) {
				float apple = drinkMap.get(JuiceMaterial.APPLE) * (coco + 1);
				int time = (int) (elfFruit * 40 * 20 * (sugar + 1));
				int level = (int) Math.min(3, apple / 3);
				drinker.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, time, level));
			}
		}

		if (eInv == null) return;

		for (int i = 0; i < eInv.getSlots(); i++) {
			ElementStack estack = eInv.getStackInSlot(i);
			if (estack.isEmpty()) continue;

			int count = (int) (rate * estack.getCount());
			estack = estack.splitStack(count);

			Element elemet = estack.getElement();
			if (elemet instanceof IElemetJuice) {
				((IElemetJuice) elemet).onDrinkJuice(world, drinker, estack, drinkCount, drinkMap);
			}
		}
	}

	protected boolean addFoodStats(EntityPlayer player, JuiceMaterial material, float count) {
		ItemStack stack = material.item;
		Item item = stack.getItem();

		int foodLevelIn = 0;
		float foodSaturationModifier = 0;

		if (item instanceof ItemFood) {
			stack = stack.copy();
			ItemFood foodItem = (ItemFood) item;
			foodLevelIn = foodItem.getHealAmount(stack);
			foodSaturationModifier = foodItem.getSaturationModifier(stack);
			// foodItem.onFoodEaten(stack, world, player);
		} else if (material == JuiceMaterial.ELF_FRUIT) {
			foodLevelIn = 1;
			foodSaturationModifier = 0.6f;
		}

		foodLevelIn = (int) (foodLevelIn * count);
		if (foodLevelIn == 0) return false;

		player.getFoodStats().addStats(foodLevelIn, foodSaturationModifier);
		return true;
	}

	@Override
	public boolean onContain(World world, EntityLivingBase drinker, BlockPos pos) {

		if (getJuiceCount() >= getMaxJuiceCount()) return false;
		IBlockState state = world.getBlockState(pos);

		if (state.getBlock() == Blocks.WATER) {
			modulate(JuiceMaterial.WATER, 200);
			return true;
		} else if (state.getBlock() == Blocks.LAVA) {
			boolean isCreative = EntityHelper.isCreative(drinker);
			if (!glassCup.isEmpty() && !isCreative) {
				world.playSound(null, pos, SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F);
				glassCup.shrink(1);
				return true;
			}
		}

		if (ElementalSorcery.isDevelop) {
//			modulate(JuiceMaterial.ELF_FRUIT, 1f);
//			modulate(JuiceMaterial.MELON, 0.5f);
//			modulate(JuiceMaterial.APPLE, 1);
//			modulate(JuiceMaterial.COCO, 20);
//			modulate(JuiceMaterial.SUGAR, 6);
//			juiceData.setFloat("water", 200);
//			addElement(new ElementStack(ESInit.ELEMENTS.FIRE, 10, 20));
			return true;
		}

		return false;
	}

	@Override
	public IElementInventory getElementInventory(ItemStack stack) {
		return new JuiceElementInventory(stack);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void beforeRenderJuice() {
		RenderItemGlassCup.TEXTURE_FLUID.bind();
		Vec3d color = ColorHelper.color(getColor());
		GlStateManager.color((float) color.x, (float) color.y, (float) color.z);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addJuiceInformation(World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		NBTTagCompound materials = NBTHelper.getOrCreateNBTTagCompound(juiceData, "material");
		float water = getJuiceCount();
		for (String key : materials.getKeySet()) {
			JuiceMaterial material = JuiceMaterial.fromKey(key);
			if (material == null) continue;
			String name = material.item.getDisplayName();
			if (material.isMain) name = TextFormatting.GOLD + name;
			float count = materials.getFloat(key);
			tooltip.add(String.format("%sx%.1f - %.2f%%", name, count, count * material.occupancy / water));
		}
	}

	public static enum JuiceMaterial {

		WATER("water", 1, 0, ItemStack.EMPTY, false),
		ELF_FRUIT("EF", 120, 0x055f11, new ItemStack(ESInit.BLOCKS.ELF_FRUIT, 1, BlockElfFruit.MAX_STATE), true),
		APPLE("AP", 80, 0xffd368, Items.APPLE, true),
		MELON("ME", 200, 0xbd2417, Items.MELON, true),
		SUGAR("SU", 5, 0, Items.SUGAR, false),
		COCO("CO", 10, 0x704425, new ItemStack(Items.DYE, 1, 3), false);

		static final Map<String, JuiceMaterial> keysMap = new TreeMap<>();

		static {
			for (JuiceMaterial material : values()) {
				keysMap.put(material.key, material);
			}
		}

		final public String key;
		final public float occupancy;
		final public boolean isMain;
		final public int color;
		final public ItemStack item;

		JuiceMaterial(String key, float occupancy, int color, ItemStack item, boolean isMain) {
			this.key = key;
			this.occupancy = occupancy;
			this.isMain = isMain;
			this.item = item;
			this.color = color;
		}

		JuiceMaterial(String key, float occupancy, int color, Item item, boolean isMain) {
			this(key, occupancy, color, new ItemStack(item), isMain);
		}

		public static JuiceMaterial fromKey(String key) {
			return keysMap.get(key);
		}

	}

	static public class JuiceElementInventory extends ElementInventory {

		public final ItemStack juiceStack;

		public JuiceElementInventory(ItemStack stack) {
			juiceStack = stack;
		}

		@Override
		public boolean insertElement(int slot, ElementStack estack, boolean simulate) {
			IJuice ijuice = ItemGlassCup.getJuice(juiceStack);
			if (ijuice instanceof Juice) {
				Juice juice = (Juice) ijuice;
				if (juice.component(JuiceMaterial.ELF_FRUIT) <= 0.001f) return false;
			}
			return super.insertElement(slot, estack, simulate);
		}

		@Override
		public ElementStack extractElement(ElementStack estack, boolean simulate) {
			return ElementStack.EMPTY;
		}

		@Override
		public ElementStack extractElement(int slot, ElementStack estack, boolean simulate) {
			return ElementStack.EMPTY;
		}
	}

}

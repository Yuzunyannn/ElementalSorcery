package yuzunyannn.elementalsorcery.element;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.util.IWorldObject;
import yuzunyannn.elementalsorcery.api.util.WorldTarget;
import yuzunyannn.elementalsorcery.element.explosion.EEStar;
import yuzunyannn.elementalsorcery.element.explosion.ElementExplosion;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.util.VariableSet;
import yuzunyannn.elementalsorcery.util.Variables;
import yuzunyannn.elementalsorcery.util.element.DrinkJuiceEffectAdder;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;
import yuzunyannn.elementalsorcery.world.JuiceMaterial;

public class ElementStar extends ElementCommon {

	public ElementStar() {
		super(0xcaf4ff, "star");
		setTransition(4f, 0, 360);
	}

	@Override
	public int complexWith(ItemStack stack, ElementStack estack, ElementStack other) {
		return 8;
	}

	@Override
	public ElementStack starFlowerCasting(World world, BlockPos pos, ElementStack estack, int tick) {
		if (world.isRemote) return estack;
		if (tick % 100 != 0) return estack;

		if (world.rand.nextFloat() < 0.05f) {
			if (estack.getCount() > 50) {
				estack.shrink(40);
				ItemStack star = new ItemStack(ESInit.BLOCKS.STAR_FLOWER, 1);
				ItemHelper.dropItem(world, pos, star);
			}
		}

		int range = getStarFlowerRange(estack);
		BlockPos at = BlockHelper.tryFind(world, (w, p) -> {
			IBlockState state = w.getBlockState(p);
			return state.getBlock() == Blocks.SAND;
		}, pos, Math.min(estack.getPower() / 100, 8), range, range);

		estack.shrink(1);
		if (at == null) return estack;

		estack.shrink(10);
		world.setBlockState(at, ESInit.BLOCKS.STAR_SAND.getDefaultState());
		world.playEvent(2001, at, Block.getStateId(world.getBlockState(at)));

		return estack;
	}

	@Override
	public ElementExplosion newExplosion(World world, Vec3d pos, ElementStack eStack, EntityLivingBase attacker) {
		return new EEStar(world, pos, ElementExplosion.getStrength(eStack) + 0.5f, eStack);
	}

	@Override
	protected void addDrinkJuiceEffect(DrinkJuiceEffectAdder helper) {
		helper.preparatory(ESInit.POTIONS.STAR, 32, 256);
		helper.check(JuiceMaterial.APPLE, 512).checkRatio(JuiceMaterial.MELON, 0.95f, 1.05f).join();

		helper.preparatory(MobEffects.LUCK, 32, 128);
		helper.check(JuiceMaterial.ELF_FRUIT, 100).join();

	}

	@Override
	public ElementStack onLaserUpdate(World world, IWorldObject caster, WorldTarget target, ElementStack eStack,
			VariableSet content) {
		ItemStack stack = ItemStack.EMPTY;
		Entity entity = target.getEntity();
		if (entity instanceof EntityItem) stack = ((EntityItem) entity).getItem();
		else if (entity instanceof EntityLivingBase) {
			stack = ((EntityLivingBase) entity).getHeldItem(EnumHand.MAIN_HAND);
			if (stack.isEmpty()) stack = ((EntityLivingBase) entity).getHeldItem(EnumHand.OFF_HAND);
		}
		if (stack.isEmpty()) return ElementStack.EMPTY;
		int cost = getChangeItemCost(world, stack, content);
		if (cost > 0) return new ElementStack(this, cost, 50);
		return ElementStack.EMPTY;
	}

	@Override
	public void onLaserExecute(World world, IWorldObject caster, WorldTarget target, ElementStack lastCost,
			VariableSet content) {
		if (world.isRemote) return;
		if (lastCost.isEmpty()) return;
		ItemStack stack = ItemStack.EMPTY;
		Entity entity = target.getEntity();
		EnumHand hand = null;
		Vec3d vec = target.getHitVec();
		if (entity instanceof EntityItem) {
			stack = ((EntityItem) entity).getItem();
			vec = entity.getPositionVector().add(0, 0.2, 0);
		} else if (entity instanceof EntityLivingBase) {
			stack = ((EntityLivingBase) entity).getHeldItem(hand = EnumHand.MAIN_HAND);
			if (stack.isEmpty()) stack = ((EntityLivingBase) entity).getHeldItem(hand = EnumHand.OFF_HAND);
			vec = entity.getPositionVector().add(0, entity.height / 2, 0);
		}
		if (stack.isEmpty()) return;
		ItemStack newStack = stack.splitStack(1);
		if (hand != null) ((EntityLivingBase) entity).setHeldItem(hand, stack);

		newStack = doChangeItemCost(world, newStack, lastCost, content);
		if (newStack.isEmpty()) return;
		ItemHelper.dropItem(world, vec.add(0, 0.2, 0), newStack);
	}

	public int getChangeItemCost(World world, ItemStack stack, VariableSet content) {
		Item item = stack.getItem();
		int meta = stack.getMetadata();
		if (item == ESInit.ITEMS.MATERIAL_DEBRIS) return 2;
		if (item == Items.COAL && meta == 1) return 1;
		if (item == Items.DYE) return 1;

		content.remove(Variables.idI);
		Enchantment enchantment = Enchantment.REGISTRY.getRandomObject(rand);
		if (enchantment.canApply(stack)) {
			NBTTagList nbttaglist = stack.getEnchantmentTagList();
			if (nbttaglist.tagCount() < 3 && EnchantmentHelper.getEnchantmentLevel(enchantment, stack) == 0) {
				content.set(Variables.idI, Enchantment.getEnchantmentID(enchantment));
				return 75;
			}
		}

		return -1;
	}

	public ItemStack doChangeItemCost(World world, ItemStack stack, ElementStack star, VariableSet content) {
		Item item = stack.getItem();
		int meta = stack.getMetadata();
		if (item == ESInit.ITEMS.MATERIAL_DEBRIS) return new ItemStack(ESInit.ITEMS.MAGIC_PIECE);
		if (item == Items.COAL && meta == 1) return new ItemStack(Items.COAL);
		if (item == Items.DYE) return new ItemStack(Items.DYE, 1, (meta + 1) % EnumDyeColor.values().length);

		if (content.has(Variables.idI)) {
			Enchantment enchantment = Enchantment.getEnchantmentByID(content.get(Variables.idI));
			content.remove(Variables.idI);
			if (enchantment == null) return stack;
			if (enchantment.canApply(stack)) {
				int minLev = enchantment.getMinLevel();
				int maxLev = enchantment.getMaxLevel();
				int moreLev = 1;
				if (star.getPower() > 1000) moreLev = 2;
				int lev = (int) (star.getPower() / 500f * (maxLev - minLev) + minLev);
				stack.addEnchantment(enchantment, MathHelper.clamp(lev, minLev, maxLev + moreLev));
			}
		}

		return stack;
	}
}

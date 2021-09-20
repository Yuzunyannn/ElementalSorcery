package yuzunyannn.elementalsorcery.item;

import java.util.List;
import java.util.Random;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import yuzunyannn.elementalsorcery.api.item.IWindmillBlade;
import yuzunyannn.elementalsorcery.element.Element;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.tile.altar.TileDeconstructWindmill;

public class ItemWindmillBlade extends Item implements IWindmillBlade {

	public static final int MAX_ELEMENT_POWER = 64;

	public ItemWindmillBlade() {
		this("", 6 * 60 * 60);
	}

	public ItemWindmillBlade(String unlocalizedName, int maxUseSec) {
		this.setUnlocalizedName("windmillBlade" + (unlocalizedName.isEmpty() ? "" : ("." + unlocalizedName)));
		this.setMaxStackSize(1);
		this.setMaxDamage(maxUseSec);
	}

	@Override
	public boolean bladeUpdate(World world, BlockPos pos, ItemStack stack, List<ElementStack> outList, float speed,
			int tick) {

		if (speed <= 0.1f) return false;

		if (tick % 20 == 0) {
			if (tryDamageItem(stack, world)) return true;
		}

		int n = MathHelper.floor(speed >= 4 ? Math.pow(10 - Math.min(9, speed), 1.1) * 4 : 29 * 4 / speed);
		if (tick % n != 0) return false;

		ElementStack product = updateOnce(world, pos, stack, speed, tick / n);
		if (product.isEmpty()) return false;
		outList.add(product);

		if (tryDamageItem(stack, world)) return true;

		return false;
	}

	@Override
	public boolean canTwirl(World world, BlockPos pos, ItemStack stack) {
		return stack.getItemDamage() < stack.getMaxDamage();
	}

	@Override
	public float bladeWindScale(World world, BlockPos pos, ItemStack stack) {
		return TileDeconstructWindmill.getWindScale(world, pos);
	}

	public boolean tryDamageItem(ItemStack stack, World world) {
		stack.attemptDamageItem(1, world.rand, null);
		if (stack.getItemDamage() >= stack.getMaxDamage()) {
			stack.shrink(1);
			return true;
		}
		return false;
	}

	public ElementStack updateOnce(World world, BlockPos pos, ItemStack stack, float speed, int tally) {
		int dimension = world.provider.getDimension();

		Biome biome = world.getBiome(pos);
		Random rand = world.rand;
		float randRate = (rand.nextFloat() * 0.25f + 0.75f);
		float speedRate = Math.min(10, speed + 1) / 10f;
		float highRate = 1 / (float) Math.log10(Math.abs(pos.getY() - dimension == -1 ? 35 : 137) + 10);

		if (biome.canRain()) outWater: {
			float rainStrength = world.getRainStrength(1);
			float thunderStrength = world.getThunderStrength(1);
			if (tally % 2 == 1) break outWater;
			float rate = rainStrength * (0.5f + thunderStrength / 2) * randRate * speedRate;
			if (rate <= 0.001f) break outWater;
			if (!world.isRainingAt(pos)) break outWater;
			float power = Math.max(1, MAX_ELEMENT_POWER * rate);
			return new ElementStack(ESInit.ELEMENTS.WATER, 1, (int) power);
		}

		if (dimension == 1) outEnder: {
			if (tally % 2 == 1) break outEnder;
			float rate = randRate * randRate * Math.min(1, speedRate + 0.5f);
			float power = Math.max(1, MAX_ELEMENT_POWER * rate * rate);
			return new ElementStack(ESInit.ELEMENTS.ENDER, 1, (int) power);
		}

		if (dimension == -1) outFire: {
			if (tally % 2 == 1) break outFire;
			float rate = highRate * speedRate * randRate;
			float power = Math.max(1, MAX_ELEMENT_POWER * rate);
			return new ElementStack(ESInit.ELEMENTS.FIRE, 1, (int) power);
		}

		if (tally % 16 == 0) outOther: {
			float rate = highRate * speedRate * randRate;
			if (rate < 0.05f) break outOther;
			float power = Math.max(1, MAX_ELEMENT_POWER * rate);
			Element element = ESInit.ELEMENTS.WOOD;
			if (rand.nextBoolean()) element = ESInit.ELEMENTS.EARTH;
			return new ElementStack(element, 1, (int) power);
		}

		if (tally % 2 == 1) {
			float rate = highRate * speedRate * randRate;
			float power = Math.max(1, MAX_ELEMENT_POWER * rate);
			return new ElementStack(ESInit.ELEMENTS.AIR, 1, (int) power);
		}

		return ElementStack.EMPTY;
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
		if (enchantment == Enchantments.MENDING) return false;
		return super.canApplyAtEnchantingTable(stack, enchantment);
	}

}

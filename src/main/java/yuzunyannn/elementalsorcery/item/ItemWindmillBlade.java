package yuzunyannn.elementalsorcery.item;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import yuzunyannn.elementalsorcery.api.item.IWindmillBlade;
import yuzunyannn.elementalsorcery.element.Element;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.entity.EntityRotaryWindmillBlate;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.potion.PotionPowerPitcher;
import yuzunyannn.elementalsorcery.tile.altar.TileDeconstructWindmill;
import yuzunyannn.elementalsorcery.util.EntityHelper;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class ItemWindmillBlade extends Item implements IWindmillBlade, PotionPowerPitcher.IPowerPitcher {

	public static final int MAX_ELEMENT_POWER = 64;

	protected float bladeDamage = 2;

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

	@Override
	public EnumActionResult onRightClickItemAsPitcher(World world, EntityLivingBase entity, ItemStack stack,
			EnumHand hand, int amplifier) {
		if (stack.getMaxDamage() - stack.getItemDamage() <= 25) return EnumActionResult.FAIL;

		Vec3d look = entity.getLookVec();
		Vec3d hLook = new Vec3d(look.x, look.y * 0.15, look.z).normalize();
		if (hLook.lengthSquared() < 0.1) return EnumActionResult.FAIL;

		if (world.isRemote) return EnumActionResult.SUCCESS;
		int length = 8 + amplifier * 2;

		Vec3d pos = entity.getPositionVector().add(hLook.scale(length)).addVector(0, 0.2, 0);
		pos = findAttackPosition(world, entity, pos, length);

		ItemStack blade = stack.copy();
		blade.damageItem(25, entity);
		EntityRotaryWindmillBlate entityBlate = new EntityRotaryWindmillBlate(world, blade, entity, amplifier);
		entityBlate.shoot(pos, 20 * (3 + amplifier * 2));
		world.spawnEntity(entityBlate);
		stack.shrink(1);

		return EnumActionResult.SUCCESS;
	}

	protected Vec3d findAttackPosition(World world, EntityLivingBase entity, Vec3d pos, float size) {
		AxisAlignedBB aabb = WorldHelper.createAABB(pos, size, 3, 1);
		List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, aabb);
		if (!entities.isEmpty()) {
			for (int i = 0; i < 3; i++) {
				EntityLivingBase target = entities.get(world.rand.nextInt(entities.size()));
				if (entity != null && EntityHelper.isSameTeam(entity, target)) continue;
				return new Vec3d(target.posX, target.posY + target.height / 2, target.posZ);
			}
		}
		return pos;
	}

	protected void pitchDestoryBlock(World world, BlockPos pos, int size) {
		for (int x = -size; x <= size; x++) {
			for (int z = -size; z <= size; z++) {
				BlockPos at = pos.add(x, 0, z);
				IBlockState state = world.getBlockState(at);
				Block block = state.getBlock();
				if (block.isAir(state, world, pos)) continue;
				if (!state.getMaterial().isLiquid()) {
					world.playEvent(2001, pos, Block.getStateId(state));
					block.dropBlockAsItem(world, pos, state, 0);
				}
				world.setBlockState(at, Blocks.AIR.getDefaultState(), 3);
			}
		}
	}

	protected void pitchAttackEntity(World world, Vec3d vec, int size, EntityRotaryWindmillBlate eBlate) {
		EntityLivingBase attacker = eBlate.getMaster();
		AxisAlignedBB aabb = WorldHelper.createAABB(vec, size + 1, 0.5, 0.5);
		List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, aabb);
		for (EntityLivingBase entity : entities) {
			if (attacker != null && EntityHelper.isSameTeam(attacker, entity)) continue;
			DamageSource ds = DamageSource.causeThrownDamage(eBlate, attacker);
			pitchDoAttackEntity(entity, vec, ds, bladeDamage * (1 + eBlate.getAmplifier() * 0.1f));
		}
	}

	protected void pitchDoAttackEntity(EntityLivingBase target, Vec3d center, DamageSource ds, float damage) {
		if (target.world.isRemote) return;
		target.attackEntityFrom(ds, damage);
	}

	protected void pitchMoveNextTarget(World world, Vec3d vec, int size, EntityRotaryWindmillBlate eBlate) {
		EntityLivingBase master = eBlate.getMaster();
		Vec3d pos = findAttackPosition(world, master, vec, size);
		int remainTick = eBlate.getRemainTick();
		eBlate.shoot(pos, remainTick);
	}

	@Override
	public void bladePitch(World world, Vec3d vec, ItemStack stack, EntityRotaryWindmillBlate eBlate) {
		final int size = 2;
		if (eBlate.tick % 10 == 0) pitchAttackEntity(world, vec, size, eBlate);
		if (world.isRemote) return;
		if (eBlate.tick % 3 == 0) pitchDestoryBlock(world, new BlockPos(vec), size);
	}
}

package yuzunyannn.elementalsorcery.item;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
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
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.element.Element;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.item.IWindmillBlade;
import yuzunyannn.elementalsorcery.api.item.IWindmillBladeController;
import yuzunyannn.elementalsorcery.entity.EntityRotaryWindmillBlate;
import yuzunyannn.elementalsorcery.potion.PotionPowerPitcher;
import yuzunyannn.elementalsorcery.tile.altar.TileDeconstructWindmill;
import yuzunyannn.elementalsorcery.util.helper.EntityHelper;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class ItemWindmillBlade extends Item implements IWindmillBlade, PotionPowerPitcher.IPowerPitcher {

	public static final int MAX_ELEMENT_POWER = 64;
	public static final int PITCH_DAMAGE = 160;

	protected float bladeDamage = 2;
	protected float bladeDestroyHardness = 20;

	public ItemWindmillBlade() {
		this("", 6 * 60 * 60);
	}

	public ItemWindmillBlade(String unlocalizedName, int maxUseSec) {
		this.setTranslationKey("windmillBlade" + (unlocalizedName.isEmpty() ? "" : ("." + unlocalizedName)));
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
			return new ElementStack(ESObjects.ELEMENTS.WATER, 1, (int) power);
		}

		if (dimension == 1) outEnder: {
			if (tally % 2 == 1) break outEnder;
			float rate = randRate * randRate * Math.min(1, speedRate + 0.5f);
			float power = Math.max(1, MAX_ELEMENT_POWER * rate * rate);
			return new ElementStack(ESObjects.ELEMENTS.ENDER, 1, (int) power);
		}

		if (dimension == -1) outFire: {
			if (tally % 2 == 1) break outFire;
			float rate = highRate * speedRate * randRate;
			float power = Math.max(1, MAX_ELEMENT_POWER * rate);
			return new ElementStack(ESObjects.ELEMENTS.FIRE, 1, (int) power);
		}

		if (tally % 16 == 0) outOther: {
			float rate = highRate * speedRate * randRate;
			if (rate < 0.05f) break outOther;
			float power = Math.max(1, MAX_ELEMENT_POWER * rate);
			Element element = ESObjects.ELEMENTS.WOOD;
			if (rand.nextBoolean()) element = ESObjects.ELEMENTS.EARTH;
			return new ElementStack(element, 1, (int) power);
		}

		if (tally % 2 == 1) {
			float rate = highRate * speedRate * randRate;
			float power = Math.max(1, MAX_ELEMENT_POWER * rate);
			return new ElementStack(ESObjects.ELEMENTS.AIR, 1, (int) power);
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
		if (stack.getMaxDamage() - stack.getItemDamage() <= PITCH_DAMAGE) return EnumActionResult.FAIL;

		Vec3d look = entity.getLookVec();
		Vec3d hLook = new Vec3d(look.x, 0, look.z).normalize();
		if (hLook.lengthSquared() < 0.01) return EnumActionResult.FAIL;
		hLook = hLook.add(0, look.y * 0.3, 0).normalize();

		if (world.isRemote) return EnumActionResult.SUCCESS;
		int length = 8 + amplifier * 2;

		Vec3d pos = entity.getPositionVector().add(hLook.scale(length)).add(0, 0.2, 0);
		// 寻找目标
		AxisAlignedBB aabb = WorldHelper.createAABB(pos, length, 3, 2);
		List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, aabb);
		if (!entities.isEmpty()) {
			Vec3d vecLook = entity.getLook(1.0F).normalize();
			// 按照向量夹角进行排序
			entities.sort((a, b) -> {
				Vec3d tarA = new Vec3d(a.posX - entity.posX,
						(a.posY + a.height / 2 - (entity.posY + entity.height / 2)) * 0.15, a.posZ - entity.posZ);
				Vec3d tarB = new Vec3d(b.posX - entity.posX,
						(b.posY + b.height / 2 - (entity.posY + entity.height / 2)) * 0.15, b.posZ - entity.posZ);

				double lenA = tarA.length();
				double lenB = tarB.length();

				if (lenA == 0) lenA = 0.00001;
				if (lenB == 0) lenB = 0.00001;

				double cosA = tarA.dotProduct(vecLook) / (lenA * vecLook.length());
				double cosB = tarB.dotProduct(vecLook) / (lenB * vecLook.length());

				return cosA < cosB ? 1 : -1;
			});
			int i = 0;
			for (Entity target : entities) {
				if (i >= 3) break;
				i = i + 1;
				if (entity != null && EntityHelper.isSameTeam(entity, target)) continue;
				pos = new Vec3d(target.posX, target.posY + target.height / 2, target.posZ);
				break;
			}
		}

		// 飞！
		ItemStack blade = stack.copy();
		blade.damageItem(PITCH_DAMAGE, entity);
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

	protected void pitchDestoryBlock(World world, BlockPos pos, int size, IWindmillBladeController eBlate) {
		for (int x = -size; x <= size; x++) {
			for (int z = -size; z <= size; z++) {
				BlockPos at = pos.add(x, 0, z);
				IBlockState state = world.getBlockState(at);
				Block block = state.getBlock();
				if (block.isAir(state, world, pos)) continue;
				float hardness = state.getBlockHardness(world, pos);
				if (!state.getMaterial().isLiquid() && (hardness < 0 || hardness > bladeDestroyHardness)) {
					eBlate.stop();
					continue;
				}
				if (!state.getMaterial().isLiquid()) {
					world.playEvent(2001, at, Block.getStateId(state));
					block.dropBlockAsItem(world, at, state, 0);
				}
				world.setBlockState(at, Blocks.AIR.getDefaultState(), 3);
			}
		}
	}

	protected void pitchAttackEntity(World world, Vec3d vec, int size, IWindmillBladeController eBlate) {
		EntityLivingBase attacker = eBlate.getMaster();
		AxisAlignedBB aabb = WorldHelper.createAABB(vec, size + 1, 0.5, 0.5);
		List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, aabb);
		for (EntityLivingBase entity : entities) {
			if (attacker != null && EntityHelper.isSameTeam(attacker, entity)) continue;
			DamageSource ds = DamageSource.causeThrownDamage(eBlate.toEntity(), attacker);
			pitchDoAttackEntity(entity, vec, ds, bladeDamage * (1 + eBlate.getAmplifier() * 0.1f));
		}
	}

	protected void pitchDoAttackEntity(EntityLivingBase target, Vec3d center, DamageSource ds, float damage) {
		if (target.world.isRemote) return;
		target.attackEntityFrom(ds, damage);
	}

	protected void pitchMoveNextTarget(World world, Vec3d vec, int size, IWindmillBladeController eBlate) {
		EntityLivingBase master = eBlate.getMaster();
		Vec3d pos = findAttackPosition(world, master, vec, size);
		int remainTick = eBlate.getRemainTick();
		eBlate.shoot(pos, remainTick);
	}

	@Override
	public void bladePitch(World world, Vec3d vec, ItemStack stack, IWindmillBladeController eBlate) {
		final int size = 2;
		if (eBlate.getTick() % 10 == 0) pitchAttackEntity(world, vec, size, eBlate);
		if (world.isRemote) return;
		if (eBlate.getTick() % 3 == 0) pitchDestoryBlock(world, new BlockPos(vec), size, eBlate);
	}
}

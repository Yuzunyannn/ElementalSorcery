package yuzunyannn.elementalsorcery.element.explosion;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleExplosion;
import net.minecraft.client.particle.ParticleExplosionLarge;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.element.IElementExplosion;
import yuzunyannn.elementalsorcery.element.Element;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.network.ESNetwork;
import yuzunyannn.elementalsorcery.network.MessageElementExplosion;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.batch.EffectElementMove;
import yuzunyannn.elementalsorcery.util.ColorHelper;
import yuzunyannn.elementalsorcery.util.DamageHelper;
import yuzunyannn.elementalsorcery.util.SeedRandom;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class ElementExplosion {

	/** 自行处理的马甲 */
	public static final ElementExplosion SELF_DEAL = new ElementExplosion();

	public static float getStrength(ElementStack eStack) {
		int power = eStack.getPower();
		int power2 = 0;
		if (power > 1000) {
			power2 = power - 1000;
			power = 1000;
		}
		int size = eStack.getCount();

		double count = Math.sqrt(power) / 7 + Math.log(Math.max(1, power2)) / 8;
		double factor = size < 100 ? size / 100f : (1 + (size - 100) / 5000f);

		return (float) (count * factor);
	}

	public static IElementExplosion getElementExplosion(ElementStack eStack) {
		if (eStack.getElement() instanceof IElementExplosion) return (IElementExplosion) eStack.getElement();
		return null;
	}

	@Nullable
	public static ElementExplosion doExplosion(World world, BlockPos pos, ElementStack eStack,
			@Nullable EntityLivingBase attacker) {
		return doExplosion(world, new Vec3d(pos).addVector(0.5, 0.5, 0.5), eStack, attacker);
	}

	@Nullable
	public static ElementExplosion doExplosion(World world, Vec3d pos, ElementStack eStack,
			@Nullable EntityLivingBase attacker) {

		IElementExplosion explosion = getElementExplosion(eStack);
		if (explosion == null) return null;
		if (explosion == SELF_DEAL) return SELF_DEAL;

		ElementExplosion instance = explosion.newExplosion(world, pos, eStack, attacker);
		if (instance == null) return null;
		if (instance.size < 0.01f) return null;

		instance.setAttacker(attacker);
		if (world.isRemote) return instance;

		MessageElementExplosion msg = new MessageElementExplosion(eStack, pos, instance.getRandSeed());
		TargetPoint point = new TargetPoint(world.provider.getDimension(), pos.x, pos.y, pos.z, 128);
		ESNetwork.instance.sendToAllAround(msg, point);

		instance.doExplosionCheckBlock();
		instance.doExplosionBlock();
		instance.doExplosionEntity();

		return instance;

	}

	@SideOnly(Side.CLIENT)
	public static <T extends Element & IElementExplosion> void doExplosionClient(World world,
			MessageElementExplosion msg) {
		if (msg.estack.isEmpty()) return;
		Vec3d pos = new Vec3d(msg.x, msg.y, msg.z);
		ElementExplosion instance = doExplosion(world, pos, msg.estack, null);
		if (instance == null) return;
		instance.setRandSeed(msg.seed);
		instance.doExplosionCheckBlock();
		instance.doExplosionBlock();
		instance.doExplosionEntity();
	}

	protected final Set<BlockPos> affectedBlockPositions = new HashSet<>();
	protected float size = 1;
	protected ElementStack eStack = ElementStack.EMPTY;
	protected World world;
	protected Vec3d position;
	protected SeedRandom rand = new SeedRandom(0);
	protected EntityLivingBase attacker;

	protected boolean passExplosionBlock = false;
	protected boolean passClientExplosionEntity = false;

	protected Explosion vest;

	public ElementExplosion(World world, Vec3d position, float size, ElementStack estack) {
		this.world = world;
		this.position = position;
		this.size = size;
		this.eStack = estack;
		this.rand.setSeed(this.world.rand.nextInt());
		this.vest = new Explosion(world, null, position.x, position.y, position.z, size, false, false);
	}

	protected ElementExplosion() {

	}

	public int getRandSeed() {
		return rand.getSeed();
	}

	public void setRandSeed(int seed) {
		rand.setSeed(seed);
	}

	public float getSize() {
		return size;
	}

	public void setAttacker(EntityLivingBase attacker) {
		this.attacker = attacker;
		try {
			ObfuscationReflectionHelper.setPrivateValue(Explosion.class, vest, attacker, "field_77283_e");
		} catch (Exception e) {}
	}

	/** 获取爆炸阻力 */
	protected float getExplosionResistance(BlockPos pos, IBlockState state) {
		try {
			return state.getBlock().getExplosionResistance(world, pos, attacker, this.vest);
		} catch (Exception e) {}
		return 0;
	}

	protected void checkBlockOnce(Vec3d orient) {
		float lengthSpread = this.size * (0.7F + rand.nextFloat() * 0.6F);

		double x = this.position.x;
		double y = this.position.y;
		double z = this.position.z;

		for (; lengthSpread > 0; lengthSpread -= 0.22500001f) {
			BlockPos pos = new BlockPos(x, y, z);
			IBlockState state = this.world.getBlockState(pos);
			if (state.getMaterial() != Material.AIR) {
				float resistance = getExplosionResistance(pos, state);
				lengthSpread -= (resistance + 0.3F) * 0.3F;
			}
			if (lengthSpread > 0) affectedBlockPositions.add(pos);
			x += orient.x * 0.30000001192092896D;
			y += orient.y * 0.30000001192092896D;
			z += orient.z * 0.30000001192092896D;
		}
	}

	/** A:进行检测爆炸方块 */
	public void doExplosionCheckBlock() {
		if (passExplosionBlock) return;
		final int faceSize = 16 - 1;
		for (int j = 0; j <= faceSize; ++j) {
			for (int k = 0; k <= faceSize; ++k) {
				for (int l = 0; l <= faceSize; ++l) {
					if (j == 0 || j == faceSize || k == 0 || k == faceSize || l == 0 || l == faceSize) {
						Vec3d orient = new Vec3d(j / 15f * 2f - 1, k / 15f * 2f - 1, l / 15f * 2f - 1).normalize();
						checkBlockOnce(orient);
					}
				}
			}
		}
	}

	/** B:进行方块爆炸 */
	public void doExplosionBlock() {
		if (passExplosionBlock) return;
		if (world.isRemote) doExplosionBlockEffect();
		for (BlockPos pos : affectedBlockPositions) doExplosionBlockAt(pos);
	}

	/** C:进行对实体爆炸 */
	public void doExplosionEntity() {
		if (passClientExplosionEntity && world.isRemote) return;

		float checkRange = this.size * 2.0F;
		AxisAlignedBB aabb = WorldHelper.createAABB(position, checkRange, checkRange, checkRange);
		List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(null, aabb);

		for (int i = 0; i < list.size(); ++i) {
			Entity entity = list.get(i);
			if (entity.isImmuneToExplosions()) continue;

			double distanceRate = entity.getDistance(this.position.x, this.position.y, this.position.z) / checkRange;
			if (distanceRate > 1.0D) continue;

			Vec3d orient = entity.getPositionVector().addVector(0, entity.getEyeHeight(), 0).subtract(position);
			double distanceOfEntityWithExplosionCenter = orient.lengthVector();
			if (distanceOfEntityWithExplosionCenter == 0.0D) continue;

			double blockDensity = this.world.getBlockDensity(this.position, entity.getEntityBoundingBox());

			double strength = (1 - distanceRate) * blockDensity;
			doExplosionEntityAt(entity, orient, strength, checkRange);

		}
	}

	@SideOnly(Side.CLIENT)
	protected void doExplosionBlockEffect() {
		world.playSound(position.x, position.y, position.z, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS,
				4.0F, (1.0F + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.2F) * 0.7F, true);
//		if (this.size >= 2.0F)
//			world.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, position.x, position.y, position.z, 1.0D, 0.0D, 0.0D);
//		else world.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, position.x, position.y, position.z, 1.0D, 0.0D,
//				0.0D);

		Function<Vec3d, Void> showParticle = v -> {
			Particle particle = new ParticleExplosionLarge(Effect.mc.getTextureManager(), world, v.x, v.y, v.z, 0, 0,
					0) {
			};
			Vec3d color = ColorHelper.color(eStack.getColor());
			float colorRate = this.rand.nextFloat() * 0.6F + 0.4F;
			particle.setRBGColorF((float) color.x * colorRate, (float) color.y * colorRate,
					(float) color.z * colorRate);
			Effect.mc.effectRenderer.addEffect(particle);
			return null;
		};

		if (this.size >= 2.0F) {
			Effect.addEffect(new Effect(world, position.x, position.y, position.z) {
				@Override
				public void onUpdate() {
					if (this.lifeTime > 8) this.lifeTime = 8;
					this.lifeTime--;
					for (int i = 0; i < 6; ++i) {
						double x = this.posX + (rand.nextDouble() - rand.nextDouble()) * size;
						double y = this.posY + (rand.nextDouble() - rand.nextDouble()) * size;
						double z = this.posZ + (rand.nextDouble() - rand.nextDouble()) * size;
						showParticle.apply(new Vec3d(x, y, z));
					}
				}
			});

		} else showParticle.apply(position);

	}

	protected void doExplosionBlockAt(BlockPos pos) {
		if (world.isRemote) return;
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		if (state.getMaterial() == Material.AIR) return;
		if (block.canDropFromExplosion(vest)) {
			block.dropBlockAsItemWithChance(world, pos, state, 1.0F / this.size, 0);
		}
		block.onBlockExploded(world, pos, vest);
	}

	protected void doExplosionEntityAt(Entity entity, Vec3d orient, double strength, double range) {
		double damage = (strength * strength + strength) / 2.0D * 7.0D * range + 1.0D;
		double pound = strength;
		if (entity instanceof EntityLivingBase)
			pound = EnchantmentProtection.getBlastDamageReduction((EntityLivingBase) entity, strength);
		doExplosionEntityAt(entity, orient, strength, damage, pound);
	}

	protected void doExplosionEntityAt(Entity entity, Vec3d orient, double strength, double damage, double pound) {
		doDamageSource(entity, damage);
		orient = orient.scale(pound);
		entity.motionX += orient.x;
		entity.motionY += orient.y;
		entity.motionZ += orient.z;
	}

	protected void doDamageSource(Entity entity, double damage) {
		if (!world.isRemote) {
			DamageSource ds = DamageHelper.getDamageSource(eStack, attacker, null);
			ds.setExplosion();
			entity.attackEntityFrom(ds, (float) damage);
		}
	}

	@SideOnly(Side.CLIENT)
	protected void spawnEffectFromBlock(BlockPos pos) {
		Vec3d at = new Vec3d(pos).addVector(0.5, 0.5, 0.5);
		doExplosionEntityAtEfect(world, eStack.getColor(), at, at.subtract(position).normalize());

		Vec3d randPos = new Vec3d(pos).addVector(world.rand.nextFloat(), world.rand.nextFloat(),
				world.rand.nextFloat());
		Vec3d orient = randPos.subtract(position);
		double length = orient.lengthVector();
		orient = orient.normalize();
		double scale = 0.5 / (length / this.size + 0.1);
		scale = scale * (world.rand.nextFloat() * world.rand.nextFloat() + 0.3);
		orient = orient.scale(scale);

		Particle particle = new ParticleExplosion(world, (randPos.x + position.x) / 2.0D,
				(randPos.y + position.y) / 2.0D, (randPos.z + position.z) / 2.0D, orient.x, orient.y, orient.z) {
		};
		Vec3d color = ColorHelper.color(eStack.getColor());
		float colorRate = this.rand.nextFloat() * 0.3F + 0.7F;
		particle.setRBGColorF((float) color.x * colorRate, (float) color.y * colorRate, (float) color.z * colorRate);
		Effect.mc.effectRenderer.addEffect(particle);

		this.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, randPos.x, randPos.y, randPos.z, orient.x, orient.y,
				orient.z);
	}

	@SideOnly(Side.CLIENT)
	protected static void doExplosionEntityAtEfect(World world, int color, Vec3d vec, Vec3d orient) {
		EffectElementMove move = new EffectElementMove(world,
				vec.addVector(world.rand.nextGaussian(), world.rand.nextGaussian(), world.rand.nextGaussian()));
		move.setColor(color);
		move.setVelocity(orient.scale(0.1f * EffectElementMove.rand.nextDouble()));

		Effect.addEffect(move);
	}
}

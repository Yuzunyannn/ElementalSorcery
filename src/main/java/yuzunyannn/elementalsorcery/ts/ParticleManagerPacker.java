package yuzunyannn.elementalsorcery.ts;

import java.util.ArrayDeque;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ParticleManagerPacker extends ParticleManager {

	public final ParticleManager parent;
	public int addCount;

	public ParticleManagerPacker(ParticleManager other) {
		super(null, null);
		this.parent = other;

		ArrayDeque<Particle>[][] fxLayers = ObfuscationReflectionHelper.getPrivateValue(ParticleManager.class, parent,
				"field_78876_b");
		for (ArrayDeque<Particle>[] particless : fxLayers) {
			for (ArrayDeque<Particle> particles : particless) {
				for (Particle particle : particles) {
					moveValue(particle, "field_187126_f", "field_187123_c");
					moveValue(particle, "field_187127_g", "field_187124_d");
					moveValue(particle, "field_187128_h", "field_187125_e");
				}
			}
		}
	}

	protected void moveValue(Particle obj, String from, String to) {
		Object val = ObfuscationReflectionHelper.getPrivateValue(Particle.class, obj, from);
		ObfuscationReflectionHelper.setPrivateValue(Particle.class, obj, val, to);
	}

	@Override
	public void registerParticle(int id, IParticleFactory particleFactory) {
//		parent.registerParticle(id, particleFactory);
	}

	@Override
	public void emitParticleAtEntity(Entity entityIn, EnumParticleTypes particleTypes) {
		parent.emitParticleAtEntity(entityIn, particleTypes);
	}

	@Override
	public void emitParticleAtEntity(Entity p_191271_1_, EnumParticleTypes p_191271_2_, int p_191271_3_) {
		parent.emitParticleAtEntity(p_191271_1_, p_191271_2_, p_191271_3_);
	}

	@Override
	public Particle spawnEffectParticle(int particleId, double xCoord, double yCoord, double zCoord, double xSpeed,
			double ySpeed, double zSpeed, int... parameters) {
		if (particleId == 39) return null;
		// 限制一下数量
		addCount++;
		if (addCount > 1000) {
			if (addCount % 4 != 0) return null;
			if (addCount > 5000) {
				if (addCount % 8 != 0) return null;
				if (addCount > 10000) return null;
			}
			return null;
		}
		return parent.spawnEffectParticle(particleId, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, parameters);
	}

	@Override
	public void addEffect(Particle effect) {
		parent.addEffect(effect);
	}

	@Override
	public void updateEffects() {

	}

	@Override
	public void renderParticles(Entity entityIn, float partialTicks) {
		parent.renderParticles(entityIn, partialTicks);
	}

	@Override
	public void renderLitParticles(Entity entityIn, float partialTick) {
		parent.renderLitParticles(entityIn, partialTick);
	}

	@Override
	public void clearEffects(@Nullable World worldIn) {
		parent.clearEffects(worldIn);
	}

	@Override
	public void addBlockDestroyEffects(BlockPos pos, IBlockState state) {
		parent.addBlockDestroyEffects(pos, state);
	}

	@Override
	public void addBlockHitEffects(BlockPos pos, EnumFacing side) {
		parent.addBlockHitEffects(pos, side);
	}

	@Override
	public String getStatistics() {
		return parent.getStatistics();
	}

	@Override
	public void addBlockHitEffects(BlockPos pos, net.minecraft.util.math.RayTraceResult target) {
		parent.addBlockHitEffects(pos, target);
	}

}

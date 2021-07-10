package yuzunyannn.elementalsorcery.summon;

import java.util.Random;

import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.entity.mob.EntityRabidRabbit;

public class SummonRabidRabbit extends SummonSilverfishSpring {

	public SummonRabidRabbit(World world, BlockPos pos) {
		super(world, pos, 0xf2e9ea);
		this.sliverFish = world.rand.nextInt(8) + 8;
	}

	@Override
	public void spawn(Vec3d pos, Random rand) {
		EntityRabbit entity;

		if (rand.nextBoolean()) entity = new EntityRabbit(world);
		else entity = new EntityRabidRabbit(world);
		entity.setRabbitType(1);

		entity.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 20, 3));
		entity.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, 20, 3));
		
		entity.setPosition(pos.x, pos.y, pos.z);
		entity.motionX = rand.nextDouble() - 0.5;
		entity.motionY = rand.nextDouble() * 0.2 + 0.4;
		entity.motionZ = rand.nextDouble() - 0.5;
		world.spawnEntity(entity);
	}
}

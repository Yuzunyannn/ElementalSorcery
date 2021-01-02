package yuzunyannn.elementalsorcery.summon;

import java.util.Random;

import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class SummonSilverfishSpring extends SummonCommon {

	protected int sliverFish;

	public SummonSilverfishSpring(World world, BlockPos pos) {
		super(world, pos, 0x109e41);
		this.sliverFish = world.rand.nextInt(36) + 16;
	}

	@Override
	public void initData() {
		this.size = 3;
		this.height = 4;
	}

	@Override
	public boolean update() {
		if (world.isRemote) return true;
		if (tick++ < 20 * 5) return true;
		if (tick % 2 != 0) return true;
		Vec3d pos = new Vec3d(this.pos).addVector(0.5, 0.1 + 1.5, 0.5);
		Random rand = world.rand;
		if (sliverFish <= 0) return false;
		sliverFish--;
		EntitySilverfish entity = new EntitySilverfish(world);
		entity.setPosition(pos.x, pos.y, pos.z);
		entity.setVelocity(rand.nextDouble() - 0.5, rand.nextDouble() * 0.2 + 0.4, rand.nextDouble() - 0.5);
		int time = 20 * 60 * 60;
		entity.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, time, 2));
		entity.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, time, 1));
		entity.addPotionEffect(new PotionEffect(MobEffects.SPEED, time, 3));
		entity.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, time, 3));
		world.spawnEntity(entity);
		WorldHelper.createExpBall(world, pos, 25 + rand.nextInt(50));
		return sliverFish > 0;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setByte("sf", (byte) this.sliverFish);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		this.sliverFish = nbt.getInteger("sf");
	}

}

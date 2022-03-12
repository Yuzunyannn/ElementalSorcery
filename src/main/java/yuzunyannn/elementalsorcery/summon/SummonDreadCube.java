package yuzunyannn.elementalsorcery.summon;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.entity.mob.EntityDreadCube;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class SummonDreadCube extends SummonCommon {

	protected int lightning;

	public SummonDreadCube(World world, BlockPos pos) {
		super(world, pos, 0x671111);
		this.lightning = world.rand.nextInt(4) + 6;
	}

	@Override
	public void initData() {
		this.size = 4;
		this.height = 5;
	}

	@Override
	public boolean update() {
		if (tick++ % 20 != 0) return true;
		if (world.isRemote) return true;
		randLightning();
		if (--lightning < 3) {
			randLightning();
			randLightning();
			lightning = 0;
			this.finish();
		}
		return lightning > 0;
	}

	private void randLightning() {
		Vec3d vec = new Vec3d(pos);
		vec = vec.add(0.5 + world.rand.nextGaussian(), 0, 0.5 + world.rand.nextGaussian());
		WorldHelper.newLightning(world, vec);
	}

	private void finish() {
		if (world.isRemote) return;
		EntityDreadCube dreadCube = new EntityDreadCube(world);
		if (!world.isAirBlock(pos)) dreadCube.setPosition(pos.getX() + 0.5, pos.getY() + 2, pos.getZ() + 0.5);
		else dreadCube.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
		world.spawnEntity(dreadCube);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("lightning", this.lightning);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		this.lightning = nbt.getInteger("lightning");
	}

}

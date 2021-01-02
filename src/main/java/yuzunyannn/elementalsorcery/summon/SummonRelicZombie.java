package yuzunyannn.elementalsorcery.summon;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.entity.mob.EntityRelicZombie;
import yuzunyannn.elementalsorcery.entity.mob.RelicZombieType;

public class SummonRelicZombie extends SummonCommon {

	protected int index;
	protected List<Map.Entry<BlockPos, IBlockState>> build;

	public SummonRelicZombie(World world, BlockPos pos) {
		super(world, pos, 0x285f57);
		this.build = new ArrayList<>();
		this.initBuilding();
	}

	@Override
	public void initData() {
		this.size = 4;
		this.height = 5;
		this.index = 0;
	}

	private void initBuilding() {
		IBlockState state = Blocks.SANDSTONE.getDefaultState();

		for (int x = -2; x <= 2; x++) {
			for (int z = -2; z <= 2; z++) {
				build.add(new AbstractMap.SimpleEntry(pos.add(x, -1, z), state));
			}
		}

		build.add(new AbstractMap.SimpleEntry(pos.add(2, 0, 2), state));
		build.add(new AbstractMap.SimpleEntry(pos.add(-2, 0, 2), state));
		build.add(new AbstractMap.SimpleEntry(pos.add(-2, 0, -2), state));
		build.add(new AbstractMap.SimpleEntry(pos.add(2, 0, -2), state));

		build.add(new AbstractMap.SimpleEntry(pos.add(1, 0, 3), state));
		build.add(new AbstractMap.SimpleEntry(pos.add(3, 0, -1), state));
		build.add(new AbstractMap.SimpleEntry(pos.add(-1, 0, -3), state));
		build.add(new AbstractMap.SimpleEntry(pos.add(-3, 0, 1), state));

		build.add(new AbstractMap.SimpleEntry(pos.add(-1, 0, 3), state));
		build.add(new AbstractMap.SimpleEntry(pos.add(3, 0, 1), state));
		build.add(new AbstractMap.SimpleEntry(pos.add(1, 0, -3), state));
		build.add(new AbstractMap.SimpleEntry(pos.add(-3, 0, -1), state));

		int high = 4;
		for (int i = 1; i < high; i++) {
			build.add(new AbstractMap.SimpleEntry(pos.add(2, i, 2), state));
			build.add(new AbstractMap.SimpleEntry(pos.add(-2, i, 2), state));
			build.add(new AbstractMap.SimpleEntry(pos.add(-2, i, -2), state));
			build.add(new AbstractMap.SimpleEntry(pos.add(2, i, -2), state));
		}
		for (int x = -2; x <= 2; x++) {
			for (int z = -2; z <= 2; z++) {
				build.add(new AbstractMap.SimpleEntry(pos.add(x, high, z), state));
			}
		}
	}

	@Override
	public boolean update() {
		if (tick++ % 2 != 0) return true;
		if (world.isRemote) return true;
		if (index >= build.size()) return false;
		if (index == 0) this.start();
		build(build.get(index));
		if (++index == build.size()) this.finish();
		return index < build.size();
	}

	private void build(Map.Entry<BlockPos, IBlockState> entry) {
		if (world.isRemote) return;
		genBlock(entry.getKey(), entry.getValue());
	}

	private void start() {
		for (int x = -2; x <= 2; x++) {
			for (int y = 0; y <= 3; y++) {
				for (int z = -2; z <= 2; z++) {
					world.destroyBlock(pos.add(x, y, z), true);
				}
			}
		}
	}

	private void finish() {
		if (world.isRemote) return;
		RelicZombieType[] types = RelicZombieType.values();
		for (int i = 0; i < types.length; i++) {
			float t = i * 360f / types.length / 180 * 3.14f;
			EntityRelicZombie zombie = new EntityRelicZombie(world, types[i]);
			zombie.setPosition(pos.getX() + 0.5 + MathHelper.sin(t) * 1.5, pos.getY(),
					pos.getZ() + 0.5 + MathHelper.cos(t) * 1.5);
			world.spawnEntity(zombie);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("index", this.index);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		this.index = nbt.getInteger("index");
	}

}

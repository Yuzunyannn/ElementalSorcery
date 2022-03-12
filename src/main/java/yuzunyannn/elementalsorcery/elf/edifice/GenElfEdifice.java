package yuzunyannn.elementalsorcery.elf.edifice;

import java.util.AbstractMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.event.EventServer;
import yuzunyannn.elementalsorcery.event.ITickTask;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.tile.TileElfTreeCore;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;
import yuzunyannn.elementalsorcery.util.helper.NBTHelper;

public class GenElfEdifice {

	public static int getFakeCircleLen(int size, int at, int n) {
		float x = n;
		int i = size - Math.abs(at) - 1;
		if (i < 0) x = 1 / x;
		return (int) ((i / x) + size);
	}

	public static final int EDIFICE_SIZE = 8;

	public final boolean dispersed;
	protected List<Map.Entry<BlockPos, IBlockState>> dispersedMap;
	protected NBTTagCompound buildCoreData = new NBTTagCompound();
	protected String restoreData;
	public int treeSize = EDIFICE_SIZE;
	public IBlockState elfLog = ESObjects.BLOCKS.ELF_LOG.getDefaultState();
	IBlockState elfLeaf = ESInit.BLOCKS.ELF_LEAF.getDefaultState().withProperty(BlockLeaves.DECAYABLE, false)
			.withProperty(BlockLeaves.CHECK_DECAY, false);

	public GenElfEdifice(boolean dispersed) {
		this.dispersed = dispersed;
		if (dispersed) dispersedMap = new LinkedList<>();
	}

	public void setBlockState(World world, BlockPos pos, IBlockState state) {
		if (dispersed) dispersedMap.add(new AbstractMap.SimpleEntry<BlockPos, IBlockState>(pos, state));
		else world.setBlockState(pos, state, 2 | 16);
	}

	public void setRestoreData(String restoreData) {
		this.restoreData = restoreData;
	}

	/** 分散建立中，将数据分散到每一tick进行设置 */
	public void buildToTick(World world) {
		if (!dispersed) return;
		if (world.isRemote) return;
		Iterator<Map.Entry<BlockPos, IBlockState>> iter = dispersedMap.iterator();
		dispersedMap = new LinkedList<>();
		NBTTagCompound coreData = buildCoreData;
		buildCoreData = new NBTTagCompound();
		EventServer.addTickTask(() -> {
			int i = 0;
			long time = System.currentTimeMillis();
			// 延迟最多不能超过10毫秒和每次最多100个方块，保证稳定
			while (System.currentTimeMillis() - time < 5 && i < 100) {
				if (!iter.hasNext()) {
					dealTreeCore(world, coreData);
					return ITickTask.END;
				}
				Map.Entry<BlockPos, IBlockState> entry = iter.next();
				world.setBlockState(entry.getKey(), entry.getValue(), 2 | 16);
				i++;
			}
			return ITickTask.SUCCESS;
		});
	}

	public void dealTreeCore(World world, NBTTagCompound data) {
		BlockPos pos = NBTHelper.getBlockPos(data, "pos");
		int high = data.getInteger("high");
		pos = pos.add(0, high - 1, 0);
		world.setBlockState(pos, ESInit.BLOCKS.ELF_TREE_CORE.getDefaultState());
		TileElfTreeCore core = BlockHelper.getTileEntity(world, pos, TileElfTreeCore.class);
		if (core == null) {
			ElementalSorcery.logger.warn("生成树核心的时候找不到核心的tile，位于" + pos);
			return;
		}
		core.initTreeData(data.getInteger("size"), high);
		if (restoreData != null) core.restore(restoreData);
	}

	public boolean checkCanGen(World world, BlockPos pos) {
		// world.getChunkProvider().isChunkGeneratedAt(x, z)
		if (world.getBlockState(pos).getBlock() != Blocks.DIRT && world.getBlockState(pos).getBlock() != Blocks.GRASS)
			return false;
		if (world.provider.getHeight() - pos.getY() < 100) return false;
		int down = 0, up = 0;
		int size = treeSize;
		for (int x = -size; x <= size; x++) {
			for (int z = -size; z <= size; z++) {
				BlockPos at = pos.add(x, 0, z);
				if (!isReplaceable(world, at.up())) up++;
				if (world.isAirBlock(at.down(2))) down++;
				IBlockState state = world.getBlockState(pos.down());
				Block block = state.getBlock();
				if (block == Blocks.DIRT || block == Blocks.GRASS || block == Blocks.STONE || isReplaceable(world, pos))
					continue;
				return false;
			}
		}
		if (down > treeSize * treeSize || up > treeSize * treeSize) return false;
		return true;
	}

	public void clearAround(World world, BlockPos pos) {
		int size = GenElfEdifice.getFakeCircleLen(treeSize, 0, 2);
		for (int y = 0; y < 10; y++) {
			for (int i = -size + 1; i < size; i++) {
				int n = GenElfEdifice.getFakeCircleLen(treeSize, i, 2);
				for (int a = 0; a < n; a++) {
					world.setBlockToAir(pos.add(i, y, a));
					world.setBlockToAir(pos.add(i, y, -a));
				}
			}
		}
	}

	public void genMainTreeEdifice(World world, BlockPos pos, Random rand) {
		// 树高
		int hight = rand.nextInt(40) + 50;
		int size = 0;
		// 封底
		size = GenElfEdifice.getFakeCircleLen(treeSize, 0, 2);
		for (int i = -size + 1; i < size; i++) {
			int n = GenElfEdifice.getFakeCircleLen(treeSize, i, 2);
			for (int a = 0; a < n; a++) {
				setBlockState(world, pos.add(i, 0, a), elfLog);
				setBlockState(world, pos.add(i, 0, -a), elfLog);
			}
		}
		// 树根
		treeRoot(world, pos, rand);
		// 树干主
		for (int y = 0; y < hight; y++) {
			for (int i = -treeSize + 1; i < treeSize; i++) {
				int n = GenElfEdifice.getFakeCircleLen(treeSize, i, 2);
				int h = hight / 6;
				if (y > h && y < hight - h && rand.nextInt(10) == 0) {
					BlockPos at = randPos(pos, rand, i, n).add(0, y, 0);
					treeBranch(world, rand, pos, at, rand.nextInt(3) + 2);
				}
				setBlockState(world, pos.add(i, y, n), elfLog);
				setBlockState(world, pos.add(i, y, -n), elfLog);
				setBlockState(world, pos.add(n, y, i), elfLog);
				setBlockState(world, pos.add(-n, y, i), elfLog);
			}
		}
		// 树叶
		treeCrown(world, pos, hight, rand);
		// 精灵石数据记录
		buildCoreData = new NBTTagCompound();
		NBTHelper.setBlockPos(buildCoreData, "pos", pos);
		buildCoreData.setInteger("high", hight);
		buildCoreData.setInteger("size", this.treeSize);
		if (!dispersed) dealTreeCore(world, buildCoreData);
	}

	private BlockPos randPos(BlockPos pos, Random rand, int i, int n) {
		switch (rand.nextInt(4)) {
		case 1:
			pos = pos.add(i, 0, n);
			break;
		case 2:
			pos = pos.add(i, 0, -n);
			break;
		case 3:
			pos = pos.add(n, 0, i);
			break;
		default:
			pos = pos.add(-n, 0, i);
			break;
		}
		return pos;
	}

	// 生成树枝
	private void treeBranch(World world, Random rand, BlockPos pos, BlockPos at, int size) {
		Vec3d d = new Vec3d(at.subtract(pos)).normalize();
		int yoff = 0;
		{
			Vec3d point = new Vec3d(at);
			for (int i = 0; i < size * 2; i++) {
				boolean pup = rand.nextInt(4) == 0;
				if (pup) yoff = 1;
				else yoff = 0;
				point = point.add(d.x, yoff, d.z);
				at = new BlockPos(point);
				if (this.isReplaceable(world, at)) setBlockState(world, at, elfLog);
			}
		}
		yoff = 0;
		int xoff, zoff;
		for (int i = 0; i < 4; i++) {
			int xsize = size / 2 - i;
			int zsize = size / 2 - i;
			for (xoff = -xsize; xoff <= xsize; xoff++) {
				for (zoff = -zsize; zoff <= zsize; zoff++) {
					BlockPos blockpos = at.add(xoff, yoff, zoff);
					if (isReplaceable(world, blockpos)) setBlockState(world, blockpos, elfLeaf);
				}
			}
			yoff++;
		}
	}

	/** 这个要计算光照，直接生成的话很慢 */
	private void treeCrown(World world, BlockPos pos, int hight, Random rand) {
		int size = treeSize * 2;
		for (int y = hight; y < hight + treeSize * 2; y++) {
			size = size - 1;
			for (int i = -size + 1; i < size; i++) {
				int n = GenElfEdifice.getFakeCircleLen(size, i, 2);
				for (int a = 0; a < n; a++) {
					if (y == hight && rand.nextInt(5) == 0) {
						BlockPos at = randPos(pos, rand, i, a).add(0, y - 1, 0);
						setBlockState(world, at, elfLeaf);
					}
					setBlockState(world, pos.add(i, y, a), elfLeaf);
					setBlockState(world, pos.add(i, y, -a), elfLeaf);
					setBlockState(world, pos.add(a, y, i), elfLeaf);
					setBlockState(world, pos.add(-a, y, i), elfLeaf);
				}
			}
		}
	}

	private void treeRoot(World world, BlockPos pos, Random rand) {
		// 树根
		int max = treeSize;
		for (int d = 0; d < max; d++) {
			int size = treeSize + d;
			int deta = d * 2 + 1;
			if (d >= max / 2) deta = max + 2 - d;
			for (int i = -size; i <= size; i++) {
				if (i > -size + deta && i < size - deta) i = size - deta;
				for (int y = 0; y <= max - d; y++) {
					setBlockState(world, pos.add(i, y, size), elfLog);
					setBlockState(world, pos.add(i, y, -size), elfLog);
					setBlockState(world, pos.add(size, y, i), elfLog);
					setBlockState(world, pos.add(-size, y, i), elfLog);
				}
				treeRootExtend(world, pos.add(i, -1, size));
				treeRootExtend(world, pos.add(i, -1, -size));
				treeRootExtend(world, pos.add(size, -1, i));
				treeRootExtend(world, pos.add(-size, -1, i));
			}
		}
		// 周围随机的树根
		int times = rand.nextInt(80) + 40;
		for (int i = 0; i < times; i++) {
			int ry = -rand.nextInt(5);
			int rx = rand.nextInt(treeSize * 4) - treeSize * 2;
			int rz = rand.nextInt(treeSize * 4) - treeSize * 2;
			BlockPos at = pos.add(rx, ry, rz);
			IBlockState state = world.getBlockState(at);
			if (state.getBlock() == Blocks.DIRT || state.getBlock() == Blocks.STONE || state.getBlock() == Blocks.SAND)
				setBlockState(world, at, elfLog);
		}
	}

	private void treeRootExtend(World world, BlockPos pos) {
		while (pos.getY() > 0 && this.isReplaceable(world, pos)) {
			setBlockState(world, pos, elfLog);
			pos = pos.down();
		}
	}

	private boolean isReplaceable(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		boolean flag = state.getBlock().isReplaceable(world, pos);
		if (flag) return true;
		flag = state.getBlock().isLeaves(state, world, pos);
		return flag;
	}

}

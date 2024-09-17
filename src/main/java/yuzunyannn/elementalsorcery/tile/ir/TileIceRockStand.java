package yuzunyannn.elementalsorcery.tile.ir;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.block.container.BlockIceRockStand;
import yuzunyannn.elementalsorcery.render.effect.Effects;
import yuzunyannn.elementalsorcery.tile.ir.TileIceRockSendRecv.FaceStatus;
import yuzunyannn.elementalsorcery.util.LambdaReference;
import yuzunyannn.elementalsorcery.util.element.ElementInventoryMonitor;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;
import yuzunyannn.elementalsorcery.util.helper.Color;
import yuzunyannn.elementalsorcery.util.helper.DamageHelper;
import yuzunyannn.elementalsorcery.util.helper.EntityHelper;
import yuzunyannn.elementalsorcery.util.helper.NBTHelper;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class TileIceRockStand extends TileIceRockBase implements ITickable {

	public static final float ONE_DAMAGE_ENND_FRAGMENT = 75000f;
	public static final float DAMAGE_GROW_POWER = 1.125f;

	protected int tick;

	protected double magicFragment = 0;
	protected int linkCount = 0;
	/** 服务端检查变化量是否值得更新 */
	public double lastUpdateMagicFragment = 0;
	/** 子节点管 */
	protected List<BlockPos> subNodes = new LinkedList<>();
	/** 发动攻击的时间，配合动画，同时作为cd */
	protected int attackReadyTick = 0;
	protected float attackDamage;
	protected Vec3d attackVec;
	protected EntityLivingBase attackCaller;

	@Override
	public double getMagicFragment() {
		return magicFragment;
	}

	@Override
	protected void setMagicFragment(double magicFragment) {
		this.magicFragment = magicFragment;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt = super.writeToNBT(nbt);
		nbt.setInteger("linkCount", linkCount);
		nbt.setDouble("fragment", getMagicFragment());
		if (isSending()) return nbt;
		NBTHelper.setBlockPosCollection(nbt, "subNs", subNodes);
		return nbt;
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		eim.checkChange(eInventoryAdapter);
		return super.getUpdateTag();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		linkCount = nbt.getInteger("linkCount");
		setMagicFragment(nbt.getDouble("fragment"));
		if (isSending()) return;
		subNodes = new LinkedList<>(NBTHelper.getBlockPosList(nbt, "subNs"));
	}

	/** 檢查合并建築 */
	public void checkAndBuildStructure() {

		linkCount = 0;
		for (int i = 0; i < BlockIceRockStand.TOWER_MAX_HEIGHT; i++) {
			BlockPos at = pos.up(i + 1);
			TileIceRockCrystalBlock tile = BlockHelper.getTileEntity(world, at, TileIceRockCrystalBlock.class);
			if (tile == null) break;
			if (tile.canNotLinkMark) break;
			setMagicFragment(getMagicFragment() + tile.transferMagicFragment());
			tile.link(pos);
			linkCount++;
		}
		if (linkCount > 0) {
			ergodicSubNodes(sub -> {
				sub.link(pos);
				return null;
			});
		}

		markDirty();
	}

	/** 檢查摧毀建築 */
	public void checkAndBreakStructure() {

		List<TileIceRockCrystalBlock> breakList = new ArrayList<>(this.linkCount);
		for (int i = 0; i < BlockIceRockStand.TOWER_MAX_HEIGHT; i++) {
			BlockPos at = pos.up(i + 1);
			TileIceRockCrystalBlock tile = BlockHelper.getTileEntity(world, at, TileIceRockCrystalBlock.class);
			if (tile == null) continue;
			if (pos.equals(tile.getLinkPos())) breakList.add(tile);
		}

		if (!breakList.isEmpty()) {
			double singleMagicFragment = getMagicFragment() / breakList.size();
			for (TileIceRockCrystalBlock tile : breakList) {
				tile.setMagicFragmentOwn(singleMagicFragment);
				tile.unlink();
			}
		}

		ergodicSubNodes(sub -> {
			sub.unlink();
			return null;
		});

		linkCount = 0;
		setMagicFragment(0);
		markDirty();
	}

	@Override
	public void updateToClient() {
		lastUpdateMagicFragment = getMagicFragment();
		super.updateToClient();
	}

//	@Override
//	public void onInventoryStatusChange() {
//		updateToClient();
//	}

	protected ElementInventoryMonitor eim = new ElementInventoryMonitor();

	@Override
	protected void checkElementInventoryStatusChange() {
		if (world.isRemote) return;
		int imp = eim.checkChange(eInventoryAdapter);
		if (imp == -1) return;
		if (imp == 0 || imp == 1) updateToClient();
	}

	public int getLinkCount() {
		return linkCount;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(pos.add(-1, 0, -1), pos.add(1, linkCount + 1, 1));
	}

	public double getMagicFragmentCapacity() {
		return linkCount == 0 ? 0 : Math.pow(10, linkCount + 1 + 6);
	}

	public double getMaxFragmentOnceTransfer() {
		return getMagicFragmentCapacity() / 128;
	}

//	/** 高速缓存 */
//	protected WeakReference<TileIceRockSendRecv>[] tileCrystals;

	public TileIceRockSendRecv getTileCrystal(int n) {
		TileIceRockSendRecv tile = BlockHelper.getTileEntity(world, pos.up(n + 1), TileIceRockSendRecv.class);
		return tile;
	}

	@Override
	public void update() {
		tick++;

		for (int i = 0; i < linkCount; i++) {
			TileIceRockSendRecv tile = getTileCrystal(i);
			// 遍历中，出现找不到的情况，说明由于位置原因缺了一个
			if (tile == null) {
				this.checkAndBreakStructure();
				this.checkAndBuildStructure();
				this.updateToClient();
				return;
			}
			tile.onUpdate();
		}

		if (world.isRemote) return;

		// 尝试更新
		if (tick % 20 == 0) {
			double fragment = getMagicFragment();
			double log10 = Math.max(Math.log10(fragment) - 3, 1);
			if (Math.ceil(fragment / log10) != Math.ceil(lastUpdateMagicFragment / log10)) updateToClient();
		}

		if (tick % 200 == 0) onUpdateSubNode();

		if (attackReadyTick > 0) {
			attackReadyTick--;
			if (attackReadyTick == 0) doAttack();
		}
	}

	/**
	 * 
	 * return 还剩多少没插入 The remaining Fragment that was not inserted
	 */
	@Override
	public double insertMagicFragment(double count, boolean simulate) {
		double fragment = getMagicFragment();
		double capacity = getMagicFragmentCapacity();
		if (fragment >= capacity) return count;
		double newCount = fragment + count;
		double remian = 0;
		if (newCount > capacity) {
			remian = newCount - capacity;
			newCount = capacity;
		}
		if (simulate) return remian;
		setMagicFragment(newCount);
		markDirty();
		return remian;
	}

	/**
	 * 
	 * return 实际取出来的
	 */
	@Override
	public double extractMagicFragment(double count, boolean simulate) {
		double fragment = getMagicFragment();
		double extract = Math.min(fragment, count);
		if (simulate) return extract;
		setMagicFragment(fragment - extract);
		markDirty();
		return extract;
	}

	public int getRadiationRange() {
		return Math.min(linkCount * 5 + 8, 16 * 3);
	}

	public boolean isInRange(BlockPos pos, int extend) {
		int range = getRadiationRange();
		return MathHelper.sqrt(this.pos.distanceSq(pos)) <= range + extend;
	}

	public boolean isInRange(Vec3d pos, int extend) {
		int range = getRadiationRange();
		return new Vec3d(this.pos.getX() + 0.5, this.pos.getY() + 0.5,
				this.pos.getZ() + 0.5).distanceTo(pos) <= range + extend;
	}

	/** 进行攻击 */
	protected void doAttack() {
		if (attackVec == null) return;
		if (attackDamage <= 0) return;
		double rang = MathHelper.clamp(Math.pow(attackDamage, 0.25), 1, 4) + 2;
		AxisAlignedBB aabb = WorldHelper.createAABB(attackVec, rang, rang, rang);
		List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, aabb);
		for (EntityLivingBase entity : entities) {
			if (attackCaller != null && EntityHelper.isSameTeam(attackCaller, entity)) continue;
			DamageSource ds = DamageHelper.getMagicDamageSource(this.attackCaller, null);
			ds.setDamageBypassesArmor();
			float attenuation = (float) Math.max(entity.getDistance(attackVec.x, attackVec.y, attackVec.z) - 1, 0.8);
			entity.attackEntityFrom(ds, attackDamage / attenuation);
		}
		attackDamage = 0;
		attackVec = null;
	}

	public void notifySubNodeCome(TileIceRockSendRecv node) {
		if (world.isRemote) return;
		if (subNodes.indexOf(node.getPos()) == -1) node.unlink();
	}

	/** 获取子节点的最多个数 */
	public int getMaxSubNodeCount() {
		return linkCount - 2;
	}

	public void ergodicSubNodes(Function<TileIceRockNode, Boolean> act) {
		Iterator<BlockPos> iter = subNodes.iterator();
		while (iter.hasNext()) {
			BlockPos at = iter.next();
			TileIceRockNode node = BlockHelper.getTileEntity(world, at, TileIceRockNode.class);
			if (node == null) {
				iter.remove();
				continue;
			}
			BlockPos nodeLinkPos = node.getLinkPos();
			if (nodeLinkPos == null) node.link(pos);
			else if (!nodeLinkPos.equals(pos)) {
				iter.remove();
				continue;
			}
			Boolean ret = act.apply(node);
			if (ret == Boolean.TRUE) {
				iter.remove();
				node.unlink();
			}
		}
	}

	protected BlockPos lastNodeFindPos;

	public void onUpdateSubNode() {
		int maxCount = getMaxSubNodeCount();
		ergodicSubNodes(node -> {
			return subNodes.size() > maxCount || !isInRange(node.getPos(), 1);
		});
		if (maxCount <= subNodes.size()) return;

		while (maxCount > subNodes.size()) {
			TileIceRockNode node = spawnNode(findNodeGenPos());
			if (node == null) break;
		}
	}

	public TileIceRockNode spawnNode(BlockPos pos) {
		if (pos == null) return null;
		if (world.isRemote) return null;
		subNodes.add(pos);
		if (!world.setBlockState(pos, ESObjects.BLOCKS.ICE_ROCK_NODE.getDefaultState())) return null;
		TileIceRockNode node = BlockHelper.getTileEntity(world, pos, TileIceRockNode.class);
		for (EnumFacing facing : EnumFacing.VALUES) node.setFaceStatus(facing, FaceStatus.OUT);
		node.link(this.pos);
		node.markDirty();
		markDirty();
		if (!world.isRemote) {
			float r = (float) (getMagicFragment() / getMagicFragmentCapacity());
			r = MathHelper.clamp(r, 0, 1);
			Vec3d vec = new Vec3d(this.pos).add(0.5, 0.5 + linkCount / 2, 0.5);
			Vec3d to = new Vec3d(pos).add(0.5, 0.5, 0.5);
			Effects.spawnFragmentTo(world, vec, to, new Color(0x7cd0d3).weight(new Color(0x9956d0), r).toInt(), 1);
		}
		return node;
	}

	public BlockPos findNodeGenPos() {
		Random rand = world.rand;
		int range = getRadiationRange() - 4;
		BlockPos pos = this.pos.add(rand.nextGaussian() * range, 10, rand.nextGaussian() * range);
		if (!BlockHelper.isReplaceBlock(world, pos)) return null;
		for (int i = 0; i < 20; i++) {
			pos = pos.down();
			if (BlockHelper.isFluid(world, pos)) return pos.up();
			if (!BlockHelper.isReplaceBlock(world, pos)) return pos.up();
		}
		return null;
	}

	public boolean callSubNodeCome(BlockPos at) {
		if (!isInRange(at, 0)) return false;
		int maxCount = getMaxSubNodeCount();

		// 个数压根没到最大
		if (subNodes.size() < maxCount) {
			spawnNode(at);
			return true;
		}

		// 寻找最近的
		LambdaReference<Double> minDistance = LambdaReference.of(Double.MAX_VALUE);
		LambdaReference<TileIceRockNode> findedNode = LambdaReference.of(null);
		ergodicSubNodes(node -> {
			double dis = at.distanceSq(node.getPos());
			if (minDistance.get() > dis) {
				minDistance.set(dis);
				findedNode.set(node);
			}
			return false;
		});

		// 有被删除的
		if (subNodes.size() < maxCount) {
			spawnNode(at);
			return true;
		}

		TileIceRockNode node = findedNode.get();
		if (node == null) return false;

		node.unlink();
		BlockPos pos = node.getPos();
		subNodes.remove(pos);

		spawnNode(at);
		return true;
	}

	public boolean callFragmentAttack(EntityLivingBase target, EntityLivingBase caller) {
		if (attackReadyTick > 0) return false;
		if (target == null || !isInRange(target.getPositionVector(), 0)) return false;
		if (world.isRemote) return true;

		float hp = target.getHealth() + 2;
		double fragmentNeed = ONE_DAMAGE_ENND_FRAGMENT * Math.pow(hp, DAMAGE_GROW_POWER);
		double fragmentGet = extractMagicFragment(Math.min(fragmentNeed, getMaxFragmentOnceTransfer()), false);
		if (fragmentGet <= 0) return false;

		double dmg = getDamageWithFragment(fragmentGet);

		this.attackDamage = (float) dmg;
		this.attackCaller = caller;
		this.attackVec = target.getPositionVector().add(0, target.height / 2, 0);
		this.attackReadyTick = 20;

		double rang = MathHelper.clamp(Math.pow(attackDamage, 0.25), 1, 4) + 2;
		float r = (float) (getMagicFragment() / getMagicFragmentCapacity());
		r = MathHelper.clamp(r, 0, 1);
		Vec3d vec = new Vec3d(this.pos).add(0.5, 0.5 + linkCount / 2, 0.5);
		int color = new Color(0x7cd0d3).weight(new Color(0x9956d0), r).toInt();
		Effects.spawnFragmentTo(world, vec, this.attackVec, color, (1 << 24) | MathHelper.floor(rang));

		return true;
	}

	static public double getDamageWithFragment(double fragment) {
		return Math.pow(fragment / ONE_DAMAGE_ENND_FRAGMENT, 1 / DAMAGE_GROW_POWER);
	}

}

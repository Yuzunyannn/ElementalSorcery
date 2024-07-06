package yuzunyannn.elementalsorcery.tile.altar;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.IGetItemStack;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.item.IWindmillBlade;
import yuzunyannn.elementalsorcery.api.tile.IAltarWake;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.api.util.render.RenderFriend;
import yuzunyannn.elementalsorcery.building.Buildings;
import yuzunyannn.elementalsorcery.building.MultiBlock;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.batch.EffectElementAbsorb;
import yuzunyannn.elementalsorcery.render.effect.batch.EffectElementMove;
import yuzunyannn.elementalsorcery.util.TileEntityGetter;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;
import yuzunyannn.elementalsorcery.util.helper.NBTHelper;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class TileDeconstructWindmill extends TileStaticMultiBlock implements IGetItemStack, ITickable {

	@Override
	public void initMultiBlock() {
		structure = new MultiBlock(Buildings.DECONSTRUCT_WINDMILL, this, new BlockPos(0, -2, 0));
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		blade = nbtReadItemStack(compound, "blade");
		if (!isSending()) {
			NBTHelper.setElementList(compound, "elms", outList);
		}
		super.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		nbtWriteItemStack(compound, "blade", blade);
		if (!isSending()) {
			outList = NBTHelper.getElementList(compound, "elms");
		}
		return super.writeToNBT(compound);
	}

	private ItemStack blade = ItemStack.EMPTY;

	@Override
	public void setStack(ItemStack stack) {
		this.blade = stack;
		this.updateToClient();
		this.markDirty();
	}

	@Override
	public ItemStack getStack() {
		return blade;
	}

	@Override
	public boolean canSetStack(ItemStack stack) {
		return stack.getItem() instanceof IWindmillBlade;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(pos.add(-1, 0, -1), pos.add(1, 9, 1));
	}

	@Override
	public boolean isAndCheckIntact() {
		int checkInterval = 20 * 30;
		if (!ok) checkInterval = 40;
		if (checkTime++ % checkInterval == 0) checkIntact(structure);
		return this.ok;
	}

	@Override
	protected boolean checkIntact(MultiBlock structure) {
		EnumFacing facing = structure.face();
		ok = structure.check(facing);
		if (!ok) ok = structure.check(facing.rotateY());
		if (ok) {
			facing = structure.face().rotateY();
			ok = ok && world.isAirBlock(pos.up(5));
			ok = ok && world.isAirBlock(pos.up(5).offset(facing, 1));
			ok = ok && world.isAirBlock(pos.up(5).offset(facing, -1));
			for (int y = 0; y < 4; y++) {
				ok = ok && world.isAirBlock(pos.up(6 + y));
				ok = ok && world.isAirBlock(pos.up(6 + y).offset(facing, 1));
				ok = ok && world.isAirBlock(pos.up(6 + y).offset(facing, 2));
				ok = ok && world.isAirBlock(pos.up(6 + y).offset(facing, -1));
				ok = ok && world.isAirBlock(pos.up(6 + y).offset(facing, -2));
			}
		}
		return ok;
	}

	@Nullable
	public EnumFacing getOrientation() {
		return structure.face();
	}

	public IWindmillBlade getWindmillBlade() {
		if (blade.isEmpty()) return null;
		Item item = blade.getItem();
		if (item instanceof IWindmillBlade) return (IWindmillBlade) item;
		return null;
	}

	protected float speed = 0;
	protected List<ElementStack> outList = new ArrayList<>();
	protected TileEntityGetter tileGetter = new TileEntityGetter();

	@Override
	public void onLoad() {
		super.onLoad();
		tileGetter.setBox(WorldHelper.createAABB(pos, 6, 10, 3));
		tileGetter.setChecker(
				tile -> ElementHelper.canInsert(ElementHelper.getElementInventory(tile)) && tile instanceof IAltarWake);
	}

	@Override
	public void update() {

		if (world.isRemote) updateClient();
		if (!isAndCheckIntact()) return;

		IWindmillBlade windmillBlade = getWindmillBlade();
		boolean canTwirl = windmillBlade != null && windmillBlade.canTwirl(world, pos, blade);
		if (!canTwirl) {
			speed = speed - speed * 0.1f;
			return;
		}

		if (checkTime % 200 == 0) tileGetter.doCheck(world);

		try {
			BlockPos bladePos = pos.up(7);
			speed = speed + (windmillBlade.bladeWindScale(world, bladePos, blade) - speed) * 0.01f;
			boolean needUpdate = windmillBlade.bladeUpdate(world, bladePos, blade, outList, speed, checkTime);
			if (needUpdate) updateToClient();
			updateElementProduce();
		} catch (Exception e) {
			ESAPI.logger.warn("风车旋转出现异常", e);
			setStack(ItemStack.EMPTY);
		}
	}

	public static final int MAX_ONCE_PRODUCE = 10; // 一次最多产出的值
	public static final int MIN_PRODUCE_BEFORE_NEXT = 2; // 积累产出多少后，就可以退出了
	public static final int MAX_LIST_LENGTH = 32; // list最长多少，超出后，就强制产出

	public void updateElementProduce() {
		if (outList.isEmpty()) return;

		int produceCount = 0;
		Iterator<ElementStack> iter = outList.iterator();
		while (iter.hasNext() && produceCount < MIN_PRODUCE_BEFORE_NEXT) {
			ElementStack estack = iter.next();
			// 如果满足最一次最多的产出或者超过了队列长度，就直接生产
			if (estack.getCount() < MAX_ONCE_PRODUCE || outList.size() > MAX_LIST_LENGTH) {
				iter.remove();
				doProduce(estack);
				if (outList.size() <= MAX_LIST_LENGTH) produceCount = produceCount + estack.getCount();
				continue;
			}
			int output = MathHelper.ceil(estack.getCount() / 2f);
			doProduce(estack.splitStack(output));
			produceCount = produceCount + output;
		}
	}

	/** 进行一次产出 */
	public void doProduce(ElementStack estack) {
		if (estack.isEmpty()) return;

		TileEntity outTile = tileGetter.checkAndGetTileCanInsertElement(world, rand.nextInt(100), estack);
		if (world.isRemote) doProduceEffect(estack, outTile);
		if (outTile == null) return;

		IAltarWake altarWake = null;
		if (outTile instanceof IAltarWake) altarWake = ((IAltarWake) outTile);
		if (altarWake == null) return;

		IElementInventory eInv = ElementHelper.getElementInventory(outTile);
		boolean isEmpty = ElementHelper.isEmpty(eInv);
		eInv.insertElement(estack, false);
		altarWake.wake(IAltarWake.OBTAIN, pos);
		if (isEmpty) altarWake.onInventoryStatusChange();
	}

	@SideOnly(Side.CLIENT)
	public void doProduceEffect(ElementStack estack, TileEntity outTile) {
		Vec3d vec = new Vec3d(pos).add(0.5, 7.5, 0.5);
		vec = vec.add(rand.nextGaussian() * 1.25, rand.nextGaussian() * 1.25, rand.nextGaussian() * 1.25);
		Vec3d speed = new Vec3d(structure.face().getDirectionVec()).scale(0.05 * this.speed);
		if (outTile == null) {
			EffectElementMove effect = new EffectElementMove(world, vec);
			effect.setColor(estack.getColor());
			effect.setVelocity(speed);
			effect.xDecay = effect.zDecay = 0.9;
			Effect.addEffect(effect);
		} else {
			EffectElementAbsorb effect = new EffectElementAbsorb(world, vec,
					new Vec3d(outTile.getPos()).add(0.5, 0.5, 0.5));
			effect.setColor(estack.getColor());
			effect.setVelocity(speed);
			effect.startTick = 10;
			Effect.addEffect(effect);
		}
	}

	@SideOnly(Side.CLIENT)
	public float bladeRotate;
	@SideOnly(Side.CLIENT)
	public float prevBladeRotate;

	@SideOnly(Side.CLIENT)
	public float bladeShiftRate;
	@SideOnly(Side.CLIENT)
	public float prevBladeShiftRate;

	@SideOnly(Side.CLIENT)
	public void updateClient() {
		prevBladeRotate = bladeRotate;
		prevBladeShiftRate = bladeShiftRate;

		if (blade.isEmpty() || !ok) bladeShiftRate = bladeShiftRate - bladeShiftRate * 0.2f;
		else bladeShiftRate = bladeShiftRate + (1 - bladeShiftRate) * 0.2f;

		bladeRotate += speed * 0.04f;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public double getMaxRenderDistanceSquared() {
		if (TILE_ENTITY_RENDER_DISTANCE > 0) return TILE_ENTITY_RENDER_DISTANCE * TILE_ENTITY_RENDER_DISTANCE;
		int distance = RenderFriend.getRenderDistanceChunks() * 16;
		return distance * distance;
	}

	/** 根据位置获取通用风级 */
	public static float getWindScale(World world, BlockPos pos) {
		int dimension = world.provider.getDimension();

		int worldTime = (int) (world.getTotalWorldTime());

		if (dimension == 1) {
			int timeSpace = worldTime / 20 / 60;
			return 1 + (timeSpace % 10) / 9f * 3;
		}

		int randSeed = pos.getX() * pos.getX() + pos.getZ() + pos.getY() * worldTime;
		randSeed = randSeed ^ randSeed << 3;
		float rand = (Math.abs(randSeed) % 10000) / 10000f;

		Biome biome = world.getBiome(pos);
		float worldBaseWindScale = 3;
		float addition = 0;
		float targetHigh;

		if (dimension == -1) {
			int timeSpace = worldTime / 20 / 16;
			if (timeSpace % 5 == 0) worldBaseWindScale = 2;
			else worldBaseWindScale = 1;

			targetHigh = 40 - rand * 20;
		} else {
			float thunderStrength = world.getThunderStrength(1);
			float rainStrength = world.getRainStrength(1);
			if (!biome.canRain()) rainStrength = thunderStrength = 0;
			int timeSpace = worldTime / 20 / 120;
			if (timeSpace % 2 == 0) worldBaseWindScale = 2;
			if (timeSpace % 5 == 0) worldBaseWindScale = 0;
			if (rainStrength > 0) worldBaseWindScale = worldBaseWindScale + 0.5f;
			if (thunderStrength > 0 && worldBaseWindScale < 1.5f)
				worldBaseWindScale = worldBaseWindScale + thunderStrength;

			addition = rainStrength / 2 + thunderStrength * 1.5f;
			targetHigh = 130 + rand * 20;
		}

		if (worldBaseWindScale == 0) return 0;

		float high = pos.getY();
		float dif = Math.abs(targetHigh - high);

		float rate = 1;
		if (dif < 75) rate = 1 - dif / 85.3f;
		else rate = 1 / MathHelper.sqrt(dif);

		return worldBaseWindScale * rate * (1 + rand) * (1 + addition);
	}

}

package yuzunyannn.elementalsorcery.tile.ir;

import java.lang.ref.WeakReference;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;
import yuzunyannn.elementalsorcery.util.helper.NBTHelper;
import yuzunyannn.elementalsorcery.util.render.RenderHelper;

public abstract class TileIceRockSendRecv extends TileIceRockBase {

	static public enum FaceStatus {
		NONE,
		IN,
		OUT;
	};

	protected BlockPos linkPos;
	protected FaceStatus[] faceStatus = new FaceStatus[4];
	protected WeakReference<TileIceRockStand> tileCoreRef = new WeakReference<>(null);

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt = super.writeToNBT(nbt);
		byte[] fss = new byte[faceStatus.length];
		for (int i = 0; i < faceStatus.length; i++)
			fss[i] = (byte) (faceStatus[i] == null ? FaceStatus.NONE : faceStatus[i]).ordinal();
		nbt.setByteArray("fStatus", fss);
		if (linkPos != null) NBTHelper.setBlockPos(nbt, "linkPos", linkPos);

		if (isSending()) return nbt;

		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		byte[] fss = nbt.getByteArray("fStatus");
		for (int i = 0; i < Math.min(fss.length, faceStatus.length); i++) faceStatus[i] = FaceStatus.values()[fss[i]];

		if (NBTHelper.hasBlockPos(nbt, "linkPos")) linkPos = NBTHelper.getBlockPos(nbt, "linkPos");
		else linkPos = null;
	}

	public boolean isLinked() {
		return linkPos != null;
	}

	public BlockPos getLinkPos() {
		return linkPos;
	}

	public void link(BlockPos standPos) {
		linkPos = standPos;
		this.markDirty();
		world.notifyNeighborsOfStateChange(pos, world.getBlockState(pos).getBlock(), false);
	}

	public void unlink() {
		linkPos = null;
		this.markDirty();
		world.notifyNeighborsOfStateChange(pos, world.getBlockState(pos).getBlock(), false);
		tileCoreRef = new WeakReference<>(null);
	}

	private void findIceRockCore() {
		TileIceRockStand tile = BlockHelper.getTileEntity(world, linkPos, TileIceRockStand.class);
		tileCoreRef = new WeakReference<>(tile);
	}

	/** 获取核心能量单位 */
	@Nullable
	public TileIceRockStand getIceRockCore() {
		if (!isLinked()) return null;
		if (tileCoreRef.get() == null) findIceRockCore();
		TileIceRockStand tile = tileCoreRef.get();
		if (tile == null) {
			this.unlink();
			return null;
		}
		if (tile.isInvalid()) findIceRockCore();
		return tileCoreRef.get();
	}

	public FaceStatus getFaceStatus(EnumFacing facing) {
		try {
			FaceStatus status = faceStatus[facing.getHorizontalIndex()];
			return status == null ? FaceStatus.NONE : status;
		} catch (ArrayIndexOutOfBoundsException e) {
			return FaceStatus.NONE;
		}
	}

	public void setFaceStatus(EnumFacing facing, FaceStatus status) {
		try {
			faceStatus[facing.getHorizontalIndex()] = status;
		} catch (ArrayIndexOutOfBoundsException e) {}
	}

	public FaceStatus shiftFaceStatus(EnumFacing facing) {
		switch (getFaceStatus(facing)) {
		case IN:
			setFaceStatus(facing, FaceStatus.OUT);
			break;
		case OUT:
			setFaceStatus(facing, FaceStatus.NONE);
			break;
		default:
			setFaceStatus(facing, FaceStatus.IN);
			break;
		}
		if (world.isRemote) updateToClient();
		return getFaceStatus(facing);
	}

	public boolean doShiftStatus(EnumFacing facing, EntityLivingBase player) {
		if (world.isRemote) return true;
		FaceStatus oldStatus = getFaceStatus(facing);
		FaceStatus newStatus = shiftFaceStatus(facing);
		if (oldStatus == newStatus) return false;
		world.notifyNeighborsOfStateChange(pos, world.getBlockState(pos).getBlock(), false);
		this.markDirty();
		this.updateToClient();
		return true;
	}

	@Override
	protected void onFromOrToFragmentChange(double wastageFragment) {
		TileIceRockStand core = getIceRockCore();
		if (core == null) return;
		core.onFromOrToFragmentChange(wastageFragment);
	}

	@Override
	public double insertMagicFragment(double count, boolean simulate) {
		TileIceRockStand core = getIceRockCore();
		if (core == null) return count;
		return core.insertMagicFragment(count, simulate);
	}

	@Override
	public double extractMagicFragment(double count, boolean simulate) {
		TileIceRockStand core = getIceRockCore();
		if (core == null) return 0;
		return core.extractMagicFragment(count, simulate);
	}

	@Override
	public double getMagicFragment() {
		TileIceRockStand core = getIceRockCore();
		if (core != null) return core.getMagicFragment();
		return 0;
	}

	@Override
	protected void setMagicFragment(double fragment) {
		TileIceRockStand core = getIceRockCore();
		if (core != null) core.setMagicFragment(fragment);
	}

	protected int tick;

	public void onUpdate() {
		tick++;
		if (world.isRemote) onUpdateClient();
	}

	public void checkFaceChange(EnumFacing facing) {
		
	}

	@SideOnly(Side.CLIENT)
	private static class FaceAnimeData {
		public float r = 0;
		public float prevR = r;
		public FaceStatus lastStatus = FaceStatus.NONE;
		public boolean shouldShow = false;
		public int shiftTick = 0;
	}

	@SideOnly(Side.CLIENT)
	public boolean clientUpdateFlag;
	@SideOnly(Side.CLIENT)
	public FaceAnimeData[] faceAnimeData;

	@SideOnly(Side.CLIENT)
	public void initFaceAnimeData() {
		if (faceAnimeData != null) return;
		faceAnimeData = new FaceAnimeData[faceStatus.length];
		for (int i = 0; i < faceAnimeData.length; i++) {
			faceAnimeData[i] = new FaceAnimeData();
			checkFaceChange(EnumFacing.byHorizontalIndex(i));
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void handleUpdateTag(NBTTagCompound tag) {
		super.handleUpdateTag(tag);
		if (isSending()) clientUpdateFlag = true;
		else {
			initFaceAnimeData();
			for (int i = 0; i < faceAnimeData.length; i++) faceAnimeData[i].lastStatus = faceStatus[i];
		}
	}

	@SideOnly(Side.CLIENT)
	public void onUpdateClient() {
		initFaceAnimeData();
		for (FaceAnimeData dat : faceAnimeData) {
			dat.prevR = dat.r;
			if (dat.shiftTick > 0) dat.shiftTick--;
			if (!dat.shouldShow && dat.shiftTick <= 0) dat.r = dat.r + (0 - dat.r) * 0.1f;
			else dat.r = dat.r + (1 - dat.r) * 0.1f;
		}

		if (tick % 20 == 0) {
			for (EnumFacing facing : EnumFacing.HORIZONTALS)
				faceAnimeData[facing.getHorizontalIndex()].shouldShow = !world.isAirBlock(pos.offset(facing));
		}

		if (clientUpdateFlag) {
			clientUpdateFlag = false;
			for (int i = 0; i < faceAnimeData.length; i++) {
				FaceAnimeData dat = faceAnimeData[i];
				FaceStatus my = dat.lastStatus == null ? FaceStatus.NONE : dat.lastStatus;
				FaceStatus him = faceStatus[i] == null ? FaceStatus.NONE : faceStatus[i];
				if (my != him) {
					dat.lastStatus = faceStatus[i];
					dat.prevR = dat.r = 0;
					dat.shiftTick = 60;
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public float getFaceAnimeRate(EnumFacing facing, float partialTicks) {
		try {
			FaceAnimeData dat = faceAnimeData[facing.getHorizontalIndex()];
			return RenderHelper.getPartialTicks(dat.r, dat.prevR, partialTicks);
		} catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
			return 1;
		}
	}

}

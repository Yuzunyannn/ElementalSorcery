package yuzunyannn.elementalsorcery.tile.ir;

import java.lang.ref.WeakReference;

import javax.annotation.Nullable;

import io.netty.buffer.Unpooled;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.tile.IAltarWake;
import yuzunyannn.elementalsorcery.api.tile.IMagicBeamHandler;
import yuzunyannn.elementalsorcery.api.util.IWorldObject;
import yuzunyannn.elementalsorcery.api.util.NBTTag;
import yuzunyannn.elementalsorcery.api.util.client.RenderFriend;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.grimoire.EffectLaser;
import yuzunyannn.elementalsorcery.render.effect.grimoire.EffectLaserMagicTransfer;
import yuzunyannn.elementalsorcery.tile.altar.TileElementalCube;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;
import yuzunyannn.elementalsorcery.util.helper.Color;
import yuzunyannn.elementalsorcery.util.helper.NBTHelper;

public abstract class TileIceRockSendRecv extends TileIceRockBase implements IAltarWake {

	static public enum FaceStatus {
		NONE,
		IN,
		OUT;
	};

	protected BlockPos linkPos;
	protected FaceStatus[] faceStatus = new FaceStatus[6];
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

	public boolean hasUpDownFace() {
		return false;
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

	protected void findIceRockCore() {
		TileIceRockStand tile = BlockHelper.getTileEntity(world, linkPos, TileIceRockStand.class);
		tileCoreRef = new WeakReference<>(tile);
	}

	/** 获取核心能量单位 */
	@Nullable
	public TileIceRockStand getIceRockCore() {
		if (!isLinked()) return null;
		TileIceRockStand tile = tileCoreRef.get();
		if (tile == null || !tile.isAlive()) findIceRockCore();
		tile = tileCoreRef.get();
		if (tile == null) {
			this.unlink();
			return null;
		}
		return tile;
	}

	public FaceStatus getFaceStatus(EnumFacing facing) {
		try {
			if (facing == null) return FaceStatus.NONE;
			FaceStatus status = faceStatus[facing.getIndex()];
			return status == null ? FaceStatus.NONE : status;
		} catch (ArrayIndexOutOfBoundsException e) {
			return FaceStatus.NONE;
		}
	}

	public void setFaceStatus(EnumFacing facing, FaceStatus status) {
		try {
			faceStatus[facing.getIndex()] = status;
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

	/** 切换一个面的状态 */
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

	public void checkFaceChange(EnumFacing facing) {

	}

	public void setBeamHandler(EnumFacing facing, IMagicBeamHandler handler) {
		if (world.isRemote) return;
		int index = facing.getIndex();
		if (handler == null && faceBeamHandlers[index] == null) return;
		if (handler == faceBeamHandlers[index]) return;
		faceBeamHandlers[index] = handler;
		IWorldObject siteBinder = handler != null ? handler.getWorldObject() : null;
		if (faceBeamHandlerSites[index] == siteBinder) return;
		if (siteBinder != null && siteBinder.equals(faceBeamHandlerSites[index])) return;
		faceBeamHandlerSites[index] = siteBinder;
		// updateToClient();
	}

	protected IWorldObject[] faceBeamHandlerSites = new IWorldObject[faceStatus.length];
	protected IMagicBeamHandler[] faceBeamHandlers = new IMagicBeamHandler[faceStatus.length];
	// 当有输入输出的时候，这个值位置为倒计时，发送给客户端决定是否展示特效
	protected int[] faceBeamActiveTicks = new int[faceStatus.length];
	protected int tick;

	public void onUpdate() {
		tick++;
		if (world.isRemote) {
			onUpdateClient();
			return;
		}
		updateBeamTransmission();
	}

	private void updateBeamOnveFace(TileIceRockStand core, int i) {
		FaceStatus fs = getFaceStatus(EnumFacing.byIndex(i));
		if (fs != FaceStatus.IN && fs != FaceStatus.OUT) {
			faceBeamActiveTicks[i] = 0;
			return;
		}
		faceBeamActiveTicks[i] = Math.max(0, faceBeamActiveTicks[i] - 1);
		IMagicBeamHandler handler = faceBeamHandlers[i];
		double capacity = core.getMagicFragmentCapacity();
		double fragmet = core.getMagicFragment();
		if (fs == FaceStatus.IN) {
			// 输入
			double transferFragmet = Math.min(capacity - fragmet, core.getMaxFragmentOnceTransfer());
			if (transferFragmet > 0) {
				double extract = handler.extractMagicFragment(transferFragmet, false);
				if (extract > 0) {
					core.insertMagicFragment(extract, false);
					faceBeamActiveTicks[i] = 4 * 20;
				}
			}
			return;
		}
		// 输出
		double transferFragmet = Math.min(fragmet, core.getMaxFragmentOnceTransfer());
		if (transferFragmet > 0) {
			double extract = core.extractMagicFragment(transferFragmet, false);
			if (extract > 0) {
				double remain = handler.insertMagicFragment(extract, false);
				if (remain > 0) {
					core.insertMagicFragment(remain, false);
					if (remain < extract) faceBeamActiveTicks[i] = 4 * 20;
				}
			}
		}
	}

	/** 更新激光传输 */
	protected void updateBeamTransmission() {

		TileIceRockStand core = getIceRockCore();
		if (core == null) return;

		boolean faceActiveChange = false;
		for (int i = 0; i < faceBeamHandlers.length; i++) {
			IMagicBeamHandler handler = faceBeamHandlers[i];
			if (handler == null) {
				// 被设置为null，数据要同步到前端
				if (faceBeamActiveTicks[i] > 0) {
					faceBeamActiveTicks[i] = 0;
					faceActiveChange = true;
				}
				continue;
			}
			if (!handler.isAlive()) {
				faceActiveChange = true;
				setBeamHandler(EnumFacing.byIndex(i), null);
				continue;
			}
			int oaTick = faceBeamActiveTicks[i];
			updateBeamOnveFace(core, i);
			int caTick = faceBeamActiveTicks[i];
			if ((oaTick == 0 && caTick > 0) || (oaTick > 0 && caTick == 0)) faceActiveChange = true;
		}

		if (faceActiveChange) updateToClient();
	}

	@Override
	public boolean wake(int type, BlockPos from) {
		return true;
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound nbt = super.getUpdateTag();
		PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
		for (int i = 0; i < faceBeamHandlerSites.length; i++) {
			if (faceBeamHandlerSites[i] != null && faceBeamHandlers[i] != null) {
				buf.writeByte(i);
				buf.writeByte(faceBeamActiveTicks[i] == 0 ? 0 : 1);
				IWorldObject.writeSendToBuf(buf, faceBeamHandlerSites[i]);
			}
		}
		if (buf.writerIndex() > 0) {
			byte[] bytes = new byte[buf.writerIndex()];
			buf.getBytes(0, bytes);
			nbt.setByteArray("fbSites", bytes);
		}
		return nbt;
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
		for (int i = 0; i < faceBeamHandlerSites.length; i++) faceBeamHandlerSites[i] = null;
		if (tag.hasKey("fbSites", NBTTag.TAG_BYTE_ARRAY)) {
			PacketBuffer buf = new PacketBuffer(Unpooled.wrappedBuffer(tag.getByteArray("fbSites")));
			while (buf.readerIndex() < buf.readableBytes()) {
				byte i = buf.readByte();
				faceBeamActiveTicks[i] = buf.readByte();
				faceBeamHandlerSites[i] = IWorldObject.readSendFromBuf(buf, world);
			}
		}

	}

	@SideOnly(Side.CLIENT)
	private static class FaceAnimeData {
		public FaceStatus lastStatus = FaceStatus.NONE;
		public float r = 0;
		public float prevR = r;
		public boolean shouldShow = false;
		public int shiftTick = 0;
		public EffectLaser effectLaser;
	}

	@SideOnly(Side.CLIENT)
	public boolean clientUpdateFlag;

	@SideOnly(Side.CLIENT)
	public FaceAnimeData[] faceAnimeData;

	@SideOnly(Side.CLIENT)
	public Color renderColor;

	@SideOnly(Side.CLIENT)
	public float stockRatio;

	@SideOnly(Side.CLIENT)
	public Color getRenderColor() {
		if (renderColor == null) renderColor = new Color(0x7cd0d3).weight(new Color(0x9956d0), stockRatio);
		return renderColor;
	}

	@SideOnly(Side.CLIENT)
	public void initFaceAnimeData() {
		if (faceAnimeData != null) return;
		faceAnimeData = new FaceAnimeData[faceStatus.length];
		for (int i = 0; i < faceAnimeData.length; i++) {
			faceAnimeData[i] = new FaceAnimeData();
			checkFaceChange(EnumFacing.byIndex(i));
		}
	}

	@SideOnly(Side.CLIENT)
	protected void updateFacingAnimeStatus(EnumFacing facing) {
		int index = facing.getIndex();
		FaceAnimeData fad = faceAnimeData[index];
		fad.shouldShow = !world.isAirBlock(pos.offset(facing));
		if (faceBeamHandlerSites[index] != null && faceBeamActiveTicks[index] > 0) {
			if (fad.effectLaser == null) {
				EffectLaserMagicTransfer laser = new EffectLaserMagicTransfer(world, this, facing, new Vec3d(this.pos),
						faceBeamHandlerSites[index].getObjectPosition());
				laser.color.setColor(0x7cd0d3).weight(new Color(0xffffff), 0.75f);
				laser.magicColor.setColor(0x9956d0).weight(laser.color, 0.5f);
				fad.effectLaser = laser;
				updateEffectLaser(index, true);
				Effect.addEffect(fad.effectLaser);
			}
		} else if (fad.effectLaser != null) fad.effectLaser = null;
	}

	@SideOnly(Side.CLIENT)
	protected void updateEffectLaser(int index, boolean isForce) {
		FaceAnimeData fad = faceAnimeData[index];
		if (faceBeamHandlerSites[index] == null || faceBeamActiveTicks[index] <= 0) {
			fad.effectLaser = null;
			return;
		}
		IWorldObject wobj = faceBeamHandlerSites[index];
		if (wobj.asTileEntity() != null) {
			if (isForce) {
				Vec3d myPos = new Vec3d(getPos()).add(0.5, 0.5, 0.5);
				Vec3d targetPos = new Vec3d(wobj.asTileEntity().getPos()).add(0.5, 0.5, 0.5);
				Vec3d tar = targetPos.subtract(myPos).normalize();
				myPos = myPos.add(tar.scale(0.6));
				targetPos = targetPos.add(tar.scale(-0.5));
				fad.effectLaser.setPosition(myPos);
				fad.effectLaser.setTargetPosition(targetPos);
			}
		} else if (wobj.asEntity() != null) {
			Vec3d myPos = new Vec3d(getPos()).add(0.5, 0.5, 0.5);
			Vec3d targetPos = wobj.getObjectPosition();
			Vec3d tar = targetPos.subtract(myPos).normalize();
			myPos = myPos.add(tar.scale(0.6));
			fad.effectLaser.setPosition(myPos);
			fad.effectLaser.setTargetPosition(targetPos);
		}

	}

	@SideOnly(Side.CLIENT)
	public void onUpdateClient() {
		initFaceAnimeData();

		if ((tick - 1) % 20 == 0) {
			TileIceRockStand core = getIceRockCore();
			if (core != null) {
				stockRatio = (float) Math.min(core.getMagicFragment() / core.getMagicFragmentCapacity(), 1);
				renderColor = null;
			}
		}

		for (int i = 0; i < faceAnimeData.length; i++) {
			FaceAnimeData dat = faceAnimeData[i];
			dat.prevR = dat.r;
			if (dat.shiftTick > 0) dat.shiftTick--;
			if (dat.effectLaser != null) {
				dat.effectLaser.hold(20);
				updateEffectLaser(i, false);
			}
			if (!dat.shouldShow && dat.shiftTick <= 0 && dat.effectLaser == null) dat.r = dat.r + (0 - dat.r) * 0.1f;
			else dat.r = dat.r + (1 - dat.r) * 0.1f;
		}

		if (tick % 20 == 0) {
			if (hasUpDownFace()) for (EnumFacing facing : EnumFacing.VALUES) updateFacingAnimeStatus(facing);
			else for (EnumFacing facing : EnumFacing.HORIZONTALS) updateFacingAnimeStatus(facing);
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

	// 获取展示特效的动画进度
	@SideOnly(Side.CLIENT)
	public float getFaceAnimeRatio(EnumFacing facing, float partialTicks) {
		try {
			if (!hasUpDownFace() && facing.getHorizontalIndex() < 0) return 0;
			FaceAnimeData dat = faceAnimeData[facing.getIndex()];
			return RenderFriend.getPartialTicks(dat.r, dat.prevR, partialTicks);
		} catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
			return 0;
		}
	}

	// 侧面是否能展示特效
	@SideOnly(Side.CLIENT)
	public boolean needRenderFaceEffect() {
		return isLinked();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateEffect(World world, int type, ElementStack estack, Vec3d pos) {
		Vec3d myPos = new Vec3d(this.pos).add(0.5, 0.25 + 0.5, 0.5);
		if (type == IAltarWake.SEND) TileElementalCube.giveParticleElementTo(world, estack.getColor(), myPos, pos, 1);
		else TileElementalCube.giveParticleElementTo(world, estack.getColor(), pos, myPos, 1);
	}

}

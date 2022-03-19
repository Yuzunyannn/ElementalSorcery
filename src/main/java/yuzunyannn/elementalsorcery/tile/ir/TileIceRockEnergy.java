package yuzunyannn.elementalsorcery.tile.ir;

import ic2.api.energy.tile.IEnergyAcceptor;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.tile.IAcceptMagic;
import yuzunyannn.elementalsorcery.api.tile.IProvideMagic;
import yuzunyannn.elementalsorcery.block.BlockMagicTorch;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.mods.Mods;
import yuzunyannn.elementalsorcery.mods.ic2.IC2EnergyTileHelper;
import yuzunyannn.elementalsorcery.tile.md.TileMDBase;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;

@Optional.Interface(iface = "ic2.api.energy.tile.IEnergySink", modid = Mods.IC2)
@Optional.Interface(iface = "ic2.api.energy.tile.IEnergySource", modid = Mods.IC2)
public abstract class TileIceRockEnergy extends TileIceRockSendRecv
		implements IEnergySink, IEnergySource, IAcceptMagic, IProvideMagic {

	// 1 UE = 4 FRAGMENT
	// 1 RF = 1 FRAGMENT

	static final double FRAGMENT_UE = 4;
	static final double FRAGMENT_RF = 1;

	public TileIceRockEnergy() {
		if (Mods.isLoaded(Mods.IC2)) this.initIC2();
	}

	@Override
	public void onLoad() {
		super.onLoad();
		if (Mods.isLoaded(Mods.IC2)) this.onLoadIC2();
		findMagicDriverAll();
		checkAroundEnergyCache();
	}

	@Override
	public void onChunkUnload() {
		super.onChunkUnload();
		if (Mods.isLoaded(Mods.IC2)) this.onChunkUnloadIC2();
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (tick % 80 == 0) findMagicDriverAll();
		if (tick % 5 == 0) onUpdateMagicTranfer(tick);
		updateEnergy();
	}

	@Override
	public boolean doShiftStatus(EnumFacing facing, EntityLivingBase player) {
		if (super.doShiftStatus(facing, player)) {

			if (ic2EnergyTileHelper != null) {
				ic2EnergyTileHelper.invalidate();
				ic2EnergyTileHelper.onLoad();
			}

			return true;
		}
		return false;
	}

	public boolean isCannotTransferTile(TileEntity other, int type) {
		if (other == null) return true;
		if (other instanceof TileIceRockSendRecv) {
			if (type == 1) return true;
			TileIceRockSendRecv sr = (TileIceRockSendRecv) other;
			if (sr.getLinkPos() == null) return true;
			if (this.getLinkPos() == null) return true;
			if (this.getLinkPos().equals(sr.getLinkPos())) return true;
		}
		return false;
	}

	@Override
	public void link(BlockPos standPos) {
		super.link(standPos);
		checkAroundEnergyCache();
	}

	/**
	 * ==================================================================== ^_^ <br>
	 * ↓ MD Part ↓ <br>
	 * ==================================================================== ^_^
	 */

	protected BlockPos[] mdFacePos;

	public void onUpdateMagicTranfer(int tick) {
		if (world.isRemote) {
			this.onUpdateMagicTranferClient(tick);
			return;
		}
		BlockPos pos = getPos();
		if (mdFacePos == null) return;
		ElementStack sendMax = ElementStack.magic(TileMDBase.MD_BASE_SEND_PRE_SECOND / 4 * 10, 50);
		ElementStack magic = eInventoryAdapter.extractElement(sendMax, true);
		if (magic.getCount() < 4) {
			for (EnumFacing facing : EnumFacing.HORIZONTALS) TileMDBase.torch(world, pos, facing, false);
			return;
		}
		magic.setCount(magic.getCount() / 4);

		for (EnumFacing facing : EnumFacing.HORIZONTALS) {
			FaceStatus fs = getFaceStatus(facing);
			if (fs != FaceStatus.OUT) {
				TileMDBase.torch(world, pos, facing, false);
				continue;
			}
			int index = facing.getHorizontalIndex();
			BlockPos at = mdFacePos[index];
			if (at == null) continue;
			IAcceptMagic accepter = BlockHelper.getTileEntity(world, at, IAcceptMagic.class);
			if (accepter == null) {
				mdFacePos[index] = null;
				TileMDBase.torch(world, pos, facing, false);
				continue;
			}
			ElementStack remain = accepter.accpetMagic(magic.copy(), pos, facing.getOpposite());
			if (remain.getCount() == magic.getCount()) {
				TileMDBase.torch(world, pos, facing, false);
				continue;
			}
			TileMDBase.torch(world, pos, facing, true);
			ElementStack drop = magic.copy();
			drop.setCount(magic.getCount() - remain.getCount());
			eInventoryAdapter.extractElement(drop, false);
		}
	}

	@SideOnly(Side.CLIENT)
	public void onUpdateMagicTranferClient(int tick) {
		if (tick % 2 == 0) return;
		for (EnumFacing facing : EnumFacing.HORIZONTALS) {
			if (TileMDBase.hasTorch(world, pos, facing)) {
				if (world.getBlockState(pos.offset(facing)).getValue(BlockMagicTorch.LIT)) {
					int index = facing.getHorizontalIndex();
					if (mdFacePos == null || mdFacePos[index] == null) findMagicDriver(facing, false);
					if (mdFacePos != null && mdFacePos[index] != null)
						TileMDBase.magicEffectTo(world, pos, facing, mdFacePos[index]);
				}
			}
		}
	}

	@Override
	public ElementStack accpetMagic(ElementStack magic, BlockPos from, EnumFacing facing) {
		FaceStatus fs = getFaceStatus(facing);
		if (fs != FaceStatus.IN) return magic;
		return eInventoryAdapter.insertElement(magic, false) ? ElementStack.EMPTY : magic;
	}

	@Override
	public void hi(BlockPos from, EnumFacing facing) {
		int index = facing.getHorizontalIndex();
		if (index < 0) return;
		findMagicDriver(facing, false);
	}

	public void findMagicDriverAll() {
		for (EnumFacing facing : EnumFacing.HORIZONTALS) {
			FaceStatus fs = getFaceStatus(facing);
			if (fs == FaceStatus.OUT) findMagicDriver(facing, true);
		}
	}

	public void findMagicDriver(EnumFacing facing, boolean sayHi) {
		int index = facing.getHorizontalIndex();
		if (!TileMDBase.hasTorch(world, pos, facing)) ending: {
			FaceStatus fs = getFaceStatus(facing);
			if (fs == FaceStatus.OUT) {
				TileEntity tile = (TileEntity) BlockHelper.getTileEntity(world, pos.offset(facing), IAcceptMagic.class);
				if (!isCannotTransferTile(tile, 1)) break ending;
			}
			if (mdFacePos == null) return;
			mdFacePos[index] = null;
			TileMDBase.torch(world, pos, facing, false);
			return;
		}
		if (mdFacePos == null) mdFacePos = new BlockPos[4];
		TileEntity tile = TileMDBase.findTarget(world, pos, facing, 16);
		if (isCannotTransferTile(tile, 2)) tile = null;
		mdFacePos[index] = tile == null ? null : tile.getPos();
		if (mdFacePos[index] != null) {
			if (sayHi && tile instanceof IProvideMagic) ((IProvideMagic) tile).hi(pos, facing.getOpposite());
		} else TileMDBase.torch(world, pos, facing, false);
	}

	/**
	 * ==================================================================== ^_^ <br>
	 * ↓ Forge Engery Part For RF ↓ <br>
	 * ==================================================================== ^_^
	 */

	protected IEnergyStorage[] energyCache = new IEnergyStorage[faceStatus.length];

	public void updateEnergy() {

		TileIceRockStand core = getIceRockCore();
		if (core == null) return;

		for (int i = 0; i < energyCache.length; i++) {
			IEnergyStorage storage = energyCache[i];
			if (storage == null) continue;
			EnumFacing facing = EnumFacing.byIndex(i);
			FaceStatus fs = getFaceStatus(facing);
			if (fs != FaceStatus.OUT) continue;
			double capacity = core.getMagicFragmentCapacity();
			double fragmet = core.getMagicFragment();
			double sendFragmet = Math.min(capacity - fragmet, core.getMaxFragmentOnceTransfer());
			sendFragmet = extractMagicFragment(sendFragmet, true);
			if (sendFragmet == 0) continue;
			int sendRF = MathHelper.floor(sendFragmet / FRAGMENT_RF);
			int receiveRF = storage.receiveEnergy(sendRF, false);
			extractMagicFragment(receiveRF * FRAGMENT_RF, false);
		}

	}

	public void checkAroundEnergyCache() {
		if (hasUpDownFace()) for (EnumFacing facing : EnumFacing.VALUES) checkFaceChange(facing);
		else for (EnumFacing facing : EnumFacing.HORIZONTALS) checkFaceChange(facing);
	}

	@Override
	public void checkFaceChange(EnumFacing facing) {
		if (world.isRemote) return;
		if (!hasUpDownFace() && facing.getHorizontalIndex() < 0) return;
		energyCache[facing.getIndex()] = null;
		TileEntity tile = world.getTileEntity(pos.offset(facing));
		if (tile == null) return;
		if (isCannotTransferTile(tile, 2)) return;
		if (!tile.hasCapability(CapabilityEnergy.ENERGY, facing.getOpposite())) return;
		energyCache[facing.getIndex()] = tile.getCapability(CapabilityEnergy.ENERGY, facing.getOpposite());
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (CapabilityEnergy.ENERGY.equals(capability)) {
			FaceStatus fs = getFaceStatus(facing);
			if (fs == FaceStatus.NONE) return false;
			TileIceRockStand core = getIceRockCore();
			if (core == null) return false;
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (CapabilityEnergy.ENERGY.equals(capability)) {
			FaceStatus fs = getFaceStatus(facing);
			if (fs == FaceStatus.NONE) return null;
			TileIceRockStand core = getIceRockCore();
			if (core == null) return null;
			return (T) new EnergyStorage(facing);
		}
		return super.getCapability(capability, facing);
	}

	protected class EnergyStorage implements IEnergyStorage {

		public final EnumFacing facing;

		public EnergyStorage(EnumFacing facing) {
			this.facing = facing;
		}

		@Override
		public int receiveEnergy(int maxReceive, boolean simulate) {
			FaceStatus fs = getFaceStatus(facing);
			if (fs != FaceStatus.IN) return 0;
			TileIceRockStand core = getIceRockCore();
			if (core == null) return 0;
			double realFragment = maxReceive * FRAGMENT_RF;
			double remainFragment = core.insertMagicFragment(realFragment, simulate);
			double insertFragment = realFragment - remainFragment;
			int insertRF = MathHelper.ceil(insertFragment / FRAGMENT_RF);
			if (!simulate) core.onFromOrToFragmentChange(insertRF * FRAGMENT_RF - insertFragment);
			return insertRF;
		}

		@Override
		public int extractEnergy(int maxExtract, boolean simulate) {
			FaceStatus fs = getFaceStatus(facing);
			if (fs != FaceStatus.OUT) return 0;
			TileIceRockStand core = getIceRockCore();
			if (core == null) return 0;
			double getFragment = core.extractMagicFragment(maxExtract * FRAGMENT_RF, simulate);
			int extractRF = MathHelper.floor(getFragment / FRAGMENT_RF);
			if (!simulate) core.onFromOrToFragmentChange(getFragment - extractRF * FRAGMENT_RF);
			return extractRF;
		}

		@Override
		public int getEnergyStored() {
			TileIceRockStand core = getIceRockCore();
			if (core == null) return 0;
			return MathHelper.floor(core.getMagicFragment() / FRAGMENT_RF);
		}

		@Override
		public int getMaxEnergyStored() {
			TileIceRockStand core = getIceRockCore();
			if (core == null) return 0;
			return MathHelper.floor(core.getMagicFragmentCapacity() / FRAGMENT_RF);
		}

		@Override
		public boolean canExtract() {
			FaceStatus fs = getFaceStatus(facing);
			if (fs != FaceStatus.OUT) return false;
			TileIceRockStand core = getIceRockCore();
			if (core == null) return false;
			return true;
		}

		@Override
		public boolean canReceive() {
			FaceStatus fs = getFaceStatus(facing);
			if (fs != FaceStatus.IN) return false;
			TileIceRockStand core = getIceRockCore();
			if (core == null) return false;
			return true;
		}

	}

	/**
	 * ==================================================================== ^_^ <br>
	 * ↓ IC2 Part ↓ <br>
	 * ==================================================================== ^_^
	 */

	IC2EnergyTileHelper ic2EnergyTileHelper = null;

	@Optional.Method(modid = Mods.IC2)
	private void initIC2() {
		ic2EnergyTileHelper = IC2EnergyTileHelper.create(this);
	}

	@Override
	@Optional.Method(modid = Mods.IC2)
	public void invalidate() {
		super.invalidate();
		if (ic2EnergyTileHelper != null) ic2EnergyTileHelper.invalidate();
	}

	@Optional.Method(modid = Mods.IC2)
	public void onLoadIC2() {
		if (ic2EnergyTileHelper != null) ic2EnergyTileHelper.onLoad();
	}

	@Optional.Method(modid = Mods.IC2)
	public void onChunkUnloadIC2() {
		if (ic2EnergyTileHelper != null) ic2EnergyTileHelper.onChunkUnload();
	}

	@Override
	@Optional.Method(modid = Mods.IC2)
	public boolean acceptsEnergyFrom(IEnergyEmitter emitter, EnumFacing facing) {
		FaceStatus fs = getFaceStatus(facing);
		if (fs != FaceStatus.IN) return false;
		TileIceRockStand core = getIceRockCore();
		if (core == null) return false;
		return true;
	}

	@Override
	@Optional.Method(modid = Mods.IC2)
	public double injectEnergy(EnumFacing facing, double amount, double voltage) {
		return this.insertMagicFragment(amount * FRAGMENT_UE, false) / FRAGMENT_UE;
	}

	@Override
	@Optional.Method(modid = Mods.IC2)
	public double getDemandedEnergy() {
		TileIceRockStand core = getIceRockCore();
		if (core == null) return 0;
		double capacity = core.getMagicFragmentCapacity();
		double fragmet = core.getMagicFragment();
		return (capacity - fragmet) / FRAGMENT_UE;
	}

	@Override
	@Optional.Method(modid = Mods.IC2)
	public int getSinkTier() {
		return Integer.MAX_VALUE;
	}

	@Override
	@Optional.Method(modid = Mods.IC2)
	public boolean emitsEnergyTo(IEnergyAcceptor acceptor, EnumFacing facing) {
		FaceStatus fs = getFaceStatus(facing);
		if (fs != FaceStatus.OUT) return false;
		TileIceRockStand core = getIceRockCore();
		if (core == null) return false;
		return true;
	}

	@Override
	@Optional.Method(modid = Mods.IC2)
	public void drawEnergy(double amount) {
		if (amount > 0) extractMagicFragment(amount * FRAGMENT_UE, false);
		else insertMagicFragment(-amount * FRAGMENT_UE, false);
	}

	@Override
	@Optional.Method(modid = Mods.IC2)
	public double getOfferedEnergy() {
		TileIceRockStand core = getIceRockCore();
		if (core == null) return 0;
		return Math.min(core.getMaxFragmentOnceTransfer(), core.getMagicFragment()) / FRAGMENT_UE;
	}

	@Override
	@Optional.Method(modid = Mods.IC2)
	public int getSourceTier() {
		return 4;
	}

}

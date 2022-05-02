package yuzunyannn.elementalsorcery.container;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.grimoire.remote.IFragmentMantraLauncher;
import yuzunyannn.elementalsorcery.grimoire.remote.IFragmentMantraLauncher.MLPair;
import yuzunyannn.elementalsorcery.network.MessageSyncContainer.IContainerNetwork;
import yuzunyannn.elementalsorcery.tile.altar.TileElementReactor;
import yuzunyannn.elementalsorcery.tile.altar.TileElementReactor.ReactorStatus;
import yuzunyannn.elementalsorcery.util.NBTTag;
import yuzunyannn.elementalsorcery.util.element.ElementTransitionReactor;
import yuzunyannn.elementalsorcery.util.helper.NBTHelper;
import yuzunyannn.elementalsorcery.util.world.WorldLocation;

public class ContainerElementReactor extends Container implements IContainerNetwork {

	final public TileElementReactor tileEntity;
	final public EntityPlayer player;
	final public BlockPos pos;
	public Object guiObject;

	public ContainerElementReactor(EntityPlayer player, TileEntity tileEntity) {
		this.tileEntity = (TileElementReactor) tileEntity;
		this.player = player;
		this.pos = this.tileEntity.getPos();
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return playerIn.getDistanceSq(this.tileEntity.getPos()) <= 64
				&& player.world.getTileEntity(this.pos) == this.tileEntity
				&& tileEntity.getStatus() != ReactorStatus.OFF;
	}

	@Override
	public void recvData(NBTTagCompound nbt, Side side) {
		if (side != Side.SERVER) {
			if (nbt.hasKey("mpb", NBTTag.TAG_BYTE_ARRAY)) {
				ByteBuf buf = new PacketBuffer(Unpooled.wrappedBuffer(nbt.getByteArray("mpb")));
				tileEntity.getWorldMap().readSendBlockData(buf);
			}
			if (nbt.hasKey("mpe", NBTTag.TAG_BYTE_ARRAY)) {
				ByteBuf buf = new PacketBuffer(Unpooled.wrappedBuffer(nbt.getByteArray("mpe")));
				tileEntity.getWorldMap().readSendEntityData(buf);
			}
			if (nbt.hasKey("status")) tileEntity.handleUpdateTagFromPacketData(nbt);
			return;
		}
		if (nbt.getBoolean("L")) tileEntity.launch();
		else if (nbt.getBoolean("S")) tileEntity.shutdown();
		else if (nbt.hasKey("M")) {
			MLPair launcher = IFragmentMantraLauncher.fromId(nbt.getString("M"));
			if (launcher != null) tileEntity.launchMantra(launcher);
		} else if (nbt.getBoolean("SML")) {
			WorldLocation location = new WorldLocation(nbt);
			tileEntity.updateMapLocation(location, false);
		} else if (nbt.hasKey("ML!")) {
			BlockPos offset = NBTHelper.getBlockPos(nbt, "ML!");
			BlockPos base = NBTHelper.getBlockPos(nbt, "Base");
			tileEntity.launchTargetLocation(base, offset);
		} else if (nbt.hasKey("FC~")) {
			tileEntity.tryShiftChargeStatus(nbt.getBoolean("FC~"));
		}
	}

	public double lastFragment = 0;
	public int lastIFragment = 0;
	public int worldMapBlockVer = 0;
	public int worldMapEntityVer = 0;
	public IFragmentMantraLauncher.MLPair lastPair;
	public boolean isFirst = true;
	public int lastProgress;
	public int lastWaitSpellTick;
	public boolean lastChargeFinMark;

	@Override
	public void detectAndSendChanges() {
		tileEntity.hasInConatinerMark = true;
		super.detectAndSendChanges();
		ElementTransitionReactor reactor = tileEntity.getReactorCore();
		int ifCheck = MathHelper.floor(tileEntity.getInstableFragment() * 10);
		if (reactor.getFragment() != lastFragment || ifCheck != lastIFragment
				|| lastPair != tileEntity.getRunningMantraPair() || isFirst) {
			lastFragment = reactor.getFragment();
			lastIFragment = ifCheck;
			lastPair = tileEntity.getRunningMantraPair();
			NBTTagCompound nbt = tileEntity.getUpdateTagForUpdateToClient();
			if (isFirst) nbt.setTag("mas", NBTHelper.serializeMantra(tileEntity.getMantras()));
			this.sendToClient(nbt, player);
			isFirst = false;
		}
		if (worldMapBlockVer != tileEntity.getWorldMap().blockUpdateVer) {
			worldMapBlockVer = tileEntity.getWorldMap().blockUpdateVer;
			NBTTagCompound nbt = new NBTTagCompound();
			PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
			tileEntity.getWorldMap().writeSendBlockData(buf);
			byte[] bytes = new byte[buf.writerIndex()];
			buf.getBytes(0, bytes);
			nbt.setByteArray("mpb", bytes);
			this.sendToClient(nbt, player);
		}
		if (worldMapEntityVer != tileEntity.getWorldMap().entityUpdateVer) {
			worldMapEntityVer = tileEntity.getWorldMap().entityUpdateVer;
			NBTTagCompound nbt = new NBTTagCompound();
			PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
			tileEntity.getWorldMap().writeSendEntityData(buf);
			byte[] bytes = new byte[buf.writerIndex()];
			buf.getBytes(0, bytes);
			nbt.setByteArray("mpe", bytes);
			this.sendToClient(nbt, player);
		}
		int p = (int) (tileEntity.mantraChargeProgress * 1000);
		if (p != lastProgress) {
			lastProgress = p;
			((IContainerListener) player).sendWindowProperty(this, 1, p);
		}
		if (tileEntity.waitSpellTick != lastWaitSpellTick) {
			lastWaitSpellTick = tileEntity.waitSpellTick;
			((IContainerListener) player).sendWindowProperty(this, 2, tileEntity.waitSpellTick);
		}
		if (tileEntity.isChargeFinMark != lastChargeFinMark) {
			lastChargeFinMark = tileEntity.isChargeFinMark;
			((IContainerListener) player).sendWindowProperty(this, 3, tileEntity.isChargeFinMark ? 1 : 0);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int data) {
		super.updateProgressBar(id, data);
		if (id == 1) tileEntity.mantraChargeProgress = data / 1000f;
		else if (id == 2) tileEntity.waitSpellTick = data;
		else if (id == 3) tileEntity.isChargeFinMark = data == 0 ? false : true;
	}
}

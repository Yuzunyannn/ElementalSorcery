package yuzunyannn.elementalsorcery.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import yuzunyannn.elementalsorcery.network.MessageSyncContainer.IContainerNetwork;
import yuzunyannn.elementalsorcery.tile.altar.TileElementReactor;
import yuzunyannn.elementalsorcery.tile.altar.TileElementReactor.ReactorStatus;
import yuzunyannn.elementalsorcery.util.element.ElementTransitionReactor;

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
			tileEntity.handleUpdateTagFromPacketData(nbt);
			return;
		}
		if (nbt.getBoolean("L")) tileEntity.launch();
		else if (nbt.getBoolean("S")) tileEntity.close();
	}

	public double lastFragment = 0;
	public int lastIFragment = 0;

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		ElementTransitionReactor reactor = tileEntity.getReactorCore();
		int ifCheck = MathHelper.floor(tileEntity.getInstableFragment() * 10);
		if (reactor.getFragment() != lastFragment || ifCheck != lastIFragment) {
			lastFragment = reactor.getFragment();
			lastIFragment = ifCheck;
			this.sendToClient(tileEntity.getUpdateTagForUpdateToClient(), player);
		}

	}
}

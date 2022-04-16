package yuzunyannn.elementalsorcery.tile.altar;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.element.Element;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.batch.EffectFragmentMove;
import yuzunyannn.elementalsorcery.tile.TileEntityNetwork;
import yuzunyannn.elementalsorcery.util.element.ElementTransitionReactor;
import yuzunyannn.elementalsorcery.util.helper.Color;

public class TileElementReactor extends TileEntityNetwork implements ITickable {

	protected ElementTransitionReactor reactorHelper = new ElementTransitionReactor();
	protected ReactorStatus status = ReactorStatus.STANDBY;

	public static enum ReactorStatus {
		OFF,
		STANDBY,
		ON;
	}

	public ElementTransitionReactor getReactorCore() {
		return reactorHelper;
	}

	public ReactorStatus getStatus() {
		return status;
	}

	@SideOnly(Side.CLIENT)
	public void setStatus(ReactorStatus status) {
		this.status = status;
	}

	/**
	 * 启动反应堆
	 * 
	 * @return 启动状态
	 */
	public boolean launch() {
		if (world.isRemote) return false;
		if (status == ReactorStatus.ON) return true;
		if (status != ReactorStatus.STANDBY) return false;
		status = ReactorStatus.ON;
		reactorHelper.reset();
		updateToClient();
		markDirty();
		return true;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		NBTTagCompound nbt = super.writeToNBT(compound);
		nbt.setByte("status", (byte) status.ordinal());
		return reactorHelper.writeToNBT(nbt);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		status = ReactorStatus.values()[compound.getByte("status")];
		reactorHelper.readFromNBT(compound);
	}

	@SideOnly(Side.CLIENT)
	protected Color renderColor;

	@SideOnly(Side.CLIENT)
	public Color getRenderColor() {
		if (renderColor != null) return renderColor;
		return renderColor = new Color(0x4d2175);
	}

	// 是否被渲染了
	@SideOnly(Side.CLIENT)
	public boolean isInRender;

	@Override
	public void update() {

		if (world.isRemote) {
			updateClient();
			return;
		}

		reactorHelper.insert(ESInit.ELEMENTS.WATER, 1);
		Element toElement = reactorHelper.lastTransitionSuggest;
		if (toElement != null && toElement != reactorHelper.getElement()) reactorHelper.transitTo(toElement);
	}

	@SideOnly(Side.CLIENT)
	public void updateClient() {
		if (!isInRender) return;
		isInRender = false;
		
		ReactorStatus status = getStatus();
		if (status != ReactorStatus.ON) return;

		if (reactorHelper.getElement() == ElementStack.EMPTY.getElement()) return;

		Vec3d at = new Vec3d(pos).add(0.5, 0.5, 0.5);
		EffectFragmentMove effect = new EffectFragmentMove(world, at);
		effect.prevScale = effect.scale = effect.defaultScale = 0.01f + Effect.rand.nextFloat() * 0.02f;
		effect.color.setColor(getRenderColor());
		Vec3d move = new Vec3d(Effect.rand.nextDouble() - 0.5, Effect.rand.nextDouble() - 0.5,
				Effect.rand.nextDouble() - 0.5).scale(0.07);
		effect.motionX = move.x;
		effect.motionY = move.y;
		effect.motionZ = move.z;
		move = move.scale(0.05);
		effect.xAccelerate = -move.x;
		effect.yAccelerate = -move.y;
		effect.zAccelerate = -move.z;
		Effect.addEffect(effect);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void handleUpdateTag(NBTTagCompound tag) {
		super.handleUpdateTag(tag);
		Element element = reactorHelper.getElement();
		if (element == ElementStack.EMPTY.getElement()) renderColor = null;
		else renderColor = new Color(element.getColor(new ElementStack(element)));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return super.getRenderBoundingBox();
	}
}

package yuzunyannn.elementalsorcery.tile.device;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.tile.IAltarWake;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.capability.ElementInventory;
import yuzunyannn.elementalsorcery.computer.DeviceInfoTile;
import yuzunyannn.elementalsorcery.logics.EventClient;
import yuzunyannn.elementalsorcery.logics.EventServer;
import yuzunyannn.elementalsorcery.tile.altar.TileElementCube;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;
import yuzunyannn.elementalsorcery.util.element.ElementInventoryMonitor;
import yuzunyannn.elementalsorcery.util.element.ElementInventoryRemoteExchange;
import yuzunyannn.elementalsorcery.util.helper.Color;
import yuzunyannn.elementalsorcery.util.helper.INBTReader;
import yuzunyannn.elementalsorcery.util.helper.INBTWriter;
import yuzunyannn.elementalsorcery.util.helper.NBTSender;
import yuzunyannn.elementalsorcery.util.math.MathSupporter;

public class TileElementTerminal extends TileDevice implements IAltarWake {

	protected ElementInventoryMonitor eim = new ElementInventoryMonitor();
	protected ElementInventoryRemoteExchange inventory;

	public TileElementTerminal() {
		DeviceInfoTile info = (DeviceInfoTile) device.getInfo();
		info.setIcon(new ItemStack(ESObjects.BLOCKS.ELEMENT_TERMINAL));
		ElementInventoryRemoteExchange eInv = new ElementInventoryRemoteExchange(3) {
			@Override
			protected void onUpdateCall() {
				dyUpdate();
			}

			@Override
			protected void onElementBehavier(ElementStack eStack) {
				TileElementTerminal.this.onElementBehavier(eStack);
			}
		};
		inventory = ElementInventory.sensor(eInv, ElementInventoryMonitor.sensor(this, () -> checkElementInventoryStatusChange()));
	}

	@Override
	public void writeSaveData(INBTWriter writer) {
		super.writeSaveData(writer);
		writer.write("eInv", inventory);
	}

	@Override
	public void readSaveData(INBTReader reader) {
		super.readSaveData(reader);
		inventory = reader.obj("eInv", inventory);
	}

	@Override
	public void writeUpdateData(INBTWriter writer) {
		super.writeUpdateData(writer);
		eim.checkChange(inventory);
		writer.writeStream("eInv", buff -> inventory.serializeBuff(buff));
	}

	@Override
	public void readUpdateData(INBTReader reader) {
		super.readUpdateData(reader);
		inventory = reader.sobj("eInv", buff -> {
			inventory.deserializeBuff(buff);
			return inventory;
		});
	}

	@Override
	public void recvUpdateData(INBTReader reader) {
		super.recvUpdateData(reader);
		if (reader.has("ept")) inventory.clear();
		if (reader.has("eInv")) inventory = reader.sobj("eInv", buff -> {
			inventory.deserializeBuff(buff);
			return inventory;
		});
		if (reader.has("hr")) hasRemote = reader.nboolean("hr");
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (ElementInventory.ELEMENTINVENTORY_CAPABILITY == capability) return true;
		return super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (ElementInventory.ELEMENTINVENTORY_CAPABILITY == capability) return (T) inventory;
		return super.getCapability(capability, facing);
	}

	@DeviceFeature(id = "storage-back")
	public boolean transferBack() {
		boolean hasBack = inventory.transferBack();
		if (process.isLogEnabled()) {
			hasBack = false;
			if (inventory.isEmpty()) {
				hasBack = true;
				process.log("now container is empty");
			} else if (!hasBack) {
				if (inventory.getRemote() == null) process.log("unable to find remote core");
				else process.log("cannot back elements to core, maybe core is full");
			} else process.log("still has elements in container");
		}
		return hasBack;
	}

	protected void linkRemote() {
		// TODO
	}

	protected int synUpdateTick;
	protected int dyUpdateTick;
	protected boolean hasRemote;

	public void checkElementInventoryStatusChange() {
		if (world.isRemote) return;
		synUpdateTick = EventServer.tick;

		switch (eim.checkChange(inventory.getCache())) {
		case 0: {
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setBoolean("ept", true);
			this.updateToClient(nbt);
			break;
		}
		case 1:
		case 2: {
			NBTSender.SHARE.writeStream("eInv", buff -> inventory.serializeBuff(buff));
			this.updateToClient(NBTSender.SHARE.spitOut());
			break;
		}
		default:
			break;
		}

	}

	public void dyUpdate() {
		if (world.isRemote) return;

		dyUpdateTick = EventServer.tick;
		int dtick = dyUpdateTick - synUpdateTick;
		if (dtick < 10) return;

		if (!hasRemote) {
			IElementInventory remote = inventory.getRemote();
			if (remote != null) {
				hasRemote = true;
				NBTSender.SHARE.write("hr", hasRemote);
			} else {
				linkRemote();
				if (hasRemote) NBTSender.SHARE.write("hr", hasRemote);
			}
		} else {
			IElementInventory remote = inventory.getRemote();
			if (remote == null) {
				hasRemote = false;
				NBTSender.SHARE.write("hr", hasRemote);
			}
		}

		if (dtick > 20) checkElementInventoryStatusChange();

		if (!NBTSender.SHARE.isEmpty()) this.updateToClient(NBTSender.SHARE.spitOut());
	}

	@Override
	public void updateByBlock() {
		super.updateByBlock();
		if (EventServer.tick - dyUpdateTick > 20 * 5) {
			dyUpdateTick = EventServer.tick;
			if (inventory.transferBack()) checkElementInventoryStatusChange();
		}
	}

	protected void onElementBehavier(ElementStack eStack) {
		if (world.isRemote) onElementColorChange(eStack);
	}

	public int animatick = 0;
	public int wake = 0;

	@SideOnly(Side.CLIENT)
	public float wakeRate;

	@SideOnly(Side.CLIENT)
	public float prevWakeRate;

	@SideOnly(Side.CLIENT)
	public float chaosRate;

	@SideOnly(Side.CLIENT)
	public float prevChaosRate;

	@SideOnly(Side.CLIENT)
	public Color color;

	@SideOnly(Side.CLIENT)
	public float colorTick;

	@SideOnly(Side.CLIENT)
	public Color toColor;

	@SideOnly(Side.CLIENT)
	public float toColorRate;

	@SideOnly(Side.CLIENT)
	public void updateAnimation() {
		int dtick = EventClient.tickRender - animatick;
		animatick = EventClient.tickRender;
		if (dtick == 0) return;
		this.prevWakeRate = this.wakeRate;
		this.prevChaosRate = this.chaosRate;

		if (wake > 0) {
			final float n = (float) MathSupporter.calculateArithmeticGeometricTerm(this.wakeRate, dtick + 1, 0.005f, 1.05f);
			this.wakeRate = Math.min(n, 1);
			wake -= dtick;
		} else {
			this.wakeRate = Math.max(this.wakeRate - 0.01f * dtick, 0);
		}

		if (hasRemote) this.chaosRate = Math.max(this.wakeRate - 0.1f, 0);
		else {
			final float n = (float) MathSupporter.calculateArithmeticGeometricTerm(this.chaosRate, dtick + 1, 0.005f, 1.05f);
			this.chaosRate = Math.min(n, 10);
		}

		if (toColor != null) {
			if (color == null) {
				color = toColor;
				toColor = null;
				colorTick = 0;
			} else {
				toColorRate = Math.min(toColorRate + 0.05f * dtick, 1);
				color = color.weight(toColor, toColorRate);
				if (toColorRate >= 1) toColor = null;
			}
		}

		this.colorTick += 0.02f;
	}

	public Color getColor() {
		return color;
	}

	public void setToColor(Color toColor) {
		this.toColor = toColor;
		this.toColorRate = 0;
	}

	@SideOnly(Side.CLIENT)
	public void onElementColorChange(ElementStack eStack) {
		if (toColor != null) return;
		setToColor(new Color(eStack.getColor()));
	}

	@Override
	public boolean wake(int type, @Nullable BlockPos from) {
		if (!world.isRemote) return true;
		this.wake = 80;
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateEffect(World world, int type, ElementStack estack, Vec3d pos) {
		Vec3d myPos = new Vec3d(this.pos).add(0.5, 0.25 + 0.5, 0.5);
		if (type == IAltarWake.SEND) TileElementCube.giveParticleElementTo(world, estack.getColor(), myPos, pos, 1);
		else TileElementCube.giveParticleElementTo(world, estack.getColor(), pos, myPos, 1);
	}

	@Override
	protected void doDestruct() {
		ElementHelper.onElementFreeFromVoid(world, pos, inventory, null);
		super.doDestruct();
	}
}

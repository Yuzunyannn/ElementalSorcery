package yuzunyannn.elementalsorcery.tile.device;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.IGetItemStack;
import yuzunyannn.elementalsorcery.api.computer.DeviceFilePath;
import yuzunyannn.elementalsorcery.api.computer.soft.IDeviceFile;
import yuzunyannn.elementalsorcery.computer.DeviceInfoTile;
import yuzunyannn.elementalsorcery.computer.files.ConstDeviceFile;
import yuzunyannn.elementalsorcery.computer.files.ItemDeviceFile;
import yuzunyannn.elementalsorcery.computer.files.LogicDeviceFolder;
import yuzunyannn.elementalsorcery.util.helper.INBTReader;
import yuzunyannn.elementalsorcery.util.helper.INBTWriter;
import yuzunyannn.elementalsorcery.util.helper.NBTSender;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

public class TileRingReader extends TileDevice implements IGetItemStack {

	protected ItemStackHandler inventory = new ItemStackHandler(1) {
		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
			if (!canSetStack(stack)) return stack;
			return super.insertItem(slot, stack, simulate);
		};
	};
	protected EnumFacing facing = EnumFacing.NORTH;
	protected boolean coverOpen;
	protected LogicDeviceFolder folder;
	protected long ioLastTs;

	public TileRingReader() {
		DeviceInfoTile info = (DeviceInfoTile) device.getInfo();
		info.setManufacturer(TextFormatting.OBFUSCATED + "mantragic");
		info.setIcon(new ItemStack(ESObjects.BLOCKS.RING_READER));
		folder = new LogicDeviceFolder(DeviceFilePath.of());
		folder.set("device.info", path -> new ConstDeviceFile(path, info.serializeNBT()));
		folder.set("input.dat", path -> getStackFile(path));
	}

	@DeviceFeature(id = "io")
	public IDeviceFile io(DeviceFilePath path) {
		if (!world.isRemote) {
			long curr = System.currentTimeMillis();
			int dropTick = (int) (30 + Math.random() * 40);
			if (curr - ioLastTs > dropTick * 50 && coverOpen) {
				NBTSender sender = new NBTSender();
				sender.write("iod", dropTick);
				this.updateToClient(sender.tag());
				world.scheduleUpdate(pos, getBlockType(), dropTick);
			}
			ioLastTs = curr;
		}
		return IDeviceFile.route(folder, path);
	}

	public IDeviceFile getStackFile(DeviceFilePath path) {
		return ItemDeviceFile.getFile(path, () -> getStack());
	}

	@Override
	public void updateByBlock() {
		super.updateByBlock();
		if (coverOpen && !getStack().isEmpty()) {
			ItemHelper.dropItem(world, new Vec3d(pos).add(0.5, 0.4, 0.5), getStack());
			setStack(ItemStack.EMPTY);
		}
	}

	@Override
	public void writeSaveData(INBTWriter writer) {
		super.writeSaveData(writer);
		writer.write("facing", facing);
		writer.write("inv", inventory);
		writer.write("coverOpen", coverOpen);
	}

	@Override
	public void readSaveData(INBTReader reader) {
		super.readSaveData(reader);
		facing = reader.facing("facing");
		inventory = reader.obj("inv", inventory);
		coverOpen = reader.nboolean("coverOpen");
	}

	@Override
	public void writeUpdateData(INBTWriter writer) {
		super.writeUpdateData(writer);
		writer.write("facing", facing);
		writer.write("itm", inventory.getStackInSlot(0));
		writer.write("ocr", coverOpen);
	}

	@Override
	public void readUpdateData(INBTReader reader) {
		super.readUpdateData(reader);
		facing = reader.facing("facing");
		inventory.setStackInSlot(0, reader.itemStack("itm"));
		coverOpen = reader.nboolean("ocr");
		if (world != null && world.isRemote) resetAnimation();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void recvUpdateData(INBTReader reader) {
		super.recvUpdateData(reader);
		if (reader.has("itm")) {
			inventory.setStackInSlot(0, reader.itemStack("itm"));
			this.prevReadRotation = this.readRotation = 0;
		}
		if (reader.has("ocr")) coverOpen = reader.nboolean("ocr");
		if (reader.has("iod")) readRotationTick = reader.nint("iod");
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.equals(capability) && (facing == null || facing == EnumFacing.UP))
			return true;
		return super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.equals(capability) && (facing == null || facing == EnumFacing.UP))
			return (T) inventory;
		return super.getCapability(capability, facing);
	}

	@Override
	public void setStack(ItemStack stack) {
		ItemStack oldStack = inventory.getStackInSlot(0);
		inventory.setStackInSlot(0, stack);
		if (world.isRemote) return;
		this.markDirty();
		if (!ItemStack.areItemsEqual(stack, oldStack)) {
			NBTSender sender = new NBTSender();
			sender.write("itm", inventory.getStackInSlot(0));
			this.updateToClient(sender.tag());
		}
	}

	@Override
	public ItemStack getStack() {
		return inventory.getStackInSlot(0);
	}

	@Override
	public boolean canSetStack(ItemStack stack) {
		if (Block.getBlockFromItem(stack.getItem()) != Blocks.AIR) return false;
		return true;
	}

	public void setCoverOpen(boolean openCover) {
		if (this.coverOpen == openCover) return;
		this.coverOpen = openCover;
		if (world.isRemote) return;
		this.markDirty();
		NBTSender sender = new NBTSender();
		sender.write("ocr", this.coverOpen);
		this.updateToClient(sender.tag());
	}

	@Override
	public void setPlaceFacing(EnumFacing facing) {
		this.facing = facing;
	}

	public EnumFacing getFacing() {
		return facing;
	}

	public boolean isOpenCover() {
		return coverOpen;
	}

	@SideOnly(Side.CLIENT)
	public float openProgress;

	@SideOnly(Side.CLIENT)
	public float prevOpenProgress;

	@SideOnly(Side.CLIENT)
	public float readRotation;

	@SideOnly(Side.CLIENT)
	public float readRotationAcce;

	@SideOnly(Side.CLIENT)
	public float prevReadRotation;

	@SideOnly(Side.CLIENT)
	protected int readRotationTick;

	@SideOnly(Side.CLIENT)
	public void updateAnimation() {
		int to = this.coverOpen ? 1 : 0;
		this.prevOpenProgress = this.openProgress;
		this.prevReadRotation = this.readRotation;
		this.openProgress = (to - this.openProgress) * 0.2f + this.openProgress;
		if (this.readRotationTick > 0) {
			this.readRotationTick--;
			this.readRotationAcce = Math.min(120, this.readRotationAcce * 1.2f + 0.1f);
		} else this.readRotationAcce = (0 - this.readRotationAcce) * 0.05f + this.readRotationAcce;
		this.readRotation += this.readRotationAcce;
	}

	@SideOnly(Side.CLIENT)
	public void resetAnimation() {
		prevOpenProgress = openProgress = this.coverOpen ? 1 : 0;
	}

}

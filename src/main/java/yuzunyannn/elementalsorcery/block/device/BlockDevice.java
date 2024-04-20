package yuzunyannn.elementalsorcery.block.device;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import yuzunyannn.elementalsorcery.api.computer.IDevice;
import yuzunyannn.elementalsorcery.api.computer.IDeviceInitializable;
import yuzunyannn.elementalsorcery.api.computer.IDeviceLinker;
import yuzunyannn.elementalsorcery.block.container.BlockContainerNormal;
import yuzunyannn.elementalsorcery.computer.Computer;
import yuzunyannn.elementalsorcery.util.helper.NBTSaver;

public abstract class BlockDevice extends BlockContainerNormal {

	protected BlockDevice(Material materialIn) {
		super(materialIn);
	}

	protected BlockDevice(Material materialIn, String unlocalizedName, float hardness, MapColor color) {
		super(materialIn, unlocalizedName, hardness, color);
	}

	@Override
	public void writeTileDataToItemStack(IBlockAccess world, BlockPos pos, EntityLivingBase user, TileEntity tile,
			ItemStack stack) {
		super.writeTileDataToItemStack(world, pos, user, tile, stack);
		IDevice device = tile.getCapability(Computer.DEVICE_CAPABILITY, null);
		if (device != null) {
			NBTSaver saver = new NBTSaver(stack.getOrCreateSubCompound("=d="));
			saver.write("name", device.getInfo().getName());
			saver.write("udid", device.getUDID());
			List<UUID> uuidList = new LinkedList<>();
			Collection<IDeviceLinker> linkers = device.getNetwork().getLinkers();
			for (IDeviceLinker linker : linkers) {
				if (linker.isLocal()) continue;
				uuidList.add(linker.getRemoteUUID());
			}
			saver.writeUUIDs("linkers", uuidList);
		}
	}

	@Override
	public void readTileDataFromItemStack(IBlockAccess world, BlockPos pos, EntityLivingBase user, TileEntity tile,
			ItemStack stack) {
		super.readTileDataFromItemStack(world, pos, user, tile, stack);
		IDevice device = tile.getCapability(Computer.DEVICE_CAPABILITY, null);
		if (device instanceof IDeviceInitializable) {
			NBTSaver saver = new NBTSaver(stack.getOrCreateSubCompound("=d="));
			IDeviceInitializable.Init init = new IDeviceInitializable.Init();
			init.udid = saver.uuid("udid");
			init.name = saver.string("name");
			init.linkers = saver.uuids("linkers");
			((IDeviceInitializable) device).init(init);
		}
	}

}

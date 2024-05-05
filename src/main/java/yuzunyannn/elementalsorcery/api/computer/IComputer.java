package yuzunyannn.elementalsorcery.api.computer;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import yuzunyannn.elementalsorcery.api.computer.soft.IComputerException;
import yuzunyannn.elementalsorcery.api.computer.soft.IOS;
import yuzunyannn.elementalsorcery.api.util.detecter.ISyncDetectable;
import yuzunyannn.elementalsorcery.computer.Disk;

public interface IComputer extends ISyncDetectable<NBTTagCompound>, INBTSerializable<NBTTagCompound>, ICalculatorObject,
		IDeviceNoticeable {

	public IComputerException getException();
	
	public String getAppearance();

	@Nonnull
	public IDevice device();

	@Nullable
	public IComputEnv getEnv();

	@Nullable
	public IOS getSystem();

	@Nonnull
	public List<IDisk> getDisks();

	public void addDisk(Disk disk);

	public IDisk removeDisk(int index);

	public void powerOn();

	public void powerOff();

	public boolean isPowerOn();

	public void update();

	public void onPlayerInteraction(EntityPlayer player, IComputEnv env);

	public void recvMessage(NBTTagCompound nbt, IComputEnv env);

	public void notice(IComputEnv env, String method, DNRequest params);

	public void markDiskValueDirty();

	public void addListener(IDeviceListener listener);

}

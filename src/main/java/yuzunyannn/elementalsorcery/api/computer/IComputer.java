package yuzunyannn.elementalsorcery.api.computer;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import yuzunyannn.elementalsorcery.api.computer.soft.IOS;
import yuzunyannn.elementalsorcery.api.util.detecter.ISyncDetectable;

public interface IComputer extends IDevice, ISyncDetectable<NBTTagCompound>, INBTSerializable<NBTTagCompound> {

	@Nullable
	public IOS getSystem();

	public String getAppearance();

	public void powerOn(IComputEnv env);

	public void powerOff(IComputEnv env);

	public boolean isPowerOn();

	public void update(IComputEnv env);

	public void onPlayerInteraction(EntityPlayer player, IComputEnv env);

	public void recvMessage(NBTTagCompound nbt, IComputEnv env);

	public void notice(IComputEnv env, String method, DNParams params);

	public void markDiskValueDirty();

	public void addListener(IDeviceListener listener);

}

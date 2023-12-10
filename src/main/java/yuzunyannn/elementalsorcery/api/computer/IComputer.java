package yuzunyannn.elementalsorcery.api.computer;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import yuzunyannn.elementalsorcery.api.computer.soft.IOS;

public interface IComputer extends IDevice, INBTSerializable<NBTTagCompound> {

	public IOS getSystem();

	public String getAppearance();

	public void powerOn(IComputEnv env);

	public void powerOff(IComputEnv env);

	public boolean isPowerOn();

	public void update(IComputEnv env);

	public void onPlayerInteraction(EntityPlayer player, IComputEnv env);

	@Nullable
	public NBTTagCompound detectChanges(IComputerWatcher watcher, IComputEnv env);

	public void detectChangesAndSend(IComputerWatcher watcher, IComputEnv env);

	public void recvMessage(NBTTagCompound nbt, IComputEnv env);

}

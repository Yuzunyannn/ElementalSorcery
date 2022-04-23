package yuzunyannn.elementalsorcery.api.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.util.IWorldObject;

public interface IPlatformTickable {

	/**
	 * @param runData 运行时数据，自行取用
	 * @return true 表示需要更新stack状态到client
	 */
	boolean platformUpdate(World world, ItemStack stack, IWorldObject caster, NBTTagCompound runData, int tick);

}

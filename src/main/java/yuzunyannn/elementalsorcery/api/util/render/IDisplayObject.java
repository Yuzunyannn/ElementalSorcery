package yuzunyannn.elementalsorcery.api.util.render;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IDisplayObject extends INBTSerializable<NBTTagCompound> {

	String getId();

	@SideOnly(Side.CLIENT)
	void update(IDisplayMaster master);

	@SideOnly(Side.CLIENT)
	void doRender(float partialTicks);

	@SideOnly(Side.CLIENT)
	Vec3d getSize();
}

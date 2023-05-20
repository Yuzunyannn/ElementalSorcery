package yuzunyannn.elementalsorcery.summon;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Summon implements INBTSerializable<NBTTagCompound> {

	final public World world;
	final public BlockPos pos;

	public Summon(World world, BlockPos pos) {
		this.world = world;
		this.pos = pos;
	}

	public void initSummoner(EntityLivingBase summoner) {

	}

	/**
	 * 召唤更新
	 * 
	 * @return 返回false表示结束
	 */
	public boolean update() {
		return false;
	}

	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		return nbt;
	}

	public void readFromNBT(NBTTagCompound nbt) {

	}

	@Override
	public NBTTagCompound serializeNBT() {
		return writeToNBT(new NBTTagCompound());
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		this.readFromNBT(nbt);
	}

	/** 正常结束后，渲染保留的持续时间 */
	@SideOnly(Side.CLIENT)
	public int getRenderEndTick() {
		return 0;
	}

	@SideOnly(Side.CLIENT)
	public void updateRender(int endTick) {

	}

	@SideOnly(Side.CLIENT)
	public void doRender(Minecraft mc, float partialTicks) {
	}

}

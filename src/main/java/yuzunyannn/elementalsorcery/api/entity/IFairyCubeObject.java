package yuzunyannn.elementalsorcery.api.entity;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.target.IWorldObject;

public interface IFairyCubeObject extends IWorldObject, IHasMaster {

	@Nonnull
	@Override
	Entity asEntity();

	/**
	 * 发送的数据将会通过{@see FairyCubeModule#onRecv(NBTTagCompound)}来接受
	 */
	void sendModuleDataToClient(FairyCubeModule fairyCubeModule, NBTTagCompound nbt);

	/**
	 * 传入属性的值，获取加成后的最终属性
	 */
	double getAttribute(String string, double attribute);

	/**
	 * 获取某种属性的基础值
	 */
	double getAttributeDefault(String name);

	default public double getAttribute(String name) {
		return getAttribute(name, getAttributeDefault(name));
	}

	/**
	 * 是否存在某种属性
	 */
	boolean hasAttribute(String string);

	/** 获取cube的等级 */
	int getCubeLevel();

	/** 为cube追加经验 */
	void addExp(double count);

	/** 获取到下个等级需要的经验 */
	double getExpToNextLevel();

	/** 获取cube的主人 */
	EntityLivingBase getMaster();

	/**
	 * 开始执行
	 * 
	 * @param duration 持续多久<br/>
	 *                 如果成功首先调用{@see FairyCubeModule#onStartExecute(EntityLivingBase)}<br/>
	 *                 接下来的持续期间将会调用{@see FairyCubeModule#onExecute(EntityLivingBase,
	 *                 int)}<br/>
	 *                 如果失败调用{@see FairyCubeModule#onFailExecute()}
	 */
	void doExecute(int duration);

	/** 停止执行 */
	void stopExecute();

	/** 是否正在执行 */
	boolean isExecuting();

	@SideOnly(Side.CLIENT)
	void doClientSwingArm(int duration, int[] colors);

	@SideOnly(Side.CLIENT)
	void doClientCastingEffect(List<BlockPos> list, int[] colors);

	@SideOnly(Side.CLIENT)
	void doClientCastingEffect(Entity entity, int[] colors);

	@SideOnly(Side.CLIENT)
	void doClientAttackEffect(Entity entity, int[] colors);

	void addAbsorbColor(int color);

	public void setLookAt(Vec3d pos);

	default public void setLookAt(BlockPos pos) {
		this.setLookAt(new Vec3d(pos).add(0.5, 0.5, 0.5));
	}

	default public void setLookAt(Entity entity) {
		this.setLookAt(entity.getPositionEyes(1));
	}

}

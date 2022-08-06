package yuzunyannn.elementalsorcery.api.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;
import yuzunyannn.elementalsorcery.api.util.IWorldObject;

public interface IWindmillBladeController extends IWorldObject {

	EntityLivingBase getMaster();

	int getTick();

	/**
	 * 移动到某个位置上
	 * 
	 * @param remainTick 持续的时长
	 */
	void shoot(Vec3d to, int remainTick);

	/**
	 * 获取当前的剩余时间
	 */
	int getRemainTick();

	/**
	 * 停止，結束，及remainTick = 0 回收风轮
	 */
	void stop();

	/**
	 * @return 获取等级
	 */
	int getAmplifier();

}

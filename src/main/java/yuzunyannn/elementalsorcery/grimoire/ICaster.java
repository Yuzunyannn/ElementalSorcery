package yuzunyannn.elementalsorcery.grimoire;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.grimoire.mantra.Mantra;

public interface ICaster {

	/** 调用者希望将一个数据发送到客户端，由{@link Mantra#recvData} 接受 */
	public void sendToClient(NBTTagCompound nbt);

	/** 停止释放 */
	public void stopCaster();

	/**
	 * 获取某些元素
	 * 
	 * @param need    需要的元素
	 * @param consume 是否取出后要实际消耗，true是要，false是不需要
	 * @return 实际取出来的，如果取出来的不为空，则一定强于need
	 */
	public ElementStack iWantSomeElement(@Nonnull ElementStack need, boolean consume);

	/** 获取积攒tick */
	public int iWantKnowCastTick();

	/**
	 * 申请获取一个落脚点，通常是玩家看到方块，但不见得一定是，
	 * 
	 * @return 落脚点下方不见得有方块！但返回的位置一定是air，找不到返回null
	 */
	@Nullable
	public BlockPos iWantFoothold();

	/**
	 * 申请获取目标方块，通常是看到的
	 */
	@Nonnull
	public WantedTargetResult iWantBlockTarget();

	/**
	 * 申请获取目标实体
	 * 
	 * @return 返回值为非null，肯定是RayTraceResult.Type.ENTITY
	 */
	@Nonnull
	public <T extends Entity> WantedTargetResult iWantLivingTarget(Class<T> cls);

	/** 申请获取一个方向 */
	@Nonnull
	public Vec3d iWantDirection();

	/** 获取施法者 ，施法者在某些情况下不一定是entitylivingbase */
	@Nonnull
	public ICasterObject iWantCaster();

	/** 获取直接施法者 ，直接释放者通常是马甲实体 */
	@Nonnull
	public ICasterObject iWantDirectCaster();

	/** 是否有特效标记，用于动画检测 */
	@SideOnly(Side.CLIENT)
	public boolean hasEffectFlags(MantraEffectFlags flag);

}

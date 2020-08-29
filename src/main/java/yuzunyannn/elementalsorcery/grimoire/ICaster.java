package yuzunyannn.elementalsorcery.grimoire;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import yuzunyannn.elementalsorcery.element.ElementStack;

public interface ICaster {

	/** 调用者希望将一个数据发送到客户端 */
	public void sendToClient(NBTTagCompound nbt);

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

	/** 获取施法者 ，施法者在某些情况下不一定是entitylivingbase */
	public Entity iWantCaster();

	/** 获取直接施法者 ，直接释放者通常是马甲实体 */
	public Entity iWantDirectCaster();

}

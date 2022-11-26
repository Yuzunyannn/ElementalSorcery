package yuzunyannn.elementalsorcery.api.mantra;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import yuzunyannn.elementalsorcery.api.element.Element;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.util.IWorldObject;
import yuzunyannn.elementalsorcery.api.util.WorldTarget;

public interface ICaster {

	/** 调用者希望将一个数据发送到客户端，由{@link Mantra#recvData} 接受 */
	public void sendToClient(NBTTagCompound nbt);

	/** 获取当前示范状态 */
	public CastStatus getCastStatus();

	/** 停止释放 */
	public void stopCaster();

	/**
	 * 随机或者顺寻获取任何一个元素
	 *
	 * @param seed 种子，继承ICaster的对象可以考虑使用
	 * 
	 * @return 只是元素的样本，及可以被消耗的元素，必须是新的副本，并不会真正的消耗元素，消耗元素的时候，依然使用iWantSomeElement进行获取
	 * 
	 */
	@Nonnull
	public ElementStack iWantAnyElementSample(int seed);

	/**
	 * 获取某些元素
	 * 
	 * @param need    需要的元素
	 * @param consume 是否取出后要实际消耗，true是要，false是不需要
	 * @return 实际取出来的，如果取出来的不为空，则一定强于need
	 */
	@Nonnull
	public ElementStack iWantSomeElement(@Nonnull ElementStack need, boolean consume);

	/**
	 * 给予某些元素
	 * 
	 * @param give   给予的元素
	 * @param accept 是否实真正的接收，true是cast会真正接受，false是只进行模拟
	 * @return 没收下的内容
	 */
	@Nonnull
	public ElementStack iWantGiveSomeElement(@Nonnull ElementStack give, boolean accept);

	/** 获取积攒tick */
	public int iWantKnowCastTick();

	/**
	 * 花费多少点的强效值
	 * 
	 * @param point 花费的点数
	 * @parma justTry 仅仅尝试获取返回值，并不真正消耗point
	 * 
	 * @return 默认在0-1之前，通常情况下不会超过1，超过表示更强
	 */
	public float iWantBePotent(float point, boolean justTry);

	/**
	 * 给予caster强效点数
	 * 
	 * @potent 强效值
	 * @point 强效点数
	 * 
	 */
	default public void iWantGivePotent(float potent, float point) {

	}

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
	public WorldTarget iWantBlockTarget();

	/**
	 * 申请获取目标实体
	 * 
	 * @return 返回值为非null，肯定是RayTraceResult.Type.ENTITY
	 */
	@Nonnull
	public <T extends Entity> WorldTarget iWantEntityTarget(Class<T> cls);

	/** 申请获取一个方向 */
	@Nonnull
	public Vec3d iWantDirection();

	/** 申请一个元素伤害 */
	@Nonnull
	public DamageSource iWantDamageSource(@Nonnull Element element);

	/** 获取真是施法者，大部分情况下和iWantCaster一样 */
	@Nonnull
	default public IWorldObject iWantRealCaster() {
		return this.iWantRealCaster();
	}

	/** 获取施法者 ，施法者在某些情况下不一定是entitylivingbase */
	@Nonnull
	public IWorldObject iWantCaster();

	/** 获取直接施法者 ，直接释放者是马甲实体，或直接使用FakePlayer(最好不要用，因为实体的生命意味着某些动画是否结束) */
	@Nonnull
	public ICasterObject iWantDirectCaster();

	/** 正在运行的咒文 */
	@Nonnull
	public Mantra iWantMantra();

	/** 正在运行的咒文数据 */
	@Nullable
	public IMantraData iWantMantraData();

	/**
	 * 获取状态
	 * 
	 * @param flagType 描述符{@link MantraCasterFlags}
	 * 
	 * @return 任何内容，通常是boolean
	 */
	default public Object getCasterFlag(int flagType) {
		return null;
	}

}

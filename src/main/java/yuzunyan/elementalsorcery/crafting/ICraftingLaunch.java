package yuzunyan.elementalsorcery.crafting;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyan.elementalsorcery.render.entity.AnimeRenderCrafting;

public interface ICraftingLaunch {

	/** 是否正在工作 */
	boolean isWorking();

	/**
	 * 制作开始[服务器]
	 * 
	 * @param type
	 *            这次进行的类型
	 * @param player
	 *            进行操作的玩家，可以为null
	 * @return ture表示正式开始，false表示条件不足无法开始
	 */
	boolean craftingBegin(CraftingType type, @Nullable EntityPlayer player);

	/**
	 * 制作恢复 恢复调用[服务器][客户端]（服务器存档恢复时调用，客户端创建的时候一定会调用，因为是从服务器传来的消息）
	 * @param nbt 之前提交内容的nbt
	 * @return 恢复的提交内容
	 */
	ICraftingCommit recovery(CraftingType type, @Nullable EntityPlayer player, NBTTagCompound nbt);

	/** 制作更新[服务器] */
	void craftingUpdate(ICraftingCommit commit);

	/** 客户端的更新[客户端] */
	default void craftingUpdateClient(ICraftingCommit commit) {

	}

	/** 是否要继续 */
	boolean canContinue();

	public static final int FAIL = -1;
	public static final int SUCCESS = 0;

	/**
	 * 制作结束，修改物品状态[服务器]
	 * 
	 * @param list
	 *            提交的物品
	 * @return flags -1失败0成功
	 */
	int craftingEnd(ICraftingCommit commit);

	/**
	 * 提交物品 [服务器]
	 */
	@Nullable
	ICraftingCommit commitItems();

	/** 检查类型是否可以完成 */
	boolean checkType(CraftingType type);

	public static enum CraftingType {
		ELEMENT_CRAFTING, ELEMENT_DECONSTRUCT
	}

	/** 获取完成后，结束时间 */
	default int getEndingTime() {
		return 20;
	}

	/**重写的话，请一定要加上@SideOnly*/
	@SideOnly(Side.CLIENT)
	default ICraftingLaunchAnime getAnime() {
		return new AnimeRenderCrafting();
	}
}

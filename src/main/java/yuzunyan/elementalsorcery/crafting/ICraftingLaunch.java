package yuzunyan.elementalsorcery.crafting;

import java.util.List;

import javax.annotation.Nullable;

import ibxm.Player;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
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
	 * 制作恢复 从村当中恢复调用[服务器][客户端]
	 */
	void craftingRecovery(CraftingType type, @Nullable EntityPlayer player);

	/** 制作更新[服务器] */
	void craftingUpdate();

	/** 客户端的更新[客户端] */
	default void craftingUpdateClient() {

	}

	/** 是否要继续 */
	boolean canContinue();

	public static final int FAIL = -1;
	public static final int SUCCESS = 0;

	/**
	 * 制作结束，修改物品状态
	 * 
	 * @param list
	 *            提交的物品
	 * @return flags -1失败0成功
	 */
	int craftingEnd(List<ItemStack> list);

	/** 提交物品 [服务器][客户端] */
	@Nullable
	List<ItemStack> commitItems();

	/** 检查类型是否可以完成 */
	boolean checkType(CraftingType type);

	public static enum CraftingType {
		ELEMENT_CRAFTING, ELEMENT_DECONSTRUCT
	}

	/** 获取完成后，结束时间 */
	default int getEndingTime() {
		return 20;
	}

	@SideOnly(Side.CLIENT)
	default ICraftingLaunchAnime getAnime() {
		return new AnimeRenderCrafting();
	}
}

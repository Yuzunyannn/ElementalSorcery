package yuzunyannn.elementalsorcery.api.mantra;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.element.ElementTransitionReactor;
import yuzunyannn.elementalsorcery.api.util.WorldLocation;
import yuzunyannn.elementalsorcery.api.util.var.VariableSet;

public interface IFragmentMantraLauncher {

	static public String toId(Mantra mantra, IFragmentMantraLauncher launcher) {
		List<IFragmentMantraLauncher> list = mantra.getFragmentMantraLaunchers();
		if (list == null || list.isEmpty()) return null;
		if (list.indexOf(launcher) == -1) return null;
		return mantra.getRegistryName() + "$" + launcher.getId();
	}

	static public class MLPair {
		final public Mantra mantra;
		final public IFragmentMantraLauncher launcher;

		public MLPair(Mantra mantra, IFragmentMantraLauncher launcher) {
			this.mantra = mantra;
			this.launcher = launcher;
		}

		public String toId() {
			return IFragmentMantraLauncher.toId(mantra, launcher);
		}
	}

	static public MLPair fromId(String id) {
		int i = id.indexOf("$");
		if (i == -1) return null;
		String mantraId = id.substring(0, i);
		String launcherId = id.substring(i + 1);
		Mantra mantra = Mantra.REGISTRY.getValue(new ResourceLocation(mantraId));
		if (mantra == null) return null;
		List<IFragmentMantraLauncher> list = mantra.getFragmentMantraLaunchers();
		if (list == null || list.isEmpty()) return null;
		for (IFragmentMantraLauncher launcher : list) {
			if (launcher.getId().equals(launcherId)) return new MLPair(mantra, launcher);
		}
		return null;
	}

	/** 获取唯一id */
	@Nonnull
	default public String getId() {
		return this.getClass().getSimpleName();
	}

	/**
	 * 判断是否可以使用
	 */
	public boolean canUse(ElementTransitionReactor core);

	/***
	 * 是否需要持续反应，释放咒文时候的反应，只会吸入少量的元素
	 */
	default public boolean needContinueReact(World world, ElementTransitionReactor core, VariableSet content) {
		return true;
	}

	/**
	 * 进行充能操作
	 * 
	 * @return 返回進度 获取当前充能进度 >100% 即可以使用
	 */
	public float charging(World world, ElementTransitionReactor core, VariableSet content);

	/**
	 * 获取可以强制暂停的充能百分比
	 */
	public float getMinCanCastCharge(World world, ElementTransitionReactor core, VariableSet content);

	/**
	 * 释放 <br/>
	 * 只有 Server 调用
	 * 
	 * @param world   释放者所在的世界
	 * @param pos     释放者的位置
	 * @param to      目标的位置，包括维度信息的位置信息，可能和world不在同一个维度
	 * @param content 施法上下文
	 * 
	 */
	public void cast(World world, BlockPos pos, WorldLocation to, VariableSet content);

	/** 释放 Client 目标位置 */
	@SideOnly(Side.CLIENT)
	default public void castClientTo(World world, BlockPos to) {
	}

	/** 释放 Client 释放位置 */
	@SideOnly(Side.CLIENT)
	default public void castClientFrom(World world, BlockPos from) {
	}

	/** 画对应的技能icon */
	@SideOnly(Side.CLIENT)
	public void renderIcon(float suggestSize, float alpha, float partialTicks);

}

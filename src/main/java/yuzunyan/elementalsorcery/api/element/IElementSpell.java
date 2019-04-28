package yuzunyan.elementalsorcery.api.element;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyan.elementalsorcery.capability.Spellbook;

public interface IElementSpell {

	/** 释放失败 */
	static public int FAIL = 0;
	/** 释放成功，但不会持续施法，仅仅endspell */
	static public int SPELL_ONCE = 1;
	/** 持续施法 */
	static public int SPELLING = 3;
	/** 需要方块目标 */
	static public int NEED_BLOCK = 4;
	/** 需要实体目标 */
	static public int NEED_ENTITY = 8;

	public static class SpellPackage {
		/** 释放者的书 */
		public Spellbook book;
		/** 元素书的等级 */
		public int level = 1;
		/** 积攒的能量，spellBegin时候无效，spellEnd时大于0表示释放成功 */
		public int power = 0;
		/** 选择的实体 */
		public Entity entity = null;
		/** 选择的位置 */
		public BlockPos pos = null;
		/** 选择位置时的坐标 */
		public EnumFacing face = null;

		/** 测试是否失败 */
		public boolean isFail() {
			return power < 0;
		}
	}

	/**
	 * 开始释放
	 * 
	 * @return 释放要求
	 */
	public int spellBegin(World world, EntityLivingBase entity, ElementStack estack, SpellPackage pack);

	/**
	 * 开始释放
	 * 
	 * @param pack
	 *            释放时获取的数据信息
	 */
	public void spelling(World world, EntityLivingBase entity, ElementStack estack, SpellPackage pack);

	/**
	 * 结束释放
	 * 
	 * @param pack
	 *            释放时获取的数据信息
	 */
	public void spellEnd(World world, EntityLivingBase entity, ElementStack estack, SpellPackage pack);

	/**
	 * 释放的前摇时间
	 * 
	 * @return 需要的前摇时间
	 */
	public int cast(ElementStack estack, int level);

	/**
	 * 释放消耗的数量
	 * 
	 * @param level
	 *            书的等级
	 * 
	 * @return 需要消耗的量
	 */
	public int cost(ElementStack estack, int level);

	/**
	 * 释放消耗时候，元素的最低能量
	 * 
	 * @param level
	 *            书的等级
	 * 
	 * @return 所需的最低能量
	 */
	public int lowestPower(ElementStack estack, int level);

	/**
	 * 添加非本地化介绍
	 */
	@SideOnly(Side.CLIENT)
	default public void addInfo(ElementStack estack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn,
			int level) {
		tooltip.add(I18n.format("info.unknow"));
	}
}

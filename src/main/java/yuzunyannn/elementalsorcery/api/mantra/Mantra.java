package yuzunyannn.elementalsorcery.api.mantra;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistryEntry;
import yuzunyannn.elementalsorcery.api.util.ESImplRegister;
import yuzunyannn.elementalsorcery.api.util.client.ESResources;
import yuzunyannn.elementalsorcery.api.util.client.RenderFriend;
import yuzunyannn.elementalsorcery.api.util.client.TextureBinder;

public class Mantra extends IForgeRegistryEntry.Impl<Mantra> {

	public static final ESImplRegister<Mantra> REGISTRY = new ESImplRegister(Mantra.class);

	private String unlocalizedName;
	private byte rarity = 0;
	private short occupation = 2;
	private List<IFragmentMantraLauncher> fmLaunchers;

	/** 是否可以开始 */
	public boolean canStart(EntityLivingBase user) {
		return true;
	}

	/**
	 * 获取mantra的动态数据 *
	 * 
	 * @param metaData 记录的nbt原始数据，第一次初始化时传入时有真是数据，存档回复传入时为null，需要在IMantraData中记录
	 * 
	 */
	public IMantraData getData(NBTTagCompound metaData, World world, ICaster caster) {
		return null;
	}

	/**
	 * 开始释放，通常只只是在开始调用一次
	 * 
	 * @data {@link Mantra#getData}中获取的内容，如果返回null此时传入的data也为null，用于记录咒文的动态数据
	 * 
	 * @caster 释放者，通过该接口可以获得相关的数据，但具有特异性
	 */
	public void startSpelling(World world, IMantraData data, ICaster caster) {

	}

	/** 释放中，及魔法书打开状态下持续调用，该函数中可以获取到元素作为能量 */
	public void onSpelling(World world, IMantraData data, ICaster caster) {

	}

	/**
	 * 结束释放，通常只在onSpelling结束时调用一次
	 */
	public void endSpelling(World world, IMantraData data, ICaster caster) {

	}

	/**
	 * 结束施法后继续调用，进行后续处理
	 * 
	 * @return 返回表明是否继续，true则继续调用结束状态，false则真正结束
	 */
	public boolean afterSpelling(World world, IMantraData data, ICaster caster) {
		return false;
	}

	/**
	 * 收到发送的数据，<br/>
	 * 调用{@link ICaster#sendToClient(NBTTagCompound)}}后发送数据
	 */
	public void recvData(World world, IMantraData data, ICaster caster, NBTTagCompound recvData) {

	}

	/**
	 * 在区块重新加载的时候，存在找不到使用者的情况，不过位于{@link Mantra#afterSpelling}中状态的咒文可能不需要使用者
	 * 
	 * @return 在afterSpelling的时候是否强制需要使用者，true的情况下，如果不能回复使用者，咒文直接结束
	 */
	public boolean mustUser() {
		return false;
	}

	/** 是否可以进行强效攻击 */
	public boolean canPotentAttack(World world, ItemStack grimoire, ICaster caster, Entity target) {
		return false;
	}

	/** 强效攻击，用魔典攻击敌人时调用 */
	public void potentAttack(World world, ItemStack grimoire, ICaster caster, Entity target) {
	}

	/**
	 * 获取远程法术处理器，元素反应堆使用
	 * 
	 * @return 返回这个咒文支持的所有全程启动魔法，返回null表示不支持，顺序铭感，用于储存
	 */
	@Nullable
	public List<IFragmentMantraLauncher> getFragmentMantraLaunchers() {
		return fmLaunchers;
	}

	public void addFragmentMantraLauncher(IFragmentMantraLauncher launcher) {
		if (fmLaunchers == null) fmLaunchers = new ArrayList<>();
		fmLaunchers.add(launcher);
	}

	/** 获取咒文的稀有度，值越小，越稀有。小于等于0表示不存在，不能通用掉落获取 */
	public int getRarity(@Nullable World world, @Nullable BlockPos pos) {
		return rarity;
	}

	/** 值越小，越稀有 */
	public void setRarity(int rarity) {
		this.rarity = (byte) rarity;
	}

	/** 获取占容量消耗 */
	public int getOccupation() {
		return occupation;
	}

	public void setOccupation(int occupation) {
		this.occupation = (short) occupation;
	}

	public String getTranslationKey() {
		return "mantra." + this.unlocalizedName;
	}

	public Mantra setTranslationKey(String unlocalizedName) {
		this.unlocalizedName = unlocalizedName;
		return this;
	}

	public ITextComponent getTextComponent() {
		ITextComponent itextcomponent = new TextComponentString("[");
		itextcomponent.appendSibling(new TextComponentTranslation(this.getTranslationKey() + ".name"));
		itextcomponent.appendSibling(new TextComponentString("]"));
		return itextcomponent;
	}

	@SideOnly(Side.CLIENT)
	public String getDisplayName() {
		return I18n.format(this.getTranslationKey() + ".name");
	}

	@Nullable
	static public Mantra getFromNBT(NBTTagCompound nbt) {
		if (nbt == null) return null;
		return Mantra.REGISTRY.getValue(new ResourceLocation(nbt.getString("id")));
	}

	/** 检测是否为自定义魔法的咒文 */
	static public boolean isCustomMantra(Mantra m) {
		return false;
	}

	/**
	 * 渲染前的颜色设置
	 * 
	 * @param mData 动态时候会传入
	 */
	public int getColor(@Nullable IMantraData mData) {
		return 0xcac5e0;
	}

	/** 渲染该咒文切换时的图标 */
	@SideOnly(Side.CLIENT)
	public void renderShiftIcon(NBTTagCompound mantraData, float suggestSize, float suggestAlpha, float partialTicks) {
		ESResources.MANTRA_COMMON_CIRCLE.bind();
		RenderFriend.drawTextureRectInCenter(0, 0, suggestSize, suggestSize);
		ResourceLocation res = this.getIconResource();
		if (res == null) res = ESResources.MANTRA_VOID.getResource();
		TextureBinder.bindTexture(res);
		suggestSize = suggestSize * 0.5f;
		RenderFriend.drawTextureRectInCenter(0, 0, suggestSize, suggestSize);
	}

	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		String describe = getTranslationKey() + ".describe";
		if (I18n.hasKey(describe)) tooltip.add(I18n.format(describe));
	}

	@Nonnull
	@SideOnly(Side.CLIENT)
	public ResourceLocation getIconResource() {
		return ESResources.MANTRA_VOID.getResource();
	}
	
	@Override
	public String toString() {
		return this.getRegistryName().toString();
	}

}

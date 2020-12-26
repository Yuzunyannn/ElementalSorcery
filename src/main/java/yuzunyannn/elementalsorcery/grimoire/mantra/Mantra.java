package yuzunyannn.elementalsorcery.grimoire.mantra;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistryEntry;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.container.gui.GuiMantraShitf;
import yuzunyannn.elementalsorcery.grimoire.ICaster;
import yuzunyannn.elementalsorcery.grimoire.IMantraData;
import yuzunyannn.elementalsorcery.init.ESImplRegister;
import yuzunyannn.elementalsorcery.util.render.RenderHelper;
import yuzunyannn.elementalsorcery.util.render.RenderObjects;

public class Mantra extends IForgeRegistryEntry.Impl<Mantra> {

	public static final ESImplRegister<Mantra> REGISTRY = new ESImplRegister(Mantra.class);

	private String unlocalizedName;
	private byte rarity = 100;

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

	/** 获取咒文的稀有度，值越小，越稀有。小于0表示不存在 */
	public int getRarity(@Nullable World world, @Nullable BlockPos pos) {
		return rarity;
	}

	/** 值越小，越稀有 */
	public void setRarity(int rarity) {
		this.rarity = (byte) rarity;
	}

	public String getUnlocalizedName() {
		return "mantra." + this.unlocalizedName;
	}

	public Mantra setUnlocalizedName(String unlocalizedName) {
		this.unlocalizedName = unlocalizedName;
		return this;
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
	@SideOnly(Side.CLIENT)
	public int getRenderColor(@Nullable IMantraData mData) {
		return 0xcac5e0;
	}

	/** 渲染该咒文切换时的图标 */
	@SideOnly(Side.CLIENT)
	public void renderShiftIcon(Minecraft mc, NBTTagCompound mantraData, float suggestSize, float suggestAlpha,
			float partialTicks) {
		mc.getTextureManager().bindTexture(GuiMantraShitf.CIRCLE);
		RenderHelper.drawTexturedRectInCenter(0, 0, suggestSize, suggestSize);
		ResourceLocation res = this.getIconResource();
		mc.getTextureManager().bindTexture(res);
		suggestSize = suggestSize * 0.5f;
		RenderHelper.drawTexturedRectInCenter(0, 0, suggestSize, suggestSize);
	}

	@SideOnly(Side.CLIENT)
	@Nonnull
	public ResourceLocation getIconResource() {
		return RenderObjects.MANTRA_VOID;
	}

	public static void registerAll() {
		reg(new MantraEnderTeleport(), "ender_teleport");
		reg(new MantraFloat(), "float");
		reg(new MantraSprint(), "sprint");
		reg(MantraFireBall.instance, "fire_ball");
		reg(new MantraLush(), "lush");
		reg(new MantraBlockCrash(), "block_crash");
		reg(new MantraMiningArea(), "mining_area");
		reg(new MantraLightningArea(), "lightning_area");
		reg(MantraSummon.instance, "summon");
	}

	private static void reg(Mantra m, String name) {
		Mantra.REGISTRY.register(m.setRegistryName(new ResourceLocation(ElementalSorcery.MODID, name)));
	}

}

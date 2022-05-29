package yuzunyannn.elementalsorcery.grimoire.mantra;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.api.util.IWorldObject;
import yuzunyannn.elementalsorcery.api.util.WorldObjectEntity;
import yuzunyannn.elementalsorcery.element.Element;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.entity.EntityGrimoire;
import yuzunyannn.elementalsorcery.grimoire.ICaster;
import yuzunyannn.elementalsorcery.grimoire.IMantraData;
import yuzunyannn.elementalsorcery.grimoire.MantraDataCommon;
import yuzunyannn.elementalsorcery.grimoire.MantraEffectFlags;
import yuzunyannn.elementalsorcery.grimoire.remote.FMantraElementDirectLaunch;
import yuzunyannn.elementalsorcery.item.ItemAncientPaper;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.batch.EffectElementMove;
import yuzunyannn.elementalsorcery.render.effect.batch.EffectSpiralMove;
import yuzunyannn.elementalsorcery.render.effect.grimoire.EffectLookAt;
import yuzunyannn.elementalsorcery.render.effect.grimoire.EffectMagicCircle;
import yuzunyannn.elementalsorcery.render.effect.grimoire.EffectMagicCircleIcon;
import yuzunyannn.elementalsorcery.render.effect.grimoire.EffectMagicEmit;
import yuzunyannn.elementalsorcery.util.VariableSet;
import yuzunyannn.elementalsorcery.util.VariableSet.Variable;
import yuzunyannn.elementalsorcery.util.helper.Color;
import yuzunyannn.elementalsorcery.util.helper.DamageHelper;

public class MantraCommon extends Mantra {

	public static final Variable<Vec3d> VEC = new Variable<>("vec", VariableSet.VEC3D);
	public static final Variable<Vec3d> TOWARD = new Variable<>("toward", VariableSet.VEC3D);
	public static final Variable<BlockPos> POS = new Variable<>("pos", VariableSet.BLOCK_POS);
	public static final Variable<Short> LAYER = new Variable<>("layer", VariableSet.SHORT);
	public static final Variable<Integer> SIZE = new Variable<>("size", VariableSet.INT);
	public static final Variable<Integer> POWERI = new Variable<>("power", VariableSet.INT);
	public static final Variable<Float> POWERF = new Variable<>("power", VariableSet.FLOAT);
	public static final Variable<Float> POTENT_POWER = new Variable<>("potentPower", VariableSet.FLOAT);
	public static final Variable<ElementStack> ELEMENT = new Variable<>("eStack", VariableSet.ELEMENT);

	protected int color = 0;
	protected ResourceLocation icon;

	public void setColor(int color) {
		this.color = color;
	}

	public static void fireMantra(World world, Mantra mantra, @Nullable Entity caster, VariableSet params) {
		if (mantra instanceof MantraCommon) {
			MantraCommon common = (MantraCommon) mantra;
			Vec3d vec = null;
			if (params.has(VEC)) vec = params.get(VEC);
			else if (params.has(POS)) vec = new Vec3d(params.get(POS));
			else if (caster != null) vec = caster.getPositionVector();
			common.directLaunchMantra(world, vec, caster != null ? new WorldObjectEntity(caster) : null, params, null);
		}
	}

	public Entity directLaunchMantra(World world, Vec3d vec, @Nullable IWorldObject caster, VariableSet params,
			NBTTagCompound meta) {
		EntityGrimoire grimoire = new EntityGrimoire(world, caster == null ? null : caster.asEntityLivingBase(), this,
				meta, EntityGrimoire.STATE_AFTER_SPELLING);
		IMantraData mantraData = grimoire.getMantraData();
		try {
			MantraDataCommon mc = (MantraDataCommon) mantraData;
			mc.setExtra(params);
		} catch (Exception e) {
			return null;
		}
		if (vec != null) grimoire.setPosition(vec.x, vec.y, vec.z);
		this.initDirectLaunchMantraGrimoire(grimoire, params);
		world.spawnEntity(grimoire);
		return grimoire;
	}

	protected void initDirectLaunchMantraGrimoire(EntityGrimoire grimoire, VariableSet params) {

	}

	public void setDirectLaunchFragmentMantraLauncher(ElementStack element, double mult, double chargeSpeedRatio,
			BiFunction<Double, VariableSet, VariableSet> callback) {
		List<ElementStack> list = new ArrayList<>(1);
		list.add(element);
		setDirectLaunchFragmentMantraLauncher(list, mult, chargeSpeedRatio, callback);
	}

	public void setDirectLaunchFragmentMantraLauncher(Collection<ElementStack> elements, double mult,
			double chargeSpeedRatio, BiFunction<Double, VariableSet, VariableSet> callback) {
		FMantraElementDirectLaunch fmb = new FMantraElementDirectLaunch(this, elements, mult);
		fmb.parmasGenerator = callback;
		fmb.setIconRes(getIconResource());
		fmb.setChargeSpeedRatio((float) chargeSpeedRatio);
		this.addFragmentMantraLauncher(fmb);
	}

	@Override
	public IMantraData getData(NBTTagCompound origin, World world, ICaster caster) {
		return new MantraDataCommon();
	}

	@Override
	public int getColor(@Nullable IMantraData data) {
		return this.color;
	}

	@Override
	public void onSpelling(World world, IMantraData data, ICaster caster) {
		MantraDataCommon mdc = (MantraDataCommon) data;
		this.onCollectElement(world, data, caster, caster.iWantKnowCastTick() + mdc.speedTick);
		IWorldObject co = caster.iWantCaster();
		// 创造模式20倍加速！
		int times = 0;
		if (co.isCreative()) times = 20;
		else {
			// 强效加速
			if (mdc.getProgress() < 1) {
				float potent = caster.iWantBePotent(0.04f, false);
				times = (int) (potent * 10);
			}
		}
		// 加速
		for (int i = 0; i < times; i++)
			this.onCollectElement(world, data, caster, caster.iWantKnowCastTick() + ++mdc.speedTick);
		if (world.isRemote) {
			if (mdc.isMarkContinue()) onSpellingEffect(world, data, caster);
		}
	}

	/** 尝试收集元素，增加进度，该函数可能在一个tick内调用多次，通过speedTick来进行区分 */
	public void onCollectElement(World world, IMantraData data, ICaster caster, int speedTick) {

	}

	@Override
	public boolean canPotentAttack(World world, ItemStack grimoire, ICaster caster, Entity target) {
		EntityLivingBase player = caster.iWantCaster().asEntityLivingBase();
		if (player == null) return false;
		if (!target.canBeAttackedWithItem()) return false;
		if (target.hitByEntity(player)) return false;
		if (target instanceof EntityPlayer && ((EntityPlayer) target).isCreative()) return false;
		float potent = caster.iWantBePotent(0.2f, true);
		return potent > 0.125f;
	}

	@Override
	public void potentAttack(World world, ItemStack grimoire, ICaster caster, Entity target) {
		float potent = caster.iWantBePotent(0.2f, false);

		EntityLivingBase player = caster.iWantCaster().asEntityLivingBase();
		doPotentAttackEffect(world, caster, target);

		float damage = DamageHelper.getNormalAttackDamage(player, target);
		if (damage <= 0) return;

		damage = damage * (1 + potent / 2);
		// 造成傷害
		target.attackEntityFrom(DamageHelper.getMagicDamageSource(player, player), damage);
	}

	public void doPotentAttackEffect(World world, ICaster caster, Entity target) {
		EntityLivingBase player = caster.iWantCaster().asEntityLivingBase();
		if (world.isRemote) onPotentAttackEffect(world, caster, target);
		world.playSound((EntityPlayer) null, player.posX, player.posY, player.posZ, SoundEvents.BLOCK_NOTE_CHIME,
				player.getSoundCategory(), 1.0F, 0F);
	}

	@SideOnly(Side.CLIENT)
	private Color potentContextColor;

	@SideOnly(Side.CLIENT)
	public void setPotentContextColor(Color potentContextColor) {
		this.potentContextColor = potentContextColor;
	}

	@SideOnly(Side.CLIENT)
	public void onPotentAttackEffect(World world, ICaster caster, Entity target) {
		Color color = potentContextColor;
		if (color == null) color = new Color(getColor(null));
		else potentContextColor = null;
		Vec3d vec = target.getPositionVector().add(0, target.height / 2, 0);
		Random rand = world.rand;
		for (int i = 0; i < 25; i++) {
			Vec3d at = vec.add(rand.nextGaussian() * 1.25, rand.nextGaussian() * 0.75, rand.nextGaussian() * 1.25);
			EffectElementMove effect = new EffectElementMove(world, at);
			effect.motionY = rand.nextGaussian() * 0.2;
			effect.yDecay = 0.75;
			effect.setColor(color);
			Effect.addEffect(effect);
		}
		for (int i = 0; i < 5; i++) {
			Vec3d at = vec.add(rand.nextGaussian() * 1.25, rand.nextGaussian() * 0.75, rand.nextGaussian() * 1.25);
			EffectSpiralMove effect = new EffectSpiralMove(world, at);
			effect.motionY = rand.nextDouble() * 0.01f + 0.005f;
			effect.yDecay = 0.98f;
			effect.alpha = 0;
			effect.setColor(color);
			Effect.addEffect(effect);
		}
	}

	@SideOnly(Side.CLIENT)
	public void onSpellingEffect(World world, IMantraData data, ICaster caster) {
		addEffectMagicCircle(world, data, caster);
		addEffectProgress(world, data, caster);
	}

	@SideOnly(Side.CLIENT)
	public void addEffectMagicCircle(World world, IMantraData data, ICaster caster) {
		if (hasEffectFlags(world, data, caster, MantraEffectFlags.MAGIC_CIRCLE)) out: {
			IWorldObject co = caster.iWantCaster();
			EntityLivingBase eb = co.asEntityLivingBase();
			if (eb == null) break out;
			MantraDataCommon dataEffect = (MantraDataCommon) data;
			if (!dataEffect.hasMarkEffect(0))
				dataEffect.addConditionEffect(caster, this.getEffectMagicCircle(world, eb, data), 0);
		}
	}

	@SideOnly(Side.CLIENT)
	public void addEffectProgress(World world, IMantraData data, ICaster caster) {
		if (hasEffectFlags(world, data, caster, MantraEffectFlags.PROGRESS)) out: {
			float r = this.getProgressRate(world, data, caster);
			if (r <= 0) break out;
			MantraDataCommon dataEffect = (MantraDataCommon) data;
			dataEffect.showProgress(r, this.getColor(data), world, caster);
		}
	}

	@SideOnly(Side.CLIENT)
	public void addEffectIndicatorEffect(World world, IMantraData data, ICaster caster) {
		if (this.hasEffectFlags(world, data, caster, MantraEffectFlags.INDICATOR)) {
			MantraDataCommon dataEffect = (MantraDataCommon) data;
			if (!caster.iWantCaster().isClientPlayer() || dataEffect.hasMarkEffect(1)) return;
			dataEffect.addConditionEffect(caster, new EffectLookAt(world, caster, this.getColor(dataEffect)), 1);
		}
	}

	@SideOnly(Side.CLIENT)
	public void addEffectEmitEffect(World world, IMantraData data, ICaster caster) {
		if (this.hasEffectFlags(world, data, caster, MantraEffectFlags.DECORATE)) {
			Entity entity = caster.iWantCaster().asEntity();
			if (entity == null) return;
			MantraDataCommon dataEffect = (MantraDataCommon) data;
			if (dataEffect.hasMarkEffect(4)) return;
			EffectMagicEmit emit = new EffectMagicEmit(world, entity);
			emit.setColor(this.getColor(data));
			dataEffect.addConditionEffect(caster, emit, 4);
		}
	}

	@SideOnly(Side.CLIENT)
	public boolean hasEffectFlags(World world, IMantraData data, ICaster caster, MantraEffectFlags flags) {
		return caster.hasEffectFlags(flags);
	}

	@SideOnly(Side.CLIENT)
	public float getProgressRate(World world, IMantraData data, ICaster caster) {
		MantraDataCommon dataEffect = (MantraDataCommon) data;
		return dataEffect.getProgress();
	}

	@SideOnly(Side.CLIENT)
	public EffectMagicCircle getEffectMagicCircle(World world, EntityLivingBase entity, IMantraData mData) {
		EffectMagicCircle emc = new EffectMagicCircleIcon(world, entity, this.getMagicCircleIcon());
		emc.setColor(this.getColor(mData));
		return emc;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ResourceLocation getIconResource() {
		return icon;
	}

	@SideOnly(Side.CLIENT)
	public ResourceLocation getMagicCircleIcon() {
		return this.getIconResource();
	}

	public void setIcon(ResourceLocation icon) {
		this.icon = icon;
	}

	public void setIcon(String name) {
		this.icon = new ResourceLocation(ElementalSorcery.MODID, "textures/mantras/" + name + ".png");
	}

	public ItemAncientPaper.EnumType getMantraSubItemType() {
		return ItemAncientPaper.EnumType.NORMAL;
	}

	public static boolean beforeGeneralStartTime(ICaster caster) {
		return caster.iWantKnowCastTick() < 20;
	}

	public static ElementStack getElement(ICaster caster, Element element, int size, int power) {
		ElementStack need = new ElementStack(element, size, power);
		return caster.iWantSomeElement(need, true);
	}

}

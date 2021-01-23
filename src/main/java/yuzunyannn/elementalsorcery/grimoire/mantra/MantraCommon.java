package yuzunyannn.elementalsorcery.grimoire.mantra;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.grimoire.ICaster;
import yuzunyannn.elementalsorcery.grimoire.IMantraData;
import yuzunyannn.elementalsorcery.grimoire.MantraDataCommon;
import yuzunyannn.elementalsorcery.grimoire.MantraEffectFlags;
import yuzunyannn.elementalsorcery.item.ItemAncientPaper;
import yuzunyannn.elementalsorcery.render.effect.grimoire.EffectMagicCircle;
import yuzunyannn.elementalsorcery.render.effect.grimoire.EffectMagicCircleIcon;
import yuzunyannn.elementalsorcery.util.VariableSet;
import yuzunyannn.elementalsorcery.util.VariableSet.Variable;

public class MantraCommon extends Mantra {

	public static final Variable<BlockPos> POS = new Variable<>("pos", VariableSet.BLOCK_POS);
	public static final Variable<Short> LAYER = new Variable<>("layer", VariableSet.SHORT);
	public static final Variable<Integer> SIZE = new Variable<>("size", VariableSet.INT);
	public static final Variable<ElementStack> ELEMENT_WOOD = new Variable<>("eWood", VariableSet.ELEMENT);

	protected int color = 0;

	public void setColor(int color) {
		this.color = color;
	}

	@Override
	public IMantraData getData(NBTTagCompound origin, World world, ICaster caster) {
		return new MantraDataCommon();
	}

	@SideOnly(Side.CLIENT)
	public int getRenderColor() {
		return color;
	}

	@Override
	public int getColor(IMantraData mData) {
		return this.color;
	}

	@Override
	public void onSpelling(World world, IMantraData data, ICaster caster) {
		MantraDataCommon mdc = (MantraDataCommon) data;
		this.onCollectElement(world, data, caster, caster.iWantKnowCastTick() + mdc.speedTick);
		Entity entity = caster.iWantCaster();
		// 创造模式20倍加速！
		if (entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative()) {
			for (int i = 0; i < 20; i++) {
				this.onCollectElement(world, data, caster, caster.iWantKnowCastTick() + ++mdc.speedTick);
			}
		}
		if (world.isRemote) {
			if (mdc.isMarkContinue()) onSpellingEffect(world, data, caster);
		}
	}

	/** 尝试收集元素，增加进度，该函数可能在一个tick内调用多次，通过speedTick来进行区分 */
	public void onCollectElement(World world, IMantraData data, ICaster caster, int speedTick) {

	}

	@SideOnly(Side.CLIENT)
	public void onSpellingEffect(World world, IMantraData data, ICaster caster) {
		if (hasEffectFlags(world, data, caster, MantraEffectFlags.MAGIC_CIRCLE)) out: {
			Entity entity = caster.iWantCaster();
			if (!(entity instanceof EntityLivingBase)) break out;
			EntityLivingBase eb = (EntityLivingBase) entity;
			MantraDataCommon dataEffect = (MantraDataCommon) data;
			if (!dataEffect.hasMarkEffect(0))
				dataEffect.addEffect(caster, this.getEffectMagicCircle(world, eb, data), 0);
		}
		if (hasEffectFlags(world, data, caster, MantraEffectFlags.PROGRESS)) out: {
			float r = this.getProgressRate(world, data, caster);
			if (r < 0) break out;
			MantraDataCommon dataEffect = (MantraDataCommon) data;
			dataEffect.setProgress(r, this.getColor(data), world, caster);
		}
	}

	@SideOnly(Side.CLIENT)
	public boolean hasEffectFlags(World world, IMantraData data, ICaster caster, MantraEffectFlags flags) {
		return caster.hasEffectFlags(flags);
	}

	@SideOnly(Side.CLIENT)
	public float getProgressRate(World world, IMantraData data, ICaster caster) {
		return -1;
	}

	@SideOnly(Side.CLIENT)
	public EffectMagicCircle getEffectMagicCircle(World world, EntityLivingBase entity, IMantraData mData) {
		EffectMagicCircle emc = new EffectMagicCircleIcon(world, entity, this.getIconResource());
		emc.setColor(this.getColor(mData));
		return emc;
	}

	public ItemAncientPaper.EnumType getMantraSubItemType() {
		return ItemAncientPaper.EnumType.NORMAL;
	}

}

package yuzunyannn.elementalsorcery.grimoire.mantra;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.grimoire.ICaster;
import yuzunyannn.elementalsorcery.grimoire.IMantraData;
import yuzunyannn.elementalsorcery.grimoire.MantraDataCommon;
import yuzunyannn.elementalsorcery.grimoire.MantraEffectFlags;
import yuzunyannn.elementalsorcery.render.effect.grimoire.EffectMagicCircle;
import yuzunyannn.elementalsorcery.render.effect.grimoire.EffectMagicCircleIcon;

public class MantraCommon extends Mantra {

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
	@SideOnly(Side.CLIENT)
	public int getRenderColor(IMantraData mData) {
		return this.getRenderColor();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onSpelling(World world, IMantraData data, ICaster caster) {
		if (world.isRemote) {
			MantraDataCommon dataEffect = (MantraDataCommon) data;
			if (dataEffect.isMarkContinue()) onSpellingEffect(world, data, caster);
		}
	}

	@SideOnly(Side.CLIENT)
	public void onSpellingEffect(World world, IMantraData data, ICaster caster) {
		if (caster.hasEffectFlags(MantraEffectFlags.MAGIC_CIRCLE)) out: {
			Entity entity = caster.iWantCaster();
			if (!(entity instanceof EntityLivingBase)) break out;
			EntityLivingBase eb = (EntityLivingBase) entity;
			MantraDataCommon dataEffect = (MantraDataCommon) data;
			if (!dataEffect.hasMarkEffect(0))
				dataEffect.addEffect(caster, this.getEffectMagicCircle(world, eb, data), 0);
		}
		if (caster.hasEffectFlags(MantraEffectFlags.PROGRESS)) out: {
			float r = this.getProgressRate(world, data, caster);
			if (r < 0) break out;
			MantraDataCommon dataEffect = (MantraDataCommon) data;
			dataEffect.setProgress(r, this.getRenderColor(data), world, caster);
		}
	}

	@SideOnly(Side.CLIENT)
	public float getProgressRate(World world, IMantraData data, ICaster caster) {
		return -1;
	}

	@SideOnly(Side.CLIENT)
	public EffectMagicCircle getEffectMagicCircle(World world, EntityLivingBase entity, IMantraData mData) {
		EffectMagicCircle emc = new EffectMagicCircleIcon(world, entity, this.getIconResource());
		emc.setColor(this.getRenderColor(mData));
		return emc;
	}

}

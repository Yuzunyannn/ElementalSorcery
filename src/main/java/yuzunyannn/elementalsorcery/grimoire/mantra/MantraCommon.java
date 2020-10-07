package yuzunyannn.elementalsorcery.grimoire.mantra;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.element.Element;
import yuzunyannn.elementalsorcery.grimoire.ICaster;
import yuzunyannn.elementalsorcery.grimoire.IMantraData;
import yuzunyannn.elementalsorcery.grimoire.MantraDataCommon;
import yuzunyannn.elementalsorcery.init.ESInitInstance;
import yuzunyannn.elementalsorcery.render.effect.grimoire.EffectElementMagicCircle;

public class MantraCommon extends Mantra {

	@Override
	public IMantraData getData(NBTTagCompound origin, World world, ICaster caster) {
		return new MantraDataCommon();
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
	public boolean onSpellingEffect(World world, IMantraData data, ICaster caster) {
		Entity entity = caster.iWantCaster();
		if (!(entity instanceof EntityLivingBase)) return true;
		EntityLivingBase eb = (EntityLivingBase) entity;
		MantraDataCommon dataEffect = (MantraDataCommon) data;
		if (!dataEffect.hasMarkEffect(0))
			dataEffect.addEffect(caster, new EffectElementMagicCircle(world, eb, this.getMagicCircle()), 0);
		return false;
	}

	@SideOnly(Side.CLIENT)
	public Element getMagicCircle() {
		return ESInitInstance.ELEMENTS.VOID;
	}

}

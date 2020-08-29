package yuzunyannn.elementalsorcery.grimoire.mantra;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.grimoire.ICaster;
import yuzunyannn.elementalsorcery.grimoire.IMantraData;
import yuzunyannn.elementalsorcery.grimoire.Mantra;
import yuzunyannn.elementalsorcery.init.ESInitInstance;
import yuzunyannn.elementalsorcery.render.effect.grimoire.EffectCondition;
import yuzunyannn.elementalsorcery.render.effect.grimoire.EffectElementMagicCircle;
import yuzunyannn.elementalsorcery.util.render.RenderObjects;

public class MantraFloat extends Mantra {

	public MantraFloat() {
		this.setUnlocalizedName("float");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IMantraData getData(NBTTagCompound origin, World world, ICaster caster) {
		return new MantraDataEffect(caster);
	}

	@Override
	public void startSpelling(World world, IMantraData data, ICaster caster) {
		onSpelling(world, data, caster);
	}

	@Override
	public void onSpelling(World world, IMantraData data, ICaster caster) {
		MantraDataEffect dataEffect = (MantraDataEffect) data;
		if (caster.iWantKnowCastTick() % 5 == 0 || !dataEffect.isMarkContinue()) {
			dataEffect.markContinue(false);
			ElementStack need = new ElementStack(ESInitInstance.ELEMENTS.AIR, 1, 20);
			ElementStack get = caster.iWantSomeElement(need, true);
			if (get.isEmpty()) return;
		}
		dataEffect.markContinue(true);
		Entity entity = caster.iWantCaster();
		entity.motionY = 0.15;
		entity.fallDistance = 0;
		if (world.isRemote) onSpellingEffect(world, data, caster);

	}

	@SideOnly(Side.CLIENT)
	public void onSpellingEffect(World world, IMantraData data, ICaster caster) {
		// 一些特效
		MantraDataEffect dataEffect = (MantraDataEffect) data;
		if (!dataEffect.hasMarkEffect(0)) {
			Entity entity = caster.iWantCaster();
			if (entity instanceof EntityLivingBase) {
				EntityLivingBase eb = (EntityLivingBase) entity;
				EffectCondition effect = new EffectElementMagicCircle(world, eb, ESInitInstance.ELEMENTS.AIR);
				effect.setCondition(new EffectCondition.ConditionEntityActionAndCanContinue(eb, dataEffect));
				dataEffect.addEffect(effect, 0);
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ResourceLocation getIconResource() {
		return RenderObjects.MANTRA_FLOAT;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderColor() {
		return 0xacffff;
	}

}

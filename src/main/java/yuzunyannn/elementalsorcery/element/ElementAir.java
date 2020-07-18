package yuzunyannn.elementalsorcery.element;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.element.IElementSpell;
import yuzunyannn.elementalsorcery.render.particle.Effect;
import yuzunyannn.elementalsorcery.render.particle.EffectResonance;

public class ElementAir extends ElementInner {

	public ElementAir() {
		super(rgb(242, 243, 243), "air");
	}

	@SideOnly(Side.CLIENT)
	static public void effect(World world, EntityLivingBase entity) {
		EffectResonance effect = new EffectResonance(world, entity.posX, entity.posY + 0.1, entity.posZ);
		effect.setColor(0xffffff);
		Effect.addEffect(effect);
	}

	@Override
	public int spellBegin(World world, EntityLivingBase entity, ElementStack estack, SpellPackage pack) {
		entity.motionX *= 0.25;
		entity.motionY *= 0.05;
		entity.motionZ *= 0.25;
		entity.fallDistance *= 0.05;
		entity.isAirBorne = true;
		if (world.isRemote) effect(world, entity);
		return IElementSpell.SPELL_ONCE;
	}

	@Override
	public void spelling(World world, EntityLivingBase entity, ElementStack estack, SpellPackage pack) {
	}

	@Override
	public void spellEnd(World world, EntityLivingBase entity, ElementStack estack, SpellPackage pack) {
	}

	@Override
	public int cast(ElementStack estack, int level) {
		return 0;
	}

	@Override
	public int cost(ElementStack estack, int level) {
		return 5;
	}

	@Override
	public int lowestPower(ElementStack estack, int level) {
		return 10;
	}

	@Override
	public void addInfo(ElementStack estack, World worldIn, List<String> tooltip, ITooltipFlag flagIn, int level) {
		tooltip.add(I18n.format("info.element.spell.air"));
	}
}

package yuzunyan.elementalsorcery.element;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import yuzunyan.elementalsorcery.api.element.Element;
import yuzunyan.elementalsorcery.api.element.ElementStack;
import yuzunyan.elementalsorcery.api.element.IElementSpell;

public class ElementAir extends Element implements IElementSpell {

	public ElementAir() {
		super(rgb(242, 243, 243));
		this.setUnlocalizedName("air");
	}

	@Override
	public int spellBegin(World world, EntityLivingBase entity, ElementStack estack, SpellPackage pack) {
		entity.motionX *= 0.25;
		entity.motionY *= 0.05;
		entity.motionZ *= 0.25;
		entity.fallDistance *= 0.05;
		entity.isAirBorne = true;
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

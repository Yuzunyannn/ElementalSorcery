package yuzunyannn.elementalsorcery.element;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.element.IElementSpell;

public class ElementFire extends ElementInner {

	public ElementFire() {
		super(rgb(255, 153, 2), "fire");
	}

	@Override
	public int spellBegin(World world, EntityLivingBase entity, ElementStack estack, SpellPackage pack) {
		return IElementSpell.SPELL_ONCE | IElementSpell.NEED_BLOCK;
	}

	@Override
	public void spelling(World world, EntityLivingBase entity, ElementStack estack, SpellPackage pack) {
	}

	@Override
	public void spellEnd(World world, EntityLivingBase entity, ElementStack estack, SpellPackage pack) {
		if (world.isRemote)
			return;
		if (pack.isFail())
			return;
		if (pack.pos != null) {
			BlockPos pos = pack.pos.up();
			if (world.isAirBlock(pos))
				world.setBlockState(pos, Blocks.FIRE.getDefaultState(), 11);
		}
	}

	@Override
	public int cost(ElementStack estack, int level) {
		return 5;
	}

	@Override
	public int lowestPower(ElementStack estack, int level) {
		return 20;
	}

	@Override
	public int cast(ElementStack estack, int level) {
		return 10;
	}
}

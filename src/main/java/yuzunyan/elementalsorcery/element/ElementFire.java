package yuzunyan.elementalsorcery.element;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyan.elementalsorcery.api.element.Element;
import yuzunyan.elementalsorcery.api.element.ElementStack;
import yuzunyan.elementalsorcery.api.element.IElementSpell;

public class ElementFire extends Element implements IElementSpell {

	public ElementFire() {
		super(rgb(233, 189, 58));
		this.setUnlocalizedName("fire");
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
				world.setBlockState(pos, Blocks.FIRE.getDefaultState());
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

	@Override
	@SideOnly(Side.CLIENT)
	public void addInfo(ElementStack estack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn,
			int level) {
		tooltip.add(I18n.format("info.element.spell.fire"));
	}

}

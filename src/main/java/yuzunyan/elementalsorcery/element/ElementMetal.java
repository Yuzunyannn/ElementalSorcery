package yuzunyan.elementalsorcery.element;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
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

public class ElementMetal extends Element implements IElementSpell {

	public ElementMetal() {
		super(rgb(242, 208, 49));
		this.setUnlocalizedName("metal");
	}

	@Override
	public int spellBegin(World world, EntityLivingBase entity, ElementStack estack, SpellPackage pack) {
		return IElementSpell.SPELL_ONCE | IElementSpell.NEED_BLOCK | IElementSpell.SPELLING;
	}

	@Override
	public void spellEnd(World world, EntityLivingBase entity, ElementStack estack, SpellPackage pack) {
		if (pack.isFail())
			return;
		if (pack.pos == null)
			return;
		int count = pack.power + 5;
		IBlockState state = world.getBlockState(pack.pos);
		if (this.canChangeLv1(state)) {
			if (world.isRemote) {

			} else
				this.changeLv1(world, pack.pos, count / 2, estack.getPower());
		}
	}

	@Override
	public int cast(ElementStack estack, int level) {
		return 20 * 5;
	}

	@Override
	public int cost(ElementStack estack, int level) {
		return 50;
	}

	@Override
	public int costSpelling(ElementStack estack, int power, int level) {
		return power % 2 == 0 ? 1 : 0;
	}

	@Override
	public float costSpellingAverage(int level) {
		return 0.5f;
	}

	@Override
	public int lowestPower(ElementStack estack, int level) {
		return 225;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInfo(ElementStack estack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn,
			int level) {
		tooltip.add(I18n.format("info.element.spell.metal"));
	}

	public boolean canChangeLv1(IBlockState state) {
		if (state == Blocks.STONE.getDefaultState())
			return true;
		if (state.getBlock() == Blocks.IRON_ORE)
			return true;
		if (state.getBlock() == Blocks.GOLD_ORE)
			return true;
		if (state.getBlock() == Blocks.REDSTONE_ORE)
			return true;
		if (state.getBlock() == Blocks.DIAMOND_ORE)
			return true;
		if (state.getBlock() == Blocks.COAL_ORE)
			return true;
		return false;
	}

	public void changeLv1(World world, BlockPos pos, int count, int power) {
		world.destroyBlock(pos, false);
		if (Math.random() < 0.5 && count >= 15 && power >= 100) {
			if (Math.random() < 0.5 && count >= 22 && power >= 250) {
				if (Math.random() < 0.5) {
					if (Math.random() < 0.5) {
						if (Math.random() < 0.5) {
							world.setBlockState(pos, Blocks.DIAMOND_ORE.getDefaultState());
						}
					} else {
						world.setBlockState(pos, Blocks.REDSTONE_ORE.getDefaultState());
					}
				} else {
					world.setBlockState(pos, Blocks.GOLD_ORE.getDefaultState());
				}
			} else {
				world.setBlockState(pos, Blocks.IRON_ORE.getDefaultState());
			}
		} else {
			world.setBlockState(pos, Blocks.COAL_ORE.getDefaultState());
		}
	}

}

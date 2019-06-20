package yuzunyan.elementalsorcery.element;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyan.elementalsorcery.api.element.Element;
import yuzunyan.elementalsorcery.api.element.ElementStack;
import yuzunyan.elementalsorcery.api.element.IElementSpell;
import yuzunyan.elementalsorcery.init.ESInitInstance;

public class ElementEarth extends Element implements IElementSpell {

	public ElementEarth() {
		super(rgb(120, 84, 57));
		this.setUnlocalizedName("earth");
	}

	@Override
	public int complexWith(ItemStack stack, ElementStack estack, ElementStack other) {
		if (other.getElement() == ESInitInstance.ELEMENTS.METAL)
			return 10;
		return super.complexWith(stack, estack, other);
	}

	@Override
	public int spellBegin(World world, EntityLivingBase entity, ElementStack estack, SpellPackage pack) {
		return IElementSpell.SPELL_ONCE | IElementSpell.NEED_BLOCK;
	}

	@Override
	public void spellEnd(World world, EntityLivingBase entity, ElementStack estack, SpellPackage pack) {
		if (pack.isFail())
			return;
		if (pack.pos == null)
			return;
		if (world.getTileEntity(pack.pos) != null)
			return;
		if (world.isRemote)
			return;
		IBlockState state = world.getBlockState(pack.pos);
		for (int i = 0; i < 5; i++) {
			BlockPos pos = pack.pos.up(i);
			IBlockState fallingState = world.getBlockState(pos);
			if (!fallingState.equals(state))
				break;
			EntityFallingBlock falling = new EntityFallingBlock(world, pos.getX() + 0.5, pos.getY() + 0.5,
					pos.getZ() + 0.5, fallingState);
			world.spawnEntity(falling);
		}
	}

	@Override
	public int cast(ElementStack estack, int level) {
		return 40;
	}

	@Override
	public int cost(ElementStack estack, int level) {
		return 20;
	}

	@Override
	public int lowestPower(ElementStack estack, int level) {
		return 50;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInfo(ElementStack estack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn,
			int level) {
		tooltip.add(I18n.format("info.element.spell.earth"));
	}
}

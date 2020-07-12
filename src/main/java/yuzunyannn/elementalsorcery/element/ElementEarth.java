package yuzunyannn.elementalsorcery.element;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.element.IElementSpell;
import yuzunyannn.elementalsorcery.init.ESInitInstance;

public class ElementEarth extends ElementInner {

	public ElementEarth() {
		super(0x785439, "earth");
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
}

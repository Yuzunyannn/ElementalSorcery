package yuzunyannn.elementalsorcery.element;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.element.IElementSpell;

public class ElementWood extends ElementInner {

	public ElementWood() {
		super(0x32CD32, "wood");
	}

	@Override
	public int spellBegin(World world, EntityLivingBase entity, ElementStack estack, SpellPackage pack) {
		return IElementSpell.SPELLING;
	}

	@Override
	public void spelling(World world, EntityLivingBase entity, ElementStack estack, SpellPackage pack) {
		if (estack.isEmpty()) return;
		if (pack.power % 15 == 0) {
			if (!world.isRemote) {
				for (int i = 0; i < 2; i++) {
					BlockPos target = entity.getPosition().up();
					target = target.add(world.rand.nextInt(8) - 4, world.rand.nextInt(2) - 2,
							world.rand.nextInt(8) - 4);
					if (entity instanceof EntityPlayer)
						ItemDye.applyBonemeal(new ItemStack(Items.DYE), world, target, (EntityPlayer) entity, null);
					else ItemDye.applyBonemeal(new ItemStack(Items.DYE), world, target);
				}
			} else {
				BlockPos target = entity.getPosition().up();
				target = target.add(world.rand.nextInt(8) - 4, world.rand.nextInt(2) - 2, world.rand.nextInt(8) - 4);
				ItemDye.spawnBonemealParticles(world, target.up(), 0);
			}
		}
	}

	@Override
	public void spellEnd(World world, EntityLivingBase entity, ElementStack estack, SpellPackage pack) {
	}

	@Override
	public int cast(ElementStack estack, int level) {
		return 60;
	}

	@Override
	public int cost(ElementStack estack, int level) {
		return 0;
	}

	@Override
	public int costSpelling(ElementStack estack, int power, int level) {
		return power % 4 == 0 ? 1 : 0;
	}

	@Override
	public float costSpellingAverage(int level) {
		return 0.25f;
	}

	@Override
	public int lowestPower(ElementStack estack, int level) {
		return 35;
	}

}

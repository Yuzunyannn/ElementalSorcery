package yuzunyannn.elementalsorcery.potion;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.util.render.RenderHelper;
import yuzunyannn.elementalsorcery.util.render.RenderObjects;

public class PotionFireWalker extends Potion {

	public PotionFireWalker() {
		super(true, 0xd38409);
		setPotionName("es.effect.fireWalker");
		registerPotionAttributeModifier(SharedMonsterAttributes.MOVEMENT_SPEED, "26ee6c1d-e2b9-44ed-ab64-10de3e407e48",
				0.04, 2);
	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		return Math.random() < 0.05f * (amplifier + 1);
	}

	@Override
	public void performEffect(EntityLivingBase entity, int amplifier) {
		if (entity.world.isRemote) return;
		World world = entity.world;
		BlockPos pos = entity.getPosition();
		if (world.isAirBlock(pos) && !world.getBlockState(pos.down()).getBlock().isReplaceable(world, pos.down()))
			world.setBlockState(pos, Blocks.FIRE.getDefaultState());
		entity.setFire(amplifier * 10 * 20 + 60);
		if (amplifier < 2) return;

		pos = pos.down();
		Random rand = entity.getRNG();
		int range = (int) Math.sqrt(amplifier - 1) + 1;
		IBlockState fire = Blocks.FIRE.getDefaultState();

		for (int i = 0; i < amplifier - 1; i++) {
			BlockPos at = pos.add(rand.nextGaussian() * range, -2, rand.nextGaussian() * range);
			if (!world.isAirBlock(at)) {
				for (int j = 0; j < 5; j++) {
					at = at.up();
					if (world.isAirBlock(at)) {
						world.setBlockState(at, fire);
						break;
					}
				}
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderHUDEffect(PotionEffect effect, Gui gui, int x, int y, float z, float alpha) {
		RenderObjects.EFFECT_BUFF.bind();
		RenderHelper.drawTexturedModalRect(x + 3, y + 3, 18, 0, 18, 18, 128, 128);
	}

}

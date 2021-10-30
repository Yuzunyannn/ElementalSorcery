package yuzunyannn.elementalsorcery.potion;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.EffectShineBlock;
import yuzunyannn.elementalsorcery.util.block.BlockHelper;

public class PotionGoldenEye extends PotionCommon {

	public PotionGoldenEye() {
		super(false, 0xffcd04, "goldenEye");
		iconIndex = 11;
	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		return duration % (20 * 16) == 0;
	}

	@Override
	public void performEffect(EntityLivingBase owner, int amplifier) {
		if (!owner.world.isRemote) return;
		if (Minecraft.getMinecraft().player != owner) return;
		int size = Math.min(4 + amplifier * 2, 16);
		doEffect(owner, size);
	}

	@SideOnly(Side.CLIENT)
	public static void doEffect(Entity entity, int size) {
		BlockPos pos = entity.getPosition();
		World world = entity.world;
		for (int x = -size; x <= size; x++) {
			for (int y = -size; y <= size; y++) {
				for (int z = -size; z <= size; z++) {
					BlockPos at = pos.add(x, y, z);
					IBlockState state = world.getBlockState(at);
					if (state.getBlock().isAir(state, world, at)) continue;
					if (!BlockHelper.isOre(state)) continue;
					shineBlock(world, at, 2, 0xffcd04);
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public static void shineBlock(World world, BlockPos pos, int sec, int color) {
		EffectShineBlock shine = new EffectShineBlock(world, pos, sec);
		shine.setColor(color);
		Effect.addEffect(shine);
	}
}
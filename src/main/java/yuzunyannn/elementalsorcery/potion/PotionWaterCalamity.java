package yuzunyannn.elementalsorcery.potion;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;
import yuzunyannn.elementalsorcery.util.helper.EntityHelper;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class PotionWaterCalamity extends PotionCommon {

	public PotionWaterCalamity() {
		super(true, 0x597cff, "waterCalamity");
		iconIndex = 9;
	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		return duration % 20 == 0;
	}

	@Override
	public void performEffect(EntityLivingBase owner, int amplifier) {
		World world = owner.world;
		if (world.isRemote) return;

		BlockPos pos = owner.getPosition();
		int i = Math.min(4 + amplifier * 2, 15);
		IBlockState waterState = Blocks.FLOWING_WATER.getDefaultState().withProperty(BlockFluidBase.LEVEL, i);
		if (world.provider.doesWaterVaporize())
			waterState = Blocks.FLOWING_LAVA.getDefaultState().withProperty(BlockFluidBase.LEVEL, i);

		boolean isPlayer = owner instanceof EntityPlayer;

		if (isPlayer || BlockHelper.isSolidBlock(world, pos.down())) {
			if (BlockHelper.isReplaceBlock(world, pos)) world.setBlockState(pos, waterState);
			for (EnumFacing facing : EnumFacing.HORIZONTALS) {
				BlockPos at = pos.offset(facing);
				if (BlockHelper.isReplaceBlock(world, at)) world.setBlockState(at, waterState);
			}
		}

		if (world.rand.nextFloat() < 0.5) return;
		if (!isPlayer && world.rand.nextFloat() < 0.8) return;

		AxisAlignedBB aabb = WorldHelper.createAABB(pos, Math.min(6 + amplifier * 2, 16), 2, 3);
		List<EntityLivingBase> list = world.getEntitiesWithinAABB(EntityLivingBase.class, aabb);
		PotionEffect effect = owner.getActivePotionEffect(ESObjects.POTIONS.WATER_CALAMITY);
		for (EntityLivingBase entity : list) {
			if (entity.isPotionActive(ESObjects.POTIONS.WATER_CALAMITY)) continue;
			if (EntityHelper.isCreative(entity)) continue;
			entity.addPotionEffect(new PotionEffect(effect.getPotion(), effect.getDuration(), effect.getAmplifier()));
		}
	}

}

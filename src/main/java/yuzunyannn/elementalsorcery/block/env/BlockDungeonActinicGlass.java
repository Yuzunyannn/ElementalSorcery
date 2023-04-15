package yuzunyannn.elementalsorcery.block.env;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBreakable;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.elf.ElfTime;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.Effects;
import yuzunyannn.elementalsorcery.render.effect.batch.EffectFragmentMove;
import yuzunyannn.elementalsorcery.util.helper.NBTHelper;

public class BlockDungeonActinicGlass extends BlockBreakable {

	public BlockDungeonActinicGlass() {
		super(Material.GLASS, false);
		this.setTranslationKey("dungeonActinicGlass");
		this.setTickRandomly(true);
		this.setHardness(3F);
		this.setResistance(6000000.0F);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.add(TextFormatting.GREEN + I18n.format("info.dungeon.actinicGlass"));
	}

	@Override
	public int quantityDropped(Random random) {
		return 0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	protected boolean canSilkHarvest() {
		return true;
	}

	@Override
	public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		if (worldIn.isRemote) return;
		if (worldIn.rand.nextDouble() > 0.25) return;
		if (worldIn.isRaining()) return;
		ElfTime time = new ElfTime(worldIn);
		if (!time.at(ElfTime.Period.DAY)) return;
		if (!worldIn.canSeeSky(pos)) return;

		int rX = rand.nextInt(7) - 3;
		int rZ = rand.nextInt(7) - 3;
		BlockPos fromPos = pos.add(rX, 0, rZ);
		for (int i = 1; i < 8; i++) {
			BlockPos at = fromPos.down(i);
			IBlockState growState = worldIn.getBlockState(at);
			if (growState.getMaterial() == Material.AIR) continue;
			Block block = growState.getBlock();
			if (block == this) continue;
			if (block instanceof IGrowable) {
				IGrowable growable = (IGrowable) block;
				if (!growable.canUseBonemeal(worldIn, rand, at, growState)) return;
				if (!growable.canGrow(worldIn, at, growState, false)) return;
				growable.grow(worldIn, rand, at, growState);

				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setByte("type", (byte) 5);
				NBTHelper.setVec3d(nbt, "to", new Vec3d(at.getX() + 0.5, at.getY(), at.getZ() + 0.5));
				Effects.spawnEffect(worldIn, Effects.PARTICLE_EFFECT,
						new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5), nbt);
			}
			return;
		}

	}

	@SideOnly(Side.CLIENT)
	public static void doEffect(World world, Vec3d pos, NBTTagCompound nbt) {
		Vec3d vec = NBTHelper.getVec3d(nbt, "to");

		final int colors[] = new int[] { 0x39ba16, 0x47c525, 0x259506 };

		Vec3d tar = vec.subtract(pos);
		Vec3d acc = tar.normalize().scale(0.01);
		double length = tar.length();
		int count = MathHelper.ceil(Math.min(16, length * 2));
		for (int i = 0; i < count; i++) {
			Vec3d at = pos.add(tar.scale(i / (float) count * (1 + Effect.rand.nextGaussian() * 0.1)));
			EffectFragmentMove effect = new EffectFragmentMove(world, at);
			effect.color.setColor(colors[Effect.rand.nextInt(colors.length)]);
			effect.xAccelerate = acc.x;
			effect.yAccelerate = acc.y;
			effect.zAccelerate = acc.z;
			effect.motionX = Effect.rand.nextDouble() * acc.x * 10;
			effect.motionY = Effect.rand.nextDouble() * acc.y * 10;
			effect.motionZ = Effect.rand.nextDouble() * acc.z * 10;
			effect.yDecay = effect.xDecay = effect.zDecay = 0.9;
			Effect.addEffect(effect);
		}
	}
}

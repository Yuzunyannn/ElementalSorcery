package yuzunyannn.elementalsorcery.block.env;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.batch.EffectElementMove;
import yuzunyannn.elementalsorcery.tile.dungeon.TileDungeonMagicCircleA;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;
import yuzunyannn.elementalsorcery.util.helper.Color;

public class BlockDungeonMagicCircleA extends BlockDungeonPropBase {
	public static final AxisAlignedBB BOTTOM_BLOCK_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.01D, 1.0D);

	public BlockDungeonMagicCircleA() {
		super(Material.GLASS);
		this.setTranslationKey("dungeonMagicCircleA");
		this.setHardness(3);
		this.setResistance(6000000.0F);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileDungeonMagicCircleA();
	}

	@Override
	public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos) {
		return false;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return BOTTOM_BLOCK_AABB;
	}

	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox,
			List<AxisAlignedBB> collidingBoxes, Entity entityIn, boolean isActualState) {

	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager) {
		TileDungeonMagicCircleA tile = BlockHelper.getTileEntity(world, pos, TileDungeonMagicCircleA.class);
		Color color = tile != null ? tile.getColor() : new Color(0x803298);
		Vec3d center = new Vec3d(pos).add(0.5, 0.1, 0.5);
		for (int i = 0; i < 8; i++) {
			EffectElementMove effect = new EffectElementMove(world, center);
			effect.color.setColor(color);
			effect.yAccelerate = Effect.rand.nextDouble() * 0.04 + 0.01;
			effect.motionX = Effect.rand.nextGaussian() * 0.1;
			effect.motionZ = Effect.rand.nextGaussian() * 0.1;
			effect.yDecay = 0.6;
			effect.xDecay = effect.zDecay = 0.9;
			Effect.addEffect(effect);
		}
		return true;
	}

}

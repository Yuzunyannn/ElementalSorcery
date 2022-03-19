package yuzunyannn.elementalsorcery.block.container;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.batch.EffectFragmentMove;
import yuzunyannn.elementalsorcery.tile.ir.TileIceRockNode;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;
import yuzunyannn.elementalsorcery.util.helper.Color;

public class BlockIceRockNode extends BlockIceRockSendRecv {

	public BlockIceRockNode() {
		super(Material.GLASS, "iceRockNode", 0, MapColor.LIGHT_BLUE_STAINED_HARDENED_CLAY);
		setLightLevel(0.75f);
		setSoundType(SoundType.SNOW);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileIceRockNode();
	}

	@Override
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {

	}

	@Override
	public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
		return false;
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state,
			int fortune) {
	}

	@Override
	public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager) {
		TileIceRockNode tile = BlockHelper.getTileEntity(world, pos, TileIceRockNode.class);
		Color color = new Color(0x7cd0d3);
		if (tile != null) color.weight(new Color(0x9956d0), tile.stockRatio);
		Vec3d center = new Vec3d(pos).add(0.5, 0.25, 0.5);
		addDestroyEffects(world, center, color);
		return true;
	}

	@SideOnly(Side.CLIENT)
	public static void addDestroyEffects(World world, Vec3d center, Color color) {
		for (int i = 0; i < 32; i++) {
			EffectFragmentMove effect = new EffectFragmentMove(world, center);
			effect.color.setColor(color);
			effect.yAccelerate = Effect.rand.nextDouble() * 0.04 + 0.01;
			effect.motionX = Effect.rand.nextGaussian() * 0.1;
			effect.motionZ = Effect.rand.nextGaussian() * 0.1;
			effect.yDecay = 0.6;
			effect.xDecay = effect.zDecay = 0.9;
			Effect.addEffect(effect);
		}
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		return NULL_AABB;
	}

}

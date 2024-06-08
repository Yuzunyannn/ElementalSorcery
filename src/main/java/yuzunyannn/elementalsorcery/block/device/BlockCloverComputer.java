package yuzunyannn.elementalsorcery.block.device;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.container.ESGuiHandler;
import yuzunyannn.elementalsorcery.model.CheckModelRenderer;
import yuzunyannn.elementalsorcery.render.model.ModelCloverComputer;
import yuzunyannn.elementalsorcery.tile.device.TileCloverComputer;
import yuzunyannn.elementalsorcery.util.math.MathSupporter;

public class BlockCloverComputer extends BlockComputer {

	static public final ModelCloverComputer MODEL_COMPUTER_A = new ModelCloverComputer();

	public BlockCloverComputer() {
		super(Material.ROCK, "cloverComputer", 1.75F, MapColor.QUARTZ);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileCloverComputer();
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	public RayTraceResult collisionRayTrace(IBlockState blockState, World worldIn, BlockPos pos, Vec3d start,
			Vec3d end) {
		TileEntity tile = worldIn.getTileEntity(pos);
		if (tile instanceof TileCloverComputer) {
			EnumFacing facing = ((TileCloverComputer) tile).getFacing();

			Vec3d startVec = start.subtract((double) pos.getX() + 0.5, (double) pos.getY(), (double) pos.getZ() + 0.5);
			Vec3d endVec = end.subtract((double) pos.getX() + 0.5, (double) pos.getY(), (double) pos.getZ() + 0.5);

			float rotation = (-facing.getHorizontalAngle() - 180) / 180 * 3.1415926f;
			startVec = MathSupporter.rotation(startVec, new Vec3d(0, 1, 0), -rotation);
			endVec = MathSupporter.rotation(endVec, new Vec3d(0, 1, 0), -rotation);

			RayTraceResult rayTrace = CheckModelRenderer.rayTraceByModel(MODEL_COMPUTER_A, startVec, endVec, true);
			if (rayTrace != null) {
				int subHit = rayTrace.subHit;
				Vec3d hitVec = MathSupporter.rotation(rayTrace.hitVec, new Vec3d(0, 1, 0), rotation);
				rayTrace = new RayTraceResult(hitVec.add(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5),
						rayTrace.sideHit, pos);
				rayTrace.subHit = subHit;
//				System.out.println("" + rayTrace.subHit);
//				if (worldIn.isRemote) {
//					EffectElementMove move = new EffectElementMove(worldIn,
//							hitVec.add((double) pos.getX() + 0.5, (double) pos.getY(), (double) pos.getZ() + 0.5));
//					move.xDecay = move.yDecay = move.zDecay = 0;
//					move.setColor(0x0000ff);
//					move.isGlow = true;
//					move.prevScale = move.scale = 0.05f;
//					Effect.addEffect(move);
//				}
				rayTrace.hitVec = new Vec3d(pos.getX(), pos.getY() + rayTrace.subHit, pos.getZ());
				return rayTrace;
			}

			return null;
		}

		return super.collisionRayTrace(blockState, worldIn, pos, start, end);
	}

	@Override
	public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
		return super.rotateBlock(world, pos, axis);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		int id = MathHelper.floor(hitY + 0.0001);
		if (id == 2) {
			if (facing == EnumFacing.UP) {
				if (worldIn.isRemote) return true;
				openScreen(worldIn, pos, state, playerIn, hand);
				return true;
			}
		} else if (id == 1) {
			if (facing.getHorizontalIndex() != -1) {
				if (worldIn.isRemote) return true;
				openEditor(worldIn, pos, state, playerIn, hand, facing);
				return true;
			}
		}
		return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
	}

	public void openScreen(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand) {
		playerIn.openGui(ElementalSorcery.instance, ESGuiHandler.GUI_COMPUTER_TILE, worldIn, pos.getX(), pos.getY(), pos.getZ());
	}

	public void openEditor(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand,
			EnumFacing facing) {
		if (facing != EnumFacing.NORTH) return;
		playerIn.openGui(ElementalSorcery.instance, ESGuiHandler.GUI_COMPUTER_EDITOR, worldIn, pos.getX(), pos.getY(), pos.getZ());
	}

}

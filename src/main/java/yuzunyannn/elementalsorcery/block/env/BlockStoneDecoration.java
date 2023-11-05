package yuzunyannn.elementalsorcery.block.env;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.block.IBlockStronger;
import yuzunyannn.elementalsorcery.block.container.BlockContainerNormal;
import yuzunyannn.elementalsorcery.render.model.ModelComputerA;
import yuzunyannn.elementalsorcery.tile.dungeon.TileStoneDecoration;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

public class BlockStoneDecoration extends BlockContainerNormal implements IBlockStronger {

	static public final ModelBase MODEL_COMPUTER_A = new ModelComputerA();

	public BlockStoneDecoration() {
		super(Material.ROCK, "stoneDecoration", 1.75F, MapColor.QUARTZ);
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		int meta = ItemHelper.getOrCreateTagCompound(stack).getInteger("tId");
		String name = EnumDecType.fromMeta(meta).getName();
		return super.getTranslationKey() + "." + name;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		if (!playerIn.isCreative())
			return new ActionResult<ItemStack>(EnumActionResult.PASS, playerIn.getHeldItem(handIn));
		if (!playerIn.isSneaking())
			return new ActionResult<ItemStack>(EnumActionResult.PASS, playerIn.getHeldItem(handIn));
		ItemStack stack = playerIn.getHeldItem(handIn);
		NBTTagCompound nbt = ItemHelper.getOrCreateTagCompound(stack);
		EnumDecType enumType = EnumDecType.fromMeta(nbt.getInteger("tId"));
		EnumDecType enums[] = EnumDecType.values();
		enumType = enums[(enumType.ordinal() + 1) % enums.length];
		nbt.setInteger("tId", enumType.getMeta());
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileStoneDecoration();
	}

	@Override
	public void writeTileDataToItemStack(IBlockAccess world, BlockPos pos, EntityLivingBase user, TileEntity tile,
			ItemStack stack) {
		super.writeTileDataToItemStack(world, pos, user, tile, stack);
		if (tile instanceof TileStoneDecoration) {
			String name = ((TileStoneDecoration) tile).getDecorationType().getName();
			NBTTagCompound nbt = ItemHelper.getOrCreateTagCompound(stack);
			nbt.setString("tId", name);
		}
	}

	@Override
	public void readTileDataFromItemStack(IBlockAccess world, BlockPos pos, EntityLivingBase user, TileEntity tile,
			ItemStack stack) {
		super.readTileDataFromItemStack(world, pos, user, tile, stack);
		if (tile instanceof TileStoneDecoration) {
			NBTTagCompound nbt = ItemHelper.getOrCreateTagCompound(stack);
			((TileStoneDecoration) tile).setDecorationType(nbt.getInteger("tId"));
		}
	}

	@Override
	public void onTileBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer,
			ItemStack stack, TileEntity tile) {
		if (tile instanceof TileStoneDecoration) {
			((TileStoneDecoration) tile).setFacing(placer.getHorizontalFacing().getOpposite());
		}
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state,
			int fortune) {
		super.getDrops(drops, world, pos, state, fortune);
		TileEntity tile = getDropTile(world, pos);
		if (tile instanceof TileStoneDecoration) {
			EnumDecType type = ((TileStoneDecoration) tile).getDecorationType();
			if (type == EnumDecType.ALTERNATOR) {
				int count = 1;
				if (Math.random() > 0.5) count++;
				drops.clear();
				drops.add(new ItemStack(ESObjects.ITEMS.CONTROLLER, count, 4));
			}
		}
	}

//	@Override
//	public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
//		return super.rotateBlock(world, pos, axis);
//	}
	
	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		return BlockFaceShape.UNDEFINED;
	}


//	@Override
//	public RayTraceResult collisionRayTrace(IBlockState blockState, World worldIn, BlockPos pos, Vec3d start,
//			Vec3d end) {
//		if (!ESAPI.isDevelop) return super.collisionRayTrace(blockState, worldIn, pos, start, end);
//
//		RayTraceResult rayTrace = super.collisionRayTrace(blockState, worldIn, pos, start, end);
//		if (rayTrace == null) return null;
//
//		TileEntity tile = worldIn.getTileEntity(pos);
//		if (tile instanceof TileStoneDecoration) {
//			EnumFacing facing = ((TileStoneDecoration) tile).getFacing();
//
//			Vec3d startVec = start.subtract((double) pos.getX() + 0.5, (double) pos.getY(), (double) pos.getZ() + 0.5);
//			Vec3d endVec = end.subtract((double) pos.getX() + 0.5, (double) pos.getY(), (double) pos.getZ() + 0.5);
//
//			float rotation = (-facing.getHorizontalAngle() - 180) / 180 * 3.1415926f;
//			startVec = MathSupporter.rotation(startVec, new Vec3d(0, 1, 0), -rotation);
//			endVec = MathSupporter.rotation(endVec, new Vec3d(0, 1, 0), -rotation);
//
//			RayTraceResult result = CheckModelRenderer.rayTraceByModel(MODEL_COMPUTER_A, startVec, endVec, true);
//			if (result != null) {
//				rayTrace.subHit = result.subHit;
//				rayTrace.hitVec = MathSupporter.rotation(result.hitVec, new Vec3d(0, 1, 0), rotation);
//				rayTrace.sideHit = result.sideHit;
//
////				System.out.println("" + result.subHit);
////				if (worldIn.isRemote) {
////					EffectElementMove move = new EffectElementMove(worldIn, rayTrace.hitVec
////							.add((double) pos.getX() + 0.5, (double) pos.getY(), (double) pos.getZ() + 0.5));
////					move.xDecay = move.yDecay = move.zDecay = 0;
////					move.setColor(0x0000ff);
////					move.isGlow = true;
////					move.prevScale = move.scale = 0.05f;
////					Effect.addEffect(move);
////				}
//			}
//		}
//
//		return rayTrace;
//	}

//	@Override
//	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
//			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
//		return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
//	}

	public static enum EnumDecType {
		ALTERNATOR("alternator"),
		COMPUTER_A("computer"),
		STAR_CPU("starCPU");

		private String name;

		private EnumDecType(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}

		public int getMeta() {
			return this.ordinal();
		}

		@Override
		public String toString() {
			return this.name;
		}

		static public EnumDecType fromMeta(int meta) {
			return values()[meta % values().length];
		}
	}

}

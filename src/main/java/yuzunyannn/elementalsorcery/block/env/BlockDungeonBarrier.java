package yuzunyannn.elementalsorcery.block.env;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.event.EventClient;
import yuzunyannn.elementalsorcery.event.ITickTask;
import yuzunyannn.elementalsorcery.potion.PotionGoldenEye;
import yuzunyannn.elementalsorcery.util.helper.EntityHelper;

public class BlockDungeonBarrier extends Block {

	public static final AxisAlignedBB ZERO_AABB = new AxisAlignedBB(0, 0, 0, 0, 0, 0);

	public BlockDungeonBarrier() {
		super(Material.GLASS);
		this.setTranslationKey("dungeonBarrier");
		this.setHardness(-1);
		this.setResistance(6000000.0F);
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		if (ESAPI.isDevelop) {
			EntityPlayerSP player = Minecraft.getMinecraft().player;
			Item barrier = Item.getItemFromBlock(ESObjects.BLOCKS.DUNGEON_BARRIER);
			if (player != null && (player.getHeldItemMainhand().getItem() == barrier
					|| player.getHeldItemOffhand().getItem() == barrier))
				return EnumBlockRenderType.MODEL;
		}
		return EnumBlockRenderType.INVISIBLE;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onBlockClicked(World worldIn, BlockPos pos, EntityPlayer playerIn) {
		if (!worldIn.isRemote) super.onBlockClicked(worldIn, pos, playerIn);
		super.onBlockClicked(worldIn, pos, playerIn);
		
		HashSet<BlockPos> posSet = new HashSet();
		LinkedList<BlockPos> doList = new LinkedList<>();
		doList.add(pos);
		posSet.add(pos);
		while (!doList.isEmpty()) {
			final BlockPos at = doList.removeFirst();
			final float lenth = MathHelper.sqrt(at.distanceSq(pos));
			if (lenth > 3) continue;
			for (EnumFacing facing : EnumFacing.VALUES) {
				BlockPos to = at.offset(facing);
				if (!posSet.contains(to)) {
					posSet.add(to);
					if (worldIn.getBlockState(to).getBlock() == this) doList.addLast(to);
				}
			}
			EventClient.addTickTask(() -> {
				PotionGoldenEye.shineBlock(worldIn, at, 1, 0xff7777);
				return ITickTask.END;
			}, (int) lenth);
		}
		
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float getAmbientOcclusionLightValue(IBlockState state) {
		return 1.0F;
	}

	@Override
	public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos) {
		return ZERO_AABB;
	}

	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox,
			List<AxisAlignedBB> collidingBoxes, Entity entityIn, boolean isActualState) {
		if (!(entityIn instanceof EntityLivingBase)) return;
		if (EntityHelper.isCreative(entityIn)) {
			EntityPlayer player = (EntityPlayer) entityIn;
			if (player.capabilities.isFlying) return;
		}
		super.addCollisionBoxToList(state, worldIn, pos, entityBox, collidingBoxes, entityIn, isActualState);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos,
			EnumFacing side) {
		IBlockState iblockstate = blockAccess.getBlockState(pos.offset(side));
		Block block = iblockstate.getBlock();
		return block == this ? false : super.shouldSideBeRendered(blockState, blockAccess, pos, side);
	}
}

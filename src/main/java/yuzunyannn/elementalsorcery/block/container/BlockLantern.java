package yuzunyannn.elementalsorcery.block.container;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.ESData;
import yuzunyannn.elementalsorcery.tile.TileLantern;
import yuzunyannn.elementalsorcery.util.helper.NBTHelper;

public class BlockLantern extends BlockContainerNormal {

	public BlockLantern() {
		super(Material.ROCK, "lantern", 3.5F);
		this.setLightLevel(1.0f);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileLantern();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		TileLantern tile = (TileLantern) worldIn.getTileEntity(pos);
		tile.randomDisplayTick();
		double d0 = (double) pos.getX() + 0.5D;
		double d1 = (double) pos.getY() + 0.675D;
		double d2 = (double) pos.getZ() + 0.5D;
		worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2, 0.0D, 0.0D, 0.0D);
		worldIn.spawnParticle(EnumParticleTypes.FLAME, d0, d1, d2, 0.0D, 0.0D, 0.0D);
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer,
			ItemStack stack) {
		if (worldIn.isRemote) return;
		NBTTagCompound nbt = ESData.getRuntimeData(placer);
		BlockPos prePos = NBTHelper.getBlockPos(nbt, "lanPre");
		TileEntity tile = worldIn.getTileEntity(prePos);
		if (tile instanceof TileLantern) {
			TileLantern tileLantern = (TileLantern) worldIn.getTileEntity(pos);
			tileLantern.link((TileLantern) tile);
			if (placer instanceof EntityPlayer) tileLantern.setPlayerName(((EntityPlayer) placer).getName());
		}
		NBTHelper.setBlockPos(nbt, "lanPre", pos);
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		if (worldIn.isRemote) return;
		TileEntity tile = worldIn.getTileEntity(pos);
		if (tile instanceof TileLantern) {
			TileLantern tileLantern = (TileLantern) tile;
			// 还原之前的位置
			String uername = tileLantern.getPlayerName();
			NBTTagCompound nbt = ESData.getRuntimeData(uername);
			BlockPos prePos = NBTHelper.getBlockPos(nbt, "lanPre");
			if (prePos.equals(pos)) {
				prePos = tileLantern.getNext();
				if (prePos != null) NBTHelper.setBlockPos(nbt, "lanPre", prePos);
			}
			// 离开
			tileLantern.leave();
		}
		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (playerIn.isSneaking()) {
			if (worldIn.isRemote) {
				// 特效一下
				for (int i = 0; i < 10; i++) {
					double d0 = pos.getX() + Math.random() * 1.5 - 0.25;
					double d1 = pos.getY() + Math.random() * 1.5 - 0.25;
					double d2 = pos.getZ() + Math.random() * 1.5 - 0.25;
					worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2, 0.0D, 0.0D, 0.0D);
					worldIn.spawnParticle(EnumParticleTypes.FLAME, d0, d1, d2, 0.0D, 0.0D, 0.0D);
				}
			} else {
				NBTTagCompound nbt = ESData.getRuntimeData(playerIn);
				NBTHelper.setBlockPos(nbt, "lanPre", pos);
			}
			return true;
		}
		TileEntity tile = worldIn.getTileEntity(pos);
		if (tile instanceof TileLantern) {
			TileLantern tileLantern = (TileLantern) worldIn.getTileEntity(pos);
			if (worldIn.isRemote) tileLantern.wantTransmit();
			return true;
		}
		return false;
	}

}

package yuzunyannn.elementalsorcery.block;

import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockLog;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.entity.elf.EntityElf;
import yuzunyannn.elementalsorcery.init.ESInitInstance;
import yuzunyannn.elementalsorcery.util.RandomHelper;

public class BlockElfLog extends BlockLog {

	final boolean cabinCenter;

	public BlockElfLog(boolean cabinCenter) {
		Blocks.FIRE.setFireInfo(this, 5, 5);
		this.setUnlocalizedName("elfLog");
		this.setDefaultState(this.blockState.getBaseState().withProperty(LOG_AXIS, BlockLog.EnumAxis.Y));
		this.cabinCenter = cabinCenter;
		if (cabinCenter) this.setTickRandomly(true);
	}

	// 精灵小屋核心，刷精灵的
	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		if (worldIn.isRemote) return;
		if (this.checkCabin(worldIn, pos) == false) return;
		final int size = 8;
		AxisAlignedBB aabb = new AxisAlignedBB(pos.getX() - size, pos.getY(), pos.getZ() - size, pos.getX() + size,
				pos.getY() + 2, pos.getZ() + size);
		List<EntityElf> list = worldIn.getEntitiesWithinAABB(EntityElf.class, aabb);
		if (list.size() > 2) return;
		pos = new BlockPos(pos.getX() + rand.nextInt(2) - 1, pos.getY() + 1, pos.getZ() + rand.nextInt(2) - 1);
		if (!worldIn.isAirBlock(pos)) return;
		EntityElf elf = new EntityElf(worldIn);
		elf.setPosition(pos.getX(), pos.getY(), pos.getZ());
		elf.setHomePosAndDistance(pos, -1);
		worldIn.spawnEntity(elf);
	}

	protected boolean checkCabin(World world, BlockPos centerPos) {
		// 地板
		int size = 3;
		for (int x = -size + 1; x <= size - 1; x++) {
			for (int z = -size + 1; z <= size - 1; z++) {
				if (x == 0 && z == 0) continue;
				if (world.getBlockState(centerPos.add(x, 0, z)).getBlock() != ESInitInstance.BLOCKS.ELF_LOG)
					return false;
			}
		}
		// 树叶
		size = 8;
		int leaf = 0;
		int fruit = 0;
		for (int x = -size; x <= size; x++) {
			for (int z = -size; z <= size; z++) {
				for (int y = -size; y <= size; y++) {
					BlockPos pos = centerPos.add(x, y, z);
					if (world.getBlockState(pos) == ESInitInstance.BLOCKS.ELF_LEAF.getDefaultState()
							.withProperty(BlockElfLeaf.DECAYABLE, true).withProperty(BlockElfLeaf.CHECK_DECAY, false))
						leaf++;
					if (world.getBlockState(pos).getBlock() == ESInitInstance.BLOCKS.ELF_FRUIT) fruit++;
				}
			}
		}
		return leaf >= 200 || (leaf > 75 && fruit > 3);
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		if (this.cabinCenter) return Item.getItemFromBlock(ESInitInstance.BLOCKS.ELF_LOG);
		return Item.getItemFromBlock(this);
	}

	@Override

	protected ItemStack getSilkTouchDrop(IBlockState state) {
		return new ItemStack(this.getItemDropped(state, RandomHelper.rand, 0));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { LOG_AXIS });
	}

	@Override
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
		if (this.cabinCenter == false) super.getSubBlocks(itemIn, items);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		IBlockState iblockstate = this.getDefaultState();
		switch (meta & 12) {
		case 0:
			iblockstate = iblockstate.withProperty(LOG_AXIS, BlockLog.EnumAxis.Y);
			break;
		case 4:
			iblockstate = iblockstate.withProperty(LOG_AXIS, BlockLog.EnumAxis.X);
			break;
		case 8:
			iblockstate = iblockstate.withProperty(LOG_AXIS, BlockLog.EnumAxis.Z);
			break;
		default:
			iblockstate = iblockstate.withProperty(LOG_AXIS, BlockLog.EnumAxis.NONE);
		}
		return iblockstate;
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		int i = 0;
		switch ((BlockLog.EnumAxis) state.getValue(LOG_AXIS)) {
		case X:
			i |= 4;
			break;
		case Z:
			i |= 8;
			break;
		case NONE:
			i |= 12;
		default:
			break;
		}
		return i;
	}
}

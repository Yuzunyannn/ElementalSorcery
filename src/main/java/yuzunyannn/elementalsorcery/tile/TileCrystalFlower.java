package yuzunyannn.elementalsorcery.tile;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.block.BlockCrystalFlower;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.item.crystal.ItemCrystal;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

/** 水晶花的tileentity除了记录数据与绘图外，还进行能量生长的判定 */
public class TileCrystalFlower extends TileEntityNetwork {

	public TileCrystalFlower() {
		super();
	}

	protected ItemStack crystal = ItemStack.EMPTY;

	public ItemStack getCrystal() {
		return crystal;
	}

	public List<ItemStack> getDropCrystal() {
		Item item = crystal.getItem();
		if (item instanceof ItemCrystal) {
			List<ItemStack> drops = new LinkedList();
			((ItemCrystal) item).getDropsOfCrystalFlower(world, crystal, drops);
			return drops;
		}
		return ItemHelper.toList(crystal);
	}

	public void setCrystal(ItemStack stack) {
		this.crystal = stack;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setTag("crystal", crystal.serializeNBT());
		return super.writeToNBT(compound);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		crystal = new ItemStack(compound.getCompoundTag("crystal"));
		super.readFromNBT(compound);
	}

	public void tryGrow(IBlockState state, Random rand) {
		if (this.crystal.isEmpty()) {
			TileEntity tile = world.getTileEntity(pos.down());
			if (tile instanceof TileLifeDirt) this.crystal = ((TileLifeDirt) tile).getPlant();
			if (this.crystal.isEmpty()) world.destroyBlock(pos, false);
			return;
		}
		int stage = state.getValue(BlockCrystalFlower.STAGE);
		if (stage >= BlockCrystalFlower.MAX_STAGE) return;
		state = state.withProperty(BlockCrystalFlower.STAGE, stage + 1);
		BlockPos lifeDirt = this.pos.down();
		Item item = this.crystal.getItem();
		if (item instanceof ItemCrystal) {
			if (((ItemCrystal) item).onCrystalFlowerGrow(this.crystal, world, rand, state, this, lifeDirt))
				world.setBlockState(pos, state);
		} else {
			// 如果是其他未录入的物品，则有25%的概率让生息之土重置
			world.setBlockState(pos, state);
			if (rand.nextInt(4) == 0) world.removeTileEntity(lifeDirt);
		}
	}

	@SideOnly(Side.CLIENT)
	public boolean needDraw() {
		return this.world.getBlockState(pos) == ESInit.BLOCKS.CRYSTAL_FLOWER.getDefaultState()
				.withProperty(BlockCrystalFlower.STAGE, 4);
	}

}

package yuzunyannn.elementalsorcery.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.block.BlockCrystalFlower;
import yuzunyannn.elementalsorcery.init.ESInitInstance;

/** 水晶花的tileentity除了记录数据与绘图外，还进行能量生长的判定 */
public class TileCrystalFlower extends TileEntityNetwork {

	public TileCrystalFlower() {
		super();
	}

	protected ItemStack crystal = ItemStack.EMPTY;

	public ItemStack getCrystal() {
		return crystal;
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

	public void tryGrow(IBlockState state) {
		if (this.crystal.isEmpty()) {
			TileEntity tile = world.getTileEntity(pos.down());
			if (tile instanceof TileLifeDirt) this.crystal = ((TileLifeDirt) tile).getPlant();
			if (this.crystal.isEmpty()) world.destroyBlock(pos, false);
			return;
		}
		int stage = state.getValue(BlockCrystalFlower.STAGE);
		if (stage >= 4) return;
		world.setBlockState(pos, state.withProperty(BlockCrystalFlower.STAGE, stage + 1));
	}

	@SideOnly(Side.CLIENT)
	public boolean needDraw() {
		return this.world.getBlockState(pos) == ESInitInstance.BLOCKS.CRYSTAL_FLOWER.getDefaultState()
				.withProperty(BlockCrystalFlower.STAGE, 4);
	}

}

package yuzunyannn.elementalsorcery.crafting.altar;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.building.Building;
import yuzunyannn.elementalsorcery.crafting.ICraftingCommit;
import yuzunyannn.elementalsorcery.item.ItemMagicRuler;
import yuzunyannn.elementalsorcery.tile.altar.TileBuildingAltar;
import yuzunyannn.elementalsorcery.util.NBTHelper;

public class CraftingBuildingRecord implements ICraftingCommit {

	private BlockPos pos1 = null;
	private BlockPos pos2 = null;
	private BlockPos pos = null;
	private boolean success = false;
	private String author = "";
	BlockPos center;
	// 客户端颜色
	public float r, g, b;
	// 客户端使用，告知结束，进行绘图
	public TileBuildingAltar tile;

	public CraftingBuildingRecord(BlockPos pos1, BlockPos pos2, String author) {
		this.pos1 = pos1;
		this.pos2 = pos2;
		this.pos = pos1;
		this.author = author;
		this.setCenter();
	}

	public CraftingBuildingRecord(NBTTagCompound nbt) {
		this.deserializeNBT(nbt);
	}

	private void setCenter() {
		if (this.pos1 == null || this.pos2 == null)
			return;
		center = new BlockPos((pos1.getX() + pos2.getX()) / 2, Math.min(pos1.getY(), pos2.getY()),
				(pos1.getZ() + pos2.getZ()) / 2);
	}

	public CraftingBuildingRecord setColor(int color) {
		this.r = ((color >> 16) & 0xff) / 255.0f;
		this.g = ((color >> 8) & 0xff) / 255.0f;
		this.b = ((color >> 0) & 0xff) / 255.0f;
		return this;
	}

	public CraftingBuildingRecord setTile(TileBuildingAltar tile) {
		this.tile = tile;
		return this;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		NBTHelper.setBlockPos(nbt, "pos1", pos1);
		NBTHelper.setBlockPos(nbt, "pos2", pos2);
		NBTHelper.setBlockPos(nbt, "pos", pos);
		nbt.setString("author", author);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		if (nbt == null)
			return;
		if (NBTHelper.hasBlockPos(nbt, "pos1"))
			this.pos1 = NBTHelper.getBlockPos(nbt, "pos1");
		if (NBTHelper.hasBlockPos(nbt, "pos2"))
			this.pos2 = NBTHelper.getBlockPos(nbt, "pos2");
		if (NBTHelper.hasBlockPos(nbt, "pos"))
			this.pos = NBTHelper.getBlockPos(nbt, "pos");
		author = nbt.getString("author");
		this.setCenter();
	}

	@Override
	public List<ItemStack> getItems() {
		return null;
	}

	public BlockPos getCurrPos() {
		return this.pos;
	}

	public boolean isSuccess() {
		return this.success;
	}

	public Building createBuilding(World world, EnumFacing face) {
		return Building.createBuilding(world, face, pos1, pos2);
	}

	public boolean onUpdate(World world) {
		if (this.pos1 == null || this.pos2 == null || this.pos == null)
			return false;
		if (this.pos1.distanceSq(this.pos2) > ItemMagicRuler.MAX_DIS_SQ)
			return false;
		do {
			this.pos = Building.movePosOnce(pos, pos1, pos2);
			if (this.pos == null) {
				this.success = true;
				return false;
			}
		} while (world.isAirBlock(this.pos));
		if (!world.isAirBlock(pos)) {
			if (center == null)
				this.setCenter();
		}
		return true;
	}

}

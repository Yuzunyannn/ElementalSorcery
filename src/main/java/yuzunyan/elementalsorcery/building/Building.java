package yuzunyan.elementalsorcery.building;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.INBTSerializable;

public class Building implements INBTSerializable<NBTTagCompound> {

	// 方块信息
	public class BlockInfo {
		final IBlockState state;
		final Block block;
		final int meta;
		int count = 0;
		final int tp_index;

		public BlockInfo(IBlockState state, int tp_index) {
			this.state = state;
			this.block = state.getBlock() == null ? Blocks.AIR : state.getBlock();
			this.meta = state.getBlock().getMetaFromState(state);
			this.tp_index = tp_index;
		}

		@Override
		public int hashCode() {
			return block.hashCode() + meta;
		}

		@Override
		public boolean equals(Object other) {
			if (other instanceof BlockInfo) {
				BlockInfo info = (BlockInfo) other;
				return info.tp_index == this.tp_index && info.block == this.block && info.meta == this.meta;
			}
			return false;
		}

		public IBlockState getState() {
			return state;
		}

		public int getCount() {
			return this.count;
		}
	}

	// 方块类型的信息
	public static class BlockTypeInfo {
		private final ItemStack bolockStack;
		int count = 0;

		public BlockTypeInfo(IBlockState state) {
			int meta = state.getBlock().damageDropped(state);
			bolockStack = new ItemStack(Item.getItemFromBlock(state.getBlock()), 1, meta);
		}

		@Override
		public boolean equals(Object other) {
			if (other instanceof BlockTypeInfo) {
				BlockTypeInfo info = (BlockTypeInfo) other;
				return info.bolockStack.getItem() == this.bolockStack.getItem()
						&& info.bolockStack.getMetadata() == this.bolockStack.getMetadata();
			}
			return false;
		}

		public String getUnlocalizedName() {
			return bolockStack.getUnlocalizedName();
		}

		public int getCount() {
			return this.count;
		}

	}

	/** 注册用名字 */
	private String keyName;

	public Building setKeyName(String name) {
		keyName = name;
		return this;
	}

	public String getKeyName() {
		return keyName;
	}

	/** 作者 */
	private String author;

	public Building setAuthor(String author) {
		this.author = author;
		return this;
	}

	public String getAuthor() {
		return this.author;
	}

	/** 记录方块类型的线性表 */
	private List<BlockInfo> info_list = new ArrayList<BlockInfo>();
	/** 记录位置到方块类型索引的哈希表 */
	private Map<BlockPos, Integer> block_map = new HashMap<BlockPos, Integer>();
	/** 记录方块种类的线性表 */
	private List<BlockTypeInfo> type_info_list = new ArrayList<BlockTypeInfo>();
	/** 建筑的方块 */
	private int maxX = 0, minX = 0, maxY = 0, minY = 0, maxZ = 0, minZ = 0;

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {

	}

	// 为制定状态添加对应的相对位置！
	public boolean add(IBlockState state, BlockPos pos) {
		if (state.getMaterial() == Material.AIR)
			return false;
		if (block_map.containsKey(pos))
			throw new IllegalArgumentException("Your pos has already exist!" + pos);
		// 类型信息
		BlockTypeInfo tp_info = new BlockTypeInfo(state);
		if (!type_info_list.contains(tp_info))
			type_info_list.add(tp_info);
		int tp_index = type_info_list.indexOf(tp_info);
		type_info_list.get(tp_index).count++;
		// 方块信息
		BlockInfo info = new BlockInfo(state, tp_index);
		if (!info_list.contains(info))
			info_list.add(info);
		int index = info_list.indexOf(info);
		block_map.put(pos, index);
		info_list.get(index).count++;
		this.update(pos);
		return true;
	}

	// 重载一下
	public boolean add(BlockPos pos, IBlockState state) {
		return this.add(state, pos);

	}

	// 重新计算最大边框
	private void update(BlockPos pos) {
		if (pos.getX() < minX)
			minX = pos.getX();
		else if (pos.getX() > maxX)
			maxX = pos.getX();
		if (pos.getY() < minY)
			minY = pos.getY();
		else if (pos.getY() > maxY)
			maxY = pos.getY();
		if (pos.getZ() < minZ)
			minZ = pos.getZ();
		else if (pos.getZ() > maxZ)
			maxZ = pos.getZ();
	}

	/** 获取边框 */
	public AxisAlignedBB getBox() {
		return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
	}

	/** 获取方块的类型list，请勿修改结果 */
	public List<BlockTypeInfo> getBlockTypeInfos() {
		return type_info_list;
	}

	/** 遍历用类 */
	public static class BuildingBlocks {
		private Iterator<Map.Entry<BlockPos, Integer>> iter = null;
		private Map.Entry<BlockPos, Integer> entry = null;
		private final Building building;
		private BlockPos off = BlockPos.ORIGIN;

		public BuildingBlocks(Building building) {
			this.building = building;
		}

		public boolean next() {
			if (iter == null)
				iter = building.block_map.entrySet().iterator();
			if (iter.hasNext()) {
				entry = iter.next();
				return true;
			}
			iter = null;
			entry = null;
			return false;
		}

		public BlockPos getPos() {
			return entry == null ? null : entry.getKey().add(off);
		}

		public IBlockState getState() {
			return entry == null ? null : building.info_list.get(entry.getValue()).getState();
		}

		public ItemStack getItemStack() {
			return entry == null ? null
					: building.type_info_list.get(building.info_list.get(entry.getValue()).tp_index).bolockStack.copy();
		}

		public BuildingBlocks setPosOff(BlockPos pos) {
			off = pos;
			return this;
		}
	}

	/** 获取遍历对象 */
	public BuildingBlocks getBuildingBlocks() {
		return new BuildingBlocks(this);
	}

}

package yuzunyannn.elementalsorcery.building;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import yuzunyannn.elementalsorcery.util.NBTHelper;

public class Building implements INBTSerializable<NBTTagCompound> {

	/** 方块信息 */
	public class BlockInfo implements INBTSerializable<NBTTagCompound> {
		/** 方块的state */
		IBlockState state;
		/** 方块 */
		Block block;
		/** 方块的meta */
		int meta;
		/** 对应物品类型的索引 */
		int typeIndex;

		public BlockInfo(IBlockState state, int tpIndex) {
			this.state = state;
			this.block = state.getBlock() == null ? Blocks.AIR : state.getBlock();
			this.meta = state.getBlock().getMetaFromState(state);
			this.typeIndex = tpIndex;
		}

		private BlockInfo(NBTTagCompound nbt) {
			this.deserializeNBT(nbt);
		}

		@Override
		public int hashCode() {
			return block.hashCode() + meta;
		}

		@Override
		public boolean equals(Object other) {
			if (other instanceof BlockInfo) {
				BlockInfo info = (BlockInfo) other;
				return info.typeIndex == this.typeIndex && info.block == this.block && info.meta == this.meta;
			}
			return false;
		}

		public IBlockState getState() {
			return state;
		}

		@Override
		public NBTTagCompound serializeNBT() {
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setInteger("meta", meta);
			nbt.setString("blockD", block.getRegistryName().getResourceDomain());
			nbt.setString("blockP", block.getRegistryName().getResourcePath());
			return nbt;
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt) {
			this.meta = nbt.getInteger("meta");
			ResourceLocation resourcelocation = new ResourceLocation(nbt.getString("blockD"), nbt.getString("blockP"));
			if (Block.REGISTRY.containsKey(resourcelocation)) {
				this.block = Block.REGISTRY.getObject(resourcelocation);
			} else {
				this.block = Blocks.DIRT;
			}
			this.state = this.block.getStateFromMeta(this.meta);
		}
	}

	/** 方块类型的信息 */
	public static class BlockItemTypeInfo {
		private final ItemStack bolockStack;
		int count = 0;

		public BlockItemTypeInfo(IBlockState state) {
			// 临时处理水
			if (state.getMaterial().isLiquid()) {
				ItemStack bucket = new ItemStack(Items.BUCKET);
				if (state.getBlock() instanceof BlockStaticLiquid) {
					if (state.getMaterial() == Material.WATER) {
						bucket = new ItemStack(Items.WATER_BUCKET);
					} else if (state.getMaterial() == Material.LAVA) {
						bucket = new ItemStack(Items.LAVA_BUCKET);
					} else {
						// 其他情况，有问题暂时不管
						IFluidHandlerItem handler = bucket
								.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
						BlockStaticLiquid blockLiquid = ((BlockStaticLiquid) state.getBlock());
						FluidStack fstack = FluidRegistry.getFluidStack(blockLiquid.getRegistryName().getResourcePath(),
								Fluid.BUCKET_VOLUME);
						handler.fill(fstack, true);
					}
				}
				this.bolockStack = bucket;
			} else if (state.getBlock() == Blocks.FLOWER_POT) {
				bolockStack = new ItemStack(Items.FLOWER_POT, 1);
			} else {
				int meta = state.getBlock().damageDropped(state);
				bolockStack = new ItemStack(Item.getItemFromBlock(state.getBlock()), 1, meta);
			}
		}

		@Override
		public boolean equals(Object other) {
			if (other instanceof BlockItemTypeInfo) {
				BlockItemTypeInfo info = (BlockItemTypeInfo) other;
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

	public Building() {

	}

	public Building(NBTTagCompound nbt) {
		this.deserializeNBT(nbt);
	}

	/** 注册用名字 */
	private String keyName;

	Building setKeyName(String name) {
		keyName = name;
		return this;
	}

	public String getKeyName() {
		return keyName;
	}

	/** 建筑名称 */
	private String name = "";

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/** 作者 */
	private String author = "";

	public Building setAuthor(String author) {
		this.author = author;
		return this;
	}

	public String getAuthor() {
		return this.author;
	}

	/** 上次修改时间 */
	long mtime = System.currentTimeMillis();

	/** 记录方块类型的线性表 */
	private List<BlockInfo> infoList = new ArrayList<BlockInfo>();
	/** 记录位置到方块类型索引的哈希表，索引的int为infoList的下标 */
	private Map<BlockPos, Integer> blockMap = new HashMap<BlockPos, Integer>();
	/** 记录方块种类的线性表 */
	private List<BlockItemTypeInfo> typeInfoList = new ArrayList<BlockItemTypeInfo>();
	/** 建筑的方块 */
	private int maxX = 0, minX = 0, maxY = 0, minY = 0, maxZ = 0, minZ = 0;

	public void mkdir() {
		this.mtime = System.currentTimeMillis();
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		// 基本数据
		nbt.setString("key", this.keyName);
		nbt.setString("name", this.name);
		nbt.setString("author", this.author);
		// nbt.setLong("mtime", this.mtime);
		// 记录位置和索引
		NBTTagList listMap = new NBTTagList();
		for (Entry<BlockPos, Integer> entry : blockMap.entrySet()) {
			NBTTagCompound posInfo = new NBTTagCompound();
			NBTHelper.setBlockPos(posInfo, "pos", entry.getKey());
			posInfo.setInteger("index", entry.getValue());
			listMap.appendTag(posInfo);
		}
		nbt.setTag("blockMap", listMap);
		// 记录所有被索引的方块类型
		NBTTagList listInfo = new NBTTagList();
		for (BlockInfo info : infoList) {
			listInfo.appendTag(info.serializeNBT());
		}
		nbt.setTag("infoList", listInfo);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		this.clear();
		// 基本数据
		this.keyName = nbt.getString("key");
		this.name = nbt.getString("name");
		this.author = nbt.getString("author");
		// this.mtime = nbt.getLong("mtime");
		// 恢复方块位置和索引
		NBTTagList listMap = nbt.getTagList("blockMap", 10);
		for (NBTBase base : listMap) {
			NBTTagCompound posInfo = (NBTTagCompound) base;
			BlockPos pos = NBTHelper.getBlockPos(posInfo, "pos");
			int index = posInfo.getInteger("index");
			blockMap.put(pos, index);
			this.update(pos);
		}
		// 恢复被索引的方块类型
		NBTTagList listInfo = nbt.getTagList("infoList", 10);
		for (NBTBase base : listInfo) {
			NBTTagCompound info = (NBTTagCompound) base;
			infoList.add(new BlockInfo(info));
		}
		// 重置数据
		for (Entry<BlockPos, Integer> entry : blockMap.entrySet()) {
			BlockInfo info = infoList.get(entry.getValue());
			BlockItemTypeInfo tpInfo = new BlockItemTypeInfo(info.state);
			if (!typeInfoList.contains(tpInfo))
				typeInfoList.add(tpInfo);
			int tpIndex = typeInfoList.indexOf(tpInfo);
			info.typeIndex = tpIndex;
			typeInfoList.get(tpIndex).count++;
		}
	}

	private void clear() {
		infoList.clear();
		blockMap.clear();
		typeInfoList.clear();
	}

	// 为制定状态添加对应的相对位置！
	public boolean add(IBlockState state, BlockPos pos) {
		if (state.getMaterial() == Material.AIR)
			return false;
		if (blockMap.containsKey(pos))
			throw new IllegalArgumentException("Your pos has already exist!" + pos);
		// 排除流动的液体
		if (state.getMaterial().isLiquid()) {
			if (state.getBlock().getMetaFromState(state) != 0)
				return false;
		}
		// 类型信息
		BlockItemTypeInfo tpInfo = new BlockItemTypeInfo(state);
		if (!typeInfoList.contains(tpInfo))
			typeInfoList.add(tpInfo);
		int tpIndex = typeInfoList.indexOf(tpInfo);
		typeInfoList.get(tpIndex).count++;
		// 方块信息
		BlockInfo info = new BlockInfo(state, tpIndex);
		if (!infoList.contains(info))
			infoList.add(info);
		int index = infoList.indexOf(info);
		// 放入方块
		blockMap.put(pos, index);
		this.update(pos);
		return true;
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
	public List<BlockItemTypeInfo> getBlockTypeInfos() {
		return typeInfoList;
	}

	/** 遍历用类 */
	public static class BuildingBlocks {
		private Iterator<Map.Entry<BlockPos, Integer>> iter = null;
		private Map.Entry<BlockPos, Integer> entry = null;
		private final Building building;
		private BlockPos off = BlockPos.ORIGIN;
		private EnumFacing facing = EnumFacing.NORTH;

		public BuildingBlocks(Building building) {
			this.building = building;
		}

		public boolean next() {
			if (iter == null)
				iter = building.blockMap.entrySet().iterator();
			if (iter.hasNext()) {
				entry = iter.next();
				return true;
			}
			iter = null;
			entry = null;
			return false;
		}

		/** 根据方向修正pos */
		static public BlockPos facePos(BlockPos pos, EnumFacing facing) {
			switch (facing) {
			case SOUTH:
				pos = new BlockPos(-pos.getX(), pos.getY(), -pos.getZ());
				break;
			case EAST:
				pos = new BlockPos(-pos.getZ(), pos.getY(), pos.getX());
				break;
			case WEST:
				pos = new BlockPos(pos.getZ(), pos.getY(), -pos.getX());
				break;
			default:
				break;
			}
			return pos;
		}

		public BlockPos getPos() {
			if (entry == null)
				return null;
			BlockPos pos = entry.getKey();
			return facePos(pos, this.facing).add(off);
		}

		/** 根据方向修正state */
		static public IBlockState faceSate(IBlockState state, EnumFacing facing) {
			switch (facing) {
			case SOUTH:
				state = state.withRotation(Rotation.CLOCKWISE_180);
				break;
			case EAST:
				state = state.withRotation(Rotation.CLOCKWISE_90);
				break;
			case WEST:
				state = state.withRotation(Rotation.COUNTERCLOCKWISE_90);
				break;
			default:
				break;
			}
			return state;
		}

		public IBlockState getState() {
			if (entry == null)
				return null;
			IBlockState state = building.infoList.get(entry.getValue()).getState();
			return this.faceSate(state, this.facing);
		}

		public ItemStack getItemStack() {
			return entry == null ? null
					: building.typeInfoList.get(building.infoList.get(entry.getValue()).typeIndex).bolockStack.copy();
		}

		public BuildingBlocks setPosOff(BlockPos pos) {
			off = pos;
			return this;
		}

		public BuildingBlocks setFace(EnumFacing facing) {
			this.facing = facing;
			return this;
		}
	}

	/** 获取遍历对象 */
	public BuildingBlocks getBuildingBlocks() {
		return new BuildingBlocks(this);
	}

	/** 通过两点，获取building */
	public static Building createBuilding(World world, EnumFacing facing, BlockPos pos1, BlockPos pos2) {
		Building building = new Building();
		BlockPos pos = pos1;
		BlockPos center = new BlockPos((pos1.getX() + pos2.getX()) / 2, Math.min(pos1.getY(), pos2.getY()),
				(pos1.getZ() + pos2.getZ()) / 2);
		// 处理方向
		if (facing == EnumFacing.WEST || facing == EnumFacing.EAST)
			facing = facing.getOpposite();
		// 记录方块
		while (true) {
			if (!world.isAirBlock(pos)) {
				building.add(BuildingBlocks.faceSate(world.getBlockState(pos), facing),
						BuildingBlocks.facePos(pos.subtract(center), facing));
			}
			// 移动pos
			pos = movePosOnce(pos, pos1, pos2);
			if (pos == null)
				break;
		}
		return building;
	}

	/**
	 * 移动一次pos在pos1和pos2之间
	 * 
	 * @return null表示移动结束！
	 */
	public static BlockPos movePosOnce(BlockPos pos, BlockPos pos1, BlockPos pos2) {
		if (pos.getX() == pos2.getX()) {
			if (pos.getZ() == pos2.getZ()) {
				if (pos.getY() == pos2.getY()) {
					return null;
				} else {
					if (pos.getY() < pos2.getY())
						pos = new BlockPos(pos1.getX(), pos.getY() + 1, pos1.getZ());
					else
						pos = new BlockPos(pos1.getX(), pos.getY() - 1, pos1.getZ());
				}
			} else {
				if (pos.getZ() < pos2.getZ())
					pos = new BlockPos(pos1.getX(), pos.getY(), pos.getZ() + 1);
				else
					pos = new BlockPos(pos1.getX(), pos.getY(), pos.getZ() - 1);
			}
		} else {
			if (pos.getX() < pos2.getX())
				pos = pos.add(1, 0, 0);
			else
				pos = pos.add(-1, 0, 0);
		}
		return pos;
	}
}

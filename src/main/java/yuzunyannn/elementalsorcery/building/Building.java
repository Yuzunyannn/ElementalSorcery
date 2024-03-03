package yuzunyannn.elementalsorcery.building;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.NBTTag;
import yuzunyannn.elementalsorcery.util.helper.NBTHelper;
import yuzunyannn.elementalsorcery.util.json.JsonObject;

public class Building implements INBTSerializable<NBTTagCompound> {

	/** 方块信息 */
	public class BlockInfo implements INBTSerializable<NBTTagCompound> {
		/** 方块的state */
		IBlockState state;
		/** 方块的保存数据 */
		NBTTagCompound tileSave;
		/** 对应物品类型的索引 */
		int typeIndex;

		public BlockInfo(IBlockState state, int tpIndex) {
			this.state = state;
			this.typeIndex = tpIndex;
		}

		public BlockInfo(IBlockState state, int tpIndex, NBTTagCompound tileSave) {
			this(state, tpIndex);
			if (tileSave == null) return;
			this.tileSave = tileSave;
			tileSave.removeTag("id");
			tileSave.removeTag("x");
			tileSave.removeTag("y");
			tileSave.removeTag("z");
		}

		private BlockInfo(NBTTagCompound nbt) {
			this.deserializeNBT(nbt);
		}

		@Override
		public int hashCode() {
			if (tileSave == null) return state.hashCode();
			return state.hashCode() ^ tileSave.hashCode();
		}

		@Override
		public boolean equals(Object other) {
			if (other instanceof BlockInfo) {
				BlockInfo info = (BlockInfo) other;
				boolean base = info.typeIndex == this.typeIndex && info.state == this.state;
				if (!base) return base;
				if (tileSave == null) return info.tileSave == null;
				return info.tileSave != null && this.tileSave.equals(info.tileSave);
			}
			return false;
		}

		public IBlockState getState() {
			return state;
		}

		public NBTTagCompound getTileEntityNBTData() {
			return tileSave;
		}

		public int getTypeIndex() {
			return typeIndex;
		}

		@Override
		public NBTTagCompound serializeNBT() {
			NBTTagCompound tag = new NBTTagCompound();

			Block block = state.getBlock();
			int meta = block.getMetaFromState(state);

			tag.setShort("meta", (short) meta);
			tag.setString("block", block.getRegistryName().toString());
			if (this.tileSave != null) tag.setTag("nbt", this.tileSave);

			return tag;
		}

		@Override
		public void deserializeNBT(NBTTagCompound tag) {
			int meta = tag.getShort("meta");

			ResourceLocation id;

			if (tag.hasKey("block", NBTTag.TAG_STRING)) id = new ResourceLocation(tag.getString("block"));
			else id = new ResourceLocation(tag.getString("blockD"), tag.getString("blockP"));

			Block block;

			if (Block.REGISTRY.containsKey(id)) block = Block.REGISTRY.getObject(id);
			else block = Blocks.DIRT;

			if (tag.hasKey("nbt", NBTTag.TAG_COMPOUND)) this.tileSave = tag.getCompoundTag("nbt");

			this.state = block.getStateFromMeta(meta);
		}
	}

	public Building() {

	}

	public Building(NBTTagCompound nbt) {
		this.deserializeNBT(nbt);
	}

	/** 注册用名字 */
	private String keyName = "";

	Building setKeyName(String name) {
		keyName = name;
		return this;
	}

	public String getKeyName() {
		return keyName;
	}

	@Override
	public String toString() {
		return "buidling:" + keyName;
	}

	/** 建筑名称 */
	protected String name = "";

	@SideOnly(Side.CLIENT)
	public String getDisplayName() {
		return this.getName();
	}

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
	protected List<BlockInfo> infoList = new ArrayList<BlockInfo>();
	/** 记录位置到方块类型索引的哈希表，索引的int为infoList的下标 */
	protected Map<BlockPos, Integer> blockMap = new HashMap<BlockPos, Integer>();
	/** 记录方块种类的线性表 */
	protected List<BlockItemTypeInfo> typeInfoList = new ArrayList<BlockItemTypeInfo>();
	/** 建筑的方块 */
	protected int maxX = 0, minX = 0, maxY = 0, minY = 0, maxZ = 0, minZ = 0;

	public void markDirty() {
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

		// 记录所有信息
		NBTTagList listInfo = new NBTTagList();
		NBTTagList[] posList = new NBTTagList[infoList.size()];
		for (int i = 0; i < infoList.size(); i++) {
			NBTTagCompound info = infoList.get(i).serializeNBT();
			posList[i] = new NBTTagList();
			info.setTag("pos", posList[i]);
			listInfo.appendTag(info);
		}
		nbt.setTag("infoList", listInfo);

		// 将坐标直接插入对应的信息里
		for (Entry<BlockPos, Integer> entry : blockMap.entrySet()) {
			int i = entry.getValue();
			BlockPos pos = entry.getKey();
			posList[i].appendTag(new NBTTagIntArray(NBTHelper.toIntArray(pos)));
		}

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

		// 恢复被索引的方块类型
		NBTTagList listInfo = nbt.getTagList("infoList", 10);
		for (int i = 0; i < listInfo.tagCount(); i++) {
			NBTTagCompound info = listInfo.getCompoundTagAt(i);
			infoList.add(new BlockInfo(info));
			// 恢复方块位置和索引
			NBTTagList poss = info.getTagList("pos", NBTTag.TAG_INT_ARRAY);
			for (NBTBase posArray : poss) {
				NBTTagIntArray posA = (NBTTagIntArray) posArray;
				BlockPos pos = NBTHelper.toBlockPos(posA.getIntArray());
				blockMap.put(pos, i);
				this.update(pos);
			}
		}

		// 重置数据
		for (Entry<BlockPos, Integer> entry : blockMap.entrySet()) {
			BlockInfo info = infoList.get(entry.getValue());
			BlockItemTypeInfo tpInfo = new BlockItemTypeInfo(info.state);
			if (info.tileSave != null) tpInfo.updateWithTileEntitySaveData(info.tileSave);
			if (tpInfo.isEmpty()) throw new RuntimeException("BlockItemTypeInfo is Missing!" + info.state);
			if (!typeInfoList.contains(tpInfo)) typeInfoList.add(tpInfo);
			int tpIndex = typeInfoList.indexOf(tpInfo);
			info.typeIndex = tpIndex;
			typeInfoList.get(tpIndex).addCountWith(info.state);
		}
	}

	public JsonObject serializeJson() {
		return new JsonObject(this.serializeNBT());
	}

	public void deserializeJson(JsonObject json) {
		this.deserializeNBT(json.asNBT());
	}

	private void clear() {
		infoList.clear();
		blockMap.clear();
		typeInfoList.clear();
	}

	public boolean add(IBlockState state, BlockPos pos) {
		return this.add(state, pos, (NBTTagCompound) null);
	}

	// 为制定状态添加对应的相对位置！
	public boolean add(IBlockState state, BlockPos pos, @Nullable NBTTagCompound tileSave) {
		if (state.getMaterial() == Material.AIR) return false;
		if (blockMap.containsKey(pos)) throw new IllegalArgumentException("Your pos has already exist!" + pos);
		// 类型信息
		BlockItemTypeInfo tpInfo = new BlockItemTypeInfo(state);
		if (tileSave != null) tpInfo.updateWithTileEntitySaveData(tileSave);
		// 一些无法被找到的方块不记录
		if (tpInfo.blockStack.isEmpty()) return false;
		if (!typeInfoList.contains(tpInfo)) typeInfoList.add(tpInfo);
		int tpIndex = typeInfoList.indexOf(tpInfo);
		typeInfoList.get(tpIndex).addCountWith(state);
		// 方块信息
		BlockInfo info = new BlockInfo(state, tpIndex, tileSave);
		if (!infoList.contains(info)) infoList.add(info);
		int index = infoList.indexOf(info);
		// 放入方块
		blockMap.put(pos, index);
		this.update(pos);
		return true;
	}

	/** 是否存在方块 */
	public boolean haveBlock(BlockPos pos) {
		return blockMap.containsKey(pos);
	}

	public BlockInfo getBlockInfo(BlockPos pos) {
		Integer index = blockMap.get(pos);
		if (index == null) return null;
		return infoList.get(index);
	}

	// 重新计算最大边框
	private void update(BlockPos pos) {
		if (pos.getX() < minX) minX = pos.getX();
		else if (pos.getX() > maxX) maxX = pos.getX();
		if (pos.getY() < minY) minY = pos.getY();
		else if (pos.getY() > maxY) maxY = pos.getY();
		if (pos.getZ() < minZ) minZ = pos.getZ();
		else if (pos.getZ() > maxZ) maxZ = pos.getZ();
	}

	/** 获取边框 */
	public AxisAlignedBB getBox() {
		return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
	}

	/** 获取方块的类型list，请勿修改结果 */
	public List<BlockItemTypeInfo> getBlockTypeInfos() {
		return typeInfoList;
	}

	/** 获取遍历对象 */
	public BuildingBlocks getBuildingIterator() {
		return new BuildingBlocks(this);
	}

	/** 通过两点，获取building */
	public static Building createBuilding(World world, EnumFacing facing, BlockPos pos1, BlockPos pos2,
			boolean checkTile) {
		Building building = new Building();
		BlockPos pos = pos1;
		BlockPos center = new BlockPos((pos1.getX() + pos2.getX()) / 2, Math.min(pos1.getY(), pos2.getY()),
				(pos1.getZ() + pos2.getZ()) / 2);
		// 处理方向
		if (facing == EnumFacing.WEST || facing == EnumFacing.EAST) facing = facing.getOpposite();
		// 记录方块
		while (true) {
			if (!world.isAirBlock(pos)) {
				IBlockState state = BuildingFace.face(world.getBlockState(pos), facing);
				BlockPos at = BuildingFace.face(pos.subtract(center), facing);
				if (checkTile) {
					if (state.getBlock().hasTileEntity(state)) {
						TileEntity tile = world.getTileEntity(pos);
						NBTTagCompound tileSave = tile == null ? null : tile.serializeNBT();
						tileSave = BuildingFace.tryFaceTile(state, tileSave, facing, false);
						building.add(state, at, tileSave);
					} else building.add(state, at);
				} else building.add(state, at);
			}
			// 移动pos
			pos = movePosOnce(pos, pos1, pos2);
			if (pos == null) break;
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
					if (pos.getY() < pos2.getY()) pos = new BlockPos(pos1.getX(), pos.getY() + 1, pos1.getZ());
					else pos = new BlockPos(pos1.getX(), pos.getY() - 1, pos1.getZ());
				}
			} else {
				if (pos.getZ() < pos2.getZ()) pos = new BlockPos(pos1.getX(), pos.getY(), pos.getZ() + 1);
				else pos = new BlockPos(pos1.getX(), pos.getY(), pos.getZ() - 1);
			}
		} else {
			if (pos.getX() < pos2.getX()) pos = pos.add(1, 0, 0);
			else pos = pos.add(-1, 0, 0);
		}
		return pos;
	}
}

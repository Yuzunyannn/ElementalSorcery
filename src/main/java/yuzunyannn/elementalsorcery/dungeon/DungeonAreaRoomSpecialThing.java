package yuzunyannn.elementalsorcery.dungeon;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

public class DungeonAreaRoomSpecialThing implements INBTSerializable<NBTTagCompound> {

	public DungeonAreaRoomSpecialThing(NBTTagCompound nbt) {
		this.deserializeNBT(nbt);
	}

	protected IDungeonSpecialThing special;
	protected Block block;

	public DungeonAreaRoomSpecialThing(Block block) {
		this.block = block;
		this.special = (IDungeonSpecialThing) block;
	}

	public IDungeonSpecialThing getHandler() {
		return special;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setString("id", block.getRegistryName().toString());
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		Block block = Block.getBlockFromName(nbt.getString("id"));
		if (block == null) block = Blocks.AIR;
		this.block = block;
		if (block instanceof IDungeonSpecialThing) special = (IDungeonSpecialThing) block;
	}

}

package yuzunyannn.elementalsorcery.explore;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

public class ExploreOreScan implements IExploreHandle {

	@Override
	public boolean explore(NBTTagCompound data, World world, BlockPos pos, int level, IBlockState state,
			EntityLivingBase portrait) {
		if (portrait != null) return true;
		if (level < 2) return true;
		NBTTagCompound ore = data.getCompoundTag("ores");
		int layer = ore.getInteger("layer");
		if (layer < 200) {
			final int layerUp = 15;
			if (layer < 5) layer = 5;
			pos = new BlockPos((pos.getX() >> 4) << 4, layer, (pos.getZ() >> 4) << 4);
			for (int x = 0; x < 16; x++) {
				for (int y = 0; y < layerUp; y++) {
					for (int z = 0; z < 16; z++) scanning(world, pos.add(x, y, z), ore);
				}
			}
			ore.setInteger("layer", layer + layerUp);
			data.setTag("ores", ore);
			return false;
		} else ore.removeTag("layer");
		return true;
	}

	public void scanning(World world, BlockPos pos, NBTTagCompound nbt) {
		if (world.isAirBlock(pos)) return;
		IBlockState state = world.getBlockState(pos);
		ItemStack realOre = scanningOre(state);
		if (realOre.isEmpty()) return;
		String id = realOre.getItem().getRegistryName().toString();
		NBTTagCompound data = nbt.getCompoundTag(id);
		data.setInteger("meta", realOre.getMetadata());
		data.setInteger("count", data.getInteger("count") + 1);
		nbt.setTag(id, data);
	}

	public static ItemStack scanningOre(IBlockState state) {
		if (isSpecialBlock(state)) return ItemHelper.toItemStack(state);
		String name = BlockHelper.getOreName(ItemHelper.toItemStack(state));
		if (!BlockHelper.isOre(name)) return ItemStack.EMPTY;
		return OreDictionary.getOres(name).get(0);
	}

	/** 檢測是否是特定的方塊，不走礦物詞典 */
	public static boolean isSpecialBlock(IBlockState state) {
		if (state.getBlock() == ESInit.BLOCKS.SEAL_STONE) return true;
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addExploreInfo(NBTTagCompound data, List<String> tooltip) {
		NBTTagCompound ore = data.getCompoundTag("ores");
		if (ore == null || ore.hasNoTags()) return;
		StringBuilder builder = new StringBuilder();
		for (String id : ore.getKeySet()) {
			Item item = Item.getByNameOrId(id);
			if (item == null) continue;
			NBTTagCompound nbt = ore.getCompoundTag(id);
			ItemStack o = new ItemStack(item, 1, nbt.getInteger("meta"));
			builder.append(o.getDisplayName());
			builder.append("X").append(nbt.getInteger("count")).append(' ');
		}
		tooltip.add(TextFormatting.GREEN + I18n.format("info.mineral.deposits", builder.toString()));
	}

	@Override
	public boolean hasExplore(NBTTagCompound data) {
		return data.hasKey("ores");
	}

}

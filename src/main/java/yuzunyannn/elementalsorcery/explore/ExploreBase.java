package yuzunyannn.elementalsorcery.explore;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityBed;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.util.NBTHelper;
import yuzunyannn.elementalsorcery.util.block.BlockHelper;

public class ExploreBase implements IExploreHandle {

	@Override
	public boolean explore(NBTTagCompound data, World world, BlockPos pos, int level, IBlockState state,
			EntityLivingBase portrait) {
		if (!data.hasKey("world")) {
			data.setInteger("world", world.provider.getDimension());
			return false;
		}
		if (!data.hasKey("biome")) {
			Biome biome = world.getBiome(pos);
			data.setString("biome", biome.getRegistryName().toString());
			return false;
		}
		if (state != null) {
			if (!data.hasKey("block")) {
				if (state.getBlock() == Blocks.DOUBLE_PLANT) {
					IBlockState downState = world.getBlockState(pos.down());
					if (downState.getBlock() == Blocks.DOUBLE_PLANT) state = downState;
				}
				data.setString("block", state.getBlock().getRegistryName().toString());
				data.setByte("bMeta", (byte) state.getBlock().getMetaFromState(state));
				if (state.getBlock() == Blocks.BED) {
					TileEntityBed tile = BlockHelper.getTileEntity(world, pos, TileEntityBed.class);
					if (tile != null) data.setByte("bMeta", (byte) tile.getColor().getMetadata());
				}
				return false;
			}
		}
		if (!NBTHelper.hasBlockPos(data, "pos")) {
			NBTHelper.setBlockPos(data, "pos", pos);
			return false;
		}
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addExploreInfo(NBTTagCompound data, List<String> tooltip) {
		if (!this.hasExplore(data)) return;
		BlockPos pos = NBTHelper.getBlockPos(data, "pos");
		int wrold = data.getInteger("world");
		ResourceLocation biomeId = new ResourceLocation(data.getString("biome"));
		String blockId = data.getString("block");
		tooltip.add(TextFormatting.GREEN + I18n.format("info.select.axis", pos.getX(), pos.getY(), pos.getZ()));
		String db = I18n.format("info.dimension.id", wrold) + " :: " + biomeId.getResourcePath();
		tooltip.add(TextFormatting.GREEN + db);
		if (!blockId.isEmpty()) {
			Block block = Block.getBlockFromName(blockId);
			if (block == null) return;
			ItemStack stack = new ItemStack(block, 1, data.getByte("bMeta"));
			if (stack.isEmpty()) stack = new ItemStack(Item.getByNameOrId(blockId), 1, data.getByte("bMeta"));
			String name = "";
			if (!stack.isEmpty() && I18n.hasKey(stack.getUnlocalizedName() + ".name"))
				name = I18n.format(stack.getUnlocalizedName() + ".name");
			else {
				if (I18n.hasKey(block.getUnlocalizedName() + ".name"))
					name = I18n.format(block.getUnlocalizedName() + ".name");
				else name = block.getRegistryName().toString();
			}
			tooltip.add(TextFormatting.GREEN + "-=" + name + "=-");
		}
	}

	@Override
	public boolean hasExplore(NBTTagCompound data) {
		return NBTHelper.hasBlockPos(data, "pos");
	}

	public ItemStack getBlockAsItem(NBTTagCompound data) {
		String blockId = data.getString("block");
		Block block = Block.getBlockFromName(blockId);
		if (block == null) return ItemStack.EMPTY;
		ItemStack stack = new ItemStack(block, 1, data.getByte("bMeta"));
		if (stack.isEmpty()) stack = new ItemStack(Item.getByNameOrId(blockId), 1, data.getByte("bMeta"));
		return stack;
	}

	public String getBiome(NBTTagCompound data) {
		return data.getString("biome");
	}

	public BlockPos getPos(NBTTagCompound data) {
		return NBTHelper.getBlockPos(data, "pos");
	}

	public int getWorldId(NBTTagCompound data) {
		return data.getInteger("world");
	}

}

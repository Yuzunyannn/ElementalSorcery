package yuzunyannn.elementalsorcery.building;

import com.mojang.authlib.GameProfile;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBanner;
import net.minecraft.item.ItemBed;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.StringUtils;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.util.helper.RandomHelper;

/** 方块类型的信息 */
public class BlockItemTypeInfo {

	protected ItemStack blockStack = ItemStack.EMPTY;
	protected int count = 0;

	public BlockItemTypeInfo(IBlockState state) {
		Block block = state.getBlock();
		if (block == Blocks.PISTON_HEAD) return;
		else if (block == Blocks.STANDING_SIGN) blockStack = new ItemStack(Items.SIGN, 1, 0);
		else if (block == Blocks.REDSTONE_WIRE) blockStack = new ItemStack(Items.REDSTONE, 1, 0);
		else if (block == Blocks.UNLIT_REDSTONE_TORCH)
			blockStack = new ItemStack(Item.getItemFromBlock(Blocks.REDSTONE_TORCH), 1, 0);
		else if (block == Blocks.REEDS) blockStack = new ItemStack(Items.REEDS);
		else if (state.getMaterial().isLiquid()) this.dealLiquid(state);
		else if (block instanceof BlockSkull) blockStack = new ItemStack(Items.SKULL, 1, 0);
		else if (block instanceof BlockSlab) {
			// 半砖
			int meta = state.getBlock().damageDropped(state);
			Item item = block.getItemDropped(state, RandomHelper.rand, 0);
			blockStack = new ItemStack(item, 1, meta);
		} else if (block instanceof BlockDoor) {
			// 门只要下半部分
			if (state.getValue(BlockDoor.HALF) == BlockDoor.EnumDoorHalf.UPPER) return;
			blockStack = new ItemStack(Item.getByNameOrId(block.getRegistryName().toString()));
		} else if (block instanceof BlockBush) {
			if (block instanceof BlockDoublePlant) {
				BlockDoublePlant.EnumBlockHalf half = state.getValue(BlockDoublePlant.HALF);
				if (half == BlockDoublePlant.EnumBlockHalf.UPPER) return;
			}
			Item item = Item.getItemFromBlock(block);
			int i = 0;
			if (item.getHasSubtypes()) i = block.getMetaFromState(state);
			blockStack = new ItemStack(item, 1, i);
		} else if (block instanceof BlockLeaves) {
			Item item = Item.getItemFromBlock(block);
			int meta = state.getBlock().damageDropped(state);
			if (block == Blocks.LEAVES) blockStack = new ItemStack(item, 1, meta);
			else blockStack = new ItemStack(item, 1, meta - 4);
		} else if (block == Blocks.BED) {
			BlockBed.EnumPartType part = state.getValue(BlockBed.PART);
			if (part == BlockBed.EnumPartType.HEAD) return;
			blockStack = new ItemStack(Items.BED, 1, 0);
		} else {
			if (state.getBlock() == Blocks.FLOWER_POT) {
				blockStack = new ItemStack(Items.FLOWER_POT, 1);
				return;
			}
			int meta = state.getBlock().damageDropped(state);
			blockStack = new ItemStack(Item.getItemFromBlock(block), 1, meta);
		}
	}

	public void updateWithTileEntitySaveData(NBTTagCompound nbt) {
		Item item = blockStack.getItem();
		if (item instanceof ItemBed) blockStack.setItemDamage(nbt.getInteger("color"));
		else if (item instanceof ItemBanner) {
			// 旗子
			EnumDyeColor color = EnumDyeColor.byDyeDamage(nbt.getInteger("Base"));
			NBTTagList patterns = nbt.getTagList("Patterns", 10);
			blockStack = ItemBanner.makeBanner(color, patterns);
			if (nbt.hasKey("CustomName", 8)) blockStack.setStackDisplayName(nbt.getString("CustomName"));
		} else if (item instanceof ItemSkull) {
			// 头骨
			byte type = nbt.getByte("SkullType");
			blockStack.setItemDamage(type);

			if (type == 3) {
				GameProfile playerProfile = null;
				if (nbt.hasKey("Owner", 10))
					playerProfile = NBTUtil.readGameProfileFromNBT(nbt.getCompoundTag("Owner"));
				else if (nbt.hasKey("ExtraType", 8)) {
					String s = nbt.getString("ExtraType");
					if (!StringUtils.isNullOrEmpty(s))
						playerProfile = TileEntitySkull.updateGameProfile(new GameProfile(null, s));
				}

				if (playerProfile != null) {
					NBTTagCompound nbttagcompound = new NBTTagCompound();
					NBTUtil.writeGameProfile(nbttagcompound, playerProfile);
					blockStack.setTagCompound(new NBTTagCompound());
					blockStack.getTagCompound().setTag("SkullOwner", nbttagcompound);
				}
			}

		}

	}

	private void dealLiquid(IBlockState state) {
		// 流动的不要
		if (state.getBlock().getMetaFromState(state) != 0) return;

		ItemStack bucket = new ItemStack(Items.BUCKET);
		if (state.getBlock() instanceof BlockStaticLiquid) {
			if (state.getMaterial() == Material.WATER) {
				bucket = new ItemStack(Items.WATER_BUCKET);
			} else if (state.getMaterial() == Material.LAVA) {
				bucket = new ItemStack(Items.LAVA_BUCKET);
			} else {
				// 其他情况，有问题暂时不管
				IFluidHandlerItem handler = bucket.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY,
						null);
				BlockStaticLiquid blockLiquid = ((BlockStaticLiquid) state.getBlock());
				FluidStack fstack = FluidRegistry.getFluidStack(blockLiquid.getRegistryName().getPath(),
						Fluid.BUCKET_VOLUME);
				handler.fill(fstack, true);
			}
		}

		this.blockStack = bucket;
	}

	static public int getCountFromState(IBlockState state) {
		Block block = state.getBlock();
		if (block instanceof BlockSlab && ((BlockSlab) block).isDouble()) return 2;
		return 1;
	}

	public void addCountWith(IBlockState state) {
		this.count += getCountFromState(state);
	}

	public ItemStack getItemStack() {
		return blockStack;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof BlockItemTypeInfo) {
			BlockItemTypeInfo info = (BlockItemTypeInfo) other;
			return info.blockStack.isItemEqual(this.blockStack);
		}
		return false;
	}

	public String getTranslationKey() {
		return blockStack.getTranslationKey();
	}

	@SideOnly(Side.CLIENT)
	public String getDisplayName() {
		return blockStack.getDisplayName();
	}

	public int getCount() {
		return count;
	}

	public static ItemStack getItemStackCanUsed(IInventory inventory, ItemStack need) {
		Block block = Block.getBlockFromItem(need.getItem());
		for (int i = 0; i < inventory.getSizeInventory(); ++i) {
			ItemStack stack = inventory.getStackInSlot(i);
			if (block == Blocks.GRASS || block == Blocks.FARMLAND) {
				Block blk = Block.getBlockFromItem(stack.getItem());
				if (blk == Blocks.DIRT && stack.getItemDamage() == 0) return stack;
			}
			if (ItemStack.areItemsEqual(need, stack) && ItemStack.areItemStackTagsEqual(need, stack)) return stack;
		}
		return ItemStack.EMPTY;
	}
}

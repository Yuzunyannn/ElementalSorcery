package yuzunyannn.elementalsorcery.item;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.block.container.BlockIceRockNode;
import yuzunyannn.elementalsorcery.tile.ir.TileIceRockStand;
import yuzunyannn.elementalsorcery.util.TextHelper;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;
import yuzunyannn.elementalsorcery.util.helper.Color;
import yuzunyannn.elementalsorcery.util.helper.NBTHelper;

public class ItemEntangleNode extends Item {

	public static ItemStack create(BlockPos pos) {
		ItemStack stack = new ItemStack(ESObjects.ITEMS.ENTANGLE_NODE);
		setBlockPos(stack, pos);
		return stack;
	}

	public static BlockPos getBlockPos(ItemStack stack) {
		if (stack.isEmpty()) return null;
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null) return null;
		return NBTHelper.getBlockPos(nbt, "iceRockCore");
	}

	public static void setBlockPos(ItemStack stack, BlockPos pos) {
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null) stack.setTagCompound(nbt = new NBTTagCompound());
		NBTHelper.setBlockPos(nbt, "iceRockCore", pos);
	}

	public ItemEntangleNode() {
		this.setTranslationKey("entangleNode");
		this.setMaxStackSize(1);
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {

	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		BlockPos pos = getBlockPos(stack);
		if (pos == null) return;
		tooltip.add(String.format("(%d,%d,%d)", pos.getX(), pos.getY(), pos.getZ()));
		if (!worldIn.isBlockLoaded(pos, false)) {
			tooltip.add(I18n.format("info.tileEntity.not.find"));
			return;
		}
		TileIceRockStand tile = BlockHelper.getTileEntity(worldIn, pos, TileIceRockStand.class);
		if (tile == null) {
			tooltip.add(I18n.format("info.tileEntity.not.find"));
			return;
		}

		tooltip.add(TextFormatting.YELLOW + I18n.format("info.magic.fragment")
				+ String.format(": %s / %s", TextHelper.toAbbreviatedNumber(tile.getMagicFragment()),
						TextHelper.toAbbreviatedNumber(tile.getMagicFragmentCapacity())));

	}

	@Override
	public boolean onEntityItemUpdate(EntityItem entityItem) {
		entityItem.setDead();
		if (entityItem.world.isRemote)
			BlockIceRockNode.addDestroyEffects(entityItem.world, entityItem.getPositionVector(), new Color(0x7cd0d3));
		return super.onEntityItemUpdate(entityItem);
	}

	public TileIceRockStand getCoreTile(ItemStack stack, World world) {
		BlockPos corePos = getBlockPos(stack);
		if (corePos == null) return null;
		return BlockHelper.getTileEntity(world, corePos, TileIceRockStand.class);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand,
			EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (worldIn.isRemote) return EnumActionResult.SUCCESS;
		ItemStack stack = player.getHeldItem(hand);
		TileIceRockStand tile = getCoreTile(stack, worldIn);
		if (tile == null) return EnumActionResult.FAIL;
		tile.callSubNodeCome(pos.offset(facing));
		return EnumActionResult.SUCCESS;
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
		World world = player.world;
		if (world.isRemote) return false;
		if (entity instanceof EntityLivingBase) {
			TileIceRockStand tile = getCoreTile(stack, world);
			if (tile == null) return false;
			tile.callFragmentAttack((EntityLivingBase) entity, player);
		}
		return false;
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
//		if (entityIn.ticksExisted % 20 != 0) return;
//		if (entityIn instanceof EntityLivingBase) {
//			EntityLivingBase player = (EntityLivingBase) entityIn;
//			DamageSource ds = player.getLastDamageSource();
//			if (ds == null) return;
//			Entity entity = ds.getTrueSource();
//			if (entity instanceof EntityLivingBase) {
//				TileIceRockStand tile = getCoreTile(stack, worldIn);
//				if (tile == null) return;
//				tile.callFragmentAttack((EntityLivingBase) entity, player);
//			}
//		}
	}
}

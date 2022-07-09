package yuzunyannn.elementalsorcery.item.prop;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;

public class ItemCollapse extends Item {

	public ItemCollapse() {
		this.setTranslationKey("collapse");
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		return TextFormatting.RED + super.getItemStackDisplayName(stack);
	}

	@Override
	public boolean onEntityItemUpdate(EntityItem entityItem) {
		World world = entityItem.world;
		if (world.isRemote) return false;
		entityItem.setNoDespawn();
		entityItem.setEntityInvulnerable(true);
		update(entityItem);
		return false;
	}

//	@Override
//	public void onUpdate(ItemStack stack, World world, Entity entityIn, int itemSlot, boolean isSelected) {
//		super.onUpdate(stack, world, entityIn, itemSlot, isSelected);
//		if (world.isRemote) return;
//		if (EntityHelper.isCreative(entityIn)) return;
//		update(entityIn);
//	}

	protected void update(Entity entity) {
		if (entity.ticksExisted % 4 != 0) return;
		World world = entity.world;
		Random rand = world.rand;
		BlockPos pos = new BlockPos(entity.posX, entity.posY - 0.5, entity.posZ);
		for (int y = 0; y < 5; y++) {
			int n = y + 1;
			for (int x = -n; x <= n; x++) {
				for (int z = -n; z < n; z++) {
					if (rand.nextFloat() < y * 0.2) continue;
					BlockPos at = pos.add(x, y, z);
					doCollapse(world, at);
				}
			}
		}
	}

	public static void doCollapse(World world, BlockPos pos) {
		if (world.isRemote) return;
		if (world.isAirBlock(pos)) return;
		if (BlockHelper.isBedrock(world, pos)) return;
		if (!BlockHelper.isFluid(world, pos)) world.playEvent(2001, pos, Block.getStateId(world.getBlockState(pos)));
		world.setBlockToAir(pos);
	}

}

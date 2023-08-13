package yuzunyannn.elementalsorcery.item.prop;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

public class ItemMeteoriteIngot extends Item {

	public ItemMeteoriteIngot() {
		this.setTranslationKey("meteoriteIngot");
	}

	@Override
	public boolean onEntityItemUpdate(EntityItem entityItem) {
		if (!entityItem.onGround) return super.onEntityItemUpdate(entityItem);

		if (entityItem.ticksExisted % 21 != 0) return super.onEntityItemUpdate(entityItem);

		World world = entityItem.world;
		if (world.isRemote) return super.onEntityItemUpdate(entityItem);

		BlockPos pos = new BlockPos(entityItem.getPositionVector().add(0, -0.2, 0));

		IBlockState state = world.getBlockState(pos);

		Block block = state.getBlock();

		if (block == Blocks.ICE || block == Blocks.PACKED_ICE) {
			int count = world.rand.nextInt(16);
			if (count / 16f > world.rand.nextFloat()) entityItem.getItem().shrink(1);
			world.destroyBlock(pos, false);
			ItemHelper.dropItem(world, pos, new ItemStack(ESObjects.ITEMS.ICE_ROCK_CHIP, count));
		}

//		AxisAlignedBB aabb = WorldHelper.createAABB(entityItem, 0.5, 0.5, 0.5);
//		List<EntityItem> list = world.getEntitiesWithinAABB(EntityItem.class, aabb);
//		updateRecipes(entityItem, list);

		return super.onEntityItemUpdate(entityItem);
	}

//	public void updateRecipes(EntityItem selfItem, List<EntityItem> list) {
//		for (EntityItem itemEntity : list) {
//			ItemStack itemStack = itemEntity.getItem();
//			Item item = itemStack.getItem();
//			if (item == Items.SNOWBALL) {
//				itemStack.shrink(1);
//			} else if (item == Item.getItemFromBlock(Blocks.ICE) || item == Item.getItemFromBlock(Blocks.PACKED_ICE)) {
//				itemStack.shrink(1);
//			}
//		}
//	}

}

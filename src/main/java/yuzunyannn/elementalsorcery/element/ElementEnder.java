package yuzunyannn.elementalsorcery.element;

import java.util.List;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import yuzunyannn.elementalsorcery.api.element.IStarFlowerCast;
import yuzunyannn.elementalsorcery.util.block.BlockHelper;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class ElementEnder extends ElementCommon implements IStarFlowerCast {

	public ElementEnder() {
		super(0xcc00fa, "ender");
	}

	@Override
	public ElementStack starFlowerCasting(World world, BlockPos pos, ElementStack estack, int tick) {
		if (tick % 60 != 0) return estack;
		if (world.isRemote) return estack;
		
		int range = getStarFlowerRange(estack);
		AxisAlignedBB aabb = WorldHelper.createAABB(pos, range, range, range);

		BlockPos chestPos = pos;
		IItemHandler itemHandler = null;
		for (EnumFacing facing : EnumFacing.HORIZONTALS) {
			TileEntity tile = world.getTileEntity(pos.offset(facing));
			if (tile == null) continue;
			itemHandler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite());
			if (itemHandler != null) {
				chestPos = pos.offset(facing);
				break;
			}
		}

		List<EntityItem> entities = world.getEntitiesWithinAABB(EntityItem.class, aabb);
		int count = 0;
		for (EntityItem entity : entities) {
			if (entity.getDistanceSq(chestPos) <= 2 * 2) continue;
			ItemStack stack = entity.getItem();
			if (stack.isEmpty()) continue;
			count++;
			if (itemHandler != null) {
				stack = BlockHelper.insertInto(itemHandler, stack);
				if (stack.isEmpty()) {
					entity.setDead();
					continue;
				}
				entity.setItem(stack);
			}
			entity.moveToBlockPosAndAngles(pos, 0, 0);
		}
		if (count == 0) return estack;
		world.playSound(null, chestPos.getX() + 0.5, chestPos.getY(), chestPos.getZ() + 0.5,
				SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.HOSTILE, 1, 1);
		estack.shrink(Math.max(1, count / 3));
		return estack;
	}

}

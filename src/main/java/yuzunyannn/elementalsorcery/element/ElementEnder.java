package yuzunyannn.elementalsorcery.element;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import yuzunyannn.elementalsorcery.element.explosion.EEEnder;
import yuzunyannn.elementalsorcery.element.explosion.ElementExplosion;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.util.element.DrinkJuiceEffectAdder;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;
import yuzunyannn.elementalsorcery.world.Juice.JuiceMaterial;

public class ElementEnder extends ElementCommon {

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

	@Override
	public ElementExplosion newExplosion(World world, Vec3d pos, ElementStack eStack, EntityLivingBase attacker) {
		return new EEEnder(world, pos, ElementExplosion.getStrength(eStack), eStack);
	}

	@Override
	protected void addDrinkJuiceEffect(DrinkJuiceEffectAdder helper) {

		helper.preparatory(MobEffects.LEVITATION, 10, 0);
		helper.check(JuiceMaterial.APPLE, 100).checkRatio(JuiceMaterial.MELON, 0.75f, 1.25f).join();

		helper.preparatory(ESInit.POTIONS.ENDERIZATION, 20, 75);
		helper.check(JuiceMaterial.APPLE, 125).join();

		helper.preparatory(ESInit.POTIONS.ENDERCORPS, 16, 150);
		helper.check(JuiceMaterial.MELON, 125).join();

	}

}

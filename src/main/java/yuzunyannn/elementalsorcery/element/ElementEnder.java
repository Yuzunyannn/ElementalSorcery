package yuzunyannn.elementalsorcery.element;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
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
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.element.JuiceMaterial;
import yuzunyannn.elementalsorcery.api.util.IWorldObject;
import yuzunyannn.elementalsorcery.api.util.WorldTarget;
import yuzunyannn.elementalsorcery.api.util.var.VariableSet;
import yuzunyannn.elementalsorcery.element.explosion.EEEnder;
import yuzunyannn.elementalsorcery.element.explosion.ElementExplosion;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraEnderTeleport;
import yuzunyannn.elementalsorcery.util.element.DrinkJuiceEffectAdder;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class ElementEnder extends ElementCommon {

	public ElementEnder() {
		super(0xcc00fa, "ender");
		setTransition(3f, 270, 180);
		setLaserCostOnce(1, 50);
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

		helper.preparatory(ESObjects.POTIONS.ENDERIZATION, 20, 75);
		helper.check(JuiceMaterial.APPLE, 125).join();

		helper.preparatory(ESObjects.POTIONS.ENDERCORPS, 16, 150);
		helper.check(JuiceMaterial.MELON, 125).join();

	}

	@Override
	protected void onExecuteLaser(World world, IWorldObject caster, WorldTarget target, ElementStack storage,
			VariableSet content) {

		Entity entity = target.getEntity();
		if (entity != null) {
			if (!entity.isNonBoss()) return;
			if (entity.height > 2) return;
			if (entity.width > 1.5) return;
			BlockPos pos = WorldHelper.tryFindPlaceToSpawn(world, rand, entity.getPosition(),
					8 + 16 * rand.nextFloat());
			if (pos != null) {
				if (world.isRemote) {
					MantraEnderTeleport.addEffect(world, target.getHitVec());
				} else MantraEnderTeleport.doEnderTeleport(world, entity, new Vec3d(pos).add(0.5, 1, 0.5));
			}
			return;
		}
		if (world.isRemote) return;
		BlockPos pos = target.getPos();
		IBlockState state = world.getBlockState(pos);
		if (state.getMaterial() == Material.AIR) return;
		Block block = state.getBlock();
		if (block.hasTileEntity(state)) return;
		if (!state.isFullBlock()) return;

		BlockPos at = pos.add(rand.nextGaussian() * 16, rand.nextGaussian() * 16, rand.nextGaussian() * 16);
		if (!BlockHelper.isReplaceBlock(world, at)) return;
		world.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
				SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.HOSTILE, 1, 1);
		world.setBlockToAir(pos);
		world.setBlockState(at, state);
	}

}

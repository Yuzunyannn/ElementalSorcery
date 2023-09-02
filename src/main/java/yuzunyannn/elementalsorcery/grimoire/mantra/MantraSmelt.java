package yuzunyannn.elementalsorcery.grimoire.mantra;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.element.Element;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.mantra.ICaster;
import yuzunyannn.elementalsorcery.api.mantra.IMantraData;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.api.tile.IElementInventoryPromote;
import yuzunyannn.elementalsorcery.api.util.IWorldObject;
import yuzunyannn.elementalsorcery.block.BlockAStone;
import yuzunyannn.elementalsorcery.item.ItemAncientPaper;
import yuzunyannn.elementalsorcery.item.ItemAncientPaper.EnumType;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.batch.EffectElementMove;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;
import yuzunyannn.elementalsorcery.util.helper.OreHelper;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

public class MantraSmelt extends MantraTypeSquareArea {

	public static int SMELT_HIGH = 8;

	public MantraSmelt() {
		this.setTranslationKey("smelt");
		this.setColor(0xde3700);
		this.setIcon("smelt");
		this.setRarity(-1);
		this.setOccupation(6);
		this.setPotentPowerCollect(0.1f, 2);
		this.addElementCollect(new ElementStack(ESObjects.ELEMENTS.FIRE, 3, 152), 240, 160);
		this.addElementCollect(new ElementStack(ESObjects.ELEMENTS.AIR, 2, 50), 80, 80);
		this.initAndAddDefaultMantraLauncher(0.002);
	}

	@Override
	public EnumType getMantraSubItemType() {
		return ItemAncientPaper.EnumType.NEW_WRITTEN;
	}

	@Override
	public void potentAttack(World world, ItemStack grimoire, ICaster caster, Entity target) {
		super.potentAttack(world, grimoire, caster, target);
		target.setFire(500);
	}

	@Override
	public void init(World world, SquareData data, ICaster caster, BlockPos pos) {
		ElementStack fire = data.get(ESObjects.ELEMENTS.FIRE);
		data.setSize(Math.min(fire.getPower() / 80, 4) + 4);
	}

	@Override
	public boolean tick(World world, SquareData data, ICaster caster, BlockPos originPos) {
		int tick = caster.iWantKnowCastTick();
		ElementStack fire = data.get(ESObjects.ELEMENTS.FIRE);
		if (fire.isEmpty()) return false;

		if (tick % 40 != 0) return true;

		fire.shrink(16);

		float pp = data.get(POTENT_POWER);

		updateSmeltArea(world, data, caster, originPos);

		if (world.isRemote) return true;

		final float size = data.getSize() / 2;
		AxisAlignedBB aabb = new AxisAlignedBB(originPos.getX() - size, originPos.getY(), originPos.getZ() - size,
				originPos.getX() + size, originPos.getY() + SMELT_HIGH, originPos.getZ() + size);
		List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, aabb);
		for (EntityLivingBase entity : entities) {
			entity.setFire(20);
			double dy = Math.max(entity.posY - originPos.getY(), 1);
			double addDamage = MathHelper.sqrt((fire.getPower() - 50) / 25) * (1 + pp * 0.5f);
			entity.attackEntityFrom(DamageSource.IN_FIRE, (float) (addDamage / dy));
		}

		return true;
	}

	public void updateSmeltArea(World world, SquareData data, ICaster caster, BlockPos originPos) {
		final int size = data.getSize() / 2;
		ElementStack fire = data.get(ESObjects.ELEMENTS.FIRE);

		double power = fire.getPower();
		IdentityHashMap<Element, ElementStack> elementMap = new IdentityHashMap();

		for (int layer = 0; layer < SMELT_HIGH; layer++) {

			for (int x = -size; x < size; x++) {
				for (int z = -size; z < size; z++) {

					BlockPos at = originPos.add(x, layer, z);
					if (BlockHelper.isBedrock(world, at)) continue;

					updateSmeltAt(world, data, caster, at, layer, power, elementMap);

				}
			}

			power = power * 0.9f;
		}

	}

	public void updateSmeltAt(World world, SquareData data, ICaster caster, BlockPos at, int layer, double power,
			IdentityHashMap<Element, ElementStack> elementMap) {

		IBlockState state = world.getBlockState(at);
		Block block = state.getBlock();
		if (block.isAir(state, world, at)) return;
		try {
			float explosionResistance = block.getExplosionResistance(world, at, null, null);
			if (explosionResistance >= 32) return;
		} catch (Exception e) {}

		if (block.hasTileEntity(state)) {
			TileEntity tile = world.getTileEntity(at);
			IElementInventory eInv = ElementHelper.getElementInventory(tile);
			if (eInv != null) {
				if (eInv.isEmpty()) return;
				IElementInventoryPromote promote = null;
				if (tile instanceof IElementInventoryPromote) promote = (IElementInventoryPromote) tile;
				if (promote != null && !promote.canInventoryOperateBy(this)) return;

				ElementStack eStack = ElementHelper.randomExtract(eInv, 50, 0);
				if (eStack.isEmpty()) return;
				if (world.isRemote) playElementGetEffect(world, at, eStack);
				else {
					if (elementMap.containsKey(eStack.getElement()))
						elementMap.get(eStack.getElement()).growOrBecome(eStack);
					else elementMap.put(eStack.getElement(), eStack);
					if (promote != null && ElementHelper.isEmpty(eInv)) promote.onInventoryStatusChange();
				}
				return;
			}
		}

		if (world.isRemote) return;
		if (layer > world.rand.nextInt(SMELT_HIGH + 1)) return;

		if (block == ESObjects.BLOCKS.METEORITE_DRUSE) {
			ElementStack wood = elementMap.get(ESObjects.ELEMENTS.WOOD);
			if (wood == null) return;
			if (wood.getCount() < 50) return;
			if (wood.getPower() < 200) return;
			ElementStack metal = elementMap.get(ESObjects.ELEMENTS.METAL);
			if (metal == null) return;
			if (metal.getCount() < 50) return;
			if (metal.getPower() < 200) return;
			ElementStack earth = elementMap.get(ESObjects.ELEMENTS.EARTH);
			if (earth == null) return;
			if (earth.getCount() < 50) return;
			if (earth.getPower() < 200) return;
			wood.shrink(50);
			metal.shrink(50);
			earth.shrink(50);
			world.setBlockToAir(at);
			dropItem(world, at, caster, new ItemStack(ESObjects.ITEMS.METEORITE_INGOT), elementMap);
			return;
		}

		if (block == ESObjects.BLOCKS.ASTONE) {
			BlockAStone.EnumType type = state.getValue(BlockAStone.VARIANT);
			if (type == BlockAStone.EnumType.FRAGMENTED) {
				world.setBlockToAir(at);
				ItemStack stack = ItemHelper
						.toItemStack(state.withProperty(BlockAStone.VARIANT, BlockAStone.EnumType.STONE));
				dropItem(world, at, caster, stack, elementMap);
			}
			return;
		}

		world.setBlockToAir(at);

		if (OreHelper.isOre(state)) {

			OreHelper.OreEnum ore = OreHelper.getOreInfo(state);
			NonNullList<ItemStack> originDrops = NonNullList.create();
			block.getDrops(originDrops, world, at, state, (int) MathHelper.clamp(power / 175, 0, 3));

			NonNullList<ItemStack> drops = NonNullList.create();

			int productTypeCount = ore.getProductTypeCount();
			if (productTypeCount > 0) {

				for (ItemStack stack : originDrops) {
					ItemStack result = FurnaceRecipes.instance().getSmeltingResult(stack);
					if (!result.isEmpty()) drops.add(result.copy());
				}

				for (int i = 0; i < ore.getProductTypeCount(); i++) {
					ItemStack result = ore.produceOreProduct(i, world, IWorldObject.of(world, at));
					drops.add(result);
				}

			} else drops = originDrops;

			for (ItemStack stack : drops) dropItem(world, at, caster, stack, elementMap);

			return;
		}

		ItemStack stack = ItemHelper.toItemStack(state);

		if (stack.isEmpty()) return;

		ItemStack result = FurnaceRecipes.instance().getSmeltingResult(stack);
		if (!result.isEmpty()) dropItem(world, at, caster, result.copy(), elementMap);
		else ItemHelper.dropItem(world, at, stack).setFire(100);

	}

	protected void dropItem(World world, BlockPos at, ICaster caster, ItemStack stack,
			IdentityHashMap<Element, ElementStack> elementMap) {
		
		if (elementMap.containsKey(ESObjects.ELEMENTS.ENDER)) {
			ElementStack ender = elementMap.get(ESObjects.ELEMENTS.ENDER);
			if (ender.getPower() > 20 && ender.getCount() > 2) {
				EntityPlayer player = caster.iWantRealCaster().asPlayer();
				if (player != null) {
					ender.shrink(2);
					ItemHelper.addItemStackToPlayer(player, stack);
					return;
				}
			}
		}

		ItemHelper.dropItem(world, at, stack);
	}

	@SideOnly(Side.CLIENT)
	protected void playElementGetEffect(World world, BlockPos at, ElementStack eStack) {
		Vec3d center = new Vec3d(at).add(0.5, 0.5, 0.5);
		Random rand = world.rand;
		for (int i = 0; i < 8; i++) {
			Vec3d vec = new Vec3d(rand.nextGaussian(), rand.nextGaussian(), rand.nextGaussian()).scale(0.1);
			EffectElementMove em = new EffectElementMove(world, center);
			em.yDecay = 0.99;
			em.zDecay = em.xDecay = 0.6;
			em.motionY = 0.05 + world.rand.nextFloat() * 0.1;
			em.motionX = vec.x;
			em.motionZ = vec.z;
			em.setColor(eStack.getColor());
			Effect.addEffect(em);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void addSquareEffect(World world, IMantraData mData, ICaster caster, int size) {
		super.addSquareEffect(world, mData, caster, size);
		Random rand = world.rand;
		Vec3d center = new Vec3d(caster.iWantDirectCaster().getPosition());
		Vec3d at = center.add(size / 2f - rand.nextDouble() * size + 0.5, 0,
				size / 2f - rand.nextDouble() * size + 0.5);
		world.spawnParticle(EnumParticleTypes.FLAME, at.x, at.y, at.z, 0, 0.4, 0);
	}

}

package yuzunyannn.elementalsorcery.item.crystal;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.api.tile.IAcceptMagic;
import yuzunyannn.elementalsorcery.block.BlockAStone;
import yuzunyannn.elementalsorcery.block.BlockElfSapling;
import yuzunyannn.elementalsorcery.block.BlockLifeFlower;
import yuzunyannn.elementalsorcery.capability.ElementInventory;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.entity.EntityThrow;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.render.effect.Effects;
import yuzunyannn.elementalsorcery.render.effect.FireworkEffect;
import yuzunyannn.elementalsorcery.tile.md.TileMDBase;
import yuzunyannn.elementalsorcery.tile.md.TileMDResonantIncubator;

public class ItemResonantCrystal extends ItemCrystal implements EntityThrow.IItemThrowAction {

	public ItemResonantCrystal() {
		super("resonantCrystal", 42.31f, 0xff7200);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		NBTTagCompound nbt = ElementalSorcery.getPlayerData(Minecraft.getMinecraft().player);
		float fre = nbt.getFloat("resFre");
		tooltip.add(TextFormatting.GOLD + I18n.format("info.crystal.percept", fre));
		tooltip.add(I18n.format("info.resonantCrystal"));
	}

	@Override
	public float probabilityOfLeftDirtClear() {
		return 0.1f;
	}

	@Override
	public int dropCountOfCrystalFlower(World world, ItemStack origin, Random rand) {
		return origin.getCount() + rand.nextInt(3);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		EntityThrow.shoot(playerIn, playerIn.getHeldItem(handIn));
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
	}

	protected int onImpactDo(World world, EntityThrow et, RayTraceResult result) {
		Random rand = et.getRandom();
		EntityLivingBase thrower = et.getThrower();

		if (result.entityHit != null) {
			if (result.entityHit instanceof EntityLivingBase) {
				EntityLivingBase entity = (EntityLivingBase) result.entityHit;
				// 砸中living
				if (entity instanceof IMob) {
					int i = 12;
					entity.attackEntityFrom(DamageSource.causeThrownDamage(et, thrower), (float) i);
					return 7;
				} else if (!world.isRemote) {
					if (entity instanceof EntitySheep) {
						final EnumDyeColor[] colors = EnumDyeColor.values();
						((EntitySheep) entity).setFleeceColor(colors[rand.nextInt(colors.length)]);
					}
					entity.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 40, 3));
					return 6;
				}
			} else {
				result.entityHit.setFire(5);
			}
		} else sp: {

//			// 物品搜查
//			final float size = 1f;
//			AxisAlignedBB aabb = new AxisAlignedBB(this.posX - size, this.posY - size, this.posZ - size,
//					this.posX + size, this.posY + size, this.posZ + size);
//			List<EntityItem> list = world.getEntitiesWithinAABB(EntityItem.class, aabb);
//			for (EntityItem entityItem : list) {
//				ItemStack stack = entityItem.getItem();
//				if (stack.getItem() == ESInitInstance.ITEMS.MAGIC_CRYSTAL) {
//					if (!world.isRemote) ItemMagicalCrystal.tryCraft(entityItem, false);
//					return 8;
//				}
//			}

			// 方块判定
			BlockPos pos = result.getBlockPos();
			if (pos == null) break sp;
			IBlockState state = world.getBlockState(pos);
			Block block = state.getBlock();
			if (block == ESInit.BLOCKS.LIFE_FLOWER) {
				// 植物花
				((BlockLifeFlower) block).tryGrowAll(world, pos);
				return 5;
			} else if (block instanceof BlockElfSapling) {
				EntityPlayer player = null;
				if (thrower instanceof EntityPlayer) player = (EntityPlayer) thrower;
				((BlockElfSapling) block).superGrow(world, rand, pos, state, player, false);
				return 5;
			} else if (block instanceof IGrowable) {
				if (((IGrowable) block).canGrow(world, pos, state, world.isRemote)) {
					((IGrowable) block).grow(world, rand, pos, state);
					return 5;
				}
			} else if (block == ESInit.BLOCKS.ASTONE) {
				if (state == block.getDefaultState().withProperty(BlockAStone.VARIANT,
						BlockAStone.EnumType.FRAGMENTED)) {
					world.setBlockState(pos, state.withProperty(BlockAStone.VARIANT, BlockAStone.EnumType.STONE));
					return 4;
				}
			}
			TileEntity tile = world.getTileEntity(pos);
			if (tile == null) break sp;
			if (tile instanceof TileMDResonantIncubator)
				((TileMDResonantIncubator) tile).resonance(rand.nextFloat() * 100);
			if (tile instanceof IAcceptMagic) {
				((IAcceptMagic) tile).accpetMagic(ElementStack.magic(80, 50), et.getPosition(), result.sideHit);
				return 4;
			} else if (tile.hasCapability(ElementInventory.ELEMENTINVENTORY_CAPABILITY, result.sideHit)) {
				if (tile.getCapability(ElementInventory.ELEMENTINVENTORY_CAPABILITY, result.sideHit)
						.insertElement(ElementStack.magic(80, 50), false))
					return 4;
			}
		}
		return 3;
	}

	@Override
	public void onImpact(EntityThrow entity, RayTraceResult result) {
		World world = entity.world;
		int effectState = 3;
		effectState = this.onImpactDo(world, entity, result);
		if (!world.isRemote) world.setEntityState(entity, (byte) effectState);
	}

	@SideOnly(Side.CLIENT)
	public static void boom(World world, Vec3d v3d, int[] color, int[] fadeColor) {
		NBTTagCompound nbt = FireworkEffect.fastNBT(0, 2, 0.375f, color, fadeColor);
		Effects.spawnEffect(world, Effects.FIREWROK, v3d, nbt);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void handleStatusUpdate(EntityThrow entity, byte id) {
		World world = entity.world;
		Vec3d v3d = entity.getPositionVector();
		if (!world.isAirBlock(entity.getPosition())) {
			v3d = v3d.addVector(0, 1, 0);
			if (!world.isAirBlock(new BlockPos(v3d))) v3d = v3d.addVector(0, 1, 0);
		}
		switch (id) {
		case 3:
			boom(world, v3d, new int[] { 0xde680a, 0x9a551d }, new int[] { 0xfdc078 });
			break;
		case 4:
			// 紫色
			boom(world, v3d, new int[] { 0xde680a, TileMDBase.PARTICLE_COLOR[0] }, TileMDBase.PARTICLE_COLOR_FADE);
			break;
		case 5:
			// 绿色
			boom(world, v3d, new int[] { 0xde680a, 0x096b18 }, new int[] { 0x5ac37b });
			break;
		case 6:
			// 粉色
			boom(world, v3d, new int[] { 0xde680a, 0xcd5cab }, new int[] { 0xfab4e5 });
			break;
		case 7:
			// 红色
			boom(world, v3d, new int[] { 0xde680a, 0xc90000 }, new int[] { 0xec8282 });
			break;
		case 8:
			// 蓝色
			boom(world, v3d, new int[] { 0xde680a, 0x0028f5 }, new int[] { 0x97a8fc });
			break;
		default:
			break;
		}

	}
}

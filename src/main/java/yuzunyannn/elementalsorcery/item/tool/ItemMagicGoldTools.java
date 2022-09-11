package yuzunyannn.elementalsorcery.item.tool;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.mantra.SilentLevel;
import yuzunyannn.elementalsorcery.render.effect.Effects;
import yuzunyannn.elementalsorcery.render.effect.scrappy.FireworkEffect;
import yuzunyannn.elementalsorcery.render.effect.scrappy.FirewrokShap;
import yuzunyannn.elementalsorcery.tile.md.TileMDBase;
import yuzunyannn.elementalsorcery.tile.md.TileMDRubbleRepair;

public class ItemMagicGoldTools {

	@SideOnly(Side.CLIENT)
	public static void createShape(World world, BlockPos pos) {
		FirewrokShap.createCircle(world, new Vec3d(pos).add(0.5, 0.5, 0.5), 0.2, 1, TileMDBase.PARTICLE_COLOR,
				TileMDBase.PARTICLE_COLOR_FADE, false, false);
	}

	@SideOnly(Side.CLIENT)
	public static void createShape(World world, Vec3d pos) {
		FirewrokShap.createCircle(world, pos, 0.2, 1, TileMDBase.PARTICLE_COLOR, TileMDBase.PARTICLE_COLOR_FADE, false,
				false);
	}

	public static final ToolMaterial MAGIC_GOLD = EnumHelper.addToolMaterial("MagicGold", 3, 16, 32, 12, 0);

	/** 工具修理的通用句柄 */
	public static interface MagicGoldToolsRepair extends TileMDRubbleRepair.IExtendRepair {
		@Override
		default public ItemStack getRepairOutput(ItemStack input) {
			if (input.getItemDamage() == 0) return ItemStack.EMPTY;
			input = input.copy();
			input.setItemDamage(input.getItemDamage() - 1);
			return input;
		}

		@Override
		default public int getRepairCost(ItemStack input) {
			return 10;
		}
	}

	// 镐子
	public static class ItemMagicGoldPickaxe extends ItemPickaxe implements MagicGoldToolsRepair {
		public ItemMagicGoldPickaxe() {
			super(MAGIC_GOLD);
			this.setTranslationKey("magicGoldPickaxe");
		}

		@Override
		public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
			return false;
		}

		@Override
		public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos,
				EntityLivingBase entityLiving) {
			if (!"pickaxe".equals(state.getBlock().getHarvestTool(state)))
				return super.onBlockDestroyed(stack, worldIn, state, pos, entityLiving);
			if (ESAPI.silent.isSilent(entityLiving, SilentLevel.RELEASE))
				return super.onBlockDestroyed(stack, worldIn, state, pos, entityLiving);
			int size = 1;
			Vec3d forward = entityLiving.getForward();
			EnumFacing facing = EnumFacing.getFacingFromVector((float) forward.x, (float) forward.y, (float) forward.z);
			EnumFacing.Axis axis = facing.getAxis();
			switch (axis) {
			case X:
				for (int y = -size; y <= size; y++) for (int z = -size; z <= size; z++) {
					if (y == 0 && z == 0) continue;
					BlockPos at = pos.add(0, y, z);
					if (worldIn.getBlockState(at) == state) {
						worldIn.destroyBlock(at, true);
						if (worldIn.isRemote) createShape(worldIn, at);
					}
				}
				break;
			case Y:
				for (int x = -size; x <= size; x++) for (int z = -size; z <= size; z++) {
					if (x == 0 && z == 0) continue;
					BlockPos at = pos.add(x, 0, z);
					if (worldIn.getBlockState(at) == state) {
						worldIn.destroyBlock(at, true);
						if (worldIn.isRemote) createShape(worldIn, at);
					}
				}
				break;
			case Z:
				for (int x = -size; x <= size; x++) for (int y = -size; y <= size; y++) {
					if (y == 0 && x == 0) continue;
					BlockPos at = pos.add(x, y, 0);
					if (worldIn.getBlockState(at) == state) {
						worldIn.destroyBlock(at, true);
						if (worldIn.isRemote) createShape(worldIn, at);
					}
				}
				break;
			}
			return super.onBlockDestroyed(stack, worldIn, state, pos, entityLiving);
		}
	}

	// 斧头
	public static class ItemMagicGoldAxe extends ItemAxe implements MagicGoldToolsRepair {
		public ItemMagicGoldAxe() {
			super(MAGIC_GOLD, MAGIC_GOLD.getAttackDamage() * 1.75f, -3.2f);
			this.setTranslationKey("magicGoldAxe");
		}

		@Override
		public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
			return false;
		}

		@Override
		public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos,
				EntityLivingBase entityLiving) {
			if (ESAPI.silent.isSilent(entityLiving, SilentLevel.RELEASE))
				super.onBlockDestroyed(stack, worldIn, state, pos, entityLiving);
			Block block = state.getBlock();
			if (block instanceof BlockLog) {
				for (int y = 0; y < 10; y++) {
					BlockPos at = pos.add(0, y, 0);
					if (worldIn.getBlockState(at) != state) break;
					worldIn.destroyBlock(at, true);
					if (worldIn.isRemote) createShape(worldIn, at);
				}
				int size = 2;
				for (int x = -size; x <= size; x++)
					for (int y = -size; y <= size; y++) for (int z = -size; z <= size; z++) {
						BlockPos at = pos.add(x, y, z);
						if (worldIn.getBlockState(at) != state) continue;
						worldIn.destroyBlock(at, true);
						if (worldIn.isRemote) createShape(worldIn, at);
					}
			}
			return super.onBlockDestroyed(stack, worldIn, state, pos, entityLiving);
		}

	}

	// 铁锨
	public static class ItemMagicGoldSpade extends ItemSpade implements MagicGoldToolsRepair {
		public ItemMagicGoldSpade() {
			super(MAGIC_GOLD);
			this.setTranslationKey("magicGoldSpade");
		}

		@Override
		public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
			return false;
		}

		@Override
		public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos,
				EntityLivingBase entityLiving) {
			if (!"shovel".equals(state.getBlock().getHarvestTool(state)))
				return super.onBlockDestroyed(stack, worldIn, state, pos, entityLiving);
			if (ESAPI.silent.isSilent(entityLiving, SilentLevel.RELEASE))
				super.onBlockDestroyed(stack, worldIn, state, pos, entityLiving);
			int size = 2;
			Vec3d forward = entityLiving.getForward();
			EnumFacing facing = EnumFacing.getFacingFromVector((float) forward.x, (float) forward.y, (float) forward.z);
			EnumFacing.Axis axis = facing.getAxis();
			switch (axis) {
			case X:
				for (int y = -size; y <= size; y++) for (int z = -size; z <= size; z++) {
					BlockPos at = pos.add(0, y, z);
					if (worldIn.getBlockState(at) == state) {
						worldIn.destroyBlock(at, true);
						if (worldIn.isRemote) createShape(worldIn, at);
					}
				}
				break;
			case Y:
				for (int x = -size; x <= size; x++) for (int z = -size; z <= size; z++) {
					BlockPos at = pos.add(x, 0, z);
					if (worldIn.getBlockState(at) == state) {
						worldIn.destroyBlock(at, true);
						if (worldIn.isRemote) createShape(worldIn, at);
					}
				}
				break;
			case Z:
				for (int x = -size; x <= size; x++) for (int y = -size; y <= size; y++) {
					BlockPos at = pos.add(x, y, 0);
					if (worldIn.getBlockState(at) == state) {
						worldIn.destroyBlock(at, true);
						if (worldIn.isRemote) createShape(worldIn, at);
					}
				}
				break;
			}

			return super.onBlockDestroyed(stack, worldIn, state, pos, entityLiving);
		}
	}

	// 锄头
	public static class ItemMagicGoldHoe extends ItemHoe implements MagicGoldToolsRepair {
		public ItemMagicGoldHoe() {
			super(MAGIC_GOLD);
			this.setTranslationKey("magicGoldHoe");
		}

		@Override
		public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
			return false;
		}

		@Override
		public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand,
				EnumFacing facing, float hitX, float hitY, float hitZ) {
			EnumActionResult result = super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
			if (result == EnumActionResult.SUCCESS) {
				if (ESAPI.silent.isSilent(player, SilentLevel.RELEASE)) return EnumActionResult.SUCCESS;
				ItemStack itemstack = player.getHeldItem(hand);
				int originDmg = itemstack.getItemDamage();
				int size = 2;
				for (int x = -size; x <= size; x++) {
					for (int z = -size; z <= size; z++) {
						if (x == 0 && z == 0) continue;
						BlockPos at = pos.add(x, 0, z);
						result = super.onItemUse(player, worldIn, at, hand, facing, hitX, hitY, hitZ);
						itemstack.setItemDamage(originDmg);
						if (worldIn.isRemote && result == EnumActionResult.SUCCESS) {
							Vec3d position = new Vec3d(at).add(0.5, 1.3, 0.5);
							createShape(worldIn, position);
						}
					}
				}
				return EnumActionResult.SUCCESS;
			}
			return result;
		}

	}

	// 剑
	public static class ItemMagicGoldSword extends ItemSword implements MagicGoldToolsRepair {
		public ItemMagicGoldSword() {
			super(MAGIC_GOLD);
			this.setTranslationKey("magicGoldSword");
		}

		@Override
		public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
			return false;
		}

		@Override
		public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
			if (ESAPI.silent.isSilent(attacker, SilentLevel.RELEASE)) return super.hitEntity(stack, target, attacker);
			Vec3d pos = target.getPositionVector().add(0, attacker.getEyeHeight(), 0);
			int size = 5;
			AxisAlignedBB aabb = new AxisAlignedBB(pos.x - size, pos.y - size, pos.z - size, pos.x + size, pos.y + size,
					pos.z + size);
			List<EntityLiving> list = attacker.world.getEntitiesWithinAABB(EntityLiving.class, aabb);
			for (EntityLiving entity : list) {
				if (entity == target) continue;
				DamageSource s = DamageSource.causeThornsDamage(attacker).setMagicDamage();
				entity.attackEntityFrom(s, MAGIC_GOLD.getAttackDamage() / 2.0f);
			}
			if (!attacker.world.isRemote) {
				NBTTagCompound nbt = FireworkEffect.fastNBT(1, 2, 0.5f, TileMDBase.PARTICLE_COLOR,
						TileMDBase.PARTICLE_COLOR_FADE);
				Effects.spawnEffect(attacker.world, Effects.FIREWROK, pos, nbt);
			}
			return super.hitEntity(stack, target, attacker);
		}
	}
}

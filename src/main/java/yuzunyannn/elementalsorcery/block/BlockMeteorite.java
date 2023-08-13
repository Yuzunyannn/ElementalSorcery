package yuzunyannn.elementalsorcery.block;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemMultiTexture;
import net.minecraft.item.ItemMultiTexture.Mapper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.elf.ElfTime;
import yuzunyannn.elementalsorcery.elf.ElfTime.Period;
import yuzunyannn.elementalsorcery.entity.elf.EntityElfBase;
import yuzunyannn.elementalsorcery.potion.PotionMeteoriteDisease;
import yuzunyannn.elementalsorcery.util.helper.EntityHelper;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class BlockMeteorite extends Block implements Mapper {

	public ItemBlock getItemBlock() {
		return new ItemMultiTexture(this, this, BlockMeteorite.this) {
			@Override
			public boolean onEntityItemUpdate(EntityItem entityItem) {
				BlockMeteorite.this.onEntityItemUpdate(entityItem);
				return super.onEntityItemUpdate(entityItem);
			}

			@Override
			public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
				BlockMeteorite.this.onEntityInventoryUpdate(stack, entityIn);
			}
		};
	}

	public static final PropertyEnum<EnumType> VARIANT = PropertyEnum.<EnumType>create("variant", EnumType.class);

	public BlockMeteorite() {
		super(Material.ROCK);
		this.setHarvestLevel("pickaxe", 3);
		this.setTranslationKey("meteorite");
		this.setHardness(32);
		this.setTickRandomly(true);
	}

	@Override
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
		for (BlockElfPlank.EnumType type : BlockElfPlank.EnumType.values())
			items.add(new ItemStack(this, 1, type.getMetadata()));
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(VARIANT, EnumType.byMetadata(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(VARIANT).getMetadata();
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { VARIANT });
	}

	@Override
	public String apply(ItemStack var1) {
		return "s";
	}

	static public enum EnumType implements IStringSerializable {
		NORMAL("normal"),
		BLOCK("block");

		final String name;

		EnumType(String name) {
			this.name = name;
		}

		@Override
		public String getName() {
			return name;
		}

		public int getMetadata() {
			return this.ordinal();
		}

		static public EnumType byMetadata(int meta) {
			return EnumType.values()[0x1 & meta];
		}
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		EnumType variant = state.getValue(VARIANT);
		if (variant != EnumType.BLOCK) return;

		boolean success = tryGrowAround(worldIn, pos);
		double infectStrength = 32;

		if (success) {
			infectStrength *= 64;
			if (rand.nextDouble() <= 1 / 8f) worldIn.setBlockState(pos, state.withProperty(VARIANT, EnumType.NORMAL));
		}

		infectAround(worldIn, new Vec3d(pos).add(0.5, 0.5, 0.5), infectStrength * 8);
	}

	public void onEntityItemUpdate(EntityItem entityItem) {
		if (entityItem.ticksExisted % 40 == 0) {
			ItemStack stack = entityItem.getItem();
			if (stack.getMetadata() != EnumType.BLOCK.getMetadata()) return;
			infectAround(entityItem.world, entityItem.getPositionVector(), 8 * stack.getCount());
		}
	}

	public void onEntityInventoryUpdate(ItemStack stack, Entity player) {
		if (player instanceof EntityLivingBase && player.ticksExisted % 120 == 0) {
			if (stack.getMetadata() != EnumType.BLOCK.getMetadata()) infect((EntityLivingBase) player, 0, 1);
			else infect((EntityLivingBase) player, 0, 6 * stack.getCount());
		}
	}

	public static void infectAround(World world, Vec3d pos, double strength) {
		double r = MathHelper.clamp(strength / 16, 2, 4);
		AxisAlignedBB aabb = WorldHelper.createAABB(pos, r, r, r);
		List<EntityLivingBase> list = world.getEntitiesWithinAABB(EntityLivingBase.class, aabb);
		for (EntityLivingBase living : list) {
			double distance = living.getPositionEyes(0).distanceTo(pos);
			infect(living, distance, strength / 4);
		}
	}

	public static void infect(EntityLivingBase living, double distance, double strength) {
		if (living.world.isRemote) return;

		if (EntityHelper.isCreative(living)) return;

		if (living.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD) return;
		if (!living.isNonBoss()) return;
		if (living instanceof EntityElfBase) return;

		ElfTime time = new ElfTime(living.world);

		if (time.at(Period.NOON)) return;
		double baseProbability = 1;

		if (time.at(Period.DAY)) baseProbability = 0.025;
		else if (time.at(Period.MIDNIGHT)) baseProbability = 0.25;
		else baseProbability = 0.1;

		if (distance < 1) strength = strength * (1 - distance) * 4;
		else strength = strength / distance;

		baseProbability = MathHelper.sqrt(strength) * baseProbability;

		if (baseProbability < living.getRNG().nextDouble()) return;

		int addLevel = 1;

		if (baseProbability > 1) {
			int intp = MathHelper.floor(baseProbability);
			addLevel += intp - 1;
			if (baseProbability - intp > living.getRNG().nextDouble()) addLevel++;
		}

		((PotionMeteoriteDisease) ESObjects.POTIONS.METEORITE_DISEASE).deepen(living, addLevel);
	}

	public static boolean tryGrowAround(World worldIn, BlockPos pos) {
		int rIndex = worldIn.rand.nextInt(6);
		int length = EnumFacing.VALUES.length;
		for (int i = 0; i < length; i++) {
			EnumFacing facing = EnumFacing.VALUES[(rIndex + i) % length];
			BlockPos at = pos.offset(facing);
			if (!worldIn.isAirBlock(at)) {
				IBlockState state = worldIn.getBlockState(at);
				if (state.getBlock() != ESObjects.BLOCKS.METEORITE_DRUSE) break;
				continue;
			}
			{
				at = at.offset(facing);
				IBlockState state = worldIn.getBlockState(at);
				if (state.getBlock() != Blocks.END_ROD) break;
			}
			{
				at = at.offset(facing);
				IBlockState state = worldIn.getBlockState(at);
				if (state.getBlock() != ESObjects.BLOCKS.ESTONE_PRISM) break;
			}
			if (i <= length / 2) continue;

			at = pos.offset(facing);
			IBlockState meteoriteDruseState = ESObjects.BLOCKS.METEORITE_DRUSE.getDefaultState();
			meteoriteDruseState = meteoriteDruseState.withProperty(BlockMeteoriteDruse.FACING, facing);
			worldIn.setBlockState(at, meteoriteDruseState);
			return true;
		}
		return false;
	}

}

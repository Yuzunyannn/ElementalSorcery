package yuzunyannn.elementalsorcery.block;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemMultiTexture;
import net.minecraft.item.ItemMultiTexture.Mapper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.advancement.ESCriteriaTriggers;
import yuzunyannn.elementalsorcery.config.Config;
import yuzunyannn.elementalsorcery.elf.ElfTime;
import yuzunyannn.elementalsorcery.entity.EntityFallingElfFruit;
import yuzunyannn.elementalsorcery.event.EventServer;
import yuzunyannn.elementalsorcery.init.ESInit;

public class BlockElfFruit extends Block implements Mapper {

	@Config
	public static float ELF_FRUIT_GEN_PROBABILITY = 0.1f;

	@Config
	public static float ELF_FRUIT_DROP_PROBABILITY = 0.2f;

	public ItemBlock getItemBlock() {
		// 可以吃的
		return new ItemMultiTexture(this, this, this) {

			@Override
			public int getMaxItemUseDuration(ItemStack stack) {
				return 32;
			}

			@Override
			public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
				if (entityLiving instanceof EntityPlayer) {
					EntityPlayer entityplayer = (EntityPlayer) entityLiving;
					entityplayer.getFoodStats().addStats(1, 0.6f);
					worldIn.playSound((EntityPlayer) null, entityplayer.posX, entityplayer.posY, entityplayer.posZ,
							SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5F,
							worldIn.rand.nextFloat() * 0.1F + 0.9F);
					this.onFoodEaten(stack, worldIn, entityplayer);
					entityplayer.addStat(StatList.getObjectUseStats(this));
					if (entityplayer instanceof EntityPlayerMP)
						CriteriaTriggers.CONSUME_ITEM.trigger((EntityPlayerMP) entityplayer, stack);
				}
				stack.shrink(1);
				return stack;
			}

			@Override
			public EnumAction getItemUseAction(ItemStack stack) {
				return EnumAction.EAT;
			}

			@Override
			public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
				ItemStack stack = playerIn.getHeldItem(handIn);
				// if (stack.getItemDamage() != MAX_STATE)
				// return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);
				playerIn.setActiveHand(handIn);
				return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
			}

			protected void onFoodEaten(ItemStack stack, World worldIn, EntityPlayer player) {
				if (player.world.isRemote) return;
				NBTTagCompound nbt = EventServer.getPlayerNBT(player);
				if (!nbt.hasKey("elfFruit", 10)) nbt.setTag("elfFruit", new NBTTagCompound());
				nbt = nbt.getCompoundTag("elfFruit");
				int count = Math.max(nbt.getInteger("times"), 1);
				long time = nbt.getLong("time");
				long currTime = System.currentTimeMillis();
				if (currTime - time > 10 * 1000) count = 1;
				final int durationIn = 10 * 20;
				switch (count) {
				case 3:
					player.addPotionEffect(new PotionEffect(MobEffects.SPEED, durationIn, 0));
					break;
				case 5:
					player.addPotionEffect(new PotionEffect(MobEffects.SPEED, durationIn, 1));
					player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, durationIn, 0));
					break;
				case 8:
					if (player instanceof EntityPlayerMP)
						ESCriteriaTriggers.EAT_ELF_FRUIT.trigger((EntityPlayerMP) player);
					player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, durationIn, 1));
					player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, durationIn, 0));
					break;
				case 11:
					player.addPotionEffect(new PotionEffect(MobEffects.WITHER, durationIn * 2, 1));
					player.addPotionEffect(new PotionEffect(MobEffects.POISON, durationIn * 2, 1));
					player.addPotionEffect(new PotionEffect(MobEffects.LEVITATION, durationIn, 0));
					break;
				default:
					break;
				}
				nbt.setInteger("times", count + 1);
				nbt.setLong("time", currTime);
			}
		};
	}

	public static final AxisAlignedBB AABB = new AxisAlignedBB(0.1875, 0.0, 0.1875, 0.8125, 0.75, 0.8125);
	public static final int MAX_STATE = 2;
	public static final PropertyInteger STAGE = PropertyInteger.create("stage", 0, MAX_STATE);

	public BlockElfFruit() {
		super(Material.PLANTS);
		this.setTranslationKey("elfFruit");
		this.setTickRandomly(true);
		Blocks.FIRE.setFireInfo(this, 20, 5);
		this.setDefaultState(this.blockState.getBaseState().withProperty(STAGE, 0));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { STAGE });
	}

	@Override
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
		items.add(new ItemStack(this, 1, 2));
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return AABB.offset(state.getOffset(source, pos));
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		return AABB.offset(blockState.getOffset(worldIn, pos));
	}

	@Override
	public EnumOffsetType getOffsetType() {
		return Block.EnumOffsetType.XZ;
	}

	@Override
	public String apply(ItemStack stack) {
		if (stack.getMetadata() == MAX_STATE) return "s" + MAX_STATE;
		return "s0";
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(STAGE, meta);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(STAGE);
	}

	@Override
	public int damageDropped(IBlockState state) {
		return this.getMetaFromState(state);
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		// 能被水冲走
		if (worldIn.getBlockState(fromPos).getMaterial().isLiquid()) {
			this.dropBlockAsItem(worldIn, pos, state, 0);
			worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
		} // 头上没东西，就掉下去
		this.falling(worldIn, pos, state);
	}

	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
		this.falling(worldIn, pos, state);
	}

	@Override
	public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
		this.falling(worldIn, pos, state, true);
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state,
			int fortune) {
		// 满成长后才会掉落
		if (state.getValue(STAGE) >= MAX_STATE) super.getDrops(drops, world, pos, state, fortune);
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		if (worldIn.isRemote) return;
		int growState = state.getValue(STAGE);
		if (growState < MAX_STATE) {
			// 检测上面是否为树叶
			if (worldIn.getBlockState(pos.up()).getBlock() != ESInit.BLOCKS.ELF_LEAF) return;
			// if (!worldIn.getBlockState(pos.up()).getValue(BlockElfLeaf.DECAYABLE))
			// return;
			if (rand.nextFloat() < 0.5f) return;
			ElfTime time = new ElfTime(worldIn);
			if (!time.at(ElfTime.Period.DAY)) return;
			worldIn.setBlockState(pos, state.withProperty(STAGE, growState + 1));
		} else {
			if (rand.nextFloat() > ELF_FRUIT_DROP_PROBABILITY) return;
			this.falling(worldIn, pos, state, true);
		}
	}

	protected void falling(World world, BlockPos pos, IBlockState state) {
		this.falling(world, pos, state, false);
	}

	protected void falling(World world, BlockPos pos, IBlockState state, boolean force) {
		if (world.isRemote) return;
		if (world.isAirBlock(pos.down()) && (force || !world.isBlockFullCube(pos.up()))) {
			EntityFallingElfFruit falling = new EntityFallingElfFruit(world, pos);
			world.spawnEntity(falling);
		}
	}

	// 颜色变化
	@SideOnly(Side.CLIENT)
	public IBlockColor getBlockColor() {
		return new IBlockColor() {
			public int colorMultiplier(IBlockState state, @Nullable IBlockAccess worldIn, @Nullable BlockPos pos,
					int tintIndex) {
				switch (state.getValue(BlockElfFruit.STAGE)) {
				case 0:
					return 0xbb44ff;
				case 1:
					return 0x7777ff;
				}
				return 0xffffff0;
			}
		};
	}

}

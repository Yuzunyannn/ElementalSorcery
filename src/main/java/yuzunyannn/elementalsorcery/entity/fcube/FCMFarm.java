package yuzunyannn.elementalsorcery.entity.fcube;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.elf.ElfTime;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.util.ESFakePlayer;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;
import yuzunyannn.elementalsorcery.util.helper.NBTHelper;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class FCMFarm extends FairyCubeModule {

	@FairyCubeModuleRecipe
	public static boolean matchAndConsumeForCraft(World world, BlockPos pos, IElementInventory inv) {
		ElfTime time = new ElfTime(world);
		if (!time.at(ElfTime.Period.DAWN)) return false;
		return matchAndConsumeForCraft(world, pos, inv,
				ItemHelper.toList(Items.WHEAT, 32, Items.CARROT, 32, Items.POTATO, 32),
				ElementHelper.toList(ESInit.ELEMENTS.WOOD, 75, 20));
	}

	public FCMFarm(EntityFairyCube fairyCube) {
		super(fairyCube);
		this.setPriority(PriorityType.EXECUTER);
		this.setStatusCount(2);
		this.setElementNeedPerExp(new ElementStack(ESInit.ELEMENTS.WOOD, 8, 50), 16);
	}

	int executeMode = 0;
	ItemStack executeStack = ItemStack.EMPTY;
	Entity executeTarget;
	BlockPos executePos;

	public static boolean isShear(ItemStack stack) {
		return stack.getItem() == Items.SHEARS;
	}

	public static boolean canShear(Object obj, ItemStack shear, World world, BlockPos pos) {
		if (obj instanceof IShearable) {
			if (((IShearable) obj).isShearable(shear, world, pos)) return true;
		}
		return false;
	}

	@Override
	public void onUpdate(EntityLivingBase master, IFairyCubeMaster fairyCubeMaster) {
		Behavior behaviorBase = fairyCubeMaster.getRecentBehavior(master);
		if (behaviorBase == null) return;
		if (master.isSneaking()) return;
		if (behaviorBase.is("entity", "interact")) {
			BehaviorInteract behavior = behaviorBase.to(BehaviorInteract.class);
			if (behavior == null) return;
			Entity target = behavior.getTarget();
			ItemStack stack = master.getHeldItem(behavior.getHand());
			executeMode = 0;
			if (isShear(stack)) {
				if (target instanceof IShearable) executeMode = 0x01;
			} else if (target instanceof EntityAnimal) {
				EntityAnimal animal = (EntityAnimal) target;
				if (animal.isBreedingItem(stack)) executeMode = 0x02;
			}
			if (executeMode == 0) return;
			executeTarget = target;
			executeStack = stack;
			fairyCube.setLookAt(target);
			fairyCube.doExecute(40);
		} else if (behaviorBase.is("block", "place")) {
			BehaviorBlock behavior = behaviorBase.to(BehaviorBlock.class);
			if (behavior == null) return;
			ItemStack stack = master.getHeldItem(EnumHand.MAIN_HAND);
			if (stack.getItem() instanceof ItemHoe) {
				executeMode = 0x11;
				executePos = behavior.getTargetPos();
				executeStack = stack.copy();
				executeStack.setItemDamage(0);
				fairyCube.setLookAt(executePos);
				fairyCube.doExecute(40);
				return;
			}
			IBlockState state = behavior.getTargetState();
			if (!(state.getBlock() instanceof IGrowable)) return;
			if (!(stack.getItem() instanceof IPlantable)) return;
			executeMode = 0x10;
			executeStack = stack;
			executePos = behavior.getTargetPos();
			fairyCube.setLookAt(executePos);
			fairyCube.doExecute(40);
		}

	}

	@Override
	public void onFailExecute() {
		executeStack = ItemStack.EMPTY;
		executeTarget = null;
		executePos = null;
	}

	private List<EntityLivingBase> getTargets(EntityLivingBase master, float range, ItemStack stack, Entity target) {
		int status = this.getCurrStatus();
		World world = master.world;
		AxisAlignedBB aabb = WorldHelper.createAABB(master.getPositionVector(), range, 8, 2);
		return world.getEntitiesWithinAABB(EntityLivingBase.class, aabb, (entity) -> {
			if (executeMode == 0x01) {
				boolean canShear = entity instanceof IShearable;
				if (status == 1) return canShear;
				if (canShear && target.getClass().equals(entity.getClass())) {
					if (target instanceof EntitySheep)
						return ((EntitySheep) target).getFleeceColor() == ((EntitySheep) entity).getFleeceColor();
					return true;
				}
			} else if (executeMode == 0x02) {
				if (entity instanceof EntityAnimal) {
					EntityAnimal animal = (EntityAnimal) target;
					if (status == 1) return animal.isBreedingItem(stack);
					return target.getClass().equals(entity.getClass())
							&& (animal.isChild() == ((EntityAnimal) (target)).isChild());
				}
			}
			return false;
		});

	}

	private boolean canPlace(World world, ItemStack plant, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		Item item = plant.getItem();
		if (item instanceof IPlantable)
			return state.getBlock().canSustainPlant(state, world, pos, EnumFacing.UP, (IPlantable) item)
					&& world.isAirBlock(pos.up());
		return false;
	}

	private BlockPos findAPlace(World world, ItemStack plant, BlockPos pos) {
		if (canPlace(world, plant, pos.down())) return pos.down();
		pos = pos.up(3);
		for (int y = 0; y < 6; y++) {
			if (canPlace(world, plant, pos)) return pos;
			pos = pos.down();
		}
		return null;
	}

	@Override
	public void onStartExecute(EntityLivingBase master) {
		ItemStack stack = executeStack;
		executeStack = ItemStack.EMPTY;
		Entity target = executeTarget;
		executeTarget = null;
		BlockPos pos = executePos;
		executePos = null;

		int status = this.getCurrStatus();

		int level = this.getLevelUsed();
		float range = FCMAttackRange.commonRange(level);
		World world = master.world;

		ItemStack copyStack = ItemStack.EMPTY;
		EntityPlayer player;
		if (master instanceof EntityPlayer) {
			player = (EntityPlayer) master;
			if (player.isCreative() || executeMode == 0x11) copyStack = stack.copy();
		} else {
			player = ESFakePlayer.get((WorldServer) world);
			player.setHeldItem(EnumHand.MAIN_HAND, stack.copy());
		}

		if (executeMode == 0x01) {
			List<EntityLivingBase> shearables = this.getTargets(master, range, stack, target);
			float luck = this.getLuck();
			for (EntityLivingBase living : shearables) {
				if (!canShear(living, stack, world, living.getPosition())) continue;
				List<ItemStack> drops = ((IShearable) living).onSheared(stack.copy(), world, living.getPosition(),
						(int) luck);
				Random rand = living.getRNG();
				for (ItemStack drop : drops) {
					EntityItem ent = living.entityDropItem(drop, 1.0F);
					ent.motionY += rand.nextFloat() * 0.05F;
					ent.motionX += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
					ent.motionZ += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
				}
			}
		} else if (executeMode == 0x02) {
			if (stack != master.getHeldItem(EnumHand.MAIN_HAND)) return;

			List<EntityLivingBase> breeds = this.getTargets(master, range, stack, target);
			if (master instanceof EntityPlayer && ((EntityPlayer) master).isCreative()) copyStack = stack.copy();
			for (EntityLivingBase living : breeds) {
				EntityAnimal animal = (EntityAnimal) living;
				animal.processInteract(player, EnumHand.MAIN_HAND);
				if (stack.isEmpty()) {
					if (!copyStack.isEmpty()) {
						stack = copyStack.copy();
						player.setHeldItem(EnumHand.MAIN_HAND, stack);
					} else break;
				}
			}

			if (!copyStack.isEmpty()) player.setHeldItem(EnumHand.MAIN_HAND, copyStack);

		} else if (executeMode == 0x10 || executeMode == 0x11) {
			if (stack != master.getHeldItem(EnumHand.MAIN_HAND) && executeMode != 0x11) return;

			int size = Math.max(1, (int) range / 2);
			for (int x = -size; x <= size; x++) over: {
				for (int z = -size; z <= size; z++) {
					if (x == 0 && z == 0) continue;
					if (executeMode == 0x11) {
						BlockPos at = pos.add(x, 0, z);
						EnumActionResult result = stack.onItemUse(player, world, at, EnumHand.MAIN_HAND, EnumFacing.UP,
								at.getX() + 0.5f, at.getY() + 1f, at.getZ() + 0.5f);
						if (result != EnumActionResult.SUCCESS && status == 1) {
							IBlockState state = world.getBlockState(at.up());
							if (state.getBlock().isReplaceable(world, at.up())) {
								world.setBlockToAir(at.up());
								stack.onItemUse(player, world, at, EnumHand.MAIN_HAND, EnumFacing.UP, at.getX() + 0.5f,
										at.getY() + 1f, at.getZ() + 0.5f);
							}
						}
					} else {
						BlockPos at = findAPlace(world, stack, pos.add(x, 0, z));
						if (at == null) continue;
						stack.onItemUse(player, world, at, EnumHand.MAIN_HAND, EnumFacing.UP, at.getX() + 0.5f,
								at.getY() + 1f, at.getZ() + 0.5f);
					}
					if (stack.isEmpty()) {
						if (!copyStack.isEmpty()) stack = copyStack.copy();
						else break over;
					}
				}
			}
			if (!copyStack.isEmpty()) {
				player.setHeldItem(EnumHand.MAIN_HAND, copyStack);
				if (executeMode == 0x11 && !player.isCreative()) copyStack.damageItem(1, player);
			}
		} else {
			this.fairyCube.stopExecute();
			return;
		}

		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setByte("Mode", (byte) executeMode);
		nbt.setFloat("R", range);
		if (target != null) nbt.setInteger("E", target.getEntityId());
		if (pos != null) NBTHelper.setBlockPos(nbt, "P", pos);
		this.sendToClient(nbt);
	}

	@SideOnly(Side.CLIENT)
	public void onRecv(NBTTagCompound nbt) {
		int[] colors = new int[] { 0xb5f4de, 0xf4d112, 0xadea1c, 0x7e9802 };
		fairyCube.doClientSwingArm(40, colors);
		executeMode = nbt.getInteger("Mode");
		float range = nbt.getFloat("R");

		ItemStack stack = ItemStack.EMPTY;
		EntityLivingBase master = fairyCube.getMaster();
		if (master != null) stack = master.getHeldItem(EnumHand.MAIN_HAND);

		if (executeMode < 0x10) {
			Entity target = fairyCube.world.getEntityByID(nbt.getInteger("E"));
			if (target == null) return;
			List<EntityLivingBase> entities = this.getTargets(master, range, stack, target);
			for (EntityLivingBase living : entities) fairyCube.doClientEntityEffect(living, colors);
		} else {
			int size = Math.max(1, (int) range / 2);
			BlockPos pos = NBTHelper.getBlockPos(nbt, "P");
			List<BlockPos> list = new ArrayList<>();
			for (int x = -size; x <= size; x++) {
				for (int z = -size; z <= size; z++) {
					if (x == 0 && z == 0) continue;
					BlockPos at;
					if (executeMode == 0x11) {
						at = pos.add(x, 0, z);
						if (!fairyCube.world.isAirBlock(at.up())) continue;
						if (fairyCube.world.isAirBlock(at)) continue;
					} else at = findAPlace(fairyCube.world, stack, pos.add(x, 0, z));
					if (at == null) continue;
					list.add(at.up());
				}
			}
			fairyCube.doClientCastingBlock(list, colors);
		}
	}

	@Override
	public String getStatusUnlocalizedValue(int status) {
		if (status == 1) return "fairy.cube.attack.normal";
		if (status == 2) return "fairy.cube.farm.accurate";
		return super.getStatusUnlocalizedValue(status);
	}

}

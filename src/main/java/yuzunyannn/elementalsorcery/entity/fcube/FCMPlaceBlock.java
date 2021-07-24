package yuzunyannn.elementalsorcery.entity.fcube;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.util.ESFakePlayer;
import yuzunyannn.elementalsorcery.util.NBTHelper;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class FCMPlaceBlock extends FairyCubeModule {

	@FairyCubeModuleRecipe
	public static boolean matchAndConsumeForCraft(World world, BlockPos pos, IElementInventory inv) {
		Biome biome = world.getBiome(pos);
		if (biome != Biomes.PLAINS) return false;
		return matchAndConsumeForCraft(world, pos, inv, ItemHelper.toList(Blocks.DIRT, 256, Items.ENDER_PEARL, 8),
				ElementHelper.toList(ESInit.ELEMENTS.ENDER, 128, 400, ESInit.ELEMENTS.AIR, 64, 10));
	}

	public FCMPlaceBlock(EntityFairyCube fairyCube) {
		super(fairyCube);
		this.setPriority(PriorityType.EXECUTER);
		this.setStatusCount(2);
		this.setElementNeedPerExp(new ElementStack(ESInit.ELEMENTS.ENDER, 8, 100), 16);
	}

	public RayTraceResult executeRay;
	public EnumHand executeHand;

	@Override
	public void onUpdate(EntityLivingBase master, IFairyCubeMaster fairyCubeMaster) {
		Behavior behaviorBase = fairyCubeMaster.getRecentBehavior(master);
		if (behaviorBase == null) return;
		if (master.isSneaking()) return;
		if (!behaviorBase.is("click", "right")) return;
		BehaviorClick behavior = behaviorBase.to(BehaviorClick.class);
		if (behavior == null) return;
		if (master.isHandActive()) return;

		ItemStack stack = master.getHeldItem(behavior.getHand());
		if (stack.isEmpty()) return;

		World world = master.world;
		int level = this.getLevelUsed();
		int dis = Math.min(16 + level * 4, 96);
		RayTraceResult ray = WorldHelper.getLookAtBlock(world, master, dis);
		if (ray == null) return;
		if (ray.hitVec.distanceTo(master.getPositionVector()) < 4) return;

		executeRay = ray;
		executeHand = behavior.getHand();

		int status = this.getCurrStatus();
		fairyCube.setLookAt(executeRay.getBlockPos());
		int addTime = (16 / (level + 1));
		if (status == 2) fairyCube.doExecute(16 + addTime * 8);
		else fairyCube.doExecute(2 + addTime);
	}

	@Override
	public void onStartExecute(EntityLivingBase master) {
		int status = this.getCurrStatus();
		World world = master.world;
		ItemStack stack = master.getHeldItem(executeHand);
		RayTraceResult ray = executeRay;
		executeRay = null;

		ItemStack copyStack = ItemStack.EMPTY;
		EntityPlayer player;
		if (master instanceof EntityPlayer) {
			player = (EntityPlayer) master;
			if (player.isCreative()) copyStack = stack.copy();
		} else {
			player = ESFakePlayer.get((WorldServer) world);
			ItemStack fakeStack = stack.copy();
			player.setHeldItem(executeHand, fakeStack);
		}

		int count = 0;
		BlockPos pos = ray.getBlockPos();
		EnumFacing facing = ray.sideHit;
		List<BlockPos> list = new ArrayList<>();

		while (true) {
			EnumActionResult result = stack.onItemUse(player, world, pos, executeHand, ray.sideHit,
					(float) ray.hitVec.x, (float) ray.hitVec.y, (float) ray.hitVec.z);
			pos = pos.offset(facing);
			list.add(pos);
			if (result == EnumActionResult.FAIL || result == EnumActionResult.PASS) break;
			if (status == 2) {
				if (++count >= 8) break;
			} else {
				++count;
				break;
			}
			if (stack.isEmpty()) break;
		}

		if (count == 0) {
			fairyCube.stopExecute();
			return;
		}
		if (!copyStack.isEmpty()) player.setHeldItem(executeHand, copyStack);
		int level = this.getLevelUsed();
		int addTime = (16 / (level + 1));

		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("D", status == 2 ? (16 + addTime * 8) : (2 + addTime));
		NBTHelper.setBlockPosList(nbt, "P", list);
		this.sendToClient(nbt);
	}

	@Override
	public void onFailExecute() {
		executeRay = null;
		executeHand = null;
	}

	@SideOnly(Side.CLIENT)
	public void onRecv(NBTTagCompound nbt) {
		int duration = nbt.getInteger("D");
		int[] colors = new int[] { 0xb5f4de, 0xcc00fa, 0x74008e };
		fairyCube.doClientSwingArm(duration, colors);
		List<BlockPos> list = NBTHelper.getBlockPosList(nbt, "P");
		fairyCube.doClientCastingBlock(list, colors);
	}

	@Override
	public String getStatusUnlocalizedValue(int status) {
		if (status == 1) return "fairy.cube.place.single";
		if (status == 2) return "fairy.cube.place.series";
		return super.getStatusUnlocalizedValue(status);
	}

}

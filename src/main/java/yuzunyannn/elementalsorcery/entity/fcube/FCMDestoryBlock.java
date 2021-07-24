package yuzunyannn.elementalsorcery.entity.fcube;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.util.ESFakePlayer;
import yuzunyannn.elementalsorcery.util.NBTHelper;
import yuzunyannn.elementalsorcery.util.RandomHelper;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

public class FCMDestoryBlock extends FairyCubeModule {

	@FairyCubeModuleRecipe
	public static boolean matchAndConsumeForCraft(World world, BlockPos pos, IElementInventory inv) {
		if (pos.getY() > 32) return false;
		return matchAndConsumeForCraft(world, pos, inv, ItemHelper.toList(Blocks.STONE, 32),
				ElementHelper.toList(ESInit.ELEMENTS.EARTH, 100, 25));
	}

	public FCMDestoryBlock(EntityFairyCube fairyCube) {
		super(fairyCube);
		this.setPriority(PriorityType.EXECUTER);
		this.setStatusCount(2);
		this.setElementNeedPerExp(new ElementStack(ESInit.ELEMENTS.EARTH, 10, 75), 16);
	}

	public List<BlockPos> executeList;

	@Override
	public void onUpdate(EntityLivingBase master, IFairyCubeMaster fairyCubeMaster) {
		Behavior behaviorBase = fairyCubeMaster.getRecentBehavior(master);
		if (behaviorBase == null) return;
		if (master.isSneaking()) return;
		if (!behaviorBase.is("block", "harvest")) return;
		BehaviorBlock behavior = behaviorBase.to(BehaviorBlock.class);
		if (behavior == null) return;

		int status = this.getCurrStatus();

		BlockPos pos = behavior.getTargetPos();
		IBlockState state = behavior.getTargetState();
		List<BlockPos> list = null;

		int level = this.getLevelUsed();
		int maxCount = (int) (Math.pow(level, 1.05f) * 4 + 4);

		if (status == 1) {
			list = findBlockAroundRandom(fairyCube, maxCount, state, pos, master.getPosition().down());
		} else if (status == 2) {
			RayTraceResult result = getOrient(pos, master);
			if (result == null) return;
			list = findBlockDirect(fairyCube, maxCount, state, pos, result.sideHit);
		}

		if (list == null || list.isEmpty()) return;

		executeList = list;
		fairyCube.setLookAt(pos);
		fairyCube.doExecute(20);
	}

	@Override
	public void onStartExecute(EntityLivingBase master) {
		World world = fairyCube.world;
		List<BlockPos> list = executeList;
		executeList = null;

		boolean silkHarvest = fairyCube.hasAttribute("silk");
		int fortune = silkHarvest ? 0 : (int) fairyCube.getAttribute("fortune");
		for (BlockPos pos : list) {
			if (silkHarvest || fortune > 0) {
				ItemStack silkTool = new ItemStack(Items.WOODEN_PICKAXE);
				FakePlayer fakePlayer = ESFakePlayer.get((WorldServer) world);
				if (silkHarvest) silkTool.addEnchantment(Enchantments.SILK_TOUCH, 1);
				else ItemHelper.addEnchantment(silkTool, Enchantments.FORTUNE, fortune);
				IBlockState iblockstate = world.getBlockState(pos);
				Block block = iblockstate.getBlock();
				block.harvestBlock(world, fakePlayer, pos, iblockstate, null, silkTool);
				world.setBlockToAir(pos);
				world.playEvent(2001, pos, Block.getStateId(iblockstate));
			} else world.destroyBlock(pos, true);
		}

		NBTTagCompound nbt = new NBTTagCompound();
		NBTHelper.setBlockPosList(nbt, "P", list);
		this.sendToClient(nbt);
	}

	@Override
	public void onFailExecute() {
		executeList = null;
	}

	@SideOnly(Side.CLIENT)
	public void onRecv(NBTTagCompound nbt) {
		int[] colors = new int[] { 0xb5f4de, 0x785439, 0x133435 };
		fairyCube.doClientSwingArm(20, colors);
		List<BlockPos> list = NBTHelper.getBlockPosList(nbt, "P");
		fairyCube.doClientCastingBlock(list, colors);
	}

	public RayTraceResult getOrient(BlockPos pos, EntityLivingBase master) {
		AxisAlignedBB aabb = new AxisAlignedBB(pos);
		Vec3d vs = master.getPositionEyes(1);
		Vec3d ve = vs.add(master.getLookVec().scale(64));// new Vec3d(pos).addVector(0.5, 0.5, 0.5);
		return aabb.calculateIntercept(vs, ve);
	}

	public List<BlockPos> findBlockAroundRandom(EntityFairyCube fairyCube, int maxCount, IBlockState state,
			BlockPos center, BlockPos masterFoot) {
		World world = fairyCube.world;
		List<BlockPos> list = new ArrayList<>();
		int range = (int) (Math.pow(maxCount, 1 / 3f) / 2.f) + 1;
		int total = (range * 2) + 1;
		total = total * total * total;
		for (int x = -range; x <= range; x++) {
			for (int y = -range; y <= range; y++) {
				for (int z = -range; z <= range; z++) {
					BlockPos pos = center.add(x, y, z);
					IBlockState findState = world.getBlockState(pos);
					if (pos.equals(masterFoot)) continue;
					if (findState == state) list.add(pos);
				}
			}
		}
		if (list.size() <= maxCount) return list;
		BlockPos[] data = RandomHelper.randomSelect(maxCount, list.toArray(new BlockPos[list.size()]));
		return Arrays.asList(data);
	}

	public List<BlockPos> findBlockDirect(EntityFairyCube fairyCube, int maxCount, IBlockState state, BlockPos center,
			EnumFacing facing) {
		World world = fairyCube.world;
		List<BlockPos> list = new ArrayList<>();
		int length = (int) (maxCount / 9f) + 1;
		Function<BlockPos, Boolean> add = p -> {
			IBlockState findState = world.getBlockState(p);
			if (findState == state) list.add(p);
			if (list.size() >= maxCount) return true;
			return false;
		};
		if (facing == EnumFacing.WEST) {
			for (int x = 0; x <= length; x++) for (int y = -1; y <= 1; y++)
				for (int z = -1; z <= 1; z++) if (add.apply(center.add(x, y, z))) return list;
		} else if (facing == EnumFacing.EAST) {
			for (int x = 0; x >= -length; x--) for (int y = -1; y <= 1; y++)
				for (int z = -1; z <= 1; z++) if (add.apply(center.add(x, y, z))) return list;
		} else if (facing == EnumFacing.NORTH) {
			for (int x = -1; x <= 1; x++) for (int y = -1; y <= 1; y++)
				for (int z = 0; z <= length; z++) if (add.apply(center.add(x, y, z))) return list;
		} else if (facing == EnumFacing.SOUTH) {
			for (int x = -1; x <= 1; x++) for (int y = -1; y <= 1; y++)
				for (int z = 0; z >= -length; z--) if (add.apply(center.add(x, y, z))) return list;
		} else if (facing == EnumFacing.DOWN) {
			for (int x = -1; x <= 1; x++) for (int y = 0; y <= length; y++)
				for (int z = -1; z <= 1; z++) if (add.apply(center.add(x, y, z))) return list;
		} else if (facing == EnumFacing.UP) {
			for (int x = -1; x <= 1; x++) for (int y = 0; y >= -length; y--)
				for (int z = -1; z <= 1; z++) if (add.apply(center.add(x, y, z))) return list;
		}

		return list;
	}

	@Override
	public String getStatusUnlocalizedValue(int status) {
		if (status == 1) return "fairy.cube.destoryBlock.random";
		if (status == 2) return "fairy.cube.destoryBlock.direct";
		return super.getStatusUnlocalizedValue(status);
	}

}

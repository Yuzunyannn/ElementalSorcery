package yuzunyannn.elementalsorcery.entity.fcube;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.util.ESFakePlayer;
import yuzunyannn.elementalsorcery.util.RandomHelper;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;
import yuzunyannn.elementalsorcery.util.render.RenderHelper;

public class FCMDestoryBlock extends FairyCubeModule {

	public FCMDestoryBlock(EntityFairyCube fairyCube) {
		super(fairyCube);
		this.setPriority(PriorityType.EXECUTER);
		this.setStatusCount(2);
		this.setElementNeedPerExp(new ElementStack(ESInit.ELEMENTS.EARTH, 10, 75), 16);
	}

	public BlockPos masterLastPosAt;
	public List<BlockPos> executeList;

	@Override
	public void onUpdate(EntityLivingBase master, IFairyCubeMaster fairyCubeMaster) {
		Behavior behavior = fairyCubeMaster.getRecentBehavior(master);
		if (behavior == null) return;
		if (master.isSneaking()) return;
		if (!behavior.is("harvest", "block")) return;
		int status = this.getCurrStatus();

		BlockPos pos = behavior.getTargetPos();
		BlockPos orient = tryGetOrient(pos, masterLastPosAt);
		masterLastPosAt = pos;
		IBlockState state = behavior.getTargetState();
		List<BlockPos> list = null;

		int level = this.getLimitLevel();
		int maxCount = (int) (Math.pow(level, 1.05f) * 4 + 4);

		if (status == 1) {
			list = findBlockAroundRandom(fairyCube, maxCount, state, pos);
		} else if (status == 2) {
			if (orient == null) return;
			list = findBlockDirect(fairyCube, maxCount, state, pos, orient);
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

		boolean silkHarvest = fairyCube.getAttribute("silk") > 0;
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
		fairyCube.letsCastingToBlock(20, new int[] { 0xb5f4de, 0x785439, 0x133435 }, list);
	}

	@Override
	public void onFailExecute() {
		executeList = null;
	}

	public BlockPos tryGetOrient(BlockPos nowPos, BlockPos origin) {
		if (origin == null) return null;
		BlockPos tar = nowPos.subtract(origin);
		if (tar.getX() == 0 && tar.getY() == 0) return tar;
		if (tar.getY() == 0 && tar.getZ() == 0) return tar;
		if (tar.getX() == 0 && tar.getZ() == 0) return tar;
		return null;
	}

	public static EnumFacing toFace(BlockPos pos) {
		if (pos.getX() == 1) return EnumFacing.NORTH;
		return EnumFacing.NORTH;
	}

	public List<BlockPos> findBlockAroundRandom(EntityFairyCube fairyCube, int maxCount, IBlockState state,
			BlockPos center) {
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
					if (findState == state) list.add(pos);
				}
			}
		}
		if (list.size() <= maxCount) return list;
		BlockPos[] data = RandomHelper.randomSelect(maxCount, list.toArray(new BlockPos[list.size()]));
		return Arrays.asList(data);
	}

	public List<BlockPos> findBlockDirect(EntityFairyCube fairyCube, int maxCount, IBlockState state, BlockPos center,
			BlockPos orient) {
		World world = fairyCube.world;
		List<BlockPos> list = new ArrayList<>();
		int length = (int) (maxCount / 9f) + 1;
		Function<BlockPos, Boolean> add = p -> {
			IBlockState findState = world.getBlockState(p);
			if (findState == state) list.add(p);
			if (list.size() >= maxCount) return true;
			return false;
		};
		if (orient.getX() > 0) {
			for (int x = 0; x <= length; x++) for (int y = -1; y <= 1; y++)
				for (int z = -1; z <= 1; z++) if (add.apply(center.add(x, y, z))) return list;
		} else if (orient.getX() < 0) {
			for (int x = 0; x >= -length; x--) for (int y = -1; y <= 1; y++)
				for (int z = -1; z <= 1; z++) if (add.apply(center.add(x, y, z))) return list;
		} else if (orient.getZ() > 0) {
			for (int x = -1; x <= 1; x++) for (int y = -1; y <= 1; y++)
				for (int z = 0; z <= length; z++) if (add.apply(center.add(x, y, z))) return list;
		} else if (orient.getZ() < 0) {
			for (int x = -1; x <= 1; x++) for (int y = -1; y <= 1; y++)
				for (int z = 0; z >= -length; z--) if (add.apply(center.add(x, y, z))) return list;
		} else if (orient.getY() > 0) {
			for (int x = -1; x <= 1; x++) for (int y = 0; y <= length; y++)
				for (int z = -1; z <= 1; z++) if (add.apply(center.add(x, y, z))) return list;
		} else if (orient.getY() < 0) {
			for (int x = -1; x <= 1; x++) for (int y = 0; y >= -length; y--)
				for (int z = -1; z <= 1; z++) if (add.apply(center.add(x, y, z))) return list;
		}

		return list;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onRenderGUIIcon() {
		RenderHelper.drawTexturedRectInCenter(0, -6, 32, 32, 53, 55, 32, 32, 256, 256);
	}

	@Override
	public String getStatusUnlocalizedValue(int status) {
		if (status == 1) return "fairy.cube.destoryBlock.random";
		if (status == 2) return "fairy.cube.destoryBlock.direct";
		return super.getStatusUnlocalizedValue(status);
	}

}

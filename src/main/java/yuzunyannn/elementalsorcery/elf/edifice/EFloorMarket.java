package yuzunyannn.elementalsorcery.elf.edifice;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockWoodSlab;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.elf.pro.ElfProfession;
import yuzunyannn.elementalsorcery.elf.pro.ElfProfessionMerchant;
import yuzunyannn.elementalsorcery.elf.pro.merchant.ElfMerchantType;
import yuzunyannn.elementalsorcery.elf.trade.Trade;
import yuzunyannn.elementalsorcery.elf.trade.TradeList;
import yuzunyannn.elementalsorcery.entity.EntityItemGoods;
import yuzunyannn.elementalsorcery.entity.elf.EntityElf;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.tile.TileElfTreeCore;
import yuzunyannn.elementalsorcery.util.var.VariableSet;

public class EFloorMarket extends ElfEdificeFloor {

	@Override
	public NBTTagCompound createBuildData(IBuilder builder, Random rand) {
		return new NBTTagCompound();
	}

	@Override
	public int getFloorHeight(IBuilder builder) {
		return 5;
	}

	@Override
	public int getInvestWeight() {
		return 100;
	}

	@Override
	public int getMaxCountInTree(TileElfTreeCore core) {
		return 1;
	}

	@Override
	public void build(IBuilder builder) {
		BlockPos pos = builder.getFloorBasicPos();
		int treeSize = builder.getEdificeSize();
		BuilderHelper helper = new BuilderHelper(builder);
		int high = this.getFloorHeight(builder);
		// 地毯
		helper.genCarpet(EnumDyeColor.BLUE);
		{
			IBlockState DSLAB = Blocks.DOUBLE_WOODEN_SLAB.getDefaultState();
			IBlockState SLAB = Blocks.WOODEN_SLAB.getDefaultState().withProperty(BlockWoodSlab.HALF,
					BlockSlab.EnumBlockHalf.TOP);
			EnumFacing facing = EnumFacing.NORTH;
			EnumFacing rFacing = facing.rotateY();
			int size = treeSize / 2;
			for (int n = 2; n <= size + 1; n++) {
				for (int j = -1; j <= 1; j++) {
					if (Math.abs(j) == 1 && (n == 2 || n == size + 1)) {
						builder.setBlockState(pos.offset(facing, size + j).offset(rFacing, n), DSLAB);
						builder.setBlockState(pos.offset(facing, size + j).offset(rFacing, -n), DSLAB);
						builder.setBlockState(pos.offset(facing.getOpposite(), size + j).offset(rFacing, n), DSLAB);
						builder.setBlockState(pos.offset(facing.getOpposite(), size + j).offset(rFacing, -n), DSLAB);
					} else {
						builder.setBlockState(pos.offset(facing, size + j).offset(rFacing, n), SLAB);
						builder.setBlockState(pos.offset(facing, size + j).offset(rFacing, -n), SLAB);
						builder.setBlockState(pos.offset(facing.getOpposite(), size + j).offset(rFacing, n), SLAB);
						builder.setBlockState(pos.offset(facing.getOpposite(), size + j).offset(rFacing, -n), SLAB);
					}
				}
			}

			// 吊灯
			for (int i = 0; i <= size; i++) {
				IBlockState GLOWSTONE = Blocks.GLOWSTONE.getDefaultState();
				int n = GenElfEdifice.getFakeCircleLen(i, 0, 2);
				builder.setBlockState(pos.add(n, high - 1, 0), GLOWSTONE);
				builder.setBlockState(pos.add(-n, high - 1, 0), GLOWSTONE);
				builder.setBlockState(pos.add(0, high - 1, n), GLOWSTONE);
				builder.setBlockState(pos.add(0, high - 1, -n), GLOWSTONE);
			}

		}
		// 后面一排树叶
		{
			IBlockState LEAF = ESInit.BLOCKS.ELF_LEAF.getDefaultState().withProperty(BlockLeaves.DECAYABLE, false);
			IBlockState LAMP = Blocks.LIT_REDSTONE_LAMP.getDefaultState();
			IBlockState REDSTONE = Blocks.REDSTONE_BLOCK.getDefaultState();
			int size = treeSize;
			int y = 0;
			for (int i = -size + 1; i < size; i++) {
				int n = GenElfEdifice.getFakeCircleLen(size, i, 2) - 1;
				int test = Math.abs(i);
				if (test == size - 1 || test == 0) {
					builder.setBlockState(pos.add(i, y, n), REDSTONE);
					builder.setBlockState(pos.add(i, y, -n), REDSTONE);
					builder.setBlockState(pos.add(n, y, i), REDSTONE);
					builder.setBlockState(pos.add(-n, y, i), REDSTONE);
					builder.setBlockState(pos.add(i, y + 1, n), LAMP);
					builder.setBlockState(pos.add(i, y + 1, -n), LAMP);
					builder.setBlockState(pos.add(n, y + 1, i), LAMP);
					builder.setBlockState(pos.add(-n, y + 1, i), LAMP);
					builder.setBlockState(pos.add(i, high - y - 1, n), REDSTONE);
					builder.setBlockState(pos.add(i, high - y - 1, -n), REDSTONE);
					builder.setBlockState(pos.add(n, high - y - 1, i), REDSTONE);
					builder.setBlockState(pos.add(-n, high - y - 1, i), REDSTONE);
					builder.setBlockState(pos.add(i, high - y - 2, n), LAMP);
					builder.setBlockState(pos.add(i, high - y - 2, -n), LAMP);
					builder.setBlockState(pos.add(n, high - y - 2, i), LAMP);
					builder.setBlockState(pos.add(-n, high - y - 2, i), LAMP);
				} else {
					builder.setBlockState(pos.add(i, y, n), LEAF);
					builder.setBlockState(pos.add(i, y, -n), LEAF);
					builder.setBlockState(pos.add(n, y, i), LEAF);
					builder.setBlockState(pos.add(-n, y, i), LEAF);
					builder.setBlockState(pos.add(i, high - y - 1, n), LEAF);
					builder.setBlockState(pos.add(i, high - y - 1, -n), LEAF);
					builder.setBlockState(pos.add(n, high - y - 1, i), LEAF);
					builder.setBlockState(pos.add(-n, high - y - 1, i), LEAF);
				}
			}
		}

	}

	@Override
	public void surprise(IBuilder builder, Random rand) {
		spawnGoods(builder, rand);
	}

	protected void spawnGoods(IBuilder builder, Random rand) {
		int treeSize = builder.getEdificeSize();
		World world = builder.getWorld();
		BlockPos pos = builder.getFloorBasicPos();
		EnumFacing facing = EnumFacing.NORTH;
		EnumFacing rFacing = facing.rotateY();
		int size = treeSize / 2;
		VariableSet set = new VariableSet();
		ElfMerchantType type = ElfMerchantType.getRandomMerchantType(rand);
		type.renewTrade(world, pos, rand, set);
		Trade trade = type.getTrade(set);
		if (trade == null) return;
		ArrayList<TradeList.TradeInfo> goods = new ArrayList<>(trade.getTradeListSize());
		for (int i = 0; i < trade.getTradeListSize(); i++) {
			TradeList.TradeInfo info = trade.getTradeInfo(i);
			if (info.isReclaim()) continue;
			ItemStack itemStack = info.getCommodity();
			if (itemStack.isEmpty()) continue;
			goods.add(info);
		}
		if (goods.isEmpty()) return;
		for (int n = 2; n <= size + 1; n++) {
			for (int j = -1; j <= 1; j++) {
				trySpawnGoods(world, pos.offset(facing, size + j).offset(rFacing, n), rand, goods);
				trySpawnGoods(world, pos.offset(facing, size + j).offset(rFacing, -n), rand, goods);
				trySpawnGoods(world, pos.offset(facing.getOpposite(), size + j).offset(rFacing, n), rand, goods);
				trySpawnGoods(world, pos.offset(facing.getOpposite(), size + j).offset(rFacing, -n), rand, goods);
			}
		}
	}

	protected void trySpawnGoods(World world, BlockPos pos, Random rand, ArrayList<TradeList.TradeInfo> goods) {
		if (rand.nextFloat() > 0.333f) return;
		double xoff = rand.nextDouble() * 0.75 - 0.75 / 2;
		double zoff = rand.nextDouble() * 0.75 - 0.75 / 2;
		Vec3d vec = new Vec3d(pos).add(0.5, 1.5, 0.5).add(xoff, 0, zoff);
		TradeList.TradeInfo info = goods.get(rand.nextInt(goods.size()));
		EntityItemGoods goodsEntity = EntityItemGoods.dropGoods(world, vec, info.getCommodity(), info.getCost(), true);
		goodsEntity.setRelativeVec(vec);
	}

	@Override
	public void spawn(IBuilder builder) {
		BuilderHelper helper = new BuilderHelper(builder);
		TileElfTreeCore core = helper.treeCore();
		if (core == null) return;
		Random rand = core.getRand();
		for (int i = 0; i < 16; i += 2) {
			Vec3d vec = EFloorHall.trySpawnElfGetPos(builder, 12);
			if (vec != null) {
				EntityElf elf = new EntityElf(builder.getWorld(), false);
				Random merchantRandom = ElfProfession.getRandomFromName(elf.getCustomNameTag());
				elf.getProfessionStorage().set(ElfProfession.M_TYPE,
						ElfMerchantType.getRandomMerchantType(merchantRandom));
				ElfProfessionMerchant.setRemainTimeBeforeLeave(elf, (int) ((20 * 60) * (1 + rand.nextFloat() * 3)));
				elf.setPosition(vec.x, vec.y, vec.z);
				builder.spawn(elf);
				elf.setProfession(ElfProfession.MERCHANT);
				i--;
			} else if (i > 3) break;
		}
		spawnGoods(builder, rand);
	}
}

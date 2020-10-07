package yuzunyannn.elementalsorcery.elf.edifice;

import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.elf.pro.ElfProfession;
import yuzunyannn.elementalsorcery.entity.EntityBulletin;
import yuzunyannn.elementalsorcery.entity.elf.EntityElf;
import yuzunyannn.elementalsorcery.entity.elf.EntityElfBase;
import yuzunyannn.elementalsorcery.tile.TileElfTreeCore;
import yuzunyannn.elementalsorcery.util.block.BlockHelper;

public class EFloorHall extends ElfEdificeFloor {

	static public final EFloorHall instance = new EFloorHall();

	/** 大厅是第一个层，不能被投资 */
	@Override
	public boolean canInvest(TileElfTreeCore core) {
		return false;
	}

	@Override
	public int getFloorHeight(IBuilder builder) {
		return 10;
	}

	@Override
	public NBTTagCompound getBuildData(IBuilder builder, Random rand) {
		return new BuilderHelper(builder).toward(rand).getNBT();
	}

	@Override
	public void build(IBuilder builder) {
		BlockPos pos = builder.getFloorBasicPos();
		int treeSize = builder.getEdificeSize();
		BuilderHelper helper = new BuilderHelper(builder);
		int size = GenElfEdifice.getFakeCircleLen(treeSize, 0, 2);
		// 地毯
		helper.genCarpet(EnumDyeColor.WHITE);
		// 门
		EnumFacing toward = helper.toward();
		EnumFacing towardOpposite = toward.getOpposite();
		EnumFacing towardRY = toward.rotateY();
		BlockPos doorPos = pos.offset(toward, size);
		IBlockState AIR = Blocks.AIR.getDefaultState();
		for (int i = -1; i <= 1; i++) {
			for (int y = 0; y < 3; y++) builder.setBlockState(doorPos.up(y).offset(toward.rotateY(), i), AIR);
		}
		// 中心地毯
		IBlockState GLOWSTONE = Blocks.GLOWSTONE.getDefaultState();
		size = treeSize;
		int offset = GenElfEdifice.getFakeCircleLen(treeSize, treeSize, 2);
		IBlockState CARPET = helper.blockCarpet(EnumDyeColor.RED);
		for (int i = -1; i <= 1; i++) {
			int n = GenElfEdifice.getFakeCircleLen(treeSize, i, 2);
			for (int a = 0; a <= n; a++) {
				BlockPos at = pos.offset(toward, a).offset(toward.rotateY(), i);
				builder.setBlockState(at, CARPET);
				if (a % 4 == 0 && i != 0) builder.setBlockState(at.down(), GLOWSTONE);
			}
			for (int a = 0; a <= offset; a++)
				builder.setBlockState(pos.offset(toward, -a).offset(toward.rotateY(), i), CARPET);

		}
		// 台子
		IBlockState WOOD = helper.blockPlank();
		size = size - 1;
		IBlockState GLASS_PANE = Blocks.GLASS_PANE.getDefaultState();
		IBlockState FENCE = helper.blockFence();
		for (int i = -size; i <= size; i++) {
			for (int y = 0; y <= 3; y++) {
				BlockPos at = pos.offset(towardOpposite, offset).offset(toward.rotateY(), i);
				int test = Math.abs(i);
				if (y == 0) {
					builder.setBlockState(at, WOOD);
					if (test == 1 || test == 3 || test == 5) {
						at = at.offset(towardOpposite, 1);
						builder.setBlockState(at.up(y), WOOD);
					}
				} else if (y == 3) {
					if (test == 0) builder.setBlockState(at.up(y), GLOWSTONE);
					else builder.setBlockState(at.up(y), WOOD);
				} else {
					if (test == 1 || test == 3 || test == 5) {
						builder.setBlockState(at.up(y), WOOD);
						at = at.offset(towardOpposite, 1);
						builder.setBlockState(at.up(y), FENCE);
					} else if (test == 0 || test == 4) {
						if (y == 2) builder.setBlockState(at.up(y), GLASS_PANE);
					} else builder.setBlockState(at.up(y), GLASS_PANE);
				}
			}
		}
		// 伪二层
		size = GenElfEdifice.getFakeCircleLen(treeSize, 0, 2);
		int len = 2;
		int ln = GenElfEdifice.getFakeCircleLen(treeSize, -1, 2);
		for (int i = -1; i < size; i++) {
			int n = GenElfEdifice.getFakeCircleLen(treeSize, i, 2);
			len = len + n - ln + 1;
			ln = n;
			for (int a = 0; a < n; a++) {
				BlockPos at = pos.offset(toward, i).up(6);
				if (i < 3) {
					if (n - a > len) continue;
					at = at.offset(toward.rotateY(), -a);
					if (i == -1) builder.setBlockState(at.up(), FENCE);
					if (n - a == len) {
						builder.setBlockState(at.up(), FENCE);
						builder.setBlockState(at.up().offset(toward), FENCE);
					}
					builder.setBlockState(at, WOOD);
				} else {
					if (i == 3) {
						if (n - a >= len) builder.setBlockState(at.offset(towardRY, -a).up(), FENCE);
						if (n - a >= 4) {
							builder.setBlockState(at.offset(towardRY, a).up(), FENCE);
							if (n - a == 4) {
								BlockPos p = at.offset(towardRY, a);
								builder.setBlockState(p.offset(towardOpposite, 1), WOOD);
								builder.setBlockState(p.offset(towardOpposite, 1).up(), FENCE);
							}
						}
					}
					builder.setBlockState(at.offset(towardRY, a), WOOD);
					builder.setBlockState(at.offset(towardRY, -a), WOOD);
				}
			}
		}
		// 楼梯
		IBlockState STAIRS = helper.blockStairs(toward);
		size = treeSize - 2;
		ln = GenElfEdifice.getFakeCircleLen(treeSize, 2, 2);
		len = 6;
		for (int i = 2; i >= -6 + 2; i--) {
			int n = GenElfEdifice.getFakeCircleLen(treeSize, i, 2);
			for (int a = ln - 3; a < n; a++) {
				BlockPos at = pos.offset(toward, i).up(len);
				builder.setBlockState(at.offset(towardRY, a), STAIRS);
			}
			len--;
		}
		// 告示板
		IBlockState REDSTONE_BLOCK = Blocks.REDSTONE_BLOCK.getDefaultState();
		IBlockState REDSTONE_LAMP = Blocks.REDSTONE_LAMP.getDefaultState();
		ln = treeSize + 1;
		size = GenElfEdifice.getFakeCircleLen(treeSize, ln, 2) - 1;
		for (int i = -size; i <= size; i++) {
			for (int y = 7; y < 10; y++) {
				BlockPos at = pos.offset(toward, ln).up(y);
				int test = Math.abs(i);
				if (test >= 3 && test % 3 == 0) {
					if (y == 8) builder.setBlockState(at.offset(towardRY, i), REDSTONE_BLOCK);
					else builder.setBlockState(at.offset(towardRY, i), REDSTONE_LAMP);
				} else {
					builder.setBlockState(at.offset(towardRY, i), WOOD);
				}
			}
		}
		// 装饰品
		size = treeSize * 3 / 4;
		// 吊灯
		{
			helper.genLamp(pos.offset(toward, size).up(5), 1);
			helper.genLamp(pos.offset(towardOpposite, 0).up(9), 1);
			helper.genLamp(pos.offset(towardOpposite, 2).offset(towardRY, treeSize / 2).up(9), 1);
			helper.genLamp(pos.offset(towardOpposite, 2).offset(towardRY, -treeSize / 2).up(9), 1);
		}
		// 旁侧灯
		offset = GenElfEdifice.getFakeCircleLen(treeSize, 0, 2) - 1;
		{
			BlockPos at = pos.offset(towardRY, offset);
			helper.genLeafLamp(at);
			at = pos.offset(towardRY, -offset);
			helper.genLeafLamp(at);
		}
		// 一队桌子和椅子
		{
			for (offset = -1; offset <= 1; offset++) {
				BlockPos at = pos.offset(towardRY, offset - 5);
				if (offset == -1) {
					builder.setBlockState(at.offset(toward, 2),
							STAIRS.withProperty(BlockStairs.FACING, towardOpposite.rotateY()));
					builder.setBlockState(at.offset(toward, -2),
							STAIRS.withProperty(BlockStairs.FACING, towardOpposite.rotateY()));
				} else if (offset == 1) {
					builder.setBlockState(at.offset(toward, 2), STAIRS.withProperty(BlockStairs.FACING, towardRY));
					builder.setBlockState(at.offset(toward, -2), STAIRS.withProperty(BlockStairs.FACING, towardRY));
				} else {
					builder.setBlockState(at.offset(toward, 2), STAIRS);
					builder.setBlockState(at.offset(toward, -2),
							STAIRS.withProperty(BlockStairs.FACING, towardOpposite));
				}
				builder.setBlockState(at, WOOD);
			}
		}
		// 箱子
		{
			IBlockState CHEST = Blocks.CHEST.getDefaultState();
			CHEST = CHEST.withProperty(BlockChest.FACING, towardRY.getOpposite());
			BlockPos at = getChestPos(builder);
			builder.setBlockState(at, CHEST);
		}
	}

	private BlockPos getChestPos(IBuilder builder) {
		BlockPos pos = builder.getFloorBasicPos();
		int treeSize = builder.getEdificeSize();
		BuilderHelper helper = new BuilderHelper(builder);
		EnumFacing toward = helper.toward();
		int n = GenElfEdifice.getFakeCircleLen(treeSize, 3, 2);
		BlockPos at = pos.offset(toward, 3).offset(toward.rotateY(), n - 1);
		return at;
	}

	@Override
	public void surprise(IBuilder builder, Random rand) {
		BlockPos at = getChestPos(builder);
		World world = builder.getWorld();
		TileEntityChest chest = BlockHelper.getTileEntity(world, at, TileEntityChest.class);
		if (chest != null) {
			ResourceLocation loot = new ResourceLocation(ElementalSorcery.MODID, "hall/es_hall");
			chest.setLootTable(loot, rand.nextLong());
		}
		this.createBulltin(builder);
	}

	public static List<EntityElfBase> getFloorElf(IBuilder builder, ElfProfession pro) {
		BlockPos pos = builder.getFloorBasicPos();
		World world = builder.getWorld();
		ElfEdificeFloor floor = builder.getFloorType();
		int size = GenElfEdifice.getFakeCircleLen(builder.getEdificeSize(), 0, 2);
		double x = pos.getX() + 0.5;
		double y = pos.getY();
		double z = pos.getZ() + 0.5;
		AxisAlignedBB aabb = new AxisAlignedBB(x - size, y, z - size, x + size, y + floor.getFloorHeight(builder),
				z + size);
		return world.getEntitiesWithinAABB(EntityElfBase.class, aabb, (elf) -> {
			if (pro == null) return true;
			return elf.getProfession() == pro;
		});
	}

	@Override
	public void spawn(IBuilder builder) {
		List<EntityElfBase> elfs = EFloorHall.getFloorElf(builder, ElfProfession.RECEPTIONIST);
		if (elfs.size() >= 3) return;
		World world = builder.getWorld();
		BlockPos pos = builder.getFloorBasicPos();
		int treeSize = builder.getEdificeSize();
		BuilderHelper helper = new BuilderHelper(builder);
		EnumFacing toward = helper.toward();
		// 接待员
		int offset = GenElfEdifice.getFakeCircleLen(treeSize, treeSize, 2) + 1;
		for (int i = -4; i <= 4; i += 4) {
			BlockPos at = pos.offset(toward.getOpposite(), offset).offset(toward.rotateY(), i);
			float x = at.getX() + 0.5f;
			float y = at.getY() + 0.5f;
			float z = at.getZ() + 0.5f;
			AxisAlignedBB aabb = new AxisAlignedBB(x - 1, y - 1, z - 1, x + 1, y + 1, z + 1);
			elfs = world.getEntitiesWithinAABB(EntityElfBase.class, aabb);
			if (!elfs.isEmpty()) continue;
			EntityElf elf = new EntityElf(world, ElfProfession.RECEPTIONIST);
			elf.setPosition(x, y - 0.5, z);
			builder.spawn(elf);
		}
		// 其余单位
		ElfProfession pro = null;
		if (world.rand.nextInt(5) == 0) pro = ElfProfession.BUILDER;
		EFloorLivingRoom.trySpawnElf(builder, pro, 6);
	}

	public void createBulltin(IBuilder builder) {
		BlockPos pos = builder.getFloorBasicPos();
		int treeSize = builder.getEdificeSize();
		BuilderHelper helper = new BuilderHelper(builder);
		EnumFacing toward = helper.toward();
		World world = builder.getWorld();
		BlockPos at = pos.offset(toward, treeSize).up(8);
		EntityBulletin bulltin = new EntityBulletin(world);
		bulltin.setPosition(at, toward.getOpposite());
		if (bulltin.onValidSurface()) world.spawnEntity(bulltin);
	}

	public EntityBulletin findBulletin(IBuilder builder) {
		BlockPos pos = builder.getFloorBasicPos();
		int treeSize = builder.getEdificeSize();
		BuilderHelper helper = new BuilderHelper(builder);
		EnumFacing toward = helper.toward();
		World world = builder.getWorld();
		BlockPos at = pos.offset(toward, treeSize).up(8);
		AxisAlignedBB aabb = new AxisAlignedBB(at);
		List<EntityBulletin> list = world.getEntitiesWithinAABB(EntityBulletin.class, aabb);
		if (list.isEmpty()) return null;
		return list.get(0);
	}
}

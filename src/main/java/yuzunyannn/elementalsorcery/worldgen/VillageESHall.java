package yuzunyannn.elementalsorcery.worldgen;

import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraft.world.gen.structure.StructureVillagePieces.PieceWeight;
import net.minecraft.world.gen.structure.StructureVillagePieces.Start;
import net.minecraft.world.gen.structure.StructureVillagePieces.Village;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.init.LootRegister;

public class VillageESHall extends Village {

	public VillageESHall() {
	}

	public VillageESHall(StructureVillagePieces.Start start, int type, Random rand, StructureBoundingBox box,
			EnumFacing facing) {
		super(start, type);
		this.setCoordBaseMode(facing);
		this.boundingBox = box;
	}

	public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
		if (this.averageGroundLvl < 0) {
			this.averageGroundLvl = this.getAverageGroundLevel(worldIn, structureBoundingBoxIn);
			if (this.averageGroundLvl < 0) { return true; }
			this.boundingBox.offset(0, this.averageGroundLvl - this.boundingBox.maxY + 6 - 1, 0);
		}

		IBlockState blockStone = this.getBiomeSpecificBlockState(Blocks.COBBLESTONE.getDefaultState());
		IBlockState blockLog = this.getBiomeSpecificBlockState(Blocks.LOG.getDefaultState());
		IBlockState blockLogX = this.getBiomeSpecificBlockState(
				Blocks.LOG.getDefaultState().withProperty(BlockLog.LOG_AXIS, BlockLog.EnumAxis.X));
		IBlockState blockPlanks = this.getBiomeSpecificBlockState(Blocks.PLANKS.getDefaultState());
		IBlockState blockGlassPane = this.getBiomeSpecificBlockState(Blocks.GLASS_PANE.getDefaultState());
		IBlockState blockGlass = this.getBiomeSpecificBlockState(Blocks.GLASS.getDefaultState());
		IBlockState blockStair = this.getBiomeSpecificBlockState(Blocks.STONE_STAIRS.getDefaultState());
		IBlockState blockWoodStair = this.getBiomeSpecificBlockState(Blocks.OAK_STAIRS.getDefaultState())
				.withProperty(BlockStairs.FACING, EnumFacing.SOUTH);
		IBlockState blockSlab = this.getBiomeSpecificBlockState(Blocks.STONE_SLAB.getDefaultState());
		IBlockState blockCarpet = this.getBiomeSpecificBlockState(Blocks.CARPET.getDefaultState());
		// 底板，墙
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 0, 1, 12, 0, 10, blockPlanks, blockPlanks, false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 1, 1, 4, 10, blockStone, blockStone, false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 12, 1, 1, 12, 4, 10, blockStone, blockStone, false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 10, 12, 4, 10, blockStone, blockStone, false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 10, 12, 4, 10, blockStone, blockStone, false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 1, 10, 10, 3, 10, blockPlanks, blockPlanks, false);
		// 地毯
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 1, 2, 11, 1, 9, blockCarpet, blockCarpet, false);
		// 房顶
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 4, 1, 12, 4, 10, blockStone, blockStone, false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 5, 1, 12, 5, 1, blockSlab, blockSlab, false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 5, 10, 12, 5, 10, blockSlab, blockSlab, false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 5, 1, 1, 5, 10, blockSlab, blockSlab, false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 12, 5, 1, 12, 5, 10, blockSlab, blockSlab, false);
		// 玻璃
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 4, 4, 4, 9, 4, 6, blockGlass, blockGlass, false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 2, 1, 4, 3, 1, blockGlassPane, blockGlassPane, false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 9, 2, 1, 11, 3, 1, blockGlassPane, blockGlassPane, false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 2, 4, 1, 3, 6, blockGlassPane, blockGlassPane, false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 12, 2, 4, 12, 3, 6, blockGlassPane, blockGlassPane, false);
		// 原木柱子
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 0, 1, 1, 4, 1, blockLog, blockLog, false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 12, 0, 1, 12, 4, 1, blockLog, blockLog, false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 0, 10, 1, 4, 10, blockLog, blockLog, false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 12, 0, 10, 12, 4, 10, blockLog, blockLog, false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 5, 0, 1, 5, 4, 1, blockLog, blockLog, false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 8, 0, 1, 8, 4, 1, blockLog, blockLog, false);
		// 内部结构
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 1, 7, 10, 1, 7, blockLog, blockLog, false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 3, 7, 11, 3, 7, blockGlassPane, blockLog, false);
		this.setBlockState(worldIn, blockLog, 3, 2, 7, structureBoundingBoxIn);
		this.setBlockState(worldIn, blockLog, 7, 2, 7, structureBoundingBoxIn);
		this.setBlockState(worldIn, blockLog, 10, 2, 7, structureBoundingBoxIn);
		this.setBlockState(worldIn, blockGlassPane, 4, 2, 7, structureBoundingBoxIn);
		this.setBlockState(worldIn, blockGlassPane, 6, 2, 7, structureBoundingBoxIn);
		this.setBlockState(worldIn, blockGlassPane, 8, 2, 7, structureBoundingBoxIn);
		this.createVillageDoor(worldIn, structureBoundingBoxIn, randomIn, 2, 1, 7, EnumFacing.NORTH);
		this.createVillageDoor(worldIn, structureBoundingBoxIn, randomIn, 11, 1, 7, EnumFacing.NORTH);
		// 一排座位
		this.setBlockState(worldIn, blockWoodStair.withProperty(BlockStairs.FACING, EnumFacing.WEST), 2, 1, 2,
				structureBoundingBoxIn);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 3, 1, 2, 4, 1, 2, blockWoodStair, blockWoodStair, false);
		this.setBlockState(worldIn, blockWoodStair.withProperty(BlockStairs.FACING, EnumFacing.EAST), 5, 1, 2,
				structureBoundingBoxIn);

		this.setBlockState(worldIn, blockWoodStair.withProperty(BlockStairs.FACING, EnumFacing.WEST), 8, 1, 2,
				structureBoundingBoxIn);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 9, 1, 2, 10, 1, 2, blockWoodStair, blockWoodStair, false);
		this.setBlockState(worldIn, blockWoodStair.withProperty(BlockStairs.FACING, EnumFacing.EAST), 11, 1, 2,
				structureBoundingBoxIn);
		// 窗口座位
		this.setBlockState(worldIn, blockWoodStair, 5, 1, 5, structureBoundingBoxIn);
		this.setBlockState(worldIn, blockWoodStair, 9, 1, 5, structureBoundingBoxIn);
		// 箱子
		ResourceLocation loot = LootRegister.ES_VILLAGE_HALL;
		this.generateChest(worldIn, structureBoundingBoxIn, randomIn, 11, 1, 9, loot);
		// 原木横条
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 1, 1, 4, 1, 1, blockLog, blockLog, false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 9, 1, 1, 11, 1, 1, blockLog, blockLog, false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 1, 1, 4, 1, 1, 6, blockLog, blockLog, false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 12, 1, 4, 12, 1, 6, blockLog, blockLog, false);
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 6, 3, 1, 7, 3, 1, blockLogX, blockLogX, false);
		// 石横条
		this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 4, 1, 11, 4, 1, blockStone, blockStone, false);
		// 门
		if (!this.isZombieInfested) {
			IBlockState doorState = this.biomeDoor().getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.NORTH);
			this.setBlockState(worldIn, doorState, 6, 1, 1, structureBoundingBoxIn);
			this.setBlockState(worldIn, doorState.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER), 6, 1 + 1,
					1, structureBoundingBoxIn);
			doorState = doorState.withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.RIGHT);
			this.setBlockState(worldIn, doorState, 7, 1, 1, structureBoundingBoxIn);
			this.setBlockState(worldIn, doorState.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER), 7, 1 + 1,
					1, structureBoundingBoxIn);
		}
		// 火把
		this.placeTorch(worldIn, EnumFacing.SOUTH, 5, 3, 0, structureBoundingBoxIn);
		this.placeTorch(worldIn, EnumFacing.SOUTH, 8, 3, 0, structureBoundingBoxIn);
		this.placeTorch(worldIn, EnumFacing.SOUTH, 1, 4, 0, structureBoundingBoxIn);
		this.placeTorch(worldIn, EnumFacing.SOUTH, 12, 4, 0, structureBoundingBoxIn);
		this.placeTorch(worldIn, EnumFacing.WEST, 0, 4, 1, structureBoundingBoxIn);
		this.placeTorch(worldIn, EnumFacing.EAST, 13, 4, 1, structureBoundingBoxIn);
		this.placeTorch(worldIn, EnumFacing.SOUTH, 3, 3, 9, structureBoundingBoxIn);
		this.placeTorch(worldIn, EnumFacing.SOUTH, 10, 3, 9, structureBoundingBoxIn);
		// 村民
		this.spawnVillagers(worldIn, structureBoundingBoxIn, 5, 1, 8, 1);
		this.spawnVillagers(worldIn, structureBoundingBoxIn, 9, 1, 8, 2);
		// 武器架子
		EntityArmorStand armorStand = this.setArmorStand(2, 1, 9, worldIn);
		this.randomArmor(armorStand, randomIn, worldIn);
		// 楼梯
		if (this.getBlockStateFromPos(worldIn, 6, 0, 0, structureBoundingBoxIn).getMaterial() == Material.AIR
				&& this.getBlockStateFromPos(worldIn, 6, -1, 0, structureBoundingBoxIn).getMaterial() != Material.AIR) {
			this.setBlockState(worldIn, blockStair, 6, 0, 0, structureBoundingBoxIn);
			if (this.getBlockStateFromPos(worldIn, 6, -1, 0, structureBoundingBoxIn).getBlock() == Blocks.GRASS_PATH) {
				this.setBlockState(worldIn, Blocks.GRASS.getDefaultState(), 6, -1, 0, structureBoundingBoxIn);
			}
		}
		if (this.getBlockStateFromPos(worldIn, 7, 0, 0, structureBoundingBoxIn).getMaterial() == Material.AIR
				&& this.getBlockStateFromPos(worldIn, 7, -1, 0, structureBoundingBoxIn).getMaterial() != Material.AIR) {
			this.setBlockState(worldIn, blockStair, 7, 0, 0, structureBoundingBoxIn);
			if (this.getBlockStateFromPos(worldIn, 7, -1, 0, structureBoundingBoxIn).getBlock() == Blocks.GRASS_PATH) {
				this.setBlockState(worldIn, Blocks.GRASS.getDefaultState(), 7, -1, 0, structureBoundingBoxIn);
			}
		}
		// 清理天空和地面
		for (int k = 0; k < 14; ++k) {
			for (int j = 0; j < 12; ++j) {
				this.clearCurrentPositionBlocksUpwards(worldIn, k, 6, j, structureBoundingBoxIn);
				this.replaceAirAndLiquidDownwards(worldIn, blockStone, k, -1, j, structureBoundingBoxIn);
			}
		}

		return true;
	}

	private EntityArmorStand setArmorStand(int x, int y, int z, World world) {
		BlockPos blockpos = new BlockPos(this.getXWithOffset(x, z), this.getYWithOffset(y), this.getZWithOffset(x, z));
		AxisAlignedBB box = new AxisAlignedBB(blockpos.getX(), blockpos.getY(), blockpos.getZ(), blockpos.getX() + 1,
				blockpos.getY() + 1, blockpos.getZ() + 1);
		List<EntityArmorStand> list = world.getEntitiesWithinAABB(EntityArmorStand.class, box);
		if (list != null && list.size() != 0) return list.get(0);

		EntityArmorStand armorStand = new EntityArmorStand(world, blockpos.getX() + 0.5, blockpos.getY() + 0.5,
				blockpos.getZ() + 0.5);
		byte status = armorStand.getDataManager().get(EntityArmorStand.STATUS);
		status = (byte) (status | 4);
		armorStand.getDataManager().set(EntityArmorStand.STATUS, status);
		switch (this.getCoordBaseMode()) {
		case NORTH:
			armorStand.rotationYaw = -45;
			break;
		case SOUTH:
			armorStand.rotationYaw = -135;
			break;
		case EAST:
			armorStand.rotationYaw = 45;
			break;
		case WEST:
			armorStand.rotationYaw = -45;
			break;
		default:
			break;
		}
		world.spawnEntity(armorStand);
		return armorStand;
	}

	private void randomArmor(EntityArmorStand armorStand, Random random, World world) {
		armorStand.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(Items.SKULL, 1, 3));
		armorStand.setItemStackToSlot(EntityEquipmentSlot.CHEST, new ItemStack(Items.LEATHER_CHESTPLATE));
		armorStand.setItemStackToSlot(EntityEquipmentSlot.LEGS, new ItemStack(Items.LEATHER_LEGGINGS));
		armorStand.setItemStackToSlot(EntityEquipmentSlot.FEET, new ItemStack(Items.LEATHER_BOOTS));
		armorStand.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, new ItemStack(ESObjects.BLOCKS.ESTONE));
		float rate = random.nextFloat();
		ItemStack mainHand = ItemStack.EMPTY;
		if (rate < 0.01) mainHand = new ItemStack(ESObjects.ITEMS.SPELLBOOK);
		else if (rate < 0.03) mainHand = new ItemStack(ESObjects.BLOCKS.ELEMENTAL_CUBE);
		else if (rate < 0.075) mainHand = new ItemStack(ESObjects.ITEMS.ELEMENT_CRYSTAL);
		else if (rate < 0.125) mainHand = new ItemStack(ESObjects.BLOCKS.MD_INFUSION);
		else if (rate < 0.3) mainHand = new ItemStack(ESObjects.ITEMS.PARCHMENT, 8);
		else if (rate < 0.5) mainHand = new ItemStack(ESObjects.BLOCKS.KYANITE_BLOCK, 2);
		else mainHand = new ItemStack(ESObjects.ITEMS.KYANITE_SWORD);
		armorStand.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, mainHand);
	}

	@Override
	protected VillagerProfession chooseForgeProfession(int count, VillagerProfession prof) {
		return ESObjects.VILLAGE.ES_VILLEGER;
	}

	// 注册句柄
	public static class VillageCreationHandler implements VillagerRegistry.IVillageCreationHandler {

		@Override
		public PieceWeight getVillagePieceWeight(Random random, int size) {
			return new StructureVillagePieces.PieceWeight(VillageESHall.class, 18,
					MathHelper.getInt(random, 0 + size, 0 + size));
		}

		@Override
		public Class<?> getComponentClass() {
			return VillageESHall.class;
		}

		@Override
		public Village buildComponent(PieceWeight villagePiece, Start startPiece, List<StructureComponent> pieces,
				Random random, int p1, int p2, int p3, EnumFacing facing, int p5) {
			StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(p1, p2, p3, 0,
					0, 0, 14, 6, 12, facing);
			return canVillageGoDeeper(structureboundingbox)
					&& StructureComponent.findIntersecting(pieces, structureboundingbox) == null
							? new VillageESHall(startPiece, p5, random, structureboundingbox, facing)
							: null;
		}

	}

}

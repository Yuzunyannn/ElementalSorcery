package yuzunyannn.elementalsorcery.elf.edifice;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.elf.pro.ElfProfession;
import yuzunyannn.elementalsorcery.elf.pro.ElfProfessionMerchant;
import yuzunyannn.elementalsorcery.elf.quest.Quest;
import yuzunyannn.elementalsorcery.elf.quest.QuestRewardExp;
import yuzunyannn.elementalsorcery.elf.quest.Quests;
import yuzunyannn.elementalsorcery.entity.EntityBulletin;
import yuzunyannn.elementalsorcery.tile.TileElfTreeCore;
import yuzunyannn.elementalsorcery.util.block.BlockHelper;
import yuzunyannn.elementalsorcery.util.item.ItemRec;

public class EFloorLivingRoom extends ElfEdificeFloor {

	@Override
	public int getInvestWeight() {
		return 15;
	}

	@Override
	public int getFloorHeight(IBuilder builder) {
		return 4;
	}

	@Override
	public NBTTagCompound getBuildData(IBuilder builder, Random rand) {
		return new NBTTagCompound();
	}

	@Override
	public void build(IBuilder builder) {
		BlockPos pos = builder.getFloorBasicPos();
		int treeSize = builder.getEdificeSize();
		BuilderHelper helper = new BuilderHelper(builder);
		// 地毯
		helper.genCarpet(EnumDyeColor.WHITE);
		// 床
		IBlockState WOOD = helper.blockPlank();
		IBlockState BED = Blocks.BED.getDefaultState();
		IBlockState FLOWER_POT = Blocks.FLOWER_POT.getDefaultState();
		IBlockState CHEST = Blocks.CHEST.getDefaultState();
		for (EnumFacing facing : EnumFacing.HORIZONTALS) {
			int offset = GenElfEdifice.getFakeCircleLen(treeSize, 0, 2) - 2;
			BlockPos cPos = pos.offset(facing, offset);
			builder.setBlockState(cPos, WOOD);
			int size = GenElfEdifice.getFakeCircleLen(treeSize, offset, 2) - 1;
			for (int i = -size; i <= size; i++) {
				int test = Math.abs(i);
				BlockPos at = cPos.offset(facing.rotateY(), i);
				if (test == 0 || test == 3) {
					if (test == 0) builder.setBlockState(at.offset(facing, -1),
							CHEST.withProperty(BlockChest.FACING, facing.getOpposite()));
					builder.setBlockState(at, WOOD);
					builder.setBlockState(at.up(), FLOWER_POT);
				} else if (test < 3) {
					BED = BED.withProperty(BlockBed.FACING, facing).withProperty(BlockBed.PART,
							BlockBed.EnumPartType.FOOT);
					builder.setBlockState(at.offset(facing, -1), BED);
					builder.setBlockState(at, BED.withProperty(BlockBed.PART, BlockBed.EnumPartType.HEAD));
				}
			}
			// 灯
			cPos = pos.offset(facing, treeSize - 1);
			size = GenElfEdifice.getFakeCircleLen(treeSize, treeSize - 1, 2) - 1;
			helper.genLeafLamp(cPos.offset(facing.rotateY(), size));
			// 灯2
			size = treeSize / 2;
			cPos = pos.offset(facing, size);
			helper.genLeafLamp(cPos.offset(facing.rotateY(), size));
		}
	}

	@Override
	public void surprise(IBuilder builder, Random rand) {
		BlockPos pos = builder.getFloorBasicPos();
		int treeSize = builder.getEdificeSize();
		World world = builder.getWorld();
		for (EnumFacing facing : EnumFacing.HORIZONTALS) {
			int offset = GenElfEdifice.getFakeCircleLen(treeSize, 0, 2) - 2;
			BlockPos cPos = pos.offset(facing, offset);
			int size = GenElfEdifice.getFakeCircleLen(treeSize, offset, 2) - 1;
			for (int i = -size; i <= size; i++) {
				int test = Math.abs(i);
				BlockPos at = cPos.offset(facing.rotateY(), i);
				if (test == 0 || test == 3) {
					if (test == 0) {
						// 箱子奖品
						TileEntityChest chest = BlockHelper.getTileEntity(world, at.offset(facing, -1),
								TileEntityChest.class);
						if (chest != null) setChestLoot(chest, rand);
					}
					// 随机花朵
					at = at.up();
					TileEntityFlowerPot pot = BlockHelper.getTileEntity(world, at, TileEntityFlowerPot.class);
					if (pot == null) continue;
					if (rand.nextInt(3) == 0) continue;
					BlockFlower.EnumFlowerType[] types = BlockFlower.EnumFlowerType.values();
					BlockFlower.EnumFlowerType type = types[rand.nextInt(types.length)];
					pot.setItemStack(new ItemStack(Blocks.RED_FLOWER, 1, type.getMeta()));
				}
			}
		}
	}

	public static void setChestLoot(TileEntityChest chest, Random rand) {
		if (chest == null) return;
		ResourceLocation loot = new ResourceLocation(ElementalSorcery.MODID, "hall/ingot_some");
		chest.setLootTable(loot, rand.nextLong());
	}

	@Override
	public void spawn(IBuilder builder) {
		BuilderHelper helper = new BuilderHelper(builder);
		TileElfTreeCore core = helper.treeCore();
		if (core == null) return;
		World world = builder.getWorld();
		// 刷个精灵
		switch (world.rand.nextInt(5)) {
		case 0:
			EFloorHall.trySpawnElf(builder, ElfProfession.BUILDER, 3);
			break;
		default:
			EFloorHall.trySpawnElf(builder, null, 3);
			break;
		}
		// 刷点任务
		EntityBulletin bulletin = core.getBulletin();
		if (bulletin == null || bulletin.getQuestCount() > 16) return;
		Random rand = world.rand;
		List<ItemRec> need = new LinkedList<ItemRec>();
		need.add(new ItemRec(Blocks.PLANKS, rand.nextInt(64) + 16));
		need.add(new ItemRec(Blocks.CARPET, rand.nextInt(64) + 16));
		if (rand.nextBoolean()) need.add(new ItemRec(Items.FLOWER_POT, rand.nextInt(10) + 2));
		if (rand.nextBoolean()) need.add(new ItemRec(Blocks.GLOWSTONE, rand.nextInt(7) + 1));
		int coin = rand.nextInt(50);
		for (ItemRec rec : need) {
			int price = ElfProfessionMerchant.priceIt(rec.getItemStack());
			if (price <= 0) price = 1;
			coin += rand.nextInt(price) + 1;
		}
		Quest quest = Quests.createRepair(coin, need);
		quest.getType().addReward(QuestRewardExp.create(rand.nextInt((int) (coin * 1.5)) + 10));
		quest.setEndTime(world.getWorldTime() + 24000 + rand.nextInt(24000 * 2));
		bulletin.addQuest(quest);
	}
}

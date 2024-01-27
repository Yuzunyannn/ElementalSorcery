package yuzunyannn.elementalsorcery.elf.pro;

import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import yuzunyannn.elementalsorcery.elf.ElfConfig;
import yuzunyannn.elementalsorcery.elf.talk.TalkChapter;
import yuzunyannn.elementalsorcery.elf.talk.TalkSceneSay;
import yuzunyannn.elementalsorcery.entity.EntityMagicMelting;
import yuzunyannn.elementalsorcery.entity.elf.EntityAIMoveToEntityItem;
import yuzunyannn.elementalsorcery.entity.elf.EntityAIMoveToLookBlock;
import yuzunyannn.elementalsorcery.entity.elf.EntityAIStrollAroundElfTree;
import yuzunyannn.elementalsorcery.entity.elf.EntityElfBase;
import yuzunyannn.elementalsorcery.item.ItemElfPurse;
import yuzunyannn.elementalsorcery.render.entity.living.RenderEntityElf;
import yuzunyannn.elementalsorcery.tile.TileElfTreeCore;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;
import yuzunyannn.elementalsorcery.util.helper.NBTHelper;
import yuzunyannn.elementalsorcery.util.helper.OreHelper;
import yuzunyannn.elementalsorcery.util.item.InventoryVest;
import yuzunyannn.elementalsorcery.util.item.ItemStackHandlerInventory;

public class ElfProfessionIronSmith extends ElfProfession {

	@Override
	public void initElf(EntityElfBase elf, ElfProfession origin) {
		elf.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, new ItemStack(Items.BLAZE_POWDER));
		elf.removeTask(EntityAIStrollAroundElfTree.class);
		elf.removeTask(EntityAIMoveToLookBlock.class);
		elf.removeTask(EntityAIMoveToEntityItem.class);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ResourceLocation getTexture(EntityElfBase elf) {
		return RenderEntityElf.TEXTURE_IRONSMITH;
	}

	@Override
	public boolean interact(EntityElfBase elf, EntityPlayer player) {
		elf.openTalkGui(player);
		return true;
	}

	@Override
	public TalkChapter getChapter(EntityElfBase elf, EntityPlayer player, NBTTagCompound shiftData) {
		TalkChapter superChapter = super.getChapter(elf, player, shiftData);
		if (superChapter != null) return superChapter;
		
		TileElfTreeCore core = elf.getEdificeCore();
		TalkChapter chapter = new TalkChapter();
		if (core == null) return chapter.addScene(new TalkSceneSay("say.edifice.broken"));
		if (ElfConfig.isVeryDishonest(player)) return chapter.addScene(new TalkSceneSay("say.dishonest.not.say"));
		chapter.addScene(new TalkSceneSay("say.ironsmith.info"));
		return chapter;
	}

	@Override
	public void tick(EntityElfBase elf) {
		super.tick(elf);
		if (elf.tick % 100 != 0) return;
		World world = elf.world;
		if (world.isRemote) return;
		TileElfTreeCore core = elf.getEdificeCore();
		if (core == null) return;
		NBTTagCompound data = elf.getEntityData();
		int status = data.getInteger("status");
		switch (status) {
		case 0:
			tickGetMoneyAndOre(elf);
			break;
		case 1:
			tickSmelting(elf);
		default:
			break;
		}
	}

	protected void tickGetMoneyAndOre(EntityElfBase elf) {
		if (!elf.getNavigator().noPath()) return;
		World world = elf.world;
		NBTTagCompound data = elf.getEntityData();
		BlockPos chestPos = NBTHelper.getBlockPos(data, "chestPos");
		IItemHandlerModifiable handler = findChest(chestPos, world);
		if (handler == null) return;
		if (chestPos.distanceSq(elf.getPosition()) > 3 * 3) {
			Random rand = world.rand;
			BlockPos pos = chestPos.add(rand.nextInt(3), 0, rand.nextInt(3));
			elf.getNavigator().tryMoveToXYZ(pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5, 1.25);
			return;
		}
		final int need = 64 * 2;
		int rest = ItemElfPurse.extract(new InventoryVest(handler), need, false);
		if (rest < need) elf.swingArm(EnumHand.MAIN_HAND);
		int money = need - rest;
		int origin = data.getInteger("money");
		money = origin + money;
		data.setInteger("money", money);
		if (money >= 16) {
			ItemStackHandlerInventory inv = new ItemStackHandlerInventory(2);
			inv.deserializeNBT(data.getCompoundTag("inv"));
			// 取矿
			for (int i = 0; i < handler.getSlots(); i++) {
				ItemStack ore = handler.getStackInSlot(i);
				if (!OreHelper.isOre(ore)) continue;
				int maxCount = money / 2;
				if (maxCount == 0) break;
				int rCount = Math.max(0, ore.getCount() - maxCount);
				ItemStack copyOre = ore.copy();
				ore.grow(-rCount);
				money -= ore.getCount() * 2;
				ItemStack rStack = BlockHelper.insertInto(inv, ore);
				copyOre.setCount(rStack.getCount() + rCount);
				handler.setStackInSlot(i, copyOre);
				if (elf.getHeldItem(EnumHand.MAIN_HAND).isEmpty()) {
					elf.swingArm(EnumHand.MAIN_HAND);
					elf.setHeldItem(EnumHand.MAIN_HAND, ore.copy());
				}
			}
			data.setTag("inv", inv.serializeNBT());
			data.setInteger("money", money);
			if (!inv.isEmpty()) data.setInteger("status", 1);
		}
	}

	protected void tickSmelting(EntityElfBase elf) {
		if (!elf.getNavigator().noPath()) return;
		World world = elf.world;
		NBTTagCompound data = elf.getEntityData();
		BlockPos standPos = NBTHelper.getBlockPos(data, "standPos");
		if (standPos.distanceSq(elf.getPosition()) > 2 * 2) {
			BlockPos pos = standPos;
			elf.getNavigator().tryMoveToXYZ(pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5, 1.25);
			return;
		}
		BlockPos furnacePos = NBTHelper.getBlockPos(data, "furnacePos");
		Random rand = world.rand;
		furnacePos = furnacePos.add(rand.nextInt(2), 0, rand.nextInt(2));
		if (world.getBlockState(furnacePos) != Blocks.LAVA.getDefaultState()) return;
		ItemStackHandlerInventory inv = new ItemStackHandlerInventory();
		inv.deserializeNBT(data.getCompoundTag("inv"));
		for (int i = 0; i < inv.getSlots(); i++) {
			ItemStack ore = inv.getStackInSlot(i);
			if (ore.isEmpty()) continue;
			EntityMagicMelting smlting = new EntityMagicMelting(world, furnacePos, ore);
			inv.setStackInSlot(i, ItemStack.EMPTY);
			world.spawnEntity(smlting);
			elf.swingArm(EnumHand.MAIN_HAND);
			elf.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
			break;
		}
		data.setTag("inv", inv.serializeNBT());
		if (inv.isEmpty()) data.setInteger("status", 0);
	}

	protected IItemHandlerModifiable findChest(BlockPos chestPos, World world) {
		EnumFacing facing = EnumFacing.HORIZONTALS[world.rand.nextInt(EnumFacing.HORIZONTALS.length)];
		chestPos = chestPos.offset(facing).up(world.rand.nextInt(3));
		TileEntity tile = world.getTileEntity(chestPos);
		if (tile == null) return null;
		IItemHandler itemHandler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.NORTH);
		if (itemHandler instanceof IItemHandlerModifiable) return (IItemHandlerModifiable) itemHandler;
		return null;
	}

	public void setChestPos(EntityElfBase elf, BlockPos chestPos) {
		NBTTagCompound data = elf.getEntityData();
		NBTHelper.setBlockPos(data, "chestPos", chestPos);
	}

	public void setFurnacePos(EntityElfBase elf, BlockPos furnacePos, BlockPos standPos) {
		NBTTagCompound data = elf.getEntityData();
		NBTHelper.setBlockPos(data, "furnacePos", furnacePos);
		NBTHelper.setBlockPos(data, "standPos", standPos);
	}

}

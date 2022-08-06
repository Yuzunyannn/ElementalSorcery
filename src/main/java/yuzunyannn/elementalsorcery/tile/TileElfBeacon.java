package yuzunyannn.elementalsorcery.tile;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.tile.IGetItemStack;
import yuzunyannn.elementalsorcery.elf.ElfPostOffice;
import yuzunyannn.elementalsorcery.elf.edifice.EFloorHall;
import yuzunyannn.elementalsorcery.elf.pro.ElfProfession;
import yuzunyannn.elementalsorcery.entity.elf.EntityElf;
import yuzunyannn.elementalsorcery.entity.elf.EntityElfBase;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;
import yuzunyannn.elementalsorcery.util.helper.NBTHelper;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class TileElfBeacon extends TileEntityNetwork implements IGetItemStack, ITickable {

	protected ItemStack plate = ItemStack.EMPTY;

	public void onBreak() {
		if (plate.isEmpty()) return;
		Block.spawnAsEntity(world, pos, plate);
	}

	@Override
	public boolean canSetStack(ItemStack stack) {
		return stack.getItem() == ESObjects.ITEMS.ADDRESS_PLATE;
	}

	@Override
	public void setStack(ItemStack stack) {
		this.plate = stack;
		this.updateToClient();
		this.markDirty();
	}

	@Override
	public ItemStack getStack() {
		return plate;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setTag("plate", plate.serializeNBT());
		return super.writeToNBT(compound);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		plate = new ItemStack(compound.getCompoundTag("plate"));
		super.readFromNBT(compound);
	}

	protected int tick;

	@Override
	public void update() {
		tick++;
		if (world.isRemote) {
			this.updateClient();
			return;
		}
		if (tick % 200 != 0) return;
		if (plate.isEmpty()) return;
		// 没地址走人
		String address = ElfPostOffice.getAddress(plate);
		if (address.isEmpty()) return;
		// 没包裹，走人
		ElfPostOffice postOffice = ElfPostOffice.getPostOffice(world);
		if (!postOffice.hasParcel(plate)) return;
		// 没箱子的话，走人
		BlockPos chestPos = tryFindChestAround(world, pos);
		if (chestPos == null) return;
		// 周围有精灵了，让她给我放
		EntityElfBase postmain = getPostmanAround(world, pos);
		if (postmain != null) {
			sendParcelForMe(postmain, plate, chestPos);
			return;
		}
		// 没能创建精灵，走人
		postmain = tryCreatePostman(world, pos);
		if (postmain == null) return;
		// 设置送快递
		sendParcelForMe(postmain, plate, chestPos);
	}

	// 给予精灵任务
	protected void sendParcelForMe(EntityElfBase elf, ItemStack plate, BlockPos chestPos) {
		NBTTagCompound nbt = elf.getEntityData();
		NBTHelper.setBlockPos(nbt, "chest", chestPos);
		nbt.setString("address", ElfPostOffice.getAddress(plate));
		nbt.setString("addressOwner", ElfPostOffice.getOwner(plate));
	}

	// 尝试创建一个精灵，再周围
	public static EntityElfBase tryCreatePostman(World world, BlockPos origin) {
		Random rand = world.rand;
		BlockPos pos = null;
		for (int tryTimes = 0; tryTimes < 4; tryTimes++) {
			pos = origin.add(rand.nextInt(5) - 2, -3, rand.nextInt(5) - 2);
			for (int i = 0; i < 12; i++) {
				if (EFloorHall.canSpawnElf(world, pos)) break;
				pos = pos.up();
			}
		}
		if (pos == null) return null;
		EntityElf elf = new EntityElf(world, ElfProfession.POSTMAN);
		elf.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
		world.spawnEntity(elf);
		return elf;
	}

	public static EntityElfBase getPostmanAround(World world, BlockPos pos) {
		final int size = 4;
		AxisAlignedBB aabb = new AxisAlignedBB(pos.add(-size, -size, -size), pos.add(size, size, size));
		List<EntityElfBase> list = WorldHelper.getElfWithAABB(world, aabb, ElfProfession.POSTMAN);
		if (list.isEmpty()) return null;
		return list.get(0);
	}

	public static BlockPos tryFindChestAround(World world, BlockPos pos) {
		for (EnumFacing facing : EnumFacing.VALUES) {
			IItemHandler inv = BlockHelper.getItemHandler(world, pos.offset(facing), null);
			if (inv != null) return pos.offset(facing);
		}
		return BlockHelper.tryFind(world, (w, p) -> {
			return BlockHelper.getItemHandler(w, p, null) != null;
		}, pos, 12, 5, 5);
	}

	public float animeRate = 0;
	public float prevAnimeRate = 0;

	@SideOnly(Side.CLIENT)
	public void updateClient() {
		this.prevAnimeRate = this.animeRate;
		if (plate.isEmpty()) this.animeRate = Math.max(0, this.animeRate - 0.005f);
		else this.animeRate = Math.min(1, this.animeRate + 0.005f);
	}
}

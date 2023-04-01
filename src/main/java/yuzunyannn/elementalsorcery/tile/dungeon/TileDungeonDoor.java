package yuzunyannn.elementalsorcery.tile.dungeon;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.block.BlockStainedGlass;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.mantra.SilentLevel;
import yuzunyannn.elementalsorcery.api.util.NBTTag;
import yuzunyannn.elementalsorcery.block.env.BlockDungeonDoor;
import yuzunyannn.elementalsorcery.dungeon.DungeonArea;
import yuzunyannn.elementalsorcery.dungeon.DungeonAreaDoor;
import yuzunyannn.elementalsorcery.dungeon.DungeonAreaRoom;
import yuzunyannn.elementalsorcery.dungeon.DungeonFuncGlobal;
import yuzunyannn.elementalsorcery.item.ItemMemoryFragment;
import yuzunyannn.elementalsorcery.util.helper.ColorHelper;
import yuzunyannn.elementalsorcery.util.helper.EntityHelper;

public class TileDungeonDoor extends TileDungeonBase {

	protected int doorIndex;

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		if (!this.isSending()) compound.setInteger("doorIndex", doorIndex);
		return super.writeToNBT(compound);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		this.doorIndex = compound.getInteger("doorIndex");
		super.readFromNBT(compound);
	}

	public DungeonAreaDoor getDungeonDoor() {
		DungeonAreaRoom room = this.getDungeonRoom();
		return room == null ? null : room.getDoorLink(this.doorIndex);
	}

	public void onBlockActivated(EntityPlayer player, EnumHand hand, EnumFacing facing, EnumFacing blockFacing) {
		if (world.isRemote) return;

		if (!isRunMode()) {

			return;
		}

		// 被沉默
		if (EntityHelper.checkSilent(player, SilentLevel.RELEASE)) return;

		ItemStack stack = player.getHeldItem(hand);
		if (stack.getItem() != ESObjects.ITEMS.DUNGEON_KEY) {
			player.sendMessage(new TextComponentTranslation("info.dungeon.no.key.to.open.door"));
			return;
		}

		stack.damageItem(1, player);

		DungeonAreaDoor door = this.getDungeonDoor();
		if (door == null) {
			world.setBlockToAir(pos);
			BlockDungeonDoor.onHarvestDoor(world, player, pos);
			return;
		}

		if (!door.isLink()) return;
		if (door.isOpen()) return;

		DungeonArea area = this.getDungeonArea();
		DungeonAreaRoom room = area.getRoomById(door.getLinkRoomId());
		if (room == null) return;

		// 根据global配置，检查钥匙是否足够
		DungeonFuncGlobal funcGlobal = room.getFuncGlobal();
		if (funcGlobal != null) next: {
			List<ItemMemoryFragment.MemoryFragment> list = funcGlobal.getRequireMemoryFragments();
			if (list.isEmpty()) break next;

			if (!consumeMemory(player.inventory, list, true)) {
				TextComponentTranslation val = new TextComponentTranslation("item.memoryFragment.name");
				int index = 0;
				for (ItemMemoryFragment.MemoryFragment mf : list) {
					if (++index != 1) val.appendText(",");
					Style style = new Style().setColor(ColorHelper.toTextFormatting(mf.getColor()));
					TextComponentTranslation mfstr = new TextComponentTranslation(
							"item.fireworksCharge." + mf.getColor().getTranslationKey());
					val.appendSibling(mfstr.setStyle(style).appendText("x" + mf.getCount()));
				}
				player.sendMessage(new TextComponentTranslation("info.dungeon.no.remember.fragment", val));
				return;
			}

			consumeMemory(player.inventory, list, false);
		}

		area.startBuildRoom(world, room.getId(), player);

		EnumDyeColor color = EnumDyeColor.BLUE;

		world.setBlockState(pos, Blocks.STAINED_GLASS.getDefaultState().withProperty(BlockStainedGlass.COLOR, color));
		world.playSound(null, pos, SoundEvents.AMBIENT_CAVE, player.getSoundCategory(), 5, 1);
	}

	public void initByDungeon(DungeonAreaRoom room, int doorIndex, NBTTagCompound dataset) {
		this.areaId = room.getAreId();
		this.roomId = room.getId();
		this.doorIndex = doorIndex;
	}

	protected boolean consumeMemory(IInventory inv, List<ItemMemoryFragment.MemoryFragment> list, boolean simulate) {

		List<ItemMemoryFragment.MemoryFragment> cList = new LinkedList();
		for (ItemMemoryFragment.MemoryFragment mf : list)
			cList.add(new ItemMemoryFragment.MemoryFragment(mf.getColor(), mf.getCount()));

		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack itemstack = inv.getStackInSlot(i);
			if (itemstack.isEmpty()) continue;
			NBTTagCompound nbt = itemstack.getTagCompound();
			if (nbt == null) continue;
			if (!nbt.hasKey("cmeta", NBTTag.TAG_NUMBER)) continue;
			EnumDyeColor color = EnumDyeColor.byMetadata(nbt.getInteger("cmeta"));
			if (nbt.hasKey("areaId", NBTTag.TAG_NUMBER)) {
				int areaId = nbt.getInteger("areaId");
				int dimId = nbt.getInteger("dimId");
				if (world.provider.getDimension() != dimId) continue;
				if (this.areaId != areaId) continue;
			}
			Iterator<ItemMemoryFragment.MemoryFragment> iter = cList.iterator();
			while (iter.hasNext()) {
				ItemMemoryFragment.MemoryFragment mf = iter.next();
				if (mf.getColor() != color) continue;

				int count = Math.min(itemstack.getCount(), mf.getCount());
				mf.setCount(mf.getCount() - count);
				if (!simulate) itemstack.shrink(count);
				if (mf.getCount() <= 0) iter.remove();
				break;
			}

			if (cList.isEmpty()) return true;
		}

		return false;
	}

}

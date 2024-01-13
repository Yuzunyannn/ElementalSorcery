package yuzunyannn.elementalsorcery.item;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;
import yuzunyannn.elementalsorcery.api.mantra.SilentLevel;
import yuzunyannn.elementalsorcery.api.util.NBTTag;
import yuzunyannn.elementalsorcery.dungeon.DungeonAreaRoom;
import yuzunyannn.elementalsorcery.dungeon.DungeonFuncGlobal;
import yuzunyannn.elementalsorcery.dungeon.DungeonWorld;
import yuzunyannn.elementalsorcery.logics.EventClient;
import yuzunyannn.elementalsorcery.logics.ITickTask;
import yuzunyannn.elementalsorcery.render.effect.Effects;
import yuzunyannn.elementalsorcery.render.effect.scrappy.FireworkEffect;
import yuzunyannn.elementalsorcery.util.LambdaReference;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;
import yuzunyannn.elementalsorcery.util.helper.EntityHelper;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

public class ItemMemoryFeather extends Item {

	public ItemMemoryFeather() {
		this.setTranslationKey("memoryFeather");
		this.setMaxStackSize(1);
		this.setMaxDamage(10);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.add(I18n.format("info.dungeon.memoryFeather.usage"));
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer player, EnumHand handIn) {
		ItemStack stack = player.getHeldItem(handIn);
		// 被沉默
		if (EntityHelper.checkSilent(player, SilentLevel.RELEASE))
			return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);

		if (worldIn.isRemote) return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);

		// check
		DungeonWorld dw = DungeonWorld.getDungeonWorld(worldIn);
		DungeonAreaRoom room = dw.getAreaRoom(player.getPosition());
		if (room == null) return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);

		if (!player.isSneaking()) {
			stack.damageItem(1, player);
			ItemStack findedStack = findHideItem(worldIn, room, false);
			if (findedStack.isEmpty())
				player.sendMessage(new TextComponentTranslation("info.dungeon.no.remember.found"));
			else player.sendMessage(new TextComponentTranslation("info.dungeon.remember.found"));
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
		}

		ItemStack findedStack = findHideItem(worldIn, room, true);
		if (findedStack.isEmpty()) {
			stack.damageItem(2, player);
			player.sendMessage(new TextComponentTranslation("info.dungeon.no.remember.found"));
		} else {
			player.sendMessage(new TextComponentTranslation("info.dungeon.remember.appear"));
			ItemHelper.addItemStackToPlayer(player, findedStack);
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setInteger("eId", player.getEntityId());
			nbt.setByte("type", (byte) 4);
			Effects.spawnEffect(worldIn, Effects.PARTICLE_EFFECT,
					new Vec3d(player.posX, player.posY + player.height / 2, player.posZ), nbt);
		}

		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
	}

	public ItemStack findHideItem(World world, DungeonAreaRoom room, boolean isConsume) {

		// 寻找所有item容器里是否有碎片，防止往箱子里放刷
		LambdaReference<ItemStack> stackRef = LambdaReference.of(ItemStack.EMPTY);
		room.visitCoreBlocks((pos) -> {
			IItemHandler handler = BlockHelper.getItemHandler(world, pos, EnumFacing.UP);
			if (handler == null) return true;
			for (int i = 0; i < handler.getSlots(); i++) {
				ItemStack innerItem = handler.getStackInSlot(i);
				if (innerItem.isEmpty()) continue;
				if (!canAsHideItem(innerItem)) continue;
				stackRef.set(innerItem.copy());
				if (isConsume) {
					innerItem.shrink(innerItem.getCount());
					TileEntity tile = world.getTileEntity(pos);
					if (tile != null) tile.markDirty();
				}
				return false;
			}
			return true;
		});
		if (!stackRef.get().isEmpty()) return stackRef.get();

		// 先到gobal配置里寻找
		DungeonFuncGlobal global = room.getFuncGlobal();
		if (global != null) {
			ItemStack stack = global.topHideItem();
			if (!stack.isEmpty()) {
				if (isConsume) return global.popHideItem();
				else return stack.copy();
			}
		}

		return ItemStack.EMPTY;
	}

	public static boolean canAsHideItem(ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null) return false;
		if (nbt.hasKey("cmeta", NBTTag.TAG_NUMBER)) return true;
		return false;
	}

	@SideOnly(Side.CLIENT)
	public static void doEffect(World world, Vec3d pos, NBTTagCompound nbt) {
		int id = nbt.getInteger("eId");
		Entity entity = world.getEntityByID(id);

		LambdaReference<Integer> count = LambdaReference.of(0);
		EventClient.addTickTask(() -> {
			count.set(count.get() + 1);
			if (count.get() % 3 == 0) {
				world.playSound(entity.posX, entity.posY, entity.posZ, SoundEvents.BLOCK_NOTE_BELL,
						entity.getSoundCategory(), 1.0F, (float) (Math.random() * 8) - 4, false);
			}
			return count.get() > 10 ? ITickTask.END : ITickTask.SUCCESS;
		});

		NBTTagCompound fireWork = FireworkEffect.fastNBT(10, 1, 0.2f,
				new int[] { 0x002059, 0x46daff, 0x199fff, 0xffffff }, null);
		nbt.setByte("extra", (byte) 1);
		pos = entity.getPositionEyes(0).add(entity.getLookVec().scale(0.5));
		Effects.spawnEffect(world, Effects.FIREWROK, pos, fireWork);
	}
}

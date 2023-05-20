package yuzunyannn.elementalsorcery.item;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.ESData;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.dungeon.DungeonArea;
import yuzunyannn.elementalsorcery.dungeon.DungeonAreaRoom;
import yuzunyannn.elementalsorcery.dungeon.DungeonWorld;
import yuzunyannn.elementalsorcery.grimoire.mantra.MantraEnderTeleport;
import yuzunyannn.elementalsorcery.util.helper.EntityHelper;

public class ItemDungeonSeed extends Item {

	public static final int SEED_MAX_LEVEL = 7;

	public ItemDungeonSeed() {
		this.setHasSubtypes(true);
		this.setTranslationKey("dungeonSeed");
		this.setMaxStackSize(1);
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		int meta = stack.getMetadata();
		if (meta < SEED_MAX_LEVEL)
			return super.getItemStackDisplayName(stack) + String.format("(%d/%d)", meta, SEED_MAX_LEVEL);
		return super.getItemStackDisplayName(stack);
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		if (stack.getMetadata() == SEED_MAX_LEVEL) return "item.dungeonSeed.fin";
		return super.getTranslationKey(stack);
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (!this.isInCreativeTab(tab)) return;
		items.add(new ItemStack(this, 1, 0));
		items.add(new ItemStack(this, 1, SEED_MAX_LEVEL));
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing _facing,
			float hitX, float hitY, float hitZ) {
		ItemStack stack = player.getHeldItem(hand);
		int meta = stack.getMetadata();
		if (meta == SEED_MAX_LEVEL) {

			if (world.isRemote) return EnumActionResult.SUCCESS;

//			boolean hasKey = false;
//			for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
//				ItemStack playStack = player.inventory.getStackInSlot(i);
//				if (playStack.isEmpty()) continue;
//				if (playStack.getItem() == ESObjects.ITEMS.DUNGEON_KEY) {
//					hasKey = true;
//					break;
//				}
//			}
//			if (!hasKey) {
//				player.sendMessage(new TextComponentTranslation("info.dungeon.no.key.to.open.door"));
//				return EnumActionResult.FAIL;
//			}

			pos = pos.up();

			boolean passBadPlace = false;
			NBTTagCompound tempData = ESData.getRuntimeData(player);
			int badTimes = tempData.getInteger("dungeonOpenNotGood");

			if (tempData.hasKey("dungeonOpenNotGood")) {
				int hash = tempData.getInteger("dungeonOpenNotGoodHash");
				int myHash = pos.hashCode();
				if (hash != myHash) badTimes = 0;
				else if (badTimes > 2) passBadPlace = true;
			}

			if (!passBadPlace) {
				boolean isBad = false;
				isBad = !world.isAirBlock(pos);
				isBad = isBad || pos.getY() < 50 || pos.getY() > (255 - 75);
				if (isBad) {
					tempData.setInteger("dungeonOpenNotGood", badTimes + 1);
					tempData.setInteger("dungeonOpenNotGoodHash", pos.hashCode());
					ITextComponent com = new TextComponentTranslation("info.dungeon.bad.place");
					if (badTimes == 1) com.setStyle(new Style().setColor(TextFormatting.GOLD));
					else if (badTimes >= 2) com.setStyle(new Style().setColor(TextFormatting.RED));
					player.sendMessage(com);
					return EnumActionResult.FAIL;
				}
			}

			tempData.removeTag("dungeonOpenNotGood");
			tempData.removeTag("dungeonOpenNotGoodHash");

			DungeonWorld dw = DungeonWorld.getDungeonWorld(world);
			if (ESAPI.isDevelop) dw.debugClear();

			DungeonArea area = dw.newDungeon(pos);
			if (area.isFail()) {
				player.sendMessage(new TextComponentTranslation(area.getFailMsg()));
				return EnumActionResult.FAIL;
			}

			area.startBuildRoom(world, 0, player);
			DungeonAreaRoom room = area.getRoomById(0);

			stack.shrink(1);

			EnumFacing facing = room.getFacing();
			AxisAlignedBB aabb = room.getBox();
			BlockPos to = pos.offset(facing, MathHelper.ceil((aabb.maxZ - aabb.minZ) / 2) + 1);
			player.rotationYaw = facing.getOpposite().getHorizontalAngle();
			player.rotationPitch = 0;
			Vec3d toPos = new Vec3d(to.getX() + 0.5, to.getY(), to.getZ() + 0.5);
			MantraEnderTeleport.playEnderTeleportEffect(world, player, toPos);
			MantraEnderTeleport.doEnderTeleport(world, player, toPos);
			boolean isCreative = EntityHelper.isCreative(player);
			world.destroyBlock(to, !isCreative);
			world.destroyBlock(to.up(), !isCreative);
			world.destroyBlock(to.offset(facing.getOpposite()), !isCreative);
			world.destroyBlock(to.up().offset(facing.getOpposite()), !isCreative);

			return EnumActionResult.SUCCESS;
		}
		return super.onItemUse(player, world, pos, hand, _facing, hitX, hitY, hitZ);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		int meta = stack.getMetadata();
		if (meta == SEED_MAX_LEVEL) {
			tooltip.add(TextFormatting.BLUE + I18n.format("info.dungeonSeed.open.new"));
			return;
		} else {
			NBTTagCompound seedData = stack.getOrCreateSubCompound("_dSeed");
			SeedLevelNeed need = getLevelNeed(meta);
			float exp = seedData.getFloat("exp");
			float hp = seedData.getFloat("hp");
			exp = Math.min(exp, need.exp);
			hp = Math.min(hp, need.hp);
			tooltip.add(TextFormatting.RED + I18n.format("info.dungeonSeed.storage.hp",
					TextFormatting.GRAY + String.format("%.2f(%.2f%%)", hp, hp / need.hp * 100)));
			tooltip.add(TextFormatting.DARK_GREEN + I18n.format("info.dungeonSeed.storage.exp",
					TextFormatting.GRAY + String.format("%.2f(%.2f%%)", exp, exp / need.exp * 100)));
		}
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return oldStack.getItem() != newStack.getItem();
	}

	public static class SeedLevelNeed {
		float exp;
		float hp;

		public float getExp() {
			return exp;
		}

		public float getHp() {
			return hp;
		}

	}

	public static SeedLevelNeed getLevelNeed(int level) {
		SeedLevelNeed need = new SeedLevelNeed();
		need.exp = 15 + level * 45;
		need.hp = 4 + level * 9;
		return need;
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		int level = stack.getMetadata();
		if (level >= SEED_MAX_LEVEL) return;

		if (entityIn.ticksExisted % 30 != 0) return;

		if (!(entityIn instanceof EntityPlayerMP)) return;

		EntityPlayerMP player = (EntityPlayerMP) entityIn;

		NBTTagCompound seedData = stack.getOrCreateSubCompound("_dSeed");
		SeedLevelNeed need = getLevelNeed(level);

		float exp = doStorageExp(player, level, seedData.getFloat("exp"), need.getExp());
		float hp = doStorageHP(player, level, seedData.getFloat("hp"), need.getHp());

		seedData.setFloat("exp", exp);
		seedData.setFloat("hp", hp);

		if (exp >= need.getExp() && hp >= need.getHp()) {
			seedData.removeTag("hp");
			seedData.removeTag("exp");
			stack.setItemDamage(level + 1);
		}

	}

	private float doStorageExp(EntityPlayerMP player, int level, float exp, float needExp) {
		if (exp >= needExp) return exp;
		float count = Math.max(0.2f, player.xpBarCap() * 0.04f);
		if (EntityHelper.isCreative(player)) return exp + count;
		float restCount = EntityHelper.dropExperience(player, count);
		if (restCount < count) {
			EntityHelper.sendExperienceChange(player);
			return exp + count - restCount;
		}
		return exp;
	}

	private float doStorageHP(EntityPlayerMP player, int level, float hp, float needHP) {
		if (hp >= needHP) return hp;
		float playerHP = player.getHealth();
		float count = Math.max(0.2f, playerHP * 0.03f * (float) (level > 1 ? Math.sqrt(level) : 1));
		if (EntityHelper.isCreative(player)) return hp + count;
		if (playerHP <= 0.2) return hp;
		player.setHealth(playerHP - count);
		return hp + count;
	}

}

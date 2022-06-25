package yuzunyannn.elementalsorcery.elf.pro;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.capability.Adventurer;
import yuzunyannn.elementalsorcery.elf.ElfChamberOfCommerce;
import yuzunyannn.elementalsorcery.elf.quest.IAdventurer;
import yuzunyannn.elementalsorcery.entity.EntityItemGoods;
import yuzunyannn.elementalsorcery.entity.elf.EntityElfBase;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.batch.EffectElementMove;
import yuzunyannn.elementalsorcery.render.entity.living.RenderEntityElf;
import yuzunyannn.elementalsorcery.util.helper.Color;
import yuzunyannn.elementalsorcery.util.helper.RandomHelper;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;
import yuzunyannn.elementalsorcery.util.var.VariableSet;
import yuzunyannn.elementalsorcery.util.var.VariableSet.Variable;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class ElfProfessionDebtCollector extends ElfProfession {

	public static final Variable<Integer> DEBTOR_ID = new Variable("debtor", VariableSet.INT);
	public static final Variable<UUID> DEBTOR_PAYER_ID = new Variable("dPlayer", VariableSet.UUID);
	public static final Variable<LinkedList<ItemStack>> HOLD_LIST = new Variable("hItem", VariableSet.ITEM_LINKED_LIST);

	@Override
	public void initElf(EntityElfBase elf, ElfProfession origin) {
		elf.setExperienceValue(0);
		elf.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.FISHING_ROD));
		elf.setDropChance(EntityEquipmentSlot.MAINHAND, 0.02f);
		elf.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, new ItemStack(ESInit.ITEMS.ELF_COIN));
		elf.setDropChance(EntityEquipmentSlot.MAINHAND, 0.02f);
	}

	@Override
	public float getAttackDistance() {
		return 2;
	}

	@Override
	public Float attackedFrom(EntityElfBase elf, DamageSource source, float amount) {
		return super.attackedFrom(elf, source, amount);
	}

	@Override
	public boolean attackEntity(EntityElfBase elf, Entity target) {
		if (super.attackEntity(elf, target)) {
			if (target instanceof EntityPlayer && !target.world.isRemote) {
				EntityPlayer player = ((EntityPlayer) target);
				int size = player.inventory.getSizeInventory();
				int randIndex = elf.getRNG().nextInt(size);
				for (int i = 0; i < 16; i++) {
					int index = (randIndex + i) % size;
					ItemStack stack = player.inventory.getStackInSlot(index);
					if (!stack.isEmpty()) {
						int price = priceIt(stack);
						if (price <= 0) continue;
						player.inventory.setInventorySlotContents(index, ItemStack.EMPTY);
						ItemHelper.dropItem(target.world, target.getPositionEyes(0), stack)
								.setThrower(player.getName());
						player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 40, 3));
						player.sendMessage(new TextComponentString(elf.getName() + ":")
								.appendSibling(new TextComponentTranslation("say.pay.debt.drop.item",
										String.valueOf(price), stack.getDisplayName()))
								.setStyle(new Style().setColor(TextFormatting.GREEN)));
						break;
					}
				}

			}
			return true;
		}
		return false;
	}

	@Override
	public void tick(EntityElfBase elf) {
		if (isAndDealBack(elf)) return;
		if (elf.ticksExisted % 20 != 0) return;
		World world = elf.world;
		if (world.isRemote) return;
		VariableSet storage = elf.getProfessionStorage();

		if (elf.ticksExisted % 60 == 0) {
			elf.addPotionEffect(new PotionEffect(MobEffects.SPEED, 40, 2));
			if (storage.has(DEBTOR_PAYER_ID)) {
				UUID playerId = storage.get(DEBTOR_PAYER_ID);
				EntityPlayer player = elf.world.getPlayerEntityByUUID(playerId);
				if (player != null) {
					IAdventurer adventurer = player.getCapability(Adventurer.ADVENTURER_CAPABILITY, null);
					if (adventurer != null && adventurer.getDebts() <= 0) {
						player.sendMessage(new TextComponentString(elf.getName() + ":")
								.appendSibling(new TextComponentTranslation("say.pay.debt.collect.over"))
								.setStyle(new Style().setColor(TextFormatting.GREEN)));
						elf.setAttackTarget(null);
						elf.setRevengeTarget(null);
						storage.remove(DEBTOR_ID);
						storage.remove(DEBTOR_PAYER_ID);
						back(elf);
						return;
					}
				}
			}
		}

		if (!storage.has(DEBTOR_ID)) next: {
			if (findDebtor(elf) != null) break next;
			onOnbodyToDebtColltion(elf);
			return;
		}
		int debtorId = storage.get(DEBTOR_ID);
		Entity entity = world.getEntityByID(debtorId);
		if (!(entity instanceof EntityLivingBase)) next: {
			entity = findDebtor(elf);
			if (entity != null) break next;
			onOnbodyToDebtColltion(elf);
			return;
		}
		EntityLivingBase debtor = (EntityLivingBase) entity;
		elf.setAttackTarget(debtor);
		if (debtor instanceof EntityPlayer) storage.set(DEBTOR_PAYER_ID, debtor.getUniqueID());
	}

	public boolean isAndDealBack(EntityElfBase elf) {
		ItemStack hold = elf.getHeldItemMainhand();
		if (hold.getItem() != ESInit.ITEMS.ELF_COIN) return false;
		if (elf.world.isRemote) {
			showBackEffect(elf);
			return true;
		}
		if (elf.ticksExisted % 2 == 0) {
			hold.shrink(1);
			if (elf.getAttackTarget() != null)
				elf.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.FISHING_ROD));
		}
		if (hold.isEmpty()) elf.setDead();
		return true;
	}

	@SideOnly(Side.CLIENT)
	public void showBackEffect(EntityElfBase elf) {
		EffectElementMove effect = new EffectElementMove(elf.world, elf.getPositionVector());
		Random rand = RandomHelper.rand;
		effect.color.setColor(Color.fromHSV(rand.nextDouble() * 360, 1, 1));
		Vec3d vec = new Vec3d(rand.nextGaussian(), rand.nextDouble() + 0.75, rand.nextGaussian()).normalize();
		effect.setVelocity(vec.scale(0.15));
		Effect.addEffect(effect);
	}

	public void back(EntityElfBase elf) {
		elf.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(ESInit.ITEMS.ELF_COIN, 64, 0));
	}

	public void onOnbodyToDebtColltion(EntityElfBase elf) {
		if (elf.getAttackTarget() != null) return;
		if (elf.getRNG().nextFloat() < 0.75) return;
		back(elf);
	}

	public EntityLivingBase findDebtor(EntityElfBase elf) {
		AxisAlignedBB aabb = WorldHelper.createAABB(elf, 8, 8, 8);
		List<EntityPlayer> players = elf.world.getEntitiesWithinAABB(EntityPlayer.class, aabb,
				e -> ElfChamberOfCommerce.isShouldDebtCollection(e));
		if (players.isEmpty()) return null;
		VariableSet storage = elf.getProfessionStorage();
		EntityPlayer player = players.get(elf.getRNG().nextInt(players.size()));
		storage.set(DEBTOR_ID, player.getEntityId());
		return player;
	}

	@Override
	public void onDead(EntityElfBase elf) {
		super.onDead(elf);
		if (elf.world.isRemote) return;
		VariableSet storage = elf.getProfessionStorage();
		if (!storage.has(HOLD_LIST)) return;
		LinkedList<ItemStack> items = storage.get(HOLD_LIST);
		for (ItemStack stack : items) {
			EntityItemGoods.dropGoods(elf, stack, priceIt(stack) * stack.getCount(), false)
					.setLife(20 * (30 + elf.getRNG().nextInt(30)));
		}
	}

	@Override
	public void dropFewItems(EntityElfBase elf, boolean wasRecentlyHit, int lootingModifier) {

	}

	public int priceIt(ItemStack stack) {
		int price = ElfChamberOfCommerce.priceIt(stack);
		return price > 0 ? price : 1;
	}

	@Override
	public boolean canDespawn(EntityElfBase elf) {
		return true;
	}

	@Override
	public boolean needPickup(EntityElfBase elf, ItemStack stack) {
		return priceIt(stack) > 0;
	}

	@Override
	public void onPickupItem(EntityElfBase elf, EntityItem itemEntity) {
		ItemStack stack = itemEntity.getItem();
		int price = priceIt(stack);
		if (price <= 0) return;
		if (elf.world.isRemote) return;

		price *= stack.getCount();
		VariableSet storage = elf.getProfessionStorage();
		EntityPlayer player = null;
		if (storage.has(DEBTOR_PAYER_ID)) {
			UUID playerId = storage.get(DEBTOR_PAYER_ID);
			player = elf.world.getPlayerEntityByUUID(playerId);
		} else if (elf.getAttackTarget() instanceof EntityPlayer) player = (EntityPlayer) elf.getAttackTarget();
		// 拿走物品，玩家减债务
		if (player != null) {
			IAdventurer adventurer = player.getCapability(Adventurer.ADVENTURER_CAPABILITY, null);
			if (adventurer != null) adventurer.incurDebts(-price);
			LinkedList<ItemStack> items = storage.get(HOLD_LIST);
			items.add(stack.copy());
			stack.shrink(stack.getCount());
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ResourceLocation getTexture(EntityElfBase elf) {
		return RenderEntityElf.TEXTURE_DEBT_COLLECTOR;
	}
}

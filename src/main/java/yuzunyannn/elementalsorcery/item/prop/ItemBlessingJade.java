package yuzunyannn.elementalsorcery.item.prop;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.item.IPlatformTickable;
import yuzunyannn.elementalsorcery.api.util.IWorldObject;
import yuzunyannn.elementalsorcery.entity.EntityThrow;
import yuzunyannn.elementalsorcery.item.IItemSmashable;
import yuzunyannn.elementalsorcery.render.effect.Effects;

public class ItemBlessingJade extends Item implements IPlatformTickable, EntityThrow.IItemThrowAction, IItemSmashable {

	public ItemBlessingJade() {
		this.setTranslationKey("blessingJade");
		this.setHasSubtypes(true);
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		return super.getTranslationKey() + "." + stack.getMetadata();
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (!this.isInCreativeTab(tab)) return;
		items.add(new ItemStack(this, 1, 0));
		items.add(new ItemStack(this, 1, 1));
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack stack = playerIn.getHeldItem(handIn);
		int meta = stack.getMetadata();
		if (meta != 0) return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
		EntityThrow.shoot(playerIn, stack);
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public void onImpact(EntityThrow entity, RayTraceResult result) {
		int meta = entity.getItemStack().getMetadata();
		if (meta != 0) {
			EntityThrow.onImpact(entity, result);
			return;
		}

		Vec3d vec = result.hitVec;
		if (vec == null) return;
		if (entity.world.isRemote) return;
		for (int i = 0; i < 8; i++) entity.entityDropItem(ItemBlessingJadePiece.createPiece(i), 0);
		entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.BLOCK_GLASS_BREAK,
				SoundCategory.NEUTRAL, 1, 1);
	}

	@Override
	public void doSmash(World world, Vec3d vec, ItemStack stack, List<ItemStack> outputs, Entity operator) {
		if (world.isRemote) return;
		if (stack.getMetadata() != 0) return;

		stack.shrink(1);
		for (int i = 0; i < 8; i++) outputs.add(ItemBlessingJadePiece.createPiece(i));
		world.playSound(null, vec.x, vec.y, vec.z, SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.NEUTRAL, 1, 1);
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		if (worldIn.isRemote) return;
		if (entityIn.ticksExisted % 200 == 0) {
			int meta = stack.getMetadata();
			if (meta != 0) return;
			EnumHand hand = ItemVortex.inEntityHand(entityIn, stack, itemSlot, isSelected);
			if (hand == null) return;
			EntityLivingBase living = (EntityLivingBase) entityIn;
			living.addPotionEffect(new PotionEffect(ESObjects.POTIONS.BLESSING, 120, 0));
		}
	}

	@Override
	public boolean platformUpdate(World world, ItemStack stack, IWorldObject caster, NBTTagCompound runData, int tick) {
		int meta = stack.getMetadata();
		if (meta != 0) return false;

		if (world.isRemote) {
			ItemCalamityGem.randEffect(world, caster.getPosition(), 8, new int[] { 0x5ba3ff, 0x3876d8 });
			return false;
		}
		if (tick % 200 != 0) return false;
		ItemCalamityGem.tryAddPotionEffect(world, caster.getPosition(), 8, e -> {
			blessingEntity(e);
			Effects.spawnSummonEntity(e, new int[] { 0x5ba3ff, 0x3876d8 });
			return null;
		});
		return false;
	}

	public void blessingEntity(EntityLivingBase living) {
		PotionEffect effect = living.getActivePotionEffect(ESObjects.POTIONS.BLESSING);
		int originDuration = effect == null ? 0 : effect.getDuration();
		int originAmplifier = effect == null ? 0 : effect.getAmplifier();

		int levelUpNeedDuration = (originAmplifier + 1) * 20 * 60;

		int amplifier = originAmplifier;
		int time = originDuration + 300;

		if (time >= levelUpNeedDuration) {
			if (amplifier < 3) {
				amplifier = amplifier + 1;
				time = 300;
			}
		}

		living.addPotionEffect(new PotionEffect(ESObjects.POTIONS.BLESSING, time, amplifier));
	}

}

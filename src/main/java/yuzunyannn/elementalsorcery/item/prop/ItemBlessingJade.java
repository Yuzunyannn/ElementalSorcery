package yuzunyannn.elementalsorcery.item.prop;

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
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.item.IPlatformTickable;
import yuzunyannn.elementalsorcery.api.util.IWorldObject;
import yuzunyannn.elementalsorcery.entity.EntityThrow;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.render.effect.Effects;

public class ItemBlessingJade extends Item implements IPlatformTickable, EntityThrow.IItemThrowAction {

	public ItemBlessingJade() {
		this.setTranslationKey("blessingJade");
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		EntityThrow.shoot(playerIn, playerIn.getHeldItem(handIn));
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
	}

	@Override
	public void onImpact(EntityThrow entity, RayTraceResult result) {
		Vec3d vec = result.hitVec;
		if (vec == null) return;
		if (entity.world.isRemote) return;
		for (int i = 0; i < 8; i++) entity.entityDropItem(ItemBlessingJadePiece.createPiece(i), 0);
		entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.BLOCK_GLASS_BREAK,
				SoundCategory.NEUTRAL, 1, 1);
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		if (worldIn.isRemote) return;
		if (entityIn.ticksExisted % 200 == 0) {

			EnumHand hand = ItemVortex.inEntityHand(entityIn, stack, itemSlot, isSelected);
			if (hand == null) return;

			EntityLivingBase living = (EntityLivingBase) entityIn;
			living.addPotionEffect(new PotionEffect(ESInit.POTIONS.BLESSING, 120, 0));
		}
	}

	public void blessingEntity(EntityLivingBase living) {
		PotionEffect effect = living.getActivePotionEffect(ESInit.POTIONS.BLESSING);
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

		living.addPotionEffect(new PotionEffect(ESInit.POTIONS.BLESSING, time, amplifier));
	}

	@Override
	public boolean platformUpdate(World world, ItemStack stack, IWorldObject caster, NBTTagCompound runData,
			int tick) {
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

}

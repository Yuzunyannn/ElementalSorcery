package yuzunyannn.elementalsorcery.item;

import java.util.List;
import java.util.Random;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.capability.CapabilityProvider;
import yuzunyannn.elementalsorcery.capability.ElementInventory;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.Effects;
import yuzunyannn.elementalsorcery.render.effect.element.EffectElementMove;
import yuzunyannn.elementalsorcery.util.RandomHelper;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;
import yuzunyannn.elementalsorcery.util.item.IItemUseClientUpdate;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class ItemMagicBlastWand extends Item implements IItemUseClientUpdate {

	public ItemMagicBlastWand() {
		this.setUnlocalizedName("magicBlastWand");
		this.setMaxStackSize(1);
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.BOW;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 72000;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		IElementInventory einv = stack.getCapability(ElementInventory.ELEMENTINVENTORY_CAPABILITY, null);
		einv.loadState(stack);
		ElementHelper.addElementInformation(einv, worldIn, tooltip, flagIn);
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
		return new CapabilityProvider.ElementInventoryUseProvider(stack, new MagicBlestInventory());
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack stack = playerIn.getHeldItem(handIn);
		playerIn.setActiveHand(handIn);
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {

	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onUsingTickClient(ItemStack stack, EntityLivingBase player, int count) {

		IElementInventory einv = stack.getCapability(ElementInventory.ELEMENTINVENTORY_CAPABILITY, null);
		count = this.getMaxItemUseDuration(stack) - count;
		if (count == 0) einv.loadState(stack);
		ElementStack estack = einv.getStackInSlot(0);
		if (estack.isEmpty()) return;

		count = Math.min(count, 80);
		Random rand = RandomHelper.rand;
		if (rand.nextInt(80) > count) return;

		Vec3d vec = player.getPositionVector().addVector(0, player.getEyeHeight(), 0);
		vec = vec.add(player.getLookVec()).addVector(rand.nextGaussian(), rand.nextGaussian(), rand.nextGaussian());
		Vec3d speed = new Vec3d(rand.nextGaussian(), rand.nextGaussian(), rand.nextGaussian());
		EffectElementMove effect = new EffectElementMove(player.world, vec);
		effect.setColor(0x4d2175);
		effect.g = 0;
		effect.setVelocity(speed.normalize().scale(0.025));
		Effect.addEffect(effect);
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {

		RayTraceResult rt = WorldHelper.getLookAtEntity(worldIn, entityLiving, 32, EntityLiving.class);
		if (rt == null) return;

		EntityLiving entitiy = (EntityLiving) rt.entityHit;
		if (entitiy.isOnSameTeam(entityLiving)) return;

		if (worldIn.isRemote) return;

		int count = this.getMaxItemUseDuration(stack) - timeLeft;
		if (count < 5) return;

		int collect = Math.min(count, 40);
		float powerUp = 1 + MathHelper.clamp(count - 40, 0, 40) / 200f;

		IElementInventory einv = stack.getCapability(ElementInventory.ELEMENTINVENTORY_CAPABILITY, null);
		einv.loadState(stack);
		ElementStack magic = einv.extractElement(ElementStack.magic(collect * 20, 20), false);
		if (magic.isEmpty()) return;
		einv.saveState(stack);
		magic.setPower(Math.round(magic.getPower() * powerUp));
		blast(magic, entitiy, entityLiving);
	}

	static class MagicBlestInventory extends ElementInventory {

		public MagicBlestInventory() {
			super(1);
		}

		@Override
		public boolean insertElement(ElementStack estack, boolean simulate) {
			return this.insertElement(0, estack, simulate);
		}

		@Override
		public ElementStack extractElement(ElementStack estack, boolean simulate) {
			return super.extractElement(0, estack, simulate);
		}

		@Override
		public boolean insertElement(int slot, ElementStack estack, boolean simulate) {
			if (!estack.isMagic()) {
				estack = estack.copy();
				estack = estack.becomeMagic(null);
			}
			return super.insertElement(slot, estack, simulate);
		}

		@Override
		public ElementStack extractElement(int slot, ElementStack estack, boolean simulate) {
			if (!estack.isMagic()) return ElementStack.EMPTY.copy();
			return super.extractElement(slot, estack, simulate);
		}
	}

	/** 爆炸 */
	public static void blast(ElementStack magic, Entity target, Entity source) {
		World world = target.world;
		int level = MathHelper.clamp(MathHelper.ceil(magic.getCount() / 200f), 1, 4);
		int size = level;
		float dmg = magic.getPower() / 24f;
		Vec3d pos = target.getPositionVector().addVector(0, target.height / 2, 0);
		DamageSource ds = DamageSource.causeIndirectMagicDamage(null, source);

		if (size > 1) {
			AxisAlignedBB AABB = new AxisAlignedBB(pos.x - size, pos.y - size, pos.z - size, pos.x + size, pos.y + size,
					pos.z + size);
			List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, AABB);
			for (EntityLivingBase entity : entities) {
				Vec3d at = entity.getPositionVector().addVector(0, entity.height / 2, 0);
				float dmgRate = Math.min(1, 1 / (MathHelper.sqrt(pos.distanceTo(at) * 0.65f)));
				entity.attackEntityFrom(ds, dmgRate * dmg);
			}

		} else {
			target.attackEntityFrom(ds, dmg);
		}

		if (world.isRemote) return;

		NBTTagCompound effect = new NBTTagCompound();
		effect.setByte("lev", (byte) level);
		effect.setByte("type", (byte) 0);
		Effects.spawnEffect(world, Effects.PARTICLE_EFFECT, pos, effect);
	}

}

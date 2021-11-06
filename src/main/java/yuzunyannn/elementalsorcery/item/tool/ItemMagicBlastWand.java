package yuzunyannn.elementalsorcery.item.tool;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

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
import net.minecraft.util.math.BlockPos;
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
import yuzunyannn.elementalsorcery.config.Config;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.element.explosion.ElementExplosion;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.Effects;
import yuzunyannn.elementalsorcery.render.effect.batch.EffectElementMove;
import yuzunyannn.elementalsorcery.util.helper.DamageHelper;
import yuzunyannn.elementalsorcery.util.helper.EntityHelper;
import yuzunyannn.elementalsorcery.util.helper.RandomHelper;
import yuzunyannn.elementalsorcery.util.item.IItemUseClientUpdate;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class ItemMagicBlastWand extends Item implements IItemUseClientUpdate {

	@Config(kind = "item")
	@Config.NumberRange(max = Double.MAX_VALUE, min = 0)
	private static float MAGIC_BLAST_ONE_DAMAGE_PER_COUNT = 80;

	@Config(kind = "item")
	private static float MAGIC_MAX_DAMAGE_LIMIT_FOR_DECAY = 50;

	@Config(kind = "item")
	@Config.NumberRange(max = Double.MAX_VALUE, min = 0)
	private static float MAGIC_RANGE_DECAY_RATE = 0.65f;

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
		einv.addInformation(worldIn, tooltip, flagIn);
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

		RayTraceResult rt = WorldHelper.getLookAtEntity(worldIn, entityLiving, 32, EntityLivingBase.class);
		if (rt == null) return;

		EntityLiving entitiy = (EntityLiving) rt.entityHit;
		if (EntityHelper.isSameTeam(entitiy, entityLiving)) return;

		if (worldIn.isRemote) return;

		int count = this.getMaxItemUseDuration(stack) - timeLeft;
		if (count < 5) return;

		int collect = Math.min(count, 40);
		float powerUp = 1 + MathHelper.clamp(count - 40, 0, 40) / 200f;

		IElementInventory einv = stack.getCapability(ElementInventory.ELEMENTINVENTORY_CAPABILITY, null);
		einv.loadState(stack);
		ElementStack magic = einv.extractElement(ElementStack.magic(collect * 20, 1), false);
		if (magic.isEmpty()) return;
		einv.saveState(stack);
		magic.setPower(Math.round(magic.getPower() * powerUp));
		blast(magic, entitiy, entityLiving, null);
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

	/** 通用的，魔力转伤害 */
	public static float getDamage(ElementStack magic) {
		if (!magic.isMagic()) magic = magic.toMagic(null);
		int count = magic.getCount();
		int power = magic.getPower();
		float point = MathHelper.sqrt(power) / 25f;
		float dmg = count / MAGIC_BLAST_ONE_DAMAGE_PER_COUNT * (1 + point * point * point);
		if (dmg > MAGIC_MAX_DAMAGE_LIMIT_FOR_DECAY) {
			float more = dmg - MAGIC_MAX_DAMAGE_LIMIT_FOR_DECAY;
			dmg = MAGIC_MAX_DAMAGE_LIMIT_FOR_DECAY + (float) Math.log10(more);
		}
		return dmg;
	}

	public static void blast(ElementStack magic, Entity target, @Nullable Entity source,
			@Nullable Entity directSource) {
		blast(magic, target.world, target, null, source, null);
	}

	public static void blast(ElementStack magic, World world, Vec3d at, @Nullable Entity source,
			@Nullable Entity directSource) {
		blast(magic, world, null, at, source, directSource);
	}

	public static void blast(ElementStack magic, World world, BlockPos pos, @Nullable Entity source,
			@Nullable Entity directSource) {
		blast(magic, world, null, new Vec3d(pos).addVector(0.5, 0.5, 0.5), source, directSource);
	}

	/**
	 * 爆炸 使用见<br/>
	 * {@link ItemMagicBlastWand#blast(ElementStack, Entity, Entity, Entity)}
	 * {@link ItemMagicBlastWand#blast(ElementStack, World, Vec3d, Entity, Entity)}
	 * {@link ItemMagicBlastWand#blast(ElementStack, World, BlockPos, Entity, Entity)}
	 * 
	 * 新爆炸直接使用通用元素接口<br/>
	 * {@link ElementExplosion#doExplosion(World, Vec3d, ElementStack, EntityLivingBase)}
	 * {@link ElementExplosion#doExplosion(World, BlockPos, ElementStack, EntityLivingBase)}
	 */
	private static void blast(ElementStack magic, World world, Entity target, Vec3d targetPos, Entity source,
			Entity directSource) {

		int level = MathHelper.clamp(MathHelper.ceil(magic.getCount() / 200f), 1, 5);
		float dmg = getDamage(magic);

		Vec3d pos;
		if (target != null) pos = target.getPositionVector().addVector(0, target.height / 2, 0);
		else pos = targetPos;

		DamageSource ds = DamageHelper.getMagicDamageSource(source, directSource);

		if (level > 1 || target == null) {
			AxisAlignedBB AABB;

			final float s = level;
			if (s == 1) AABB = new AxisAlignedBB(pos.x, pos.y, pos.z, pos.x + s, pos.y + s, pos.z + s);
			else AABB = new AxisAlignedBB(pos.x - s, pos.y - s, pos.z - s, pos.x + s, pos.y + s, pos.z + s);

			List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, AABB);
			for (EntityLivingBase entity : entities) {
				Vec3d at = entity.getPositionVector().addVector(0, entity.height / 2, 0);
				float attenuate = MathHelper.sqrt(pos.distanceTo(at) * MAGIC_RANGE_DECAY_RATE);
				float dmgRate = 1 / Math.max(attenuate, 1);
				entity.attackEntityFrom(ds, dmgRate * dmg);
			}

		} else target.attackEntityFrom(ds, dmg);

		if (world.isRemote) return;

		Effects.spawnTypeEffect(world, pos, 0, level);
	}

}

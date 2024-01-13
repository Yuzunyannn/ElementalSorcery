package yuzunyannn.elementalsorcery.item.tool;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.mantra.SilentLevel;
import yuzunyannn.elementalsorcery.entity.mob.EntityRelicGuard;
import yuzunyannn.elementalsorcery.entity.mob.EntityRelicZombie;
import yuzunyannn.elementalsorcery.logics.EventServer;
import yuzunyannn.elementalsorcery.logics.ITickTask;
import yuzunyannn.elementalsorcery.logics.IWorldTickTask;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.Effects;
import yuzunyannn.elementalsorcery.render.effect.batch.EffectElementMove;
import yuzunyannn.elementalsorcery.util.LambdaReference;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;
import yuzunyannn.elementalsorcery.util.helper.EntityHelper;
import yuzunyannn.elementalsorcery.util.helper.NBTHelper;
import yuzunyannn.elementalsorcery.util.helper.RandomHelper;
import yuzunyannn.elementalsorcery.util.item.IItemUseClientUpdate;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class ItemShockWand extends Item implements IItemUseClientUpdate {

	public ItemShockWand() {
		this.setTranslationKey("shockWand");
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
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack stack = playerIn.getHeldItem(handIn);
		if (EntityHelper.checkSilent(playerIn, SilentLevel.RELEASE))
			return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);
		playerIn.setActiveHand(handIn);
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {

	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onUsingTickClient(ItemStack stack, EntityLivingBase player, int count) {
		count = this.getMaxItemUseDuration(stack) - count;
		count = Math.min(count, 80);
		Random rand = RandomHelper.rand;
		if (rand.nextInt(80) > count) return;

		Vec3d vec = player.getPositionVector().add(0, player.getEyeHeight(), 0);
		vec = vec.add(player.getLookVec()).add(rand.nextGaussian(), rand.nextGaussian(), rand.nextGaussian());
		Vec3d speed = new Vec3d(rand.nextGaussian(), rand.nextGaussian(), rand.nextGaussian());
		EffectElementMove effect = new EffectElementMove(player.world, vec);
		effect.setColor(0xc7faff);
		if (count >= 80 && rand.nextFloat() < 0.5) effect.setColor(0x9feeff);
		effect.setVelocity(speed.normalize().scale(0.025));
		Effect.addEffect(effect);
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {

		int time = this.getMaxItemUseDuration(stack) - timeLeft;
		if (time < 5) return;
		time = Math.min(80, time);
		double originRate = time / 80.0;
		double rate = originRate * originRate * 3;

		RayTraceResult rt = WorldHelper.getLookAtEntity(worldIn, entityLiving, 32, EntityLivingBase.class);
		if (rt != null) {
			Vec3d orient = rt.hitVec.subtract(entityLiving.getPositionEyes(0)).normalize();
			Entity target = rt.entityHit;
			target.motionX += orient.x * rate;
			target.motionY += orient.y * rate;
			target.motionZ += orient.z * rate;
			target.velocityChanged = true;
			if (target instanceof EntityRelicGuard) ((EntityRelicGuard) target).onShock(entityLiving);
			if (target instanceof EntityRelicZombie) ((EntityRelicZombie) target).onShock();
			if (target instanceof EntityLivingBase) {
				EntityLivingBase living = ((EntityLivingBase) target);
				living.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, (int) Math.max(20, 160 * rate), 1));
				living.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, (int) Math.max(20, 160 * rate)));
				if (living.getRevengeTarget() == null) living.setRevengeTarget(entityLiving);
			}
			shockEffect(worldIn, rt.hitVec, orient.scale(-1));
			return;
		}

		rt = WorldHelper.getLookAtBlock(worldIn, entityLiving, 32);
		if (rt != null) {
			BlockPos from = rt.getBlockPos();
			BlockPos to = from.offset(rt.sideHit.getOpposite());
			IBlockState state = worldIn.getBlockState(from);
			if (state.getBlock() == ESObjects.BLOCKS.STRANGE_EGG) {
//				clearStrangeEgg(worldIn, from, rt.sideHit.getOpposite(), (int) (originRate * 32));
			} else if (!from.equals(to) && BlockHelper.isReplaceBlock(worldIn, to)
					&& !BlockHelper.isBedrock(worldIn, from))
				exit: {
					if (!BlockHelper.isSolidBlock(worldIn, from)) break exit;
					if (state.getBlock().hasTileEntity(state)) break exit;
					float hardness = state.getBlockHardness(worldIn, from);
					if (hardness > rate * 10) break exit;
					worldIn.destroyBlock(to, true);
					worldIn.setBlockState(to, state);
					worldIn.setBlockToAir(from);
				}
			shockEffect(worldIn, rt.hitVec, new Vec3d(rt.sideHit.getDirectionVec()));
			return;
		}
	}

	static public void shockEffect(World world, Vec3d hitVec, Vec3d orient) {
		Effects.spawnTypeEffect(world, hitVec, 3, orient);
	}

	@SideOnly(Side.CLIENT)
	public static void doEffect(World world, Vec3d pos, NBTTagCompound nbt) {
		Vec3d orient = NBTHelper.getVec3d(nbt, "vec");
		Random rand = Effect.rand;
		for (int i = 0; i < 16; i++) {
			Vec3d rVec = new Vec3d(rand.nextGaussian(), rand.nextGaussian(), rand.nextGaussian()).normalize();
			Vec3d speed = orient.add(rVec.scale(rand.nextGaussian() * 0.25));
			EffectElementMove effect = new EffectElementMove(world, pos);
			if (rand.nextFloat() < 0.5) effect.setColor(0x9feeff);
			else effect.setColor(0xc7faff);
			effect.setVelocity(speed.scale(0.2));
			Effect.addEffect(effect);
		}
	}

	public void clearStrangeEgg(World world, BlockPos pos, EnumFacing facing, int maxLength) {
		if (world.isRemote) {
			int size = Math.max((maxLength - 0) / 4, 0);
			BlockPos at = pos;
			EnumFacing dFacing = facing.rotateY();
			Vec3d dirVec = new Vec3d(facing.getOpposite().getDirectionVec());
			for (int i = -size; i <= size; i++)
				shockEffect(world, new Vec3d(at.offset(dFacing, i)).add(0.5, 0.5, 0.5), dirVec);
			return;
		}
		LambdaReference<Integer> count = LambdaReference.of(0);
		LambdaReference<Integer> tick = LambdaReference.of(0);
		LambdaReference<BlockPos> currPos = LambdaReference.of(pos);
		Runnable clear = () -> {
			int c = count.get();
			int size = Math.max((maxLength - c) / 4, 0);
			BlockPos at = currPos.get();
			IBlockState state = world.getBlockState(at);
			if (state.getBlock() != ESObjects.BLOCKS.STRANGE_EGG) return;
			EnumFacing dFacing = facing.rotateY();
			for (int i = -size; i <= size; i++) {
				world.destroyBlock(at.offset(dFacing, i), false);
			}
		};
		final IWorldTickTask task = (w) -> {
			int t = tick.get();
			tick.set(t + 1);
			if (t % 2 != 0) return ITickTask.SUCCESS;
			int c = count.get();
			count.set(c + 1);
			if (c > maxLength) return ITickTask.END;
			clear.run();
			BlockPos at = currPos.get();
			currPos.set(at.offset(facing));
			return ITickTask.SUCCESS;
		};
		EventServer.addWorldTickTask(world, task);
	}

}

package yuzunyannn.elementalsorcery.item;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.entity.EntityRotaryWindmillBlate;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.util.TextHelper;

public class ItemWindmillBlades {

	static public final ResourceLocation TEXTURE_BLADE_ASTONE = TextHelper
			.toESResourceLocation("textures/blocks/windmill/blade_astone.png");
	static public final ResourceLocation TEXTURE_BLADE_WOOD = TextHelper
			.toESResourceLocation("textures/blocks/windmill/blade_wood.png");
	static public final ResourceLocation TEXTURE_BLADE_CRYSTAL = TextHelper
			.toESResourceLocation("textures/blocks/windmill/blade_crystal.png");

	public static class AStone extends ItemWindmillBlade {

		public AStone() {
			super("astone", 4 * 60 * 60);
			bladeDamage = 1;
		}

		@Override
		public ElementStack updateOnce(World world, BlockPos pos, ItemStack stack, float speed, int tally) {
			ElementStack estack = super.updateOnce(world, pos, stack, speed, tally);
			if (estack.isEmpty()) return estack;
			return estack.becomeMagic(world);
		}

		@Override
		public float bladeWindScale(World world, BlockPos pos, ItemStack stack) {
			return (super.bladeWindScale(world, pos, stack) + 0.1f) * 1.1f;
		}

		@Override
		@SideOnly(Side.CLIENT)
		public ResourceLocation getWindmillBladeSkin() {
			return TEXTURE_BLADE_ASTONE;
		}

		@Override
		public void bladePitch(World world, Vec3d vec, ItemStack stack, EntityRotaryWindmillBlate eBlate) {
			if (world.isRemote) return;
			super.bladePitch(world, vec, stack, eBlate);
			if (eBlate.tick % 40 == 0) pitchMoveNextTarget(world, vec, 8, eBlate);
		}

	}

	public static class WOOD extends ItemWindmillBlade {

		public WOOD() {
			super("wood", 1 * 60 * 60);
			bladeDamage = 0.5f;
		}

		@Override
		public boolean bladeUpdate(World world, BlockPos pos, ItemStack stack, List<ElementStack> outList, float speed,
				int tick) {
			if (world.provider.getDimension() == -1) {
				if (tryDamageItem(stack, world)) return true;
			}
			return super.bladeUpdate(world, pos, stack, outList, speed, tick);
		}

		@Override
		public ElementStack updateOnce(World world, BlockPos pos, ItemStack stack, float speed, int tally) {
			ElementStack estack = super.updateOnce(world, pos, stack, speed, tally);
			if (estack.isEmpty()) return estack;
			if (estack.getElement() == ESInit.ELEMENTS.WATER || estack.getElement() == ESInit.ELEMENTS.AIR
					|| estack.getElement() == ESInit.ELEMENTS.FIRE) {
				estack.weaken(0.5f);
				return estack;
			}
			return ElementStack.EMPTY;
		}

		@Override
		public float bladeWindScale(World world, BlockPos pos, ItemStack stack) {
			return (super.bladeWindScale(world, pos, stack) + 0.2f) * 1.25f;
		}

		@Override
		@SideOnly(Side.CLIENT)
		public ResourceLocation getWindmillBladeSkin() {
			return TEXTURE_BLADE_WOOD;
		}

		@Override
		protected void pitchDoAttackEntity(EntityLivingBase target, Vec3d center, DamageSource ds, float damage) {
			super.pitchDoAttackEntity(target, center, ds, damage * 0.5f);
			target.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 20, 3));
			Vec3d vec = new Vec3d(target.posX, target.posY + target.height / 2, target.posZ);
			Vec3d tar = center.subtract(vec).normalize().scale(0.5);
			target.motionX += tar.x;
			target.motionZ += tar.z;
		}

	}

	public static class CRYSTAL extends ItemWindmillBlade {

		public CRYSTAL() {
			super("crystal", 24 * 60 * 60);
			bladeDamage = 3.5f;
		}

		@Override
		public ElementStack updateOnce(World world, BlockPos pos, ItemStack stack, float speed, int tally) {
			ElementStack estack = super.updateOnce(world, pos, stack, speed, tally);
			if (estack.isEmpty()) return estack;
			estack.weaken(1.2f);
			return estack;
		}

		@Override
		@SideOnly(Side.CLIENT)
		public ResourceLocation getWindmillBladeSkin() {
			return TEXTURE_BLADE_CRYSTAL;
		}

		@Override
		public boolean isWindmillBladeSkinNeedBlend() {
			return true;
		}

		@Override
		public float bladeWindScale(World world, BlockPos pos, ItemStack stack) {
			return super.bladeWindScale(world, pos, stack) + 0.25f;
		}

	}
}

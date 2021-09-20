package yuzunyannn.elementalsorcery.item;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.util.text.TextHelper;

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

	}

	public static class WOOD extends ItemWindmillBlade {

		public WOOD() {
			super("wood", 1 * 60 * 60);
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

	}

	public static class CRYSTAL extends ItemWindmillBlade {

		public CRYSTAL() {
			super("crystal", 24 * 60 * 60);
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

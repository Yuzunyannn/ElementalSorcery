package yuzunyannn.elementalsorcery.grimoire.mantra;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStone;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.util.WorldTarget;
import yuzunyannn.elementalsorcery.block.BlockFluorspar;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.grimoire.ICaster;
import yuzunyannn.elementalsorcery.grimoire.IMantraData;
import yuzunyannn.elementalsorcery.grimoire.MantraDataCommon;
import yuzunyannn.elementalsorcery.init.ESInit;

public class MantraFluorspar extends MantraCommon {

	public MantraFluorspar() {
		this.setTranslationKey("fluorspar");
		this.setColor(0xf6e069);
		this.setIcon("fluorspar");
		this.setRarity(95);
		this.setOccupation(2);
		this.setDirectLaunchFragmentMantraLauncher(new ElementStack(ESInit.ELEMENTS.METAL, 20, 40), 2, 0.005, null);
	}

	@Override
	public void potentAttack(World world, ItemStack grimoire, ICaster caster, Entity target) {
		super.potentAttack(world, grimoire, caster, target);
		if (!target.isNonBoss()) return;

		if (target instanceof EntityLivingBase) {
			ElementStack stack = getElement(caster, ESInit.ELEMENTS.METAL, 3, 30);
			if (stack.isEmpty()) return;
			int time = 20 * stack.getPower() / 8 + 20 * 3;
			((EntityLivingBase) target).addPotionEffect(new PotionEffect(MobEffects.GLOWING, time));
		}

	}

	@Override
	public void startSpelling(World world, IMantraData data, ICaster caster) {
		MantraDataCommon dataEffect = (MantraDataCommon) data;
		ElementStack need = new ElementStack(ESInit.ELEMENTS.METAL, 2, 40);
		ElementStack stack = caster.iWantSomeElement(need, false);
		dataEffect.markContinue(!stack.isEmpty());
	}

	@Override
	public void onSpelling(World world, IMantraData data, ICaster caster) {
		MantraDataCommon mdc = (MantraDataCommon) data;
		int maxTick = caster.iWantBePotent(0.1f, true) > 0.2f ? 5 : 20;
		mdc.setProgress(caster.iWantKnowCastTick(), maxTick);
		super.onSpelling(world, data, caster);
	}

	@Override
	public void endSpelling(World world, IMantraData data, ICaster caster) {
		MantraDataCommon mdc = (MantraDataCommon) data;
		if (mdc.getProgress() < 1) return;
		WorldTarget result = caster.iWantBlockTarget();
		if (result.getPos() == null) return;
		BlockPos pos = result.getPos();
		IBlockState originState = world.getBlockState(pos);
		if (getChange(originState) == null) return;

		EnumFacing facing = result.getFace();
		float potent = caster.iWantBePotent(0.5f, true);
		ElementStack stack = ElementStack.EMPTY;
		boolean superSpell = false;
		if (potent >= 0.5f && facing != null) {
			stack = getElement(caster, ESInit.ELEMENTS.METAL, 8, 40);
			if (!stack.isEmpty()) {
				superSpell = true;
				caster.iWantBePotent(0.5f, false);
			}
		}
		if (stack.isEmpty()) stack = getElement(caster, ESInit.ELEMENTS.METAL, 2, 40);
		if (stack.isEmpty()) return;

		doFluorsparChange(world, pos);

		if (superSpell) doFluorsparChangeSuper(world, pos, facing, 30);

	}

	@Override
	public boolean afterSpelling(World world, IMantraData data, ICaster caster) {
		if (world.isRemote) return false;
		MantraDataCommon mdc = (MantraDataCommon) data;
		ElementStack eStack = mdc.get(ESInit.ELEMENTS.METAL);
		if (eStack.isEmpty()) return false;
		BlockPos pos = caster.iWantDirectCaster().getPosition();
		doFluorsparChangeSuper(world, pos, EnumFacing.UP,
				(int) Math.min(128, ((eStack.getCount() - 30) / 2 + 30) * (eStack.getPower() / 100f + 1)));
		return false;
	}

	@Nullable
	public static IBlockState getChange(IBlockState state) {
		IBlockState fluorsparState = ESInit.BLOCKS.FLUORSPAR.getDefaultState();
		Block block = state.getBlock();
		if (block == Blocks.STONE) {
			switch (state.getValue(BlockStone.VARIANT)) {
			case STONE:
				return fluorsparState.withProperty(BlockFluorspar.VARIANT, BlockFluorspar.EnumType.STONE);
			case ANDESITE:
				return fluorsparState.withProperty(BlockFluorspar.VARIANT, BlockFluorspar.EnumType.ANDESITE);
			case GRANITE:
				return fluorsparState.withProperty(BlockFluorspar.VARIANT, BlockFluorspar.EnumType.GRANITE);
			case DIORITE:
				return fluorsparState.withProperty(BlockFluorspar.VARIANT, BlockFluorspar.EnumType.DIORITE);
			default:
				break;
			}
		}
		if (block == Blocks.DIRT || block == Blocks.GRASS)
			return fluorsparState.withProperty(BlockFluorspar.VARIANT, BlockFluorspar.EnumType.DIRT);
		if (block == Blocks.COBBLESTONE)
			return fluorsparState.withProperty(BlockFluorspar.VARIANT, BlockFluorspar.EnumType.COBBLESTONE);
		if (block == Blocks.NETHERRACK)
			return fluorsparState.withProperty(BlockFluorspar.VARIANT, BlockFluorspar.EnumType.NETHERRACK);
		return null;
	}

	public static EnumFacing rotate1(EnumFacing facing) {
		switch (facing) {
		case NORTH:
			return EnumFacing.WEST;
		case WEST:
			return EnumFacing.SOUTH;
		case SOUTH:
			return EnumFacing.EAST;
		case EAST:
			return EnumFacing.NORTH;
		case UP:
			return EnumFacing.WEST;
		case DOWN:
			return EnumFacing.EAST;
		}
		return EnumFacing.NORTH;
	}

	public static EnumFacing rotate2(EnumFacing facing) {
		switch (facing) {
		case NORTH:
			return EnumFacing.UP;
		case WEST:
			return EnumFacing.UP;
		case SOUTH:
			return EnumFacing.UP;
		case EAST:
			return EnumFacing.UP;
		case UP:
			return EnumFacing.SOUTH;
		case DOWN:
			return EnumFacing.NORTH;
		}
		return EnumFacing.NORTH;
	}

	public void doFluorsparChangeSuper(World world, BlockPos pos, EnumFacing facing, int count) {
		Random rand = world.rand;
		for (int i = 0; i < count; i++) {
			float theta = rand.nextFloat() * 3.1415926f * 2;
			float r = rand.nextFloat() * Math.min(i * 4, count / 2 + 1) + 2;

			float a = MathHelper.cos(theta) * r;
			float b = MathHelper.sin(theta) * r;

			BlockPos at = pos.offset(rotate1(facing), (int) a).offset(rotate2(facing), (int) b);
			at = at.offset(facing, -2);
			for (int n = 0; n < 4; n++) {
				if (world.isAirBlock(at.offset(facing, 1))) {
					doFluorsparChange(world, at);
					break;
				}
				at = at.offset(facing, 1);
			}
		}
	}

	public void doFluorsparChange(World world, BlockPos pos) {
		IBlockState state = getChange(world.getBlockState(pos));
		if (state == null) return;
		world.setBlockState(pos, state);
		if (world.isRemote) MantraBlockCrash.addBlockElementEffect(new Vec3d(pos).add(0.5, 0.5, 0.5), getColor(null));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onSpellingEffect(World world, IMantraData data, ICaster caster) {
		super.onSpellingEffect(world, data, caster);
		addEffectIndicatorEffect(world, data, caster);
	}

}

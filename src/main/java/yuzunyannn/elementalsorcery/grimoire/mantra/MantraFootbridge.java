package yuzunyannn.elementalsorcery.grimoire.mantra;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.function.Function;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.entity.EntityBlockMove;
import yuzunyannn.elementalsorcery.grimoire.ICaster;
import yuzunyannn.elementalsorcery.grimoire.ICasterObject;
import yuzunyannn.elementalsorcery.grimoire.IMantraData;
import yuzunyannn.elementalsorcery.grimoire.MantraDataCommon;
import yuzunyannn.elementalsorcery.grimoire.MantraDataCommon.CollectResult;
import yuzunyannn.elementalsorcery.grimoire.WantedTargetResult;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.util.VariableSet;
import yuzunyannn.elementalsorcery.util.VariableSet.Variable;

public class MantraFootbridge extends MantraCommon {

	public static final Variable<LinkedList<BlockPos>> POS_LIST = new Variable<>("@posList",
			VariableSet.BLOCK_POS_LIST_LINKED);

	public MantraFootbridge() {
		this.setUnlocalizedName("footbridge");
		this.setColor(0x00e682);
		this.setIcon("footbridge");
		this.setRarity(80);
		this.setOccupation(3);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onSpellingEffect(World world, IMantraData data, ICaster caster) {
		super.onSpellingEffect(world, data, caster);
		addEffectIndicatorEffect(world, data, caster);
	}

	@Override
	public void onCollectElement(World world, IMantraData data, ICaster caster, int speedTick) {
		if (beforeGeneralStartTime(caster)) return;
		MantraDataCommon mData = (MantraDataCommon) data;
		WantedTargetResult wr = caster.iWantBlockTarget();
		int max = 96;
		Vec3d pos = wr.getHitVec();
		if (pos != null) {
			ICasterObject co = caster.iWantCaster();
			double dis = co.getPositionVector().distanceTo(pos);
			max = MathHelper.ceil(Math.min(96, 3 * dis));
		}
		CollectResult cr = mData.tryCollect(caster, ESInit.ELEMENTS.EARTH, 1, 25, max);
		mData.setProgress(cr.getStackCount(), max);
		if (!cr.getElementStack().isEmpty()) mData.markContinue(true);
	}

	@Override
	public void endSpelling(World world, IMantraData data, ICaster caster) {
		if (world.isRemote) return;

		MantraDataCommon mData = (MantraDataCommon) data;
		BlockPos pos1, pos2;
		ICasterObject co = caster.iWantCaster();
		pos1 = co.getPosition().down();
		WantedTargetResult wr = caster.iWantBlockTarget();
		pos2 = wr.getPos();

		if (pos1 == null || pos2 == null) return;

		float potent = caster.iWantBePotent(1.75f, false);
		mData.set(POTENT_POWER, potent);

		Set<BlockPos> posSet = new HashSet<BlockPos>();
		LinkedList<BlockPos> posList = new LinkedList<BlockPos>();
		ElementStack estack = mData.get(ESInit.ELEMENTS.EARTH);
		int power = estack.getPower();
		Vec3d start = new Vec3d(pos1);
		Vec3d end = new Vec3d(pos2);
		Vec3d tar = end.subtract(start).normalize().scale(0.5);
		Vec3d nTar1 = new Vec3d(-tar.z, 0, tar.x);
		Vec3d nTar2 = new Vec3d(tar.z, 0, -tar.x);

		Function<BlockPos, Void> adder = p -> {
			if (world.isAirBlock(p) && !posSet.contains(p)) {
				posList.add(p);
				posSet.add(p);
			}
			return null;
		};

		while (start.squareDistanceTo(end) > 1 && !estack.isEmpty()) {
			int shrink = (int) (3 - Math.min((potent * 2), 2));
			estack.shrink(shrink);
			for (int i = 0; i < 2; i++) {
				start = start.add(tar);
				BlockPos pos = new BlockPos(start);
				adder.apply(pos);
				pos = new BlockPos(start.add(nTar1));
				adder.apply(pos);
				pos = new BlockPos(start.add(nTar1).add(nTar1));
				adder.apply(pos);
				pos = new BlockPos(start.add(nTar2));
				adder.apply(pos);
				pos = new BlockPos(start.add(nTar2).add(nTar2));
				adder.apply(pos);
			}
		}
		if (posList.isEmpty()) return;
		mData.set(POS_LIST, posList);
		mData.set(POWER, power);
	}

	@Override
	public boolean afterSpelling(World world, IMantraData data, ICaster caster) {
		if (world.isRemote) return true;
		MantraDataCommon mData = (MantraDataCommon) data;

		int times = (int) (mData.get(POTENT_POWER) * 4 + 1);

		for (int n = 0; n < times; n++) {
			if (!mData.has(POS_LIST)) return false;
			LinkedList<BlockPos> posList = mData.get(POS_LIST);
			if (posList.isEmpty()) return false;
			BlockPos pos = posList.pop();
			if (posList.isEmpty()) mData.remove(POS_LIST);

			int power = mData.get(POWER);

			BlockPos fly = pos;
			for (int i = 0; i <= 100 && fly != null && fly.getY() > 0; i++) {
				fly = fly.down();
				if (world.isAirBlock(fly)) continue;
				IBlockState state = world.getBlockState(fly);
				Block block = state.getBlock();
				if (block.isReplaceable(world, fly)) continue;
				if (!state.isOpaqueCube()) continue;
				if (block.hasTileEntity(state)) fly = null;
				float hardness = block.getBlockHardness(state, world, fly);
				if (hardness <= 0 || hardness > Math.max(power / 20, 3)) fly = null;
				if (!state.isFullBlock() || !state.isFullCube()) fly = null;
				break;
			}

			if (fly == null) return true;

			EntityBlockMove blockMove = new EntityBlockMove(world, fly, pos);
			blockMove.setColor(this.getColor(data));
			world.spawnEntity(blockMove);
			world.setBlockToAir(fly);
		}

		return true;
	}

}

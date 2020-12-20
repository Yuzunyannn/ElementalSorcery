package yuzunyannn.elementalsorcery.grimoire.mantra;

import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockStone;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.entity.EntityBlockMove;
import yuzunyannn.elementalsorcery.grimoire.ICaster;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.util.VariableSet;
import yuzunyannn.elementalsorcery.util.block.BlockHelper;
import yuzunyannn.elementalsorcery.util.render.RenderObjects;

public class MantraMiningArea extends MantraSquareAreaAdv {

	public static final VariableSet.Variable<Short> LAYER = new VariableSet.Variable<Short>("layer", VariableSet.SHORT);

	public MantraMiningArea() {
		this.setUnlocalizedName("miningArea");
		this.setRarity(2);
		this.setColor(0xc8971e);
		this.addElementCollect(new ElementStack(ESInit.ELEMENTS.EARTH, 2, 75), 400, 32);
		this.addElementCollect(new ElementStack(ESInit.ELEMENTS.METAL, 3, 50), -1, 32);
	}

	@Override
	public int getAccumulatePreTick() {
		return 10;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ResourceLocation getMagicCircleIcon() {
		return RenderObjects.MAGIC_CIRCLE_PICKAXE;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ResourceLocation getIconResource() {
		return RenderObjects.MANTRA_AUTO_MINING;
	}

	@Override
	public void init(World world, SquareDataAdv data, ICaster caster, BlockPos pos) {
		ElementStack earth = data.getElement(ESInit.ELEMENTS.EARTH);
		data.setSize(Math.min(earth.getPower() / 200, 4) * 2 + 8);
		data.set(LAYER, (short) (pos.getY() - 1));
	}

	@Override
	public boolean tick(World world, SquareDataAdv data, ICaster caster, BlockPos originPos) {
		int tick = caster.iWantKnowCastTick();
		ElementStack earth = data.getElement(ESInit.ELEMENTS.EARTH);
		ElementStack metal = data.getElement(ESInit.ELEMENTS.METAL);
		if (earth.isEmpty() || metal.isEmpty()) return false;
		if (tick % 20 != 0) return true;
		short layer = data.get(LAYER);
		int hSize = data.size / 2;
		BlockPos pos = new BlockPos(originPos.getX(), layer, originPos.getZ());
		BlockPos go = findChestPos(world, originPos);
		for (int x = -hSize; x < hSize; x++) {
			for (int z = -hSize; z < hSize; z++) {
				BlockPos at = pos.add(x, 0, z);
				if (world.isAirBlock(at)) continue;
				IBlockState state = world.getBlockState(at);
				if (state.getBlock() instanceof BlockStone) continue;
				if (state.getBlock() instanceof BlockSand) continue;
				if (state.getBlock() instanceof BlockStone) continue;
				if (state.getBlock() instanceof BlockDirt) continue;
				if (state.getBlock() instanceof BlockGrass) continue;
				if (state.getBlock() instanceof BlockRotatedPillar) continue;
				if (this.canGet(world, at, state, data)) {
					metal.shrink(1);
					BlockPos flyPos;
					if (go != null) flyPos = go;
					else flyPos = new BlockPos(at.getX(), originPos.getY(), at.getZ());
					EntityBlockMove move = new EntityBlockMove(world, at, flyPos);
					move.setFlag(EntityBlockMove.FLAG_FORCE_DROP, true);
					move.setColor(0xc8971e);
					move.getTrace().setOrder(move.getRNG().nextBoolean() ? "yxz" : "yzx");
					world.spawnEntity(move);
					world.setBlockToAir(at);
					data.setDelay((int) (move.getTrace().getTotalLength() / 5 * 20));
				}
			}
		}
		data.set(LAYER, (short) (layer - 1));
		earth.shrink(2);
		return true;
	}

	public BlockPos findChestPos(World world, BlockPos center) {
		for (int x = -2; x <= 2; x++) {
			for (int z = -2; z <= 2; z++) {
				BlockPos pos = center.add(x, 0, z);
				if (world.isAirBlock(pos)) continue;
				TileEntity tile = world.getTileEntity(pos);
				if (tile == null) continue;
				if (tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null) != null) return pos;
			}
		}
		return null;
	}

	public boolean canGet(World world, BlockPos pos, IBlockState state, SquareDataAdv data) {
		return state.getBlock() == ESInit.BLOCKS.SEAL_STONE || BlockHelper.isOre(state);
	}

}

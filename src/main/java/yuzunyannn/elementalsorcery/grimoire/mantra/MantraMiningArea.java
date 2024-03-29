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
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.mantra.ICaster;
import yuzunyannn.elementalsorcery.entity.EntityBlockMove;
import yuzunyannn.elementalsorcery.util.helper.OreHelper;
import yuzunyannn.elementalsorcery.util.render.RenderObjects;

public class MantraMiningArea extends MantraTypeSquareArea {

	public MantraMiningArea() {
		this.setTranslationKey("miningArea");
		this.setColor(0xc8971e);
		this.setIcon("auto_mining");
		this.setRarity(25);
		this.setOccupation(10);
		this.setAccumulatePreTick(10);
		this.addElementCollect(new ElementStack(ESObjects.ELEMENTS.EARTH, 2, 75), 400, 32);
		this.addElementCollect(new ElementStack(ESObjects.ELEMENTS.METAL, 3, 50), -1, 32);
		this.initAndAddDefaultMantraLauncher(0.0005);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ResourceLocation getMagicCircleIcon() {
		return RenderObjects.MAGIC_CIRCLE_PICKAXE;
	}

	@Override
	public void init(World world, SquareData data, ICaster caster, BlockPos pos) {
		ElementStack earth = data.get(ESObjects.ELEMENTS.EARTH);
		float rate = 1f * caster.iWantBePotent(2f, false) + 1;
		data.setSize(Math.min(earth.getPower() / 200, 4) * 2 * rate + 8);
		data.set(LAYER, (short) (pos.getY() - 1));
	}

	@Override
	public boolean tick(World world, SquareData data, ICaster caster, BlockPos originPos) {
		if (world.isRemote) return true;
		int tick = caster.iWantKnowCastTick();
		ElementStack earth = data.get(ESObjects.ELEMENTS.EARTH);
		ElementStack metal = data.get(ESObjects.ELEMENTS.METAL);
		if (earth.isEmpty() || metal.isEmpty()) return false;
		short layer = data.get(LAYER);
		if (layer < 0) return false;
		if (tick % 20 != 0) return true;
		int hSize = data.getSize() / 2;
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

	public boolean canGet(World world, BlockPos pos, IBlockState state, SquareData data) {
		return state.getBlock() == ESObjects.BLOCKS.SEAL_STONE || OreHelper.isOre(state);
	}

}

package yuzunyannn.elementalsorcery.grimoire.mantra;

import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockStone;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import yuzunyannn.elementalsorcery.element.Element;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.entity.EntityBlockMove;
import yuzunyannn.elementalsorcery.grimoire.ICaster;
import yuzunyannn.elementalsorcery.grimoire.IMantraData;
import yuzunyannn.elementalsorcery.grimoire.MantraDataCommon;
import yuzunyannn.elementalsorcery.grimoire.MantraDataCommon.ConditionEffect;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.render.effect.grimoire.EffectMagicCircle;
import yuzunyannn.elementalsorcery.render.effect.grimoire.EffectMagicCircleIcon;
import yuzunyannn.elementalsorcery.render.effect.grimoire.EffectMagicSquare;
import yuzunyannn.elementalsorcery.util.block.BlockHelper;
import yuzunyannn.elementalsorcery.util.render.RenderObjects;

public class MantraAutoMining extends MantraCommon {

	public static final int MAX_SPELL_TICK = 400;

	public static class Data extends MantraDataCommon {
		protected ElementStack metal = new ElementStack(ESInit.ELEMENTS.METAL, 0); // 一个矿消耗一个金
		protected ElementStack earth = new ElementStack(ESInit.ELEMENTS.EARTH, 0); // 一层消耗两个地
		protected int size = 0;
		protected int layer = 0;
		protected int delay = 0;

		@Override
		public NBTTagCompound serializeNBT() {
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setTag("metal", metal.serializeNBT());
			nbt.setTag("earth", earth.serializeNBT());
			nbt.setInteger("size", size);
			nbt.setInteger("layer", layer);
			nbt.setInteger("delay", delay);
			return nbt;
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt) {
			metal = new ElementStack(nbt.getCompoundTag("metal"));
			earth = new ElementStack(nbt.getCompoundTag("earth"));
			size = nbt.getInteger("size");
			layer = nbt.getInteger("layer");
			delay = nbt.getInteger("delay");
		}
	}

	public MantraAutoMining() {
		this.setUnlocalizedName("autoMining");
		this.setRarity(2);
		this.setColor(0xc8971e);
	}

	@Override
	public Element getMagicCircle() {
		return ESInit.ELEMENTS.METAL;
	}

	@Override
	public ResourceLocation getIconResource() {
		return RenderObjects.MANTRA_AUTO_MINING;
	}

	@Override
	public IMantraData getData(NBTTagCompound origin, World world, ICaster caster) {
		return new Data();
	}

	@Override
	public void startSpelling(World world, IMantraData data, ICaster caster) {
		MantraDataCommon dataEffect = (MantraDataCommon) data;
		dataEffect.markContinue(true);
	}

	@Override
	public float getProgressRate(World world, IMantraData mData, ICaster caster) {
		int tick = caster.iWantKnowCastTick();
		if (tick < 20) return -1;
		Data data = (Data) mData;
		return data.earth.getCount() / (float) MAX_SPELL_TICK;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public EffectMagicCircle getEffectMagicCircle(World world, EntityLivingBase entity, IMantraData mData) {
		EffectMagicCircle emc = new EffectMagicCircleIcon(world, entity, RenderObjects.MAGIC_CIRCLE_PICKAXE);
		emc.setColor(this.getRenderColor(mData));
		return emc;
	}

	@Override
	public void onSpelling(World world, IMantraData mData, ICaster caster) {
		int tick = caster.iWantKnowCastTick();
		if (tick < 20) return;
		Data data = (Data) mData;
		if (data.earth.getCount() > MAX_SPELL_TICK) {
			super.onSpelling(world, mData, caster);
			return;
		}
		if (tick % 10 != 0) return;
		out: {
			ElementStack need = new ElementStack(ESInit.ELEMENTS.EARTH, 2, 50);
			ElementStack stack = caster.iWantSomeElement(need, true);
			if (stack.isEmpty()) break out;
			data.earth.grow(stack);
		}
		if (data.earth.isEmpty()) return;
		out: {
			ElementStack need = new ElementStack(ESInit.ELEMENTS.METAL, 3, 50);
			ElementStack stack = caster.iWantSomeElement(need, true);
			if (stack.isEmpty()) break out;
			data.metal.grow(stack);
		}
		super.onSpelling(world, mData, caster);
	}

	@Override
	public void endSpelling(World world, IMantraData mData, ICaster caster) {
		int tick = caster.iWantKnowCastTick();
		if (tick < 20) return;
		Data data = (Data) mData;
		if (data.metal.getCount() < 32 || data.earth.getCount() < 32) return;
		Entity user = caster.iWantCaster();
		Entity entity = caster.iWantDirectCaster();
		BlockPos pos = user.getPosition();
		entity.setPosition(pos.getX(), pos.getY(), pos.getZ());
		data.size = Math.min(data.earth.getPower() / 100, 8) + 8;
		data.layer = pos.getY() - 1;
	}

	@Override
	public boolean afterSpelling(World world, IMantraData mData, ICaster caster) {
		Data data = (Data) mData;
		if (world.isRemote) {
			this.addAfterEffect(data, caster, data.size);
			return true;
		}
		if (data.size <= 0 || data.layer <= 0 || data.earth.isEmpty() || data.metal.isEmpty()) {
			if (data.delay <= 0) return false;
			data.delay = data.delay - 1;
			return true;
		}
		int tick = caster.iWantKnowCastTick();
		if (tick % 20 == 0) {
			Entity entity = caster.iWantDirectCaster();
			int hSize = data.size / 2;
			BlockPos pos = new BlockPos(entity.posX, data.layer, entity.posZ);
			BlockPos go = findChestPos(world, entity.getPosition());
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
						data.metal.shrink(1);
						BlockPos flyPos;
						if (go != null) flyPos = go;
						else flyPos = new BlockPos(at.getX(), entity.posY, at.getZ());
						EntityBlockMove move = new EntityBlockMove(world, at, flyPos);
						move.setFlag(EntityBlockMove.FLAG_FORCE_DROP, true);
						move.setColor(0xc8971e);
						move.getTrace().setOrder(move.getRNG().nextBoolean() ? "yxz" : "yzx");
						world.spawnEntity(move);
						world.setBlockToAir(at);
						data.delay = (int) (move.getTrace().getTotalLength() / 5 * 20);
					}
				}
			}
			data.layer = data.layer - 1;
			data.earth.shrink(2);
		}
		return true;
	}

	@SideOnly(Side.CLIENT)
	public void addAfterEffect(Data data, ICaster caster, int size) {
		if (size <= 0) return;
		if (data.hasMarkEffect(1000)) return;
		Entity entity = caster.iWantDirectCaster();
		EffectMagicSquare ems = new EffectMagicSquare(entity.world, entity, size, this.getRenderColor());
		ems.setCondition(new ConditionEffect(entity, data, 1000, false));
		data.addEffect(caster, ems, 1000);
		ems.setIcon(RenderObjects.MAGIC_CIRCLE_PICKAXE);
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

	public boolean canGet(World world, BlockPos pos, IBlockState state, Data data) {
		return state.getBlock() == ESInit.BLOCKS.SEAL_STONE || BlockHelper.isOre(state);
	}

}

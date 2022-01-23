package yuzunyannn.elementalsorcery.tile.altar;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import yuzunyannn.elementalsorcery.api.tile.IAltarWake;
import yuzunyannn.elementalsorcery.api.tile.IGetItemStack;
import yuzunyannn.elementalsorcery.building.Buildings;
import yuzunyannn.elementalsorcery.building.MultiBlock;
import yuzunyannn.elementalsorcery.crafting.element.ElementMap;
import yuzunyannn.elementalsorcery.element.Element;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.batch.EffectElementMove;
import yuzunyannn.elementalsorcery.util.element.ElementAnalysisPacket;
import yuzunyannn.elementalsorcery.util.item.ItemHandlerAdapter;

public class TileDisintegrateStela extends TileStaticMultiBlock implements ITickable, IGetItemStack {

	/** 仓库的处理马甲 */
	protected IItemHandler invVest = new ItemHandlerAdapter() {

		public int getSlots() {
			return 1;
		};

		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
			if (disintegrate(stack, simulate)) return ItemStack.EMPTY;
			return stack;
		};
	};

	public static final int SEND_DATA_CD_INTERVAL = 30;

	protected int tick = 0;
	/** 分解的队列 */
	protected LinkedList<ElementAnalysisPacket> eaPacketStack = new LinkedList<>();
	/** 将数据同步到client展示特效的剩余cd */
	public int tickSendDataCD = 0;
	/** server计算展示数据的map */
	protected Map<Integer, Integer> tickSendMap = new HashMap<>();
	/** 超载比例 0-2 */
	protected float overLoad = 0;
	/** 服务端处理要不要更新，客户端进行渲染 */
	public float prevOverLoad = 0;

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.equals(capability)) return (T) invVest;
		return super.getCapability(capability, facing);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.equals(capability)) return invVest != null;
		return super.hasCapability(capability, facing);
	}

	@Override
	public void initMultiBlock() {
		structure = new MultiBlock(Buildings.DISINTEGRATE_ALTAR, this, new BlockPos(0, -4, 0));
		structure.addSpecialBlock(new BlockPos(10, 1, 10));
		structure.addSpecialBlock(new BlockPos(-10, 1, 10));
		structure.addSpecialBlock(new BlockPos(-10, 1, -10));
		structure.addSpecialBlock(new BlockPos(10, 1, -10));
		structure.addSpecialBlock(new BlockPos(10, 1, 0));
		structure.addSpecialBlock(new BlockPos(0, 1, 10));
		structure.addSpecialBlock(new BlockPos(-10, 1, 0));
		structure.addSpecialBlock(new BlockPos(0, 1, -10));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setFloat("overload", overLoad);
		return super.writeToNBT(compound);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		overLoad = compound.getFloat("overload");
		super.readFromNBT(compound);
	}

	@Override
	public ItemStack getStack() {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canSetStack(ItemStack stack) {
		return disintegrate(stack, true);
	}

	@Override
	public void setStack(ItemStack stack) {
		disintegrate(stack);
	}

	public boolean disintegrate(ItemStack stack) {
		return disintegrate(stack, false);
	}

	public boolean disintegrate(ItemStack stack, boolean simulate) {
		if (!isIntact()) return false;
		if (world.isRemote) return true;
		ElementAnalysisPacket packet = TileAnalysisAltar.analysisItem(stack, ElementMap.instance, true);
		if (packet == null) return false;
		if (simulate) return true;
		eaPacketStack.addLast(packet);
		return true;
	}

	@Override
	public void update() {
		tick++;

		isAndCheckIntact();

		if (world.isRemote) {
			this.updateClient();
			return;
		}

		while (!eaPacketStack.isEmpty()) {
			ElementAnalysisPacket packet = eaPacketStack.removeFirst();
			if (packet.daStack.isEmpty()) continue;
			ElementStack[] estacks = packet.element();
			if (estacks == null) continue;
			for (ElementStack estack : estacks) {
				estack.setCount(estack.getCount() * packet.daStack.getCount());
				estack = estack.onDeconstruct(world, packet.daStack, packet.complex(), Element.DP_ALTAR_ADV).copy();
				if (estack.isEmpty()) continue;
				int posIndex = doSendElement(estack) + 1;
				int id = Element.getIdFromElement(estack.getElement());
				int idKey = (id << 4) | (posIndex & 0xf);
				Integer oCount = tickSendMap.get(idKey);
				tickSendMap.put(idKey, (oCount == null ? 0 : oCount.intValue()) + estack.getCount());
			}
		}

		if (tickSendDataCD > 0) {
			tickSendDataCD--;
			return;
		}

		if (tickSendMap.isEmpty() && prevOverLoad == overLoad) return;
		tickSendDataCD = SEND_DATA_CD_INTERVAL;
		updateDeconstructDataToClient();
	}

	/**
	 * 将分解的元素存入周围的元素容器
	 * 
	 * @return 存入的位置index
	 */
	protected int doSendElement(ElementStack estack) {
		return putElementToSpPlace(estack, this.pos);
	}

	private void updateDeconstructDataToClient() {
		NBTTagCompound nbt = new NBTTagCompound();

		if (!tickSendMap.isEmpty()) {
			int[] ints = new int[tickSendMap.size() * 2];
			int i = 0;
			for (Entry<Integer, Integer> entry : tickSendMap.entrySet()) {
				ints[i++] = entry.getKey();
				ints[i++] = entry.getValue();
			}
			nbt.setIntArray("DDS", ints);
			tickSendMap.clear();
		}

		if (prevOverLoad != overLoad) {
			nbt.setFloat("DOL", overLoad);
			prevOverLoad = overLoad;
		}

		if (nbt.hasNoTags()) return;

		updateToClient(nbt);
	}

	@Override
	public void handleUpdateTag(NBTTagCompound tag) {
		if (!tag.hasKey("DDS") && !tag.hasKey("DOL")) {
			super.handleUpdateTag(tag);
			return;
		}
		if (tag.hasKey("DOL")) overLoad = tag.getFloat("DOL");
		if (!tag.hasKey("DDS")) return;
		// 根据传过来的数据，计算最佳的list
		// 比如。每隔 x tick传输一次，就要尽可能的平均分成x次到client处理特效的帧内
		final int sendDataCDInterval = SEND_DATA_CD_INTERVAL / 2;
		int[] ints = tag.getIntArray("DDS");
		effectList.clear();
		tickSendMap.clear();
		for (int i = 0; i < ints.length; i += 2) {
			int idKey = ints[i];
			int count = ints[i + 1];
			tickSendMap.put(idKey, count);
		}
		int n = tickSendMap.size();
		int averageSplit = MathHelper.ceil(sendDataCDInterval / (float) n);
		for (Entry<Integer, Integer> entry : tickSendMap.entrySet()) {
			if (effectList.size() >= sendDataCDInterval) break;
			int idKey = entry.getKey();
			int count = entry.getValue();
			int splitCount = Math.max(10, count / averageSplit);
			int m = MathHelper.ceil(count / (float) splitCount);
			for (int i = 0; i < m; i++) {
				int cc = Math.min(count, splitCount);
				effectList.addLast(new AbstractMap.SimpleEntry(idKey, cc));
				count -= splitCount;
			}
		}
	}

	protected LinkedList<Entry<Integer, Integer>> effectList = new LinkedList();
	public float roate, prevRoate;
	public float animeRate = 0; // 动画比例，转的快还是慢
	public float wakeRate = 0, prevWakeRate = 0; // 是否起来可以旋转

	@SideOnly(Side.CLIENT)
	public float getOverLoadRate() {
		return overLoad;
	}

	@SideOnly(Side.CLIENT)
	private void updateClient() {
		prevOverLoad = overLoad;
		prevRoate = roate;
		prevWakeRate = wakeRate;

		if (isIntact()) {
			wakeRate = Math.min(1, wakeRate + (1 - wakeRate) * 0.1f);
			roate += 0.01f * (1 - animeRate) + 0.2f * animeRate;
		} else {
			float r = wakeRate - 0.01f;
			wakeRate = Math.max(0, r * r);
		}

		genActiveEffect();
		if (overLoad > 1) genOverLoadEffect();
		if (tick % 2 != 0) return;

		if (effectList.isEmpty()) {
			animeRate = Math.max(0, animeRate - 0.05f);
			return;
		}

		animeRate = 1;

		Entry<Integer, Integer> entry = effectList.removeFirst();
		int idKey = entry.getKey();
		ElementStack estack = new ElementStack(Element.getElementFromId(idKey >> 4), entry.getValue(), 1);
		if (estack.isEmpty()) return;
		int posIndex = (idKey & 0xf) - 1;

		BlockPos toPos = structure.getSpecialBlockPos(posIndex);
		int n = (int) MathHelper.clamp(entry.getKey() / 75f, 1, 5);
		if (toPos == null) {
			for (int i = 0; i < n; i++) genNowherePlaceEffect(estack);
		} else {
			TileEntity tile = world.getTileEntity(toPos);
			IAltarWake altarWake = getAlterWake(tile);
			if (altarWake != null) altarWake.wake(IAltarWake.OBTAIN, this.pos);
			for (int i = 0; i < n; i++) genParticleElementTo(false, altarWake, estack, this.pos, toPos);
		}
	}

	@SideOnly(Side.CLIENT)
	public void genOverLoadEffect() {
		int n = MathHelper.ceil((MathHelper.clamp(overLoad, 1, 2) - 1) * 4);
		final int[] OVERLOAD_COLORS = new int[] { 0x992500, 0xc22f00, 0xca4619, 0xdc663e };
		for (int i = 0; i < n; i++) {
			Vec3d pos = new Vec3d(this.pos).addVector(0.5, 0.5, 0.5);
			EffectElementMove effect = new EffectElementMove(world, pos);
			effect.lifeTime = 40;
			effect.dalpha = 1f / effect.lifeTime;
			effect.prevScale = effect.scale = 0.2f;
			effect.setColor(OVERLOAD_COLORS[Effect.rand.nextInt(OVERLOAD_COLORS.length)]);
			float sin = MathHelper.sin(tick * 3.1415926f / 20 + i * 3.1415926f / 2);
			float cos = MathHelper.cos(tick * 3.1415926f / 20 + i * 3.1415926f / 2);
			Vec3d speed = new Vec3d(sin, 0, cos);
			effect.setVelocity(new Vec3d(0, 0.4, 0));
			effect.setAccelerate(speed.scale(0.035));
			effect.xDecay = effect.zDecay = effect.yDecay = 0.8;
			Effect.addEffect(effect);
		}
	}

	@SideOnly(Side.CLIENT)
	public void genActiveEffect() {

		EntityPlayerSP player = Minecraft.getMinecraft().player;
		if (player == null) return;
		if (player.getDistanceSq(this.pos) > 64 * 64) return;

		double range = 0.4;
		Vec3d targetVec = new Vec3d(0, 0.2, 0);
		switch (Effect.rand.nextInt(5)) {
		case 1:
			targetVec = new Vec3d(-5.5, -3, 5.5);
			break;
		case 2:
			targetVec = new Vec3d(-5.5, -3, -5.5);
			break;
		case 3:
			targetVec = new Vec3d(5.5, -3, -5.5);
			break;
		case 4:
			targetVec = new Vec3d(5.5, -3, 5.5);
			break;
		}
		Vec3d pos = new Vec3d(this.pos)
				.addVector(Effect.rand.nextGaussian() * range + 0.5, 0.5, Effect.rand.nextGaussian() * range + 0.5)
				.add(targetVec);
		EffectElementMove effect = new EffectElementMove(world, pos);
		final int[] COLORS = new int[] { 0x1b6eb8, 0x196ab5, 0x458ed1, 0x86c0f6 };
		final int[] OVERLOAD_COLORS = new int[] { 0x992500, 0xc22f00, 0xca4619, 0xdc663e };
		int color = 0;
		if (overLoad > Effect.rand.nextFloat()) color = OVERLOAD_COLORS[Effect.rand.nextInt(OVERLOAD_COLORS.length)];
		else color = COLORS[Effect.rand.nextInt(COLORS.length)];
		effect.setColor(color);
		Vec3d speed = new Vec3d(0, Effect.rand.nextDouble(), 0);
		effect.setVelocity(speed.normalize().scale(0.01));
		effect.xDecay = effect.zDecay = effect.yDecay = 0.8;
		Effect.addEffect(effect);

	}

	@SideOnly(Side.CLIENT)
	public void genNowherePlaceEffect(ElementStack elementStack) {
		Vec3d pos = new Vec3d(this.pos).addVector(0.5, 0.5, 0.5);
		EffectElementMove effect = new EffectElementMove(world, pos);
		effect.setColor(elementStack.getColor());
		Vec3d speed = new Vec3d(Effect.rand.nextGaussian(), 0, Effect.rand.nextGaussian());
		effect.setVelocity(speed.normalize().addVector(0, Effect.rand.nextDouble() * 0.5 + 0.5, 0).scale(0.3));
		effect.xDecay = effect.zDecay = effect.yDecay = 0.6;
		Effect.addEffect(effect);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void genParticleElementTo(boolean isGet, IAltarWake altarWake, ElementStack estack, BlockPos from,
			BlockPos to) {
		Vec3d refPos;
		if (isGet) refPos = new Vec3d(to).addVector(0.5, 0.5, 0.5);
		else refPos = new Vec3d(from).addVector(0.5, 0.5, 0.5);
		if (altarWake == null) {
			if (isGet) TileElementalCube.giveParticleElementTo(world, estack.getColor(),
					new Vec3d(from).addVector(0.5, 0.5, 0.5), refPos, 1);
			else TileElementalCube.giveParticleElementTo(world, estack.getColor(), refPos,
					new Vec3d(to).addVector(0.5, 0.5, 0.5), 1);
		} else altarWake.updateEffect(world, isGet ? IAltarWake.SEND : IAltarWake.OBTAIN, estack, refPos);
	}

}

package yuzunyannn.elementalsorcery.tile.altar;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.IGetItemStack;
import yuzunyannn.elementalsorcery.api.crafting.IItemStructure;
import yuzunyannn.elementalsorcery.api.element.Element;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.tile.IAltarWake;
import yuzunyannn.elementalsorcery.api.util.NBTTag;
import yuzunyannn.elementalsorcery.building.Buildings;
import yuzunyannn.elementalsorcery.building.MultiBlock;
import yuzunyannn.elementalsorcery.crafting.element.ElementMap;
import yuzunyannn.elementalsorcery.crafting.element.ItemStructure;
import yuzunyannn.elementalsorcery.element.explosion.ElementExplosion;
import yuzunyannn.elementalsorcery.logics.EventServer;
import yuzunyannn.elementalsorcery.logics.ITickTask;
import yuzunyannn.elementalsorcery.logics.IWorldTickTask;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.batch.EffectElementMove;
import yuzunyannn.elementalsorcery.util.LambdaReference;
import yuzunyannn.elementalsorcery.util.element.ElementAnalysisPacket;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;
import yuzunyannn.elementalsorcery.util.helper.RandomHelper;
import yuzunyannn.elementalsorcery.util.item.ItemHandlerAdapter;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

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
	public static final float OVERLOAD_BELOW_ONE_DECLINE = 0.001f;

	protected int tick = 0;
	/** 分解的队列 */
	protected LinkedList<ElementAnalysisPacket> eaPacketStack = new LinkedList<>();
	/** 将数据同步到client展示特效的剩余cd */
	public int tickSendDataCD = 0;
	/** server计算展示数据的map */
	protected Map<Integer, Integer> tickSendMap = new HashMap<>();
	/** 超载比例 0-2 */
	protected float overload = 0;
	/** 服务端处理要不要更新，客户端进行渲染 */
	public float prevOverload = 0;
	/** 元素超载记录，不写入NBT储存了 */
	protected Map<Element, Float> overloadMap = new HashMap<>();
	/** 超载保护 -Float.MAX_VALUE~1~Float.MAX_VALUE */
	protected float overloadProtect = 0;
	/** 使用的toElement */
	protected ElementMap toElement = null;
	/** 距离超载爆炸的tick记时 */
	protected int overloadExplosionTick = 0;

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
		
		structure.addSpecialBlock(new BlockPos(0, 1, 10));
		structure.addSpecialBlock(new BlockPos(10, 1, 0));
		structure.addSpecialBlock(new BlockPos(0, 1, -10));
		structure.addSpecialBlock(new BlockPos(-10, 1, 0));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setFloat("overload", overload);
		return super.writeToNBT(compound);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		overload = compound.getFloat("overload");
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
		if (overloadExplosionTick > 0) return false;
		if (toElement == null) toElement = ElementMap.instance;
		ElementAnalysisPacket packet = TileAnalysisAltar.analysisItem(stack, toElement, false);
		if (packet == null) return false;
		if (simulate) return true;
		eaPacketStack.addLast(packet);
		ItemStack[] remains = packet.remain();
		if (remains != null && remains.length > 0) {
			for (ItemStack itemStack : remains) ItemHelper.dropItem(world, pos, itemStack.copy());
		}
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

		if (overloadExplosionTick > 0) {
			overloadExplosionTick--;
			if (overloadExplosionTick == 0) {
				BlockPos pos = this.pos;
				world.setBlockToAir(pos);
				doOverloadExplosion(world, pos);
			}
			updateCheckAndSendData();
			return;
		}

		if (overload < 1f) overload = Math.max(0, overload - OVERLOAD_BELOW_ONE_DECLINE);
		else overload = overload - OVERLOAD_BELOW_ONE_DECLINE * 10;

		if (!eaPacketStack.isEmpty()) {

			int lvPower = Element.DP_ALTAR_ADV + (Element.DP_ALTAR_SURPREME - Element.DP_ALTAR_ADV) * 3 / 4;
			float ol = MathHelper.clamp((2 - overload) / 2, 0, 1);
			lvPower = (int) (lvPower * ol * ol * ol);

			// 记录本次分解存在哪些元素的Set
			Set<Element> elementOverloadSet = new HashSet<>();

			while (!eaPacketStack.isEmpty()) {
				ElementAnalysisPacket packet = eaPacketStack.removeFirst();
				if (packet.daStack.isEmpty()) continue;
				ElementStack[] estacks = packet.element();
				if (estacks == null) continue;
				for (ElementStack estack : estacks) {
					// 计算分解
					estack = estack.onDeconstruct(world, packet.daStack, packet.complex(), lvPower).copy();
					if (estack.isEmpty()) continue;
					estack.setCount(estack.getCount() * packet.daStack.getCount());
					// 处理过载
					elementOverloadSet.add(doIncrOverload(estack));
					// 分发
					int posIndex = doSendElement(estack) + 1;
					// 生成发送更新数据
					int id = Element.getIdFromElement(estack.getElement());
					int idKey = (id << 4) | (posIndex & 0xf);
					Integer oCount = tickSendMap.get(idKey);
					tickSendMap.put(idKey, (oCount == null ? 0 : oCount.intValue()) + estack.getCount());
				}
			}

			// 降低所有本次分解处理中，不包括的元素
			reduceOverloadMap(0.8f, e -> elementOverloadSet.contains(e));

			if (overload >= 2) overloadExplosionTick = 20 * 4;
		}

		// 再过载大于1时，会强行降低overload数据
		if (tick % 20 == 0) {
			float olp = overloadProtect;
			if (overload > 1) {
				if (olp < 0) reduceOverloadMap(0.5f - Math.max(olp, -0.45f), e -> false);
				else reduceOverloadMap(0.5f / (olp + 1), e -> false);
			} else if (olp > 0.25f) reduceOverloadMap(1 / (olp + 1), e -> false);
		}
		// 更新发送数据
		updateCheckAndSendData();

		if (tick % 100 == 0) refreshAmbitus();
	}

	public static void doOverloadExplosion(World world, BlockPos pos) {
		if (world.isRemote) return;
		world.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 20, true);
		EventServer.addWorldTickTask(world, new IWorldTickTask() {
			final List<Element> elements = Element.REGISTRY.getValues();
			int index = 0;
			int tick = 0;

			@Override
			public int onTick(World w) {
				if (!world.isBlockLoaded(pos)) return ITickTask.END;
				if ((this.tick++) % 5 != 0) return ITickTask.SUCCESS;
				Element element = elements.get(this.index++);
				if (element == ESObjects.ELEMENTS.WATER) return ITickTask.SUCCESS;
				if (element == ESObjects.ELEMENTS.FIRE) return ITickTask.SUCCESS;
				BlockPos at = pos.add(RandomHelper.rand.nextGaussian() * 4, RandomHelper.rand.nextGaussian() * 4,
						RandomHelper.rand.nextGaussian() * 4);
				ElementExplosion.doExplosion(world, at, new ElementStack(element, 5000, 5000), null);
				return this.index >= elements.size() ? ITickTask.END : ITickTask.SUCCESS;
			}
		});

		EntityItem entityitem = ItemHelper.dropItem(world, new Vec3d(pos).add(0.5, 0.5, 0.5),
				new ItemStack(ESObjects.ITEMS.COLLAPSE));
		entityitem.motionX = entityitem.motionY = entityitem.motionZ = 0;
		entityitem.velocityChanged = true;
		entityitem.setNoDespawn();
	}

	public void ergodicMaigcPlatform(Function<BlockPos, Boolean> callback) {
		for (int x = -2; x <= 2; x++) {
			for (int z = -2; z <= 2; z++) {
				if (Math.abs(x) != 2 && z != -2) z = 2;
				callback.apply(pos.add(x, -3, z));
			}
		}
	}

	/** 刷新周边参数 */
	protected void refreshAmbitus() {

		toElement = null;
		overloadProtect = 0;

		LambdaReference<Integer> ppRef = LambdaReference.of(0);

		ergodicMaigcPlatform(at -> {
			IGetItemStack itemGetter = BlockHelper.getTileEntity(world, at, IGetItemStack.class);
			if (itemGetter == null) return false;
			ItemStack stack = itemGetter.getStack();
			if (stack.isEmpty()) return false;

			if (stack.getItem() == ESObjects.ITEMS.BLESSING_JADE) ppRef.set(ppRef.get() + 1);
			if (stack.getItem() == ESObjects.ITEMS.CALAMITY_GEM) ppRef.set(ppRef.get() - 1);

			NBTTagCompound nbt = stack.getTagCompound();
			if (nbt == null) return true;

			IItemStructure itemStructure = ItemStructure.getItemStructure(stack);
			if (!itemStructure.isEmpty()) {
				if (toElement == null) toElement = new ElementMap();
				toElement.add(itemStructure);
			}

			if (nbt.hasKey("_DSOP", NBTTag.TAG_NUMBER)) ppRef.set(ppRef.get() + nbt.getInteger("_DSOP"));

			return true;
		});

		float protectPotin = Math.abs(ppRef.get());
		overloadProtect = (float) (Math.pow(0.001, 1 / protectPotin) * (-1 / (protectPotin + 1) + 1.4));
		if (ppRef.get() < 0) overloadProtect = -overloadProtect;

		if (toElement != null) toElement.add(ElementMap.instance);
	}

	/** 减少过载数据记录 */
	protected void reduceOverloadMap(float rate, Function<Element, Boolean> passer) {
		Iterator<Entry<Element, Float>> iter = overloadMap.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<Element, Float> entry = iter.next();
			if (passer.apply(entry.getKey())) continue;
			entry.setValue(entry.getValue() * rate);
			if (entry.getValue() < 0.01f) iter.remove();
		}
	}

	/**
	 * 将分解的元素存入周围的元素容器
	 * 
	 * @return 存入的位置index
	 */
	protected int doSendElement(ElementStack estack) {
		return putElementToSpPlace(estack, this.pos);
	}

	/** 处理过载 */
	protected Element doIncrOverload(ElementStack estack) {

		if (estack.isMagic()) return estack.getElement();

		float drop = 1 - overloadProtect;
		final float dropDivide = 0.25f;
		if (drop < dropDivide) drop = dropDivide / (dropDivide - drop + 1);

		Element element = estack.getElement();
		Float f = overloadMap.get(element);
		float overRatio = f == null ? 0 : f.floatValue();
		float overIncr = estack.getCount() / 10f * (1 + MathHelper.log2(Math.max(estack.getPower(), 1)) / 7);
		overRatio = overRatio + overIncr * drop;

		float overloadInc = overRatio / 92f;
		if (overload > 1) overloadInc = overloadInc / (float) Math.pow(overload, 4);
		overloadInc = overloadInc * drop;
		overload = Math.min(overload + overloadInc, 2);

		if (overloadInc > OVERLOAD_BELOW_ONE_DECLINE * 2) overRatio = overRatio * Math.min(0.875f + 0.125f * drop, 1);
		overloadMap.put(element, overRatio);

		return element;
	}

	/** 每tick调用更新 */
	protected void updateCheckAndSendData() {
		if (tickSendDataCD > 0) {
			tickSendDataCD--;
			return;
		}

		if (tickSendMap.isEmpty() && prevOverload == overload) return;
		tickSendDataCD = SEND_DATA_CD_INTERVAL;
		updateDeconstructDataToClient();
	}

	/** 处理数据，并更新到客户端 */
	protected void updateDeconstructDataToClient() {
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

		if (prevOverload != overload) {
			nbt.setFloat("DOL", overload);
			prevOverload = overload;
		}

		if (nbt.isEmpty()) return;

		updateToClient(nbt);
	}

	protected LinkedList<Entry<Integer, Integer>> effectList = new LinkedList();
	public float roate, prevRoate;
	public float animeRate = 0; // 动画比例，转的快还是慢
	public float wakeRate = 0, prevWakeRate = 0; // 是否起来可以旋转
	/** 客户端overload变更预期值，渲染平滑过度 */
	public float targetOverload = 0;

	@Override
	@SideOnly(Side.CLIENT)
	public void handleUpdateTag(NBTTagCompound tag) {
		if (!tag.hasKey("DDS") && !tag.hasKey("DOL")) {
			super.handleUpdateTag(tag);
			return;
		}
		if (tag.hasKey("DOL")) targetOverload = tag.getFloat("DOL");
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

	@SideOnly(Side.CLIENT)
	public float getOverloadRate() {
		return overload;
	}

	@SideOnly(Side.CLIENT)
	private void updateClient() {
		prevOverload = overload;
		prevRoate = roate;
		prevWakeRate = wakeRate;

		if (isIntact()) {
			wakeRate = Math.min(1, wakeRate + (1 - wakeRate) * 0.1f);
			roate += 0.01f * (1 - animeRate) + 0.2f * animeRate;
		} else {
			float r = wakeRate - 0.01f;
			wakeRate = Math.max(0, r * r);
		}

		if (targetOverload >= 2) overload = targetOverload;
		else overload = overload + (targetOverload - overload) * 0.075f;

		genActiveEffect();
		if (overload > 1) genOverloadEffect();
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
	public void genOverloadEffect() {
		int n = MathHelper.ceil((MathHelper.clamp(overload, 1, 2) - 1) * 4);
		final int[] OVERLOAD_COLORS = new int[] { 0x992500, 0xc22f00, 0xca4619, 0xdc663e };
		for (int i = 0; i < n; i++) {
			Vec3d pos = new Vec3d(this.pos).add(0.5, 0.5, 0.5);
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
		if (!isIntact()) return;
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
				.add(Effect.rand.nextGaussian() * range + 0.5, 0.5, Effect.rand.nextGaussian() * range + 0.5)
				.add(targetVec);
		EffectElementMove effect = new EffectElementMove(world, pos);
		final int[] COLORS = new int[] { 0x1b6eb8, 0x196ab5, 0x458ed1, 0x86c0f6 };
		final int[] OVERLOAD_COLORS = new int[] { 0x992500, 0xc22f00, 0xca4619, 0xdc663e };
		int color = 0;
		if (overload > Effect.rand.nextFloat()) color = OVERLOAD_COLORS[Effect.rand.nextInt(OVERLOAD_COLORS.length)];
		else color = COLORS[Effect.rand.nextInt(COLORS.length)];
		effect.setColor(color);
		Vec3d speed = new Vec3d(0, Effect.rand.nextDouble(), 0);
		effect.setVelocity(speed.normalize().scale(0.01));
		effect.xDecay = effect.zDecay = effect.yDecay = 0.8;
		Effect.addEffect(effect);

	}

	@SideOnly(Side.CLIENT)
	public void genNowherePlaceEffect(ElementStack elementStack) {
		Vec3d pos = new Vec3d(this.pos).add(0.5, 0.5, 0.5);
		EffectElementMove effect = new EffectElementMove(world, pos);
		effect.setColor(elementStack.getColor());
		Vec3d speed = new Vec3d(Effect.rand.nextGaussian(), 0, Effect.rand.nextGaussian());
		effect.setVelocity(speed.normalize().add(0, Effect.rand.nextDouble() * 0.5 + 0.5, 0).scale(0.3));
		effect.xDecay = effect.zDecay = effect.yDecay = 0.6;
		Effect.addEffect(effect);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void genParticleElementTo(boolean isGet, IAltarWake altarWake, ElementStack estack, BlockPos from,
			BlockPos to) {
		Vec3d refPos;
		if (isGet) refPos = new Vec3d(to).add(0.5, 0.5, 0.5);
		else refPos = new Vec3d(from).add(0.5, 0.5, 0.5);
		if (altarWake == null) {
			if (isGet) TileElementalCube.giveParticleElementTo(world, estack.getColor(),
					new Vec3d(from).add(0.5, 0.5, 0.5), refPos, 1);
			else TileElementalCube.giveParticleElementTo(world, estack.getColor(), refPos,
					new Vec3d(to).add(0.5, 0.5, 0.5), 1);
		} else altarWake.updateEffect(world, isGet ? IAltarWake.SEND : IAltarWake.OBTAIN, estack, refPos);
	}

}

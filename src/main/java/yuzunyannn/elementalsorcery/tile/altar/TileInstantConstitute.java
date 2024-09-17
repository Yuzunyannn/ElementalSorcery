package yuzunyannn.elementalsorcery.tile.altar;

import java.util.AbstractMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.crafting.IItemStructure;
import yuzunyannn.elementalsorcery.api.crafting.IToElementInfo;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.tile.IAltarWake;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.api.tile.IMagicBeamHandler;
import yuzunyannn.elementalsorcery.api.util.NBTTag;
import yuzunyannn.elementalsorcery.api.util.render.RenderFriend;
import yuzunyannn.elementalsorcery.building.Buildings;
import yuzunyannn.elementalsorcery.building.MultiBlock;
import yuzunyannn.elementalsorcery.crafting.element.ItemStructure;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.batch.EffectElementMove;
import yuzunyannn.elementalsorcery.render.effect.grimoire.EffectInstantConstituteCharge;
import yuzunyannn.elementalsorcery.render.effect.scrappy.FirewrokShap;
import yuzunyannn.elementalsorcery.tile.TileEStoneCrock;
import yuzunyannn.elementalsorcery.tile.ir.TileIceRockCrystalBlock;
import yuzunyannn.elementalsorcery.tile.ir.TileIceRockStand;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;
import yuzunyannn.elementalsorcery.util.helper.Color;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

public class TileInstantConstitute extends TileStaticMultiBlock implements ITickable {

	public static final double ONE_ORDER_CRYSTAL_NEED_FRAGMENT = 1024;

	public static int getOrderValUsed(IToElementInfo info) {
		int complex = info.complex();
		return 1 + MathHelper.floor(complex + Math.pow(1.4, complex));
	}

	/** order crystal 的最高数量 */
	protected int orderValMax = 64;
	/** order crystal 的数量 */
	protected int orderVal;
	/** 之前的数量，客户端用于动画，服务端用于判定是否要发送更新 */
	public int prevOrderVal;
	/** 发送动画用的记录数组 */
	public int[] sendList = new int[4];

	@Override
	public void initMultiBlock() {
		structure = new MultiBlock(Buildings.INSTANT_CONSTITUTE, this, new BlockPos(0, -2, 0));
		structure.addSpecialBlock(new BlockPos(2, 2, 3));
		structure.addSpecialBlock(new BlockPos(-2, 2, 3));
		structure.addSpecialBlock(new BlockPos(-2, 2, -3));
		structure.addSpecialBlock(new BlockPos(2, 2, -3));
		structure.addSpecialBlock(new BlockPos(3, 2, 2));
		structure.addSpecialBlock(new BlockPos(-3, 2, 2));
		structure.addSpecialBlock(new BlockPos(-3, 2, -2));
		structure.addSpecialBlock(new BlockPos(3, 2, -2));
	}

	public int getOrderVal() {
		return orderVal;
	}

	public int getMaxOrderVal() {
		return orderValMax;
	}

	public void setOrderVal(int orderVal) {
		this.orderVal = orderVal;
	}

	public void setMaxOrderVal(int orderValMax) {
		this.orderValMax = Math.max(64, orderValMax);
	}

	public void addOrderVal(int val) {
		setOrderVal(getOrderVal() + val);
	}

	public void growOrderVal(int val) {
		addOrderVal(val);
		if (getOrderVal() > getMaxOrderVal()) {
			setOrderVal(getMaxOrderVal());
			if (getMaxOrderVal() < Integer.MAX_VALUE / 2) setMaxOrderVal(getMaxOrderVal() * 2);
		}
		this.markDirty();
	}

	protected void writeMyNBT(NBTTagCompound compound) {
		compound.setInteger("oVal", getOrderVal());
		compound.setInteger("ovMax", getMaxOrderVal());
		if (isSending()) {
			boolean needSend = false;
			for (int n : sendList) {
				if (n != 0) {
					needSend = true;
					break;
				}
			}
			if (needSend) {
				compound.setIntArray("~sList", sendList);
				sendList = new int[sendList.length];
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		writeMyNBT(compound);
		return super.writeToNBT(compound);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		setOrderVal(compound.getInteger("oVal"));
		setMaxOrderVal(compound.getInteger("ovMax"));
		super.readFromNBT(compound);
		if (world != null && !world.isRemote) prevOrderVal = getOrderVal();
	}

	public void tryUpdateOrderValToClient() {
		int orderVal = getOrderVal();
		if (prevOrderVal == 0 && orderVal > 0) {
			prevOrderVal = orderVal;
			updateToClient();
		} else if (orderVal == 0 && prevOrderVal > 0) {
			prevOrderVal = orderVal;
			updateToClient();
		} else {
			float d = Math.abs(orderVal - prevOrderVal) / (float) getMaxOrderVal();
			if (d >= 0.01f) {
				prevOrderVal = orderVal;
				updateToClient();
			}
		}
	}

	public boolean doConstitute(ItemStack itemStack) {
		if (world.isRemote) return true;
		if (!checkIntact(structure)) return false;

		IItemStructure itemStructure = ItemStructure.getItemStructure(itemStack);
		if (itemStructure.isEmpty()) return false;

		ItemStack targetStack = itemStructure.getStructureItem(0);
		IToElementInfo info = itemStructure.toElement(targetStack);
		if (info == null) return false;

		int costVal = getOrderValUsed(info);
		int currVal = this.getOrderVal();
		if (currVal < costVal) return false;

		ElementStack[] eStacks = info.element();
		List<ElementStack> needElementStacks = new LinkedList<>();
		for (ElementStack eStack : eStacks) needElementStacks.add(eStack.copy());
		if (needElementStacks.isEmpty()) return false;

		List<Map.Entry<Integer, List<ElementStack>>> cubes = new LinkedList<>();
		int size = structure.getSpecialBlockCount();
		int startIndex = getStartIndex();
		for (int i = 0; i < size; i++) {
			int index = (startIndex + i) % size;
			if (needElementStacks.isEmpty()) break;
			TileEntity tile = structure.getSpecialTileEntity(index);
			IAltarWake altarWake = getAlterWake(tile);
			if (altarWake == null) continue;
			IElementInventory einv = ElementHelper.getElementInventory(tile);
			if (einv == null) continue;
			LinkedList<ElementStack> costList = new LinkedList();
			Iterator<ElementStack> iter = needElementStacks.iterator();
			while (iter.hasNext()) {
				ElementStack eStack = iter.next();
				ElementStack getStack = einv.extractElement(eStack, true);
				if (getStack.isEmpty()) continue;
				eStack.shrink(getStack.getCount());
				costList.add(getStack);
				if (eStack.isEmpty()) iter.remove();
			}
			if (costList.isEmpty()) continue;
			Map.Entry<Integer, List<ElementStack>> entry = new AbstractMap.SimpleEntry(index, costList);
			cubes.add(entry);
		}
		if (cubes.isEmpty()) return false;
		if (!needElementStacks.isEmpty()) return false;

		setOrderVal(currVal - costVal);
		NBTTagList cDatList = new NBTTagList();
		for (Map.Entry<Integer, List<ElementStack>> entry : cubes) {
			TileEntity tile = structure.getSpecialTileEntity(entry.getKey());
			IAltarWake altarWake = getAlterWake(tile);
			IElementInventory eInv = ElementHelper.getElementInventory(tile);
			List<ElementStack> costList = entry.getValue();
			int[] colors = new int[costList.size()];
			int colorIndex = 0;
			for (ElementStack eStack : costList) {
				eInv.extractElement(eStack, false);
				colors[colorIndex++] = eStack.getColor();
			}
			altarWake.wake(IAltarWake.SEND, pos);
			eInv.markDirty();
//			altarWake.onInventoryStatusChange();

			NBTTagCompound cDat = new NBTTagCompound();
			cDat.setByte("i", entry.getKey().byteValue());
			cDat.setIntArray("c", colors);
			cDatList.appendTag(cDat);
		}

		Vec3d app = new Vec3d(pos).add(0.5, 1, 0.5);
		ItemHelper.dropItem(world, app, targetStack.copy());

		NBTTagCompound clientDat = new NBTTagCompound();
		clientDat.setTag("~!~", cDatList);
		writeMyNBT(clientDat);
		updateToClient(clientDat);

		this.markDirty();
		return true;
	}

	@Override
	public void handleUpdateTag(NBTTagCompound tag) {
		if (tag.hasKey("~!~", NBTTag.TAG_LIST)) doConstituteClient(tag.getTagList("~!~", NBTTag.TAG_COMPOUND));
		if (tag.hasKey("~sList")) {
			int[] sendList = tag.getIntArray("~sList");
			for (int i = 0; i < Math.min(sendList.length, this.sendList.length); i++) this.sendList[i] += sendList[i];
		}
		super.handleUpdateTag(tag);
	}

	public int tick;

	@Override
	public void update() {
		tick++;
		if (world.isRemote) updateClient();
		else if (tick % 10 == 0) tryUpdateOrderValToClient();
		if (tick % 60 == 0) tryUpdateCrockCraft();
	}

	protected class CrockCraft implements TileEStoneCrock.ICrockCraft {

		protected BlockPos connectPos;
		protected final int index;

		public CrockCraft(int index, BlockPos connectPos) {
			this.index = index;
			this.connectPos = connectPos;
		}

		@Override
		public boolean isAlive() {
			return TileInstantConstitute.this.isAlive();
		}

		@Override
		public void tryConnect(World world, BlockPos pos, IMagicBeamHandler hanlder) {
			TileIceRockCrystalBlock tile = BlockHelper.getTileEntity(world, connectPos, TileIceRockCrystalBlock.class);
			if (tile == null) return;
			TileIceRockStand core = tile.getIceRockCore();
			if (core == null) return;
			EnumFacing facing = EnumFacing.byHorizontalIndex(index).rotateY();
			tile.setBeamHandler(facing.getOpposite(), hanlder);
		}

		@Override
		public double getCraftCost(World world, BlockPos pos, ItemStack stack) {
			if (stack.getItem() == ESObjects.ITEMS.ORDER_CRYSTAL) return ONE_ORDER_CRYSTAL_NEED_FRAGMENT;
			return -1;
		}

		@Override
		public ItemStack doCraft(World world, BlockPos pos, ItemStack stack) {
			stack.shrink(1);
			TileInstantConstitute.this.growOrderVal(1);
			sendList[index % sendList.length]++;
			if (stack.isEmpty()) {
				TileInstantConstitute.this.prevOrderVal = TileInstantConstitute.this.getOrderVal();
				TileInstantConstitute.this.updateToClient();
			}
			return stack;
		}

	}

	public void tryUpdateCrockCraft() {
		for (EnumFacing facing : EnumFacing.HORIZONTALS) {
			BlockPos at = this.pos.offset(facing, 6);
			TileEStoneCrock crock = BlockHelper.getTileEntity(world, at, TileEStoneCrock.class);
			if (crock == null) continue;
			if (crock.getCraft() != null) continue;
			crock.setCraft(new CrockCraft(facing.getHorizontalIndex(), at.offset(facing.rotateY(), 6)));
		}
	}

	@SideOnly(Side.CLIENT)
	public void doConstituteClient(NBTTagList list) {
		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound dat = list.getCompoundTagAt(i);
			int index = dat.getInteger("i");
			TileEntity tile = structure.getSpecialTileEntity(index);
			if (tile == null) continue;
			IAltarWake altarWake = getAlterWake(tile);
			if (altarWake == null) continue;
			altarWake.wake(IAltarWake.SEND, pos);
			playConstituteEffect(tile.getPos(), dat.getIntArray("c"));
		}
		Vec3d dstVec = new Vec3d(this.pos).add(0.5, 1, 0.5);
		FirewrokShap.createECircle(world, dstVec, 0.1, 1,
				new int[] { 0xe2e2ef, 0xcdcde4, 0x99ecff, 0x5c9ad8, 0x25346e });
	}

	@SideOnly(Side.CLIENT)
	public void clientSendEffect(EnumFacing facing) {
		int[] colors = new int[] { 0x385ab5, 0x293e80, 0x8ae3ff, 0xc9faff };
		BlockPos at = this.pos.offset(facing, 6);
		EffectInstantConstituteCharge effect = new EffectInstantConstituteCharge(world, at, facing.getOpposite());
		effect.color.setColor(colors[rand.nextInt(colors.length)]);
		Effect.addEffect(effect);
	}

	@SideOnly(Side.CLIENT)
	public void playConstituteEffect(BlockPos pos, int[] colors) {
		if (colors.length == 0) return;

		Vec3d srcVec = new Vec3d(pos).add(0.5, 0.5, 0.5);
		Vec3d dstVec = new Vec3d(this.pos).add(0.5, 1.2, 0.5);
		Vec3d tarVec = dstVec.subtract(srcVec).scale(1 / 8f);
		for (int i = 0; i < 8; i++) {
			Vec3d speed = new Vec3d(Effect.rand.nextGaussian(), Effect.rand.nextGaussian(), Effect.rand.nextGaussian());
			EffectElementMove effect = new EffectElementMove(world, srcVec.add(tarVec.scale(i)));
			effect.setColor(getRandomColor(colors));
			effect.setVelocity(speed.scale(0.025));
			effect.xDecay = effect.yDecay = effect.zDecay = 0.75;
			Effect.addEffect(effect);
		}
		for (int i = 0; i < 8; i++) {
			Vec3d speed = new Vec3d(Effect.rand.nextGaussian(), Effect.rand.nextGaussian(), Effect.rand.nextGaussian());
			EffectElementMove effect = new EffectElementMove(world, dstVec.add(speed.scale(-0.5)));
			effect.lifeTime = 10;
			effect.dalpha = 1f / effect.lifeTime;
			effect.setColor(getRandomColor(colors));
			effect.setVelocity(speed.scale(0.2));
			effect.xDecay = effect.yDecay = effect.zDecay = 0.5;
			Effect.addEffect(effect);
		}
		for (int i = 0; i < 8; i++) {
			Vec3d speed = new Vec3d(Effect.rand.nextGaussian(), Effect.rand.nextGaussian(), Effect.rand.nextGaussian());
			EffectElementMove effect = new EffectElementMove(world, dstVec);
			effect.setColor(getRandomColor(colors));
			effect.setVelocity(speed.scale(0.025));
			Effect.addEffect(effect);
		}
	}

	@SideOnly(Side.CLIENT)
	public Color getRandomColor(int[] colors) {
		return new Color(colors[Effect.rand.nextInt(colors.length)]).add((float) (Effect.rand.nextFloat() * 0.125f));
	}

	@SideOnly(Side.CLIENT)
	public int targetOrderVal;

	@SideOnly(Side.CLIENT)
	public void updateClient() {
		prevOrderVal = targetOrderVal;
		targetOrderVal = getOrderVal();
		for (int i = 0; i < sendList.length; i++) {
			if (sendList[i] > 0) {
				sendList[i]--;
				clientSendEffect(EnumFacing.byHorizontalIndex(i));
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public float getOderValRationForShow(float partialTicks) {
		if (targetOrderVal == 0 && prevOrderVal == 0) return 0;
		float r = RenderFriend.getPartialTicks(targetOrderVal, prevOrderVal, partialTicks) / (float) getMaxOrderVal();
		if (r < 0.1) r = MathHelper.sqrt(r) / 0.316228f * 0.1f;
		return Math.min(r, 1);
	}
}

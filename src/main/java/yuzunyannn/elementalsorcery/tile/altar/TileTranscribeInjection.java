package yuzunyannn.elementalsorcery.tile.altar;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import yuzunyannn.elementalsorcery.building.Buildings;
import yuzunyannn.elementalsorcery.building.MultiBlock;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.grimoire.mantra.Mantra;
import yuzunyannn.elementalsorcery.init.ESInitInstance;
import yuzunyannn.elementalsorcery.item.ItemAncientPaper;
import yuzunyannn.elementalsorcery.item.ItemGrimoire;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.EffectElementMove;
import yuzunyannn.elementalsorcery.util.MultiRets;
import yuzunyannn.elementalsorcery.util.block.BlockHelper;

public class TileTranscribeInjection extends TileStaticMultiBlock implements ITickable {

	/** 仓库 */
	protected ItemStackHandler inventory = new ItemStackHandler(6) {
		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
			if (ItemAncientPaper.hasMantraData(stack)) return super.insertItem(slot, stack, simulate);
			return stack;
		}
	};

	@Override
	public void initMultiBlock() {
		structure = new MultiBlock(Buildings.TRANSCRIBE_ALTAR, this, new BlockPos(0, -6, 0));
		structure.addSpecialBlock(new BlockPos(8, 1, 4));
		structure.addSpecialBlock(new BlockPos(8, 1, -4));
		structure.addSpecialBlock(new BlockPos(-8, 1, -4));
		structure.addSpecialBlock(new BlockPos(-8, 1, 4));
		structure.addSpecialBlock(new BlockPos(4, 1, 8));
		structure.addSpecialBlock(new BlockPos(4, 1, -8));
		structure.addSpecialBlock(new BlockPos(-4, 1, -8));
		structure.addSpecialBlock(new BlockPos(-4, 1, 8));
	}

	protected Mantra mantraHandle;
	protected int transcribeProgress;
	protected boolean finish;

	@Override
	public void update() {
		if (mantraHandle == null) return;
		if (!isIntact()) {
			transcribeProgress = 0;
			return;
		}
		if (checkTime % 10 == 0) this.doCheck();
		updateTranscribe();
	}

	public void wake() {
		finish = false;
		this.doCheck();
		this.checkIntact(structure);
	}

	protected void doCheck() {
		// 进行写入判断
		Mantra originHave = mantraHandle;
		this.check();
		// 状态变化，更新状态
		if (originHave != mantraHandle) {
			transcribeProgress = 0;
			this.markDirty();
			this.updateToClient();
		}
	}

	protected void check() {
		mantraHandle = null;
		Mantra mantraType = null;
		int progress = 0;
		for (int i = 0; i < inventory.getSlots(); i++) {
			ItemStack paper = inventory.getStackInSlot(i);
			if (paper.isEmpty()) continue;
			MultiRets rets = ItemAncientPaper.getMantraData(paper);
			if (rets.isEmpty()) return;
			Mantra m = rets.get(0, Mantra.class);
			if (mantraType == null) mantraType = m;
			else if (mantraType != m) return;
			// 有四个返回值的时普通的咒文
			if (!Mantra.isCustomMantra(m)) {
				Integer start = rets.getNumber(2, Integer.class);
				Integer end = rets.getNumber(3, Integer.class);
				if (start == null || end == null) return;
				// 超出当前进度直接结束
				if (start > progress) return;
				progress = MathHelper.clamp(end, progress, 100);
				continue;
			}
			// 自定义魔法允许拼凑，秩序同是自定义的就可以
		}
		if (mantraType == null) return;
		if (!Mantra.isCustomMantra(mantraType) && progress < 100) return;
		// 是否可以写入的检查
		TileTranscribeTable table = BlockHelper.getTileEntity(world, pos.down(4), TileTranscribeTable.class);
		if (table == null || finish) return;
		ItemStack grimoire = table.getStack();
		if (grimoire.isEmpty()) return;
		ItemGrimoire.MantrasData data = ItemGrimoire.getAllMantra(grimoire);
		if (data == null) return;
		int restCapacity = data.getCapacityTotally() - data.getCapacity();
		int capacity = this.getMantraCapacity(mantraType);
		if (capacity > restCapacity) return;
		// 设置句柄，表示可以开始计时了
		mantraHandle = mantraType;
	}

	/** 更新一次写入，提升进度，抽取元素 */
	protected void updateTranscribe() {
		if (mantraHandle == null) return;
		ElementStack need = ElementStack.EMPTY;
		switch (transcribeProgress % 4) {
		case 1:
			need = new ElementStack(ESInitInstance.ELEMENTS.FIRE, 1, 100);
			break;
		case 2:
			need = new ElementStack(ESInitInstance.ELEMENTS.WATER, 1, 100);
			break;
		default:
			need = new ElementStack(ESInitInstance.ELEMENTS.KNOWLEDGE, 1, 100);
			break;
		}
		transcribeProgress--;
		ElementStack get = this.getElementFromSpPlace(need, pos.up(2));
		if (get.isEmpty()) return;
		if (world.isRemote) this.updateTranscribeEffect();
		transcribeProgress += 2;
		if (transcribeProgress > 100) {
			this.doCheck();
			if (mantraHandle == null) return;
			transcribeProgress = 0;
			this.transcribe();
		}
	}

	protected int getMantraCapacity(Mantra mantra) {
		if (Mantra.isCustomMantra(mantra)) return 1;
		return 2;
	}

	@Nullable
	protected NBTTagCompound getMantraData(Mantra mantra) {
		if (Mantra.isCustomMantra(mantra)) return null;
		return null;
	}

	/** 真正的写入函数 */
	protected void transcribe() {
		TileTranscribeTable table = BlockHelper.getTileEntity(world, pos.down(4), TileTranscribeTable.class);
		if (table == null) return;
		ItemStack grime = table.getStack();
		if (grime.isEmpty()) return;
		ItemGrimoire.MantrasData data = ItemGrimoire.getAllMantra(grime);
		if (data == null) return;
		if (world.isRemote) return;
		int capacity = this.getMantraCapacity(mantraHandle);
		data.growCapacity(capacity);
		data.add(mantraHandle, this.getMantraData(mantraHandle));
		ItemGrimoire.setAllMantra(grime, data);
		finish = true;
	}

	@SideOnly(Side.CLIENT)
	protected void updateTranscribeEffect() {
		if (checkTime % 5 != 0) return;
		// 简单的粒子效果，后面有了文字在改
		Vec3d pos = new Vec3d(this.pos).addVector(rand.nextDouble(), rand.nextDouble(), rand.nextDouble());
		EffectElementMove effect = new EffectElementMove(world, pos);
		effect.setColor(mantraHandle.getRenderColor());
		Effect.addEffect(effect);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.equals(capability)) return true;
		return super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.equals(capability)) return (T) inventory;
		return super.getCapability(capability, facing);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		if (this.isSending()) {
			if (nbt.hasKey("mId")) mantraHandle = Mantra.REGISTRY.getValue(nbt.getInteger("mId"));
			else mantraHandle = null;
			return;
		}
		// 还原状态
		if (nbt.hasKey("mantra", 8)) {
			mantraHandle = Mantra.REGISTRY.getValue(new ResourceLocation(nbt.getString("mantra")));
			transcribeProgress = nbt.getInteger("progress");
		}
		finish = nbt.getBoolean("fin");
		this.inventory.deserializeNBT(nbt.getCompoundTag("inv"));
		super.readFromNBT(nbt);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		if (this.isSending()) {
			if (mantraHandle != null) nbt.setInteger("mId", Mantra.REGISTRY.getId(mantraHandle));
			return nbt;
		}
		// 记录状态
		if (mantraHandle != null) {
			nbt.setString("mantra", mantraHandle.getRegistryName().toString());
			nbt.setInteger("progress", transcribeProgress);
		}
		nbt.setBoolean("fin", finish);
		nbt.setTag("inv", this.inventory.serializeNBT());
		return super.writeToNBT(nbt);
	}

}

package yuzunyannn.elementalsorcery.tile.altar;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
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
import yuzunyannn.elementalsorcery.api.tile.IAltarWake;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.building.Buildings;
import yuzunyannn.elementalsorcery.building.MultiBlock;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.elf.research.AncientPaper;
import yuzunyannn.elementalsorcery.grimoire.Grimoire;
import yuzunyannn.elementalsorcery.grimoire.mantra.Mantra;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.element.EffectElementMove;
import yuzunyannn.elementalsorcery.render.effect.particle.ParticleTranscribe;
import yuzunyannn.elementalsorcery.util.block.BlockHelper;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;

public class TileTranscribeInjection extends TileStaticMultiBlock implements ITickable {

	/** 仓库 */
	protected ItemStackHandler inventory = new ItemStackHandler(6) {
		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
			if (!AncientPaper.hasMantra(stack)) return stack;
			AncientPaper ap = new AncientPaper(stack);
			if (ap.isLocked()) return stack;
			return super.insertItem(slot, stack, simulate);
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

	/** 撰录类型：1撰录、2充能 */
	protected byte workType;
	/** 正在撰录的句柄，1模式下才有 */
	protected Mantra mantraHandle;
	/** 撰录的进度条 */
	protected int transcribeProgress;
	/** 本次完成标记 */
	protected boolean finish;
	/** 上次的状态记录 */
	private byte lastWorkType;
	private Mantra lastMantraHandle;

	public boolean hasStateChange() {
		return lastWorkType != workType || lastMantraHandle != mantraHandle;
	}

	public void syncState() {
		lastWorkType = workType;
		lastMantraHandle = mantraHandle;
	}

	public void clearState() {
		workType = 0;
		mantraHandle = null;
	}

	public boolean inWork() {
		return !finish && (workType == 1 || workType == 2);
	}

	@Override
	public void update() {
		if (world.isRemote) this.updateTranscribeAltarClient();
		if (!inWork()) return;
		switch (workType) {
		case 1:
			if (mantraHandle == null) return;
			if (updateCheck()) return;
			updateTranscribe();
			break;
		case 2:
			if (updateCheck()) return;
			this.updateCharge();
			break;
		default:
			finish = true;
			break;
		}
	}

	public float animeRate = 0;
	public float prevAnimeRate = 0;

	@SideOnly(Side.CLIENT)
	public void updateTranscribeAltarClient() {
		this.prevAnimeRate = this.animeRate;
		if (inWork() && this.ok) this.animeRate = Math.min(1, this.animeRate + 0.01f);
		else this.animeRate = Math.max(0, this.animeRate - 0.01f);
	}

	private boolean updateCheck() {
		if (!isIntact()) {
			transcribeProgress = 0;
			return true;
		}
		if (!world.isRemote && checkTime % 10 == 0) this.doCheck();
		return false;
	}

	public void wake() {
		finish = false;
		transcribeProgress = 0;
		this.clearState();
		this.syncState();
		this.doCheck();
		this.checkIntact(structure);
	}

	protected void doCheck() {
		// 进行写入判断
		this.check();
		// 状态变化，更新状态
		if (this.hasStateChange()) {
			this.syncState();
			transcribeProgress = 0;
			this.markDirty();
			this.updateToClient();
		}
	}

	protected void check() {
		this.clearState();
		if (this.checkCanTranscribe()) return;
		if (this.checkCanCharge()) return;

	}

	public ItemStack getGrimoire() {
		TileTranscribeTable table = BlockHelper.getTileEntity(world, pos.down(4), TileTranscribeTable.class);
		if (table == null) return ItemStack.EMPTY;
		return table.getStack();

	}

	// -----------------------------------//
	// ============撰录部分==================//
	// -----------------------------------//

	protected boolean checkCanTranscribe() {
		Mantra mantraType = null;
		int progress = 0;
		for (int i = 0; i < inventory.getSlots(); i++) {
			ItemStack paper = inventory.getStackInSlot(i);
			if (paper.isEmpty()) continue;
			AncientPaper ap = new AncientPaper(paper);
			if (!ap.hasMantra()) return false;
			Mantra m = ap.getMantra();
			if (mantraType == null) mantraType = m;
			else if (mantraType != m) return false;
			// 有四个返回值的时普通的咒文
			if (!Mantra.isCustomMantra(m)) {
				Integer start = ap.getStart();
				Integer end = ap.getEnd();
				if (start == null || end == null) return false;
				// 超出当前进度直接结束
				if (start > progress) return false;
				progress = MathHelper.clamp(end, progress, 100);
				continue;
			}
			// 自定义魔法允许拼凑，秩序同是自定义的就可以
		}
		if (mantraType == null) return false;
		if (!Mantra.isCustomMantra(mantraType) && progress < 100) return false;
		// 是否可以写入的检查
		ItemStack grimoire = this.getGrimoire();
		if (grimoire.isEmpty()) return false;
		Grimoire data = grimoire.getCapability(Grimoire.GRIMOIRE_CAPABILITY, null);
		if (data == null) return false;
		data.loadState(grimoire);
		int restCapacity = data.getCapacityMax() - data.getCapacity();
		int capacity = this.getMantraCapacity(mantraType);
		if (capacity > restCapacity) return false;
		// 设置句柄，表示可以开始计时了
		mantraHandle = mantraType;
		workType = 1;
		return true;
	}

	/** 更新一次写入，提升进度，抽取元素 */
	protected void updateTranscribe() {
		if (mantraHandle == null) return;
		if (transcribeProgress < 200) {
			ElementStack need = ElementStack.EMPTY;
			switch (transcribeProgress % 4) {
			case 1:
				need = new ElementStack(ESInit.ELEMENTS.FIRE, 1, 100);
				break;
			case 2:
				need = new ElementStack(ESInit.ELEMENTS.WATER, 1, 100);
				break;
			default:
				need = new ElementStack(ESInit.ELEMENTS.KNOWLEDGE, 1, 100);
				break;
			}
			transcribeProgress--;
			ElementStack get = this.getElementFromSpPlace(need, pos.up(2));
			if (get.isEmpty()) return;
			transcribeProgress++;
		}
		transcribeProgress++;
		if (world.isRemote) this.updateTranscribeEffect();
		if (transcribeProgress > 200 + 1000) {
			this.doCheck();
			if (mantraHandle == null) return;
			transcribeProgress = 0;
			this.transcribe();
		}
	}

	/** 获取某个咒文的容量 */
	protected int getMantraCapacity(Mantra mantra) {
		if (Mantra.isCustomMantra(mantra)) return 1;
		return Math.max(mantra.getOccupation(), 0);
	}

	@Nullable
	protected NBTTagCompound getMantraData(Mantra mantra) {
		if (Mantra.isCustomMantra(mantra)) return null;
		return null;
	}

	/** 真正的写入函数 */
	protected void transcribe() {
		ItemStack grimoire = this.getGrimoire();
		if (grimoire.isEmpty()) return;
		Grimoire data = grimoire.getCapability(Grimoire.GRIMOIRE_CAPABILITY, null);
		if (data == null) return;
		finish = true;
		if (world.isRemote) return;
		data.loadState(grimoire);
		int capacity = this.getMantraCapacity(mantraHandle);
		data.growCapacity(capacity);
		data.add(mantraHandle, this.getMantraData(mantraHandle));
		data.saveState(grimoire);
	}

	@SideOnly(Side.CLIENT)
	protected void updateTranscribeEffect() {
		if (this.transcribeProgress < 200) return;
		if (checkTime % 5 != 0) return;
		Vec3d pos = new Vec3d(this.pos);
		double x = rand.nextDouble() * 0.2 + 0.4;
		double z = rand.nextDouble() * 0.2 + 0.4;
		double y = rand.nextDouble() * 0.1 + 0.45;
		ParticleTranscribe effect = new ParticleTranscribe(world, pos.addVector(x, y, z));
		int c = mantraHandle.getColor(null);
		float r = ((c >> 16) & 0xff) / 255f;
		float g = ((c >> 8) & 0xff) / 255f;
		float b = ((c >> 0) & 0xff) / 255f;
		float add = (rand.nextFloat() - 0.5f) * 0.5f;
		r = MathHelper.clamp(r + add, 0, 1);
		g = MathHelper.clamp(g + add, 0, 1);
		b = MathHelper.clamp(b + add, 0, 1);
		effect.setRBGColorF(r, g, b);
		Minecraft.getMinecraft().effectRenderer.addEffect(effect);
	}

	// -----------------------------------//
	// ============充能部分==================//
	// -----------------------------------//

	protected boolean checkCanCharge() {
		for (int i = 0; i < inventory.getSlots(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if (stack.isEmpty()) continue;
			return false;
		}
		ItemStack grimoire = this.getGrimoire();
		if (grimoire.isEmpty()) return false;
		Grimoire data = grimoire.getCapability(Grimoire.GRIMOIRE_CAPABILITY, null);
		if (data == null) return false;
		data.loadState(grimoire);
		IElementInventory einv = data.getInventory();
		if (!ElementHelper.canInsert(einv)) return false;
		workType = 2;
		return true;
	}

	/** 下一个寻找元素的地方，不用持久化 */
	public int nextFindPlace = 0;
	/** 下一个魔法书充能的位置，不用持久化 */
	public int nextChargePlace = 0;

	protected void updateCharge() {
		ItemStack grimoire = this.getGrimoire();
		if (grimoire.isEmpty()) return;
		Grimoire data = grimoire.getCapability(Grimoire.GRIMOIRE_CAPABILITY, null);
		if (data == null) return;
		data.loadState(grimoire);
		IElementInventory eInv = data.getInventory();
		if (eInv == null) {
			finish = true;
			return;
		}
		this.transcribeProgress++;
		if (this.transcribeProgress < 60) return;
		nextChargePlace = (nextChargePlace + 1) % eInv.getSlots();
		ElementStack smaple = eInv.getStackInSlot(nextChargePlace).copy();
		if (!smaple.isEmpty()) {
			smaple.setCount(10);
			smaple = this.getElementFromSpPlace(smaple, pos.up(2));
			if (smaple.isEmpty()) return;
			if (world.isRemote) updateChargeEffect(smaple);
			eInv.insertElement(smaple, false);
			eInv.saveState(grimoire);
		} else {
			// 没有元素，开始寻找
			int size = structure.getSpecialBlockCount();
			for (int i = 0; i < 4; i++) {
				nextFindPlace = (nextFindPlace + 1) % size;
				TileEntity tile = structure.getSpecialTileEntity(nextFindPlace);
				IAltarWake altarWake = getAlterWake(tile);
				if (altarWake == null) continue;
				IElementInventory cubInv = ElementHelper.getElementInventory(tile);
				if (cubInv == null) continue;
				for (int j = 0; j < cubInv.getSlots(); j++) {
					ElementStack find = cubInv.getStackInSlot(j);
					if (find.isEmpty()) continue;
					find = find.copy();
					find.setCount(10);
					smaple = cubInv.extractElement(find, false);
					if (world.isRemote) updateChargeEffect(smaple);
					eInv.insertElement(smaple, false);
					eInv.saveState(grimoire);
					return;
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	protected void updateChargeEffect(ElementStack estack) {
		if (transcribeProgress < 60) return;
		if (nextChargePlace % 2 != 0) return;
		double x = rand.nextDouble() * 0.5 + 0.25;
		double z = rand.nextDouble() * 0.5 + 0.25;
		double y = rand.nextDouble() * 0.1 + 0.45;
		Vec3d pos = new Vec3d(this.pos).addVector(x, y, z);
		EffectElementMove effect = new EffectElementMove(world, pos);
		effect.setColor(estack.getColor());
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
			workType = nbt.getByte("workType");
			if (nbt.hasKey("mId")) mantraHandle = Mantra.REGISTRY.getValue(nbt.getInteger("mId"));
			else mantraHandle = null;
			transcribeProgress = 0;
			finish = false;
			return;
		}
		// 还原状态
		workType = nbt.getByte("workType");
		finish = nbt.getBoolean("fin");
		if (inWork()) {
			if (nbt.hasKey("mantra", 8))
				mantraHandle = Mantra.REGISTRY.getValue(new ResourceLocation(nbt.getString("mantra")));
			transcribeProgress = nbt.getInteger("progress");
		}
		this.inventory.deserializeNBT(nbt.getCompoundTag("inv"));
		super.readFromNBT(nbt);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		if (this.isSending()) {
			nbt.setByte("workType", workType);
			if (mantraHandle != null) nbt.setInteger("mId", Mantra.REGISTRY.getId(mantraHandle));
			return nbt;
		}
		// 记录状态
		if (inWork()) {
			if (mantraHandle != null) nbt.setString("mantra", mantraHandle.getRegistryName().toString());
			nbt.setInteger("progress", transcribeProgress);
			nbt.setByte("workType", workType);
		}
		nbt.setBoolean("fin", finish);
		nbt.setTag("inv", this.inventory.serializeNBT());
		return super.writeToNBT(nbt);
	}

}

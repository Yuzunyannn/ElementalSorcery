package yuzunyannn.elementalsorcery.tile.altar;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.tile.IAltarWake;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.block.altar.BlockElementalCube;
import yuzunyannn.elementalsorcery.capability.ElementInventory;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.item.ItemSpellbook;
import yuzunyannn.elementalsorcery.render.effect.EffectElementFly;
import yuzunyannn.elementalsorcery.tile.TileEntityNetwork;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;
import yuzunyannn.elementalsorcery.util.obj.Vertex;

public class TileElementalCube extends TileEntityNetwork implements ITickable, IAltarWake {

	// 根据仓库和所需，获取一个元素
	public static ElementStack getAndTestElementTransBetweenInventory(ElementStack need, IElementInventory inv,
			IElementInventory inv_other) {
		if (need.isEmpty()) {
			// 如果书其他空闲空间，那就随便找出一个元素来
			for (int a = 0; a < inv_other.getSlots(); a++) {
				ElementStack other_estack = inv_other.getStackInSlot(a).copy();
				other_estack.setCount(1);
				if (other_estack.isEmpty()) continue;
				ElementStack extract = inv_other.extractElement(other_estack, true);
				if (extract.arePowerfulAndMoreThan(other_estack)) {
					if (inv.insertElement(extract, true)) { return extract.copy(); }
				}
			}
		} else {
			// 如果书其他空闲空间，查看是否拥有指定元素
			ElementStack extract = inv_other.extractElement(need, true);
			if (extract.arePowerfulAndMoreThan(need)) {
				if (inv.insertElement(extract, true)) { return extract.copy(); }
			}
		}
		return ElementStack.EMPTY;
	}

	// 获取一次元素动画
	@SideOnly(Side.CLIENT)
	public static void giveParticleElementTo(World world, int color, BlockPos from, BlockPos pto, float possibility) {
		Random rand = world.rand;
		if (rand.nextFloat() > possibility) return;
		EffectElementFly effect;
		if (pto.getY() > from.getY()) effect = new EffectElementFly(world,
				new Vec3d(from.getX() + Math.random(), from.getY() + Math.random(), from.getZ() + Math.random()),
				new Vec3d(pto.getX() + rand.nextDouble(), pto.getY() + 0.5, pto.getZ() + rand.nextDouble()));
		else effect = new EffectElementFly(world,
				new Vec3d(from.getX() + 0.25 + Math.random() * 0.5, from.getY() + 0.25 + Math.random() * 0.5,
						from.getZ() + 0.25 + Math.random() * 0.5),
				new Vec3d(pto.getX() + 0.5, pto.getY() + 1.0, pto.getZ() + 0.5));
		effect.setColor(color);
		yuzunyannn.elementalsorcery.render.effect.Effect.addEffect(effect);

	}

	// 保存句柄
	private static IStorage<IElementInventory> storage = ElementInventory.ELEMENTINVENTORY_CAPABILITY.getStorage();
	// 仓库
	private IElementInventory inventory = new ElementInventory();

	// 拥有能力
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (ElementInventory.ELEMENTINVENTORY_CAPABILITY == capability) { return true; }
		return super.hasCapability(capability, facing);
	}

	// 获取能力
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (ElementInventory.ELEMENTINVENTORY_CAPABILITY == capability) { return (T) inventory; }
		return super.getCapability(capability, facing);
	}

	// 加载
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		storage.readNBT(ElementInventory.ELEMENTINVENTORY_CAPABILITY, inventory, null,
				compound.getCompoundTag("inventory"));
		if (this.isSending()) {
			if (ElementHelper.isEmpty(inventory)) this.wake = 0;
		}
	}

	// 保存
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setTag("inventory", storage.writeNBT(ElementInventory.ELEMENTINVENTORY_CAPABILITY, inventory, null));
		return super.writeToNBT(compound);
	}

	// 唤醒
	@Override
	public boolean wake(int type) {
		if (!this.world.isRemote) return true;
		this.wake = 80;
		this.markDirty();
		return true;
	}

	@Override
	public void onEmpty() {
		if (!world.isRemote) this.updateToClient();
	}

	// 设置仓库
	public void setElementInventory(IElementInventory inventory) {
		this.inventory = inventory;
		if (world.isRemote) changeColor();
	}

	// 转移仓库
	public void toElementInventory(IElementInventory inventory) {
		inventory.setSlots(this.inventory.getSlots());
		for (int i = 0; i < this.inventory.getSlots(); i++) {
			inventory.setStackInSlot(i, this.inventory.getStackInSlot(i).copy());
		}
	}

	// 更新规则
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	@Override
	public void update() {
		if (world.isRemote) tick();
		else {

		}
	}

	// 默认颜色
	public static final Vertex ORIGIN_COLOR = new Vertex(29.0F / 255, 115.0F / 255, 109.0F / 255);
	// 默认旋转角度，每tick
	public static final float SOTATE_PRE_TICK = (float) (Math.PI / 4) / 20.0f;
	// 默认站立来的比率增加量
	public static final float WAKE_UP_RARE = 0.025f;
	// 站起来的状态剩余tick
	public int wake = 0;
	// 站立来的比率0-1
	public float wakeUp = 0.0f;
	public float rotate = (float) (Math.random() * Math.PI * 2);
	public Vertex color = ORIGIN_COLOR;
	public float colorRate = 0.0f;
	public float detlaCr = 0.01f;

	@SideOnly(Side.CLIENT)
	public void tick() {
		if (wake > 0) {
			if (color == ORIGIN_COLOR) changeColor();
			// 站起来的比率
			if (wakeUp >= 1.0f) wakeUp = 1.0f;
			else wakeUp += WAKE_UP_RARE;
			// 旋转
			rotate += SOTATE_PRE_TICK;
			if (rotate >= Math.PI * 2) rotate -= Math.PI * 2;
			// 颜色转变
			colorRate += detlaCr;
			if (colorRate >= 1.0f || colorRate <= 0.0f) {
				detlaCr = -detlaCr;
				if (colorRate <= 0.0f) changeColor();
			}
			wake--;
		} else {
			if (wakeUp <= 0.0f) wakeUp = 0.0f;
			else wakeUp -= WAKE_UP_RARE;
		}
		colorRate *= wakeUp;
	}

	// 切换颜色
	@SideOnly(Side.CLIENT)
	private void changeColor() {
		if (color == ORIGIN_COLOR) color = new Vertex(ORIGIN_COLOR);
		ElementStack estack = ItemSpellbook.giveMeRandomElement(inventory);
		int ecolor = color.toColor();
		if (!estack.isEmpty()) ecolor = estack.getElement().getColor(estack);
		color.toColor(ecolor);
	}

	@SideOnly(Side.CLIENT)
	public float getHigh(float rotate, float wakeUp) {
		float rate = (float) Math.cos(rotate);
		rate = rate * rate;
		float high = (float) ((rate + 1) * (0.70F - BlockElementalCube.BLOCK_HALF_SIZE)) + 0.3F;
		return high * wakeUp + (1 - wakeUp) * 0.25f;
	}

	@SideOnly(Side.CLIENT)
	public float getRoate(float rotate, float wakeUp) {
		return rotate * wakeUp;
	}

}

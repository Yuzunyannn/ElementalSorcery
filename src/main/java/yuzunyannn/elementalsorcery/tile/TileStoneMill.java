package yuzunyannn.elementalsorcery.tile;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.util.helper.NBTHelper;

public class TileStoneMill extends TileEntityNetwork implements ITickable {

	// 正在研磨的队列
	List<Milling> millList = new LinkedList<>();
	// 当前正在研磨的物品类型
	ItemStack millingItem = ItemStack.EMPTY;
	// 研磨粉末的量
	int dusty;
	// 研磨一次的剩余时间
	int restTick;
	// 锤子旋转角度
	public float rotate;
	public float prevRotate;
	static final float ROTATE_RATE = 0.4f;
	// 研磨的内容的槽
	protected IItemHandler inventory = new IItemHandler() {
		@Override
		@Nonnull
		public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
			if (Block.getBlockFromItem(stack.getItem()) != ESObjects.BLOCKS.STAR_STONE) return stack;
			if (slot < 0 || slot >= this.getSlots()) return stack;
			if (millList.size() >= this.getSlots()) return stack;
			// 不同的物品不能同时放入
			if (!millingItem.isEmpty()) {
				if (!ItemStack.areItemsEqual(millingItem, stack)) return stack;
			}
			// 获取添加到list的次数，规定list里，每一个位置只能有一个物品
			int addTimes = (this.getSlots() - millList.size());
			addTimes = addTimes <= stack.getCount() ? addTimes : stack.getCount();
			if (addTimes == 0) return stack;
			if (simulate) return ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - addTimes);
			for (int i = 0; i < addTimes; i++) {
				Milling m = new Milling();
				m.stack = new ItemStack(stack.getItem(), 1, stack.getItemDamage());
				millList.add(m);
				if (millingItem.isEmpty()) millingItem = m.stack.copy();
			}
			TileStoneMill.this.markDirty();
			TileStoneMill.this.updateToClient();
			return ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - addTimes);
		}

		@Override
		public int getSlots() {
			return 4;
		}

		@Override
		public ItemStack getStackInSlot(int slot) {
			return millList.size() <= slot ? ItemStack.EMPTY : millList.get(slot).stack;
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate) {
			return ItemStack.EMPTY;
		}

		@Override
		public int getSlotLimit(int slot) {
			return 1;
		}

	};

	/** 存放研磨状态，和一些客户端使用的渲染数据 */
	static public class Milling implements INBTSerializable<NBTTagCompound> {
		public ItemStack stack;
		public int degree;

		public Milling() {
			xoff = (float) Math.random() * 0.4f + 0.2f;
			zoff = (float) Math.random() * 0.4f + 0.2f;
			roate = (float) Math.random() * 360;
		}

		@Override
		public NBTTagCompound serializeNBT() {
			NBTTagCompound nbt = stack.serializeNBT();
			nbt.setInteger("degree", degree);
			nbt.setFloat("xoff", xoff);
			nbt.setFloat("zoff", zoff);
			nbt.setFloat("roate", roate);
			return nbt;
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt) {
			degree = nbt.getInteger("degree");
			xoff = nbt.getFloat("xoff");
			zoff = nbt.getFloat("zoff");
			roate = nbt.getFloat("roate");
			stack = new ItemStack(nbt);
		}

		// 渲染的位置数据
		public float xoff, zoff, roate;
	}

	/** 掉落 */
	public void drop() {
		for (Milling m : millList) {
			if (m.degree == 0) {
				Block.spawnAsEntity(this.world, this.pos, m.stack);
			}
		}
		millList.clear();
	}

	/** 吃掉一个物品 */
	public void eatItem(EntityItem item) {
		if (this.world.isRemote) return;
		ItemStack stack = item.getItem();
		if (inventory.insertItem(0, stack, true).isEmpty()) {
			inventory.insertItem(0, stack, false);
			item.setDead();
		}

	}

	/** 获取当前粉尘数量 */
	public int getDusty() {
		return this.dusty;
	}

	/** 获取当前正在研磨的物品 */
	public ItemStack getCurrMillingItem() {
		return this.millingItem;
	}

	/** 获取指定物品的粉末量 */
	public int getDustyCount(ItemStack stack) {
		return 250;
	}

	/** 获取产物 */
	public ItemStack getResult(ItemStack stack) {
		return new ItemStack(ESObjects.BLOCKS.STAR_SAND);
	}

	/** 获取产物消耗的粉尘数 */
	public int getResultDustyCount(ItemStack stack) {
		return 1000;
	}

	public void millOrDrop() {
		this.mill();
		this.millDrop();
	}

	// 进行一次研磨
	public void mill() {
		if (this.getCurrMillingItem().isEmpty()) return;
		if (restTick <= 20) restTick = 60;
	}

	// 获取研磨产物
	public void millDrop() {
		if (this.millingItem.isEmpty()) return;
		int need = this.getResultDustyCount(this.millingItem);
		if (need <= this.dusty) {
			ItemStack result = this.getResult(this.millingItem);
			this.dusty -= need;
			if (this.dusty == 0) {
				if (this.millList.isEmpty()) this.millingItem = ItemStack.EMPTY;
			}
			if (!this.world.isRemote) Block.spawnAsEntity(this.world, this.pos, result);
			this.restTick = 0;
			this.markDirty();
		}
	}

	@Override
	public void update() {
		this.prevRotate = this.rotate;
		if (restTick > 0 || this.rotate > 3.1415926f) {
			restTick--;
			// 还没砸下去
			if (this.rotate < 4.7f) {
				this.rotate += ROTATE_RATE;
				if (this.rotate >= 4.7f) {
					// 砸下去的一刻，处理
					if (this.dusty < 1000) {
						Iterator<Milling> itor = millList.iterator();
						while (itor.hasNext()) {
							Milling m = itor.next();
							m.degree += 25;
							this.dusty += 25;
							if (m.degree >= this.getDustyCount(m.stack)) itor.remove();
						}
						if (this.dusty > 1000) this.dusty = 1000;
						this.markDirty();
					}
				}
			} else {
				this.rotate += ROTATE_RATE;
				if (this.rotate > 3.1415926f * 2) {
					this.prevRotate -= 3.1415926f * 2;
					this.rotate -= 3.1415926f * 2;
				}
			}
			if (restTick == 0) this.markDirty();
			if (world.isRemote) this.renderClient(true);
		} else {
			// 结束旋转
			if (this.rotate > 0) {
				if (this.rotate < 3.1415926f) {
					this.rotate += ROTATE_RATE;
					if (this.rotate > 3.1415926f) this.prevRotate = this.rotate = 0;
				} else {
					this.rotate += ROTATE_RATE;
					if (this.rotate > 3.1415926f * 2) this.prevRotate = this.rotate = 0;
				}
			}
			if (world.isRemote) this.renderClient(false);
		}
	}

	// [客户端]渲染使用的抬手时间
	@SideOnly(Side.CLIENT)
	public float liftTick;
	@SideOnly(Side.CLIENT)
	public float prevLiftTick;
	@SideOnly(Side.CLIENT)
	public float playerRoate;
	@SideOnly(Side.CLIENT)
	public float prevPlayerRoate;

	@SideOnly(Side.CLIENT)
	public void renderClient(boolean lift) {
		this.prevLiftTick = this.liftTick;
		this.prevPlayerRoate = this.playerRoate;

		if (lift) {
			if (this.liftTick < 1.0f) this.liftTick += 0.05f;
			EntityPlayer entityplayer = this.world.getClosestPlayer((double) ((float) this.pos.getX() + 0.5F),
					(double) ((float) this.pos.getY() + 0.5F), (double) ((float) this.pos.getZ() + 0.5F), 4.0D, false);
			if (entityplayer != null) {
				double d0 = entityplayer.posX - (double) ((float) this.pos.getX() + 0.5F);
				double d1 = entityplayer.posZ - (double) ((float) this.pos.getZ() + 0.5F);
				this.playerRoate = -(float) MathHelper.atan2(d1, d0) + 3.1415926f;
				if (this.playerRoate > 3.1415926f) this.playerRoate -= 3.1415926f * 2;
			}
		} else {
			if (this.liftTick > 0.0f) this.liftTick -= 0.05f;
		}
	}

	static final public ResourceLocation TEX_STAR_SAND = new ResourceLocation(ESAPI.MODID,
			"textures/blocks/star_sand.png");

	@SideOnly(Side.CLIENT)
	public ResourceLocation getDustyTexture() {
		return TEX_STAR_SAND;
	}

	/** 获取研磨列表 */
	@SideOnly(Side.CLIENT)
	public List<Milling> getMillList() {
		return millList;
	}

	/** 获取研磨物品的高度，渲染使用 */
	@SideOnly(Side.CLIENT)
	public float getHight(ItemStack stack) {
		return 0.25f;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		millList = NBTHelper.getNBTSerializableList(compound, "inv", Milling.class, NBTTagCompound.class);
		dusty = compound.getInteger("dusty");
		if (compound.hasKey("milling")) millingItem = new ItemStack((NBTTagCompound) compound.getTag("milling"));
		if (this.isSending()) return;
		super.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		NBTHelper.setNBTSerializableList(compound, "inv", millList);
		compound.setInteger("dusty", dusty);
		compound.setTag("milling", millingItem.serializeNBT());
		if (this.isSending()) return compound;
		return super.writeToNBT(compound);
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
}

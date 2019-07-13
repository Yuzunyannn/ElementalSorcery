package yuzunyannn.elementalsorcery.tile.altar;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleFirework;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.api.ability.IAltarWake;
import yuzunyannn.elementalsorcery.api.ability.IElementInventory;
import yuzunyannn.elementalsorcery.api.ability.IGetItemStack;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.util.ElementHelper;
import yuzunyannn.elementalsorcery.building.Buildings;
import yuzunyannn.elementalsorcery.building.MultiBlock;
import yuzunyannn.elementalsorcery.capability.Spellbook;
import yuzunyannn.elementalsorcery.init.ESInitInstance;
import yuzunyannn.elementalsorcery.item.ItemSpellbook;
import yuzunyannn.elementalsorcery.render.item.SpellbookRenderInfo;
import yuzunyannn.elementalsorcery.tile.TileElementalCube;

public class TileMagicDesk extends TileStaticMultiBlock implements ITickable, IGetItemStack {

	// 记录的物品
	private ItemStack book = ItemStack.EMPTY;
	// 是否合成了一个新的物品
	private boolean crafting_success = false;

	public ItemStack getBook() {
		return book;
	}

	public void setBook(ItemStack book) {
		this.book = book;
		this.markDirty();
		if (book.isEmpty())
			return;
		if (book.hasCapability(Spellbook.SPELLBOOK_CAPABILITY, null)) {
			if (world.isRemote) {
				Spellbook spellbook = book.getCapability(Spellbook.SPELLBOOK_CAPABILITY, null);
				ItemSpellbook.renderStart(spellbook);
			}
		} else if (!world.isRemote) {
			ElementalSorcery.logger.warn("魔法书桌上不应该放入除spellbook以外的物品");
		}
	}

	@Override
	public void setStack(ItemStack stack) {
		this.setBook(stack);
		this.updateToClient();
	}

	@Override
	public ItemStack getStack() {
		return this.getBook();
	}

	@Override
	public boolean canSetStack(ItemStack stack) {
		return stack.getItem() instanceof ItemSpellbook;
	}

	@Override
	public void initMultiBlock() {
		structure = new MultiBlock(Buildings.SPELLBOOK_ALTAR, this, new BlockPos(0, -1, 0));
		structure.addSpecialBlock(new BlockPos(0, 1, 3));
		structure.addSpecialBlock(new BlockPos(0, 1, -3));
		structure.addSpecialBlock(new BlockPos(3, 1, 0));
		structure.addSpecialBlock(new BlockPos(-3, 1, 0));
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		if (compound.hasKey("book"))
			book = new ItemStack(compound.getCompoundTag("book"));
		else
			book = ItemStack.EMPTY;
		if (this.isSending()) {
			this.readNBTToUpdate(compound);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setTag("book", book.serializeNBT());
		if (this.isSending()) {
			this.writeNBTToSend(compound);
		}
		return super.writeToNBT(compound);
	}

	public NBTTagCompound writeNBTToSend(NBTTagCompound nbt) {
		nbt.setBoolean("CS", this.crafting_success);
		return nbt;
	}

	public void readNBTToUpdate(NBTTagCompound nbt) {
		this.crafting_success = nbt.getBoolean("CS");
		if (this.crafting_success && this.world.isRemote) {
			this.finishCraftingClient(this.book);
			this.crafting_success = false;
		}
	}

	private float tRot = 0;
	private int tick = 0;

	@Override
	public void update() {
		if (book.isEmpty())
			return;
		if (!book.hasCapability(Spellbook.SPELLBOOK_CAPABILITY, null))
			return;
		tick++;
		if (tick % 40 == 0)
			ok = structure.check(EnumFacing.NORTH);
		if (world.isRemote)
			this.updateClientBookRedner();
		if (ok == false)
			return;
		// 更新给书充能
		if (tick % 2 == 0)
			this.updateCharge();
		if (tick % 10 != 0)
			return;
		final float range = 1.5f;
		AxisAlignedBB aabb = new AxisAlignedBB(pos.getX() + 0.5 - range, pos.getY(), pos.getZ() + 0.5 - range,
				pos.getX() + 0.5 + range, pos.getY() + 0.5, pos.getZ() + 0.5 + range);
		// 将物品弹到祭坛上
		List<EntityItem> list = world.getEntitiesWithinAABB(EntityItem.class, aabb);
		for (EntityItem eitem : list) {
			if (eitem.motionY < 0.001) {
				Vec3d tar = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5)
						.subtract(eitem.getPositionVector());
				if (tar.lengthSquared() > 1) {
					eitem.motionY = 40 * 0.012;
					tar = tar.scale(0.05);
					eitem.motionX = tar.x;
					eitem.motionZ = tar.z;
				} else {
					tar = tar.scale(0.075);
					eitem.motionX = -tar.x;
					eitem.motionZ = -tar.z;
				}
			}
		}
		if (world.isRemote)
			return;
		aabb = new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 2, pos.getZ() + 1);
		// 获取祭坛上面的物品，进行合成
		list = world.getEntitiesWithinAABB(EntityItem.class, aabb);
		for (EntityItem eitem : list) {
			eitem.moveToBlockPosAndAngles(pos, 0, 0);
			ItemStack stack = eitem.getItem();
			eitem.setItem(ItemStack.EMPTY);
			this.eat(stack);
		}
	}

	// 充能
	public void updateCharge() {
		Spellbook spellbook = book.getCapability(Spellbook.SPELLBOOK_CAPABILITY, null);
		IElementInventory inv = spellbook.getInventory();
		if (inv == null)
			return;
		inv.loadState(book);
		ElementStack need = ItemSpellbook.giveMeRandomElement(inv).copy();
		if (!need.isEmpty()) {
			need.setCount(1);
			need.weaken(0);
		}
		boolean succuess = false;
		for (int i = 0; i < 4; i++) {
			TileEntity cube = structure.getSpecialTileEntity(i);
			IAltarWake altarWake = TileStaticMultiBlock.getAlterWake(cube);
			if (altarWake == null)
				continue;
			IElementInventory inv_other = ElementHelper.getElementInventory(cube);
			if (inv_other == null)
				continue;
			ElementStack estack = TileElementalCube.getAndTestElementTransBetweenInventory(need.copy(), inv, inv_other);
			if (!estack.isEmpty()) {
				altarWake.wake(IAltarWake.SEND);
				if (world.isRemote)
					TileElementalCube.giveParticleElementTo(world, estack.getColor(), structure.getSpecialBlockPos(i),
							this.pos.up(), 0.5f);
				inv_other.extractElement(estack, false);
				inv.insertElement(estack, false);
				succuess = true;
			}
		}
		if (succuess)
			inv.saveState(book);
	}

	// 吸收物品
	public void eat(ItemStack stack) {
		if (automataList.isEmpty())
			this.resetAllAutomata();
		// 成功物品
		ItemStack finish = ItemStack.EMPTY;
		// 开始遍历自所有自动机
		Iterator<ItemAutomata> iter = automataList.iterator();
		while (iter.hasNext()) {
			ItemAutomata auto = iter.next();
			if (auto.next(stack) == false) {
				iter.remove();
				continue;
			}
			if (auto.isFinish()) {
				finish = auto.getOutput();
				break;
			}
		}
		if (!finish.isEmpty()) {
			// 完成比对
			automataList.clear();
			this.finishCrafting(finish);
		} else if (automataList.isEmpty()) {
			// 失败比对
			BlockPos pos = this.pos.up();
			world.createExplosion(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 1f, false);
		}
	}

	public void finishCrafting(ItemStack output) {
		this.setBook(output);
		this.crafting_success = true;
		this.updateToClient();
		this.crafting_success = false;
	}

	@SideOnly(Side.CLIENT)
	public void finishCraftingClient(ItemStack output) {
		if (book.hasCapability(Spellbook.SPELLBOOK_CAPABILITY, null)) {
			// 重置书的位置
			Spellbook spellbook = book.getCapability(Spellbook.SPELLBOOK_CAPABILITY, null);
			SpellbookRenderInfo info = spellbook.render_info;
			EntityPlayer entityplayer = this.world.getClosestPlayer((double) ((float) this.pos.getX() + 0.5F),
					(double) ((float) this.pos.getY() + 0.5F), (double) ((float) this.pos.getZ() + 0.5F), 3.5D, false);
			if (this.ok && entityplayer != null) {
				double d0 = entityplayer.posX - (double) ((float) this.pos.getX() + 0.5F);
				double d1 = entityplayer.posZ - (double) ((float) this.pos.getZ() + 0.5F);
				tRot = (float) MathHelper.atan2(d1, d0);
				info.bookSpread = 1.0f;
				info.bookSpread = info.bookSpread > 1.0F ? 1.0F : info.bookSpread;
				info.bookRotation = tRot;
			}
			// 新的粒子效果
			BlockPos pos = this.pos.up();
			Overlay effect = new Overlay(this.world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
			effect.setRBGColorF(1.0f, 1.0f, 1.0f);
			Minecraft.getMinecraft().effectRenderer.addEffect(effect);
		}
	}

	// 临时的例子效果
	@SideOnly(Side.CLIENT)
	public static class Overlay extends ParticleFirework.Overlay {
		protected Overlay(World p_i46466_1_, double p_i46466_2_, double p_i46466_4_, double p_i46466_6_) {
			super(p_i46466_1_, p_i46466_2_, p_i46466_4_, p_i46466_6_);
		}
	}

	private static List<ItemAutomata> automataList = new LinkedList<ItemAutomata>();

	private void resetAllAutomata() {
		automataList.clear();
		if (this.book.getItem() == ESInitInstance.ITEMS.SPELLBOOK) {
			automataList.add(new ItemAutomata(AUTO_LAUNCH_BOOK, ESInitInstance.ITEMS.SPELLBOOK_LAUNCH));
			automataList.add(new ItemAutomata(AUTO_ELEMENT_BOOK, ESInitInstance.ITEMS.SPELLBOOK_ELEMENT));
		}
	}

	// 特效的更新
	public void updateClientBookRedner() {
		Spellbook spellbook = book.getCapability(Spellbook.SPELLBOOK_CAPABILITY, null);
		SpellbookRenderInfo info = spellbook.render_info;
		info.tickCount++;
		info.bookSpreadPrev = info.bookSpread;
		info.pageFlipPrev = info.pageFlip;
		info.bookRotationPrev = info.bookRotation;
		EntityPlayer entityplayer = this.world.getClosestPlayer((double) ((float) this.pos.getX() + 0.5F),
				(double) ((float) this.pos.getY() + 0.5F), (double) ((float) this.pos.getZ() + 0.5F), 3.5D, false);
		if (this.ok && entityplayer != null) {
			double d0 = entityplayer.posX - (double) ((float) this.pos.getX() + 0.5F);
			double d1 = entityplayer.posZ - (double) ((float) this.pos.getZ() + 0.5F);
			tRot = (float) MathHelper.atan2(d1, d0);
			info.bookSpread += 0.075f;
			info.bookSpread = info.bookSpread > 1.0F ? 1.0F : info.bookSpread;
		} else {
			tRot = 0;
			if (info.bookSpread > 0) {
				info.bookSpread -= 0.075f;
			} else
				info.bookSpread = 0;
		}
		float f2;
		for (f2 = this.tRot - info.bookRotation; f2 >= (float) Math.PI; f2 -= ((float) Math.PI * 2F))
			;
		while (f2 < -(float) Math.PI)
			f2 += ((float) Math.PI * 2F);
		info.bookRotation += f2 * 0.25F;
		info.pageFlip += (1.0f - info.bookSpread) * 1.25;
		info.pageFlip *= 0.85F;
	}

	// 各种合成表
	public static List<ItemStack> AUTO_LAUNCH_BOOK;
	public static List<ItemStack> AUTO_ELEMENT_BOOK;

	public static void init() {
		// launch
		AUTO_LAUNCH_BOOK = new ArrayList<ItemStack>();
		AUTO_LAUNCH_BOOK.add(new ItemStack(ESInitInstance.ITEMS.MAGIC_CRYSTAL, 3));
		AUTO_LAUNCH_BOOK.add(new ItemStack(Blocks.OBSIDIAN, 2));
		// element
		AUTO_ELEMENT_BOOK = new ArrayList<ItemStack>();
		AUTO_ELEMENT_BOOK.add(new ItemStack(ESInitInstance.ITEMS.MAGIC_CRYSTAL, 10));
		AUTO_ELEMENT_BOOK.add(new ItemStack(Items.DIAMOND, 2));
	}

	// ItemStack的自动机
	public static class ItemAutomata {
		final List<ItemStack> list;
		final ItemStack output;
		Iterator<ItemStack> iter;
		ItemStack current = ItemStack.EMPTY;
		private boolean finish;

		public ItemAutomata(List<ItemStack> list, ItemStack output) {
			this.list = list;
			this.output = output;
			this.reset();
		}

		public ItemAutomata(List<ItemStack> list, Item output) {
			this.list = list;
			this.output = new ItemStack(output);
			this.reset();
		}

		public ItemStack getOutput() {
			return this.output;
		}

		public void reset() {
			finish = false;
			iter = list.iterator();
			if (!iter.hasNext())
				finish = true;
		}

		// 自动机要求必须完全一样
		public boolean next(ItemStack stack) {
			if (this.finish) {
				this.reset();
				return true;
			}
			ItemStack come = stack.copy();
			while (!come.isEmpty()) {
				if (current.isEmpty()) {
					if (!iter.hasNext())
						return false;
					current = iter.next().copy();
				} else {
					if (!ItemStack.areItemsEqual(current, come))
						return false;
					int size = Math.min(current.getCount(), come.getCount());
					current.shrink(size);
					come.shrink(size);
				}
			}
			if (!iter.hasNext() && current.isEmpty()) {
				finish = true;
				return true;
			}
			return true;
		}

		public boolean isFinish() {
			return finish;
		}

	}

}

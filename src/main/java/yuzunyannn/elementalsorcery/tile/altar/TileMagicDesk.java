package yuzunyannn.elementalsorcery.tile.altar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleFirework;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.advancement.ESCriteriaTriggers;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.tile.IAltarWake;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.api.tile.IGetItemStack;
import yuzunyannn.elementalsorcery.building.Buildings;
import yuzunyannn.elementalsorcery.building.MultiBlock;
import yuzunyannn.elementalsorcery.capability.Spellbook;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.elf.research.AncientPaper;
import yuzunyannn.elementalsorcery.grimoire.mantra.Mantra;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.item.book.ItemSpellbook;
import yuzunyannn.elementalsorcery.render.item.SpellbookRenderInfo;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

public class TileMagicDesk extends TileStaticMultiBlock implements ITickable, IGetItemStack {

	// 记录的物品
	private ItemStack book = ItemStack.EMPTY;
	// 是否合成了一个新的物品
	private boolean craftingSuccess = false;

	public ItemStack getBook() {
		return book;
	}

	public void setBook(ItemStack book) {
		this.book = book;
		this.markDirty();
		if (book.isEmpty()) return;
		if (book.hasCapability(Spellbook.SPELLBOOK_CAPABILITY, null)) {
			if (!world.isRemote) return;
			Spellbook spellbook = book.getCapability(Spellbook.SPELLBOOK_CAPABILITY, null);
			ItemSpellbook.renderStart(spellbook);
			return;
		}
		if (book.getItem() == ESInit.ITEMS.ANCIENT_PAPER) return;

		ElementalSorcery.logger.warn("魔法书桌上不应该放入除spellbook和ancient_paper以外的物品");
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
		return stack.getItem() instanceof ItemSpellbook
				|| (stack.getItem() == ESInit.ITEMS.ANCIENT_PAPER && stack.getMetadata() == 2);
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
		if (compound.hasKey("book")) book = new ItemStack(compound.getCompoundTag("book"));
		else book = ItemStack.EMPTY;
		if (this.isSending()) this.readNBTToUpdate(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setTag("book", book.serializeNBT());
		if (this.isSending()) this.writeNBTToSend(compound);
		return super.writeToNBT(compound);
	}

	public NBTTagCompound writeNBTToSend(NBTTagCompound nbt) {
		nbt.setBoolean("CS", this.craftingSuccess);
		return nbt;
	}

	public void readNBTToUpdate(NBTTagCompound nbt) {
		this.craftingSuccess = nbt.getBoolean("CS");
		if (this.craftingSuccess && this.world.isRemote) {
			this.finishCraftingClient(this.book);
			this.craftingSuccess = false;
		}
	}

	private float tRot = 0;
	private int tick = 0;

	@Override
	public void update() {
		if (book.isEmpty()) return;
		tick++;
		if (tick % 40 == 0) ok = structure.check(EnumFacing.NORTH);
		if (world.isRemote) this.updateClientBookRender();
		if (ok == false) return;
		// 更新给书充能
		if (tick % 2 == 0) this.updateCharge();
		if (tick % 10 != 0) return;
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
		if (world.isRemote) return;
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
		if (spellbook == null) return;
		IElementInventory inv = spellbook.getInventory();
		if (inv == null) return;
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
			if (altarWake == null) continue;
			IElementInventory inv_other = ElementHelper.getElementInventory(cube);
			if (inv_other == null) continue;
			ElementStack estack = TileElementalCube.getAndTestElementTransBetweenInventory(need.copy(), inv, inv_other);
			if (!estack.isEmpty()) {
				altarWake.wake(IAltarWake.SEND, this.pos);
				if (world.isRemote && world.rand.nextFloat() <= 0.5) altarWake.updateEffect(world, IAltarWake.SEND,
						estack, new Vec3d(this.pos.up()).add(0.5, 0.5, 0.5));
				inv_other.extractElement(estack, false);
				inv.insertElement(estack, false);
				succuess = true;
			}
		}
		if (succuess) inv.saveState(book);
	}

	// 吸收物品
	public void eat(ItemStack stack) {
		if (automataList.isEmpty()) this.resetAllAutomata();
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
			this.finishCrafting(finish.copy());
		} else if (automataList.isEmpty()) {
			// 失败比对
			BlockPos pos = this.pos.up();
			world.createExplosion(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 1f, false);
		}
	}

	public void finishCrafting(ItemStack output) {
		this.setBook(output);
		this.craftingSuccess = true;
		this.updateToClient();
		this.craftingSuccess = false;
		if (!world.isRemote) {
			// 成就
			final int size = 6;
			AxisAlignedBB aabb = new AxisAlignedBB(pos.getX() - size, pos.getY() - size, pos.getZ() - size,
					pos.getX() + size, pos.getY() + size, pos.getZ() + size);
			List<EntityPlayerMP> list = world.getEntitiesWithinAABB(EntityPlayerMP.class, aabb);
			for (EntityPlayerMP player : list) ESCriteriaTriggers.ES_ITEMSTACK.trigger(player, "spellbook", output);
		}
	}

	@SideOnly(Side.CLIENT)
	public void finishCraftingClient(ItemStack output) {
		if (book.hasCapability(Spellbook.SPELLBOOK_CAPABILITY, null)) {
			// 重置书的位置
			Spellbook spellbook = book.getCapability(Spellbook.SPELLBOOK_CAPABILITY, null);
			SpellbookRenderInfo info = spellbook.renderInfo;
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
		}
		// 新的粒子效果
		BlockPos pos = this.pos.up();
		Overlay effect = new Overlay(this.world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
		effect.setRBGColorF(1.0f, 1.0f, 1.0f);
		Minecraft.getMinecraft().effectRenderer.addEffect(effect);
	}

	// 临时的例子效果
	@SideOnly(Side.CLIENT)
	public static class Overlay extends ParticleFirework.Overlay {
		protected Overlay(World p_i46466_1_, double p_i46466_2_, double p_i46466_4_, double p_i46466_6_) {
			super(p_i46466_1_, p_i46466_2_, p_i46466_4_, p_i46466_6_);
		}
	}

	// 特效的更新
	@SideOnly(Side.CLIENT)
	public void updateClientBookRender() {
		Spellbook spellbook = book.getCapability(Spellbook.SPELLBOOK_CAPABILITY, null);
		if (spellbook == null) return;
		SpellbookRenderInfo info = spellbook.renderInfo;
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
			} else info.bookSpread = 0;
		}
		float f2;
		for (f2 = this.tRot - info.bookRotation; f2 >= (float) Math.PI; f2 -= ((float) Math.PI * 2F));
		while (f2 < -(float) Math.PI) f2 += ((float) Math.PI * 2F);
		info.bookRotation += f2 * 0.25F;
		info.pageFlip += (1.0f - info.bookSpread) * 1.25;
		info.pageFlip *= 0.85F;
	}

	/** 自动机匹配表 */
	private final List<ItemAutomata> automataList = new LinkedList<ItemAutomata>();

	private void resetAllAutomata() {
		automataList.clear();
		for (TileMagicDesk.Recipe r : getRecipes()) {
			ItemStack input = r.getInput();
			if (input.isItemEqual(book)) automataList.add(new ItemAutomata(r.getSequence(), r.getOutput()));
		}
	}

	static public class Recipe {
		ItemStack output;
		ItemStack input;
		NonNullList<ItemStack> sequence;

		public ItemStack getOutput() {
			return output;
		}

		public ItemStack getInput() {
			return input;
		}

		public NonNullList<ItemStack> getSequence() {
			return sequence;
		}
	}

	static private final List<Recipe> recipes = new ArrayList<>();

	public static List<Recipe> getRecipes() {
		return recipes;
	}

	static public void addRecipe(ItemStack input, ItemStack output, ItemStack... stacks) {
		if (stacks.length == 0) return;
		if (input.isEmpty() || output.isEmpty()) return;
		Recipe r = new Recipe();
		r.input = input;
		r.output = output;
		r.sequence = NonNullList.from(ItemStack.EMPTY, stacks);
		recipes.add(r);
	}

	static public void addRecipeFromMantra(Mantra mantra, ItemStack... stacks) {
		ItemStack input = new ItemStack(ESInit.ITEMS.ANCIENT_PAPER, 1, 2);
		ItemStack output = new ItemStack(ESInit.ITEMS.ANCIENT_PAPER, 1, 3);

		AncientPaper ap = new AncientPaper();
		ap.setMantra(mantra).setStart(0).setEnd(100);
		ap.saveState(output);

		addRecipe(input, output, stacks);
	}

	static public List<ItemStack> findRecipe(ItemStack input) {
		for (TileMagicDesk.Recipe r : getRecipes()) {
			if (ItemStack.areItemsEqual(r.getInput(), input)) return r.sequence;
		}
		return Collections.emptyList();
	}

	// 各种合成表
	public static void init() {
		ESObjects.Items ITEMS = ESInit.ITEMS;
		// launch
		addRecipe(new ItemStack(ITEMS.SPELLBOOK), new ItemStack(ITEMS.SPELLBOOK_LAUNCH),
				new ItemStack(ESInit.ITEMS.QUILL, 1, 1), new ItemStack(ESInit.ITEMS.MAGIC_CRYSTAL, 3),
				new ItemStack(ESInit.ITEMS.ELF_CRYSTAL, 10), new ItemStack(Blocks.CRAFTING_TABLE, 2),
				new ItemStack(ESInit.ITEMS.PARCHMENT, 16));
		// element
		addRecipe(new ItemStack(ITEMS.SPELLBOOK), new ItemStack(ITEMS.SPELLBOOK_ELEMENT),
				new ItemStack(ESInit.ITEMS.QUILL, 1, 1), new ItemStack(ESInit.ITEMS.MAGIC_CRYSTAL, 10),
				new ItemStack(ESInit.ITEMS.MAGICAL_ENDER_EYE, 1), new ItemStack(ESInit.ITEMS.ELF_CRYSTAL, 32),
				new ItemStack(Blocks.RED_FLOWER, 6), new ItemStack(Items.GOLD_INGOT, 3), new ItemStack(Items.COAL, 7),
				new ItemStack(Blocks.OBSIDIAN, 2));
		// building
		addRecipe(new ItemStack(ITEMS.SPELLBOOK), new ItemStack(ITEMS.SPELLBOOK_ARCHITECTURE),
				new ItemStack(ESInit.ITEMS.QUILL, 1, 1), new ItemStack(ESInit.BLOCKS.ESTONE, 16),
				new ItemStack(ESInit.ITEMS.ARCHITECTURE_CRYSTAL, 1), new ItemStack(ESInit.BLOCKS.KYANITE_BLOCK, 2),
				new ItemStack(Blocks.STONE, 16), new ItemStack(Blocks.IRON_BLOCK, 1),
				new ItemStack(Blocks.GOLD_BLOCK, 1), new ItemStack(Blocks.REDSTONE_BLOCK, 1),
				new ItemStack(Blocks.OBSIDIAN, 1), new ItemStack(Blocks.BRICK_BLOCK, 2));
		// 咒文
		addRecipeFromMantra(ESInit.MANTRAS.LAUNCH_ECR,
				ItemHelper.toArray(ITEMS.QUILL, 1, 1, Blocks.CRAFTING_TABLE, 2, ITEMS.QUILL, 1, 1));
		addRecipeFromMantra(ESInit.MANTRAS.LAUNCH_EDE,
				ItemHelper.toArray(ITEMS.QUILL, 1, 1, ITEMS.AZURE_CRYSTAL, 5, ITEMS.QUILL, 1, 1));
		addRecipeFromMantra(ESInit.MANTRAS.LAUNCH_ECO,
				ItemHelper.toArray(ITEMS.QUILL, 1, 1, ITEMS.ORDER_CRYSTAL, 5, ITEMS.QUILL, 1, 1));
		addRecipeFromMantra(ESInit.MANTRAS.LAUNCH_BRC,
				ItemHelper.toArray(ITEMS.QUILL, 1, 1, ITEMS.ARCHITECTURE_CRYSTAL, 3, ITEMS.QUILL, 1, 1));
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
			if (!iter.hasNext()) finish = true;
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
					if (!iter.hasNext()) return false;
					current = iter.next().copy();
				} else {
					if (!ItemStack.areItemsEqual(current, come)) return false;
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

package yuzunyannn.elementalsorcery.item.book;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.tile.IAcceptMagic;
import yuzunyannn.elementalsorcery.api.tile.IAltarWake;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.capability.ElementInventory;
import yuzunyannn.elementalsorcery.capability.Spellbook;
import yuzunyannn.elementalsorcery.config.Config;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.element.EffectElementAbsorb;
import yuzunyannn.elementalsorcery.render.item.RenderItemSpellbook;
import yuzunyannn.elementalsorcery.render.item.SpellbookRenderInfo;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class ItemSpellbookElement extends ItemSpellbook {

	@Config(kind = "item", sync = true)
	private int AR_COUNT_PRE_TICK = 2;

	final int level;

	public ItemSpellbookElement() {
		this.setUnlocalizedName("spellbookElement");
		this.level = 1;
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void initRenderInfo(SpellbookRenderInfo info) {
		info.texture = RenderItemSpellbook.instance.TEXTURE_SPELLBOOK_ELEMENT;
	}

	@Override
	protected IElementInventory getInventory(ItemStack stack) {
		return new ElementInventory(1);
	}

	@Override
	public int getCast(Spellbook book) {
		return 20;
	}

	@Override
	public void swap(World world, EntityLivingBase entity, ItemStack stack, Spellbook book) {
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null) stack.setTagCompound(nbt = new NBTTagCompound());
		boolean isAbsorb = nbt.getBoolean("absorb");
		nbt.setBoolean("absorb", !isAbsorb);

		if (world.isRemote && entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			String uText = !isAbsorb ? "info.spbe.absorb" : "info.spbe.release";
			player.sendMessage(new TextComponentTranslation(uText));
		}
	}

	@Override
	public boolean spellBegin(World world, EntityLivingBase entity, ItemStack stack, Spellbook book) {
		NBTTagCompound nbt = stack.getTagCompound();
		book.obj = nbt == null ? null : (nbt.getBoolean("absorb") ? true : null);
		return true;
	}

	@Override
	public void spelling(World world, EntityLivingBase entity, ItemStack stack, Spellbook book, int power) {
		if (power < this.getCast(book)) return;

		if (book.obj == null) {

			IElementInventory inventory = book.getInventory();
			ElementStack estack = inventory.getStackInSlot(0);
			if (estack.isEmpty()) return;

			RayTraceResult rt = WorldHelper.getLookAtBlock(world, entity, 32);
			if (rt == null) return;
			TileEntity tile = world.getTileEntity(rt.getBlockPos());

			if (spellingInsert(world, entity, tile, estack)) return;
			spellingInsertMD(world, entity, tile, estack, rt.sideHit);
		} else {

			RayTraceResult rt = WorldHelper.getLookAtBlock(world, entity, 32);
			if (rt == null) return;
			TileEntity tile = world.getTileEntity(rt.getBlockPos());
			IElementInventory eInv = ElementHelper.getElementInventory(tile);
			if (eInv == null) return;

			IElementInventory inventory = book.getInventory();

			for (int i = 0; i < eInv.getSlots(); i++) {
				ElementStack estack = eInv.getStackInSlot(i);
				if (estack.isEmpty()) continue;
				ElementStack e = estack.copy();
				e.setCount(Math.min(AR_COUNT_PRE_TICK, e.getCount()));

				if (inventory.insertElement(e, true)) {
					if (tile instanceof IAltarWake) ((IAltarWake) tile).wake(IAltarWake.SEND, null);
					if (world.isRemote)
						flyEffect(world, e.getColor(), new Vec3d(tile.getPos()).addVector(0.5, 0.5, 0.5), entity);
					inventory.insertElement(e, false);
					estack.shrink(e.getCount());
					break;
				}
			}

		}

	}

	/** 插入元素容器 */
	private boolean spellingInsert(World world, EntityLivingBase entity, TileEntity tile, ElementStack estack) {
		IElementInventory eInv = ElementHelper.getElementInventory(tile);
		if (eInv == null) return false;
		ElementStack e = estack.copy();
		e.setCount(Math.min(AR_COUNT_PRE_TICK, e.getCount()));
		if (!eInv.insertElement(e, true)) return false;
		if (tile instanceof IAltarWake) ((IAltarWake) tile).wake(IAltarWake.OBTAIN, null);
		if (world.isRemote) flyEffect(world, e.getColor(), entity.getPositionVector().addVector(0, 0.5, 0),
				new Vec3d(tile.getPos()).addVector(0.5, 0.5, 0.5));
		eInv.insertElement(e, false);
		estack.shrink(e.getCount());
		return true;
	}

	/** 插入魔力设备 */
	private boolean spellingInsertMD(World world, EntityLivingBase entity, TileEntity tile, ElementStack estack,
			EnumFacing face) {
		if (!(tile instanceof IAcceptMagic)) return false;
		IAcceptMagic accept = (IAcceptMagic) tile;

		ElementStack e = estack.splitStack(2);
		ElementStack magic = e.becomeMagic(world);

		ElementStack ret = accept.accpetMagic(magic, entity.getPosition(), face);

		if (world.isRemote) flyEffect(world, e.getColor(), entity.getPositionVector().addVector(0, 0.5, 0),
				new Vec3d(tile.getPos()).addVector(0.5, 0.5, 0.5));

		estack.grow(ret);

		return true;
	}

	@SideOnly(Side.CLIENT)
	public void flyEffect(World world, int color, Vec3d from, Vec3d to) {
		EffectElementAbsorb eea = new EffectElementAbsorb(world, from, to);
		eea.setColor(color);
		Effect.addEffect(eea);
	}

	@SideOnly(Side.CLIENT)
	public void flyEffect(World world, int color, Vec3d from, EntityLivingBase to) {
		EffectElementAbsorb eea = new EffectElementAbsorb(world, from, to);
		eea.setColor(color);
		Effect.addEffect(eea);
	}

	// 元素书的信息
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {

		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null) return;
		boolean isAbsorb = nbt.getBoolean("absorb");
		if (isAbsorb) tooltip.add(TextFormatting.GOLD + I18n.format("info.spbe.absorb"));
		else tooltip.add(TextFormatting.GOLD + I18n.format("info.spbe.release"));

		Spellbook book = stack.getCapability(Spellbook.SPELLBOOK_CAPABILITY, null);
		IElementInventory inventory = book.getInventory();
		inventory.loadState(stack);
		ElementHelper.addElementInformation(inventory, worldIn, tooltip, flagIn);
	}

}

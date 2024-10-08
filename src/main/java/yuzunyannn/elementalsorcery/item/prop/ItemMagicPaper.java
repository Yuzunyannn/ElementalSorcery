package yuzunyannn.elementalsorcery.item.prop;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.element.ElementTransition;
import yuzunyannn.elementalsorcery.api.tile.IAltarWake;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.config.Config;
import yuzunyannn.elementalsorcery.render.effect.Effects;
import yuzunyannn.elementalsorcery.render.effect.scrappy.FireworkEffect;
import yuzunyannn.elementalsorcery.tile.altar.TileStaticMultiBlock;
import yuzunyannn.elementalsorcery.tile.md.TileMDBase;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;

public class ItemMagicPaper extends Item {

	@Config
	static public double UPGRADE_MAGIC_FRAGMENT_NEED = 320000;

	public ItemMagicPaper() {
		this.setHasSubtypes(true);
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (!this.isInCreativeTab(tab)) return;
		for (EnumType type : EnumType.values()) items.add(new ItemStack(this, 1, type.getMeta()));
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		return "item." + EnumType.fromId(stack.getMetadata()).getTranslationKey();
	}

	@Override
	public int getMetadata(int damage) {
		return damage;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		return stack.getMetadata() != 0 || stack.isItemEnchanted();
	}

	public static enum EnumType {
		MAGIC("magicPaper"),
		SPELL("spellPaper"),
		WRITTEN("writtenPaper"),
		MANTRA("mantraPaper");

		final String unlocalizedName;

		EnumType(String unlocalizedName) {
			this.unlocalizedName = unlocalizedName;
		}

		public int getMeta() {
			return this.ordinal();
		}

		public String getTranslationKey() {
			return unlocalizedName;
		}

		public String getName() {
			return this.name().toLowerCase();
		}

		public static EnumType fromId(int id) {
			EnumType[] types = EnumType.values();
			return types[id % types.length];
		}
	}

	public static float getMagicContain(ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null) return 0;
		return nbt.getFloat("mPoint");
	}

	public static void growMagicContain(ItemStack stack, float point) {
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null) stack.setTagCompound(nbt = new NBTTagCompound());
		nbt.setFloat("mPoint", point + nbt.getFloat("mPoint"));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		if (stack.getMetadata() != EnumType.WRITTEN.getMeta()) return;
		float point = getMagicContain(stack);
		tooltip.add(TextFormatting.DARK_PURPLE + I18n.format("info.power", String.format("%.2f", point)));
	}

	@Override
	public boolean onEntityItemUpdate(EntityItem entityItem) {
		ItemStack stack = entityItem.getItem();
		if (stack.getMetadata() != EnumType.WRITTEN.getMeta()) return false;
		if (!entityItem.world.isRemote) entityItem.setNoDespawn();
		if (entityItem.ticksExisted % 20 != 0) return false;
		int size = 6;
		World world = entityItem.world;
		BlockPos pos = entityItem.getPosition();
		for (int x = -size; x <= size; x++) {
			for (int z = -size; z <= size; z++) {

				if (Math.abs(x) < 2 && Math.abs(z) < 2) continue;

				BlockPos at = pos.add(x, 0, z);

				TileEntity tile = world.getTileEntity(at);
				IAltarWake altarWake = TileStaticMultiBlock.getAlterWake(tile);
				if (altarWake == null) continue;

				IElementInventory eInv = ElementHelper.getElementInventory(tile);
				if (eInv == null) continue;

				ElementStack estack = ElementHelper.randomExtract(eInv, 4, world.rand.nextInt());
				if (estack.isEmpty()) continue;

				altarWake.wake(IAltarWake.SEND, pos);
				eInv.markDirty();
//				if (ElementHelper.isEmpty(einv)) altarWake.onInventoryStatusChange();

				if (world.isRemote) {
					for (int i = 0; i < 4; i++) {
						altarWake.updateEffect(world, IAltarWake.SEND, estack, entityItem.getPositionVector().add(0, 0.5, 0));
						if (world.rand.nextFloat() >= 0.75) break;
					}
				}

				double point = ElementTransition.toMagicFragment(estack);
				growMagicContain(stack, (float) point / stack.getCount());
			}
		}

		if (world.isRemote) return false;
		float point = getMagicContain(stack);
		if (point >= UPGRADE_MAGIC_FRAGMENT_NEED) {
			stack.setItemDamage(EnumType.MANTRA.getMeta());
			stack.setTagCompound(null);
			entityItem.setItem(stack);
			NBTTagCompound nbt = FireworkEffect.fastNBT(1, 1, 0.05f, TileMDBase.PARTICLE_COLOR, TileMDBase.PARTICLE_COLOR_FADE);
			Effects.spawnEffect(world, Effects.FIREWROK, entityItem.getPositionVector().add(0, 0.5, 0), nbt);
		}

		return false;
	}

}

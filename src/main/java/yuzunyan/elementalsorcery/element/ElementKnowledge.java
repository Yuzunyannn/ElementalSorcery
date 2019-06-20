
package yuzunyan.elementalsorcery.element;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerEnchantment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.World;
import yuzunyan.elementalsorcery.api.element.Element;
import yuzunyan.elementalsorcery.api.element.ElementStack;
import yuzunyan.elementalsorcery.api.element.IElementSpell;

public class ElementKnowledge extends Element implements IElementSpell {

	public ElementKnowledge() {
		super(rgb(192, 192, 192));
		this.setUnlocalizedName("knowledge");
	}

	@Override
	public int spellBegin(World world, EntityLivingBase entity, ElementStack estack, SpellPackage pack) {
		return IElementSpell.SPELL_ONCE;
	}

	@Override
	public void spelling(World world, EntityLivingBase entity, ElementStack estack, SpellPackage pack) {

	}

	@Override
	public void spellEnd(World world, EntityLivingBase entity, ElementStack estack, SpellPackage pack) {
		if (pack.isFail())
			return;
		if (world.isRemote)
			return;
		if (entity instanceof EntityPlayer) {
			((EntityPlayer) entity).displayGui(new InteractionObject(world, entity.getPosition()));
		}
	}

	@Override
	public int cast(ElementStack estack, int level) {
		return 20;
	}

	@Override
	public int cost(ElementStack estack, int level) {
		return 10;
	}

	@Override
	public int lowestPower(ElementStack estack, int level) {
		return 10;
	}

	@Override
	public void addInfo(ElementStack estack, World worldIn, List<String> tooltip, ITooltipFlag flagIn, int level) {
		tooltip.add(I18n.format("info.element.spell.knowledge"));
	}

	private static class InteractionObject implements IInteractionObject {
		final World world;
		final BlockPos pos;

		public InteractionObject(World world, BlockPos pos) {
			this.world = world;
			this.pos = pos;
		}

		public String getName() {
			return "container.enchant";
		}

		public boolean hasCustomName() {
			return false;
		}

		public void setCustomName(String customNameIn) {

		}

		public ITextComponent getDisplayName() {
			return (ITextComponent) (this.hasCustomName() ? new TextComponentString(this.getName())
					: new TextComponentTranslation(this.getName(), new Object[0]));
		}

		public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
			return new ContainerEnchantment(playerInventory, this.world, this.pos) {
				@Override
				public boolean canInteractWith(EntityPlayer playerIn) {
					return InteractionObject.this.pos.distanceSq(playerIn.getPosition()) <= 64;
				}
			};
		}

		public String getGuiID() {
			return "minecraft:enchanting_table";
		}

	}
}

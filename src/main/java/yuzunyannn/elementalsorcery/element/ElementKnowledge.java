
package yuzunyannn.elementalsorcery.element;

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

public class ElementKnowledge extends ElementCommon {

	public static final int COLOR = 0x9b9b9b;

	public ElementKnowledge() {
		super(COLOR, "knowledge");
	}


//	@Override
//	public void spellEnd(World world, EntityLivingBase entity, ElementStack estack, SpellPackage pack) {
//		if (pack.isFail()) return;
//		if (world.isRemote) return;
//		if (entity instanceof EntityPlayer) {
//			((EntityPlayer) entity).displayGui(new InteractionObject(world, entity.getPosition()));
//		}
//	}


	public static class InteractionObject implements IInteractionObject {
		final World world;
		final BlockPos pos;

		public InteractionObject(World world, BlockPos pos) {
			this.world = world;
			this.pos = pos;
		}

		@Override
		public String getName() {
			return "container.enchant";
		}

		@Override
		public boolean hasCustomName() {
			return false;
		}

		@Override
		public ITextComponent getDisplayName() {
			return (ITextComponent) (this.hasCustomName() ? new TextComponentString(this.getName())
					: new TextComponentTranslation(this.getName(), new Object[0]));
		}

		@Override
		public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
			return new ContainerEnchantment(playerInventory, this.world, this.pos) {
				@Override
				public boolean canInteractWith(EntityPlayer playerIn) {
					return InteractionObject.this.pos.distanceSq(playerIn.getPosition()) <= 64;
				}
			};
		}

		@Override
		public String getGuiID() {
			return "minecraft:enchanting_table";
		}

	}
}

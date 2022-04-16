
package yuzunyannn.elementalsorcery.element;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerEnchantment;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.element.explosion.EEKnowledge;
import yuzunyannn.elementalsorcery.element.explosion.ElementExplosion;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.util.element.DrinkJuiceEffectAdder;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;
import yuzunyannn.elementalsorcery.world.JuiceMaterial;

public class ElementKnowledge extends ElementCommon {

	public static final int COLOR = 0x9b9b9b;

	public ElementKnowledge() {
		super(COLOR, "knowledge");
		setTransition(3f, 90, 180);
	}

	@Override
	public ElementStack starFlowerCasting(World world, BlockPos pos, ElementStack estack, int tick) {
		if (world.isRemote) return estack;
		if (tick % 40 != 0) return estack;

		int exp = MathHelper.ceil(MathHelper.sqrt(estack.getPower()));
		int range = getStarFlowerRange(estack);
		AxisAlignedBB aabb = WorldHelper.createAABB(pos, range, range, 0.5);
		List<EntityPlayer> entities = world.getEntitiesWithinAABB(EntityPlayer.class, aabb);
		for (EntityPlayer entity : entities) WorldHelper.createExpBall(entity, exp);
		if (entities.isEmpty()) return estack;
		estack.shrink(Math.max(4, entities.size() * 4));
		return estack;
	}

	@Override
	public ElementExplosion newExplosion(World world, Vec3d pos, ElementStack eStack, EntityLivingBase attacker) {
		return new EEKnowledge(world, pos, ElementExplosion.getStrength(eStack) + 1, eStack);
	}

	@Override
	protected void addDrinkJuiceEffect(DrinkJuiceEffectAdder helper) {
		helper.preparatory(ESInit.POTIONS.COMBAT_SKILL, 30, 125);
		helper.check(JuiceMaterial.APPLE, 125).join();

		helper.preparatory(ESInit.POTIONS.DEFENSE_SKILL, 30, 150);
		helper.check(JuiceMaterial.MELON, 125).join();
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

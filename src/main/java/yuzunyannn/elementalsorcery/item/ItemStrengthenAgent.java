package yuzunyannn.elementalsorcery.item;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESObjects;

public class ItemStrengthenAgent extends ItemFood {

	public ItemStrengthenAgent() {
		super(0, false);
		this.setTranslationKey("strengthenAgent");
		this.setMaxStackSize(16);
		this.setAlwaysEdible();
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.BOW;
	}

	public static void addPotionIfNotExit(EntityPlayer player, Potion potionIn, int durationIn, int amplifierIn) {
		PotionEffect effect = player.getActivePotionEffect(potionIn);
		if (effect != null) {
			player.addPotionEffect(new PotionEffect(potionIn, effect.getDuration(), effect.getAmplifier()));
			return;
		}
		player.addPotionEffect(new PotionEffect(potionIn, durationIn, amplifierIn));
	}

	public static void addPotionGrowLevel(EntityPlayer player, Potion potionIn, int durationIn, int amplifierIn) {
		PotionEffect effect = player.getActivePotionEffect(potionIn);
		if (effect != null) {
			player.addPotionEffect(new PotionEffect(potionIn, effect.getDuration(), effect.getAmplifier() + 1));
			return;
		}
		player.addPotionEffect(new PotionEffect(potionIn, durationIn, amplifierIn));
	}

	@Override
	protected void onFoodEaten(ItemStack stack, World worldIn, EntityPlayer player) {
		if (worldIn.isRemote) return;
		player.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 20 * 10));

		addPotionGrowLevel(player, ESObjects.POTIONS.DEATH_WATCH, 20 * 60, 9);

		int tick = 20 * 60;

		addPotionIfNotExit(player, MobEffects.RESISTANCE, tick, 3);
		addPotionIfNotExit(player, MobEffects.WATER_BREATHING, tick, 0);
		addPotionIfNotExit(player, MobEffects.FIRE_RESISTANCE, tick, 0);
		addPotionIfNotExit(player, ESObjects.POTIONS.POWER_PITCHER, tick, 4);

		addPotionIfNotExit(player, MobEffects.JUMP_BOOST, tick, 4);
		addPotionIfNotExit(player, MobEffects.SPEED, tick, 4);
		addPotionIfNotExit(player, MobEffects.STRENGTH, tick, 5);
		addPotionIfNotExit(player, MobEffects.HASTE, tick, 5);

		player.getFoodStats().addExhaustion(40);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.add(I18n.format("info.dungeon.strengthenAgent"));
	}
}

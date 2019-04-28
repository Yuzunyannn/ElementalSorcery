package yuzunyan.elementalsorcery.item;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemSome {

	static private Item newItem(String unloaclizedName) {
		return new Item().setUnlocalizedName(unloaclizedName);
	}

	// 蓝晶石
	static public Item newKynaite() {
		return newItem("kynaite");
	}

	// 魔力碎片
	static public Item newMagicalPiece() {
		return new Item() {
			@SideOnly(Side.CLIENT)
			public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip,
					ITooltipFlag flagIn) {
				tooltip.add(I18n.format("info.magicalPiece.itcando"));
			}
		}.setUnlocalizedName("magicalPiece");
	}

	// 带有魔力魔的末影之眼
	static public Item newMagicalEnderEye() {
		return new ItemMagicalEnderEye();
	}

	// 魔力水晶
	static public Item newMagicalCrystal() {
		return newItem("magicalCrystal");
	}

	// 咒术水晶
	static public Item newSpellCrystal() {
		return newItem("spellCrystal");
	}

	// 魔法纸张
	static public Item newMagicPaper() {
		return newItem("magicPaper");
	}

	// 咒术纸张
	static public Item newSpellPaper() {
		return new Item() {
			@Override
			public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn) {
				stack.addEnchantment(null, 1);
			}

			@Override
			public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
				if (!this.isInCreativeTab(tab))
					return;
				ItemStack stack = new ItemStack(this, 1, 0);
				stack.addEnchantment(null, 1);
				items.add(stack);
			}
		}.setUnlocalizedName("spellPaper");
	}
}

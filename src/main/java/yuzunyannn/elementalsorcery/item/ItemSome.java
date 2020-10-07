package yuzunyannn.elementalsorcery.item;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemSome {

	static private Item newItem(String unloaclizedName) {
		return new Item().setUnlocalizedName(unloaclizedName);
	}

	/** 精灵币 */
	static public Item newElfCoin() {
		return newItem("elfCoin");
	}

	/** 蓝晶石 */
	static public Item newKyanite() {
		return newItem("kyanite");
	}

	/** 魔力碎片 */
	static public Item newMagicalPiece() {
		return new Item() {
			@SideOnly(Side.CLIENT)
			public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip,
					ITooltipFlag flagIn) {
				tooltip.add(I18n.format("info.magicalPiece.itcando"));
			}
		}.setUnlocalizedName("magicalPiece");
	}

	/** 魔石 */
	static public Item newMagicStone() {
		return newItem("magicStone");
	}
	
	/** 魔石 */
	static public Item newMagicGold() {
		return newItem("magicGold");
	}

	/** 带有魔力魔的末影之眼 */
	static public Item newMagicalEnderEye() {
		return new ItemMagicalEnderEye();
	}

	/** 精巧刻刀 */
	static public Item newTinyKnife() {
		return newItem("tinyKnife");
	}

	/** md底座 */
	static public Item newMDBase() {
		return newItem("MDBase");
	}

	/** 魔法纸张 */
	static public Item newMagicPaper() {
		return newItem("magicPaper");
	}

	/** 咒术纸张 */
	static public Item newSpellPaper() {
		return new Item() {
			@Override
			public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn) {
				stack.addEnchantment(null, 1);
			}

			@Override
			public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
				if (!this.isInCreativeTab(tab)) return;
				ItemStack stack = new ItemStack(this, 1, 0);
				NBTTagCompound nbt = new NBTTagCompound();
				stack.setTagCompound(nbt);
				NBTTagList list = new NBTTagList();
				nbt.setTag("ench", list);
				nbt = new NBTTagCompound();
				list.appendTag(nbt);
				nbt.setInteger("id", (short) -1);
				nbt.setInteger("lvl", (short) 1);
				items.add(stack);
			}
		}.setUnlocalizedName("spellPaper");
	}
}

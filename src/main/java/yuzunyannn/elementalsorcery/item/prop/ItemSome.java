package yuzunyannn.elementalsorcery.item.prop;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSimpleFoiled;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemSome {

	static private Item newItem(String unloaclizedName) {
		return new Item().setTranslationKey(unloaclizedName);
	}

	/** 遗迹宝石 */
	static public Item newRelicGem() {
		return newItem("relicGem");
	}

	/** 折光宝石 */
	static public Item newJumpGem() {
		return newItem("jumpGem");
	}

	/** 精灵之星 */
	static public Item newElfStar() {
		return new ItemSimpleFoiled().setTranslationKey("elfStar");
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
		}.setTranslationKey("magicalPiece");
	}

	/** 魔金 */
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

}

package yuzunyannn.elementalsorcery.item.crystal;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.block.BlockCrystalFlower;
import yuzunyannn.elementalsorcery.init.ESInitInstance;
import yuzunyannn.elementalsorcery.tile.TileCrystalFlower;

public class ItemCrystal extends Item {

	private static final ArrayList<ItemCrystal> crysstals = new ArrayList<ItemCrystal>();

	public static ArrayList<ItemCrystal> getCrysstals() {
		return crysstals;
	}

	public static void init() throws ReflectiveOperationException {
		Class<ESObjects.Items> cls = ESObjects.Items.class;
		Field[] fields = cls.getFields();
		for (int i = 0; i < fields.length; i++) {
			Item item = (Item) fields[i].get(ESInitInstance.ITEMS);
			if (item instanceof ItemCrystal) crysstals.add((ItemCrystal) item);
		}
	}

	/** 咒术水晶 */
	static public Item newSpellCrystal() {
		return new ItemCrystal("spellCrystal", 34.3f, 0x989898);
	}

	/** 湛蓝水晶 */
	static public Item newAzureCrystal() {
		return new ItemCrystal("azureCrystal", 89.7f, 0x2279c5);
	}

	/** 精灵水晶（结晶） */
	static public Item newElfCrystal() {
		return new ItemCrystal("elfCrystal", 22.22f, 0x096b18);
	}

	/** 秩序水晶 */
	static public Item newOrderCrystal() {
		return new ItemCrystal("orderCrystal", 59.35f, 0x385ab5);
	}

	protected float frequency = 0.0f;
	protected int color = 0;

	public ItemCrystal() {

	}

	public ItemCrystal(String unlocalizedName, float frequency, int color) {
		this.setUnlocalizedName(unlocalizedName);
		this.setFrequency(frequency);
		this.setColor(color);
	}

	/** 水晶的频率，取值应当位于[0,100]之间，用于共振计算，不传入itemstack，我们认为没有subtype而且频率是一种水晶的固有值 */
	public float getFrequency() {
		return frequency;
	}

	public void setFrequency(float frequency) {
		this.frequency = frequency;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public int getColor() {
		return color;
	}

	public float probabilityOfLeftDirtClear() {
		return 0.25f;
	}

	/** 当水晶花生长 */
	public boolean onCrystalFlowerGrow(ItemStack stack, World world, Random rand, IBlockState crystalFlowerState,
			TileCrystalFlower fc, BlockPos leftDirt) {
		// 长到最大计算
		int stage = crystalFlowerState.getValue(BlockCrystalFlower.STAGE);
		if (stage < BlockCrystalFlower.MAX_STAGE) return true;
		// 生息之土重置
		if (rand.nextFloat() <= this.probabilityOfLeftDirtClear()) world.removeTileEntity(leftDirt);
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.add(TextFormatting.LIGHT_PURPLE + I18n.format("info.crystal.frequency", this.getFrequency()));
	}

}

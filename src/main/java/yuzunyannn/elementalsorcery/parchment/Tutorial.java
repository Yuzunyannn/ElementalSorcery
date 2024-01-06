package yuzunyannn.elementalsorcery.parchment;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Tutorial {

	public int cacheAction = 0;

	protected String id;
	protected ItemStack coverItem = ItemStack.EMPTY;
	protected String titleKey = "";
	protected String hoverKey = "";
	protected String describeKey = "";
	protected List<ItemStack> crafts = null;
	protected int level;
	protected int unlock;

	void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	void setLevel(int level) {
		this.level = level;
	}

	public int getLevel() {
		return level;
	}

	void setUnlock(int unlock) {
		this.unlock = unlock;
	}

	public int getUnlock() {
		return unlock;
	}

	public void setCoverItem(ItemStack coverItem) {
		this.coverItem = coverItem;
	}

	public ItemStack getCoverItem() {
		return coverItem;
	}

	@Nullable
	public List<ItemStack> getCrafts() {
		return crafts;
	}

	public void setCrafts(List<ItemStack> crafts) {
		this.crafts = crafts;
	}

	public void setTitleKey(String titleKey) {
		this.titleKey = titleKey;
	}

	public String getTitleKey() {
		return "es.tutorial.title." + titleKey;
	}

	public void setHoverKey(String hoverKey) {
		this.hoverKey = hoverKey;
	}

	public String getHoverKey() {
		return "es.tutorial.hover." + hoverKey;
	}

	public void setDescribeKey(String describeKey) {
		this.describeKey = describeKey;
	}

	public String getDescribeKey() {
		return "es.tutorial.describe." + describeKey;
	}

	@SideOnly(Side.CLIENT)
	public String getTitleDisplay() {
		return I18n.format(this.getTitleKey());
	}

	@SideOnly(Side.CLIENT)
	public String getHoverDisplay() {
		if (this.hoverKey.isEmpty()) return "";
		return I18n.format(this.getHoverKey());
	}

	@SideOnly(Side.CLIENT)
	public String getDescribeDisplay() {
		return I18n.format(this.getDescribeKey());
	}

}

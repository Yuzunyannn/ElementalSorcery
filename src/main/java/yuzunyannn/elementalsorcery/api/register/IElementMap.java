package yuzunyannn.elementalsorcery.api.register;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import yuzunyannn.elementalsorcery.api.crafting.IToElement;
import yuzunyannn.elementalsorcery.element.ElementStack;

public interface IElementMap extends IToElement {

	/** 添加一个新的IToElement句柄到图中 */
	void add(IToElement toElement);

	/** 添加一个新的stack-estacks到图中，注意estacks的顺序，决定那种元素处于主导地位 */
	void add(ItemStack stack, ElementStack... estacks);

	/** 添加一个新的item-estacks到图中 */
	void add(Item item, ElementStack... estacks);

	/** 添加一个新的block-estacks到图中 */
	void add(Block block, ElementStack... estacks);

	/** 添加一个新的stack-estacks到图中 */
	void add(ItemStack stack, int complex, ElementStack... estacks);

	/** 添加一个新的item-estacks到图中 */
	void add(Item item, int complex, ElementStack... estacks);

	/** 添加一个新的block-estacks到图中 */
	void add(Block block, int complex, ElementStack... estacks);

	/** 将方块转成元素 */
	@Nullable
	ElementStack[] toElement(Block block);

	/** 获取复杂度 */
	int complex(Block block);
}
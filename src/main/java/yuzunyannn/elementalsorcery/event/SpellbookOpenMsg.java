package yuzunyannn.elementalsorcery.event;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.capability.Spellbook;
import yuzunyannn.elementalsorcery.item.book.ItemSpellbook;

public class SpellbookOpenMsg implements ITickTask {

	public EntityLivingBase entity;
	public ItemStack stack;

	@Override
	public int onTick() {
		Spellbook book = this.stack.getCapability(Spellbook.SPELLBOOK_CAPABILITY, null);
		if (this.entity.isHandActive()) ItemSpellbook.renderOpen(book);
		else {
			if (ItemSpellbook.renderClose(book)) {
				ItemSpellbook.renderEnd(book);
				if (book.who != null) {
					this.stack.getItem().onPlayerStoppedUsing(this.stack, this.entity.getEntityWorld(), this.entity,
							this.entity.getItemInUseCount());
				}
				return ITickTask.END;
			}
		}
		return ITickTask.SUCCESS;
	}

	static public void addSpellbookOpen(EntityLivingBase entity, ItemStack stack) {
		Spellbook book = stack.getCapability(Spellbook.SPELLBOOK_CAPABILITY, null);
		if (book == null) return;
		SpellbookOpenMsg msg = new SpellbookOpenMsg();
		if (Minecraft.getMinecraft().player != entity) book.who = entity;
		msg.entity = entity;
		msg.stack = stack;
		EventClient.addTickTask(msg);
		ItemSpellbook.renderStart(book);
		// 如果是当前客户端的其他玩家
		if (book.who != null) {
			Item item = stack.getItem();
			if (item instanceof ItemSpellbook) {
				((ItemSpellbook) item).spellBegin(entity.getEntityWorld(), entity, stack, book);
			} else ElementalSorcery.logger.warn("客户端传入的spellbook的打开消息的物品有误！传入物品：" + item);
		}
	}
}

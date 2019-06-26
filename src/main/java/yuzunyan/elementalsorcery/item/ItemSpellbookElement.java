package yuzunyan.elementalsorcery.item;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyan.elementalsorcery.api.ability.IElementInventory;
import yuzunyan.elementalsorcery.api.element.ElementStack;
import yuzunyan.elementalsorcery.api.element.IElementSpell;
import yuzunyan.elementalsorcery.api.element.IElementSpell.SpellPackage;
import yuzunyan.elementalsorcery.capability.ElementInventory;
import yuzunyan.elementalsorcery.capability.Spellbook;
import yuzunyan.elementalsorcery.render.item.RenderItemSpellbook;
import yuzunyan.elementalsorcery.render.item.SpellbookRenderInfo;
import yuzunyan.elementalsorcery.util.WorldHelper;

public class ItemSpellbookElement extends ItemSpellbook {
	final int level;

	public ItemSpellbookElement() {
		this.setUnlocalizedName("spellbookElement");
		this.level = 1;
	}

	// 元素书的信息
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		Spellbook book = stack.getCapability(Spellbook.SPELLBOOK_CAPABILITY, null);
		IElementInventory inventory = book.getInventory();
		ElementStack estack = inventory.getStackInSlot(0);
		if (estack.isEmpty())
			return;
		String str = I18n.format("info.spbe.has0", I18n.format(estack.getElementUnlocalizedName()));
		tooltip.add("§6" + str);

		if (estack.getElement() instanceof IElementSpell) {
			IElementSpell es = (IElementSpell) estack.getElement();
			// 添加数量，不足的话，显示红色
			str = I18n.format("info.spbe.has1", estack.getCount());
			if (es.cost(estack, this.level) > estack.getCount())
				tooltip.add("§4" + str);
			else
				tooltip.add("§6" + str);
			// 添加能量消息，如果不够，显示成红色
			if (estack.usePower()) {
				str = I18n.format("info.spbe.has2", estack.getPower());
				if (es.lowestPower(estack, this.level) > estack.getPower())
					tooltip.add("§4" + str);
				else
					tooltip.add("§6" + str);
			}
			// 消耗
			int cost = es.cost(estack, this.level);
			if (cost > 0) {
				str = I18n.format("info.spbe.has3", es.cost(estack, this.level));
				tooltip.add("§6" + str);
			}
			// 持续的平均消耗
			if ((book.flags & IElementSpell.SPELLING) != 0) {
				float ac = es.costSpellingAverage(this.level);
				str = I18n.format("info.spbe.has4", (int) (ac * 20));
				tooltip.add("§6" + str);
			}
			// 前摇时间
			str = I18n.format("info.spbe.has5", es.cast(estack, this.level) / 20.0f);
			tooltip.add("§6" + str);
			// 效果
			str = I18n.format("info.spbe.effect");
			tooltip.add("§d" + str);
			es.addInfo(estack, worldIn, tooltip, flagIn, this.level);
		} else {
			str = I18n.format("info.spbe.has1", estack.getCount());
			tooltip.add("§6" + str);
			if (estack.usePower()) {
				str = I18n.format("info.spbe.has2", estack.getPower());
				tooltip.add("§6" + str);
			}
			str = I18n.format("info.spbe.none");
			tooltip.add("§8" + str);
		}

	}

	@SideOnly(Side.CLIENT)
	@Override
	protected void initRenderInfo(SpellbookRenderInfo info) {
		info.texture = RenderItemSpellbook.instance.TEXTURE_SPELLBOOK_ELEMENT_01;
	}

	@Override
	protected IElementInventory getInventory(ItemStack stack) {
		return new ElementInventory(1);
	}

	@Override
	public int getCast(Spellbook book) {
		return book.castTime;
	}

	@Override
	public boolean spellBegin(World world, EntityLivingBase entity, ItemStack stack, Spellbook book) {
		IElementInventory inventory = book.getInventory();
		ElementStack estack = inventory.getStackInSlot(0);
		if (estack.isEmpty())
			return false;
		if (!(estack.getElement() instanceof IElementSpell))
			return false;
		IElementSpell es = (IElementSpell) estack.getElement();
		// 获取包
		IElementSpell.SpellPackage pack = getPack(world, entity, book, es, 0);
		// 检测最少需要的量
		if (estack.getCount() < es.cost(estack, this.level))
			return false;
		// 检测最低能量
		if (estack.usePower() && estack.getPower() < es.lowestPower(estack, this.level))
			return false;
		// 准备开始
		int flags = es.spellBegin(world, entity, estack, pack);
		if (flags == IElementSpell.FAIL)
			return false;
		if (pack.power < 0)
			return false;
		book.castTime = es.cast(estack, this.level);
		book.flags = flags;
		book.obj = getPack(world, entity, book, es, 0);
		estack.shrink(es.cost(estack, this.level));
		return true;
	}

	@Override
	public void spelling(World world, EntityLivingBase entity, ItemStack stack, Spellbook book, int power) {
		if (power < this.getCast(book))
			return;
		if (world.isRemote) {
			this.giveMeParticleAboutSpelling(world, entity, stack, book, power);
			// 别人释放的时候，仅仅是效果
			if (book.who != null)
				return;
			IElementSpell.SpellPackage pack = (SpellPackage) book.obj;
			if ((book.flags & IElementSpell.NEED_ENTITY) != 0) {
				if (pack.entity != null)
					this.giveMeParticleAboutSelect(world, book, pack.entity, power);
			}
			if ((book.flags & IElementSpell.NEED_BLOCK) != 0) {
				if (pack.pos != null)
					this.giveMeParticleAboutSelect(world, book, pack.pos, power);
			}
		}
		if ((book.flags & IElementSpell.SPELLING) != 0) {
			IElementInventory inventory = book.getInventory();
			ElementStack estack = inventory.getStackInSlot(0);
			IElementSpell es = (IElementSpell) estack.getElement();
			IElementSpell.SpellPackage pack = (SpellPackage) book.obj;
			int cost = es.costSpelling(estack, pack.power, this.level);
			if (cost < 0) {
				pack = getPack(world, entity, book, es, pack.power);
				pack.normal = false;
			} else {
				if (cost <= estack.getCount()) {
					pack = getPack(world, entity, book, es, pack.power + 1);
					pack.normal = true;
					estack.shrink(cost);
				} else {
					pack = getPack(world, entity, book, es, pack.power);
					pack.normal = false;
				}
			}
			book.obj = pack;
			es.spelling(world, entity, estack, pack);
		} else {
			if (power % 10 == 0 && world.isRemote) {
				IElementInventory inventory = book.getInventory();
				ElementStack estack = inventory.getStackInSlot(0);
				IElementSpell es = (IElementSpell) estack.getElement();
				book.obj = getPack(world, entity, book, es, power);
			}
		}
	}

	@Override
	public boolean spellEnd(World world, EntityLivingBase entity, ItemStack stack, Spellbook book, int power) {
		if (book.obj == null)
			return false;
		IElementInventory inventory = book.getInventory();
		ElementStack estack = inventory.getStackInSlot(0);
		IElementSpell es = (IElementSpell) estack.getElement();
		IElementSpell.SpellPackage pack = getPack(world, entity, book, es, power);
		pack.normal = true;
		if ((book.flags & IElementSpell.SPELLING) != 0) {
			pack.power = ((IElementSpell.SpellPackage) book.obj).power;
		} else {
			pack.power = power - this.getCast(book);
		}
		if (power < this.getCast(book)) {
			pack.power = -1;
		}
		es.spellEnd(world, entity, estack, pack);
		book.obj = null;
		return false;
	}

	public IElementSpell.SpellPackage getPack(World wrold, EntityLivingBase entity, Spellbook book, IElementSpell es,
			int power) {
		IElementSpell.SpellPackage pack = new IElementSpell.SpellPackage();
		pack.book = book;
		pack.power = power;
		pack.level = this.level;
		if ((book.flags & IElementSpell.NEED_ENTITY) != 0) {
			pack.entity = WorldHelper.getLookAtEntity(wrold, entity, EntityLiving.class, 64);
		}
		if ((book.flags & IElementSpell.NEED_BLOCK) != 0) {
			RayTraceResult result = WorldHelper.getLookAtBlock(wrold, entity, 64);
			pack.pos = result == null ? null : result.getBlockPos();
			pack.face = result == null ? null : result.sideHit;
		}
		return pack;
	}
}

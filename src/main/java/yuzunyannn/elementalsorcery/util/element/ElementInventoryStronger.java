package yuzunyannn.elementalsorcery.util.element;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.capability.ElementInventory;
import yuzunyannn.elementalsorcery.container.ESGuiHandler;
import yuzunyannn.elementalsorcery.util.helper.INBTReader;
import yuzunyannn.elementalsorcery.util.helper.INBTWriter;

public class ElementInventoryStronger extends ElementInventory {

	protected byte terminal;
	protected int upperLimit;
	protected int lowerLimit;

	public ElementInventoryStronger() {
		super();
	}

	public ElementInventoryStronger(int slots) {
		super(slots);
	}

	public ElementInventoryStronger setUpperLimit(int upper) {
		if (getLowerLimit() == 0) this.upperLimit = upper;
		else this.upperLimit = upper == 0 ? 0 : Math.max(upper, getLowerLimit());
		return this;
	}

	public ElementInventoryStronger setLowerLimit(int lower) {
		if (getUpperLimit() == 0) this.lowerLimit = lower;
		else this.lowerLimit = lower == 0 ? 0 : Math.min(lower, getUpperLimit());
		return this;
	}

	public int getUpperLimit() {
		return upperLimit;
	}

	public int getLowerLimit() {
		return lowerLimit;
	}

	public boolean meetCondition(ElementStack estack) {
		if (estack.isEmpty()) return true;
		if (getUpperLimit() > 0 && estack.getPower() > getUpperLimit()) return false;
		if (getLowerLimit() > 0 && estack.getPower() < getLowerLimit()) return false;
		return true;
	}

	@Override
	public boolean insertElement(int slot, ElementStack estack, boolean simulate) {
		if (!meetCondition(estack)) return false;
		return super.insertElement(slot, estack, simulate);
	}

	@Override
	public void writeSaveData(INBTWriter writer) {
		super.writeSaveData(writer);
		if (upperLimit > 0) writer.write("upper", upperLimit);
		if (lowerLimit > 0) writer.write("lower", lowerLimit);
		if (terminal > 0) writer.write("terminal", terminal);
	}

	@Override
	public void readSaveData(INBTReader reader) {
		super.readSaveData(reader);
		upperLimit = reader.nint("upper");
		lowerLimit = reader.nint("lower");
		terminal = reader.nbyte("terminal");
	}

	@Override
	public ElementInventoryStronger assign(IElementInventory other) {
		super.assign(other);
		if (other instanceof ElementInventoryStronger) {
			ElementInventoryStronger stronger = (ElementInventoryStronger) other;
			upperLimit = stronger.upperLimit;
			lowerLimit = stronger.lowerLimit;
			terminal = stronger.terminal;
		}
		return this;
	}

	@Override
	public void addInformation(World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		TextFormatting c = TextFormatting.LIGHT_PURPLE;
		if (getTerminal() != 0) tooltip.add(TextFormatting.YELLOW + I18n.format("info.elementCube.terminal"));
		if (getUpperLimit() > 0) tooltip.add(c + I18n.format("info.elementCube.limit.upper", getUpperLimit()));
		if (getLowerLimit() > 0) tooltip.add(c + I18n.format("info.elementCube.limit.lower", getLowerLimit()));
		ElementHelper.addElementInformation(this, worldIn, tooltip, flagIn);
	}

	public boolean openTerminal(World world, BlockPos pos, EntityPlayer player) {
		if (world.isRemote) return false;
		if (getTerminal() == 0) return false;
		IElementInventory eInv = ElementHelper.getElementInventory(world.getTileEntity(pos));
		if (eInv instanceof ElementInventoryStronger) {
			player.openGui(ElementalSorcery.instance, ESGuiHandler.GUI_ELEMENT_INVENTORY_STRONGER, world, pos.getX(), pos.getY(), pos.getZ());
			return true;
		}
		return false;
	}

	public void setTerminal(byte terminal) {
		this.terminal = terminal;
	}

	public byte getTerminal() {
		return terminal;
	}

}

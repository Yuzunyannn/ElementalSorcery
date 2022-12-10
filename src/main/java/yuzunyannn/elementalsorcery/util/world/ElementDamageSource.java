package yuzunyannn.elementalsorcery.util.world;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.element.Element;
import yuzunyannn.elementalsorcery.api.element.ElementStack;

public class ElementDamageSource extends EntityDamageSource {

	protected final Entity immediateEntity;

	protected ElementStack eStack = ElementStack.EMPTY;

	public ElementDamageSource(ElementStack eStack, @Nullable Entity source, @Nullable Entity immediateSource) {
		super("element", source);
		this.immediateEntity = immediateSource;
		this.eStack = eStack;
	}

	public ElementDamageSource(ElementStack eStack, @Nullable Entity source) {
		this(eStack, source, null);
	}

	public ElementStack getElementStack() {
		return eStack;
	}

	public Element getElement() {
		return eStack.getElement();
	}

	public boolean isVoidDamage() {
		return eStack.getElement() == ESObjects.ELEMENTS.VOID;
	}

	@Override
	public Entity getImmediateSource() {
		return immediateEntity;
	}

	@Override
	public ITextComponent getDeathMessage(EntityLivingBase dead) {
		Entity attacker = this.getTrueSource();
		if (eStack.isMagic()) {
			if (attacker == null) return new TextComponentTranslation("eath.es.killed.by.magic", dead.getDisplayName());
			if (attacker == dead)
				return new TextComponentTranslation("eath.es.killed.by.self.magic", dead.getDisplayName());
			return new TextComponentTranslation("eath.es.killed.by.entity.magic", attacker.getDisplayName(),
					dead.getDisplayName());
		}
		if (attacker == null) return new TextComponentTranslation("eath.es.killed.by.element", dead.getDisplayName(),
				eStack.getTextComponent());
		if (attacker == dead) return new TextComponentTranslation("eath.es.killed.by.self.element",
				dead.getDisplayName(), eStack.getTextComponent());
		return new TextComponentTranslation("eath.es.killed.by.entity.element", attacker.getDisplayName(),
				eStack.getTextComponent(), dead.getDisplayName());
	}

}

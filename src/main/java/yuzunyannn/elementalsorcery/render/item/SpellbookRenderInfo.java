package yuzunyannn.elementalsorcery.render.item;

import yuzunyannn.elementalsorcery.api.util.client.TextureBinder;

public class SpellbookRenderInfo {

	// 作为判定instance是否存在的标识
	static public RenderItemSpellbook renderInstance = null;

	public byte scaleLevel = 1;
	public TextureBinder texture = renderInstance == null ? null : renderInstance.TEXTURE_SPELLBOOK;
	public int tickCount = 0;
	public float bookRotation = 0;
	public float bookRotationPrev = 0;
	public float pageFlipPrev = 0;
	public float pageFlip = 0;
	public float bookSpreadPrev = 0;
	public float bookSpread = 0;
}

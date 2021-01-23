package yuzunyannn.elementalsorcery.api;

import net.minecraftforge.registries.IForgeRegistry;
import yuzunyannn.elementalsorcery.api.crafting.IRecipe;
import yuzunyannn.elementalsorcery.crafting.RecipeManagement;
import yuzunyannn.elementalsorcery.crafting.element.ElementMap;
import yuzunyannn.elementalsorcery.element.Element;
import yuzunyannn.elementalsorcery.grimoire.mantra.Mantra;

public class ESRegister {

	public final static IForgeRegistry<Element> ELEMENT = Element.REGISTRY;
	public final static IElementMap ELEMENT_MAP = ElementMap.instance;
	public final static IForgeRegistry<IRecipe> RECIPE = RecipeManagement.instance;
	public final static IForgeRegistry<Mantra> MANTRA = Mantra.REGISTRY;

}

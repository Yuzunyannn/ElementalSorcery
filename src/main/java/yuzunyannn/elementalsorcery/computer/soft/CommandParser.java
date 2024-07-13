package yuzunyannn.elementalsorcery.computer.soft;

import java.util.ArrayList;
import java.util.List;

public class CommandParser {

	public static final int TYPE_STR = 0;
	public static final int TYPE_LIST = 1;
	public static final int TYPE_ORIENT = 2;
	public static final int TYPE_REF = 3;

	public final String cmd;
	private int i, j;
	private Element root;

	public static class Element {
		final int type;
		private String str;
		private int i, j;
		private int sIndex;
		private int aIndex;
		private List<Element> sub;
		private Element parent;

		protected Element(int type) {
			this.type = type;
			if (type == TYPE_LIST) sub = new ArrayList<>();
			else if (type == TYPE_STR) str = "";
		}

		protected Element(String str, int i, int j) {
			this.type = TYPE_STR;
			this.i = i;
			this.j = j;
			this.str = str;
		}

		protected Element(int type, String str, int i, int j) {
			this.type = type;
			this.i = i;
			this.j = j;
			this.str = str;
		}

		public int getEndIndex() {
			return j;
		}

		public int getStartIndex() {
			return i;
		}

		public boolean isCommand() {
			return type == TYPE_LIST;
		}

		public boolean isInnerCommand() {
			return type == TYPE_LIST && "inner".equals(str);
		}

		public boolean isOrient() {
			return type == TYPE_ORIENT;
		}

		public boolean isArgs() {
			return type != TYPE_ORIENT;
		}

		public boolean isRef() {
			return type == TYPE_REF;
		}

		public int getType() {
			return type;
		}

		public String getString() {
			return str;
		}

		public int getIndex() {
			return sIndex;
		}

		public int getArgsIndex() {
			return aIndex;
		}

		public List<Element> getElements() {
			return sub;
		}

		protected void add(Element element) {
			element.parent = this;
			element.sIndex = sub.size();
			sub.add(element);
			if (!element.isArgs()) element.aIndex = -1;
			else {
				Element finded = null;
				for (Element e : sub) {
					if (e.isArgs()) {
						finded = e;
						break;
					}
				}
				if (finded != null) element.aIndex = element.sIndex - finded.sIndex;
				else element.aIndex = 0;
			}
			this.i = Math.min(element.i, this.i);
			this.j = Math.max(element.j, this.j);
		}

		public Element getParent() {
			return parent;
		}

		@Override
		public String toString() {
			if (type == TYPE_LIST) return sub.toString();
			else if (type == TYPE_ORIENT) return "|" + str + "|";
			else if (type == TYPE_REF) return "$" + str;
			return str;
		}
	}

	@SuppressWarnings("serial")
	private class End extends Throwable {
	}

	public CommandParser(String cmd) {
		this.cmd = cmd;
		this.parser();
	}

	public Element getRoot() {
		return root;
	}

	public Element findElement(int cursor) {
		Element currElement = this.root;
		while (currElement.isCommand()) {
			Element lastFront = null;
			Element _curr = currElement;
			for (Element element : currElement.getElements()) {
				boolean checkI = element.isCommand() ? element.i < cursor : element.i <= cursor;
				if (checkI && cursor <= element.j) {
					currElement = element;
					lastFront = null;
					break;
				}
				if (element.j <= cursor) lastFront = element;
			}
			if (lastFront != null) {
				currElement = lastFront;
				break;
			}
			if (_curr == currElement) {
				if (currElement.i < cursor || currElement == this.root) {
					List<Element> elements = currElement.getElements();
					if (!elements.isEmpty()) currElement = elements.get(0);
				}
				break;
			}
		}
		return currElement;
	}

	private void parser() {
		this.root = nextListElement("root", (char) 0);
	}

	private boolean isSpace(char ch) {
		return ch == ' ' || ch == '\t' || ch == '\n';
	}

	private boolean isSpecalMark(char ch) {
		return ch == '<' || ch == '>' || ch == '(' || ch == ')';
	}

	private void trim() throws End {
		if (i >= cmd.length()) throw new End();
		char ch = cmd.charAt(i);
		if (!isSpace(ch)) return;
		for (; i < cmd.length(); i++) {
			ch = cmd.charAt(i);
			if (isSpace(ch)) continue;
			break;
		}
		j = i;
		if (i >= cmd.length()) throw new End();
	}

	private Element nextElement() throws End {
		trim();
		char ch = cmd.charAt(i);
		if (ch == '$') return nextRefString();
		else if (ch == '"') return nextLongString(TYPE_STR, '"');
		else if (ch == '\'') return nextLongString(TYPE_STR, '\'');
		else if (ch == '|') return nextLongString(TYPE_ORIENT, '|');
		else if (ch == '<') return nextListElement("inner", '>');
		else if (ch == '(') return nextListElement("sub", ')');
		return nextCmdString();
	}

	private Element nextListElement(String mode, char endMark) {
		Element element = new Element(TYPE_LIST);
		element.i = i;
		element.str = mode;
		if (endMark != 0) i = ++j;
		try {
			while (i < cmd.length()) {
				Element e = nextElement();
				element.add(e);
				trim();
				if (endMark != 0 && cmd.charAt(i) == endMark) break;
			}
		} catch (End e) {}
		element.j = j;
		i = ++j;
		return element;
	}

	private Element nextCmdString() throws End {
		for (j++; j < cmd.length(); j++) {
			char ch = cmd.charAt(j);
			if (isSpace(ch) || isSpecalMark(ch)) {
				Element element = new Element(cmd.substring(i, j), i, j - 1);
				i = j;
				return element;
			}
		}
		Element element = new Element(cmd.substring(i, j), i, j);
		i = j;
		return element;
	}

	private Element nextRefString() throws End {
		for (j++; j < cmd.length(); j++) {
			char ch = cmd.charAt(j);
			if (isSpace(ch)) {
				Element element = new Element(TYPE_REF, cmd.substring(i + 1, j), i, j - 1);
				i = j;
				return element;
			}
		}
		Element element = new Element(TYPE_REF, cmd.substring(i + 1, j), i, j - 1);
		i = j;
		return element;
	}

	private Element nextLongString(int type, char endMark) throws End {
		for (j++; j < cmd.length(); j++) {
			char ch = cmd.charAt(j);
			if (ch == endMark) {
				Element element = new Element(type, cmd.substring(i + 1, j), i, j);
				i = ++j;
				return element;
			}
		}
		Element element = new Element(type, cmd.substring(i + 1, j), i, j);
		i = j;
		return element;
	}

	@Override
	public String toString() {
		return root.toString();
	}
}

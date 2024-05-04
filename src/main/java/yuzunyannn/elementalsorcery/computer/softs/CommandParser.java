package yuzunyannn.elementalsorcery.computer.softs;

import java.util.ArrayList;
import java.util.List;

public class CommandParser {

	public static final int TYPE_STR = 0;
	public static final int TYPE_LIST = 1;
	public static final int TYPE_LINK = 2;
	public static final int TYPE_REF = 3;

	public final String cmd;
	private int i, j;
	private final Element root = new Element(TYPE_LIST);

	public static class Element {
		final int type;
		private String str;
		private int i, j;
		private int sIndex;
		private List<Element> sub;

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

		public boolean isCommand() {
			return type == TYPE_LIST;
		}

		public boolean isLink() {
			return type == TYPE_LINK;
		}

		public int getType() {
			return type;
		}

		public String getString() {
			return str;
		}

		public int getArgsIndex() {
			return sIndex;
		}

		public List<Element> getElements() {
			return sub;
		}

		protected void add(Element element) {
			sub.add(element);
			this.i = Math.min(element.i, this.i);
			this.j = Math.max(element.j, this.j);
		}

		@Override
		public String toString() {
			if (type == TYPE_LIST) return sub.toString();
			else if (type == TYPE_LINK) return str + "|";
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
		return null;
	}

	private void parser() {
		try {
			_parser();
		} catch (End e) {}
	}

	private void _parser() throws End {
		while (true) {
			Element element = nextElement();
			element.sIndex = root.sub.size();
			root.add(element);
			if (i >= cmd.length()) throw new End();
		}
	}

	private boolean isSpace(char ch) {
		return ch == ' ' || ch == '\t' || ch == '\n';
	}

	private void trim() throws End {
		for (; i < cmd.length(); i++) {
			char ch = cmd.charAt(i);
			if (isSpace(ch)) continue;
			break;
		}
		j = i;
		if (i >= cmd.length()) throw new End();
	}

	private Element nextElement() throws End {
		trim();
		char ch = cmd.charAt(i);
		if (ch == '<') return nextAngleVal();
		else if (ch == '"') return nextLongString();
		else if (ch == '(') return nextSubElement();
		else if (ch == '$') return nextRefString();
		return nextCmdString();
	}

	private Element nextSubElement() throws End {
		i = ++j;
		Element element = new Element(TYPE_LIST);
		try {
			while (i < cmd.length()) {
				Element e = nextElement();
				e.sIndex = root.sub.size();
				element.add(e);
				trim();
				if (cmd.charAt(i) == ')') break;
			}
		} catch (End e) {}
		i = ++j;
		return element;
	}

	private Element nextCmdString() throws End {
		for (j++; j < cmd.length(); j++) {
			char ch = cmd.charAt(j);
			if (ch == '|') {
				Element element = new Element(TYPE_LINK, cmd.substring(i, j), i, j - 1);
				i = ++j;
				return element;
			} else if (isSpace(ch)) {
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

	private Element nextAngleVal() throws End {
		int count = 0;
		for (; j < cmd.length(); j++) {
			char ch = cmd.charAt(j);
			if (ch == '>') {
				if (--count == 0) {
					Element element = new Element(cmd.substring(i, j + 1), i, j);
					i = ++j;
					return element;
				}
			} else if (ch == '<') count++;
		}
		Element element = new Element(cmd.substring(i, j), i, j);
		i = j;
		return element;
	}

	private Element nextLongString() throws End {
		for (j++; j < cmd.length(); j++) {
			char ch = cmd.charAt(j);
			if (ch == '"') {
				Element element = new Element(cmd.substring(i + 1, j), i, j);
				i = ++j;
				return element;
			}
		}
		Element element = new Element(cmd.substring(i, j), i, j);
		i = j;
		return element;
	}

	@Override
	public String toString() {
		return root.toString();
	}
}

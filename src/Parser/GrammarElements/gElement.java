package Parser.GrammarElements;

import Parser.GrammarType;

import java.util.ArrayList;

public abstract class gElement {
    protected GrammarType grammarType;
    protected ArrayList<gElement> elements;
    public gElement(GrammarType grammarType) {
        this.grammarType = grammarType;
        elements = new ArrayList<>();
    }

    public void addElement(gElement element) {
        elements.add(element);
    }

    public gElement get(int index) {
        return elements.get(index);
    }

    public String toString() {
        return String.format("<%s>", grammarType.toString());
    }
}

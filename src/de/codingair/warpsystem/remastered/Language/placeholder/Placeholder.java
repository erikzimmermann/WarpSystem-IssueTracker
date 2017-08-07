package de.codingair.warpsystem.remastered.Language.placeholder;

public class Placeholder {
    private String replace;
    private String data;

    public Placeholder(String replace, String data) {
        this.replace = replace;
        this.data = data;
    }

    public String getReplace() {
        return replace;
    }

    public String getData() {
        return data;
    }
}

package de.codingair.warpsystem.spigot.base.utils.options;

public class Option<E> {
    private String path;
    private E value;
    private E def;

    public Option(String path) {
        this.path = path;
    }

    public Option(String path, E def) {
        this.path = path;
        this.def = def;
    }

    public Option(String path, E value, E def) {
        this.path = path;
        this.value = value;
        this.def = def;
    }

    public String getPath() {
        return path;
    }

    public E getValue() {
        return value;
    }

    public void setValue(E value) {
        this.value = value;
    }

    public Option<E> clone() {
        return new Option<>(path, value, def);
    }

    public E getDefault() {
        return def;
    }
}

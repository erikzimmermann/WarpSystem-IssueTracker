package de.codingair.warpsystem.spigot.base.utils.options;

import java.util.Objects;

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

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Option) {
            Option o = (Option) obj;
            return o.getPath().equals(path) && Objects.equals(getValue(), o.getValue()) && Objects.equals(getDefault(), o.getDefault());
        } else return false;
    }
}

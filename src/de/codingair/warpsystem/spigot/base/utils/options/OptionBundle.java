package de.codingair.warpsystem.spigot.base.utils.options;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OptionBundle {
    private List<Options> options;

    public OptionBundle(List<Options> options) {
        this.options = options;
    }

    public OptionBundle(Options... options) {
        this.options = Arrays.asList(options);
    }

    public void apply(OptionBundle bundle) {
        for(Options option : bundle.options) {
            Options o = getOptions(option.getClass());
            if(o != null) {
                o.apply(option);
            }
        }
    }

    public OptionBundle clone() {
        List<Options> options = new ArrayList<>();

        for(Options option : this.options) {
            options.add(option.clone());
        }

        return new OptionBundle(options);
    }

    public void write() {
        for(Options option : this.options) {
            option.reloadFile(false);
            option.write();
        }
    }

    public void read() {
        for(Options option : this.options) {
            option.read();
        }
    }

    public <E extends Options> E getOptions(Class<? extends E> clazz) {
        for(Options option : this.options) {
            if(option.getClass().equals(clazz)) return (E) option;
        }

        return null;
    }

    public List<Options> getOptions() {
        return options;
    }
}

package de.codingair.warpsystem.spigot.features.warps.guis.utils;

import de.codingair.codingapi.tools.items.ItemBuilder;
import org.bukkit.inventory.ItemStack;

public enum Head {
    RED_ARROW_UP("2d9287616343d833e9e7317159caa2cb3e59745113962c1379052ce478884fa"),
    RED_ARROW_RIGHT("fcfe8845a8d5e635fb87728ccc93895d42b4fc2e6a53f1ba78c845225822"),
    RED_ARROW_LEFT("f84f597131bbe25dc058af888cb29831f79599bc67c95c802925ce4afba332fc"),
    RED_ARROW_DOWN("a3852bf616f31ed67c37de4b0baa2c5f8d8fca82e72dbcafcba66956a81c4"),
    RED_PLUS("ac731c3c723f67d2cfb1a1192b947086fba32aea472d347a5ed5d7642f73b"),
    CYAN_ARROW_RIGHT("6ff55f1b32c3435ac1ab3e5e535c50b527285da716e54fe701c9b59352afc1c"),
    CYAN_ARROW_LEFT("6768edc28853c4244dbc6eeb63bd49ed568ca22a852a0a578b2f2f9fabe70"),
    CYAN_ARROW_UP("4b221cb9607c8a9bf02fef5d7614e3eb169cc219bf4250fd5715d5d2d6045f7"),
    CYAN_ARROW_DOWN("d8aab6d9a0bdb07c135c97862e4edf3631943851efc545463d68e793ab45a3d3"),
    CYAN_PLUS("dd8b7173d841f2563ec108888b0f797917efc18be27861f0a6761aa3ed91ce"),
    GRAY_ARROW_RIGHT("f32ca66056b72863e98f7f32bd7d94c7a0d796af691c9ac3a9136331352288f9"),
    GRAY_ARROW_LEFT("86971dd881dbaf4fd6bcaa93614493c612f869641ed59d1c9363a3666a5fa6"),
    GRAY_ARROW_UP("3f46abad924b22372bc966a6d517d2f1b8b57fdd262b4e04f48352e683fff92"),
    GRAY_ARROW_DOWN("be9ae7a4be65fcbaee65181389a2f7d47e2e326db59ea3eb789a92c85ea46"),
    GRAY_PLUS("10c97e4b68aaaae8472e341b1d872b93b36d4eb6ea89ecec26a66e6c4e178");

    private String data;

    Head(String data) {
        this.data = data;
    }

    public ItemStack getItem() {
        return getItemBuilder().getItem();
    }

    public ItemBuilder getItemBuilder() {
        return new ItemBuilder(data);
    }
}

package de.codingair.warpsystem.spigot.base.commands;

import de.codingair.codingapi.API;
import de.codingair.codingapi.player.chat.ChatButton;
import de.codingair.codingapi.player.chat.SimpleMessage;
import de.codingair.codingapi.player.gui.anvil.AnvilClickEvent;
import de.codingair.codingapi.player.gui.anvil.AnvilCloseEvent;
import de.codingair.codingapi.player.gui.anvil.AnvilGUI;
import de.codingair.codingapi.player.gui.anvil.AnvilListener;
import de.codingair.codingapi.player.gui.hotbar.HotbarGUI;
import de.codingair.codingapi.server.commands.builder.BaseComponent;
import de.codingair.codingapi.server.commands.builder.CommandComponent;
import de.codingair.codingapi.server.commands.builder.special.MultiCommandComponent;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.codingapi.tools.time.TimeList;
import de.codingair.warpsystem.spigot.api.WSCommandBuilder;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.setupassistant.utils.NavigationCommand;
import de.codingair.warpsystem.spigot.base.setupassistant.utils.SetupAssistant;
import de.codingair.warpsystem.spigot.features.animations.AnimationManager;
import de.codingair.warpsystem.spigot.features.animations.guis.editor.*;
import de.codingair.warpsystem.spigot.features.animations.utils.Animation;
import de.codingair.warpsystem.spigot.features.simplewarps.SimpleWarp;
import de.codingair.warpsystem.spigot.features.simplewarps.managers.SimpleWarpManager;
import de.codingair.warpsystem.spigot.features.warps.importfilter.ImportType;
import de.codingair.warpsystem.spigot.features.warps.importfilter.Result;
import de.codingair.warpsystem.spigot.features.warps.managers.IconManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CWarpSystem extends WSCommandBuilder {
    public CWarpSystem() {
        super("WarpSystem", new BaseComponent(WarpSystem.PERMISSION_MODIFY) {
            @Override
            public void noPermission(CommandSender sender, String label, CommandComponent child) {
                Player p = (Player) sender;
                sendInfoMessage(p);
            }

            @Override
            public void onlyFor(boolean player, CommandSender sender, String label, CommandComponent child) {
                if(player) {
                    sender.sendMessage(Lang.getPrefix() + Lang.get("Only_For_Players"));
                }
            }

            @Override
            public void unknownSubCommand(CommandSender sender, String label, String[] args) {
                sender.sendMessage(Lang.getPrefix() + WarpSystem.opt().cmdSug() + Lang.get("Use") + ": /" + label + " " + WarpSystem.opt().cmdArg() + "<info, reload, import, news, report, options, animations>");
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                sender.sendMessage(Lang.getPrefix() + WarpSystem.opt().cmdSug() + Lang.get("Use") + ": /" + label + " " + WarpSystem.opt().cmdArg() + "<info, reload, import, news, report, options, animations>");
                return false;
            }
        });

        getBaseComponent().addChild(new CommandComponent("setupassistant") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                SetupAssistant a = WarpSystem.getInstance().getSetupAssistantManager().getAssistant();
                if(a != null) {
                    sender.sendMessage(Lang.getPrefix() + "§7The setup assistant is §calready used §7by §e" + a.getPlayer().getName() + "§7.");
                    return false;
                }

                WarpSystem.getInstance().getSetupAssistantManager().startAssistant((Player) sender, true);
                return false;
            }
        }.setOnlyPlayers(true).addChild(new NavigationCommand()));

        getBaseComponent().addChild(new CommandComponent("shortcut") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                sender.sendMessage(Lang.getPrefix() + WarpSystem.opt().cmdSug() + Lang.get("Use") + ": /shortcuts");
                return false;
            }
        });

        getBaseComponent().addChild(new CommandComponent("animations") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                sender.sendMessage(Lang.getPrefix() + WarpSystem.opt().cmdSug() + Lang.get("Use") + ": /" + label + " animations " + WarpSystem.opt().cmdArg() + "<activate, add, edit, remove>");
                return false;
            }
        });

        getComponent("animations").addChild(new CommandComponent("activate") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                sender.sendMessage(Lang.getPrefix() + WarpSystem.opt().cmdSug() + Lang.get("Use") + ": /" + label + " animations activate " + WarpSystem.opt().cmdArg() + "<name>");
                return false;
            }
        });

        getComponent("animations", "activate").addChild(new MultiCommandComponent() {
            @Override
            public void addArguments(CommandSender sender, String[] args, List<String> suggestions) {
                for(Animation animation : AnimationManager.getInstance().getAnimationList()) {
                    suggestions.add(animation.getName());
                }
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String argument, String[] args) {
                Animation animation = AnimationManager.getInstance().getAnimation(argument);

                if(animation == null) {
                    sender.sendMessage(Lang.getPrefix() + Lang.get("Animation_does_not_exist"));
                    return false;
                }

                AnimationManager.getInstance().setActive(animation);
                sender.sendMessage(Lang.getPrefix() + "§a" + Lang.get("Changes_have_been_saved"));
                return false;
            }
        });

        getComponent("animations").addChild(new CommandComponent("add") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                sender.sendMessage(Lang.getPrefix() + WarpSystem.opt().cmdSug() + Lang.get("Use") + ": /" + label + " animations add " + WarpSystem.opt().cmdArg() + "<name>");
                return false;
            }
        }.setOnlyPlayers(true));

        getComponent("animations", "add").addChild(new MultiCommandComponent() {
            @Override
            public void addArguments(CommandSender sender, String[] args, List<String> suggestions) {
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String argument, String[] args) {
                if(AnimationManager.getInstance().existsAnimation(argument)) {
                    sender.sendMessage(Lang.getPrefix() + Lang.get("Name_Already_Exists"));
                    return false;
                }

                HotbarGUI h = API.getRemovable((Player) sender, HotbarGUI.class);
                if(h != null) {
                    h.destroy();
                    Menu m = null;

                    if(h instanceof AnimationPart) m = ((AnimationPart) h).getMenuGUI();
                    if(h instanceof BuffPart) m = ((BuffPart) h).getMenuGUI();
                    if(h instanceof Buffs) m = ((Buffs) h).getMenuGUI();
                    if(h instanceof Particles) m = ((Particles) h).getMenuGUI();
                    if(h instanceof Sounds) m = ((Sounds) h).getMenuGUI();
                    if(h instanceof Menu) m = (Menu) h;

                    m.getAnimPlayer().setLoop(false);
                    m.getAnimPlayer().setRunning(false);
                }

                new Menu((Player) sender, argument).open(true);
                return false;
            }
        }.setOnlyPlayers(true));

        getComponent("animations").addChild(new CommandComponent("edit") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                sender.sendMessage(Lang.getPrefix() + WarpSystem.opt().cmdSug() + Lang.get("Use") + ": /" + label + " animations edit " + WarpSystem.opt().cmdArg() + "<name>");
                return false;
            }
        }.setOnlyPlayers(true));

        getComponent("animations", "edit").addChild(new MultiCommandComponent() {
            @Override
            public void addArguments(CommandSender sender, String[] args, List<String> suggestions) {
                for(Animation animation : AnimationManager.getInstance().getAnimationList()) {
                    suggestions.add(animation.getName());
                }
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String argument, String[] args) {
                Animation animation = AnimationManager.getInstance().getAnimation(argument);

                if(animation == null) {
                    sender.sendMessage(Lang.getPrefix() + Lang.get("Animation_does_not_exist"));
                    return false;
                }

                HotbarGUI h = API.getRemovable((Player) sender, HotbarGUI.class);
                if(h != null) {
                    h.destroy();
                    Menu m = null;

                    if(h instanceof AnimationPart) m = ((AnimationPart) h).getMenuGUI();
                    if(h instanceof BuffPart) m = ((BuffPart) h).getMenuGUI();
                    if(h instanceof Buffs) m = ((Buffs) h).getMenuGUI();
                    if(h instanceof Particles) m = ((Particles) h).getMenuGUI();
                    if(h instanceof Sounds) m = ((Sounds) h).getMenuGUI();
                    if(h instanceof Menu) m = (Menu) h;

                    m.getAnimPlayer().setLoop(false);
                    m.getAnimPlayer().setRunning(false);
                }

                new Menu((Player) sender, animation).open(true);
                return false;
            }
        }.setOnlyPlayers(true));

        getComponent("animations").addChild(new CommandComponent("remove") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                sender.sendMessage(Lang.getPrefix() + WarpSystem.opt().cmdSug() + Lang.get("Use") + ": /" + label + " animations remove " + WarpSystem.opt().cmdArg() + "<name>");
                return false;
            }
        });

        getComponent("animations", "remove").addChild(new MultiCommandComponent() {
            @Override
            public void addArguments(CommandSender sender, String[] args, List<String> suggestions) {
                for(Animation animation : AnimationManager.getInstance().getAnimationList()) {
                    suggestions.add(animation.getName());
                }
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String argument, String[] args) {
                Animation animation = AnimationManager.getInstance().getAnimation(argument);

                if(animation == null) {
                    sender.sendMessage(Lang.getPrefix() + Lang.get("Animation_does_not_exist"));
                    return false;
                }

                AnimationManager.getInstance().removeAnimation(animation);
                sender.sendMessage(Lang.getPrefix() + Lang.get("Animation_was_removed").replace("%ANIMATION%", animation.getName()));
                return false;
            }
        });

        getBaseComponent().addChild(new CommandComponent("options") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                getComponent("setupassistant").runCommand(sender, label, args);
                return false;
            }
        }.setOnlyPlayers(true));

        getBaseComponent().addChild(new CommandComponent("info") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                sendInfoMessage(sender);
                return false;
            }
        });

        getBaseComponent().addChild(new CommandComponent("news") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                if(WarpSystem.getInstance().getUpdateNotifier().getDownload() == null) {
                    sender.sendMessage(Lang.getPrefix() + "§cFetching data... Please try again.");
                    return false;
                }

                TextComponent tc0 = new TextComponent(Lang.getPrefix() + "§7Click »");
                TextComponent click = new TextComponent("§chere");
                TextComponent tc1 = new TextComponent("§7« to read all new stuff!");

                click.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, WarpSystem.getInstance().getUpdateNotifier().getDownload()));
                click.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new net.md_5.bungee.api.chat.BaseComponent[] {new TextComponent("§7»Click«")}));

                tc0.addExtra(click);
                tc0.addExtra(tc1);

                ((Player) sender).spigot().sendMessage(tc0);
                return false;
            }
        });

        getBaseComponent().addChild(new CommandComponent("report") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                sender.sendMessage(Lang.getPrefix() + WarpSystem.opt().cmdSug() + Lang.get("Use") + ": /" + label + " report " + WarpSystem.opt().cmdArg() + "<GitHub, Spigot-Forum, Direct>");
                return false;
            }
        });

        getComponent("report").addChild(new MultiCommandComponent() {
            @Override
            public void addArguments(CommandSender sender, String[] args, List<String> suggestions) {
                suggestions.add("GitHub");
                suggestions.add("Spigot-Forum");
                suggestions.add("Direct");
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String argument, String[] args) {
                switch(argument.toLowerCase()) {
                    case "github":
                        TextComponent base = new TextComponent(Lang.getPrefix() + "§7Click »");
                        TextComponent link = new TextComponent("§chere");
                        TextComponent base1 = new TextComponent("§7« to report the bug to §cGitHub§7.");

                        link.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/CodingAir/WarpSystem-IssueTracker/issues/new"));
                        link.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new net.md_5.bungee.api.chat.BaseComponent[] {new TextComponent("§7»Click«")}));

                        base.addExtra(link);
                        base.addExtra(base1);

                        ((Player) sender).spigot().sendMessage(base);
                        break;

                    case "spigot-forum":
                        base = new TextComponent(Lang.getPrefix() + "§7Click »");
                        link = new TextComponent("§chere");
                        base1 = new TextComponent("§7« to report the bug to the §6SpigotMc-Forum§7.");

                        link.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org/threads/Premium-WarpSystem.369986/page-9999"));
                        link.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new net.md_5.bungee.api.chat.BaseComponent[] {new TextComponent("§7»Click«")}));

                        base.addExtra(link);
                        base.addExtra(base1);

                        ((Player) sender).spigot().sendMessage(base);
                        break;

                    case "direct":
                        base = new TextComponent(Lang.getPrefix() + "§7Click »");
                        link = new TextComponent("§chere");
                        base1 = new TextComponent("§7« to report the bug to §bme §8(§bCodingAir§8)§7.");

                        link.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org/conversations/add?to=CodingAir&title=WarpSystem-Bug%20(v" + WarpSystem.getInstance().getDescription().getVersion() + ",%20" + System.getProperty("os.name").replace(" ", "%20") + ")"));
                        link.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new net.md_5.bungee.api.chat.BaseComponent[] {new TextComponent("§7»Click«")}));

                        base.addExtra(link);
                        base.addExtra(base1);

                        ((Player) sender).spigot().sendMessage(base);
                        break;

                    default:
                        sender.sendMessage(Lang.getPrefix() + WarpSystem.opt().cmdSug() + Lang.get("Use") + ": /" + label + " report <GitHub, Spigot-Forum>");
                        break;
                }
                return false;
            }
        });

        getBaseComponent().addChild(new CommandComponent("reload") {
            TimeList<CommandSender> confirm = new TimeList<>();

            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                if(confirm.contains(sender)) {
                    try {
                        sender.sendMessage(Lang.getPrefix() + Lang.get("Plugin_Reloading"));
                        WarpSystem.getInstance().reload(false);
                        sender.sendMessage(Lang.getPrefix() + Lang.get("Success_Plugin_Reloaded"));
                    } catch(Throwable ex) {
                        if(ex instanceof NoClassDefFoundError) return false;
                        ex.printStackTrace();
                    }
                } else {
                    sender.sendMessage(Lang.getPrefix() + WarpSystem.opt().cmdSug() + Lang.get("Unsaved_Changes"));
                    confirm.add(sender, 10);
                }
                return false;
            }
        });

        getComponent("reload").addChild(new MultiCommandComponent() {
            @Override
            public void addArguments(CommandSender sender, String[] args, List<String> suggestions) {
                suggestions.add("true");
                suggestions.add("false");
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String argument, String[] args) {
                if(argument == null || (!argument.equalsIgnoreCase("true") && !argument.equalsIgnoreCase("false"))) {
                    sender.sendMessage(Lang.getPrefix() + WarpSystem.opt().cmdSug() + Lang.get("Use") + ": /" + label + " reload " + WarpSystem.opt().cmdArg() + "<true, false>");
                    return false;
                }

                boolean save = Boolean.parseBoolean(argument);
                sender.sendMessage(Lang.getPrefix() + Lang.get("Plugin_Reloading"));
                WarpSystem.getInstance().reload(save);
                if(save)
                    sender.sendMessage(Lang.getPrefix() + Lang.get("Success_Plugin_Reloaded"));
                else
                    sender.sendMessage(Lang.getPrefix() + Lang.get("Success_Plugin_Reloaded_Without_Saving"));
                return false;
            }
        });

        getBaseComponent().addChild(new CommandComponent("import") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                sender.sendMessage(Lang.getPrefix() + WarpSystem.opt().cmdSug() + Lang.get("Use") + ": /" + label + " import " + WarpSystem.opt().cmdArg() + "<CategoryWarps, Essentials> [Warp]");
                return false;
            }
        });

        getComponent("import").addChild(new MultiCommandComponent() {
            @Override
            public void addArguments(CommandSender sender, String[] args, List<String> suggestions) {
                suggestions.add("Essentials");
                suggestions.add("CategoryWarps");
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String argument, String[] args) {
                ImportType type = null;

                switch(argument.toLowerCase()) {
                    case "categorywarps": {
                        type = ImportType.CATEGORY_WARPS;
                        break;
                    }

                    case "essentials": {
                        type = ImportType.ESSENTIALS;
                        break;
                    }
                }

                if(type == null) {
                    sender.sendMessage(Lang.getPrefix() + WarpSystem.opt().cmdSug() + Lang.get("Use") + ": /" + label + " import " + WarpSystem.opt().cmdArg() + "<CategoryWarps, Essentials> [Warp]");
                } else {
                    sender.sendMessage(Lang.getPrefix() + Lang.get("Import_Start"));

                    Result result;
                    int size = (IconManager.getInstance() == null ? 0 : IconManager.getInstance().getIcons().size()) + (SimpleWarpManager.getInstance() == null ? 0 : SimpleWarpManager.getInstance().getWarps().size());
                    if((result = type.importData()).isFinished()) {
                        int amount = (IconManager.getInstance() == null ? 0 : IconManager.getInstance().getIcons().size()) + (SimpleWarpManager.getInstance() == null ? 0 : SimpleWarpManager.getInstance().getWarps().size()) - size;
                        sender.sendMessage(Lang.getPrefix() + Lang.get("Import_Finish").replace("%AMOUNT%", amount + ""));
                    } else {
                        sender.sendMessage(Lang.getPrefix() + Lang.get("Import_Finish_With_Errors") + " §8[" + result.name() + "]");
                    }
                }
                return false;
            }
        });

        getComponent("import", null).addChild(new MultiCommandComponent() {
            @Override
            public void addArguments(CommandSender sender, String[] args, List<String> suggestions) {
                switch(args[1].toLowerCase()) {
                    case "essentials": {
                        List<String> l = ImportType.ESSENTIALS.loadWarpNames();
                        suggestions.addAll(l);
                        l.clear();
                        break;
                    }
                }
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String argument, String[] args) {
                switch(args[1].toLowerCase()) {
                    case "essentials": {
                        SimpleWarp warp = ImportType.ESSENTIALS.loadWarp(argument);

                        if(warp == null) {
                            sender.sendMessage(Lang.getPrefix() + Lang.get("Import_Could_Not_Import_Warp"));
                            return false;
                        }

                        if(IconManager.getInstance().getIcon(warp.getName()) != null || SimpleWarpManager.getInstance().existsWarp(warp.getName())) {
                            sender.sendMessage(Lang.getPrefix() + Lang.get("Name_Already_Exists"));

                            SimpleMessage simpleMessage = new SimpleMessage(Lang.getPrefix() + Lang.get("Import_Choose_New_Name"), WarpSystem.getInstance());

                            simpleMessage.replace("%YES%", new ChatButton("§a" + Lang.get("Yes"), Lang.get("Click_Hover")) {
                                @Override
                                public void onClick(Player player) {
                                    AnvilGUI.openAnvil(WarpSystem.getInstance(), (Player) sender, new AnvilListener() {
                                        @Override
                                        public void onClick(AnvilClickEvent e) {
                                            e.setCancelled(true);
                                            e.setClose(false);

                                            String s = e.getInput();
                                            if(s != null && (s.isEmpty() || s.equalsIgnoreCase("none") || s.equalsIgnoreCase("-") || s.equalsIgnoreCase("null"))) s = null;

                                            if(s == null) {
                                                sender.sendMessage(Lang.getPrefix() + Lang.get("Enter_Name"));
                                                return;
                                            }

                                            if(IconManager.getInstance().getIcon(s) != null || SimpleWarpManager.getInstance().existsWarp(s)) {
                                                sender.sendMessage(Lang.getPrefix() + Lang.get("Name_Already_Exists"));
                                                return;
                                            }

                                            warp.setName(s);
                                            SimpleWarpManager.getInstance().addWarp(warp);
                                            sender.sendMessage(Lang.getPrefix() + Lang.get("Import_Warp_Imported").replace("%WARP%", warp.getName()));
                                            e.setClose(true);
                                        }

                                        @Override
                                        public void onClose(AnvilCloseEvent e) {
                                            if(e.getSubmittedText() == null)
                                                sender.sendMessage(Lang.getPrefix() + Lang.get("Import_Could_Not_Import_Warp"));
                                        }
                                    }, new ItemBuilder(XMaterial.NAME_TAG).setName(Lang.get("Name") + "...").getItem());
                                    simpleMessage.destroy();
                                }
                            });

                            simpleMessage.replace("%NO%", new ChatButton("§c" + Lang.get("No"), Lang.get("Click_Hover")) {
                                @Override
                                public void onClick(Player player) {
                                    sender.sendMessage(Lang.getPrefix() + Lang.get("Import_Could_Not_Import_Warp"));
                                    simpleMessage.destroy();
                                }
                            });

                            simpleMessage.send((Player) sender);
                        } else {
                            SimpleWarpManager.getInstance().addWarp(warp);
                            sender.sendMessage(Lang.getPrefix() + Lang.get("Import_Warp_Imported").replace("%WARP%", warp.getName()));
                        }
                        break;
                    }

                    default: {
                        sender.sendMessage(Lang.getPrefix() + Lang.get("Single_Import_Not_Available"));
                    }
                }
                return false;
            }
        });
    }

    private static void sendInfoMessage(CommandSender sender) {
        boolean line = sender instanceof Player;

        sender.sendMessage(new String[] {
                "",
                "§7§m" + (line ? "               " : "---------------") + "§7< §6WarpSystem §7>§m" + (line ? "               " : "---------------") + "§7",
                "",
                "     §3Author: §bCodingAir",
                "     §3Version: §bv" + WarpSystem.getInstance().getDescription().getVersion() + " §7[" + "§6Premium" + "§7]",
                "",
                "     §eAvailable on SpigotMc!",
                ""
        });
    }
}
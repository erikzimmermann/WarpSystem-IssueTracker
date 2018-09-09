package de.codingair.warpsystem.spigot.base.commands;

import de.codingair.codingapi.server.commands.BaseComponent;
import de.codingair.codingapi.server.commands.CommandBuilder;
import de.codingair.codingapi.server.commands.CommandComponent;
import de.codingair.codingapi.server.commands.MultiCommandComponent;
import de.codingair.codingapi.server.fancymessages.FancyMessage;
import de.codingair.codingapi.server.fancymessages.MessageTypes;
import de.codingair.codingapi.utils.TextAlignment;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.features.globalwarps.managers.GlobalWarpManager;
import de.codingair.warpsystem.spigot.features.shortcuts.managers.ShortcutManager;
import de.codingair.warpsystem.spigot.features.shortcuts.utils.Shortcut;
import de.codingair.warpsystem.spigot.features.warps.guis.GWarps;
import de.codingair.warpsystem.spigot.features.warps.guis.affiliations.Warp;
import de.codingair.warpsystem.spigot.features.warps.guis.utils.GUIListener;
import de.codingair.warpsystem.spigot.features.warps.guis.utils.Task;
import de.codingair.warpsystem.spigot.features.warps.importfilter.ImportType;
import de.codingair.warpsystem.spigot.features.warps.importfilter.Result;
import de.codingair.warpsystem.spigot.base.language.Example;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.warps.managers.IconManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CWarpSystem extends CommandBuilder {
    public CWarpSystem() {
        super("WarpSystem", new BaseComponent(WarpSystem.PERMISSION_MODIFY) {
            @Override
            public void noPermission(CommandSender sender, String label, CommandComponent child) {
                Player p = (Player) sender;
                getInfoMessage().send(p);
            }

            @Override
            public void onlyFor(boolean player, CommandSender sender, String label, CommandComponent child) {
                if(player) {
                    sender.sendMessage(Lang.getPrefix() + Lang.get("Only_For_Players", new Example("ENG", "&cThis is only for players!"), new Example("GER", "&cDies ist nur für Spieler!")));
                }
            }

            @Override
            public void unknownSubCommand(CommandSender sender, String label, String[] args) {
                sender.sendMessage("§8» §7" + Lang.get("Use", new Example("ENG", "Use"), new Example("GER", "Benutze")) + ": /" + label + " §e<info, reload, import, news, report, shortcut>");
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                sender.sendMessage("§8» §7" + Lang.get("Use", new Example("ENG", "Use"), new Example("GER", "Benutze")) + ": /" + label + " §e<info, reload, import, news, report, shortcut>");
                return false;
            }
        }, true);

        getBaseComponent().addChild(new CommandComponent("shortcut") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                return false;
            }
        }.setOnlyPlayers(true));

        getComponent("shortcut").addChild(new CommandComponent("remove") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                sender.sendMessage("§8» §7" + Lang.get("Use", new Example("ENG", "Use"), new Example("GER", "Benutze")) + ": /" + label + " shortcut remove §e<name>");
                return false;
            }
        }.setOnlyPlayers(true));

        getComponent("shortcut", "remove").addChild(new MultiCommandComponent() {
            @Override
            public void addArguments(CommandSender sender, List<String> suggestions) {
                for(Shortcut shortcut : ShortcutManager.getInstance().getShortcuts()) {
                    suggestions.add(shortcut.getDisplayName());
                }
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String argument, String[] args) {
                Shortcut shortcut = ShortcutManager.getInstance().getShortcut(argument);

                if(shortcut == null) {
                    sender.sendMessage(Lang.getPrefix() + Lang.get("Shortcut_does_not_exist", new Example("ENG", "&cThis shortcut does not exist!"), new Example("GER", "&cDieser Shortcut existiert nicht!")));
                    return false;
                }

                ShortcutManager.getInstance().getShortcuts().remove(shortcut);
                sender.sendMessage(Lang.getPrefix() + Lang.get("Shortcut_was_removed", new Example("ENG", "&7The shortcut \"&b%SHORTCUT%&7\" was &cremoved&7!"), new Example("GER", "&7Der Shortcut \"&b%SHORTCUT%&7\" wurde &cgelöscht&7!")).replace("%SHORTCUT%", shortcut.getDisplayName()));
                return false;
            }
        });

        getComponent("shortcut").addChild(new CommandComponent("add") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                sender.sendMessage("§8» §7" + Lang.get("Use", new Example("ENG", "Use"), new Example("GER", "Benutze")) + ": /" + label + " shortcut add §e<warp, globalwarp>");
                return false;
            }
        }.setOnlyPlayers(true));

        getComponent("shortcut", "add").addChild(new CommandComponent("warp") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                sender.sendMessage("§8» §7" + Lang.get("Use", new Example("ENG", "Use"), new Example("GER", "Benutze")) + ": /" + label + " shortcut add warp §e<warp>");
                return false;
            }
        }.setOnlyPlayers(true));

        getComponent("shortcut", "add", "warp").addChild(new MultiCommandComponent() {
            @Override
            public void addArguments(CommandSender sender, List<String> suggestions) {
                System.out.println("Add 1");
                for(Warp warp : IconManager.getInstance().getWarps()) {
                    suggestions.add(warp.getIdentifier());
                }
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String argument, String[] args) {
                sender.sendMessage("§8» §7" + Lang.get("Use", new Example("ENG", "Use"), new Example("GER", "Benutze")) + ": /" + label + " shortcut add warp " + argument + " §e<shortcut>");
                return false;
            }
        });

        getComponent("shortcut", "add", "warp", null).addChild(new MultiCommandComponent() {
            @Override
            public void addArguments(CommandSender sender, List<String> suggestions) {
                System.out.println("Add 2");
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String argument, String[] args) {
                String warpId = args[3];
                String shortcut = argument;

                Warp warp = IconManager.getInstance().getWarp(warpId);

                if(warp == null) {
                    sender.sendMessage(Lang.getPrefix() + Lang.get("WARP_DOES_NOT_EXISTS", new Example("ENG", "&cThis warp does not exist."), new Example("GER", "&cDieser Warp existiert nicht.")));
                    return false;
                }

                if(ShortcutManager.getInstance().getShortcut(shortcut) != null) {
                    sender.sendMessage(Lang.getPrefix() + Lang.get("Shortcut_already_exists", new Example("ENG", "&cThis shortcut already exists!"), new Example("GER", "&cDieser Shortcut existiert bereits!")));
                    return false;
                }

                ShortcutManager.getInstance().getShortcuts().add(new Shortcut(warp, shortcut));
                sender.sendMessage(Lang.getPrefix() + Lang.get("Shortcut_created", new Example("ENG", "&7The shortcut \"&b%SHORTCUT%&7\" was &aadded&7!"), new Example("GER", "&7Der Shortcut \"&b%SHORTCUT%&7\" wurde &ahinzugefügt&7!")).replace("%SHORTCUT%", shortcut));
                return false;
            }
        });

        getComponent("shortcut", "add").addChild(new CommandComponent("globalwarp") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                sender.sendMessage("§8» §7" + Lang.get("Use", new Example("ENG", "Use"), new Example("GER", "Benutze")) + ": /" + label + " shortcut add globalwarp §e<globalwarp>");
                return false;
            }
        }.setOnlyPlayers(true));

        getComponent("shortcut", "add", "globalwarp").addChild(new MultiCommandComponent() {
            @Override
            public void addArguments(CommandSender sender, List<String> suggestions) {
                suggestions.addAll(GlobalWarpManager.getInstance().getGlobalWarps().keySet());
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String argument, String[] args) {
                sender.sendMessage("§8» §7" + Lang.get("Use", new Example("ENG", "Use"), new Example("GER", "Benutze")) + ": /" + label + " shortcut add globalwarp " + argument + " §e<shortcut>");
                return false;
            }
        });

        getComponent("shortcut", "add", "globalwarp", null).addChild(new MultiCommandComponent() {
            @Override
            public void addArguments(CommandSender sender, List<String> suggestions) {

            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String argument, String[] args) {
                String warpId = args[3];
                String shortcut = argument;

                String globalWarp = GlobalWarpManager.getInstance().getCaseCorrectlyName(warpId);

                if(globalWarp == null) {
                    sender.sendMessage(Lang.getPrefix() + Lang.get("WARP_DOES_NOT_EXISTS", new Example("ENG", "&cThis warp does not exist."), new Example("GER", "&cDieser Warp existiert nicht.")));
                    return false;
                }

                if(ShortcutManager.getInstance().getShortcut(shortcut) != null) {
                    sender.sendMessage(Lang.getPrefix() + Lang.get("Shortcut_already_exists", new Example("ENG", "&cThis shortcut already exists!"), new Example("GER", "&cDieser Shortcut existiert bereits!")));
                    return false;
                }

                ShortcutManager.getInstance().getShortcuts().add(new Shortcut(globalWarp, shortcut));
                sender.sendMessage(Lang.getPrefix() + Lang.get("Shortcut_created", new Example("ENG", "&7The shortcut \"&b%SHORTCUT%&7\" was &aadded&7!"), new Example("GER", "&7Der Shortcut \"&b%SHORTCUT%&7\" wurde &ahinzugefügt&7!")).replace("%SHORTCUT%", shortcut));
                return false;
            }
        });


        getBaseComponent().addChild(new CommandComponent("info") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                Player p = (Player) sender;
                getInfoMessage().send(p);
                return false;
            }
        }.setOnlyPlayers(true));

        getBaseComponent().addChild(new CommandComponent("news") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                int updateId = WarpSystem.getInstance().getLatestVersionId();

                Player p = (Player) sender;
                TextComponent tc0 = new TextComponent(Lang.getPrefix() + "§7Click »");
                TextComponent click = new TextComponent("§chere");
                TextComponent tc1 = new TextComponent("§7« to read all new stuff!");

                click.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org/resources/warps-portals-and-warpsigns-warp-system-only-gui.29595/update?update=" + updateId));
                click.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new net.md_5.bungee.api.chat.BaseComponent[] {new TextComponent("§7»Click«")}));

                tc0.addExtra(click);
                tc0.addExtra(tc1);

                p.spigot().sendMessage(tc0);
                return false;
            }
        }.setOnlyPlayers(true));

        getBaseComponent().addChild(new CommandComponent("report") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                sender.sendMessage("§8» §7" + Lang.get("Use", new Example("ENG", "Use"), new Example("GER", "Benutze")) + ": /" + label + " report §e<GitHub, Spigot-Forum, Direct>");
                return false;
            }
        }.setOnlyPlayers(true));

        getComponent("report").addChild(new MultiCommandComponent() {
            @Override
            public void addArguments(CommandSender sender, List<String> suggestions) {
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

                        link.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/CodingAir/WarpSystem/issues/new"));
                        link.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new net.md_5.bungee.api.chat.BaseComponent[] {new TextComponent("§7»Click«")}));

                        base.addExtra(link);
                        base.addExtra(base1);

                        ((Player) sender).spigot().sendMessage(base);
                        break;

                    case "spigot-forum":
                        base = new TextComponent(Lang.getPrefix() + "§7Click »");
                        link = new TextComponent("§chere");
                        base1 = new TextComponent("§7« to report the bug to the §6SpigotMc-Forum§7.");

                        link.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org/threads/warps-portals-and-warpsigns-warp-system-only-gui.182037/page-9999"));
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
                        sender.sendMessage("§8» §7" + Lang.get("Use", new Example("ENG", "Use"), new Example("GER", "Benutze")) + ": /" + label + " report <GitHub, Spigot-Forum>");
                        break;
                }
                return false;
            }
        });

        getBaseComponent().addChild(new CommandComponent("reload") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                try {
                    sender.sendMessage(Lang.getPrefix() + Lang.get("Plugin_Reloading", new Example("ENG", "&cThe plugin will be reloaded now..."), new Example("GER", "&cDas Plugin wird jetzt neu geladen...")));
                    WarpSystem.getInstance().reload(true);
                    sender.sendMessage(Lang.getPrefix() + Lang.get("Success_Plugin_Reloaded", new Example("ENG", "&aThe plugin has been reloaded."), new Example("GER", "&aDas Plugin wurde neu geladen.")));
                } catch(Exception ex) {
                    ex.printStackTrace();
                }
                return false;
            }
        });

        getComponent("reload").addChild(new MultiCommandComponent() {
            @Override
            public void addArguments(CommandSender sender, List<String> suggestions) {
                suggestions.add("true");
                suggestions.add("false");
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String argument, String[] args) {
                if(argument == null || (!argument.equalsIgnoreCase("true") && !argument.equalsIgnoreCase("false"))) {
                    sender.sendMessage("§8» §7" + Lang.get("Use", new Example("ENG", "Use"), new Example("GER", "Benutze")) + ": /" + label + " §e<true, false>");
                    return false;
                }

                boolean save = argument.equalsIgnoreCase("true");
                sender.sendMessage(Lang.getPrefix() + Lang.get("Plugin_Reloading", new Example("ENG", "&cThe plugin will be reloaded now..."), new Example("GER", "&cDas Plugin wird jetzt neu geladen...")));
                WarpSystem.getInstance().reload(save);
                if(save)
                    sender.sendMessage(Lang.getPrefix() + Lang.get("Success_Plugin_Reloaded", new Example("ENG", "&aThe plugin has been reloaded."), new Example("GER", "&aDas Plugin wurde neu geladen.")));
                else
                    sender.sendMessage(Lang.getPrefix() + Lang.get("Success_Plugin_Reloaded_Without_Saving", new Example("ENG", "&aThe plugin was reloaded without saving."), new Example("GER", "&aDas Plugin wurde ohne zu speichern neu geladen.")));
                return false;
            }
        });

        getBaseComponent().addChild(new CommandComponent("import") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                sender.sendMessage("§8» §7" + Lang.get("Use", new Example("ENG", "Use"), new Example("GER", "Benutze")) + ": /" + label + " import §e<CategoryWarps, Essentials>");
                return false;
            }
        });

        getComponent("import").addChild(new MultiCommandComponent() {
            @Override
            public void addArguments(CommandSender sender, List<String> suggestions) {
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
                    sender.sendMessage("§8» §7" + Lang.get("Use", new Example("ENG", "Use"), new Example("GER", "Benutze")) + ": /" + label + " import §e<CategoryWarps, Essentials>");
                } else {
                    sender.sendMessage(Lang.getPrefix() + Lang.get("Import_Start", new Example("ENG", "&7Importing data from other systems..."), new Example("GER", "&7Dateien von anderen System werden importiert...")));

                    Result result;
                    if((result = type.importData()).isFinished()) {
                        sender.sendMessage(Lang.getPrefix() + Lang.get("Import_Finish", new Example("ENG", "&7All files are imported &asuccessfully&7."), new Example("GER", "&7Alle Dateien wurden &aerfolgreich &7importiert.")));
                    } else {
                        sender.sendMessage(Lang.getPrefix() + Lang.get("Import_Finish_With_Errors", new Example("ENG", "&7Could &cnot &7import all files."), new Example("GER", "&7Es konnten &cnicht alle Dateien &7importiert werden.")) + " §8[" + result.name() + "]");
                    }
                }
                return false;
            }
        });
    }

    private static FancyMessage getInfoMessage() {
        FancyMessage fancyMessage = new FancyMessage(MessageTypes.INFO_MESSAGE, true, "§6§nWarpSystem", "", "§3Author: §bCodingAir", "§3Version: §b" + WarpSystem.getInstance().getDescription().getVersion(), "", "§eAvailable on SpigotMc!");
        fancyMessage.setAlignment(TextAlignment.CENTER);
        fancyMessage.setCentered(true);
        return fancyMessage;
    }
}
package prisongame.prisongame;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffectType;

public class CommandKit implements CommandExecutor {

    // This method is called, when somebody uses our command
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (PrisonGame.wardenenabled && !PrisonGame.hardmode.get(sender)) {
            if (args.length == 0) {
                if (PrisonGame.warden == null && sender instanceof Player && PrisonGame.wardenCooldown <= 0) {
                    if (((Player) sender).getGameMode() != GameMode.SPECTATOR) {
                        Player nw = (Player) sender;
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "advancement grant " + sender.getName() + " only prison:mprison");
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "advancement grant " + sender.getName() + " only prison:guard");
                        for (Player p : Bukkit.getOnlinePlayers()) {
                                if (PrisonGame.type.get(p) != 0) {
                                    MyListener.playerJoin(p, false);
                                }
                                PrisonGame.type.put(p, 0);
                                PrisonGame.askType.put(p, 0);
                            p.playSound(p, Sound.BLOCK_END_PORTAL_SPAWN, 1, 1);
                            p.sendTitle("", ChatColor.RED + nw.getName() + ChatColor.GREEN + " is the new warden!");
                            PrisonGame.wardenCooldown = 20 * 6;
                        }
                        Bukkit.getScoreboardManager().getMainScoreboard().getTeam("Warden").addPlayer(nw);
                        PrisonGame.type.put(nw, -1);
                        PrisonGame.warden = nw;
                        PrisonGame.swat = false;
                        nw.teleport(PrisonGame.active.getWardenspawn());
                        nw.setCustomName(ChatColor.GRAY + "[" + ChatColor.RED + "WARDEN" + ChatColor.GRAY + "] " + ChatColor.WHITE + nw.getName());
                        nw.setPlayerListName(ChatColor.GRAY + "[" + ChatColor.RED + "WARDEN" + ChatColor.GRAY + "] " + ChatColor.WHITE + nw.getName());
                        nw.setDisplayName(ChatColor.GRAY + "[" + ChatColor.RED + "WARDEN" + ChatColor.GRAY + "] " + ChatColor.WHITE + nw.getName());

                        ItemStack card2 = new ItemStack(Material.IRON_SHOVEL);
                        ItemMeta cardm2 = card2.getItemMeta();
                        cardm2.setDisplayName(ChatColor.BLUE + "Handcuffs " + ChatColor.RED + "[CONTRABAND]");
                        cardm2.addEnchant(Enchantment.KNOCKBACK, 1, true);
                        card2.setItemMeta(cardm2);
                        nw.getInventory().addItem(card2);

                        nw.getInventory().setHelmet(new ItemStack(Material.CHAINMAIL_HELMET));
                        nw.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
                        nw.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
                        nw.getInventory().setBoots(new ItemStack(Material.NETHERITE_BOOTS));

                        ItemStack wardenSword = new ItemStack(Material.DIAMOND_SWORD);
                        wardenSword.addEnchantment(Enchantment.DAMAGE_ALL, 2);
                        wardenSword.addEnchantment(Enchantment.DURABILITY, 2);

                        if (nw.getPersistentDataContainer().has(PrisonGame.reinforcement, PersistentDataType.INTEGER)) {
                            nw.getInventory().addItem(new ItemStack(Material.DIAMOND_AXE));
                            nw.getInventory().setHelmet(new ItemStack(Material.IRON_HELMET));
                        }

                        nw.getInventory().addItem(wardenSword);
                        nw.getInventory().addItem(new ItemStack(Material.BOW));
                        nw.getInventory().addItem(new ItemStack(Material.ARROW, 64));
                        nw.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 64));

                        ItemStack card = new ItemStack(Material.TRIPWIRE_HOOK);
                        ItemMeta cardm = card.getItemMeta();
                        cardm.setDisplayName(ChatColor.BLUE + "Keycard " + ChatColor.RED + "[CONTRABAND]");
                        card.setItemMeta(cardm);
                        nw.getInventory().addItem(card);

                        if (nw.getPersistentDataContainer().has(PrisonGame.protspawn, PersistentDataType.INTEGER)) {
                                if (nw.getInventory().getHelmet() != null)
                                    nw.getInventory().getHelmet().addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
                                if (nw.getInventory().getChestplate() != null)
                                    nw.getInventory().getChestplate().addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
                                if (nw.getInventory().getLeggings() != null)
                                    nw.getInventory().getLeggings().addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
                                if (nw.getInventory().getBoots() != null)
                                    nw.getInventory().getBoots().addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
                        }
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Someone else is already the warden!");
                }
            } else if (PrisonGame.warden.equals(sender)) {
                if (args[0].equals("guard")) {
                    if (Bukkit.getPlayer(args[1]) != null) {
                        Player g = Bukkit.getPlayer(args[1]);
                        if (g.isOnline() && g != sender && PrisonGame.type.get(g) == 0) {
                            PrisonGame.askType.put(g, 1);
                            g.sendMessage(ChatColor.BLUE + "The wardens wants you to be a guard! use '/accept'");
                        } else {
                            sender.sendMessage(ChatColor.BLUE + "We had troubles promoting this player.");
                        }
                    }
                }
                if (args[0].equals("swat")) {
                    if (PrisonGame.swat) {
                        if (Bukkit.getPlayer(args[1]) != null) {
                            Player g = Bukkit.getPlayer(args[1]);
                            if (g.isOnline() && g != sender) {
                                PrisonGame.askType.put(g, 3);
                                g.sendMessage(ChatColor.DARK_GRAY + "The wardens wants you to be a SWAT guard! use '/accept'");
                            } else {
                                sender.sendMessage(ChatColor.BLUE + "We had troubles promoting this player. If they're a guard/nurse, demote them, then promote them  to swat again!");
                            }
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "You don't have the 'SWAT GUARDS' upgrade! Buy it from the guard shop!");
                    }
                }
                if (args[0].equals("nurse")) {
                    if (Bukkit.getPlayer(args[1]) != null) {
                        Player g = Bukkit.getPlayer(args[1]);
                        if (g.isOnline() && g != sender) {
                            PrisonGame.askType.put(g, 2);
                            g.sendMessage(ChatColor.LIGHT_PURPLE + "The wardens wants you to be a nurse! use '/accept'");
                        } else {
                            sender.sendMessage(ChatColor.BLUE + "We had troubles promoting this player.");
                        }
                    }
                }
                if (args[0].equals("resign")) {
                    if (PrisonGame.wardenCooldown <= 0) {
                        MyListener.playerJoin(PrisonGame.warden, false);
                        Bukkit.broadcastMessage(ChatColor.GREEN + "The warden has resigned!");
                        PrisonGame.wardenCooldown = 60;
                        PrisonGame.warden = null;
                    }
                }
                if (args[0].equals("solitary")) {
                    if (Bukkit.getPlayer(args[1]) != null) {
                        Player g = Bukkit.getPlayer(args[1]);
                        Integer solitcount = 0;
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            if (p.getDisplayName().contains("SOLITARY")) {
                                solitcount += 1;
                            }
                        }
                        if (solitcount < 3) {
                            if (PrisonGame.solitcooldown <= 0) {
                                if (g.isOnline() && g != sender && PrisonGame.type.get(g) == 0) {
                                    if (g.getGameMode() == GameMode.SPECTATOR) {
                                        PrisonGame.solitcooldown = (20 * 60) * 2;
                                        Bukkit.broadcastMessage(ChatColor.GRAY + g.getName() + " was sent to solitary!");
                                        g.setGameMode(GameMode.ADVENTURE);
                                        g.removePotionEffect(PotionEffectType.LUCK);
                                        PrisonGame.escaped.put(g, true);
                                        PrisonGame.solittime.put(g, 20 * 120);
                                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "advancement grant " + g.getName() + " only prison:solit");
                                        Bukkit.getScheduler().runTaskLater(PrisonGame.getPlugin(PrisonGame.class), () -> {
                                            g.teleport(PrisonGame.active.getSolit());
                                        }, 3);
                                        g.sendTitle("", "You're in solitary.", 10, 0, 10);
                                        g.addPotionEffect(PotionEffectType.WATER_BREATHING.createEffect(Integer.MAX_VALUE, 1));
                                        Player p = g;
                                        p.setCustomName(ChatColor.GRAY + "[" + ChatColor.BLACK + "SOLITARY" + ChatColor.GRAY + "] " + ChatColor.DARK_GRAY + p.getName());
                                        p.setPlayerListName(ChatColor.GRAY + "[" + ChatColor.BLACK + "SOLITARY" + ChatColor.GRAY + "] " + ChatColor.DARK_GRAY + p.getName());
                                        p.setDisplayName(ChatColor.GRAY + "[" + ChatColor.BLACK + "SOLITARY" + ChatColor.GRAY + "] " + ChatColor.DARK_GRAY + p.getName());

                                    }
                                }
                            } else {
                                    sender.sendMessage(ChatColor.RED + "Solitary is on cooldown!");
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED + "Solitary can't hold more than 3 people!");
                        }
                    }
                }
                if (args[0].equals("fire")) {
                    if (Bukkit.getPlayer(args[1]) != null) {
                        Player g = Bukkit.getPlayer(args[1]);
                        if (g.isOnline() && g != sender && PrisonGame.type.get(g) != 0) {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "advancement grant " + sender.getName() + " only prison:strike");
                            Bukkit.broadcastMessage(ChatColor.GOLD + g.getName() + " was fired.");
                            MyListener.playerJoin(g, false);
                        }
                    }
                }
                if (args[0].equals("prefix")) {
                    String prefix = args[1].toUpperCase();
                    Integer prefixlength = 16;
                /*if (((Player) sender).getPersistentDataContainer().getOrDefault(PrisonGame.rank, PersistentDataType.INTEGER, 0) == 1) {
                    prefixlength = 32;
                }*/
                    if (prefix.length() <= prefixlength) {
                        Player g = (Player) sender;
                        g.setCustomName(ChatColor.GRAY + "[" + ChatColor.RED + ChatColor.translateAlternateColorCodes('&', prefix) + " WARDEN" + ChatColor.GRAY + "] " + ChatColor.WHITE + g.getName());
                        g.setPlayerListName(ChatColor.GRAY + "[" + ChatColor.RED + ChatColor.translateAlternateColorCodes('&', prefix) + " WARDEN" + ChatColor.GRAY + "] " + ChatColor.WHITE + g.getName());
                        g.setDisplayName(ChatColor.GRAY + "[" + ChatColor.RED + ChatColor.translateAlternateColorCodes('&', prefix) + " WARDEN" + ChatColor.GRAY + "] " + ChatColor.WHITE + g.getName());

                    } else {
                        sender.sendMessage("That's too long!");
                    }
                }
                if (args[0].equals("target")) {
                    Player g = Bukkit.getPlayer(args[1]);
                    g.addPotionEffect(PotionEffectType.GLOWING.createEffect(20 * 30, 0));
                }
                if (args[0].equals("help")) {
                    if (PrisonGame.warden.equals(sender)) {
                        Player p = (Player) sender;
                        p.sendMessage(ChatColor.DARK_GRAY + "-=-=-=-=-=-=-=-");
                        p.sendMessage(ChatColor.BLUE + "/warden help" + ChatColor.DARK_GRAY + " - " + ChatColor.WHITE + "Shows you this menu.");
                        p.sendMessage(ChatColor.BLUE + "/warden guard [name]" + ChatColor.DARK_GRAY + " - " + ChatColor.WHITE + "Makes another player a guard.");
                        p.sendMessage(ChatColor.BLUE + "/warden solitary [name]" + ChatColor.DARK_GRAY + " - " + ChatColor.WHITE + "Sends a player to solitary, later to torture them. " + ChatColor.RED + "[PLAYER MUST BE DEAD]");
                        p.sendMessage(ChatColor.BLUE + "/warden swat [name]" + ChatColor.DARK_GRAY + " - " + ChatColor.WHITE + "Makes another player a SWAT guard." + ChatColor.RED + " [REQUIRES 'SWAT GUARDS' UPGRADE!]");
                        p.sendMessage(ChatColor.BLUE + "/warden target [name]" + ChatColor.DARK_GRAY + " - " + ChatColor.WHITE + "Turns a player in a target.");
                        p.sendMessage(ChatColor.BLUE + "/warden nurse [name]" + ChatColor.DARK_GRAY + " - " + ChatColor.WHITE + "Makes another player a nurse.");
                        p.sendMessage(ChatColor.BLUE + "/warden prefix [prefix]" + ChatColor.DARK_GRAY + " - " + ChatColor.WHITE + "Adds a prefix to your name.");
                        p.sendMessage(ChatColor.BLUE + "/warden fire [guard name]" + ChatColor.DARK_GRAY + " - " + ChatColor.WHITE + "Fires a guard from their job");
                        p.sendMessage(ChatColor.BLUE + "/warden resign" + ChatColor.DARK_GRAY + " - " + ChatColor.WHITE + "Resigns you from your job.");
                        p.sendMessage(ChatColor.DARK_GRAY + "-=-=-=-=-=-=-=-");

                    }
                }
            }
        } else {
            Player p = (Player) sender;
            p.kickPlayer(ChatColor.RED + "Do not /warden during a reload.");
            p.sendMessage(ChatColor.RED + sender.getName() + " was kicked for doing /warden during a reload");
        }
        return true;
    }
}
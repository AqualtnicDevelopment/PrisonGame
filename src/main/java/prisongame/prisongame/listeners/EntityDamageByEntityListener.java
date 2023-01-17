package prisongame.prisongame.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffectType;
import prisongame.prisongame.MyTask;
import prisongame.prisongame.PrisonGame;
import prisongame.prisongame.lib.Role;

public class EntityDamageByEntityListener implements Listener {
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity().getType().equals(EntityType.ZOMBIE))
            event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamageByEntity2(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player a = (Player) event.getDamager();
            if (a.getInventory().getItemInMainHand().getType().equals(Material.IRON_SHOVEL)) {
                if (event.getEntity() instanceof Player p) {
                    p.setNoDamageTicks(0);
                }
            }
            if (PrisonGame.roles.get(a) == Role.PRISONER) {
                if (a.getInventory().getItemInMainHand().getType().equals(Material.WOODEN_AXE)) {
                    a.sendMessage(ChatColor.GREEN + "You cannot use prison work tools to fight!");
                    event.setCancelled(true);
                    return;
                }
                Player d = (Player) event.getEntity();
                if (PrisonGame.roles.get(d) != Role.PRISONER) {
                    a.addPotionEffect(PotionEffectType.GLOWING.createEffect(20 * 5, 0));
                }
            }
            Player p = (Player) event.getDamager();
            if (p.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
                p.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                p.setNoDamageTicks(0);
            }
        }
        if (event.getEntity() instanceof Player) {
            Player p = (Player) event.getEntity();
            if (p.isSleeping()) {
                event.setCancelled(true);
            }
        }
        if (MyTask.bossbar.getTitle().equals("ROLL CALL") || MyTask.bossbar.getTitle().equals("EVENING ROLL CALL")) {
            if (event.getEntity() instanceof Player) {
                Player p = (Player) event.getEntity();
                if (new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY() - 1, p.getLocation().getZ()).getBlock().getType().equals(Material.RED_SAND)) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
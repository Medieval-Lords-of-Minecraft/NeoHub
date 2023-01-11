package me.neoblade298.neohub;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.Event.Result;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.neoblade298.neocore.NeoCore;
import me.neoblade298.neocore.exceptions.NeoIOException;
import me.neoblade298.neocore.util.Util;

public class NeoHub extends JavaPlugin implements Listener {
	private static NeoHub inst;
	private static Location spawn;
	private static ItemStack selector;
	private static PotionEffect jump, speed;
	
	public void onEnable() {
		Bukkit.getServer().getLogger().info("NeoHub Enabled");
		Bukkit.getPluginManager().registerEvents(this, this);
		
		selector = new ItemStack(Material.COMPASS);
		ItemMeta meta = selector.getItemMeta();
		meta.setDisplayName("§9Server Selector");
		ArrayList<String> lore = new ArrayList<String>();
		lore.add("§7Right-click to select");
		meta.setLore(lore);
		selector.setItemMeta(meta);
		jump = new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 4);
		speed = new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 9);
		
		inst = this;
		reload();
	}
	
	private void reload() {
		try {
			NeoCore.loadFiles(new File(getDataFolder(), "config.yml"), (cfg, yml) -> {
				spawn = Util.stringToLoc(cfg.getString("spawn"));
			});
		} catch (NeoIOException e) {
			e.printStackTrace();
		}
	}
	
	public void onDisable() {
	    org.bukkit.Bukkit.getServer().getLogger().info("NeoHub Disabled");
	    super.onDisable();
	}
	
	public static NeoHub inst() {
		return inst;
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		p.teleport(spawn);
		ItemStack[] inv = p.getInventory().getContents();
		inv[8] = selector;
		p.getInventory().setContents(inv);
		p.addPotionEffect(jump);
		p.addPotionEffect(speed);
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if (e.getSlot() == 8 || e.getHotbarButton() == 8) {
			e.setResult(Result.DENY);
		}
	}
	
	@EventHandler
	public void onItemDrop(PlayerDropItemEvent e) {
		e.setCancelled(true);
	}
	
	@EventHandler(ignoreCancelled = false)
	public void onInteract(PlayerInteractEvent e) {
		if (e.getAction() != Action.RIGHT_CLICK_AIR &&
				e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		
		if (e.getItem() == null || e.getItem().getType() != Material.COMPASS) return;

		Bukkit.getServer().dispatchCommand(getServer().getConsoleSender(), "deluxemenus open servers " + e.getPlayer().getName());
	}
}

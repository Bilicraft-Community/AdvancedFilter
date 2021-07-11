package com.mcsunnyside.advancedfilter;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class AdvancedFilter extends JavaPlugin implements Listener {
    private final List<KeywordGroup> keywords = new ArrayList<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        Bukkit.getPluginManager().registerEvents(this,this);
        keywords.clear();
        ConfigurationSection keywordSection = getConfig().getConfigurationSection("keywords");
        keywordSection.getKeys(false).forEach(key->{
            ConfigurationSection column = keywordSection.getConfigurationSection(key);
            KeywordGroup group = new KeywordGroup(key,column.getStringList("words"),PunishmentWay.fromId(column.getString("punish")),column.getString("extra"));
            keywords.add(group);
        });
    }

    private boolean playerNameHit(String str){
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if(onlinePlayer.getName().contains(str)){
                return true;
            }
        }
        return false;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        keywords.clear();
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void playerChat(AsyncPlayerChatEvent event){
        FilterResult result = check(event.getPlayer(),event.getMessage());
        if(!result.isHit()){
            return;
        }
        if(result.isBlock()){
            event.setCancelled(true);
            return;
        }
        event.setMessage(result.getNewString());
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void playerSign(SignChangeEvent event){
        for (int i = 0; i < event.getLines().length; i++) {
            String line = event.getLine(i);
            FilterResult result = check(event.getPlayer(),line);
            if(!result.isHit()){
                return;
            }
            if(result.isBlock()){
                event.setCancelled(true);
                return;
            }
            event.setLine(i,result.getNewString());
        }
    }

    private void reportToAdmins(CommandSender sender, String text){
        if(sender == null){
            return;
        }
        getLogger().warning(ChatColor.RED+"玩家 "+sender.getName()+" 发送了包含关键词的信息： "+text);
        Bukkit.getOnlinePlayers().forEach(player->{
            if(player.hasPermission("advancedfilter.admin") || player.isOp()){
                player.sendMessage(ChatColor.RED+"玩家 "+sender.getName()+" 发送了包含关键词的信息： "+text);
            }
        });
    }

    /**
     * 检测关键字
     * @param sender sender
     * @param original text
     * @return 阻止下一步操作
     */
    public FilterResult check(CommandSender sender, String original){
        for (KeywordGroup group : this.keywords){
            String text = ChatColor.stripColor(original).toLowerCase();
            for (String keyword : group.getKeywords()){
                if(!text.toLowerCase().contains(keyword)){
                    continue;
                }
                if(playerNameHit(keyword)){
                    // 可能是提及玩家
                    continue;
                }
                //检测到了
                switch (group.getPunishmentWay()){
                    case BLOCK:
                        this.reportToAdmins(sender,original);
                        return new FilterResult(true,true,original);
                    case REPLACE:
                        this.reportToAdmins(sender,original);
                        if(group.getExtra().isEmpty()) {
                            return new FilterResult(true, false, ignoreCaseReplace(original, keyword, fillStar(keyword.length())));
                        }else{
                            return new FilterResult(true, false, ignoreCaseReplace(original, keyword, group.getExtra()));
                        }
                    case COMMAND:
                        this.reportToAdmins(sender,original);
                        Bukkit.getScheduler().runTask(this,()-> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), group.getExtra().replace("{group}",group.getName())
                                .replace("{name}",sender.getName())
                                .replace("{player}",sender.getName())
                                .replace("{sender}",sender.getName())
                                .replace("{keyword}",keyword)));
                        return new FilterResult(true,true,original);
                    case SILENT:
                        this.reportToAdmins(sender,original);
                        return new FilterResult(true,false,original);
                }
            }
        }
        return new FilterResult(false,false,original);
    }

    public String fillStar(int amount){
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < amount; i++) {
            buffer.append('*');
        }
        return buffer.toString();
    }

    public String ignoreCaseReplace(String source, String oldstring, String newstring){
        Pattern p = Pattern.compile(oldstring, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(source);
        return m.replaceAll(newstring);
    }
}


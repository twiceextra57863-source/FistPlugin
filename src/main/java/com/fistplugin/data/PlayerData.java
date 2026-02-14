package com.fistplugin.data;

import java.util.UUID;

public class PlayerData {
    
    private final UUID uuid;
    private FistType fistType;
    private long lastFistChange;
    private int kills;
    private int deaths;
    private int abilitiesUsed;
    private long playTime;
    
    public PlayerData(UUID uuid) {
        this.uuid = uuid;
        this.fistType = null;
        this.lastFistChange = 0;
        this.kills = 0;
        this.deaths = 0;
        this.abilitiesUsed = 0;
        this.playTime = 0;
    }
    
    // Getters and Setters
    public UUID getUuid() { return uuid; }
    public FistType getFistType() { return fistType; }
    public void setFistType(FistType fistType) { this.fistType = fistType; }
    public long getLastFistChange() { return lastFistChange; }
    public void setLastFistChange(long lastFistChange) { this.lastFistChange = lastFistChange; }
    public int getKills() { return kills; }
    public void setKills(int kills) { this.kills = kills; }
    public void addKill() { this.kills++; }
    public int getDeaths() { return deaths; }
    public void setDeaths(int deaths) { this.deaths = deaths; }
    public void addDeath() { this.deaths++; }
    public int getAbilitiesUsed() { return abilitiesUsed; }
    public void setAbilitiesUsed(int abilitiesUsed) { this.abilitiesUsed = abilitiesUsed; }
    public void addAbilityUsed() { this.abilitiesUsed++; }
    public long getPlayTime() { return playTime; }
    public void setPlayTime(long playTime) { this.playTime = playTime; }
}

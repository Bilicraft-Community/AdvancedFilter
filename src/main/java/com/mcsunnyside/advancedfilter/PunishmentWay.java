package com.mcsunnyside.advancedfilter;

public enum PunishmentWay {
    COMMAND("command"),
    BLOCK("block"),
    REPLACE("replace"),
    SILENT("silent");

    private final String id;
    PunishmentWay(String id){
        this.id = id;
    }

    public static PunishmentWay fromId(String id){
        for (PunishmentWay value : PunishmentWay.values()) {
            if(value.id.equalsIgnoreCase(id)){
                return value;
            }
        }
        throw new IllegalArgumentException("Enum cannot found: "+id);
    }
}

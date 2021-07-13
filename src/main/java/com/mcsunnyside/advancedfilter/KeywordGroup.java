package com.mcsunnyside.advancedfilter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@AllArgsConstructor
@Data
@Builder
public class KeywordGroup {
    private String name;
    private Set<String> keywords;
    private PunishmentWay punishmentWay;
    private String extra;
}

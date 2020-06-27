package com.mcsunnyside.advancedfilter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
@Builder
public class KeywordGroup {
    private String name;
    private List<String> keywords;
    private PunishmentWay punishmentWay;
    private String extra;
}

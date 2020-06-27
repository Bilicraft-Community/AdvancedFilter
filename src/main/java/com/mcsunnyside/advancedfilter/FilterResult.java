package com.mcsunnyside.advancedfilter;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class FilterResult {
    private boolean hit;
    private boolean block;
    private String newString;
}

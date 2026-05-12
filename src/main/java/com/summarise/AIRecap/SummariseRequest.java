package com.summarise.AIRecap;

import lombok.Data;

import java.util.List;

@Data
public class SummariseRequest {
    private List<String> messages;
}

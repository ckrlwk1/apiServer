package org.gaon.apiserver.settlement.dto;

import lombok.Data;

import java.util.List;

@Data
public class Expense {
    private int order;
    private String place;
    private String payer;
    private int amount;
    private String account;
    private List<String> participants;
}

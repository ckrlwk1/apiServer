package org.gaon.apiserver.settlement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ExpenseSettlementResult {
    private int order;

    private String place;
    private String account;

    private int totalAmount;

    private int perPersonAmount;

    private List<String> participants;
}

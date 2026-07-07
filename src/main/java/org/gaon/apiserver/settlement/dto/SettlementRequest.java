package org.gaon.apiserver.settlement.dto;

import lombok.Data;

import java.util.List;

@Data
public class SettlementRequest {

    private Host host;

    private List<String> participants;

    private List<Expense> expenses;
}

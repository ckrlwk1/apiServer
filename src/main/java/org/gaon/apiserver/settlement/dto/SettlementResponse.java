package org.gaon.apiserver.settlement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SettlementResponse {
    // 차수별 상세
    private List<ExpenseSettlementResult> details;


    // 실제 송금 내역
    private List<TransferResult> transfers;


    // AI 설명
    private String summary;
}
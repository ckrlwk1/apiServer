package org.gaon.apiserver.settlement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransferResult {
    private String from;

    private String to;

    private double amount;

}

package org.gaon.apiserver.settlement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
public class PersonBalance {
    String name;
    int amount;

    public PersonBalance(
            String name,
            int amount
    ) {
        this.name = name;
        this.amount = amount;
    }
    public void subtract(int value) {
        this.amount -= value;
    }

//    public void subtract(int value) {
//
//        this.amount -= value;
//
//    }
}

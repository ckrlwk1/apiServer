package org.gaon.apiserver.settlement;


import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.gaon.apiserver.settlement.dto.SettlementRequest;
import org.gaon.apiserver.settlement.dto.SettlementResponse;
import org.gaon.apiserver.settlement.service.SettlementService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SettlementController {

    private final SettlementService settlementService;

    @RequestMapping("/settlement")
    public SettlementResponse calculate(
            @RequestBody SettlementRequest request
    ) throws JsonProcessingException {
        return settlementService.calculateSettlement(request);
    }
}

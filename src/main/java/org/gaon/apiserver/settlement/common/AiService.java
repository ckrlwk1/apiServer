package org.gaon.apiserver.settlement.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.gaon.apiserver.settlement.dto.ExpenseSettlementResult;
import org.gaon.apiserver.settlement.dto.SettlementRequest;
import org.gaon.apiserver.settlement.dto.TransferResult;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.lang.runtime.ObjectMethods;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AiService {
    private final ChatClient chatClient;

    private final ObjectMapper objectMapper;

    public String makeSummary( List<ExpenseSettlementResult> details
    ) throws JsonProcessingException {

            String prompt = """
    너는 모임 정산 도우미야.
    
    아래 정산 결과를 사용자가 이해하기 쉽게 정리해줘.
    
    규칙:
    - 금액은 절대 변경하지 마.
    - 계산하지 마.
    - 제공된 결과만 설명해.
    - 각 차수별로 보여줘.
    
    정산 데이터:
    %s
        """.formatted(
                        objectMapper.writeValueAsString(details)
                );

        return chatClient.prompt(prompt)
                .call()
                .content();

    }
}

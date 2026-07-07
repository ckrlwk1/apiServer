package org.gaon.apiserver.settlement.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gaon.apiserver.settlement.common.AiService;
import org.gaon.apiserver.settlement.dto.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class SettlementService {
    private final AiService aiService;

    public SettlementResponse calculateSettlement(
            SettlementRequest request
    ) throws JsonProcessingException {


        // 1. 차수별 상세 계산
        List<ExpenseSettlementResult> details =
                calculateDetails(request);



        // 2. 실제 송금 계산
        List<TransferResult> transfers =
                calculateTransfers(request);



        // 3. AI 요약은 나중에 연결
        String summary = "";

        StringBuilder sb = new StringBuilder();

        sb.append("========== 정산 결과 ==========\n\n");

        Map<String, Integer> personAmount = new LinkedHashMap<>();

        for (ExpenseSettlementResult detail : details) {

            Expense expense =
                    request.getExpenses().stream()
                            .filter(e -> e.getOrder() == detail.getOrder())
                            .findFirst()
                            .orElseThrow();

            sb.append("📍 ")
                    .append(detail.getOrder())
                    .append("차 - ")
                    .append(detail.getPlace())
                    .append("\n");

            sb.append("참석자 : ");

            for (int i = 0; i < detail.getParticipants().size(); i++) {

                String name = detail.getParticipants().get(i);
                if (name.equals(expense.getPayer())) {
                    sb.append(name).append("(결제)");
                } else {
                    sb.append(name);
                }

                if (i < detail.getParticipants().size() - 1) {
                    sb.append(", ");
                }

                personAmount.merge(
                        name,
                        detail.getPerPersonAmount(),
                        Integer::sum
                );
            }

            sb.append("\n");
            sb.append("총 금액 : ")
                    .append(String.format("%,d", detail.getTotalAmount()))
                    .append("원\n");

            sb.append("1인당 : ")
                    .append(String.format("%,d", detail.getPerPersonAmount()))
                    .append("원\n\n");
        }

        sb.append("==============================\n\n");

        sb.append("💰 개인별 정산 금액\n\n");

        personAmount.forEach((name, amount) -> {
            sb.append(name)
                    .append(" : ")
                    .append(String.format("%,d", amount))
                    .append("원\n");
        });

        sb.append("\n==============================\n\n");

        sb.append("🏦 입금 계좌\n");
        sb.append(request.getHost().getAccount()).append("\n");
        sb.append("예금주 : ").append(request.getHost().getName()).append("\n\n");
        sb.append("입금 부탁드립니다 😊");



       // summary = aiService.makeSummary(details);


        log.info("summary={}", sb.toString());

        return new SettlementResponse(
                details,
                transfers,
                sb.toString()
        );

    }



    /**
     * 차수별 정산 상세
     */
    private List<ExpenseSettlementResult> calculateDetails(
            SettlementRequest request
    ) {


        List<ExpenseSettlementResult> result =
                new ArrayList<>();



        for(Expense expense
                : request.getExpenses()) {


            int participantCount =
                    expense.getParticipants().size();



            int perPerson =
                    expense.getAmount()
                            / participantCount;



            result.add(
                    new ExpenseSettlementResult(

                            expense.getOrder(),

                            expense.getPlace(),
                            expense.getAccount(),
                            expense.getAmount(),

                            perPerson,

                            expense.getParticipants()

                    )
            );

        }


        return result;

    }



    /**
     * 송금 계산
     */
    private List<TransferResult> calculateTransfers(
            SettlementRequest request
    ) {


        Map<String,Integer> balance =
                new HashMap<>();


        for(Expense expense
                : request.getExpenses()) {


            int perPerson =
                    expense.getAmount()
                            / expense.getParticipants().size();



            // 결제자 + 금액
            balance.merge(
                    expense.getPayer(),
                    expense.getAmount(),
                    Integer::sum
            );



            // 참여자 부담금 -
            for(String person :
                    expense.getParticipants()) {


                balance.merge(
                        person,
                        -perPerson,
                        Integer::sum
                );

            }

        }



        List<PersonBalance> receivers =
                new ArrayList<>();


        List<PersonBalance> senders =
                new ArrayList<>();



        balance.forEach((name, amount) -> {


            if(amount > 0) {

                receivers.add(
                        new PersonBalance(
                                name,
                                amount
                        )
                );

            }


            if(amount < 0) {

                senders.add(
                        new PersonBalance(
                                name,
                                -amount
                        )
                );

            }

        });



        List<TransferResult> result =
                new ArrayList<>();



        for(PersonBalance sender : senders) {


            for(PersonBalance receiver : receivers) {


                if(sender.getAmount() == 0) {
                    break;
                }


                if(receiver.getAmount() == 0) {
                    continue;
                }



                int amount =
                        Math.min(
                                sender.getAmount(),
                                receiver.getAmount()
                        );



                result.add(
                        new TransferResult(
                                sender.getName(),
                                receiver.getName(),
                                amount
                        )
                );



                sender.subtract(amount);

                receiver.subtract(amount);

            }

        }


        return result;

    }

}

package com.finance.transaction_service.presentation.controller;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionApplicationService service;
    private final TransactionProducer producer;

    @PostMapping
    public TransactionResponseDTO create(@RequestBody TransactionRequestDTO dto,
                                         @AuthenticationPrincipal User user) {

        Transaction transaction = dto.toDomain(user.getId());
        Transaction saved = service.create(transaction);

        TransactionResponseDTO response = TransactionResponseDTO.fromDomain(saved);
        producer.publish(response);

        return response;
    }

    @GetMapping
    public List<TransactionResponseDTO> list(@AuthenticationPrincipal User user) {
        return service.list(user.getId())
                .stream()
                .map(TransactionResponseDTO::fromDomain)
                .toList();
    }
}

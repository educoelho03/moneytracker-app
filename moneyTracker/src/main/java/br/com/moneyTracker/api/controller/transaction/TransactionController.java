package br.com.moneyTracker.api.controller.transaction;

import br.com.moneyTracker.domain.model.entities.Transactions;
import br.com.moneyTracker.api.dto.request.TransactionRequestDTO;
import br.com.moneyTracker.api.dto.response.TransactionResponseDTO;
import br.com.moneyTracker.config.security.SecurityConfig;
import br.com.moneyTracker.domain.model.enums.TRANSACTION_TYPE;
import br.com.moneyTracker.domain.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@SecurityRequirement(name = SecurityConfig.SECURITY)
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/add")
    public ResponseEntity<TransactionResponseDTO> createNewTransaction(@RequestHeader("Authorization") String token, @RequestBody TransactionRequestDTO request) {
        Transactions createdTransaction = transactionService.createNewTransaction(token, request.transaction());
        TransactionResponseDTO response = TransactionResponseDTO.fromEntity(createdTransaction);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping()
    public ResponseEntity<List<TransactionResponseDTO>> getAllTransactions(
            @RequestHeader("Authorization") String token
    ) {
        return ResponseEntity.status(200).body(transactionService.listTransactionsByEmail(token));
    }


    @GetMapping("/pagination")
    public ResponseEntity<Page<TransactionResponseDTO>> listAllTransactionsPageable
            (@RequestHeader("Authorization") String token,
             @RequestParam int page,
             @RequestParam int size
            ){
        Page<Transactions> transactionsEntity = transactionService.findAllPaginacao(token, page, size);
        Page<TransactionResponseDTO> transactionResponse = TransactionResponseDTO.fromPageEntity(transactionsEntity);
        return ResponseEntity.status(200).body(transactionResponse);
    }

    @GetMapping("/filter/{name}")
    public ResponseEntity<List<TransactionResponseDTO>> filterTransactionsByName
            (@RequestHeader("Authorization") String token,
             @PathVariable String name
            ){
        List<Transactions> transactionsEntity = transactionService.listAllTransactionsByName(token, name);
        List<TransactionResponseDTO> transactionResponse = TransactionResponseDTO.fromEntityList(transactionsEntity);
        return ResponseEntity.status(200).body(transactionResponse);
    }

    @GetMapping("/filter/{name}/{transactionType}")
    public ResponseEntity<List<TransactionResponseDTO>> filterTransactionsByName
            (@RequestHeader("Authorization") String token,
             @RequestParam String name,
             @RequestParam String transactionType
            ){
        List<Transactions> transactionsEntity = transactionService.listAllTransactionsByNameAndType(token, name, transactionType);
        List<TransactionResponseDTO> transactionResponse = TransactionResponseDTO.fromEntityList(transactionsEntity);
        return ResponseEntity.status(200).body(transactionResponse);
    }
}

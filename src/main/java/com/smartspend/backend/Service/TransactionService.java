package com.smartspend.backend.Service;

import com.smartspend.backend.DTO.TransactionDTO;
import com.smartspend.backend.DTO.TransactionResponseDTO;
import com.smartspend.backend.Entity.Transaction;
import com.smartspend.backend.Entity.TransactionType;
import com.smartspend.backend.Entity.User;
import com.smartspend.backend.Repository.TransactionRepository;
import com.smartspend.backend.Repository.UserRepository;
import com.smartspend.backend.Util.FirebaseTokenUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    // ------------------- DTO MAPPING -------------------
    private TransactionDTO mapToDTO(Transaction transaction) {
        return TransactionDTO.builder()
                .id(transaction.getId())
                .userUid(transaction.getUserUid())
                .amount(transaction.getAmount())
                .categories(transaction.getCategories())
                .date(transaction.getDate())
                .type(transaction.getType())
                .recurring(transaction.getRecurring())
                .description(transaction.getDescription())
                .mainCategory(transaction.getMainCategory())
                .build();
    }

    private Transaction mapToEntity(TransactionDTO dto) {
        return Transaction.builder()
                .id(dto.getId())
                .amount(dto.getAmount())
                .categories(dto.getCategories())
                .date(dto.getDate())
                .type(dto.getType())
                .recurring(dto.getRecurring())
                .description(dto.getDescription())
                .mainCategory(dto.getMainCategory())
                .build();
    }

    private TransactionResponseDTO mapToResponseDTO(Transaction transaction){
        return TransactionResponseDTO.builder()
                .id(transaction.getId())
                .type(transaction.getType())
                .amount(transaction.getAmount())
                .description(transaction.getDescription())
                .categories(transaction.getCategories())
                .isRecurring(transaction.getRecurring())
                .date(transaction.getDate())
                .mainCategory(transaction.getMainCategory())
                .build();
    }

    // ------------------- USER BALANCE UPDATE -------------------\
    //if any other transaction is already in process the current transaction will be failed
    //and it will retry for 3 times (as per the while loop) even after three times the current transaction
    //is not done then we are throwing the error and saying the client to try after some time
    @Transactional
    private void updateUserBalance(Transaction transaction, boolean add) {
        int maxRetries = 3;
        int attempt = 0;
        double effect = transaction.getAmount();
        if (transaction.getType() == TransactionType.EXPENSE) effect = -effect;
        if (!add) effect = -effect;

        while (attempt < maxRetries) {
            attempt++;
            try {
                User user = userRepository.findByUid(transaction.getUserUid())
                        .orElseThrow(() -> new RuntimeException("User not found"));
                user.setTotalBalance(user.getTotalBalance() + effect);
                userRepository.save(user); // @Version ensures optimistic locking
                return; // success
            } catch (ObjectOptimisticLockingFailureException ex) {
                if (attempt >= maxRetries)
                    throw new RuntimeException("Too many concurrent updates. Try again.", ex);
                //giving some time for the already processing transaction
                try{
                    Thread.sleep(50);
                }catch (InterruptedException e){

                }
                // else retry automatically
            }
        }
    }

    // ------------------- CRUD -------------------

    // CREATE
    @Transactional
    public TransactionResponseDTO createTransaction(TransactionDTO dto, String authHeader) throws Exception {
        // Extract UID and email from token
        String uid = FirebaseTokenUtil.getUidFromToken(authHeader);
        String email = FirebaseTokenUtil.getEmailFromToken(authHeader);
        if(dto.getTxnId()!=null && transactionRepository.existsByTxnId(dto.getTxnId())){
            Transaction existing=transactionRepository.findByTxnId(dto.getTxnId());
            return mapToResponseDTO(existing);
        }

        // Map DTO to entity
        Transaction transaction = mapToEntity(dto);
        transaction.setUserUid(uid);
        transaction.setEmail(email);


        //generate transaction id if it is not there (for idempotency)
        if(dto.getTxnId()==null){
            transaction.setType(TransactionType.valueOf(UUID.randomUUID().toString()));
        }
        // Save transaction
        Transaction saved = transactionRepository.save(transaction);

        // Update user's balance
        updateUserBalance(saved, true);

        return mapToResponseDTO(saved);
    }

    // UPDATE
    @Transactional
    public TransactionResponseDTO updateTransaction(Long id, TransactionDTO dto, String authHeader) throws Exception {
        Transaction existing = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        // Remove previous effect
        updateUserBalance(existing, false);

        // Map DTO to entity
        Transaction updated = mapToEntity(dto);
        updated.setUserUid(existing.getUserUid());
        updated.setEmail(existing.getEmail());
        updated.setTxnId(existing.getTxnId());

        Transaction saved = transactionRepository.save(updated);

        // Apply updated effect
        updateUserBalance(saved, true);

        return mapToResponseDTO(saved);
    }

    // DELETE
    @Transactional
    public void deleteTransaction(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        updateUserBalance(transaction, false);
        transactionRepository.deleteById(id);
    }

    // ------------------- GET METHODS -------------------

    public List<TransactionResponseDTO> getTransactionsByUser(String authHeader) throws Exception {
        String uid = FirebaseTokenUtil.getUidFromToken(authHeader);
        return transactionRepository.findByUserUid(uid)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<TransactionResponseDTO> searchByCategory(String authHeader, String category) throws Exception {
        String uid = FirebaseTokenUtil.getUidFromToken(authHeader);
        return transactionRepository.findByUserUidAndCategoriesContaining(uid, category)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<TransactionResponseDTO> filterByType(String authHeader, TransactionType type) throws Exception {
        String uid = FirebaseTokenUtil.getUidFromToken(authHeader);
        return transactionRepository.findByUserUidAndType(uid, type)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<TransactionResponseDTO> getRecentTransactions(String authHeader, int limit) throws Exception {
        String uid = FirebaseTokenUtil.getUidFromToken(authHeader);
        return transactionRepository.findByUserUidOrderByCreatedAtDesc(uid, Pageable.ofSize(limit))
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }
    public List<TransactionResponseDTO> getCurrentMonthTransaction(String authHeader) throws Exception {
        String uid = FirebaseTokenUtil.getUidFromToken(authHeader);

        // Define start and end of month
        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfMonth = LocalDate.now()
                .withDayOfMonth(LocalDate.now().lengthOfMonth())
                .atTime(23, 59, 59);

        Pageable pageable = PageRequest.of(0, 30);

        List<Transaction> transactions = transactionRepository
                .findByUserUidAndCreatedAtBetweenOrderByCreatedAtDesc(uid, startOfMonth, endOfMonth, pageable);
        System.out.println(transactions);
        return transactions.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    //Getting the main category from my dummy data
//    private final Map<String, String> subToMain = new HashMap<>() {{
//        put("biriyani" , "Food");
//        put("lunch", "Food");
//        put("dinner", "Food");
//        put("breakfast", "Food");
//        put("snacks", "Food");
//        put("chips", "Food");
//        put("biscuit", "Food");
//        put("chicken", "Food");
//        put("petrol", "Travel");
//        put("uber", "Travel");
//        put("movie", "Entertainment");
//        put("netflix", "Entertainment");
//        put("amazon", "Shopping");
//        put("shirt", "Shopping");
//        put("electricity bill", "Bills");
//        put("wifi", "Bills");
//    }};
//    public String getMainCatfromData(List<String> categoriesList){
//        Map<String ,Long> mainCount=new HashMap<>();
//        for(String subCat : categoriesList){
//            String mainCat=subToMain.getOrDefault(subCat.toLowerCase() , "Uncategorized");
//            mainCount.put(mainCat , mainCount.getOrDefault(mainCat , 0L)+1);
//        }
//        return mainCount.entrySet()
//                .stream()
//                .max(Map.Entry.comparingByValue())
//                .map(Map.Entry::getKey)
//                .orElse("Uncategorized");
//    }

}

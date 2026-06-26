package com.smartspend.backend.Service;

import com.smartspend.backend.Repository.MandatoryExpenseRepository;
import com.smartspend.backend.Repository.TransactionRepository;
import com.smartspend.backend.Repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteUserService {
   private final MandatoryExpenseRepository mandatoryExpenseRepository;
   private final UserRepository userRepository;
   private final TransactionRepository transactionRepository;

   @Transactional
    public void deleteUserCompletely(String uid){
       mandatoryExpenseRepository.deleteByUid(uid);
       userRepository.deleteByUid(uid);
       transactionRepository.deleteByUserUid(uid);
   }
}

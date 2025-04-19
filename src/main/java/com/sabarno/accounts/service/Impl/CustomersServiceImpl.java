package com.sabarno.accounts.service.Impl;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.sabarno.accounts.dto.AccountsDto;
import com.sabarno.accounts.dto.CardsDto;
import com.sabarno.accounts.dto.CustomerDetailsDto;
import com.sabarno.accounts.dto.LoansDto;
import com.sabarno.accounts.entity.Accounts;
import com.sabarno.accounts.entity.Customer;
import com.sabarno.accounts.exception.ResourceNotFoundException;
import com.sabarno.accounts.mapper.AccountsMapper;
import com.sabarno.accounts.mapper.CustomerMapper;
import com.sabarno.accounts.repository.AccountsRepository;
import com.sabarno.accounts.repository.CustomerRepository;
import com.sabarno.accounts.service.ICustomersService;
import com.sabarno.accounts.service.client.CardsFeignClient;
import com.sabarno.accounts.service.client.LoansFeignClient;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CustomersServiceImpl implements ICustomersService {

    private AccountsRepository accountsRepository;
    private CustomerRepository customerRepository;
    private CardsFeignClient cardsFeignClient;
    private LoansFeignClient loansFeignClient;

    /**
     * @param mobileNumber - Input Mobile Number
     * @return Customer Details based on a given mobileNumber
     */
    @Override
    public CustomerDetailsDto fetchCustomerDetails(String mobileNumber, String correlationId) {
        Customer customer = customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber)
        );
        Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId()).orElseThrow(
                () -> new ResourceNotFoundException("Account", "customerId", customer.getCustomerId().toString())
        );

        CustomerDetailsDto customerDetailsDto = CustomerMapper.mapToCustomerDetailsDto(customer, new CustomerDetailsDto());
        customerDetailsDto.setAccountsDto(AccountsMapper.mapToAccountsDto(accounts, new AccountsDto()));

        ResponseEntity<LoansDto> loansDtoResponseEntity = loansFeignClient.fetchLoanDetails(correlationId, mobileNumber);
        if(loansDtoResponseEntity != null) customerDetailsDto.setLoansDto(loansDtoResponseEntity.getBody());

        ResponseEntity<CardsDto> cardsDtoResponseEntity = cardsFeignClient.fetchCardDetails(correlationId, mobileNumber);
        if(cardsDtoResponseEntity != null) customerDetailsDto.setCardsDto(cardsDtoResponseEntity.getBody());

        return customerDetailsDto;

    }
}

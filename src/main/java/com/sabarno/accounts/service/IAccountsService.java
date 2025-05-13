package com.sabarno.accounts.service;

import com.sabarno.accounts.dto.CustomerDto;

public interface IAccountsService {

  void createAccount(CustomerDto customerDto);

  CustomerDto fetchAccount(String mobileNumber);

  boolean updateAccount(CustomerDto customerDto);

  boolean deleteAccount(String mobileNumber);

  boolean updateCommunicationStatus(Long accountNumber);
}

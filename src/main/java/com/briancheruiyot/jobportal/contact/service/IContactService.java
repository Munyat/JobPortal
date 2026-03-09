package com.briancheruiyot.jobportal.contact.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.briancheruiyot.jobportal.dto.ContactRequestDto;
import com.briancheruiyot.jobportal.dto.ContactResponseDto;

public interface IContactService {

    boolean saveContact(ContactRequestDto contactRequestDto);

    List<ContactResponseDto> fetchNewContactMsgs();

    List<ContactResponseDto> fetchNewContactMsgsWithSort(String sortBy, String sortDir);

    Page<ContactResponseDto> fetchNewContactMsgsWithPaginationAndSort(int pageNumber, int pageSize,
            String sortBy, String sortDir);

    boolean closeContactMsg(Long id, String status);
}

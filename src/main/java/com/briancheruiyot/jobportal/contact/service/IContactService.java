package com.briancheruiyot.jobportal.contact.service;

import com.briancheruiyot.jobportal.dto.ContactRequestDto;

public interface IContactService {

    boolean saveContact(ContactRequestDto contactRequestDto);

}

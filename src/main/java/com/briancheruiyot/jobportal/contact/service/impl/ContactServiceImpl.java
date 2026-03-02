package com.briancheruiyot.jobportal.contact.service.impl;

import com.briancheruiyot.jobportal.contact.service.IContactService;
import com.briancheruiyot.jobportal.dto.ContactRequestDto;
import com.briancheruiyot.jobportal.entity.Contact;
import com.briancheruiyot.jobportal.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ContactServiceImpl implements IContactService {

    private final ContactRepository contactRepository;

    @Override
    public boolean saveContact(ContactRequestDto contactRequestDto) {
        boolean result = false;
        Contact contact = contactRepository.save(transformToEntity(contactRequestDto));
        if (contact != null && contact.getId() != null) {
            result = true;
        }
        return result;
    }

    private Contact transformToEntity(ContactRequestDto contactRequestDto) {
        Contact contact = new Contact();
        BeanUtils.copyProperties(contactRequestDto, contact);
        // contact.setCreatedAt(Instant.now());
        // contact.setCreatedBy("System");
        contact.setStatus("NEW");
        return contact;
    }
}

package com.briancheruiyot.jobportal.repository;

import com.briancheruiyot.jobportal.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactRepository extends JpaRepository<Contact, Long> {
}
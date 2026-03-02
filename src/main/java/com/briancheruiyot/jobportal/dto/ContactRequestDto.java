package com.briancheruiyot.jobportal.dto;

import java.io.Serializable;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ContactRequestDto(
        @NotBlank(message = "Email can not be Empty") @Email(message = "Invalid email address") String email,
        @NotBlank(message = "Message can not be Empty") @Size(min = 5, max = 500, message = "Message must be between 5 and 500 characters") String message,
        @NotBlank(message = "name can not be Empty") @Size(min = 5, max = 30, message = "Name must be between 5 and 30 characters") String name,
        @NotBlank(message = "subject can not be Empty") @Size(min = 5, max = 150, message = "Subject must be between 5 and 150 characters") String subject,
        @NotBlank(message = "userType can not be Empty") @Pattern(regexp = "Job Seeker|Employer|Other", message = "UserType must be one of: Job Seeker,Employer,Other") String userType)
        implements Serializable {
}
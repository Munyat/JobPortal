package com.briancheruiyot.jobportal.util;

import com.briancheruiyot.jobportal.constants.ApplicationConstants;
import com.briancheruiyot.jobportal.dto.JobApplicationDto;
import com.briancheruiyot.jobportal.dto.JobDto;
import com.briancheruiyot.jobportal.dto.ProfileDto;
import com.briancheruiyot.jobportal.entity.Job;
import com.briancheruiyot.jobportal.entity.JobApplication;
import com.briancheruiyot.jobportal.entity.JobPortalUser;
import com.briancheruiyot.jobportal.entity.Profile;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class ApplicationUtility {

    public static String getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
                authentication.getPrincipal().equals("anonymousUser")) {
            return ApplicationConstants.SYSTEM;
        }
        Object principal = authentication.getPrincipal();
        String username;
        if (principal instanceof JobPortalUser jobPortalUser) {
            username = jobPortalUser.getEmail();
        } else {
            username = principal.toString(); // fallback
        }
        return username;
    }

    public static JobDto transformJobToDto(Job job) {
        return new JobDto(
                job.getId(),
                job.getTitle(),
                job.getCompany().getId(),
                job.getCompany().getName(),
                job.getCompany().getLogo(),
                job.getLocation(),
                job.getWorkType(),
                job.getJobType(),
                job.getCategory(),
                job.getExperienceLevel(),
                job.getSalaryMin(),
                job.getSalaryMax(),
                job.getSalaryCurrency(),
                job.getSalaryPeriod(),
                job.getDescription(),
                job.getRequirements(),
                job.getBenefits(),
                job.getPostedDate(),
                job.getApplicationDeadline(),
                job.getApplicationsCount(),
                job.getFeatured(),
                job.getUrgent(),
                job.getRemote(),
                job.getStatus());
    }

    public static JobApplicationDto mapToJobApplicationDto(JobApplication application) {
        // Map profile if exists
        ProfileDto profileDto = null;
        Profile profile = application.getUser().getProfile();
        if (profile != null) {
            profileDto = new ProfileDto(
                    profile.getId(),
                    profile.getUser().getId(),
                    profile.getJobTitle(),
                    profile.getLocation(),
                    profile.getExperienceLevel(),
                    profile.getProfessionalBio(),
                    profile.getPortfolioWebsite(),
                    profile.getProfilePicture(),
                    profile.getProfilePictureName(),
                    profile.getProfilePictureType(),
                    profile.getResume(),
                    profile.getResumeName(),
                    profile.getResumeType(),
                    profile.getCreatedAt(),
                    profile.getUpdatedAt());
        }
        return new JobApplicationDto(
                application.getId(),
                application.getUser().getId(),
                application.getUser().getName(),
                application.getUser().getEmail(),
                application.getUser().getMobileNumber(),
                profileDto,
                ApplicationUtility.transformJobToDto(application.getJob()),
                application.getAppliedAt(),
                application.getStatus(),
                application.getCoverLetter(),
                application.getNotes());
    }
}
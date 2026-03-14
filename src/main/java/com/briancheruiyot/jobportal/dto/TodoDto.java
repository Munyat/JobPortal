package com.briancheruiyot.jobportal.dto;

public record TodoDto(Long userId, Long id, String title, boolean completed) {
}
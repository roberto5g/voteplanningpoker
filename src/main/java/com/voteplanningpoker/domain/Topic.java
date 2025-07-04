package com.voteplanningpoker.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Topic {
    private UUID id;
    private String title;
    private TopicStatus status;
    private List<Vote> votes;
    private boolean votesRevealed = false;
    private Double average;
    private Integer suggested;
}

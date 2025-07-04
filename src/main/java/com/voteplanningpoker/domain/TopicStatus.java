package com.voteplanningpoker.domain;

public enum TopicStatus {
    OPEN("OPEN"),
    CLOSED("CLOSED");

    private final String status;
    TopicStatus(String status) {
        this.status = status;
    }
    public String getStatus() {
        return status;
    }

    public static TopicStatus fromString(String status) {
        for (TopicStatus ts : TopicStatus.values()) {
            if (ts.status.equalsIgnoreCase(status)) {
                return ts;
            }
        }
        throw new IllegalArgumentException("Unknown status: " + status);
    }
}

package com.example.grapefield.events.model.entity;

import lombok.Getter;

@Getter
public enum TicketVendor {
    INTERPARK("인터파크"),
    YES24("예스24"),
    MELON("멜론티켓"),
    TICKETLINK("티켓링크"),
    TICKETBAY("티켓베이");

    private final String vendorName;

    TicketVendor(String vendorName) {
        this.vendorName = vendorName;
    }
}

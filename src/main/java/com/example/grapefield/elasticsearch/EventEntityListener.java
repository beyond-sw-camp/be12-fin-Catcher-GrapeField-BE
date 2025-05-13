package com.example.grapefield.elasticsearch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EventEntityListener {

    private EventSearchService searchService;

    @Autowired
    public void setSearchService(EventSearchService searchService) {
        this.searchService = searchService;
    }
}
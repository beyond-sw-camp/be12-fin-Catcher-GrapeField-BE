package com.example.grapefield.elasticsearch;

import com.example.grapefield.events.model.entity.Events;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EventEntityListener {

    private EventSearchService searchService;

    @Autowired
    public void setSearchService(EventSearchService searchService) {
        this.searchService = searchService;
    }

    @PostPersist
    @PostUpdate
    public void afterSave(Events event) {
        if (searchService != null) {
            try {
                searchService.indexEvent(event);
                System.out.println("Event indexed successfully: " + event.getIdx());
            } catch (Exception e) {
                System.err.println("Error indexing event: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @PostRemove
    public void afterDelete(Events event) {
        if (searchService != null) {
            try {
                searchService.deleteEventDocument(event.getIdx());
                System.out.println("Event deleted from index: " + event.getIdx());
            } catch (Exception e) {
                System.err.println("Error deleting event from index: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
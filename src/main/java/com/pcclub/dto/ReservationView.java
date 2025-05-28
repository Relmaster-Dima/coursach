package com.pcclub.dto;

import com.pcclub.model.ResourceType;
import com.pcclub.model.ReservationStatus;
import java.time.LocalDateTime;

public class ReservationView {
    private Integer id;
    private ResourceType resourceType;
    private Integer resourceId;
    private String resourceName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private ReservationStatus status;

    public ReservationView(Integer id,
                           ResourceType resourceType,
                           Integer resourceId,
                           String resourceName,
                           LocalDateTime startTime,
                           LocalDateTime endTime,
                           ReservationStatus status) {
        this.id = id;
        this.resourceType = resourceType;
        this.resourceId = resourceId;
        this.resourceName = resourceName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }

    // геттеры (и сеттеры, если нужны)
    public Integer getId() { return id; }
    public ResourceType getResourceType() { return resourceType; }
    public Integer getResourceId()   { return resourceId;   }
    public String getResourceName()  { return resourceName;  }
    public LocalDateTime getStartTime() { return startTime;  }
    public LocalDateTime getEndTime()   { return endTime;    }
    public ReservationStatus getStatus() { return status;    }
}

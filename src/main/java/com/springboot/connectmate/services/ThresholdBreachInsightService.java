package com.springboot.connectmate.services;

import com.springboot.connectmate.dtos.Metric.ThresholdBreachInsightDTO;
import com.springboot.connectmate.enums.Status;

import java.util.List;

public interface ThresholdBreachInsightService {
    List<ThresholdBreachInsightDTO> getInsightsByStatus(Status status);
    List<ThresholdBreachInsightDTO> getInsightsByConnectItemId(String connectItemId);
    // Additional methods for other attributes
}

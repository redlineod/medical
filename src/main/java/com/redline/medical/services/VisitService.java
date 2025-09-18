package com.redline.medical.services;

import com.redline.medical.dto.VisitCreateRequest;
import com.redline.medical.dto.VisitCreateResponse;

public interface VisitService {
    VisitCreateResponse createVisit(VisitCreateRequest request);
}

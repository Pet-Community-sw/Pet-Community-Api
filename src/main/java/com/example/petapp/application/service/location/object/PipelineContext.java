package com.example.petapp.application.service.location.object;

import com.example.petapp.application.in.location.dto.request.LocationMessage;
import io.reactivex.rxjava3.subjects.Subject;

public record PipelineContext(String memberId, Subject<LocationMessage> subject) {
}

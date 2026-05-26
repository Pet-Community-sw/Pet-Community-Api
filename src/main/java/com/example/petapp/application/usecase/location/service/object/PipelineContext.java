package com.example.petapp.application.usecase.location.service.object;

import com.example.petapp.application.usecase.location.dto.request.LocationMessage;
import io.reactivex.rxjava3.subjects.Subject;

public record PipelineContext(String memberId, Subject<LocationMessage> subject) {
}

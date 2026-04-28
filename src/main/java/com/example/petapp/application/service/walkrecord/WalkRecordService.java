package com.example.petapp.application.service.walkrecord;

import com.example.petapp.application.common.DistanceUtil;
import com.example.petapp.application.in.location.LocationUseCase;
import com.example.petapp.application.in.member.MemberUseCase;
import com.example.petapp.application.in.notification.dto.NotificationEvent;
import com.example.petapp.application.in.walkrecord.WalkRecordUseCase;
import com.example.petapp.application.in.walkrecord.dto.response.CreateWalkRecordResponseDto;
import com.example.petapp.application.in.walkrecord.dto.response.GetWalkRecordLocationResponseDto;
import com.example.petapp.application.in.walkrecord.dto.response.GetWalkRecordResponseDto;
import com.example.petapp.application.in.walkrecord.mapper.WalkRecordMapper;
import com.example.petapp.application.out.cache.LocationCachePort;
import com.example.petapp.domain.member.model.Member;
import com.example.petapp.domain.post.model.DelegateWalkPost;
import com.example.petapp.domain.walkrecord.WalkRecordRepository;
import com.example.petapp.domain.walkrecord.model.WalkRecord;
import com.example.petapp.domain.walkrecord.model.WalkStatus;
import com.example.petapp.interfaces.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WalkRecordService implements WalkRecordUseCase {

    private final WalkRecordRepository walkRecordRepository;
    private final MemberUseCase memberUseCase;
    private final LocationCachePort port;
    private final ApplicationEventPublisher eventPublisher;
    private final LocationUseCase locationUseCase;

    @Transactional
    @Override
    public CreateWalkRecordResponseDto createWalkRecord(DelegateWalkPost delegateWalkPost) {
        Member member = memberUseCase.findOrThrow(delegateWalkPost.getSelectedApplicantMemberId());
        WalkRecord walkRecord = WalkRecordMapper.toEntity(delegateWalkPost, member);
        WalkRecord savedWalkRecord = walkRecordRepository.save(walkRecord);

        eventPublisher.publishEvent(new NotificationEvent(member.getId(), "산책 권한이 부여 되었습니다."));

        return new CreateWalkRecordResponseDto(savedWalkRecord.getId());
    }

    @Override
    public GetWalkRecordResponseDto getWalkRecord(Long walkRecordId, Long id) {
        WalkRecord walkRecord = findOrThrow(walkRecordId);
        return WalkRecordMapper.toGetWalkRecordResponseDto(walkRecord);
    }

    @Override
    public GetWalkRecordLocationResponseDto getWalkRecordLocation(Long walkRecordId, Long id) {
        Member member = memberUseCase.findOrThrow(id);
        WalkRecord walkRecord = findOrThrow(walkRecordId);
        walkRecord.validateMember(member.getId());
        return new GetWalkRecordLocationResponseDto(port.find(walkRecordId));
    }

    @Transactional
    @Override
    public void updateStartWalkRecord(Long walkRecordId, Long id) {
        Member member = memberUseCase.findOrThrow(id);
        WalkRecord walkRecord = findOrThrow(walkRecordId);
        walkRecord.validateMember(member.getId());
        walkRecord.updateWalkStatus(WalkStatus.START);

        eventPublisher.publishEvent(new NotificationEvent(walkRecord.getDelegateWalkPost().getProfile().getMember().getId(),
                member.getName() + "님이 산책을 시작하였습니다."));
    }

    @Transactional
    @Override
    public void finishWalkRecord(Long walkRecordId, Long id) {
        Member member = memberUseCase.findOrThrow(id);
        WalkRecord walkRecord = findOrThrow(walkRecordId);
        walkRecord.validateMember(member.getId());
        walkRecord.updateWalkStatus(WalkStatus.FINISH);

        locationUseCase.finishWalkRecord(walkRecordId); //locationPipeline 종료
        eventPublisher.publishEvent(new NotificationEvent(walkRecord.getDelegateWalkPost().getProfile().getMember().getId(),
                member.getName() + "님이 산책을 마쳤습니다. 후기를 작성해주세요."));

        updateWalkRecordPathData(walkRecordId, walkRecord);
    }

    private void updateWalkRecordPathData(Long walkRecordId, WalkRecord walkRecord) {
        List<String> paths = port.findList(walkRecordId);

        Double totalDistance = DistanceUtil.calculateTotalDistance(paths);
        walkRecord.updateRecordToPath(totalDistance, paths);
        port.delete(walkRecordId);
    }

    @Transactional(readOnly = true)
    @Override
    public WalkRecord findOrThrow(Long id) {
        return walkRecordRepository.find(id).orElseThrow(() -> new NotFoundException("해당 산책기록은 없습니다."));
    }

    @Transactional(readOnly = true)
    @Override
    public WalkRecord findAndValidate(Long id, Long memberId) {
        WalkRecord walkRecord = findOrThrow(id);
        walkRecord.validateMember(memberId);
        walkRecord.validateStart();
        return walkRecord;
    }

}

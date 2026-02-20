package com.example.petapp.application.service.walkrecord;

import com.example.petapp.application.common.DistanceUtil;
import com.example.petapp.application.in.location.LocationUseCase;
import com.example.petapp.application.in.member.MemberQueryUseCase;
import com.example.petapp.application.in.notification.dto.NotificationEvent;
import com.example.petapp.application.in.walkrecord.WalkRecordQueryUseCase;
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
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WalkRecordService implements WalkRecordUseCase {

    private final WalkRecordRepository walkRecordRepository;
    private final WalkRecordQueryUseCase walkRecordQueryUseCase;
    private final MemberQueryUseCase memberQueryUseCase;
    private final LocationCachePort port;
    private final ApplicationEventPublisher eventPublisher;
    private final LocationUseCase locationUseCase;

    @Transactional
    @Override
    public CreateWalkRecordResponseDto createWalkRecord(DelegateWalkPost delegateWalkPost) {
        Member member = memberQueryUseCase.findOrThrow(delegateWalkPost.getSelectedApplicantMemberId());
        WalkRecord walkRecord = WalkRecordMapper.toEntity(delegateWalkPost, member);
        WalkRecord savedWalkRecord = walkRecordRepository.save(walkRecord);

        eventPublisher.publishEvent(new NotificationEvent(member.getId(), "산책 권한이 부여 되었습니다."));

        return new CreateWalkRecordResponseDto(savedWalkRecord.getId());
    }

    @Transactional(readOnly = true)
    @Override
    public GetWalkRecordResponseDto getWalkRecord(Long walkRecordId, Long id) {
        WalkRecord walkRecord = walkRecordQueryUseCase.findOrThrow(walkRecordId);
        return WalkRecordMapper.toGetWalkRecordResponseDto(walkRecord);
    }

    @Transactional(readOnly = true)
    @Override
    public GetWalkRecordLocationResponseDto getWalkRecordLocation(Long walkRecordId, Long id) {
        Member member = memberQueryUseCase.findOrThrow(id);
        WalkRecord walkRecord = walkRecordQueryUseCase.findOrThrow(walkRecordId);
        walkRecord.validateMember(member.getId());
        return new GetWalkRecordLocationResponseDto(port.find(walkRecordId));
    }

    @Transactional
    @Override
    public void updateStartWalkRecord(Long walkRecordId, Long id) {
        Member member = memberQueryUseCase.findOrThrow(id);
        WalkRecord walkRecord = walkRecordQueryUseCase.findOrThrow(walkRecordId);
        walkRecord.validateMember(member.getId());
        walkRecord.updateWalkStatus(WalkStatus.START);

        eventPublisher.publishEvent(new NotificationEvent(walkRecord.getDelegateWalkPost().getProfile().getMember().getId(),
                member.getName() + "님이 산책을 시작하였습니다."));
    }

    @Transactional//분리를 어떻게 시키면 졸을까
    @Override
    public void finishWalkRecord(Long walkRecordId, Long id) {
        Member member = memberQueryUseCase.findOrThrow(id);
        WalkRecord walkRecord = walkRecordQueryUseCase.findOrThrow(walkRecordId);
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

    //todo : delete 있어야함.
}

package com.example.petapp.application.service.walkrecord;

import com.example.petapp.application.in.member.MemberQueryUseCase;
import com.example.petapp.application.in.walkrecord.WalkRecordQueryUseCase;
import com.example.petapp.application.in.walkrecord.WalkRecordUseCase;
import com.example.petapp.application.in.walkrecord.dto.response.CreateWalkRecordResponseDto;
import com.example.petapp.application.in.walkrecord.dto.response.GetWalkRecordResponseDto;
import com.example.petapp.application.in.walkrecord.mapper.WalkRecordMapper;
import com.example.petapp.common.aop.annotation.Notification;
import com.example.petapp.common.base.util.DistanceUtil;
import com.example.petapp.domain.member.model.Member;
import com.example.petapp.domain.post.model.DelegateWalkPost;
import com.example.petapp.domain.walklocation.model.dto.response.GetWalkRecordLocationResponseDto;
import com.example.petapp.domain.walkrecord.WalkRecordRepository;
import com.example.petapp.domain.walkrecord.model.WalkRecord;
import com.example.petapp.port.InMemoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WalkRecordService implements WalkRecordUseCase {

    private final WalkRecordRepository walkRecordRepository;
    private final WalkRecordQueryUseCase walkRecordQueryUseCase;
    private final MemberQueryUseCase memberQueryUseCase;
    private final InMemoryService inMemoryService;

    @Notification(recipient = "@queryService.findByMember(#p0.selectedApplicantMemberId).member", message = "산책 권한이 부여 되었습니다.")
    @Transactional
    @Override
    public CreateWalkRecordResponseDto createWalkRecord(DelegateWalkPost delegateWalkPost) {
        Member member = memberQueryUseCase.findOrThrow(delegateWalkPost.getSelectedApplicantMemberId());
        WalkRecord walkRecord = WalkRecordMapper.toEntity(delegateWalkPost, member);
        WalkRecord savedWalkRecord = walkRecordRepository.save(walkRecord);
        return new CreateWalkRecordResponseDto(savedWalkRecord.getId());
    }

    @Transactional(readOnly = true)
    @Override
    public GetWalkRecordResponseDto getWalkRecord(Long walkRecordId, String email) {
        WalkRecord walkRecord = walkRecordQueryUseCase.findOrThrow(walkRecordId);
        return WalkRecordMapper.toGetWalkRecordResponseDto(walkRecord);
    }

    @Transactional(readOnly = true)
    @Override
    public GetWalkRecordLocationResponseDto getWalkRecordLocation(Long walkRecordId, String email) {
        Member member = memberQueryUseCase.findOrThrow(email);
        WalkRecord walkRecord = walkRecordQueryUseCase.findOrThrow(walkRecordId);
        walkRecord.validateMember(member);
        return new GetWalkRecordLocationResponseDto(inMemoryService.getLocationData(walkRecordId));
    }

    @Notification(recipient = "@queryServiceImpl.findByWalkRecord(#p0).delegateWalkPost.profile.member",
            message = "@queryServiceImpl.findByWalkRecord(#p0).member.name+'님이 산책을 시작하였습니다.'")
    @Transactional
    @Override
    public void updateStartWalkRecord(Long walkRecordId, String email) {
        Member member = memberQueryUseCase.findOrThrow(email);
        WalkRecord walkRecord = walkRecordQueryUseCase.findOrThrow(walkRecordId);
        walkRecord.validateMember(member.getId());
        walkRecord.updateWalkStatus(WalkRecord.WalkStatus.START);
    }

    @Notification(recipient = "@queryServiceImpl.findByWalkRecord(#p0).delegateWalkPost.profile.member",
            message = "@queryServiceImpl.findByWalkRecord(#p0).member.name+'님이 산책을 마쳤습니다. 후기를 작성해주세요.'")
    @Transactional//분리를 어떻게 시키면 졸을까
    @Override
    public void FinishWalkRecord(Long walkRecordId, String email) {
        Member member = memberQueryUseCase.findOrThrow(email);
        WalkRecord walkRecord = walkRecordQueryUseCase.findOrThrow(walkRecordId);
        walkRecord.validateMember(member.getId());
        walkRecord.updateWalkStatus(WalkRecord.WalkStatus.FINISH);

        updateWalkRecordPathData(walkRecordId, walkRecord);
    }

    private void updateWalkRecordPathData(Long walkRecordId, WalkRecord walkRecord) {
        List<String> paths = inMemoryService.getLocationDatas(walkRecordId);

        Double totalDistance = DistanceUtil.calculateTotalDistance(paths);
        walkRecord.updateRecordToPath(totalDistance, paths);
        inMemoryService.deleteLocationData(walkRecordId);
    }

    //todo : delete 있어야함.
}

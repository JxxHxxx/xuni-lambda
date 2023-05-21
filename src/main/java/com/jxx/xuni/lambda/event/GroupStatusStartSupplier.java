package com.jxx.xuni.lambda.event;

import com.jxx.xuni.lambda.group.domain.Group;
import com.jxx.xuni.lambda.group.domain.GroupRepository;
import com.jxx.xuni.lambda.group.domain.StudyProductDto;
import com.jxx.xuni.lambda.studyproduct.domain.StudyProduct;
import com.jxx.xuni.lambda.studyproduct.query.StudyProductReadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Supplier;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
public class GroupStatusStartSupplier implements Supplier<GroupStatusResponse> {

    private final GroupRepository groupRepository;
    private final StudyProductReadRepository studyProductReadRepository;
    private static Long BATCH_NO = 0l;
    @Override
    public GroupStatusResponse get() {
        log.info("[BEGIN BATCH JOB] : GROUP UPDATE TO START STATUS");
        // 금일 배치 돌려야 될 데이터 가져오기
        List<Group> groups = groupRepository.findBatchCond();
        for (Group group : groups) {
            // 해당 그룹의 스터디 목차 가져오기
            StudyProduct studyProduct = studyProductReadRepository.readDetailWithFetch(group.getStudy().getId());

            List<StudyProductDto> studyProductDtos = studyProduct.getStudyProductDetail().stream()
                    .map(spd -> new StudyProductDto(spd.getChapterId(), spd.getTitle()))
                    .toList();
            //그롭 멤버 식별자 가져오기
            List<Long> identifiers = group.getGroupMembers().stream().map(g -> g.getGroupMemberId()).toList();
            //식별자 마다 Group Task 업데이트
            for (Long identifier : identifiers) {
                group.update(identifier, studyProductDtos);
            }
            log.info("[NO. {}][GROUP ID : {}][GROUP STUDY PRODUCT ID : {}]", BATCH_NO++, group.getId(), group.getStudy().getId());
        }

        // 그룹 상태 시작으로 변경
        groupRepository.updateGroupStatusToStart();
        log.info("[END BATCH JOB] : GROUP UPDATE TO START STATUS");
        log.info("THANK YOU");
        return new GroupStatusResponse(true);
    }
}

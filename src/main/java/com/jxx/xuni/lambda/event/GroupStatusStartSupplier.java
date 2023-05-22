package com.jxx.xuni.lambda.event;

import com.jxx.xuni.lambda.group.domain.Group;
import com.jxx.xuni.lambda.group.domain.GroupRepository;
import com.jxx.xuni.lambda.studyproduct.domain.StudyProductDetail;
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
public class GroupStatusStartSupplier implements Supplier<GroupStatusResponse> {

    private final GroupRepository groupRepository;
    private final StudyProductReadRepository studyProductReadRepository;
    private static Long BATCH_NO = 1l;

    @Override
    @Transactional
    public GroupStatusResponse get() {
        log.info("[BEGIN BATCH JOB] : [GROUP UPDATE TO START STATUS]");
        List<Group> groups = groupRepository.findGroupToChangeStartStatus();
        log.info("[TODAY JOB QUANTITY : {}]" , groups.size());
        for (Group group : groups) {
            List<Long> members = receiveMemberIdOf(group);
            List<StudyProductDetail> details = studyProductReadRepository.readProduct(group.getStudyProductId()).getDetails();

            for (Long member : members) {
                group.initGroupTask(member, details);
            }
            log.info("[BATCH JOB NO. {}][GROUP ID : {}][GROUP STUDY PRODUCT ID : {}]", BATCH_NO++, group.getId(), group.getStudyProductId());
        }

        groupRepository.updateGroupStatusToStart();
        log.info("[COMPLETE BATCH JOB] : [GROUP UPDATE TO START STATUS] THANK YOU >.<");
        return new GroupStatusResponse(true);
    }

    private List<Long> receiveMemberIdOf(Group group) {
        return group.getGroupMembers().stream().map(g -> g.getGroupMemberId()).toList();
    }
}

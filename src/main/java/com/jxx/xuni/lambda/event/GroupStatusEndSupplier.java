package com.jxx.xuni.lambda.event;

import com.jxx.xuni.lambda.group.domain.GroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

/** Q. 이 배치 작업이 왜 필요하나요?
 *  A. 그룹은 상태에 따라 할 수 있는 행위 및 조회의 대상이 되기도 됩니다.
 *  따라서 START 상태인 그룹을 적절한 시기에 END로 변경해야 합니다.
 *  xuni 에서는 그룹 종료일로부터 4일이 지난 그룹을 자동으로 END 상태로 만들기로 하였습니다.
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class GroupStatusEndSupplier implements Supplier<GroupStatusResponse> {

    private final GroupRepository groupRepository;

    @Override
    public GroupStatusResponse get() {
        log.info("[BEGIN BATCH JOB] : [GROUP UPDATE TO END STATUS]");
        groupRepository.updateGroupStatusToEnd();
        log.info("[COMPLETE BATCH JOB] : [GROUP UPDATE TO END STATUS] THANK YOU >.<");
        return new GroupStatusResponse(true);
    }
}

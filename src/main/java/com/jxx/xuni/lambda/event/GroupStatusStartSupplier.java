package com.jxx.xuni.lambda.event;

import com.jxx.xuni.lambda.group.domain.GroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Slf4j
@Component
@RequiredArgsConstructor
public class GroupStatusStartSupplier implements Supplier<GroupStatusResponse> {

    private final GroupRepository groupRepository;

    @Override
    public GroupStatusResponse get() {
        groupRepository.updateGroupStatusToStart();
        log.info("xuni - update group ststus to start status");
        return new GroupStatusResponse(true);
    }
}

package com.jxx.xuni.lambda.event;

import com.jxx.xuni.lambda.group.domain.*;
import com.jxx.xuni.lambda.studyproduct.domain.StudyProductRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static com.jxx.xuni.lambda.group.domain.GroupStatus.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class GroupStatusEndSupplierTest {

    @Autowired
    GroupStatusEndSupplier groupStatusEndSupplier;
    @Autowired
    GroupRepository groupRepository;

    private Group createGroup(long minusDays) {
        Group group = new Group(Period.of(LocalDate.now(), LocalDate.now().minusDays(minusDays)),
                Time.of(LocalTime.MIDNIGHT, LocalTime.NOON),
                new Capacity(5),
                Study.of("study-product-id", "자바의 정석", Category.JAVA),
                new Host(1l, "host"));

        return group;
    }

    @BeforeEach
    void beforeEach() {
        ArrayList<Group> groups = new ArrayList<>();
        for (long minusDay = 0; minusDay < 5; minusDay++) {
            Group group1 = createGroup(minusDay);
            group1.changeGroupStatusTo(GroupStatus.START);

            Group group2 = createGroup(minusDay);
            group2.changeGroupStatusTo(GroupStatus.GATHER_COMPLETE);

            Group group3 = createGroup(minusDay);

            groups.addAll(List.of(group1, group2, group3));
        }

        groupRepository.saveAll(groups);
    }

    @DisplayName("groupStatusEndSupplier 배치 작업이 수행되는 그룹은 " +
            "1. period.endDate 금일에서 -3입니다. (UTC 타임존을 사용해서 9시간 느리기 때문에 -3 을 해야 된다.) " +
            "2. groupStatus 는 END 로 변경된다.")
    @Test
    void group_status_end_function() {
        //when
        groupStatusEndSupplier.get();

        List<Group> totalGroup = groupRepository.findAll();


        List<Group> endGroup = totalGroup.stream().filter(g -> groupEndCond(g)).toList();
        assertThat(endGroup).extracting("groupStatus").containsOnly(END);

        List<Group> notEndGroup = totalGroup.stream().filter(g -> !groupEndCond(g)).toList();
        assertThat(notEndGroup).extracting("groupStatus").doesNotContain(END);
    }

    private boolean groupEndCond(Group g) {
        return LocalDate.now().minusDays(3l).equals(g.getEndDate());
    }
}
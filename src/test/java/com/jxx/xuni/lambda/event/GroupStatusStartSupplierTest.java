package com.jxx.xuni.lambda.event;

import com.jxx.xuni.lambda.group.domain.*;
import com.jxx.xuni.lambda.studyproduct.domain.StudyProduct;
import com.jxx.xuni.lambda.studyproduct.domain.StudyProductDetail;
import com.jxx.xuni.lambda.studyproduct.domain.StudyProductRepository;
import com.jxx.xuni.lambda.studyproduct.domain.Topic;
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

import static com.jxx.xuni.lambda.group.domain.GroupStatus.START;
import static com.jxx.xuni.lambda.studyproduct.domain.Category.*;
import static org.assertj.core.api.Assertions.*;

@Transactional
@SpringBootTest
class GroupStatusStartSupplierTest {

    @Autowired
    GroupStatusStartSupplier groupStatusStartSupplier;
    @Autowired
    GroupRepository groupRepository;
    @Autowired
    StudyProductRepository studyProductRepository;

    private Group createGroup(String studyProductId, long plusDay, String hostName) {
        Group group = new Group(Period.of(LocalDate.now().plusDays(plusDay), LocalDate.now().plusDays(10l)),
                Time.of(LocalTime.MIDNIGHT, LocalTime.NOON),
                new Capacity(5),
                Study.of(studyProductId, "자바의 정석", Category.JAVA),
                new Host(1l, hostName));
        return group;
    }

    @BeforeEach
    void beforeEach() {
        //테스트를 위한 스터디 상품 및 상품 디테일 넣기
        StudyProduct studyProduct = new StudyProduct(
                "자바스터디",
                JAVA,
                Topic.of("자바의 정석", "남궁성", "IMG"));

        studyProduct.getDetails().addAll(List.of(
                new StudyProductDetail(1l, "Chapter 1 자바를 시작하기 전에"),
                new StudyProductDetail(2l, "Chapter 2 변수(Variable)"),
                new StudyProductDetail(3l, "Chapter 3 연산자(Operator)"),
                new StudyProductDetail(4l, "Chapter 4 조건문과 반복문")
        ));

        StudyProduct saveStudyProduct = studyProductRepository.save(studyProduct);
        String studyProductId = saveStudyProduct.getId();

        //테스트를 위한 그룹 생성 시작일이 금일부터 9일 후 인 그룹을 5개씩 만듬
        List<Group> groups = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            for (long plusDay= 0; plusDay < 10; plusDay++) {
                Group group = createGroup(studyProductId, plusDay,"host" + plusDay);
                groups.add(group);
            }
        }
        groupRepository.saveAll(groups);
    }

    //given - beforeEach를 통해 50개 그룹이 생성되었고 그 중 5개는 group_status_start_function에 의해 배치 작업이 수행되어야 한다.

    @DisplayName("groupStatusStartSupplier 배치 작업이 수행되는 그룹은 " +
            "1. period.startDate가 금일 + 1 이다.(람다 서버가 UTC 타임존을 사용해서 9시간 느리기 때문에 + 1 해야 한다) " +
            "2. groupStatus 는 START 로 변경된다. " +
            "3. group Task가 초기화 된다.")
    @Test
    void group_status_start_function() {
        //when
        groupStatusStartSupplier.get();

        //then
        List<Group> startGroup = groupRepository.findAll()
                .stream()
                .filter(g -> LocalDate.now().plusDays(1l).equals(g.getStartDate())).toList();

        assertThat(startGroup).extracting("groupStatus").containsOnly(START);

        List<Task> tasks = startGroup.get(0).getTasks();
        assertThat(tasks.size()).isEqualTo(4);
    }
}
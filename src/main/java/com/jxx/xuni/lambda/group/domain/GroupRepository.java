package com.jxx.xuni.lambda.group.domain;

import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/** FIXME : lambda 서버에서 실행되는 WAS 타임존이 UTC로 설정됩니다. 이로 인해 curdate() + 1, curdate() -3 작업이 추가되었습니다.
 *  타임존이 Asia/Seoul 일 경우 curdate() + 1 -> curdate() 로 수정 /curdate() - 3 -> curdate() - 4 로 수정되어야 합니다.
 *  추가로 AWS 내 트리거 실행 시간을 적절하게 변경해야 합니다.
 **/
// 시도해본 방법 - 프로젝트 디폴트 타임존 설정 - 실패

@Transactional
public interface GroupRepository extends JpaRepository<Group, Long> {

    @Lock(value = LockModeType.OPTIMISTIC)
    @Query(value = "select g from Group g where g.id = :groupId")
    Optional<Group>  readWithOptimisticLock(@Param("groupId") Long groupId);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE study_group g SET g.group_status = 'START' " +
            "WHERE g.start_date = curdate() + 1 " +
            "AND (g.group_status = 'GATHERING' OR g.group_status = 'GATHER_COMPLETE')",
            nativeQuery = true)
    void updateGroupStatusToStart();

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE study_group g SET g.group_status = 'END' " +
            "WHERE g.end_date = curdate() - 3 ",
            nativeQuery = true)
    void updateGroupStatusToEnd();


    @Query(value = "select g from Group g " +
            "where g.period.startDate = curdate() + 1 " +
            "and (g.groupStatus ='GATHERING' or g.groupStatus = 'GATHER_COMPELETE')")
    List<Group> findGroupToChangeStartStatus();
}

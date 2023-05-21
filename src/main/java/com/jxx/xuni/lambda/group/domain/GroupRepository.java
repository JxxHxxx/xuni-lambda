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
            "WHERE g.end_date = DATE_SUB(CURDATE(), INTERVAL 3 DAY) " +
            "AND g.group_status = 'START'",
            nativeQuery = true)
    void updateGroupStatusToEnd();


    @Query(value = "select g from Group g " +
            "where g.period.startDate = curdate() + 1 " +
            "and (g.groupStatus ='GATHERING' or g.groupStatus = 'GATHER_COMPELETE')")
    List<Group> findBatchCond();
}

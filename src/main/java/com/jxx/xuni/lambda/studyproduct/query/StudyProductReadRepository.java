package com.jxx.xuni.lambda.studyproduct.query;

import com.jxx.xuni.lambda.studyproduct.domain.Category;
import com.jxx.xuni.lambda.studyproduct.domain.StudyProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StudyProductReadRepository extends JpaRepository<StudyProduct, String> {

    List<StudyProduct> findStudyProductByCategory(Category category);

    @Query(value = "select sp from StudyProduct sp " +
            "join fetch sp.studyProductDetail spd where sp.id =:studyProductId")
    StudyProduct readProduct(@Param("studyProductId") String studyProductId);
}
package com.example.demo.Repository;

import com.example.demo.Domain.Image;
import com.example.demo.Domain.Project;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

    Optional<Image> findByHash(String hash);

    // 조회와 동시에 다른 트랜잭션에서 수정, 삽입 못하게 락
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Image i WHERE i.hash = :hash")
    Optional<Image> findByHashForUpdate(@Param("hash") String hash);


    @Query(value = "SELECT * FROM image WHERE project_id = :projectId AND " +
            "soft_delete = 0 ORDER BY id ASC LIMIT :limit " +
            "OFFSET :offset", nativeQuery = true)
    List<Image> findByProjectIdAndSoftDeleteFalseWithOffset(@Param("projectId") Long projectId,
                                                            @Param("offset") int offset,
                                                            @Param("limit") int limit);
}

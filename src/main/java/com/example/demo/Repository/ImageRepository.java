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
    /* 단일 엔티티를 조회할 때 결과가 없을 수 있는 경우 */
    Optional<Image> findByProjectIdAndOriginFileName(Long projectId, String filename);

    /* List는 **결과가 없을 때에도 null이 아니라 빈 리스트([])**를 반환하는 게 JPA 기본 동작 */
    @Query("select i from Image i where i.project = :project and i.softDelete = false " +
            "and (:status is null or i.status = :status) " +
            "and (:tags is null or i.tags like %:tags%) " +
            "order by i.createdAt DESC")
    List<Image> findImagesByProject(@Param("project") Project project,
                                    @Param("status") String status,
                                    @Param("tags") String tags,
                                    Pageable pageable);


    Optional<Image> findByHash(String hash);


    // 조회와 동시에 다른 트랜잭션에서 수정, 삽입 못하게 락
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Image i WHERE i.hash = :hash")
    Optional<Image> findByHashForUpdate(@Param("hash") String hash);
}

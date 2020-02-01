package gt.app.modules.follow;

import gt.app.domain.Follow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

interface FollowRepository extends JpaRepository<Follow, Long> {

    @Query("select new gt.app.modules.follow.FollowDTO(  fo.follower.uniqueId, fo.follower.firstName,  fo.follower.lastName  )" +
        " from  Follow fo  where fo.user.id = :id")
    Page<FollowDTO> getFollowers(@Param("id") Long userId, Pageable pageable);

    @Query("select new gt.app.modules.follow.FollowDTO(  fo.user.uniqueId, fo.user.firstName,  fo.user.lastName  )" +
        " from  Follow fo  where fo.follower.id = :id")
    Page<FollowDTO> getFollowing(@Param("id") Long userId, Pageable pageable);


    Optional<Follow> findByUserIdAndFollowerId(Long userId, Long followerId);

    boolean existsByUser_UniqueIdAndFollowerId(String userUniqueId, Long followerId);

    @Transactional
    @Modifying
    @Query("delete from Follow f where f.user.id = :userId and f.follower.id = :followerId")
    void delete(@Param("userId") Long userId, @Param("followerId") Long followerId);

}

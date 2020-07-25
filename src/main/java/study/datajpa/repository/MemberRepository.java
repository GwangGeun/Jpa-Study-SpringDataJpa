package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findByUsername(String username);

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findUsernameList();

    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") Collection<String> names);

    List<Member> findListByUsername(String username); //컬렉션
    Member findMemberByUsername(String username); //단건
    Optional<Member> findOptionalByUsername(String username); //단건 Optional

//    count 쿼리의 경우 조인을 할 필요가 없다. 때문에, 성능상 이슈가 될 경우 카운트 쿼리와 일반 쿼리를 구분해서 사용할 수 있다.
//    @Query(value = "select m from Member m left join m.team t", countQuery = "select count(m) from Member m")
    Page<Member> findByAge(int age, Pageable pageable);

//    Slice<Member> findByAge(int age, Pageable pageable);

     // @Modifying : 실제 JPA executeUpdate()
     @Modifying(clearAutomatically = true)
     @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
     int bulkAgePlus(@Param("age") int age);

//   하단의 로직(패치 조인)은 56 번 Line > @EntityGraph 로 대체 될 수 있다.
//   But, 53~54 line 의 방법이 영한님이 권장 하는 방법
     @Query("select m from Member m left join fetch m.team")
     List<Member> findMemberFetchJoin();

     @Override
     @EntityGraph(attributePaths = {"team"})
     List<Member> findAll();

     // 56~58 line 의 코드를 아래와 같이 풀 수도 있음.
     @EntityGraph(attributePaths = {"team"})
     @Query("select m from Member m")
     List<Member> findMemberEntityGraph();

     @EntityGraph(attributePaths = {"team"})
     List<Member> findEntityGraphByUsername(String username);

     // 아래와 같이 설정하면 dirty checking 이 작동하지 않는다.
     // 실무에서는 사용하지 않는 것을 권장
     @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value="true"))
     Member findReadOnlyByUsername(String username);

     // select... for update 문
     @Lock(LockModeType.PESSIMISTIC_WRITE)
     List<Member> findLockByUsername(String username);

}

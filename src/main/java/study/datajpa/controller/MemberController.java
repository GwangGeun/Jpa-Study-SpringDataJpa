package study.datajpa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

import javax.annotation.PostConstruct;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id) {
        Member member = memberRepository.findById(id).get();
        return member.getUsername();
    }

    /**
     * spring data jpa > 도메인 클래스 컨버터
     * : 권장하는 방법은 X. 그냥 있다는 것 정도만 알고 넘어가기.
     */
    @GetMapping("/members2/{id}")
    public String findMember2(@PathVariable("id") Member member) {
        return member.getUsername();
    }

    /**
     * - Spring 에서 Parameter 중에 Pageable 이 있으면 Page Request 라는 객체를 생성해서 자동으로 값을 채워서 주입해준다.
     * - globally : default page 갯수는 20개 이지만 application.yml 에서 하게 설정을 바꿀 수 있다.
     * - locally : PageableDefault 를 통해 변경 가능 (globally 보다 우선 순위를 가진다)
     * - request ex ) http://localhost:8080/members?page=1&size=3&sort=id,desc&sort=username,desc
     */
    @GetMapping("/members")
    public Page<MemberDto> list(@PageableDefault(size=5) Pageable pageable){
//        Page<Member> page = memberRepository.findAll(pageable);
//        return page;
        return memberRepository.findAll(pageable)
                .map(MemberDto::new);
    }

    // @PostConstruct:  WAS가 띄워질 때 실행된다.
    @PostConstruct
    public void init() {
        for(int i =0; i<100; i++){
            memberRepository.save(new Member("user"+i,i));
        }
    }

}

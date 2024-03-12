# 중간 정리
* 각 클래스 별로 코드를 짠 이유와 최적화 방안을 생각해보려고 한다.
* DB 활용 전 스프링에 대한 이해를 다시 한 번 깨닫고자 진행한 프로젝트임으로 간단하게 진행하려고 함.

## ⌨️CodeReview
* 패키지 구성에 따라서 나눠서 정리

### 📁Package
* [config](#config)
  * WebConfig.class
* [controller](#controller)
  * HomeContorller.class
  * ItemController.class
  * LoginController.class
  * MemberController.class
  * OrderController.class
  * TermsController.class
* [domain](#domain)
  * Cart.class
  * CartItem.class
  * DiscountCode.class
  * Item.class
  * LoginMember.class
  * Member.class
  * Order.class
* [filter](#filter)
  * LogFilter.class
  * LoginCheckFilter.class
* [interceptor](#interceptor)
  * LoginCheckInterceptor.class
* [repository](#repository)
  * MemoryCartRepository.class
  * MemoryItemRepository.class
  * MemoryMemberRepository.class
  * MemoryOrderRepository.class
* [service](#service)
  * cart
    * CartService.class
  * login
    * LoginService.class
  * test
    * TestDataInit.class
* [session](#session)
  * SessionConst.class

### config
<b>WebConfig.class</b>
```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    // 인터셉터 추가
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginCheckInterceptor())
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns("/", "/home", "/assets/*","/css/*","/js/*", "/login", "/members/add", "/members/findPw", "/happystore", "/policy/*");
    }

}
```
> 스프링 Bean에 Configuration 파일임을 인식시키기 위하여 어노테이션을 붙혔으며 스프링의 WebMvcConfigurer의 자식으로 addInterceptor를 오버라이딩하여 구현하였다.
> 인터셉터 순서는 1번, 적용할 경로 패턴은 모든 패턴, 제외할 패턴의 범위를 지정해주었다.

<br><br>

### controller
<b>HomeController.class<b/>
```java
@Controller
@Slf4j
@RequiredArgsConstructor
public class HomeController {

    private final MemoryItemRepository memoryItemRepository;
    private final MemoryCartRepository memoryCartRepository;

    @GetMapping({"/", "/home"})
    public String welcomehome(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false)Member loginMember, Model model) {
        // 상품 리스트
        List<Item> items = memoryItemRepository.findAll();
        model.addAttribute("items", items);

        if (loginMember == null) {
            return "home/home";
        }
        
        long count = memoryCartRepository.findByMember(loginMember.getID()).size();
        model.addAttribute("member", loginMember);
        model.addAttribute("cartNum", count);
        return "home/loginHome";
    }

    @GetMapping("/happystore")
    public String aboutstore(){
        return "home/about";
    }

}
```
> 스프링의 Bean에 등록하기 위해 Controller 어노테이션을 작성, 로그를 출력하기 위해 lombok의 Slf4j 어노테이션을 작성하였다.
> 그리고 생성자 메서드 실행 시에 final로 선언된 변수를 자동으로 wired 해주는 Lombok의 RequireArgsConstructor 어노테이션을 작성하였다.

> home 주소의 GetMapping을 보면 파라미터로 SessionAttribute 어노테이션을 사용했다. HttpSession을 사용한 번거로운 세션 등록을 간략화해준다.
> 그리고 다음 Model 파라미터는 각 데이터를 담아 html에서 사용할 수 있게 만들어준다.

> memoryItemRepository에서 모든 아이템을 불러와 list에 담고 model로 보내준다 이 데이터들은 home.html, loginhome.html에서 활용될 것이다.

> httpsession의 name인 sessionconst.loginmember의 값으로 member값을 찾아온다. 이 때 멤버 값이 없으면 home.html을 띄운다.
> 멤버 값이 있을 경우에는 memoryCartRepository에서 loginmember의 id값으로 카트 안의 아이템 개수를 불러온다. 이 데이터는
> loginhome.html의 카트 수량 표시에 이용될 것이다. loginhome.html에 member와 cartNum을 보내주고 페이지를 이동시킨다.

> happystore로 get요청을 보내면 사이트 정보를 작성한 about.html로 페이지를 이동시킨다.

<br><br>

<b>ItemController.class</b>
```java
@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/item")
public class ItemController {

    private final CartService cartService;
    private final MemoryCartRepository memoryCartRepository;
    private final MemoryItemRepository memoryItemRepository;

    @GetMapping("cart")
    public String cart(Model model, HttpServletRequest req) {
        HttpSession session = req.getSession();
        Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        model.addAttribute("itemList", memoryCartRepository.findByMember(loginMember.getID()));
        model.addAttribute("itemInfo", memoryItemRepository.findAll());
        model.addAttribute("discountCode", new DiscountCode());
        return "item/cart";
    }

    @GetMapping("cart/add/{id}")
    public String cartAdd(@PathVariable Long id, HttpServletRequest req) {
        HttpSession session = req.getSession();
        Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        cartService.addItem(loginMember.getID(), id, 1L);
        log.info("addcart = {}", memoryCartRepository.findByMember(loginMember.getID()));
        return "redirect:/home";
    }

    @PostMapping("cart/delete")
    public String cartdelete(HttpServletRequest req) {
      String[] parameterValues = req.getParameterValues("itemIdDelete");
      HttpSession session = req.getSession();
      Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
      for (String parameterValue : parameterValues) {
        log.info("parameterVlaue = {}",parameterValue);
        memoryCartRepository.deleteCart(loginMember.getID(), Long.valueOf(parameterValue));
    }
}
```

> requestMapping을 "/item"으로 받아와 기본적인 경로가 item으로 시작되게 만듦 나머지 어노테이션은 homecontroller와 같음

> cart의 get 요청시에는 데이터를 담을 Model과 Http 요청 헤더를 쉽게 읽어올 HttpServletRequest를 파라미터로 사용

> request의 getSession 메서드를 이용해 세션 값을 읽어옴 (cart 페이지는 로그인 된 사용자만 접근 가능하기 때문에 별도의 체크 X) <br>
> Session에 저장되어있는 Member 값을 const 값으로 이뤄진 key로 받아와 강제형변환 해줌 <br>
> loginmember 변수로 model에 담을 값들의 key로 이용함 (아이템리스트, 아이템정보, 할인코드)<br>
> model에 데이터들을 담고 cart.html로 이동

> cart/add/id의 get 요청 시에는 Pathvariable 어노테이션을 사용해 path 내의 괄호 속 정보를 변수로 사용할 수 있게 만듦

> req로 세션을 받아와 member를 가져옴<br>
> 카트 서비스를 통해 아이템을 등록 (loginmember의 ID를 카트 아이디로 사용, 파라미터의 id는 아이템ID, 마지막 파라미터인 수량은 1개)
> log를 찍어주고 home으로 리다이렉트 시켜줌

> cart의 delete post 요청 시에는 세션데이터와 파라미터 밸류를 받아옴

> cart.html에서 받아온 itemIdDelete 정보를 받아오고 loginMember의 정보를 얻어오기 위해 session 변수를 할당<br>
> loginMember를 얻어와서 그 memberId와 itemId를 memoryCartRepository의 deleteCart 메서드의 파라미터로 넘김.

<br>
<br>

<b>LoginController.class</b>
```java
@Controller
@Slf4j
@RequiredArgsConstructor
public class LoginController {
    private final LoginService loginService;

    @GetMapping("/login")
    public String loginForm(@ModelAttribute("loginForm") LoginForm form) {
        return "login/loginForm";
    }

    @PostMapping("/login")
    public String login(@Validated @ModelAttribute("loginForm") LoginForm form, @RequestParam(name = "redirectURL", required = false) String redirectURL, BindingResult bindingResult, HttpServletRequest req) {

        if (bindingResult.hasErrors()) {
            log.info("errors = {}", bindingResult.getAllErrors());
            return "login/loginForm";
        }

        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());
        log.info("login = {}", loginMember);
        // RememberMe 구현을 위해 remember 세션 처리 TODO
        log.info("isRemember = {}", form.isRemember());

        if (loginMember == null) {
            bindingResult.reject("check.login.IdorPw", "check");
            return "login/loginForm";
        }

        // 로그인 성공 처리 TODO
        HttpSession session = req.getSession(true);
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);
        if (redirectURL != null) {
            System.out.println("redirectURL = " + redirectURL);
            return "redirect:" + redirectURL;
        }
        return "redirect:/home";
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest req) {

        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/";
    }
}
```

> login 메서드가 있는 loginService 클래스를 선언하고 의존성을 주입

> /login 으로 GET 요청이 들어오면 loginForm을 model에 등록하여서 login/loginForm.html로 페이지 이동 (오류가 발생했을 시에 재접속 되면 loginform을 담을 비어있는 객체를 지원하기 위해서 modelattribute 어노테이션 사용)

> /login으로 POST 요청이 들어오면 모델에 담겨있는 loginForm이라는 데이터를 불러오고 검증함, redirectURL이라는 파라미터를 받아옴 (인터셉터 적용 시 리다이렉트 되기 전 페이지로 되돌려 보내주기 위함)
>  오류를 처리할 BindingResult 객체 선언, 세션을 생성해줄 HttpServletRequest 도 받아옴

> bindingresult내에 에러가 존재하면 모든 에러를 출력하고 loginform 페이지로 다시 이동시킴(redirect가 아니기 때문에 html 내의 경고문 확인 가능)

> loginService의 login 메서드를 사용해 아이디와 비밀번호가 매치되는 지 확인하고 리턴값으로 Member객체를 받아옴 loginMember가 null일 시에는 
> 에러 코드를 bindingresult에 등록하고 다시 loginform 페이지로 이동 시킴 (application.properties에서 errors.properties를 등록하였는데 이 파일 내부에 지정해놓은 국제화된 에러 메시지가 출력됨. 또는 SpringBootApplication 어노테이션이 붙은 메인 클래스에 MessageSource를 구현하여 빈 등록하면 됨.)

>  로그인 성공 시에는 session을 생성함 (getsession시에 true 값을 주면 세션이 없을 시에 새로 생성해줌.) 세션데이터에 사전에 준비해놓은 sessionconst를 키로 주고 loginmember 값을 밸류로 넣어줌

> redirectURL에 값이 존재할 경우에 파라미터에서 받아온 redirectURL로 redirect 시켜줌

> /logout으로 POST 요청이 들어오면 session을 get하여서 (logout 버튼은 session이 존재할 때만 보이기 때문에 체크 X) session이 존재할 경우에 세션을 초기화해주고 홈으로 redirect 시켜줌 

<br><br>

<b>MemberController.class</b>
```java
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemoryMemberRepository memberRepository;

    @GetMapping("/add")
    public String addMemberView(@ModelAttribute("member") Member member) {
        return "members/addMember";
    }

    @PostMapping("/add")
    public String save(@Validated @ModelAttribute Member member, BindingResult result) {
        if (result.hasErrors()) {
            return "members/addMember";
        }
        memberRepository.save(member);
        return "redirect:/home";
    }

    @GetMapping("/findPw")
    public String findPassword() {
        return "members/findPasswordMember";
    }

    @PostMapping("/info")
    public String Information(HttpServletRequest req, Model model){
        HttpSession session = req.getSession(false);
        Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        model.addAttribute("member", loginMember);
        return "members/informationMember";
    }
}
```

> /members/add의 get 요청이 들어올 시에 Member 데이터 형으로 modelattribute 바인딩을 해주고 members/addMember.html로 페이지를 이동시킵니다.

> /members/add의 post 요청이 들어올 시에 Member 데이터 형으로 modelattribute 바인딩을 해주고 에러를 체크하는 bindingresult를 파라미터로 받습니다.

> bindingresult에 error가 있다면 리다이렉트가 아닌 addMember.html로 페이지 이동을 하여서 error들을 사용자에게 알려줍니다. <br>
> 에러가 없다면 /home으로 redirect 시킵니다. (세션이 존재하는 로그인된 상태의 home을 출력하기 위해 redirect 시킴)

> /members/findPw의 get 요청이 들어올 시에 단순히 members/findpasswordmember.html의 페이지로 이동시킵니다. (구현 x)

> /mebers/info의 post 요청이 들어올 시에 session을 받아올 HttpservletRequest와 데이터를 담을 Model을 파라미터로 받아옵니다.

> session이 존재한다면 session을 get 해오고 loginMember를 세션저장소에서 받아옵니다. 그리고 페이지에서 사용해야할 loginmember 값을 member의 이름으로 model에 add 해줍니다.
> <br> 그리고 members/informationmember.html로 페이지 이동시킵니다.

<br><br>

<b>OrderController.class</b>
```java
@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping
public class OrderController {

    private final MemoryItemRepository memoryItemRepository;
    private final MemoryOrderRepository memoryOrderRepository;

    @GetMapping("/order")
    public String showOrder(@ModelAttribute Order order, Model model) {
        return "redirect:/members/orderHistoryMember";
    }

    @PostMapping("/order")
    public String addOrder(HttpServletRequest req,
             Model model) {
        String[] itemIds = req.getParameterValues("itemId");
        String[] counts = req.getParameterValues("count");
        String[] cartIds = req.getParameterValues("cartId");
        String[] saleCodes = req.getParameterValues("saleCode");
        List<Long> totalPrice = new ArrayList<>();
        Long price = 0L;
        for (int i = 0; i < counts.length; i++) {
            if (memoryItemRepository.findById(Long.valueOf(itemIds[i])).isSale()){
                price = (long) (memoryItemRepository.findById(Long.valueOf(itemIds[i])).getPrice() - (memoryItemRepository.findById(Long.valueOf(itemIds[i])).getPrice()
                                        * (memoryItemRepository.findById(Long.valueOf(itemIds[i])).getSalePercentage() * 0.01)));
            }else {
                price = Long.valueOf(memoryItemRepository.findById(Long.valueOf(itemIds[i])).getPrice());
            }

            Optional<String> any = Arrays.stream(saleCodes).findAny();
            if (any.isPresent()) {
                price = checkDiscountCode(price, any.get());
            }
            totalPrice.add(price * Long.parseLong(counts[i]));

            memoryOrderRepository.save(new Order(Long.parseLong(cartIds[i]), totalPrice.get(i),
                    Long.parseLong(itemIds[i]),Long.parseLong(counts[i])));
        }

        model.addAttribute("orders", memoryOrderRepository.findByMember(Long.parseLong(cartIds[0])));
        return "/members/orderHistoryMember";
    }

    private Long checkDiscountCode(Long price, String checkCode) {
        for (String code : DiscountCode.codes) {
            if (checkCode.equals(code) && checkCode.equals("10percent")) {
                return (long) (price - (price * 0.1));
            }
            if (code.equals(checkCode) && checkCode.equals("500coin")) {
                return (price - 500);
            }
        }
        return price;
    }

}
```

> /order의 get 요청이 들어올 시에 빈 order 객체를 선언해주고 model을 파라미터로 받아온 뒤 mebers/orderhistorymember.html로 redirect 해줍니다. (order들을 띄우기 위함.)

> /order의 post 요청이 들어올 시에 parametervalue를 읽기 위한 HttpservletRequest를 받아오고 데이터를 html에 넘길 Model을 파라미터로 받아옵니다.

> http request를 사용해 parameterValues를 모두 받아옵니다. (itemId, count, cartId, saleCode) 그리고 각 item의 amount와 특성에 맞는 price를 곱해 저장해줄 totalprice list를 선언해줍니다. 또, 중간 연산에서 사용해줄 price 변수도 선언해줍니다.

> 일단 파라미터 밸류들의 배열 속에서 index를 사용해야 하기 때문에 for i 반복문을 사용했고 어떠한 밸류를 사용해도 상관 없지만 일단 counts의 length를 기준으로 for문을 작성했습니다.<br>

> 만약 memoryitemrepository에서 findbyid(itemid를 기준으로 store를 뒤져 item을 반환하는 메서드)의 반환 Item 객체가 isSale() 의 조건에 만족한다면 <br>
> 가격은 sale되는 가격으로 책정되고 아닐 시의 가격은 그냥 amount와 price를 곱해줌

> 그리고 salecodes stream 배열에서 findany() method를 사용해 어떠한 값이라도 찾아내 값이 존재함을 확인한다면 (any.ispresent()) price는 checkDiscountCode (현 클래스 내부의 메서드)를 사용해 해당되는 코드의 세일 연산을 진행합니다. <br>

> discountcode는 각 항목마다 10퍼센트 할인이 적용되는 코드가 있고 각 항목마다 500원씩 할인이 적용되는 코드가 있습니다. DiscountCode 도메인의 codes 속에서 equals가 참인 값에 따라 return 값이 달라집니다.

> totalPrice에 add하여 순차적으로 memoryOrderRepository의 save 메서드의 파라미터로 Order 객체를 생성하여 넘깁니다.

> length의 길이에 따라 모든 반복문이 진행되면 model에 orders (memoryOrderRepository.findByMember 메서드를 실행하여 받아온 Order Collection을 반환 받음)를 add해줍니다.
> <br> 마지막으로 members/orderhistorymember.html로 페이지 이동

<br><br>

<b>TermsController.class</b>
```java
@Slf4j
@Controller
@RequestMapping("/policy")
public class TermsController {

    @GetMapping("terms")
    public String termsPage(){
        return "policy/terms";
    }

}
```

> /policy/terms의 get 요청을 받으면 policy/terms.html로 페이지 이동

<br><br>

### domain
<b>Cart.class</b>
```java
@Getter
@Setter
public class Cart {
    private Long Id;
}

```

> lombok의 getter setter를 이용하고 카드별로 아이디를 부여하여 cart repository에 저장함.

<br><br>

<b>CartItem.class</b>
```java
@Getter
@Setter
public class CartItem {
    private Long id;
    private Long cartId;
    private Long itemId;
    private Long count;

    @Override
    public String toString() {
        return "CartItem{" +
                "id=" + id +
                ", cartId=" + cartId +
                ", itemId=" + itemId +
                ", count=" + count +
                '}';
    }
}
```

> 카트에 담겨있는 아이템을 의미하며 고유 식별 id와 cartid, itemid, count를 변수로 두어 실제 값을 계산할 때 사용

<br><br>

<b>DiscountCode.class</b>
```java
@Getter
@Setter
public class DiscountCode {
    public static List<String> codes = List.of("10percent","500coin");
}
```

> 적용되는 코드를 저장하는 List를 static으로 선언하여 equals 연산 시에 사용할 수 있게 만듦.

<br><br>

<b>Item.class</b>
```java
@Getter
@Setter
public class Item {

    private Long ItemId;
    private String ItemName;
    private Integer Price;
    private String ItemSrc;
    private Integer StarCount;
    private boolean Sale;
    private Integer SalePercentage;


    public Item(){
    }

    @Override
    public String toString() {
        return "Item{" +
                "ItemId=" + ItemId +
                ", ItemName='" + ItemName + '\'' +
                ", Price=" + Price +
                ", ItemSrc='" + ItemSrc + '\'' +
                ", StarCount=" + StarCount +
                ", Sale=" + Sale +
                ", SalePercentage=" + SalePercentage +
                '}';
    }
}
```

> 아이템의 상세 정보, 고유 식별이 가능한 아이디와 아이템 이름, 가격, 이미지를 출력해줄 링크, 별점 개수, 세일 적용 여부, 세일 퍼센테이지 등이 존재

<br><br>

<b>LoginForm.class</b>
```java
@Getter
@Setter
public class LoginForm {
    @NotBlank
    private String loginId;
    @NotBlank
    private String password;
    private boolean Remember;
}
```

> 로그인 시 적용되는 아이디와 패스워드 그리고 아이디 remember 기능의 체크박스의 boolean 값을 할당하는 변수 선언<br>유효성 체크 시에 공백과 빈 칸을 허용하지 않는 notblank를 사용 <br>

<br><br>

<b>Member.class</b>
```java
@Getter
@Setter
public class Member {
    private Long ID;
    @NotEmpty
    private String loginId;
    @NotEmpty
    private String name;
    @NotEmpty
    private String password;

    @Override
    public String toString() {
        return "Member{" +
                "ID=" + ID +
                ", loginId='" + loginId + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
```

> 멤버의 고유 ID와 회원 가입 시에 loginId, 이름, 비밀번호를 변수로 선언 <br> notempty는 유효성 체크 시에 공백은 가능하지만 빈 칸만 불가능 하게 만듦

<br><br>

<b>Order.class</b>
```java
@Getter
@Setter
public class Order {
    private Long orderId;
    private Long cartId;
    private Long totalPrice;
    private Long itemId;
    private Long amount;

    public Order(Long cartId, Long totalPrice, Long itemId, Long amount) {
        this.cartId = cartId;
        this.totalPrice = totalPrice;
        this.itemId = itemId;
        this.amount = amount;
    }

    public Order() {
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", cartId=" + cartId +
                ", totalPrice=" + totalPrice +
                ", itemId=" + itemId +
                ", amount=" + amount +
                '}';
    }
}
```

> order의 고유 Id와 카트의 아이디(멤버 아이디와 같은 값으로 처리됨), 총 가격(세일, 할인코드 적용 가격), 아이템 아이디, 수량을 변수로 선언

> 생성자 초기화로 orderid를 제외한 나머지 값 대입 (orderId는 repository 저장 시에 할당됨) 

<br><br>

### filter
* 프로젝트에 적용되지는 않음, 인터셉트 단에서 처리할 작업 서블릿 단에서 처리할 작업을 추후에 구별할 수도 있어 구현(filter는 chain 연결 시에 request와 response를 재구현해서 사용할 수 있기 때문)

<br><br>

<b>Logfilter.class</b>
```java
@Slf4j
public class LogFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
      log.info("filter init");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String reqUri = req.getRequestURI();

        String uuid = UUID.randomUUID().toString();

        try{
            log.info("REQUEST [{}] [{}]",uuid, reqUri);
            chain.doFilter(request, response);
        }catch (Exception e){
            throw e;
        }finally {
            log.info("RESPONSE [{}] [{}]",uuid, reqUri);
        }
    }

    @Override
    public void destroy() {
        log.info("filter destroy");
    }
}
```

> filter 인터페이스를 상속받아 init > doFilter > destory 순으로 작업이 진행되는 작업을 오버라이딩하여 구현함

> init시에 로그 출력

> 다형성 보장을 위해 상위 인터페이스로 주어진 request와 response를 다운캐스팅 시켜주고 uuid를 생성하여 uri와 uuid를 출력하는 log 전용 필터 구현 

> destroy시에 로그 출력

<br><br>

<b>LoginCheckFilter.class</b>
```java
@Slf4j
public class LoginCheckFilter implements Filter {

    private final static String[] whitelist = {"/","/home","/static/*","/login","/members/add","/members/findPw","/happystore","/policy/*"};

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String requestURI = req.getRequestURI();
        HttpServletResponse resp = (HttpServletResponse) response;

        try {
            if (isLoginCheckPath(requestURI)) {
                HttpSession session = req.getSession(false);
                if (session == null || session.getAttribute(SessionConst.LOGIN_MEMBER) == null) {
                    resp.sendRedirect("/login?redirectURL" + requestURI);
                    return;
                }
            }
            chain.doFilter(request, response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isLoginCheckPath(String requestURI) {
        return !PatternMatchUtils.simpleMatch(whitelist, requestURI);
    }
}

```

> 마찬가지로 filter를 구현, whiteList 구현을 위한 list를 static으로 선언함

> 실제 기능이 동작하는 doFilter를 구현 마찬가지로 req, resp를 다운캐스팅함

> if문을 사용해 사용자가 모든 사이트 접근 시에 화이트 리스트에 등록된 주소가 아닌 다른 주소를 세션 없이 접속한다면 redirectURL을 추가하여 로그인 페이지로 리다이렉트시킴. 


<br><br>

### interceptor
<b>LoginCheckInterceptor.class</b>
```java
public class LoginCheckInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute(SessionConst.LOGIN_MEMBER) == null) {
            response.sendRedirect("/login?redirectURL=" + requestURI);
            return false;
        }
        return true;
    }
}
```

> HandlerInterceptor 인터페이스를 구현하여 메서드오버라이딩을 진행, login여부를 체크해야하니 controller로 넘어가기 전 처리하는 prehandler를 구현

> req로 URI를 받아와 redirectURI를 만들어주고 session을 얻어옴, 이 과정에서 session이 null이거나 session 속 loginmember가 존재하지 않으면 리다이렉트URI와 함께 login페이지로 리다이렉트시킴.

> 세션이 없을 경우 false 반환 있을 경우 true 반환

<br><br>

### repository
* CRUD 작업만 하는 Repository를 만들어야 하는데 구현에 급급하다보니 RESTful하게 구현하는 부분을 놓쳤다. 추후에 꼭 수정할 것.

<b>MemoryCartRepository.class</b>
```java
@Repository
@Slf4j
public class MemoryCartRepository {
    private static Map<Long, Cart> store = new ConcurrentHashMap<>();
    private static Map<Long, CartItem> itemStore = new ConcurrentHashMap<>();
    private static AtomicLong count = new AtomicLong();

    public Cart createCart(Long memberId) {
        if (store.get(memberId) != null) {
            Cart cart = new Cart();
            store.put(memberId, cart);
            return cart;
        }
        return store.get(memberId);
    }

    public CartItem addCart(CartItem cartItem) {
        Optional<CartItem> item = findByMember(cartItem.getCartId()).stream()
                .filter(c -> c.getItemId().equals(cartItem.getItemId()))
                .findAny();
        if (item.isPresent()) {
            CartItem cartItem1 = item.get();
            cartItem1.setCount(cartItem1.getCount() + cartItem.getCount());
            itemStore.replace(item.get().getId(), cartItem1);
        }else {
            cartItem.setId(count.incrementAndGet());
            itemStore.put(cartItem.getId(), cartItem);
        }
        return cartItem;
    }

    public void deleteCart(Long Id, Long itemId) {
        List<CartItem> byMember = findByMember(Id);
        for (CartItem cartItem : byMember) {
            if (cartItem.getItemId().equals(itemId)) {
                itemStore.remove(cartItem.getId());
            }
        }
    }

    public CartItem findById(Long id) {
        return itemStore.get(id);
    }

    public List<CartItem> findByMember(Long memberId){
        return itemStore.values().stream()
                .filter(c -> c.getCartId().equals(memberId))
                .toList();
    }





}
```

> 스프링 빈에 Repository임을 알리는 어노테이션 작성

> 멤버 아이디 값을 키값으로 갖는 카트를 저장하는 store를 ConcurrentHashMap으로 선언 (동시성 문제) <br>
> AtomicLong인 count를 키값으로 CartItem을 저장하는 ItemStore를 ConCurrentHashMap으로 선언

> memberId를 key값으로 받아 store에 cart를 put 해주고 store를 반환함. 이미 카트가 존재하면 get하여 Cart를 반환함 

> CartItem을 파라미터로 받아와서 cartitem 객체의 cartId를 이용해 findbymember를 이용해 cartItem을 optional로 받아옴. null이 아니라면 item의 변수를 이용해 카운트를 하나 늘려주고 replace해주는 방식으로 갯수를 하나 더해줌. <br>
> null이라면 itemstore에 count로 고유값을 id로 set 해주고 그 아이디를 인덱스로 itemStore에 put 해줌 마지막으로 cartItem을 반환

> cartid(memberid)와 itemid를 파라미터로 받아와 findbymember를 해주어 cart 속 cartitem의 list를 받아옴. itemid는 중복되지 않으니 delete 작업이 실행되면 remove해줌

> id를 받아와 인덱스로 사용해 cartitem을 받아옴

> memberId를 이용해 cartid(memberid)와 일치하는 결과값을 toList시켜 반환 



<br><br>

<b>MemoryItemRepository.class</b>
```java
@Slf4j
@Repository
public class MemoryItemRepository {
    private static Map<Long, Item> store = new ConcurrentHashMap<>();
    private static AtomicLong sequence = new AtomicLong();

    public Item save(Item item){
        item.setItemId(sequence.incrementAndGet());
        store.put(item.getItemId(), item);
        log.info("Save item = {}", item);
        return item;
    }

    public Item findById(Long id) {
        return store.get(id);
    }

    public List<Item> findAll(){
        return new ArrayList<>(store.values());
    }

    public void update(Long itemId, Item updateParam) {
        Item findItem = findById(itemId);
        findItem.setItemName(updateParam.getItemName());
        findItem.setPrice(updateParam.getPrice());
    }

    public void clearStore(){
        store.clear();
    }

}
```

> item을 저장할 store를 선언, 인덱스를 저장할 sequence를 선언

> item을 파라미터로 받아와 store에 index를 적용하고 저장해줌

> id를 파라미터로 받아와 index를 기준으로 store에서 get

> 모든 값을 List로 return함

> itemid와 item 객체를 파라미터로 받아와 수정해줌 (추후에 사용)

> store를 claer시킴 

<br><br>

<b>MemoryMemberRepository.class</b>
```java
@Slf4j
@Repository
public class MemoryMemberRepository {
    private static Map<Long, Member> store = new ConcurrentHashMap<>();
    private static AtomicLong sequence = new AtomicLong();

    public Member save(Member member) {
        member.setID(sequence.incrementAndGet());
        log.info("save : member = {}", member);
        store.put(member.getID(), member);
        return member;
    }

    public Member findById(Long id) {

        return store.get(id);
    }

    public Optional<Member> findByLoginId(String loginId) {
        return findAll().stream()
                .filter(m -> m.getLoginId().equals(loginId))
                .findFirst();
    }

    public List<Member> findAll() {
        return new ArrayList<>(store.values());
    }

    public void clearStore(){
        store.clear();
    }


}
```

> 데이터 저장소와 인덱스 선언

> member를 파라미터로 받아와 index를 설정해주고 store에 put 시키고 member를 반환함

> id를 파라미터로 받아와 member를 반환함 

> loginid(통상적으로 사용하는 id)를 받아와 member를 반환

> store의 값들을 list로 반환

> store를 clear시킴 

<br><br>

<b>MemoryOrderRepository.class</b>
```java
@Repository
@Slf4j
public class MemoryOrderRepository {
    Map<Long, Order> store = new ConcurrentHashMap<>();
    AtomicLong sequence = new AtomicLong();
    public Order save(Order order){
        order.setOrderId(sequence.incrementAndGet());
        store.put(sequence.get(), order);
        return order;
    }

    public Collection<Order> findAll(){
        return store.values();
    }

    public Collection<Order> findByMember(Long memberId) {
        return store.values().stream().filter(c -> Objects.equals(c.getCartId(), memberId)).toList();
    }
}

```

> 값을 저장할 store와 index로 사용할 sequence 선언

> order를 파라미터로 받아와 store에 put하고 order를 return 해줌

> store의 모든 값을 Collection으로 반환

> memberid를 파라미터로 받아와 store 안에 cartid와 같은 모든 값들을 리턴 

<br><br>

### service
<b>CartService.class</b>
```java
@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {

    private final MemoryCartRepository memorycartrepository;

    public CartItem addItem(Long memberId, Long itemId, Long count) {
        memorycartrepository.createCart(memberId);
        CartItem cartItem = new CartItem();
        cartItem.setItemId(itemId);
        cartItem.setCartId(memberId);
        cartItem.setCount(count);
        return memorycartrepository.addCart(cartItem);
    }
}

```

> 스프링 빈 컨테이너에 해당 클래스가 서비스임을 알리는 어노테이션 사용

> memorycartrepository를 생성자 의존성 주입으로 선언

> memberid와 itemid 그리고 count를 파라미터로 받아와 객체를 새로 만들어 파라미터 값을 대입 시킨 후 repository에 add하는 작업 

<br><br>

<b>LoginService.class</b>
```java
@Service
@RequiredArgsConstructor
public class LoginService {
    private final MemoryMemberRepository memoryMemberRepository;
    /**
     * @return null이면 Login Fail
     */
    public Member login(String loginId, String password) {
//        return memoryMemberRepository.findByLoginId(loginId)
//                .filter(m -> m.getPassword().equals(password))
//                .orElse(null);
        Optional<Member> byLoginId = memoryMemberRepository.findByLoginId(loginId);
        if (byLoginId.isPresent()) {
            Member member = byLoginId.get();
            if (member.getPassword().equals(password)) {
                return member;
            }
        } else {
            return null;
        }
        return null;
    }


}
```

> LoginId와 password를 파라미터로 받아와 member를 loginid 기준으로 찾아오고 loginid가 존재할 시에 패스워드가 맞는 지 확인 후 멤버를 return 하거나 null을 return함

> 주석 코드는 stream에 익숙해지기 전이라 스트림 코드도 짜보고 일반 코드도 짜 보았던 것. 

<br><br>

<b>TestDataInit</b>
```java
@Component
@RequiredArgsConstructor
public class TestDataInit {
    private final MemoryMemberRepository memberRepository;
    private final MemoryItemRepository memoryItemRepository;

    @PostConstruct
    public void init(){
        Item item = new Item();
        item.setItemName("테스트상품");
        item.setPrice(1000);
        item.setItemSrc("/assets/testItem.jpg");
        item.setStarCount(4);
        item.setSale(true);
        item.setSalePercentage(10);

        Item item2 = new Item();
        item2.setItemName("테스트상품2");
        item2.setPrice(10000);
        item2.setItemSrc("/assets/testItem2.jpg");
        item2.setStarCount(3);
        item2.setSale(false);
        item2.setSalePercentage(50);

        Item item3 = new Item();
        item3.setItemName("테스트상품3");
        item3.setPrice(120000);
        item3.setItemSrc("/assets/testItem3.jpg");
        item3.setStarCount(2);
        item3.setSale(true);
        item3.setSalePercentage(32);

        Item item4 = new Item();
        item4.setItemName("테스트상품4");
        item4.setPrice(900000);
        item4.setItemSrc("/assets/testItem4.jpg");
        item4.setStarCount(5);
        item4.setSale(false);
        item4.setSalePercentage(90);

        Item item5 = new Item();
        item5.setItemName("테스트상품4");
        item5.setPrice(900000);
        item5.setItemSrc("/assets/testItem4.jpg");
        item5.setStarCount(5);
        item5.setSale(false);
        item5.setSalePercentage(90);


        memoryItemRepository.save(item);
        memoryItemRepository.save(item2);
        memoryItemRepository.save(item3);
        memoryItemRepository.save(item4);
        memoryItemRepository.save(item5);


        Member member = new Member();
        member.setLoginId("test");
        member.setName("test");
        member.setPassword("1234");
        memberRepository.save(member);

        Member member2 = new Member();
        member2.setLoginId("test2");
        member2.setName("test2");
        member2.setPassword("1234");
        memberRepository.save(member2);
    }
}
```

> 빈 컨테이너에 컴포넌트임을 알리는 어노테이션 작성

> 생성자 의존성 주입으로 memorymemberrepository, memoryitemrepository를 선언

> 컨테이너에 컴포넌트를 등록하며 초기화 하는 메서드인 PostConstruct 어노테이션을 사용하여 init 메서드를 작성함 (한 번만 실행됨을 보장하기 위해)

> 각 테스트 데이터를 작성함 

<br><br>


### session
<b>SessionConst.class</b>
```java
public class SessionConst {
    public static final String LOGIN_MEMBER = "loginMember";
}
```

> 세션 컨테이너에 loginmember로 session 데이터를 저장하기 위하여 상수를 선언함.

<br><br>


package jpabook.jpashop.service;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static com.jayway.jsonpath.internal.path.PathCompiler.fail;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class OrderServiceTest {

    @Autowired EntityManager em;
    @Autowired OrderService orderService;
    @Autowired OrderRepository orderRepository;

    @Test
    public void Itemorder() throws Exception {
        //given
        Member member = createMember();
        Item item = CreateBook("시골jpa", 10000, 10);


        int orderCount =2;

        //when
        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);


        //then
        Order getOrder = orderRepository.findOne(orderId);

        Assert.assertEquals("상품주문시 상태는 ORDER", OrderStatus.ORDER, getOrder.getStatus());
        Assert.assertEquals("주문한 상품 종류수가 정확해야한다", 1, getOrder.getOrderItems().size());
        Assert.assertEquals("주문가격은 가격 곱 수량이다", 10000*orderCount, getOrder.getTotalPrice());
        Assert.assertEquals("주문 수량만큼 재고가 줄어야한다", 8, item.getStockQuantity());
    }



    @Test(expected = NotEnoughStockException.class)
    public void ItemExceed() throws Exception {
        //given
        Member member = createMember();
        Item item = CreateBook("시골jpa", 10000, 10);

        int orderCount = 11;

        //when
        orderService.order(member.getId(), item.getId(), orderCount);

        //then
        fail("재고수량  예외가 발생해야");

    }



    @Test
    public void OrderCancel() throws Exception {

        //given
        Member member = createMember();
        Book item = CreateBook("시골 JPA", 10000, 10);

        int orderCount = 2;
        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);

        //when
        orderService.cancelOrder(orderId);

        //then
        Order getOrder = orderRepository.findOne(orderId);
        Assert.assertEquals("주문 취소시 상태는 cancel이다", OrderStatus.CANCEL, getOrder.getStatus() );
        Assert.assertEquals("주문이 취소된 상품은 그만큼 재고가 증가해야한다", 10, item.getStockQuantity());


    }


    private Book CreateBook(String name, int price, int stockQuantity) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        em.persist(book);
        return book;
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울", "강가", "1232323"));
        em.persist(member);
        return member;
    }




}
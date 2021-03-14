package jpabook.jpashop.domain;

import lombok.Getter;

import javax.persistence.Embeddable;

@Embeddable
@Getter
public class Address {

    private String city;
    private String steet;
    private String zipcode;

    protected Address() {
    }

    public Address(String city, String steet, String zipcode) {
        this.city = city;
        this.steet = steet;
        this.zipcode = zipcode;
    }


}

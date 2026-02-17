package co.com.pragma.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
@Setter
@Getter
public class Branch {
    private String id;
    private String name;
    private String address;
    private String city;
    private List<Product> products;

    public Branch() {
        this.products = new ArrayList<>();
    }

    public Branch(String id, String name) {
        this.id = id;
        this.name = name;
        this.products = new ArrayList<>();
    }

    public Branch(String id, String name, String address, String city) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.city = city;
        this.products = new ArrayList<>();
    }

    public Branch(String id, String name, List<Product> products) {
        this.id = id;
        this.name = name;
        this.products = products != null ? products : new ArrayList<>();
    }

    public Branch(String id, String name, String address, String city, List<Product> products) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.city = city;
        this.products = products != null ? products : new ArrayList<>();
    }

}

package com.example.TPO6_MD_S22825;

public class Book {
    private int lp;
    private String bookName;
    private String author;
    private double price;

    public Book() {
    }

    public Book(int lp, String name, String author, double price) {
        this.lp = lp;
        this.bookName = name;
        this.author = author;
        this.price = price;
    }

    public int getLp() {
        return lp;
    }

    public String getName() {
        return bookName;
    }

    public String getAuthor(){
        return author;
    }

    public double getPrice() {
        return price;
    }
}

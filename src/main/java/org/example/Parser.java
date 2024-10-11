package org.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class Parser {

    public static void parse(String url) {
        try {
            Document doc = Jsoup.connect(url).get();
            Element productGrid = doc.selectFirst("div.product-grid");

            if (productGrid != null) {
                Elements products = productGrid.select("> div");

                for (Element product : products) {
                    String productLink = Validator.validateLink(product.selectFirst("div.image a"), "href");
                    String phoneName = Validator.validateText(product.selectFirst("div.name a span"));
                    String phonePrice = Validator.validatePrice(product.selectFirst("div.price"));

                    Document productDoc = Jsoup.connect(productLink).get();
                    Element creditPriceElement = productDoc.selectFirst(".postreqcredit_btn strong");
                    String creditPrice = Validator.validateText(creditPriceElement);

                    System.out.println("Phone Name: " + phoneName);
                    System.out.println("Price: " + phonePrice);
                    System.out.println("Link: " + productLink);
                    System.out.println("Price per month in credit: " + creditPrice);
                    System.out.println();
                }
            } else {
                System.out.println("No product grid found on the page.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

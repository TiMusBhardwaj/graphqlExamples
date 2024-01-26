package com.example.sellerspace.model.response;

import lombok.Data;

@Data
public class PageMeta {
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

}

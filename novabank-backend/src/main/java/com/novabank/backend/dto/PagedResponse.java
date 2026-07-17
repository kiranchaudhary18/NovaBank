package com.novabank.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Generic response wrapper containing page content lists and navigation metadata.
 *
 * @param <T> content element type
 * @author Senior Java Backend Architect
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagedResponse<T> {

    /** List payload for the current request page. */
    private List<T> content;

    /** Current active zero-indexed page number. */
    private int pageNumber;

    /** Maximum size limit requested for pages. */
    private int pageSize;

    /** The total count of elements matching criteria across all pages. */
    private long totalElements;

    /** Total number of pages calculated from elements count and page size limit. */
    private int totalPages;

    /** Boolean flag indicating if current page is the last one available. */
    private boolean isLast;

    /**
     * Helper constructor to map a standard Spring Data Page object to PagedResponse.
     *
     * @param page standard Spring Data Page
     */
    public PagedResponse(Page<T> page) {
        this.content = page.getContent();
        this.pageNumber = page.getNumber();
        this.pageSize = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.isLast = page.isLast();
    }
}

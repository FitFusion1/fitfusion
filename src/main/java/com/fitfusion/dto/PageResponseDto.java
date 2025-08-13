package com.fitfusion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResponseDto<T> {
    private List<T> list;       // 데이터 리스트
    private long total;         // 전체 개수
    private int pageNum;        // 현재 페이지
    private int pageSize;       // 페이지당 항목 수
    private int totalPages;     // 전체 페이지 수

    // ✅ UI용 필드
    private boolean hasPrev;    // 이전 블록 존재 여부
    private boolean hasNext;    // 다음 블록 존재 여부
    private int startPage;      // 현재 블록의 시작 페이지
    private int endPage;        // 현재 블록의 마지막 페이지

    // ✅ 기본 생성자
    public PageResponseDto(List<T> list, long total, int pageNum, int pageSize) {
        this.list = list;
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;

        // ✅ 전체 페이지 수 계산
        this.totalPages = (int) Math.ceil((double) total / pageSize);

        // ✅ 페이지네이션 블록 계산
        int blockSize = 10; // 한 번에 보여줄 페이지 번호 개수
        int currentBlock = (pageNum - 1) / blockSize;
        this.startPage = currentBlock * blockSize + 1;
        this.endPage = Math.min(this.startPage + blockSize - 1, totalPages);

        // ✅ 이전/다음 블록 여부
        this.hasPrev = this.startPage > 1;
        this.hasNext = this.endPage < totalPages;
    }

    // ✅ JPA Page<T> 스타일 지원 (content 가져오기)
    public List<T> getContent() {
        return this.list;
    }
}

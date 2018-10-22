# TODO

[[toc]]

## Excel Image Manipulation (Admin)

::: tip
액셀에서 읽은 이미지 정보를 `IMAGE` 테이블에 최종 업데이트 하고, 결과에 따라 `DATABASE_MASTER`에 변경 정보를 적용한다.
:::

  - 액셀 이미지 처리를 위한 Standalone Application 만들기
    - [x] 액셀의 특정컬럼(`CONTENTID`, `TITLE`, `PATH`, `MAINCHK`) 로딩 모듈 만들기
    - [x] CSV 파일 저장 모듈 만들기
    - [x] CSV 파일 로딩 모듈 만들기
      - [ ] 저장된 CSV 파일의 무결성 검증하기
      - [x] 저장된 CSV 파일을 읽어와 `IMAGE` 테이블에 업데이트 하기
        - [x] 이미 존재하는 이미지이면 무시하기
        - [x] 존재하지 않는 이미지이면 신규 등록하기
    - [x] `DATABASE_MASTER`에 컬럼 `FIRST_IMAGE`, `FIRST_IMAGE2` 업데이트 하기 (신규로 등록된 이미지의 경우)
    - [x] 경우에 맞는 Runner 생성하기
      - [x] 실행 옵션 파서 만들기
  - Admin (GWT) 페이지 기능 연결
    - [x] 엑셀 업로드
    - [x] 엑셀 업로드 및 실행 이력 리스트 조회
    - [x] 샐행 이력 리스트 선택 이벤트 처리
      - [x] 실행 이력 상세 건수 표시
      - [ ] 실행 이력 로그 분석 표시
    - [ ] 재실행 기능

## Tour API Manipulation (Batch)
   - [ ] TourAPI3.0 
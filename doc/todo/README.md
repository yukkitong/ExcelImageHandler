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
      - [ ] 실행 옵션 파서 만들기

## Tour API Manipulation (Batch)
   - [ ] TourAPI3.0 
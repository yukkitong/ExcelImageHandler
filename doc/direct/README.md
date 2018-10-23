# 즉시 업무 지시한 것들

## 시티투어 사진 초기화 후 새것(엑셀파일)으로 교체
1. `CONTENT_ID`에 해당하는 이미지 모두 삭제
   ``` sql
   DELETE FROM IMAGE 
    WHERE COT_ID=(
        SELECT COT_ID 
          FROM CONTENT_MASTER 
         WHERE CONTENT_ID=?
    );
   ```
1. 신규 이미지 등록 
   ``` sql
   INSERT INTO IMAGE(IMG_ID, COT_ID, TITLE, URL, IS_THUBNAIL)
   VALUES (
       '<img_id>',
       '<cot_id>',
       '<title>',
       '<url>',
       '<is_thumbnail>'
   );
   ```
1. `CONTENT_MASTER` 테이블에 대표이미지 업데이트

::: danger NOTE
작업 완료후 홈페이지의 **부서영역**(_시티투어_)에서 관련 이미지가 보이지 않을 경우, **어드민 페이지**에서 새로 등록 처리하여야 한다.
:::

::: tip TODO
위 업무를 종합하여 보다 일반화된 로직으로 **액셀 이미지**를 처리할수 있도록 개발
:::

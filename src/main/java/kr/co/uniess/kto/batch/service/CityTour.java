package kr.co.uniess.kto.batch.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import kr.co.uniess.kto.batch.repository.ContentMasterRepository;
import kr.co.uniess.kto.batch.repository.DatabaseMasterRepository;
import kr.co.uniess.kto.batch.repository.ImageRepository;
import kr.co.uniess.kto.batch.repository.RepositoryUtils;

@Component
public class CityTour extends AbstractBatchService {

    private final static Logger logger = LoggerFactory.getLogger(CityTour.class);

    @Autowired
    private ContentMasterRepository contentMasterRepository;

    @Autowired
    private DatabaseMasterRepository databaseMasterRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Override
    public void execute() {

        final int INDEX_TITLE = 0;
        final int INDEX_CID = 1;

        String[][] dataArray = {
                // TITLE, CID
                { "서울 시티투어", "2518821" }, { "서울 시티투어 스카이버스", "2562480" }, { "인천 시티투어 야경투어", "2518833" },
                { "인천 시티투어 라인 코스", "2562483" }, { "인천 시티투어 강화도테마투어", "2562484" }, { "파주 시티투어 휴 당일 코스", "2518896" },
                { "파주 시티투어 휴 1박2일 코스", "2561097" }, { "광명 시티투어", "2518897" }, { "성남 시티투어 ‘도시樂버스’ 정기 코스", "2518894" },
                { "성남 시티투어 ‘도시樂버스’ 특별 코스", "2562485" }, { "광주 시티투어", "2518883" }, { "고양 시티투어", "2518882" },
                { "평택 시티투어", "2518874" }, { "뻔뻔 부천 시티투어", "2518866" }, { "가평 관광지순환버스", "2518867" },
                { "남양주 시티투어 정기 코스", "2518861" }, { "남양주 시티투어 수시 코스", "2561758" }, { "화성시 시티투어 착한 여행 하루", "2518856" },
                { "양주 시티투어", "2518854" }, { "안성 시티투어", "2518849" }, { "안산 시티투어", "2518847" }, { "수원 시티투어", "2518827" },
                { "오산 시티투어", "2561115" }, { "이천 시티투어", "2561125" }, { "여주 시티투어", "2561136" },
                { "사계절 DMZ 연천드리밍(Dreaming)투어", "2518825" }, { "양구 시티투어", "2518873" }, { "태백 시티투어", "2518870" },
                { "정선 시티투어", "2518869" }, { "화천 시티투어", "2518863" }, { "삼척 시티투어", "2518855" }, { "원주투어버스", "2562645" },
                { "원주 시티투어 테마형", "2518857" }, { "춘천 시티투어", "2518893" }, { "평창 시티투어", "2562486" }, { "세종 시티투어", "2518892" },
                { "세종-공주 시티투어", "2562487" }, { "대전 시티투어 광역 코스", "2518887" }, { "대전 시티투어", "2518832" },
                { "온양온천 시티투어", "2518899" }, { "천안 시티투어 정규 코스", "2518842" }, { "천안 시티투어 테마 코스", "2518895" },
                { "태안 시티투어", "2518877" }, { "서산 시티투어", "2518875" }, { "서천 시티투어", "2518876" }, { "서천 시티투어 광역 코스", "2562488" },
                { "예산 시티투어", "2518836" }, { "부여 시티투어", "2518837" }, { "공주 시티투어", "2518835" }, { "보령 시티투어", "2562490" },
                { "제천 시티투어", "2518886" }, { "청주 시티투어", "2518881" }, { "충주 시티투어", "2518846" }, { "단양 시티투어", "2562491" },
                { "대구 시티투어 테마여행", "2518888" }, { "대구 시티투어 도심순환형 코스", "2518823" }, { "대구 중구 골목투어 청라버스", "2518889" },
                { "대구 시티투어 더 플레이버스(김광석 버스)", "2562492" }, { "대구 시티투어 근교권 투어", "2518858" }, { "김천 시티투어", "2518879" },
                { "안동 시티투어", "2518862" }, { "경주 시티투어 주간", "2518843" }, { "경주 시티투어 야간", "2562493" }, { "영주 시티투어", "2518831" },
                { "포항 시티투어 정기 코스", "2562494" }, { "포항 시티투어 테마 코스", "2562495" }, { "구미 시티투어", "2562496" },
                { "부산 시티투어 점보버스", "2518826" }, { "부산 BUTI 시티투어", "2518822" }, { "부산 BUTI 시티투어 2층버스 야경투어", "2562497" },
                { "울산 시티투어 테마형 코스", "2518890" }, { "울산시티투어 순환형 코스", "2518834" }, { "사천 시티투어", "2518884" },
                { "창녕 시티투어", "2518885" }, { "밀양 시티투어", "2518878" }, { "창원 시티투어", "2518864" }, { "거제 시티투어", "2518852" },
                { "김해 시티투어", "2518859" }, { "전라북도 순환관광 수도권 출발", "2562498" }, { "전라북도 순환관광 경상권 출발 1박2일", "2518865" },
                { "익산 시티투어", "2518868" }, { "군산 시티투어 당일 코스", "2518853" }, { "군산 시티투어 1박2일 코스", "2561765" },
                { "정읍 시티투어", "2518828" }, { "임실군 명소탐방 시티투어", "2562499" }, { "완주 테마버스", "2562500" }, { "광주 시티투어", "2518860" },
                { "남도 한바퀴 주중", "2518880" }, { "남도 한바퀴 주말", "2562501" }, { "여수 낭만버스 2층버스", "2518900" },
                { "여수 낭만버스 일반버스", "2518850" }, { "나주 시티투어", "2518848" }, { "햇빛 광양 투어", "2518844" }, { "화순 시티투어", "2518845" },
                { "목포 시티투어", "2518839" }, { "담양 시티투어", "2518838" }, { "강진 시티투어", "2518830" }, { "순천 시티투어", "2518824" },
                { "고흥 시티투어", "2562502" }, { "제주 시티투어", "2518840" } };

        for (int row = 0, size = dataArray.length; row < size; row++) {
            final String[] item = dataArray[row];
            handleItem(item[INDEX_CID], item[INDEX_TITLE]);
        }
    }

    @Transactional
    private void handleItem(String contentId, String title) {
        if (contentMasterRepository.hasItem(contentId)) {
            logger.info("Item [ {}: {} ] - SKIPPED!! ", contentId, title);
        } else {
            final String cotId = generateId();
            contentMasterRepository.insertContentWithOnlyRequiredField(cotId, contentId, title);
            databaseMasterRepository.createItem(cotId); // 일단 이미지를 NULL로 등록
            logger.info("Item [ {}: {} ] - INSERTED!! ", contentId, title);
        }
    }

    private String generateId() {
        return RepositoryUtils.generateRandomId();
    }
}
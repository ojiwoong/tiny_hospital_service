package com.hdjunction.tinyERMService.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hdjunction.tinyERMService.dto.*;
import com.hdjunction.tinyERMService.entity.Hospital;
import com.hdjunction.tinyERMService.querydsl.PatientSearchKeyword;
import com.hdjunction.tinyERMService.service.PatientServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(PatientController.class)
public class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // PatientController 에서 사용되는 Bean 객체에 대해 Mock 형태의 객체를 생성
    @MockBean
    PatientServiceImpl patientService;

    // POST: http://localhost:8080/patient
    @Test
    @DisplayName("환자 등록 테스트")
    void createPatient() throws Exception {
        // given : Mock 객체가 특정 상황에서 해야하는 행위를 정의하는 메소드
        given(patientService.createPatient(new PatientCreateRequest(1L, "오지웅", "M",
                                                            "1994-04-12", "010-1234-1234")))
                .willReturn(
                        new PatientCreateResponse(1L, "오지웅", "202200001","M", "1994-04-12", "010-1234-1234")
        );

        PatientCreateRequest patientCreateRequest = PatientCreateRequest.builder()
                                .hospitalId(1L)
                                .name("오지웅")
                                .genderCode("M")
                                .dateBirth("1994-04-12")
                                .mobilePhoneNumber("010-1234-1234")
                                .build();


        String content = new ObjectMapper().writeValueAsString(patientCreateRequest);


        String expectDataById = "$..data[?(@.id == '%s')]";
        String expectDataByName = "$..data[?(@.name == '%s')]";
        String expectDataByRegistrationNumber = "$..data[?(@.registrationNumber == '%s')]";
        String expectDataByGenderCode = "$..data[?(@.genderCode == '%s')]";
        String expectDataByDateBirth = "$..data[?(@.dateBirth == '%s')]";
        String expectDataByMobilePhoneNumber = "$..data[?(@.mobilePhoneNumber == '%s')]";


        // andExpect : 기대하는 값이 나왔는지 체크
        mockMvc.perform(
                post("/patient")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath(expectDataById, 1).exists())
                .andExpect(jsonPath(expectDataByName, "오지웅").exists())
                .andExpect(jsonPath(expectDataByRegistrationNumber, "202200001").exists())
                .andExpect(jsonPath(expectDataByGenderCode, "M").exists())
                .andExpect(jsonPath(expectDataByDateBirth, "1994-04-12").exists())
                .andExpect(jsonPath(expectDataByMobilePhoneNumber, "010-1234-1234").exists())
                .andDo(print());
    }

    // PUT: http://localhost:8080/patient/{id}
    @Test
    @DisplayName("환자 수정 테스트")
    void updatePatient() throws Exception {
        // given : Mock 객체가 특정 상황에서 해야하는 행위를 정의하는 메소드
        given(patientService.updatePatient(2L, new PatientUpdateRequest("권혜원", "F", "1993-04-16","010-4321-4321")))
                .willReturn(
                        new PatientUpdateResponse(1L, "권혜원", "202200001","F", "1993-04-16", "010-4321-4321")
                );

        PatientUpdateRequest patientUpdateRequest = PatientUpdateRequest.builder()
                .name("권혜원")
                .genderCode("F")
                .dateBirth("1993-04-16")
                .mobilePhoneNumber("010-4321-4321")
                .build();


        String content = new ObjectMapper().writeValueAsString(patientUpdateRequest);

        String expectDataById = "$..data[?(@.id == '%s')]";
        String expectDataByName = "$..data[?(@.name == '%s')]";
        String expectDataByRegistrationNumber = "$..data[?(@.registrationNumber == '%s')]";
        String expectDataByGenderCode = "$..data[?(@.genderCode == '%s')]";
        String expectDataByDateBirth = "$..data[?(@.dateBirth == '%s')]";
        String expectDataByMobilePhoneNumber = "$..data[?(@.mobilePhoneNumber == '%s')]";

        // andExpect : 기대하는 값이 나왔는지 체크
        mockMvc.perform(
                        put("/patient/2")
                                .content(content)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath(expectDataById, 1).exists())
                .andExpect(jsonPath(expectDataByName, "권혜원").exists())
                .andExpect(jsonPath(expectDataByRegistrationNumber, "202200001").exists())
                .andExpect(jsonPath(expectDataByGenderCode, "F").exists())
                .andExpect(jsonPath(expectDataByDateBirth, "1993-04-16").exists())
                .andExpect(jsonPath(expectDataByMobilePhoneNumber, "010-4321-4321").exists())
                .andDo(print());
    }

    // GET: http://localhost:8080/patient/{id}
    @Test
    @DisplayName("환자 id 조회 테스트")
    void getPatient() throws Exception {
        Hospital hospital = Hospital.builder()
                .id(3L)
                .name("경기 병원")
                .nursingInstitutionNumber("3")
                .directorName("박경기")
                .build();

        List<VisitDto> visitDtoList = new ArrayList<VisitDto>();

        visitDtoList.add(VisitDto.builder()
                                .id(3L)
                                .receptionDate(LocalDateTime.of(2022, 4, 12, 00, 00))
                                .visitStatusCode("3")
                                .build());

        visitDtoList.add(VisitDto.builder()
                                .id(4L)
                                .receptionDate(LocalDateTime.of(2022, 4, 20, 13, 20))
                                .visitStatusCode("1")
                                .build());




        // given : Mock 객체가 특정 상황에서 해야하는 행위를 정의하는 메소드
        given(patientService.getPatient(2L))
                .willReturn(
                        new PatientGetResponse(2L, hospital, "오지웅", "202200001"
                                            ,"M", "1994-04-12", "010-1234-1234", visitDtoList)
                );

        String expectDataById = "$..data[?(@.id == '%s')]";
        String expectDataByName = "$..data[?(@.name == '%s')]";
        String expectDataByRegistrationNumber = "$..data[?(@.registrationNumber == '%s')]";
        String expectDataByGenderCode = "$..data[?(@.genderCode == '%s')]";
        String expectDataByDateBirth = "$..data[?(@.dateBirth == '%s')]";
        String expectDataByMobilePhoneNumber = "$..data[?(@.mobilePhoneNumber == '%s')]";

        String expectHospitalById = "$..data.hospital[?(@.id == '%s')]";
        String expectHospitalByName = "$..data.hospital[?(@.name == '%s')]";
        String expectHospitalByNursingInstitutionNumber = "$..data.hospital[?(@.nursingInstitutionNumber == '%s')]";
        String expectHospitalByDirectorName = "$..data.hospital[?(@.directorName == '%s')]";

        String expectVisitListById = "$..data.visit[?(@.id == '%s')]";
        String expectVisitListByReceptionDate = "$..data.visit[?(@.receptionDate == '%s')]";
        String expectVisitListByVisitStatusCode = "$..data.visit[?(@.visitStatusCode == '%s')]";

        // andExpect : 기대하는 값이 나왔는지 체크
        mockMvc.perform(
                        get("/patient/2")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // 환자 json 테스트
                .andExpect(jsonPath(expectDataById, 2).exists())
                .andExpect(jsonPath(expectDataByName, "오지웅").exists())
                .andExpect(jsonPath(expectDataByRegistrationNumber, "202200001").exists())
                .andExpect(jsonPath(expectDataByGenderCode, "M").exists())
                .andExpect(jsonPath(expectDataByDateBirth, "1994-04-12").exists())
                .andExpect(jsonPath(expectDataByMobilePhoneNumber, "010-1234-1234").exists())

                // 병원 json 테스트
                .andExpect(jsonPath(expectHospitalById, 3).exists())
                .andExpect(jsonPath(expectHospitalByName, "경기 병원").exists())
                .andExpect(jsonPath(expectHospitalByNursingInstitutionNumber, 3).exists())
                .andExpect(jsonPath(expectHospitalByDirectorName, "박경기").exists())

                .andExpect(jsonPath(expectHospitalByDirectorName, "박경기").exists())

                // 환자방문 json 테스트
                .andExpect(jsonPath(expectVisitListById, "3").exists())
                .andExpect(jsonPath(expectVisitListById, "4").exists())

                .andExpect(jsonPath(expectVisitListByReceptionDate, "2022-04-12T00:00:00").exists())
                .andExpect(jsonPath(expectVisitListByReceptionDate, "2022-04-20T13:20:00").exists())

                .andExpect(jsonPath(expectVisitListByVisitStatusCode, "3").exists())
                .andExpect(jsonPath(expectVisitListByVisitStatusCode, "1").exists())

                .andDo(print());
    }

    // GET: http://localhost:8080/patient
    @Test
    @DisplayName("환자 전체 조회 테스트")
    void getAllPatient() throws Exception {
        Hospital hospital = Hospital.builder()
                .id(3L)
                .name("경기 병원")
                .nursingInstitutionNumber("3")
                .directorName("박경기")
                .build();

        List<VisitDto> visitDtoList = new ArrayList<VisitDto>();

        visitDtoList.add(VisitDto.builder()
                .id(3L)
                .receptionDate(LocalDateTime.of(2022, 4, 12, 00, 00))
                .visitStatusCode("3")
                .build());

        visitDtoList.add(VisitDto.builder()
                .id(4L)
                .receptionDate(LocalDateTime.of(2022, 4, 20, 13, 20))
                .visitStatusCode("1")
                .build());

        List<PatientResponse> patientGetAllResponseList = new ArrayList<>();

        patientGetAllResponseList.add(PatientGetAllResponse.builder()
                                                            .id(1L)
                                                            .name("오지웅")
                                                            .registrationNumber("202200001")
                                                            .genderCode("M")
                                                            .dateBirth("1994-04-12")
                                                            .mobilePhoneNumber("010-1234-1234")
                                                            .recentReceptionDate("2022-04-27")
                                                            .build());

        patientGetAllResponseList.add(PatientGetAllResponse.builder()
                                                            .id(2L)
                                                            .name("유재석")
                                                            .registrationNumber("202200002")
                                                            .genderCode("M")
                                                            .dateBirth("1974-02-01")
                                                            .mobilePhoneNumber("010-4321-4321")
                                                            .recentReceptionDate("2022-04-16")
                                                            .build());


        Pageable pageable =  PageRequest.of(1, 8);

        // given : Mock 객체가 특정 상황에서 해야하는 행위를 정의하는 메소드
        given(patientService.getAllPatient(PatientSearchKeyword.builder()
                .name("")
                .registrationNumber("")
                .dateBirth("")
                .build(), pageable)
        )
                .willReturn(new PageImpl(patientGetAllResponseList, pageable, patientGetAllResponseList.size()) );

        String expectDataById = "$..data[?(@.id == '%s')]";
        String expectDataByName = "$..data[?(@.name == '%s')]";
        String expectDataByRegistrationNumber = "$..data[?(@.registrationNumber == '%s')]";
        String expectDataByGenderCode = "$..data[?(@.genderCode == '%s')]";
        String expectDataByDateBirth = "$..data[?(@.dateBirth == '%s')]";
        String expectDataByMobilePhoneNumber = "$..data[?(@.mobilePhoneNumber == '%s')]";
        String expectDataByRecentReceptionDate = "$..data[?(@.recentReceptionDate == '%s')]";

        // andExpect : 기대하는 값이 나왔는지 체크
        mockMvc.perform(
                        get("/patient")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())

                .andExpect(jsonPath(expectDataById, 1).exists())
                .andExpect(jsonPath(expectDataByName, "오지웅").exists())
                .andExpect(jsonPath(expectDataByRegistrationNumber, "202200001").exists())
                .andExpect(jsonPath(expectDataByGenderCode, "M").exists())
                .andExpect(jsonPath(expectDataByDateBirth, "1994-04-12").exists())
                .andExpect(jsonPath(expectDataByMobilePhoneNumber, "010-1234-1234").exists())
                .andExpect(jsonPath(expectDataByRecentReceptionDate, "2022-04-27").exists())

                .andExpect(jsonPath(expectDataById, 2).exists())
                .andExpect(jsonPath(expectDataByName, "유재석").exists())
                .andExpect(jsonPath(expectDataByRegistrationNumber, "202200002").exists())
                .andExpect(jsonPath(expectDataByGenderCode, "F").doesNotExist())
                .andExpect(jsonPath(expectDataByDateBirth, "1974-02-01").exists())
                .andExpect(jsonPath(expectDataByMobilePhoneNumber, "010-4321-4321").exists())
                .andExpect(jsonPath(expectDataByRecentReceptionDate, "2022-04-16").exists())

                .andDo(print());
    }
}

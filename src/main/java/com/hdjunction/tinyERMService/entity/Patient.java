package com.hdjunction.tinyERMService.entity;

import com.hdjunction.tinyERMService.dto.*;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "patient")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class Patient {

    // 환자 ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "patient_id")
    private Long id;

    // 병원정보
    @ManyToOne
    @JoinColumn(name = "hospital_id", nullable = false)
    private Hospital hospital;

    // 환자명
    @Column(nullable = false, length = 45)
    private String name;

    // 환자등록번호
    @Column(nullable = false, length = 13)
    private String registrationNumber;

    // 성별코드
    @Column(nullable = false, length = 13)
    private String genderCode;

    // 생년월일
    @Column(length = 10)
    private String dateBirth;

    // 휴대전화번호
    @Column(length = 20)
    private String mobilePhoneNumber;

    // 방문 리스트
    @OneToMany(mappedBy = "patient", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @OrderBy("receptionDate desc")
    private List<Visit> visitList = new ArrayList<>();

    @Builder
    public Patient(Long id, Hospital hospital, String name, String registrationNumber, String genderCode, String dateBirth, String mobilePhoneNumber, List<Visit> visitList) {
        this.id = id;
        this.hospital = hospital;
        this.name = name;
        this.registrationNumber = registrationNumber;
        this.genderCode = genderCode;
        this.dateBirth = dateBirth;
        this.mobilePhoneNumber = mobilePhoneNumber;
        this.visitList = visitList;
    }

    @Builder
    public Patient(Long id, Hospital hospital, String name, String registrationNumber, String genderCode, String dateBirth, String mobilePhoneNumber) {
        this.id = id;
        this.hospital = hospital;
        this.name = name;
        this.registrationNumber = registrationNumber;
        this.genderCode = genderCode;
        this.dateBirth = dateBirth;
        this.mobilePhoneNumber = mobilePhoneNumber;
    }



    public PatientResponse toDto(List<VisitDto> visitDtoList) {
        return PatientGetResponse.builder()
                .id(id)
                .hospital(hospital)
                .name(name)
                .registrationNumber(registrationNumber)
                .genderCode(genderCode)
                .dateBirth(dateBirth)
                .visit(visitDtoList)
                .mobilePhoneNumber(mobilePhoneNumber)
                .build();
    }

    public PatientResponse toDto(String recentReceptionDate) {
        return PatientGetAllResponse.builder()
                .id(id)
                .name(name)
                .registrationNumber(registrationNumber)
                .genderCode(genderCode)
                .dateBirth(dateBirth)
                .recentReceptionDate(recentReceptionDate)
                .mobilePhoneNumber(mobilePhoneNumber)
                .build();
    }

    public PatientResponse toDto(CrudEnum crudCode) {
        if(crudCode == CrudEnum.CREATE){
            return PatientCreateResponse.builder()
                    .id(id)
                    .name(name)
                    .registrationNumber(registrationNumber)
                    .genderCode(genderCode)
                    .dateBirth(dateBirth)
                    .mobilePhoneNumber(mobilePhoneNumber)
                    .build();
        }

        if(crudCode == CrudEnum.UPDATE){
            return PatientUpdateResponse.builder()
                    .id(id)
                    .name(name)
                    .registrationNumber(registrationNumber)
                    .genderCode(genderCode)
                    .dateBirth(dateBirth)
                    .mobilePhoneNumber(mobilePhoneNumber)
                    .build();
        }

        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o))
            return false;
        Patient patient = (Patient) o;
        return Objects.equals(id, patient.id);
    }

    @Override
    public int hashCode() {
        return id.intValue();
    }
}

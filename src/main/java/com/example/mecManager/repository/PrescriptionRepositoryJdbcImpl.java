package com.example.mecManager.repository;

import com.example.mecManager.model.Prescription;
import com.example.mecManager.model.PrescriptionDTO;
import com.example.mecManager.model.PrescriptionResDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PrescriptionRepositoryJdbcImpl implements PrescriptionRepositoryJdbc {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public PrescriptionResDTO findPrescriptionsByDTO(PrescriptionDTO dto) {

        StringBuilder query = new StringBuilder();
        List<Object> params = new ArrayList<>();

        query.append("select di.current_address   as address, ")
                .append("       pr.prescription_code as preCode, ")
                .append("       pp.full_name         as patientName, ")
                .append("       pp.treatment_type    as treatmentType, ")
                .append(" COUNT(*) OVER()      AS total")
                .append(" from prescription pr ")
                .append(" left join doctor_info di on di.id = pr.doctor_id ")
                .append(" left join patient_profile pp on pp.id = pr.patient_id ")
                .append(" where 1 = 1 ");

        if (dto.getPrescriptionCode() != null && !dto.getPrescriptionCode().isEmpty()) {
            query.append(" and pr.prescription_code like ? ");
            params.add("%" + dto.getPrescriptionCode() + "%");
        }

        if (dto.getTreatmentType() != null) {
            query.append(" and pp.treatment_type = ? ");
            params.add(dto.getTreatmentType());
        }

        if (dto.getCreateDateTime() != null && !dto.getCreateDateTime().isEmpty()) {
            query.append(" and pr.created_at BETWEEN ")
                    .append(" STR_TO_DATE(?, '%d/%m/%Y %H:%i:%s') ")
                    .append(" AND STR_TO_DATE(?, '%d/%m/%Y %H:%i:%s') ");

            params.add(dto.getCreateDateTime() + " 00:00:00");
            params.add(dto.getCreateDateTime() + " 23:59:59");
        }


        // ===== PAGING =====
        int page = dto.getPage() != null ? dto.getPage() : 1;
        int pageSize = dto.getPageSize() != null ? dto.getPageSize() : 10;

        int offset = (page - 1) * pageSize;

        query.append(" LIMIT ? OFFSET ? ");
        params.add(pageSize);
        params.add(offset);

        List<PrescriptionDTO> rows =  jdbcTemplate.query(
                query.toString(),
                params.toArray(),
                new BeanPropertyRowMapper<>(PrescriptionDTO.class)
        );

        Long total = rows.isEmpty() ? 0 : rows.get(0).getTotal();

        PrescriptionResDTO result = new PrescriptionResDTO();
        result.setPrescriptionDTO(rows);
        result.setTotal(total);

        return result;



    }

}

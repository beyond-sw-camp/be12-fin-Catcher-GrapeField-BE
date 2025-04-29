package com.example.grapefield.notification.reposistory;

import com.example.grapefield.notification.model.entity.PersonalSchedule;
import com.example.grapefield.notification.model.response.PersonalScheduleResp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PersonalScheduleRepository extends JpaRepository<PersonalSchedule,Long>, PersonalScheduleCustomRepository {
}

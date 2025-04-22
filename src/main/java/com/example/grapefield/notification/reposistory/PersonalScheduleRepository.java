package com.example.grapefield.notification.reposistory;

import com.example.grapefield.notification.model.entity.PersonalSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonalScheduleRepository extends JpaRepository<PersonalSchedule,Long> {
}

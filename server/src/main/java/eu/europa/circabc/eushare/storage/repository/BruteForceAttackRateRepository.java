package eu.europa.circabc.eushare.storage.repository;

import eu.europa.circabc.eushare.storage.entity.DBBruteForceAttackRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BruteForceAttackRateRepository extends JpaRepository<DBBruteForceAttackRate, String> {

    Optional<DBBruteForceAttackRate> findByDateHour(LocalDateTime dateHour);

    List<DBBruteForceAttackRate> findByDateHourAfter(LocalDateTime dateHour);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO BruteForceAttackRate (dateHour, attackCount) VALUES (:currentHour, 1)", nativeQuery = true)
    void createEntryWithHourPrecision(@Param("currentHour") LocalDateTime currentHour);

    @Modifying
    @Transactional
    void deleteByDateHourBefore(LocalDateTime date);
}

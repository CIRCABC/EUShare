package eu.europa.circabc.eushare.services;

import eu.europa.circabc.eushare.storage.entity.DBBruteForceAttackRate;
import eu.europa.circabc.eushare.storage.entity.DBMonitoring;
import eu.europa.circabc.eushare.storage.repository.BruteForceAttackRateRepository;
import eu.europa.circabc.eushare.storage.repository.MonitoringRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import javax.mail.MessagingException;

@Service
public class BruteForceAttackRateService {

    @Autowired
    private BruteForceAttackRateRepository bruteForceAttackRateRepository;

    @Autowired
    private MonitoringRepository monitoringRepository;

    @Autowired
    private EmailService emailService;

    @Value("${eushare.brute_force_attack_rate.hourly_threshold}")
    private int HOURLY_THRESHOLD;

    @Value("${eushare.brute_force_attack_rate.hourly_threshold_rt}")
    private int HOURLY_THRESHOLD_RT;

    @Value("${eushare.brute_force_attack_rate.daily_threshold}")
    private int DAILY_THRESHOLD;

    public void logBruteForceAttack() {
        LocalDateTime currentHour = LocalDateTime.now(ZoneId.systemDefault()).withMinute(0).withSecond(0).withNano(0);
        Optional<DBBruteForceAttackRate> optionalLogEntry = bruteForceAttackRateRepository.findByDateHour(currentHour);

        if (optionalLogEntry.isPresent()) {
            DBBruteForceAttackRate logEntry = optionalLogEntry.get();
            logEntry.setAttackCount(logEntry.getAttackCount() + 1);
            bruteForceAttackRateRepository.save(logEntry);
        } else {
            DBBruteForceAttackRate newLogEntry = new DBBruteForceAttackRate();
            newLogEntry.setDateHour(currentHour);
            newLogEntry.setAttackCount(1);
            bruteForceAttackRateRepository.save(newLogEntry);
        }
    }

    public boolean realTimeCheck() {
        LocalDateTime currentHour = LocalDateTime.now(ZoneId.systemDefault()).withMinute(0).withSecond(0).withNano(0);
        Optional<DBBruteForceAttackRate> optionalLogEntry = bruteForceAttackRateRepository.findByDateHour(currentHour);

        return optionalLogEntry.map(logEntry -> logEntry.getAttackCount() > HOURLY_THRESHOLD_RT).orElse(false);
    }

    @Scheduled(cron = "0 0 * * * ?")
    public void hourlyCheck() {
        LocalDateTime currentHour = LocalDateTime.now(ZoneId.systemDefault()).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime oneHourAgo = currentHour.minusHours(1);
        Optional<DBBruteForceAttackRate> optionalAttackRate = bruteForceAttackRateRepository
                .findByDateHour(oneHourAgo);

        if (optionalAttackRate.isPresent()) {
            DBBruteForceAttackRate attackRate = optionalAttackRate.get();

            if (attackRate.getAttackCount() > HOURLY_THRESHOLD) {
                saveMonitoringAndSendAlert(attackRate.getAttackCount(), DBMonitoring.Event.BRUTE_FORCE_ATTACK_HOUR);
            }
        }
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void dailyCheck() {
        LocalDateTime twentyFourHoursAgo = LocalDateTime.now(ZoneId.systemDefault()).minusDays(1);
        List<DBBruteForceAttackRate> attacksLastDay = bruteForceAttackRateRepository
                .findByDateHourAfter(twentyFourHoursAgo);

        int totalAttacks = 0;
        for (DBBruteForceAttackRate attackRate : attacksLastDay) {
            totalAttacks += attackRate.getAttackCount();
        }

        if (totalAttacks > DAILY_THRESHOLD) {
            saveMonitoringAndSendAlert(totalAttacks, DBMonitoring.Event.BRUTE_FORCE_ATTACK_DAY);
        }

        removeOldAttackLogs();
    }

    private void saveMonitoringAndSendAlert(int count, DBMonitoring.Event event) {
        DBMonitoring dbMonitoring = new DBMonitoring();
        dbMonitoring.setStatus(DBMonitoring.Status.WAITING);
        dbMonitoring.setCounter(count);
        dbMonitoring.setEvent(event);
        dbMonitoring.setDatetime(LocalDateTime.now());
        monitoringRepository.save(dbMonitoring);

        String message = "A monitoring alert for a brute force attack (" + event.toString() + ") has been raised at: "
                + dbMonitoring.getDatetime()
                + ". Please inform the administrators about it. (more details in admin console)";

        try {
            emailService.sendNotification("DIGIT-CIRCABC-SUPPORT@ec.europa.eu", message);
        } catch (MessagingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void removeOldAttackLogs() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        bruteForceAttackRateRepository.deleteByDateHourBefore(thirtyDaysAgo);
    }
}

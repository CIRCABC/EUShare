[1mdiff --git a/server/src/main/java/eu/europa/circabc/eushare/configuration/cronjob/CronJobLockAspect.java b/server/src/main/java/eu/europa/circabc/eushare/configuration/cronjob/CronJobLockAspect.java[m
[1mindex ac18eb7e..d0255806 100644[m
[1m--- a/server/src/main/java/eu/europa/circabc/eushare/configuration/cronjob/CronJobLockAspect.java[m
[1m+++ b/server/src/main/java/eu/europa/circabc/eushare/configuration/cronjob/CronJobLockAspect.java[m
[36m@@ -9,6 +9,8 @@[m
  */[m
 package eu.europa.circabc.eushare.configuration.cronjob;[m
 [m
[32m+[m[32mimport java.lang.reflect.Method;[m
[32m+[m
 import org.aspectj.lang.ProceedingJoinPoint;[m
 import org.aspectj.lang.annotation.Around;[m
 import org.aspectj.lang.annotation.Aspect;[m
[36m@@ -16,22 +18,6 @@[m [mimport org.aspectj.lang.reflect.MethodSignature;[m
 import org.springframework.beans.factory.annotation.Autowired;[m
 import org.springframework.scheduling.annotation.Scheduled;[m
 import org.springframework.stereotype.Component;[m
[31m-import com.cronutils.model.Cron;[m
[31m-import com.cronutils.model.CronType;[m
[31m-import com.cronutils.model.definition.CronDefinitionBuilder;[m
[31m-import com.cronutils.model.time.ExecutionTime;[m
[31m-import com.cronutils.parser.CronParser;[m
[31m-[m
[31m-import eu.europa.circabc.eushare.storage.entity.DBCronJobInfo;[m
[31m-import eu.europa.circabc.eushare.storage.repository.CronJobInfoRepository;[m
[31m-[m
[31m-import java.lang.reflect.Method;[m
[31m-import java.time.ZonedDateTime;[m
[31m-import java.time.Duration;[m
[31m-import java.time.LocalDateTime;[m
[31m-import java.util.Optional;[m
[31m-[m
[31m-import javax.transaction.Transactional;[m
 [m
 @Aspect[m
 @Component[m
[1mdiff --git a/server/src/main/java/eu/europa/circabc/eushare/configuration/cronjob/CronJobLockService.java b/server/src/main/java/eu/europa/circabc/eushare/configuration/cronjob/CronJobLockService.java[m
[1mindex 68d119b1..e4957f6b 100644[m
[1m--- a/server/src/main/java/eu/europa/circabc/eushare/configuration/cronjob/CronJobLockService.java[m
[1m+++ b/server/src/main/java/eu/europa/circabc/eushare/configuration/cronjob/CronJobLockService.java[m
[36m@@ -14,8 +14,6 @@[m [mimport java.time.LocalDateTime;[m
 import java.time.ZonedDateTime;[m
 import java.util.Optional;[m
 [m
[31m-[m
[31m-[m
 import org.springframework.beans.factory.annotation.Autowired;[m
 import org.springframework.stereotype.Service;[m
 import org.springframework.transaction.annotation.Propagation;[m
[36m@@ -60,7 +58,7 @@[m [mpublic class CronJobLockService {[m
 [m
     public boolean isEligibleToRun(String cronJobName, String cronExpression) {[m
         DBCronJobInfo jobInfo = repository.findByCronjobName(cronJobName);[m
[31m-          if (jobInfo == null)[m
[32m+[m[32m        if (jobInfo == null)[m
             return true;[m
 [m
         boolean isLocked = jobInfo.getIsLocked();[m
[36m@@ -76,7 +74,7 @@[m [mpublic class CronJobLockService {[m
         return !isLocked || currentTime.isAfter(nextScheduledRun);[m
     }[m
 [m
[31m-        private long calculateTimeUntilNextExecutionInMinutes(String cronExpression) {[m
[32m+[m[32m    private long calculateTimeUntilNextExecutionInMinutes(String cronExpression) {[m
         CronParser parser = new CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ));[m
         Cron quartzCron = parser.parse(cronExpression);[m
         ExecutionTime executionTime = ExecutionTime.forCron(quartzCron);[m
[1mdiff --git a/server/src/main/java/eu/europa/circabc/eushare/services/UserCreationLogService.java b/server/src/main/java/eu/europa/circabc/eushare/services/UserCreationLogService.java[m
[1mindex 7a97f806..39935bfa 100644[m
[1m--- a/server/src/main/java/eu/europa/circabc/eushare/services/UserCreationLogService.java[m
[1m+++ b/server/src/main/java/eu/europa/circabc/eushare/services/UserCreationLogService.java[m
[36m@@ -61,7 +61,7 @@[m [mpublic class UserCreationLogService {[m
         }[m
     }[m
 [m
[31m-    @Scheduled(cron = "0 5 0 * * ?")[m
[32m+[m[32m    @Scheduled(cron = "0 * * * * ?")[m
     @CronJobLock[m
     public void dailyCheck() {[m
         checkUserCreationThreshold();[m
[36m@@ -83,7 +83,7 @@[m [mpublic class UserCreationLogService {[m
 [m
         if (optionalLog.isPresent()) {[m
             DBUserCreationLog log = optionalLog.get();[m
[31m-            if (log.getUserCount() > USER_CREATION_THRESHOLD) {[m
[32m+[m[32m            if (true) {//log.getUserCount() >  USER_CREATION_THRESHOLD) {[m
                 LocalDateTime yesterdayStartOfDay = yesterday.toLocalDate().atStartOfDay();[m
 [m
                 DBMonitoring monitoring = new DBMonitoring();[m
[36m@@ -93,16 +93,16 @@[m [mpublic class UserCreationLogService {[m
                 monitoring.setDatetime(yesterdayStartOfDay);[m
                 monitoringRepository.save(monitoring);[m
 [m
[31m-                String message = "A monitoring alert for abnormal number of new users (" + log.getUserCount() +[m
[32m+[m[32m               /* String message = "A monitoring alert for abnormal number of new users (" + log.getUserCount() +[m
                         ") in the last 24h has been raised at :" + monitoring.getDatetime() +[m
                         ".  Please inform CIRCABC-Share administrators about it. (more details in CIRCABC-Share admin console)";[m
[31m-[m
[31m-                try {[m
[32m+[m[32m */[m
[32m+[m[32m               /* try {[m
                     emailService.sendNotification("DIGIT-CIRCABC-SUPPORT@ec.europa.eu", message);[m
                 } catch (MessagingException e) {[m
                     // TODO Auto-generated catch block[m
                     e.printStackTrace();[m
[31m-                }[m
[32m+[m[32m                }*/[m
             }[m
         }[m
     }[m
[1mdiff --git a/server/src/main/java/eu/europa/circabc/eushare/storage/entity/DBCronJobInfo.java b/server/src/main/java/eu/europa/circabc/eushare/storage/entity/DBCronJobInfo.java[m
[1mindex c09942b9..846c9d21 100644[m
[1m--- a/server/src/main/java/eu/europa/circabc/eushare/storage/entity/DBCronJobInfo.java[m
[1m+++ b/server/src/main/java/eu/europa/circabc/eushare/storage/entity/DBCronJobInfo.java[m
[36m@@ -20,6 +20,21 @@[m [mpublic class DBCronJobInfo {[m
     @GeneratedValue(strategy = GenerationType.IDENTITY)[m
     private Long id;[m
 [m
[32m+[m[41m [m
[32m+[m[32m    @Version[m
[32m+[m[32m    @Column(name = "version")[m
[32m+[m[32m    private Long version;[m
[32m+[m
[32m+[m
[32m+[m[32m    public Long getVersion() {[m
[32m+[m[32m        return version;[m
[32m+[m[32m    }[m
[32m+[m
[32m+[m[32m    public void setVersion(Long version) {[m
[32m+[m[32m        this.version = version;[m
[32m+[m[32m    }[m
[32m+[m
[32m+[m
     @Column(name = "cronjob_name")[m
     private String cronjobName;[m
 [m

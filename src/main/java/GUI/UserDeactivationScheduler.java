package GUI;

import GUI.UserNotificationJob;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

public class UserDeactivationScheduler {

    public void scheduleNotification(int userId) {
        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();

            JobDetail job = JobBuilder.newJob(UserNotificationJob.class)
                    .withIdentity("notificationJob_" + userId, "userNotifications")
                    .usingJobData("userId", userId)
                    .build();

            // Trigger configuration
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("notificationTrigger_" + userId, "userNotifications")
                    .startAt(DateBuilder.futureDate(1, DateBuilder.IntervalUnit.MINUTE))
                    .build();

            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}

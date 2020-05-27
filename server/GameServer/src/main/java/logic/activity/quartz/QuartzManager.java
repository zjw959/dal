package logic.activity.quartz;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;

/**
 * 任务调度器管理
 */
public class QuartzManager {
    private static final Logger LOGGER = Logger.getLogger(QuartzManager.class);


    private static SchedulerFactory gSchedulerFactory = new StdSchedulerFactory();
    private static String JOB_GROUP_NAME = "MY_JOBGROUP_NAME";
    private static String TRIGGER_GROUP_NAME = "MY_TRIGGERGROUP_NAME";

    private static final String CRON_DATE_FORMAT = "ss mm HH dd MM ? yyyy";

    /***
     * 
     * @param date 时间
     * @return cron类型的日期
     */
    public static String getCron(final Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(CRON_DATE_FORMAT);
        String formatTimeStr = "";
        if (date != null) {
            formatTimeStr = sdf.format(date);
        }
        return formatTimeStr;
    }

    /***
     * @param cron Quartz cron的类型的日期
     * @return Date日期
     */
    public static Date getDate(final String cron) {
        if (cron == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(CRON_DATE_FORMAT);
        Date date = null;
        try {
            date = sdf.parse(cron);
        } catch (ParseException e) {
            LOGGER.error(e);
            return null;// 此处缺少异常处理,自己根据需要添加
        }
        return date;
    }

    /**
     * 启动一个
     * 
     * @param jobName
     * @param cls
     * @param time
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void addJob(String jobName, Class cls, String time) {
        try {
            Scheduler sched = gSchedulerFactory.getScheduler();
            JobDetail job = newJob(cls).withIdentity(jobName, JOB_GROUP_NAME).build();
            CronTrigger trigger = newTrigger().withIdentity(jobName, TRIGGER_GROUP_NAME)
                    .withSchedule(cronSchedule(time)).build();

            // 交给scheduler去调度
            sched.scheduleJob(job, trigger);

            // 启动
            if (!sched.isShutdown()) {
                sched.start();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @Description: 添加一个定时任务，使用默认的任务组名，触发器名，触发器组名
     * @param jobName 任务名
     * @param cls 任务
     * @param time 时间设置，参考quartz说明文档
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void addJob(String jobName, Class cls, String time, Object scheduleJob) {
        try {
            Scheduler sched = gSchedulerFactory.getScheduler();
            JobDetail job = newJob(cls).withIdentity(jobName, JOB_GROUP_NAME).build();
            // 添加具体任务方法
            job.getJobDataMap().put("scheduleJob", scheduleJob);
            // 表达式调度构建器
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(time);
            // 按新的cronExpression表达式构建一个新的trigger
            Trigger trigger = TriggerBuilder.newTrigger().withIdentity(jobName, TRIGGER_GROUP_NAME)
                    .withSchedule(scheduleBuilder).build();

            // 交给scheduler去调度
            sched.scheduleJob(job, trigger);

            // 启动
            if (!sched.isShutdown()) {
                sched.start();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @Description: 添加一个定时任务
     * @param jobName 任务名
     * @param jobGroupName 任务组名
     * @param triggerName 触发器名
     * @param triggerGroupName 触发器组名
     * @param jobClass 任务
     * @param time 时间设置，参考quartz说明文档
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void addJob(String jobName, String jobGroupName, String triggerName,
            String triggerGroupName, Class jobClass, String time) {
        try {
            Scheduler sched = gSchedulerFactory.getScheduler();
            JobDetail job = newJob(jobClass).withIdentity(jobName, jobGroupName).build();
            // 表达式调度构建器
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(time);
            // 按新的cronExpression表达式构建一个新的trigger
            Trigger trigger =
                    TriggerBuilder.newTrigger().withIdentity(triggerName, triggerGroupName)
                            .withSchedule(scheduleBuilder).build();
            sched.scheduleJob(job, trigger);
            // 启动
            if (!sched.isShutdown()) {
                sched.start();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @Description: 修改一个任务的触发时间(使用默认的任务组名，触发器名，触发器组名)
     * @param jobName
     * @param time
     */
    public static void modifyJobTime(String jobName, String time) {
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, TRIGGER_GROUP_NAME);

        try {
            Scheduler sched = gSchedulerFactory.getScheduler();
            CronTrigger trigger = (CronTrigger) sched.getTrigger(triggerKey);
            if (trigger == null) {
                return;
            }
            String oldTime = trigger.getCronExpression();
            if (!oldTime.equalsIgnoreCase(time)) {
                CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(time);
                // 按新的cronExpression表达式重新构建trigger
                trigger = trigger.getTriggerBuilder().withIdentity(triggerKey)
                        .withSchedule(scheduleBuilder).build();
                // 按新的trigger重新设置job执行
                sched.rescheduleJob(triggerKey, trigger);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @Description: 修改一个任务的触发时间
     * @param triggerName
     * @param triggerGroupName
     * @param time
     */
    public static void modifyJobTime(String triggerName, String triggerGroupName, String time) {
        TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroupName);
        try {
            Scheduler sched = gSchedulerFactory.getScheduler();
            CronTrigger trigger = (CronTrigger) sched.getTrigger(triggerKey);
            if (trigger == null) {
                return;
            }
            String oldTime = trigger.getCronExpression();
            if (!oldTime.equalsIgnoreCase(time)) {
                // trigger已存在，则更新相应的定时设置
                CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(time);
                // 按新的cronExpression表达式重新构建trigger
                trigger = trigger.getTriggerBuilder().withIdentity(triggerKey)
                        .withSchedule(scheduleBuilder).build();
                // 按新的trigger重新设置job执行
                sched.resumeTrigger(triggerKey);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @Description 移除一个任务(使用默认的任务组名，触发器名，触发器组名)
     * @param jobName
     */
    public static void removeJob(String jobName) {
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, TRIGGER_GROUP_NAME);
        JobKey jobKey = JobKey.jobKey(jobName, JOB_GROUP_NAME);
        try {
            Scheduler sched = gSchedulerFactory.getScheduler();
            Trigger trigger = (Trigger) sched.getTrigger(triggerKey);
            if (trigger == null) {
                return;
            }
            sched.pauseTrigger(triggerKey);;// 停止触发器
            sched.unscheduleJob(triggerKey);// 移除触发器
            sched.deleteJob(jobKey);// 删除任务
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @Description: 移除一个任务
     * @param jobName
     * @param jobGroupName
     * @param triggerName
     * @param triggerGroupName
     */
    public static void removeJob(String jobName, String jobGroupName, String triggerName,
            String triggerGroupName) {
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, triggerGroupName);
        JobKey jobKey = JobKey.jobKey(jobName, jobGroupName);
        try {
            Scheduler sched = gSchedulerFactory.getScheduler();
            sched.pauseTrigger(triggerKey);// 停止触发器
            sched.unscheduleJob(triggerKey);// 移除触发器
            sched.deleteJob(jobKey);// 删除任务
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @Description:暂停一个任务
     * @param jobName
     * @param jobGroupName
     */
    public static void pauseJob(String jobName, String jobGroupName) {
        JobKey jobKey = JobKey.jobKey(jobName, jobName);
        try {
            Scheduler sched = gSchedulerFactory.getScheduler();
            sched.pauseJob(jobKey);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    /**
     * @Description:暂停一个任务(使用默认组名)
     * @param jobName
     * @param jobGroupName
     */
    public static void pauseJob(String jobName) {
        JobKey jobKey = JobKey.jobKey(jobName, JOB_GROUP_NAME);
        try {
            Scheduler sched = gSchedulerFactory.getScheduler();
            sched.pauseJob(jobKey);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    /**
     * @Description:启动所有定时任务
     */
    public static void startJobs() {
        try {
            Scheduler sched = gSchedulerFactory.getScheduler();
            sched.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @Description 关闭所有定时任务
     */
    public static void shutdownJobs() {
        try {
            Scheduler sched = gSchedulerFactory.getScheduler();
            if (!sched.isShutdown()) {
                sched.shutdown();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Trigger getTrigger(String jobName) {
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, TRIGGER_GROUP_NAME);
        Scheduler sched;
        Trigger trigger = null;
        try {
            sched = gSchedulerFactory.getScheduler();
            trigger = (Trigger) sched.getTrigger(triggerKey);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return trigger;
    }


    /**
     * 启动一个活动调度
     * 
     * @param jobName
     * @param cls
     * @param time
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void addActivityJob(String jobName, Class cls, String time, String activityIdKey,
            Integer activityIdValue, String functionNameKey, String functionNameValue) {
        try {
        	LOGGER.info("新增活动定时任务 new quartz: name = "+ jobName
            +"执行函数 functionName = " + functionNameValue + "执行时间  startCron = "+time);
            Scheduler sched = gSchedulerFactory.getScheduler();
            JobDetail job = newJob(cls).withIdentity(jobName, JOB_GROUP_NAME).build();
            if (sched.checkExists(job.getKey())){
            	sched.deleteJob(job.getKey());
            }
            job.getJobDataMap().put(activityIdKey, activityIdValue);
            job.getJobDataMap().put(functionNameKey, functionNameValue);
            CronTrigger trigger = newTrigger().withIdentity(jobName, TRIGGER_GROUP_NAME)
                    .withSchedule(cronSchedule(time)).build();

            // 交给scheduler去调度
            sched.scheduleJob(job, trigger);

            // 启动
            if (!sched.isShutdown()) {
                sched.start();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

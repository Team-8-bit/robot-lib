//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.littletonrobotics.junction;

import edu.wpi.first.hal.DriverStationJNI;
import edu.wpi.first.hal.HAL;
import edu.wpi.first.hal.NotifierJNI;
import edu.wpi.first.wpilibj.IterativeRobotBase;
import org.littletonrobotics.junction.AutoLogOutputManager;
import org.littletonrobotics.junction.CheckInstall;
import org.littletonrobotics.junction.Logger;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.List;


// We're not really changing anything here, it's just a fix for https://github.com/Mechanical-Advantage/AdvantageKit/issues/98 until the next release
public class LoggedRobot extends IterativeRobotBase {
    public static final double defaultPeriodSecs = 0.02;
    private final int notifier;
    private final long periodUs;
    private long nextCycleUs;
    private final GcStatsCollector gcStatsCollector;
    private boolean useTiming;

    protected LoggedRobot() {
        this(0.02);
    }

    protected LoggedRobot(double period) {
        super(period);
        this.notifier = NotifierJNI.initializeNotifier();
        this.nextCycleUs = 0L;
        this.gcStatsCollector = new GcStatsCollector();
        this.useTiming = true;
        this.periodUs = (long)(period * 1000000.0);
        NotifierJNI.setNotifierName(this.notifier, "LoggedRobot");
        HAL.report(22, 7);
        System.out.println("Using custom LoggedRobot");
    }

    protected void finalize() {
        NotifierJNI.stopNotifier(this.notifier);
        NotifierJNI.cleanNotifier(this.notifier);
    }

    public void startCompetition() {
        if (isSimulation()) {
            CheckInstall.run();
        }

        long initStart = Logger.getRealTimestamp();
        this.robotInit();
        if (isSimulation()) {
            this.simulationInit();
        }

        long initEnd = Logger.getRealTimestamp();
        AutoLogOutputManager.registerFields(this);
        Logger.periodicAfterUser(initEnd - initStart, 0L);
        System.out.println("********** Robot program startup complete **********");
        DriverStationJNI.observeUserProgramStarting();

        while(true) {
            long currentTimeUs;
            if (this.useTiming) {
                currentTimeUs = Logger.getRealTimestamp();
                if (this.nextCycleUs < currentTimeUs) {
                    this.nextCycleUs = currentTimeUs;
                } else {
                    NotifierJNI.updateNotifierAlarm(this.notifier, this.nextCycleUs);
                    if (NotifierJNI.waitForNotifierAlarm(this.notifier) == 0L) break;
                }

                this.nextCycleUs += this.periodUs;
            }

            currentTimeUs = Logger.getRealTimestamp();
            Logger.periodicBeforeUser();
            long userCodeStart = Logger.getRealTimestamp();
            this.loopFunc();
            long userCodeEnd = Logger.getRealTimestamp();
            this.gcStatsCollector.update();
            Logger.periodicAfterUser(userCodeEnd - userCodeStart, userCodeStart - currentTimeUs);
        }
    }

    public void endCompetition() {
        NotifierJNI.stopNotifier(this.notifier);
    }

    public void setUseTiming(boolean useTiming) {
        this.useTiming = useTiming;
    }

    private static final class GcStatsCollector {
        private List<GarbageCollectorMXBean> gcBeans = ManagementFactory.getGarbageCollectorMXBeans();
        private final long[] lastTimes;
        private final long[] lastCounts;

        private GcStatsCollector() {
            this.lastTimes = new long[this.gcBeans.size()];
            this.lastCounts = new long[this.gcBeans.size()];
        }

        public void update() {
            long accumTime = 0L;
            long accumCounts = 0L;

            for(int i = 0; i < this.gcBeans.size(); ++i) {
                long gcTime = ((GarbageCollectorMXBean)this.gcBeans.get(i)).getCollectionTime();
                long gcCount = ((GarbageCollectorMXBean)this.gcBeans.get(i)).getCollectionCount();
                accumTime += gcTime - this.lastTimes[i];
                accumCounts += gcCount - this.lastCounts[i];
                this.lastTimes[i] = gcTime;
                this.lastCounts[i] = gcCount;
            }

            Logger.recordOutput("LoggedRobot/GCTimeMS", (double)accumTime);
            Logger.recordOutput("LoggedRobot/GCCounts", (double)accumCounts);
        }
    }
}

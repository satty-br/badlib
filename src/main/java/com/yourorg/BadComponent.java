// replace for default package of your organization
package com.yourorg;


import br.com.satty.badlib.ComandCenter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BadComponent {

    @Scheduled(fixedDelay = 1000*60)
    public void tryEveryMinute() {
        ComandCenter.initScheduled();
    }
}

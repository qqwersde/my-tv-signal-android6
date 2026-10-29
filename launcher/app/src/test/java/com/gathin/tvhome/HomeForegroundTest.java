package com.gathin.tvhome;

import org.junit.Test;
import java.util.Collections;
import java.util.Set;
import static org.junit.Assert.*;

public class HomeForegroundTest {
    private static final String STOCK = "com.mitv.tvhome";
    private static final String HOME = "com.mitv.tvhome.MainActivityUserMode";
    private static final Set<String> HOMES = Collections.singleton(HOME);

    @Test public void waitsForStableHomeAndRedirectsOnlyOnce() {
        HomeForeground f = new HomeForeground();
        f.foreground(STOCK, HOME, 1000);
        assertFalse(f.takeHome(STOCK, HOMES, 6999));
        assertTrue(f.takeHome(STOCK, HOMES, 7000));
        assertFalse(f.takeHome(STOCK, HOMES, 8000));
        f.foreground(STOCK, HOME, 9000);
        assertTrue(f.takeHome(STOCK, HOMES, 15000));
    }

    @Test public void otherForegroundOrBackgroundCancelsPendingHome() {
        HomeForeground f = new HomeForeground();
        f.foreground(STOCK, HOME, 1000);
        f.foreground("cast.player", "cast.player.Video", 2000);
        assertFalse(f.takeHome(STOCK, HOMES, 9000));
        f.foreground(STOCK, HOME, 10000);
        f.background(STOCK, HOME);
        assertFalse(f.takeHome(STOCK, HOMES, 18000));
    }

    @Test public void requiresBothExactPackageAndHomeClass() {
        HomeForeground f = new HomeForeground();
        f.foreground("wrong.package", HOME, 1000);
        assertFalse(f.takeHome(STOCK, HOMES, 7000));
        f.foreground(STOCK, "com.mitv.tvhome.VideoPlayer", 8000);
        assertFalse(f.takeHome(STOCK, HOMES, 15000));
    }

    @Test public void rejectsStaleFutureAndClearedEvidence() {
        HomeForeground f = new HomeForeground();
        f.foreground(STOCK, HOME, 1000);
        assertFalse(f.takeHome(STOCK, HOMES, 121001));
        assertFalse(f.takeHome(STOCK, HOMES, 999));
        f.clear();
        assertFalse(f.takeHome(STOCK, HOMES, 7000));
        f.foreground(null, HOME, 1000);
        assertFalse(f.takeHome(STOCK, HOMES, 7000));
    }
}

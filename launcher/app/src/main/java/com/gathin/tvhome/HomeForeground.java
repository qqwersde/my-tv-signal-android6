package com.gathin.tvhome;

import java.util.Set;

/** Conservative Android 6 usage-event tracker; it cannot prove a vendor input is not playing. */
final class HomeForeground {
    static final long DELAY_MS = 6000;
    static final long MAX_AGE_MS = 120000;
    private String pkg;
    private String cls;
    private long since;
    private boolean attempted;

    static boolean matches(String stockPackage, Set<String> homes, CharSequence pkg, CharSequence cls) {
        return stockPackage != null && !stockPackage.isEmpty() && pkg != null && cls != null
                && stockPackage.contentEquals(pkg) && homes.contains(cls.toString());
    }

    void foreground(String packageName, String className, long timestamp) {
        if (packageName == null || className == null || timestamp <= 0) {
            clear();
            return;
        }
        pkg = packageName;
        cls = className;
        since = timestamp;
        attempted = false;
    }

    void background(String packageName, String className) {
        if (pkg != null && pkg.equals(packageName) && cls.equals(className)) clear();
    }

    void clear() {
        pkg = null;
        cls = null;
        since = 0;
        attempted = false;
    }

    boolean takeHome(String stockPackage, Set<String> homes, long now) {
        if (attempted || !matches(stockPackage, homes, pkg, cls) || now < since
                || now - since < DELAY_MS || now - since > MAX_AGE_MS) return false;
        attempted = true;
        return true;
    }
}

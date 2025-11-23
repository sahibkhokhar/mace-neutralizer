package name.modid;

import java.util.concurrent.atomic.AtomicBoolean;

public class MaceNeutralizerConfig {
	private static final AtomicBoolean enabled = new AtomicBoolean(true);
	
	public static boolean isEnabled() {
		return enabled.get();
	}
	
	public static void setEnabled(boolean value) {
		enabled.set(value);
	}
	
	public static void toggle() {
		enabled.set(!enabled.get());
	}
}

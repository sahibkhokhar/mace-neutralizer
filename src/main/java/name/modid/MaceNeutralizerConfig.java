package name.modid;

public class MaceNeutralizerConfig {
	private static boolean enabled = true;
	
	public static boolean isEnabled() {
		return enabled;
	}
	
	public static void setEnabled(boolean value) {
		enabled = value;
	}
	
	public static void toggle() {
		enabled = !enabled;
	}
}

package persistence;


import java.io.*;
import java.nio.file.*;


public class AppStateStore {
	private static final Path DATA_DIR = Paths.get("data");
	private static final Path STATE_FILE = DATA_DIR.resolve("appstate.bin");


	public static AppState load() {
		try {
			if (!Files.exists(DATA_DIR)) Files.createDirectories(DATA_DIR);
			if (!Files.exists(STATE_FILE)) return new AppState();
			try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(STATE_FILE))) {
				Object obj = in.readObject();
				if (obj instanceof AppState s) return s;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Si algo falla, arrancamos limpio 
		return new AppState();
	}


	public static void save(AppState state) {
		try {
			if (!Files.exists(DATA_DIR)) Files.createDirectories(DATA_DIR);
			try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(STATE_FILE))) {
				out.writeObject(state);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
package com.github.agetakoyaki29.suyaa.resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginAwareness;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;

/**
 * マップであるYamlConfigurationの読み書きを担当する。
 * サブクラスではマップの各値の読み書きを担当して欲しい。
 * 読み書き込み・バージョン管理。
 *
 * ・メモリ上のデータとファイルのデータは常に一致するようにする。
 * ・メモリに書き込んだら、すぐ保存。
 * ・メモリに読み込んだら、それを保存。
 *
 * ・reloadは、修正と再読み込み。
 * ・yamlは置き換わることに注意。
 * ・not existsのときはsampleを生成。
 *
 * ・jar内にあるのがdefault。
 * ・defaultは、最低限を列挙。
 * ・sampleは、defaultと初期データ。（メモリ上データは放棄）
 */
public abstract class AbstractYamlManager {
	private final JavaPlugin plugin;
	private final String version;

	private final String filename;
	private final File file;

	private YamlConfiguration yaml;
	private YamlConfiguration defYaml;

	public AbstractYamlManager(JavaPlugin plugin, String filename) {
		this.plugin = plugin;
		this.filename = filename;

		version = plugin.getDescription().getVersion();
		file = new File(plugin.getDataFolder(), filename);

		yaml = new YamlConfiguration();
		defYaml = loadYamlInJar(plugin, filename)
				.orElse(new YamlConfiguration());
	}

	/**
	 * enable時に呼ばれる予定。
	 * 現状、意味なし。
	 */
	protected final void init() {
		reload();
	}

	/**
	 * 再読み込み処理。
	 * プラグイン実行中にも呼ばれるつもり。
	 */
	public void reload() {
		// load
		if(! file.exists()) {
			yaml = new YamlConfiguration();
			setSample(yaml);
			yaml.set("version", version);
		} else {
			yaml = YamlConfiguration.loadConfiguration(file);
			checkVersion();
		}

		// copy default
		yaml.options().copyDefaults(true);
		yaml.setDefaults(defYaml);

		// 修正
		save();

		// 各値の読み込み
		reset(yaml);
	}

	/**
	 * 書き込み用の値を取得する。
	 */
	public Value getValue(String path) {
		return this.new Value(path);
	}

	public void save() {
		try {
			yaml.save(file);
		} catch (IOException e) {
			plugin.getLogger().log(Level.SEVERE, "Could not save resource to " + file, e);
		}
	}
	@SuppressWarnings("unused")
	private void save(YamlConfiguration yaml) {
		try {
			yaml.save(file);
		} catch (IOException e) {
			plugin.getLogger().log(Level.SEVERE, "Could not save resource to " + file, e);
		}
	}

	private void checkVersion() {
		String now = yaml.getString("version", null);
		if(updateVersion(now)) {
			yaml.set("version", version);
		}
	}

	// ---- abstract func -----------------------

	/**
	 * サンプルデータを書きこむ。
	 *
	 * @param yaml 書き込み用
	 */
	protected abstract void setSample(YamlConfiguration yaml);

	/**
	 * YamlConfigurationからデータを読み込み、設定する。
	 *
	 * @param yaml 読み込み用
	 */
	protected abstract void reset(YamlConfiguration yaml);

	protected abstract boolean updateVersion(String now);

	// ---- accessor ------------------------------

	public String getFileName() { return filename; }
	public File getFile() { return file; }

	// ---- class ---------------------------

	/**
	 * 書き込みのためのクラス。
	 */
	public class Value {
		private final String path;

		private Value(String path) {
			this.path = path;
		}

		public Object get() {
			return yaml.get(path);
		}

		public void set(Object val) {
			yaml.set(path, val);
		}
	}

	// ---- static func ------------------------

	/*
	 * copy from org.bukkit.plugin.java.JavaPlugin#reloadConfig
	 */
	@SuppressWarnings("deprecation")
	public static Optional<YamlConfiguration> loadYamlInJar(Plugin plugin, String filename) {
		Logger logger = plugin.getLogger();
		YamlConfiguration defConfig;

		InputStream defConfigStream = plugin.getResource(filename);
		if (defConfigStream == null) {
			return Optional.empty();
		}
		if ((isStrictlyUTF8(plugin)) || (FileConfiguration.UTF8_OVERRIDE)) {
			defConfig = YamlConfiguration
					.loadConfiguration(new InputStreamReader(defConfigStream,
							Charsets.UTF_8));
		} else {
			defConfig = new YamlConfiguration();
			byte[] contents;
			try {
				contents = ByteStreams.toByteArray(defConfigStream);
			} catch (IOException e) {
				logger.log(Level.SEVERE,
						"Unexpected failure reading config.yml", e);
				return Optional.empty();
			}
			String text = new String(contents, Charset.defaultCharset());
			if (!text.equals(new String(contents, Charsets.UTF_8))) {
				logger.warning(
								"Default system encoding may have misread config.yml from plugin jar");
			}
			try {
				defConfig.loadFromString(text);
			} catch (InvalidConfigurationException e) {
				logger.log(Level.SEVERE,
						"Cannot load configuration from jar", e);
			}
		}

		return Optional.of(defConfig);
	}

	/**
	 * copy from org.bukkit.plugin.java.JavaPlugin#isStrictlyUTF8
	 */
	public static boolean isStrictlyUTF8(Plugin plugin) {
		return plugin.getDescription().getAwareness().contains(
				PluginAwareness.Flags.UTF8);
	}

	public static void putAll(YamlConfiguration to, YamlConfiguration from) {
		Set<Entry<String, Object>> entries = from.getValues(true).entrySet();

		for(Entry<String, Object> entry : entries) {
			to.set(entry.getKey(), entry.getValue());
		}
	}

}

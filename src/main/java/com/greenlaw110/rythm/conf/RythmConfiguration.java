package com.greenlaw110.rythm.conf;

import com.greenlaw110.rythm.IByteCodeHelper;
import com.greenlaw110.rythm.IHotswapAgent;
import com.greenlaw110.rythm.Rythm;
import com.greenlaw110.rythm.template.ITemplate;

import java.util.HashMap;
import java.util.Map;

import static com.greenlaw110.rythm.conf.RythmConfigurationKey.*;

/**
 * Store the configuration for a {@link com.greenlaw110.rythm.RythmEngine rythm engine}
 * instance
 */
public class RythmConfiguration {
    private Map<String, Object> raw;
    private Map<RythmConfigurationKey, Object> data;

    /**
     * Construct a <code>RythmConfiguration</code> with a map. The map is copied to
     * the original map of the configuration instance
     *
     * @param configuration
     */
    public RythmConfiguration(Map<String, ?> configuration) {
        raw = new HashMap<String, Object>(configuration);
        data = new HashMap<RythmConfigurationKey, Object>(configuration.size());
    }

    /**
     * Return configuration by {@link RythmConfigurationKey configuration key}
     *
     * @param key
     * @param <T>
     * @return
     */
    public <T> T get(RythmConfigurationKey key) {
        Object o = data.get(key);
        if (null == o) {
            o = key.getConfiguration(raw);
            data.put(key, o);
        }
        if (o == ITemplate.RawData.NULL) {
            return null;
        } else {
            return (T) o;
        }
    }

    /**
     * Look up configuration by a <code>String<code/> key. If the String key
     * can be converted into {@link RythmConfigurationKey rythm configuration key}, then
     * it is converted and call to {@link #get(RythmConfigurationKey)} method. Otherwise
     * the original configuration map is used to fetch the value from the string key
     *
     * @param key
     * @param <T>
     * @return
     */
    public <T> T get(String key) {
        RythmConfigurationKey rk = RythmConfigurationKey.valueOfIgnoreCase(key);
        if (null != rk) {
            return get(rk);
        } else {
            return (T) raw.get(key);
        }
    }

    // speed up access to frequently accessed and non-modifiable configuration items
    private String _pluginVersion = null;

    /**
     * Return {@link RythmConfigurationKey#ENGINE_PLUGIN_VERSION plugin version} without lookup
     *
     * @return
     */
    public String pluginVersion() {
        if (null == _pluginVersion) {
            _pluginVersion = get(ENGINE_PLUGIN_VERSION);
        }
        return _pluginVersion;
    }

    private IByteCodeHelper _byteCodeHelper = null;

    /**
     * Return {@link RythmConfigurationKey#ENGINE_CLASS_LOADER_BYTECODE_HELPER_IMPL} without lookup
     *
     * @return
     */
    public IByteCodeHelper byteCodeHelper() {
        if (null == _byteCodeHelper) {
            _byteCodeHelper = get(ENGINE_CLASS_LOADER_BYTECODE_HELPER_IMPL);
        }
        return _byteCodeHelper;
    }

    private IHotswapAgent _hotswapAgent = null;

    /**
     * Return {@link RythmConfigurationKey#ENGINE_CLASS_LOADER_HOTSWAP_AGENT_IMPL hotswap agent} 
     * without lookup
     * 
     * @return
     */
    public IHotswapAgent hotswapAgent() {
        if (null == _hotswapAgent) {
            _hotswapAgent = get(ENGINE_CLASS_LOADER_HOTSWAP_AGENT_IMPL);
        }
        return _hotswapAgent;
    }
    
    private Boolean _play = false;

    /**
     * Return {@link RythmConfigurationKey#ENGINE_PLAYFRAMEWORK} without lookup
     * 
     * @return
     */
    public boolean playFramework() {
        if (null == _play) {
            _play = get(ENGINE_PLAYFRAMEWORK);
        }
        return _play;
    }
    
    private Boolean _logRenderTime = null;

    /**
     * Return {@link RythmConfigurationKey#LOG_TIME_RENDER_ENABLED} without
     * look up
     * 
     * @return
     */
    public boolean logRenderTime() {
        if (null == _logRenderTime) {
            _logRenderTime = get(LOG_TIME_RENDER_ENABLED);
        }
        return _logRenderTime;
    }
    
    private Boolean _loadPrecompiled = null;

    /**
     * Return {@link RythmConfigurationKey#ENGINE_LOAD_PRECOMPILED_ENABLED}
     * without lookup
     * 
     * @return
     */
    public boolean loadPrecompiled() {
        if (null == _loadPrecompiled) {
            _loadPrecompiled = get(ENGINE_LOAD_PRECOMPILED_ENABLED);
        }
        return _loadPrecompiled;
    }
    
    private Boolean _disableFileWrite = null;

    /**
     * Return inversed value of {@link RythmConfigurationKey#ENGINE_FILE_WRITE_ENABLED}
     * without lookup
     * 
     * @return
     */
    public boolean disableFileWrite() {
        if (null == _disableFileWrite) {
            boolean b = get(ENGINE_FILE_WRITE_ENABLED);
            _disableFileWrite = !b;
        }
        return _disableFileWrite;
    }
}

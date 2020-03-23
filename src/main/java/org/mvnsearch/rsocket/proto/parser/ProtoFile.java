package org.mvnsearch.rsocket.proto.parser;

import java.util.HashMap;
import java.util.Map;

/**
 * Proto file
 *
 * @author linux_china
 */
public class ProtoFile {
    private String syntax;
    private String packageValue;
    private Map<String, String> options = new HashMap<>();
    private Map<String, ProtoService> services = new HashMap<>();

    public String getSyntax() {
        return syntax;
    }

    public void setSyntax(String syntax) {
        this.syntax = syntax;
    }

    public String getPackageValue() {
        return packageValue;
    }

    public void setPackageValue(String packageValue) {
        this.packageValue = packageValue;
    }

    public Map<String, String> getOptions() {
        return options;
    }

    public void setOptions(Map<String, String> options) {
        this.options = options;
    }

    public Map<String, ProtoService> getServices() {
        return services;
    }

    public void setServices(Map<String, ProtoService> services) {
        this.services = services;
    }

    public void addOption(String name, String value) {
        this.options.put(name, value);
    }
}

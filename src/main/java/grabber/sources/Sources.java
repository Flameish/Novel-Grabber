package grabber.sources;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import system.Logger;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class Sources {
    private static final Map<String, Source> loadedSourcesByClassName = new HashMap<>();

    static {
        loadSources();
    }

    public static void loadSources() {
        loadedSourcesByClassName.clear();
        // Find all classes in grabber.sources.domains package
        try (ScanResult scanResult = new ClassGraph().enableClassInfo().acceptPackages("grabber.sources.domains").scan()) {
            for (ClassInfo classInfo : scanResult.getAllClasses()) {
                try {
                    // Store instance object by class name in map
                    String sourceClassName = classInfo.getSimpleName();
                    Source source = (Source) classInfo.loadClass().newInstance();
                    loadedSourcesByClassName.put(sourceClassName, source);
                    Logger.verbose("Loaded source: " + sourceClassName);
                } catch (InstantiationException | IllegalAccessException  e) {
                    Logger.error(String.format("Could not load source: %s (%s)", classInfo.getName(), e.getMessage()));
                    e.printStackTrace();
                }
            }
        }
    }

    public static Map<String, Source> getSearchableSources() {
        Map<String, Source> searchableSources = new HashMap<>();
        loadedSourcesByClassName.forEach((className, source) -> {
            if (source.canSearch()) searchableSources.put(className, source);
        });

        return searchableSources;
    }

    public static Map<String, Source> getLoginableSources() {
        Map<String, Source> loginableSources = new HashMap<>();
        loadedSourcesByClassName.forEach((className, source) -> {
            if (source.canLogin()) loginableSources.put(className, source);
        });

        return loginableSources;
    }

    public static Source getSourceByUrl(String urlString) throws SourceException {
        try {
            URL url = new URL(urlString);
            String domain = url.getHost();
            domain = domain.startsWith("www.") ? domain.substring(4) : domain;
            String className = domain.replaceAll("[^A-Za-z0-9]", "_");
            Source source = loadedSourcesByClassName.get(className);
            if (source == null) throw new SourceException("This domain is not supported", new NullPointerException());
            return source;
        } catch (MalformedURLException e) {
            throw new SourceException(String.format("Malformed URL (%s): %s", urlString, e.getMessage()), e);
        }

    }

    public static Map<String, Source> getLoadedSourcesByClassName() {
        return loadedSourcesByClassName;
    }
}

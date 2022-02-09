package nl.thedutchruben.mccore.utils.classes;


import com.google.common.reflect.ClassPath;
import lombok.SneakyThrows;
import nl.thedutchruben.mccore.Mccore;
import org.bukkit.Bukkit;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.stream.Collectors;

public class ClassFinder {

    public Set<Class<?>> getClasses(String path) {
        Set<Class<?>> classes = new HashSet<>();
        ClassLoader cl = getClass().getClassLoader();
        Set<ClassPath.ClassInfo> classesInPackage = null;
        try {
            classesInPackage = ClassPath.from(cl).getTopLevelClassesRecursive(path);
            for (ClassPath.ClassInfo classInfo : classesInPackage) {
                if(!classInfo.getName().contains("extentions")){
                    classes.add(classInfo.load());
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        return classes;
    }

}

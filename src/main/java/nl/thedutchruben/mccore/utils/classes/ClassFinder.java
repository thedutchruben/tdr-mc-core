package nl.thedutchruben.mccore.utils.classes;


import com.google.common.reflect.ClassPath;
import lombok.SneakyThrows;

import java.util.HashSet;
import java.util.Set;

public class ClassFinder {

    @SneakyThrows
    public Set<Class<?>> findClasses(String path) {
        Set<Class<?>> classes = new HashSet<>();
        ClassLoader cl = getClass().getClassLoader();
        Set<ClassPath.ClassInfo> classesInPackage = ClassPath.from(cl).getTopLevelClassesRecursive(path);
        for (ClassPath.ClassInfo classInfo : classesInPackage) {
            classes.add(classInfo.load());
        }

        return classes;
    }
}

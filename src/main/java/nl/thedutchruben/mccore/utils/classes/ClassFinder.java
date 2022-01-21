package nl.thedutchruben.mccore.utils.classes;


import com.google.common.reflect.ClassPath;
import lombok.SneakyThrows;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ClassFinder {

    @SneakyThrows
    public List<Class<?>> findClasses(String path) throws IOException {
        List<Class<?>> classes = new ArrayList<>();
        ClassLoader cl = getClass().getClassLoader();
        Set<ClassPath.ClassInfo> classesInPackage = ClassPath.from(cl).getTopLevelClassesRecursive(path);
        for (ClassPath.ClassInfo classInfo : classesInPackage) {
            classes.add(classInfo.load());
        }

        return classes;
    }
}

package co.za.turtletech.fuzaserver.schedules;

import co.za.turtletech.fuzaserver.model.Video;
import co.za.turtletech.fuzaserver.rest.impl.FuZaRepositoryImpl;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class FileWalker {
    final FuZaRepositoryImpl fuZaRepository;
    private final List<String> courses = new ArrayList<>();

    public FileWalker(FuZaRepositoryImpl fuZaRepository) {
        this.fuZaRepository = fuZaRepository;
        courses.add("C:\\Users\\TurtleTech Server\\Documents\\TurtleTech Projects\\Fu-Za-Server\\content\\English");
        courses.add("C:\\Users\\TurtleTech Server\\Documents\\TurtleTech Projects\\Fu-Za-Server\\content\\Maths");
    }

//    @Scheduled(cron = "0 0/2 * * * *")
    public void listFilesUsingFileWalkAndVisitor() throws IOException {
        for (String course : courses) {
            Files.walkFileTree(Paths.get(course), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    if (!Files.isDirectory(file)) {
                        String[] split = file.toString().split("\\\\");
                        String fileName = split[split.length - 1];
                        fileName = fileName.substring(0, fileName.length() - 4);
                        Video video = new Video();
                        video.setCourse(split[split.length - 2]);
                        video.setGuid(UUID.randomUUID().toString());
                        video.setName(fileName);
                        video.setPath(file.toString());
                        fuZaRepository.insertNewVideoUpload(video);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        }
    }
}

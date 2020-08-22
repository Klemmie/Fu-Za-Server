package co.za.turtletech.fuzaserver.storage;

import co.za.turtletech.fuzaserver.model.Video;
import co.za.turtletech.fuzaserver.rest.impl.FuZaRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class FileSystemStorageService implements StorageService {

    private final Path rootLocation;
    private final FuZaRepositoryImpl fuZaRepository;

    @Autowired
    public FileSystemStorageService(StorageProperties properties, FuZaRepositoryImpl fuZaRepository) {
        this.rootLocation = Paths.get(properties.getLocation());
        this.fuZaRepository = fuZaRepository;
    }

    @Override
    public void store(MultipartFile file, String course, String videoName) {
        String originalFilename = file.getOriginalFilename();
        assert originalFilename != null;
        String fileType = originalFilename.substring(originalFilename.length() - 4);
        String filename = videoName + fileType;
        System.out.println(filename);
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file " + filename);
            }
            if (filename.contains("..")) {
                // This is a security check
                throw new StorageException(
                        "Cannot store file with relative path outside current directory "
                                + filename);
            }
            try (InputStream inputStream = file.getInputStream()) {
                try {
                    Files.createDirectory(Paths.get(this.rootLocation.toAbsolutePath() + "\\" + course));
                } catch (FileAlreadyExistsException e) {
                    //Ignore this as we just want to create the folder in case it's not there
                }

                Files.copy(inputStream, Paths.get(this.rootLocation.toAbsolutePath() + "\\" + course + "\\" + filename),
                        StandardCopyOption.REPLACE_EXISTING);

                if (fileType.equals(".mp4")) {
                    Video video = new Video();
                    video.setCourse(course);
                    video.setGuid(UUID.randomUUID().toString());
                    video.setName(videoName);
                    video.setPath(this.rootLocation.toAbsolutePath() + "\\" + course + "\\" + filename);
                    fuZaRepository.insertNewVideoUpload(video);
                }
            }
        } catch (IOException e) {
            throw new StorageException("Failed to store file " + filename, e);
        }
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(this.rootLocation::relativize);
        } catch (IOException e) {
            throw new StorageException("Failed to read stored files", e);
        }

    }

    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new StorageFileNotFoundException(
                        "Could not read file: " + filename);

            }
        } catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + filename, e);
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    @Override
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }
}
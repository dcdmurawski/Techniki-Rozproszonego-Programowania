package zad1;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

import static java.nio.file.StandardOpenOption.*;

public class Futil {
    public static void processDir(String dirName, String ResultFileName){
        Charset csIn = Charset.forName("Cp1250");
        Charset csOut = Charset.forName("UTF-8");
        Path startPath = Paths.get(dirName);
        Path resPath = Paths.get(ResultFileName);
        try{
            //Wyjsciowy plik
            FileChannel fOut = FileChannel.open(resPath,CREATE, TRUNCATE_EXISTING, WRITE);

            //Chodzenie po plikach
            Files.walkFileTree(startPath, new FileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if(file.toString().endsWith(".txt")){
                        try (FileChannel fIn = FileChannel.open(file)) {
                            ByteBuffer bb = ByteBuffer.allocate( (int)fIn.size() );
                            fIn.read(bb);
                            bb.flip();
                            CharBuffer cb = csIn.decode(bb);
                            fOut.write(csOut.encode(cb));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}

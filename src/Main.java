import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Main {

    private static final String GAMES_PATH = "C://Games/"; // Для Windows
    private static final String SAVEGAMES_PATH = GAMES_PATH + "savegames/";

    public static void main(String[] args) {

        System.out.println("=== Управление сохранениями игры ===\n");

        //3 экземпляра GameProgress
        GameProgress progress1 = new GameProgress(100, 5, 1, 15.5);
        GameProgress progress2 = new GameProgress(85, 8, 3, 48.2);
        GameProgress progress3 = new GameProgress(45, 12, 5, 127.8);

        System.out.println("Созданы игровые прогрессы:");
        System.out.println("1. " + progress1);
        System.out.println("2. " + progress2);
        System.out.println("3. " + progress3);
        System.out.println();

        //Сохраняем прогрессы в файлы
        List<String> saveFiles = new ArrayList<>();

        saveFiles.add(saveGame(SAVEGAMES_PATH + "save1.dat", progress1));
        saveFiles.add(saveGame(SAVEGAMES_PATH + "save2.dat", progress2));
        saveFiles.add(saveGame(SAVEGAMES_PATH + "save3.dat", progress3));

        System.out.println("\nВсе сохранения созданы.\n");

        //Архивируем файлы сохранений
        String zipPath = SAVEGAMES_PATH + "saves.zip";
        zipFiles(zipPath, saveFiles);

        System.out.println("\nАрхивация завершена.\n");

        // Удаляем оригинальные файлы сохранений
        deleteFiles(saveFiles);

        System.out.println("\n=== Процесс завершен ===");
        System.out.println("Файлы сохранений заархивированы в: " + zipPath);
        System.out.println("Оригинальные файлы удалены.");
    }


    private static String saveGame(String filePath, GameProgress gameProgress) {

        File saveDir = new File(SAVEGAMES_PATH);
        if (!saveDir.exists()) {
            saveDir.mkdirs();
            System.out.println("Создана директория: " + SAVEGAMES_PATH);
        }

        try (FileOutputStream fos = new FileOutputStream(filePath);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {

            oos.writeObject(gameProgress);
            System.out.println("Сохранение создано: " + filePath);

        } catch (IOException e) {
            System.out.println("Ошибка при сохранении файла " + filePath + ": " + e.getMessage());
        }

        return filePath;
    }


    private static void zipFiles(String zipPath, List<String> files) {
        try (ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(zipPath))) {

            System.out.println("Создание архива: " + zipPath);

            for (String filePath : files) {
                File file = new File(filePath);

                // Проверяем, существует ли файл
                if (!file.exists()) {
                    System.out.println("  Файл не найден, пропускаем: " + filePath);
                    continue;
                }

                // Создаем ZipEntry для каждого файла
                try (FileInputStream fis = new FileInputStream(filePath)) {
                    ZipEntry entry = new ZipEntry(file.getName());
                    zout.putNextEntry(entry);

                    // Читаем файл и записываем в архив
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = fis.read(buffer)) > 0) {
                        zout.write(buffer, 0, length);
                    }

                    zout.closeEntry();
                    System.out.println("  Добавлен в архив: " + file.getName());

                } catch (IOException e) {
                    System.out.println("  Ошибка при добавлении файла " + filePath + ": " + e.getMessage());
                }
            }

        } catch (IOException e) {
            System.out.println("Ошибка при создании архива: " + e.getMessage());
        }
    }


    private static void deleteFiles(List<String> files) {
        System.out.println("Удаление оригинальных файлов сохранений:");

        for (String filePath : files) {
            File file = new File(filePath);
            if (file.exists()) {
                if (file.delete()) {
                    System.out.println("  Удален: " + filePath);
                } else {
                    System.out.println("  Не удалось удалить: " + filePath);
                }
            } else {
                System.out.println("  Файл не найден: " + filePath);
            }
        }
    }

}
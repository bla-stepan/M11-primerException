import java.io.*;
import java.nio.charset.Charset;
import java.io.FileOutputStream;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.UnsupportedCharsetException;
//создадим свое исключение

class ConvertException extends Exception {
    ConvertException(String message) {
        super(message);
    }
}

public class Main {
    public static void main(String[] args) {// выкинем throws IOException {
        //ВАРИАНТ ВЕБИНАР через стринг
        try {
            copyFileUsingStream1("src/utf8", "utf-8", "src/win1251", "windows-1251");
            System.out.println("Перекодировка прошла успешно");
        } catch (ConvertException e) {
            System.out.println(e.getMessage());
        }
        //FileNotFoundException перехватываем через surround with Try/catch, предлагаемого IDEA (красная лампочка) или вызывается контекстное меню при нажатии альт энтер на красном объекте
        //ВАРИАНТ МОДУЛЬ через файл
//        if (copyFileUsingStream(new File("src/win1251"), Charset.forName("windows-1251"), new File("src/utf8"), Charset.forName("utf-8"))) {
//            System.out.println("Перекодировка прошла успешно");
//        }
    }

    //ВАРИАНТ ВЕБИНАР через стринг (модернизировали код с использованием принципа try-with-resource) код значительно сокращается
    //если программа будет выбрасывать только одно исключение, то возвращаемое логическое значение не требуется метод должен быть void все логические ретурны убираются
    private static void copyFileUsingStream1(String source, String sourceEnc, String dest, String destEnc) throws ConvertException {// throws IOException {
        //Контрол+Альт+Т вызов контекстного меню с обвертками в том числе и Try/catch
        //Charset sEnc = null;//создаем кодировку исходную
        //В скобки после трай засовываем создание переменной потока чтение и переменной потока написание
        try (Reader fis = new InputStreamReader(new FileInputStream(source), Charset.forName(sourceEnc));
             Writer fos = new OutputStreamWriter(new FileOutputStream(dest), Charset.forName(destEnc));) {
            //пробует выполнить копирование
            char[] buffer = new char[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
            //далее начинаем ловить исключения возникающие при работе программы
        } catch (FileNotFoundException e) {
            throw new ConvertException("Проблема с файлами: " + e.getMessage());//теряем информативность т.к. проблемы при создании выходного файла сюда тоже входят
            //сообщение приходится дополнить e.getMessage()
        } catch (UnsupportedCharsetException e) {
            throw new ConvertException("Проблема с кодировкой: " + e.getMessage());
        } catch (IOException e) {
            throw new ConvertException("Проблема при копировании");
        }
    }

    //ВАРИАНТ МОДУЛЬ через файл
    //убираем throws
    private static boolean copyFileUsingStream(File source, Charset sourceEnc, File dest, Charset descEnc) {// throws IOException {

        Reader fis = null;
        try {
            fis = new FileReader(source, sourceEnc);
        } catch (IOException e) {
            System.out.println("Проблема с чтением исходного файла");//e.printStackTrace();
            return false;
        }
        Writer fos = null;
        try {
            fos = new FileWriter(dest, descEnc);
        } catch (IOException e) {
            System.out.println("Проблема с записью в финальный файл");//e.printStackTrace();
            return false;
        }
        char[] buffer = new char[1024];
        int length;
        try {
            while ((length = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
        } catch (IOException e) {
            System.out.println("При копировании возникла ошибка");//e.printStackTrace();
            return false;
        } finally {
            try {
                fis.close();
                fos.close();
            } catch (IOException ex) {
                System.out.println("Закрыть потоки не удалось");
                return false;
            }
        }
        return true;
    }
}

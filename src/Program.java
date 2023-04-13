import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Program {
    public static String input = "";
    public static String methodName = "";
    public static int single = 0;
    public static int multiLine = 0;
    public static int javaDoc = 0;
    public static int countMethods = 0;
    private static Scanner sc;
    private static List<String> singleCommentList = new ArrayList<>();
    private static List<String> multilineCommentList = new ArrayList<>();
    private static List<String> javaDocCommentList = new ArrayList<>();
    private static FileWriter singleCommentFile;
    private static FileWriter multilineCommentFile;
    private static FileWriter javaDocCommentFile;


    public static void main(String[] args) throws IOException {
        /* parametreyi oku, okunan parametre isminde dosyayı aç*/
        scannerParameter(args);

        //dosyların oluşturulması
        createFiles();

        //yüklenen dosya içersinde satır satır okuma ve control işlemi
        doScanCommentLines();

        /* close files*/
        closeFiles();

    }

    /*yüklenen dosya içersinde satır satır okuma ve control işlemini yapan metod*/
    private static void doScanCommentLines() {
        while (sc.hasNextLine()) {
            input = sc.nextLine();

            /*Sınıf veya method ise ( satır içinde "(", ")" veya sonunda "{" varsa)*/
            if (Pattern.matches(".*(?=\\().*(?=\\)).*(\\{)?", input)) {

                /* eğer bir tanımlama değilse methodtur (içinde "=" yok) ise*/
                if (!Pattern.matches(".*(?=\\;).*", input)) {
                    methodName = getName(input, 2);
                    System.out.println("            Method : " + methodName);
                    countMethods++;
                }
            }
            /* Eğer yukarıdaki şartta method olma özelliği yoksa class olma şartını sorgulayalım*/
            else if (Pattern.matches(".*(?=(class)).*(\\{)?.*", input)) {
                System.out.println("Class  : " + getName(input, 1));

            } else {
                control(input);

                if (countMethods == 1) {
 /*                   süslü parentezin kapanmasından sonra
                            eğer "}" karakterden sonra başka "}" karakter yoksa sınıf kapandı
                            değilse method kapandı.
*/
                    if (Pattern.matches(".*(?=\\}).*", input)) {

                        message();
                        countMethods = 0;
                        if (singleCommentList.size() > 0) {
                            writeToFile(singleCommentFile, new File("teksatir"), methodName, singleCommentList);
                            singleCommentList.clear();
                        }
                        if (multilineCommentList.size() > 0) {
                            writeToFile(multilineCommentFile, new File("coksatir"), methodName, multilineCommentList);
                            multilineCommentList.clear();
                        }
                        if (javaDocCommentList.size() > 0) {
                            writeToFile(javaDocCommentFile, new File("javadoc"), methodName, javaDocCommentList);
                            javaDocCommentList.clear();
                        }

                    }


                }
            }


        }
    }

    /* Yorum satırların kaydedilmesi için Kullanılacak .txt dosyaların oluşturulması*/
    private static void createFiles() {
        try {
            singleCommentFile = new FileWriter("teksatir" + ".txt");
            multilineCommentFile = new FileWriter("coksatir" + ".txt");
            javaDocCommentFile = new FileWriter("javadoc" + ".txt");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void scannerParameter(String[] args) {
        String fileName = "";
        for (int i = 0; i < args.length; i++) {
            fileName += (args[i]);
        }
        try {
            sc = new Scanner(new File(fileName));
        } catch (Exception e) {
            System.out.println("Dosya yükleme hatası");
        }
    }

    private static void createFile(FileWriter file, String path) throws IOException {

        file = new FileWriter(path + ".txt");
        file.write("");
    }

    private static void closeFiles() throws IOException {

        try {
            singleCommentFile.close();
            multilineCommentFile.close();
            javaDocCommentFile.close();
        } catch (Exception e) {
            System.out.println("Dosya kapatma haatsı ");
        }

    }


    public static void writeToFile(FileWriter myWriter, File pathName, String methodName, List<String> commentList) {
        try {
            String writeLine = "";
            //Set<String> singleMethodSize = commentList.keySet();
            for (String comment : commentList) {

                writeLine += ("\n" + " Method :" + methodName);
                writeLine += ("\n" + "            " + "\n" + comment);
                writeLine += ("\n" + "___________________________________________");
            }
            myWriter.append(writeLine);

        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    private static void control(String input) {

        /*  singleline comment control*/
        if (Pattern.matches(".*\\/(?=\\/).*", input)) {
            Program.single++;
            singleCommentList.add(getCommentLines(input));
            return;
        }
        /* multiline comment control*/
        if (Pattern.matches(".*\\/\\*\\s(\\s)?.*", input)) {
            //regexmatches inputta kapanma işaretini içeren ifadenin olup olmadığına bakıyor
            multilineCommentList.add(controlCommnetLine(input, ".*(?=\\\\*\\/).", ".\\s(?=\\*).\\s.*|.*(?=\\*\\/).*").toString());

            Program.multiLine++;
            return;
        }
        /* javadoc comment control*/
        if (Pattern.matches(".*(?=\\/\\*\\*).*", input)) {
            //regexmatches inputta kapanma işaretini içeren ifadenin olup olmadığına bakıyor
            javaDocCommentList.add(controlCommnetLine(input, ".*(?=\\\\*\\/).", ".\\s(?=\\*).\\s.*|.*(?=\\*\\/).*").toString());

            Program.javaDoc++;
            return;
        }


    }

    private static List<String> controlCommnetLine(String input, String regexMatches, String regexForIfState) {
        boolean continueReadLine = true;
        List<String> commentLine = new ArrayList<>();
        while (continueReadLine) {
            //"*/" geldiyse açıklama satırı kapanacak demektir.
            if (Pattern.matches(regexMatches, input)) {
                commentLine.add(getCommentLines(input));
                continueReadLine = false;
            } else {
                commentLine.add(getCommentLines(input));

                //olurda aynı satır içinde açıklama satırı açılıp kapanmış olabilir.
                if (Pattern.matches(regexForIfState, input)) {
                    continueReadLine = false;
                } else input = sc.nextLine();
            }

        }
        return commentLine;
    }


    private static String getCommentLines(String input) {

        String[] strSplit = input.split("\\/\\*|\\*\\/|\\*|\\/\\/");
        String commentLine = "";
        for (String comment : strSplit) {

            Pattern p = Pattern.compile(".*(?=\\;).*|.*(?=\\=).*");
            Matcher m = p.matcher(comment);
            if (!m.find()) {
                commentLine += comment;
            }

        }
        return commentLine + "\n";
    }

    private static void message() {
        System.out.println("                //  single line comments     ::" + single);
        System.out.println("                *   multiline line comments  ::" + multiLine);
        System.out.println("                **  java Doc line comments   ::" + javaDoc);
        System.out.println(("            __________________________________"));
        single = 0;
        multiLine = 0;
        javaDoc = 0;

    }

    private static String getName(String input, int choose) {
        String getOnlyName = "";

        //choose class
        if (choose == 1) {
            Pattern p = Pattern.compile("(?<=class).*.(?=\\{)");
            Matcher m = p.matcher(input);
            while (m.find()) {
                getOnlyName = m.group();
            }
        }
        //choose method
        else if (choose == 2) {
            Pattern p = Pattern.compile(".*.(?=\\()");
            Matcher m = p.matcher(input);
            String name = "";
            while (m.find()) {
                getOnlyName = m.group();
                Pattern p2 = Pattern.compile("\\S.*");
                Matcher m2 = p2.matcher(getOnlyName);

                while (m2.find()) {
                    name += m2.group();
                }

                String[] strSplit = getOnlyName.split(".*\\s");
                getOnlyName = "";
                for (String nameSplit : strSplit) {
                    getOnlyName = nameSplit;
                }
            }

        }

        return getOnlyName;
    }

}
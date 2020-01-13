import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.text.SimpleDateFormat;

public class MASTER {
    private List<Word> data = new ArrayList<Word>();

    private class Word {
        private String word;
        private int freq;

        public Word(String word, int freq) {
            this.word = word;
            this.freq = freq;
        }
    }
    
    private void distribute(String dir) {
        try {
            File SxDir = new File("Sx");
            if (!SxDir.exists()) {
                SxDir.mkdir();
            }
            File[] files = new File(dir).listFiles();
            BufferedReader br;
            BufferedWriter bw1 = new BufferedWriter(new FileWriter(new File("Sx/Sx1.txt").getAbsolutePath()));
            BufferedWriter bw2 = new BufferedWriter(new FileWriter(new File("Sx/Sx2.txt").getAbsolutePath()));
            BufferedWriter bw3 = new BufferedWriter(new FileWriter(new File("Sx/Sx3.txt").getAbsolutePath()));
            for (File file : files) {
                br = new BufferedReader(new FileReader(file));
                String strLine = null;
                int countLine = 1;
                while((strLine = br.readLine()) != null) {
                    strLine = strLine.replaceAll("((\r\n)|\n)[\\s\t ]*(\\1)+", "$1").replaceAll("^((\r\n)|\n)", "");
                    if (strLine.length() == 0) {
                        continue;
                    }
                    if (countLine % 3 == 1) {
                        bw1.write(strLine + '\n');
                    } else if (countLine % 3 == 2) {
                        bw2.write(strLine + '\n');
                    } else {
                        bw3.write(strLine + '\n');
                    }
                    countLine++;
                }
            }
            bw1.close();
            bw2.close();
            bw3.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("master: finish file arrangement");
    }

    private void collect() {
        try {
            File[] files = new File("RM").listFiles();
            BufferedReader br;
            for (File file : files) {
                br = new BufferedReader(new FileReader(file));
                String strLine = null;
                while ((strLine = br.readLine()) != null) {
                    final String[] word = strLine.split(" ");
                    data.add(new Word(word[0], Integer.parseInt(word[1])));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Sort by frequencies (prior) and strings
    private Stack<Record> stack = new Stack<>();

    public void sort() {
        if (1 < data.size()) {
            stack.push(new Record(0, data.size() - 1));
            int pivot;
            while (!stack.isEmpty()) {
                Record record = stack.pop();
                pivot = partition(record.left, record.right);
                if (pivot - 1 >= record.left) {
                    stack.push(new Record(record.left, pivot - 1));
                }
                if (pivot + 1 <= record.right) {
                    stack.push(new Record(pivot + 1, record.right));
                }
            }
        }
    }

    private static class Record {
        int left;
        int right;
 
        private Record(int left, int right) {
            this.left = left;
            this.right = right;
        }
    }
    
    // Mono-direction partition: left -> right
    private int partition(int low, int high) {
        int i = low;
        Word tmp;
        for (int j = low + 1; j <= high; j++) {
            if (wordCmp(j, low) == true) {
                i++;
                if (i != j) {
                    tmp = data.get(j);
                    data.set(j, data.get(i));
                    data.set(i, tmp);
                }
            }
        }
        tmp = data.get(low);
        data.set(low, data.get(i));
        data.set(i, tmp);
        return i;
    }

    // compare two words' priority, return true : data[i] has higher priority
    private boolean wordCmp(int i, int j) {
        Word w1 = data.get(i);
        Word w2 = data.get(j);
        if (w1.freq > w2.freq) {
            return true;
        } else if (w1.freq == w2.freq && w1.word.compareTo(w2.word) < 0) {
            return true;
        }
        return false;
    }

    // Display all words and corresponding frequencies
    private void output() {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File("result.txt").getAbsolutePath()));
            for (Word w : data) {
                bw.write(w.word + '\t' + w.freq + '\n');
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        MASTER master = new MASTER();
        if (args.length == 1) {
            System.out.println(new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(new Date()));
            master.distribute(args[0]);
        } else {
            master.collect();
            master.sort();
            master.output();
            System.out.println(new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(new Date()));
        }
    }
}